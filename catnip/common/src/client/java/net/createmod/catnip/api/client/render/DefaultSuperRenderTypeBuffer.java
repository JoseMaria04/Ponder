package net.createmod.catnip.api.client.render;


import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.compat.render.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;

public class DefaultSuperRenderTypeBuffer implements SuperRenderTypeBuffer {
	private static final DefaultSuperRenderTypeBuffer INSTANCE = new DefaultSuperRenderTypeBuffer();

	public static DefaultSuperRenderTypeBuffer getInstance() {
		return INSTANCE;
	}

	protected SuperRenderTypeBufferPhase earlyBuffer;
	protected SuperRenderTypeBufferPhase defaultBuffer;
	protected SuperRenderTypeBufferPhase lateBuffer;

	public DefaultSuperRenderTypeBuffer() {
		earlyBuffer = new SuperRenderTypeBufferPhase();
		defaultBuffer = new SuperRenderTypeBufferPhase();
		lateBuffer = new SuperRenderTypeBufferPhase();
	}

	@Override
	public VertexConsumer getEarlyBuffer(RenderType type) {
		return earlyBuffer.bufferSource.getBuffer(type);
	}

	@Override
	public VertexConsumer getBuffer(RenderType type) {
		return defaultBuffer.bufferSource.getBuffer(type);
	}

	@Override
	public VertexConsumer getLateBuffer(RenderType type) {
		return lateBuffer.bufferSource.getBuffer(type);
	}

	@Override
	public void draw() {
		earlyBuffer.bufferSource.endBatch();
		defaultBuffer.bufferSource.endBatch();
		lateBuffer.bufferSource.endBatch();
	}

	@Override
	public void draw(RenderType type) {
		earlyBuffer.bufferSource.endBatch(type);
		defaultBuffer.bufferSource.endBatch(type);
		lateBuffer.bufferSource.endBatch(type);
	}

	public static class SuperRenderTypeBufferPhase {
		// 26.3 port: vanilla's fixed-buffer immediate source is gone; the compat BufferSource records per render
		// type and draws on endBatch, which keeps the same early/default/late ordering.
		private final MultiBufferSource.BufferSource bufferSource = new MultiBufferSource.BufferSource();
	}
}
