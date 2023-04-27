package ca.wescook.nutrition.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import ca.wescook.nutrition.gui.IGuiContainerGetters;

@Mixin(GuiContainer.class)
public class GuiContainerMixin implements IGuiContainerGetters {

    @Shadow
    int xSize;
    @Shadow
    int ySize;
    @Shadow
    int guiLeft;
    @Shadow
    int guiTop;

    @Override
    public int getXSize() {
        return xSize;
    }

    @Override
    public int getYSize() {
        return ySize;
    }

    @Override
    public int getGuiLeft() {
        return guiLeft;
    }

    @Override
    public int getGuiTop() {
        return guiTop;
    }
}
