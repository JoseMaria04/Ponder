package net.createmod.catnip.compat.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/// Records vertices (already transformed by the caller) so they can be replayed into a real
/// VertexConsumer later, e.g. inside a 26.3 custom geometry submit.
/// (Added by the unofficial 26.3 port.)
public final class RecordingVertexConsumer implements VertexConsumer {
	private static final int HAS_COLOR = 1, HAS_UV = 2, HAS_UV1 = 4, HAS_UV2 = 8, HAS_UV3 = 16, HAS_NORMAL = 32, HAS_LINE_WIDTH = 64;
	private static final int FLOATS = 11; // x y z u v u3 v3 nx ny nz lineWidth
	private static final int INTS = 4; // flags color uv1 uv2

	private final FloatArrayList floats = new FloatArrayList();
	private final IntArrayList ints = new IntArrayList();
	private int vertexCount;

	private int f(int attr) {
		return (vertexCount - 1) * FLOATS + attr;
	}

	private int i(int attr) {
		return (vertexCount - 1) * INTS + attr;
	}

	private void flag(int bit) {
		ints.set(i(0), ints.getInt(i(0)) | bit);
	}

	public boolean isEmpty() {
		return vertexCount == 0;
	}

	public int vertexCount() {
		return vertexCount;
	}

	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		vertexCount++;
		for (int k = 0; k < FLOATS; k++) floats.add(0f);
		for (int k = 0; k < INTS; k++) ints.add(0);
		floats.set(f(0), x);
		floats.set(f(1), y);
		floats.set(f(2), z);
		return this;
	}

	@Override
	public VertexConsumer setColor(int r, int g, int b, int a) {
		return setColor((a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | b & 255);
	}

	@Override
	public VertexConsumer setColor(int color) {
		ints.set(i(1), color);
		flag(HAS_COLOR);
		return this;
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		floats.set(f(3), u);
		floats.set(f(4), v);
		flag(HAS_UV);
		return this;
	}

	@Override
	public VertexConsumer setUv1(int u, int v) {
		ints.set(i(2), (u & 0xFFFF) | (v << 16));
		flag(HAS_UV1);
		return this;
	}

	@Override
	public VertexConsumer setUv2(int u, int v) {
		ints.set(i(3), (u & 0xFFFF) | (v << 16));
		flag(HAS_UV2);
		return this;
	}

	@Override
	public VertexConsumer setUv3(float u, float v) {
		floats.set(f(5), u);
		floats.set(f(6), v);
		flag(HAS_UV3);
		return this;
	}

	@Override
	public VertexConsumer setNormal(float x, float y, float z) {
		floats.set(f(7), x);
		floats.set(f(8), y);
		floats.set(f(9), z);
		flag(HAS_NORMAL);
		return this;
	}

	@Override
	public VertexConsumer setLineWidth(float width) {
		floats.set(f(10), width);
		flag(HAS_LINE_WIDTH);
		return this;
	}

	public void replay(VertexConsumer out) {
		for (int v = 0; v < vertexCount; v++) {
			int fo = v * FLOATS, io = v * INTS;
			int flags = ints.getInt(io);
			out.addVertex(floats.getFloat(fo), floats.getFloat(fo + 1), floats.getFloat(fo + 2));
			if ((flags & HAS_COLOR) != 0) out.setColor(ints.getInt(io + 1));
			if ((flags & HAS_UV) != 0) out.setUv(floats.getFloat(fo + 3), floats.getFloat(fo + 4));
			if ((flags & HAS_UV1) != 0) {
				int p = ints.getInt(io + 2);
				out.setUv1(p & 0xFFFF, p >>> 16);
			}
			if ((flags & HAS_UV2) != 0) {
				int p = ints.getInt(io + 3);
				out.setUv2(p & 0xFFFF, p >>> 16);
			}
			if ((flags & HAS_UV3) != 0) out.setUv3(floats.getFloat(fo + 5), floats.getFloat(fo + 6));
			if ((flags & HAS_NORMAL) != 0) out.setNormal(floats.getFloat(fo + 7), floats.getFloat(fo + 8), floats.getFloat(fo + 9));
			if ((flags & HAS_LINE_WIDTH) != 0) out.setLineWidth(floats.getFloat(fo + 10));
		}
	}

	public void clear() {
		floats.clear();
		ints.clear();
		vertexCount = 0;
	}
}
