package net.createmod.catnip.impl.client.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

/// 26.3 moved the current screen from Minecraft to Gui. ConfirmationScreen swaps it without the
/// setScreen side effects (closing/removing the previous screen), like it did with Minecraft#screen before.
@Mixin(Gui.class)
public interface GuiAccessor {
	@Accessor("screen")
	void catnip$setScreen(@Nullable Screen screen);
}
