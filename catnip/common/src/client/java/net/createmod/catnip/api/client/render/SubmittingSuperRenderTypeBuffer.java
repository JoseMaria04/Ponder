package net.createmod.catnip.api.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.createmod.catnip.compat.render.SubmitBufferSource;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/// A SuperRenderTypeBuffer that turns every buffer request into a submit on a SubmitNodeCollector.
/// Used where 26.3 renders through submits (e.g. picture-in-picture GUI rendering) instead of immediate buffers.
/// Early/default/late phases share the collector; render type batching keeps them sorted per type.
/// (Added by the unofficial 26.3 port.)
public class SubmittingSuperRenderTypeBuffer implements SuperRenderTypeBuffer {
	private final SubmitBufferSource source;

	public SubmittingSuperRenderTypeBuffer(OrderedSubmitNodeCollector collector) {
		this.source = new SubmitBufferSource(collector);
	}

	@Override
	public VertexConsumer getEarlyBuffer(RenderType type) {
		return source.getBuffer(type);
	}

	@Override
	public VertexConsumer getBuffer(RenderType type) {
		return source.getBuffer(type);
	}

	@Override
	public VertexConsumer getLateBuffer(RenderType type) {
		return source.getBuffer(type);
	}

	@Override
	public void draw() {
		// submitted geometry is drawn by the frame that owns the collector
	}

	@Override
	public void draw(RenderType type) {
	}
}
