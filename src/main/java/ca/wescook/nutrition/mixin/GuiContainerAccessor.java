package ca.wescook.nutrition.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiContainer.class)
public interface GuiContainerAccessor {

    @Accessor("xSize")
    int nutrition$getXSize();

    @Accessor("ySize")
    int nutrition$getYSize();

    @Accessor("guiLeft")
    int nutrition$getGuiLeft();

    @Accessor("guiTop")
    int nutrition$getGuiTop();
}
