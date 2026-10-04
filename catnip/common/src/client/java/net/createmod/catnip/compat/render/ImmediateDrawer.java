package net.createmod.catnip.compat.render;

import java.util.Optional;
import java.util.OptionalDouble;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.renderpearl.api.commands.RenderPass;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.rendertype.RenderType;

/// Draws recorded geometry right away into the main render target, the way MultiBufferSource.BufferSource#endBatch
/// did before 26.3. (Added by the unofficial 26.3 port.)
public final class ImmediateDrawer {
	private static StagedVertexBuffer buffer;

	private ImmediateDrawer() {
	}

	public static void draw(RenderType renderType, RecordingVertexConsumer recorder) {
		draw(renderType, recorder, Minecraft.getInstance().gameRenderer.mainRenderTarget());
	}

	public static void draw(RenderType renderType, RecordingVertexConsumer recorder, RenderTarget target) {
		if (recorder.isEmpty()) {
			return;
		}
		RenderSystem.assertOnRenderThread();
		if (buffer == null) {
			buffer = new StagedVertexBuffer(() -> "Catnip immediate", 1 << 16);
		}

		VertexSorting sorting = renderType.sortOnUpload() ? RenderSystem.getProjectionType().vertexSorting() : null;
		StagedVertexBuffer.Draw draw = buffer.appendDraw(renderType.format(), renderType.primitiveTopology(), sorting);
		recorder.replay(buffer.getVertexBuilder(draw));
		buffer.requestIndexCount(draw);
		buffer.upload();
		StagedVertexBuffer.ExecuteInfo info = buffer.getExecuteInfo(draw);
		if (info != null) {
			try (RenderPass pass = RenderSystem.getDevice()
					.createCommandEncoder()
					.createRenderPass(() -> "Catnip immediate " + renderType, target.getColorTextureView(), Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
				RenderSystem.bindDefaultUniforms(pass);
				renderType.prepare().drawFromBuffer(info, pass);
			}
		}
		buffer.endFrame();
	}
}
