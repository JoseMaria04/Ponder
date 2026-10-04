package net.createmod.catnip.impl.client.gui.element.pip;

import com.mojang.blaze3d.vertex.PoseStack;

import net.createmod.catnip.api.client.gui.render.pip.GuiBlockModelRenderState;
import net.createmod.catnip.api.client.level.SinglePosVirtualBlockGetter;
import net.createmod.catnip.api.client.render.model.BakedModelBufferer;
import net.createmod.catnip.impl.client.render.ColoringVertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.createmod.catnip.compat.render.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;

public class GuiBlockModelRenderer extends PictureInPictureRenderer<GuiBlockModelRenderState> {
	public GuiBlockModelRenderer() {
	}

	@Override
	public Class<GuiBlockModelRenderState> getRenderStateClass() {
		return GuiBlockModelRenderState.class;
	}

	@Override
	protected void renderToTexture(GuiBlockModelRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
		MultiBufferSource bufferSource = MultiBufferSource.submitting(submitNodeCollector);
		SinglePosVirtualBlockGetter level = SinglePosVirtualBlockGetter.createFullBright();
		level.blockState(renderState.state());
		level.blockEntity(renderState.blockEntity());

		int color = renderState.color();
		BakedModelBufferer.bufferModel(Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(renderState.state()), BlockPos.ZERO, level, renderState.state(),
			poseStack, (layer, shade) -> {
				RenderType type = layer == ChunkSectionLayer.TRANSLUCENT
					? Sheets.translucentBlockItemSheet()
					: Sheets.cutoutBlockItemSheet();

				return new ColoringVertexConsumer(
					bufferSource.getBuffer(type),
					ARGB.red(color) / 255f,
					ARGB.green(color) / 255f,
					ARGB.blue(color) / 255f,
					1);
			}
		);

	}

	@Override
	protected String getTextureLabel() {
		return "catnip:gui_block_model";
	}
}
