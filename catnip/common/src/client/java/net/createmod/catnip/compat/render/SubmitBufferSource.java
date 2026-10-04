package net.createmod.catnip.compat.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/// A MultiBufferSource view of a SubmitNodeCollector: every getBuffer call returns a recorder whose vertices
/// are replayed (already transformed) when the frame builds its geometry.
/// (Added by the unofficial 26.3 port.)
public final class SubmitBufferSource implements MultiBufferSource {
	private static final PoseStack IDENTITY = new PoseStack();

	private final OrderedSubmitNodeCollector collector;

	public SubmitBufferSource(OrderedSubmitNodeCollector collector) {
		this.collector = collector;
	}

	public OrderedSubmitNodeCollector collector() {
		return collector;
	}

	@Override
	public VertexConsumer getBuffer(RenderType renderType) {
		RecordingVertexConsumer recorder = new RecordingVertexConsumer();
		submit(collector, renderType, recorder);
		return recorder;
	}

	static void submit(OrderedSubmitNodeCollector collector, RenderType renderType, RecordingVertexConsumer recorder) {
		// the geometry renderer runs later (when the frame is prepared), by then the recorder is filled
		collector.submitCustomGeometry(IDENTITY, renderType, (pose, buffer) -> recorder.replay(buffer));
	}
}
