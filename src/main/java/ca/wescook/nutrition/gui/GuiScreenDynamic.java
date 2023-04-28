package ca.wescook.nutrition.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ca.wescook.nutrition.Tags;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Similar to GuiScreen, but draws a dynamic Minecraft border around any size
@SideOnly(Side.CLIENT)
public abstract class GuiScreenDynamic extends GuiScreen {

    // Container size
    private int guiWidth = 0;
    private int guiHeight = 0;

    // Offsets
    public int top = 0;
    public int left = 0;
    public int right = 0;
    public int bottom = 0;

    protected final List<GuiLabelNutrition> labelList = new ArrayList<>();

    // Container info
    private final ResourceLocation GUI_BORDERS = new ResourceLocation(Tags.MODID, "textures/gui/gui.png");

    // Update GUI size
    // Must be increment of 4!
    void updateContainerSize(int guiWidth, int guiHeight) {
        // Update container size
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;

        // Calculate offsets
        top = (height / 2) - (guiHeight / 2);
        left = (width / 2) - (guiWidth / 2);
        right = (width / 2) + (guiWidth / 2);
        bottom = (height / 2) + (guiHeight / 2);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground(); // Darken background
        drawBackground();
    }

    private void drawBackground() {
        // Init
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager()
            .bindTexture(GUI_BORDERS); // Fetch texture

        // Top left corner
        drawTexturedModalRect(left, top, 0, 0, 4, 4);

        // Bottom left corner
        drawTexturedModalRect(left, bottom - 4, 0, 8, 4, 4);

        // Top right corner
        drawTexturedModalRect(right - 4, top, 8, 0, 4, 4);

        // Bottom right corner
        drawTexturedModalRect(right - 4, bottom - 4, 8, 8, 4, 4);

        // Left side
        for (int i = 0; i < guiHeight - 8; i += 4) drawTexturedModalRect(left, top + 4 + i, 0, 4, 4, 4);

        // Top side
        for (int i = 0; i < guiWidth - 8; i += 4) drawTexturedModalRect(left + 4 + i, top, 4, 0, 4, 4);

        // Right side
        for (int i = 0; i < guiHeight - 8; i += 4) drawTexturedModalRect(right - 4, top + 4 + i, 8, 4, 4, 4);

        // Bottom side
        for (int i = 0; i < guiWidth - 8; i += 4) drawTexturedModalRect(left + 4 + i, bottom - 4, 4, 8, 4, 4);

        // Draw center tiles
        for (int i = 0; i < guiWidth - 8; i += 4)
            for (int j = 0; j < guiHeight - 8; j += 4) drawTexturedModalRect(left + 4 + i, top + 4 + j, 4, 4, 4, 4);
    }

    // Draw labels and buttons (replacing super.drawScreen() call)
    // Can be called after background drawing, for proper layering
    public void drawLabels(int mouseX, int mouseY) {
        // Labels
        GL11.glDisable(GL11.GL_LIGHTING);
        for (GuiButton aButtonList : this.buttonList) aButtonList.drawButton(this.mc, mouseX, mouseY);

        // Buttons
        for (GuiLabelNutrition aLabelList : this.labelList) aLabelList.drawLabel(this.mc, mouseX, mouseY);
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
