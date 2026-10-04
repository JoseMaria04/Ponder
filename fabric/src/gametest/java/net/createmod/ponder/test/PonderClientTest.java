package net.createmod.ponder.test;

import java.lang.reflect.Method;

import net.createmod.catnip.api.client.gui.ScreenOpener;
import net.createmod.ponder.impl.client.gui.PonderUI;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.world.item.Items;

public class PonderClientTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		try (TestSingleplayerContext sp = context.worldBuilder().create()) {
			sp.getConnection().waitForChunksRender();
			context.runOnClient(mc -> ScreenOpener.open(PonderUI.of(Items.SPYGLASS)));
			context.waitForScreen(PonderUI.class);
			context.waitTicks(80);
			context.takeScreenshot("ponder_scene_1");
			// scenes: 1 coordinates, 2 blocks, 3 fluids, 4 off screen, 5 particles
			for (int i = 2; i <= 5; i++) {
				context.runOnClient(mc -> {
					try {
						Method scroll = PonderUI.class.getDeclaredMethod("scroll", boolean.class);
						scroll.setAccessible(true);
						scroll.invoke(mc.gui.screen(), true);
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				});
				context.waitTicks(i == 5 ? 120 : 80);
				if (i != 4) context.takeScreenshot("ponder_scene_" + i);
			}
		}
	}
}
