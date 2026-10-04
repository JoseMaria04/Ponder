package net.createmod.catnip.compat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/// FriendlyByteBuf helpers that Minecraft 26.3 removed, with the same wire format as 26.1.
/// (Added by the unofficial 26.3 port.)
public final class FriendlyByteBufCompat {
	private FriendlyByteBufCompat() {
	}

	public static <B extends FriendlyByteBuf, T, C extends Collection<T>> C readCollection(B buf, IntFunction<C> ctor, StreamDecoder<? super B, T> decoder) {
		int count = buf.readVarInt();
		C result = ctor.apply(count);
		for (int i = 0; i < count; i++) {
			result.add(decoder.decode(buf));
		}
		return result;
	}

	public static <B extends FriendlyByteBuf, T> void writeCollection(B buf, Collection<T> collection, StreamEncoder<? super B, T> encoder) {
		buf.writeVarInt(collection.size());
		for (T element : collection) {
			encoder.encode(buf, element);
		}
	}

	public static <B extends FriendlyByteBuf, T> List<T> readList(B buf, StreamDecoder<? super B, T> decoder) {
		return readCollection(buf, ArrayList::new, decoder);
	}

	public static IntList readIntIdList(FriendlyByteBuf buf) {
		int count = buf.readVarInt();
		IntList result = new IntArrayList();
		for (int i = 0; i < count; i++) {
			result.add(buf.readVarInt());
		}
		return result;
	}

	public static void writeIntIdList(FriendlyByteBuf buf, IntList ids) {
		buf.writeVarInt(ids.size());
		ids.forEach(buf::writeVarInt);
	}

	public static <B extends FriendlyByteBuf, K, V, M extends Map<K, V>> M readMap(B buf, IntFunction<M> ctor, StreamDecoder<? super B, K> keyDecoder, StreamDecoder<? super B, V> valueDecoder) {
		int count = buf.readVarInt();
		M result = ctor.apply(count);
		for (int i = 0; i < count; i++) {
			K key = keyDecoder.decode(buf);
			V value = valueDecoder.decode(buf);
			result.put(key, value);
		}
		return result;
	}

	public static <B extends FriendlyByteBuf, K, V> Map<K, V> readMap(B buf, StreamDecoder<? super B, K> keyDecoder, StreamDecoder<? super B, V> valueDecoder) {
		return readMap(buf, HashMap::new, keyDecoder, valueDecoder);
	}

	public static <B extends FriendlyByteBuf, K, V> void writeMap(B buf, Map<K, V> map, StreamEncoder<? super B, K> keyEncoder, StreamEncoder<? super B, V> valueEncoder) {
		buf.writeVarInt(map.size());
		map.forEach((k, v) -> {
			keyEncoder.encode(buf, k);
			valueEncoder.encode(buf, v);
		});
	}

	public static Instant readInstant(FriendlyByteBuf buf) {
		return Instant.ofEpochMilli(buf.readLong());
	}

	public static void writeInstant(FriendlyByteBuf buf, Instant value) {
		buf.writeLong(value.toEpochMilli());
	}

	public static BlockHitResult readBlockHitResult(FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		Direction face = buf.readEnum(Direction.class);
		float clickX = buf.readFloat();
		float clickY = buf.readFloat();
		float clickZ = buf.readFloat();
		boolean inside = buf.readBoolean();
		boolean worldBorder = buf.readBoolean();
		return new BlockHitResult(new Vec3(pos.getX() + clickX, pos.getY() + clickY, pos.getZ() + clickZ), face, pos, inside, worldBorder);
	}

	public static void writeBlockHitResult(FriendlyByteBuf buf, BlockHitResult blockHit) {
		BlockPos blockPos = blockHit.getBlockPos();
		buf.writeBlockPos(blockPos);
		buf.writeEnum(blockHit.getDirection());
		Vec3 location = blockHit.getLocation();
		buf.writeFloat((float) (location.x - blockPos.getX()));
		buf.writeFloat((float) (location.y - blockPos.getY()));
		buf.writeFloat((float) (location.z - blockPos.getZ()));
		buf.writeBoolean(blockHit.isInside());
		buf.writeBoolean(blockHit.isWorldBorderHit());
	}
}
