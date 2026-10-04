package net.createmod.catnip.impl.mixin;

import java.util.concurrent.atomic.AtomicInteger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.createmod.catnip.api.level.wrapper.SchematicLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/// 26.3 throws when reading the id of an entity that was never added to a real level. Entities living in virtual
/// levels (Ponder scenes, schematics) are never added, so they get a unique negative id on construction.
/// (Added by the unofficial 26.3 port.)
@Mixin(Entity.class)
public abstract class VirtualEntityIdMixin {
	@Unique
	private static final AtomicInteger catnip$nextVirtualId = new AtomicInteger(-1_000_000);

	@Inject(method = "<init>", at = @At("RETURN"))
	private void catnip$assignVirtualId(EntityType<?> type, Level level, CallbackInfo ci) {
		if (level instanceof SchematicLevel) {
			((Entity) (Object) this).setId(catnip$nextVirtualId.getAndDecrement());
		}
	}
}
