package ca.wescook.nutrition.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// because MC's button class has no way to set a custom icon without extension
@SideOnly(Side.CLIENT)
public class GuiButtonNutrition extends GuiButton {

    private static final int BUTTON_ID = 800;
    private static final ResourceLocation TEXTURE_LOC = new ResourceLocation("nutrition", "textures/gui/gui.png");
    private static final int X_TEX_START = 14;
    private static final int Y_TEX_START = 0;
    private static final int Y_DIFF_TEXT = 19;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 18;

    public GuiButtonNutrition(int x, int y) {
        super(BUTTON_ID, x, y, WIDTH, HEIGHT, "");
    }

    public void setPosition(int x, int y) {
        this.xPosition = x;
        this.yPosition = y;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            // hovered
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;

            mc.getTextureManager()
                .bindTexture(TEXTURE_LOC);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int j = Y_TEX_START;
            if (this.field_146123_n) {
                j += Y_DIFF_TEXT;
            }
            this.drawTexturedModalRect(this.xPosition, this.yPosition, X_TEX_START, j, this.width, this.height);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }
}
