package net.createmod.catnip.compat.render;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SequencedMap;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

/// Replacement for vanilla's MultiBufferSource, which Minecraft 26.3 removed when it moved all rendering to
/// SubmitNodeCollector. Code that still asks for a VertexConsumer per RenderType can use:
/// - {@link #submitting(OrderedSubmitNodeCollector)}: records vertices and submits them as custom geometry, or
/// - {@link BufferSource}: records vertices and draws them immediately on {@link BufferSource#endBatch()}.
/// (Added by the unofficial 26.3 port.)
public interface MultiBufferSource {
	VertexConsumer getBuffer(RenderType renderType);

	static MultiBufferSource submitting(OrderedSubmitNodeCollector collector) {
		return new SubmitBufferSource(collector);
	}

	static BufferSource immediate(ByteBufferBuilder sharedBuffer) {
		return new BufferSource();
	}

	static BufferSource immediateWithBuffers(SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers, ByteBufferBuilder sharedBuffer) {
		return new BufferSource();
	}

	class BufferSource implements MultiBufferSource {
		private final Map<RenderType, RecordingVertexConsumer> pending = new LinkedHashMap<>();
		private @org.jspecify.annotations.Nullable RenderType lastType;

		@Override
		public VertexConsumer getBuffer(RenderType renderType) {
			// keep draw order between different render types the same as the old immediate buffer source
			if (lastType != null && lastType != renderType && !renderType.canConsolidateConsecutiveGeometry()) {
				endBatch(lastType);
			}
			lastType = renderType;
			return pending.computeIfAbsent(renderType, t -> new RecordingVertexConsumer());
		}

		public void endLastBatch() {
			if (lastType != null) {
				endBatch(lastType);
			}
		}

		public void endBatch() {
			for (Map.Entry<RenderType, RecordingVertexConsumer> e : pending.entrySet()) {
				ImmediateDrawer.draw(e.getKey(), e.getValue());
			}
			pending.clear();
			lastType = null;
		}

		public void endBatch(RenderType renderType) {
			RecordingVertexConsumer rec = pending.remove(renderType);
			if (rec != null) {
				ImmediateDrawer.draw(renderType, rec);
			}
			if (lastType == renderType) {
				lastType = null;
			}
		}

		/// Submit everything recorded so far instead of drawing it immediately.
		public void submitTo(OrderedSubmitNodeCollector collector) {
			for (Map.Entry<RenderType, RecordingVertexConsumer> e : pending.entrySet()) {
				SubmitBufferSource.submit(collector, e.getKey(), e.getValue());
			}
			pending.clear();
			lastType = null;
		}
	}
}
