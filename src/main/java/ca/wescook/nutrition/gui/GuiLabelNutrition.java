package ca.wescook.nutrition.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// Basically just copied from MC's GuiLabel class, because it's completely unusable...
@SideOnly(Side.CLIENT)
public class GuiLabelNutrition extends Gui {

    protected int width;
    protected int height;
    public int x;
    public int y;
    private final List<String> labels;
    public int id;
    private boolean centered;
    public boolean visible = true;
    private boolean labelBgEnabled;
    private final int textColor;
    private int backColor;
    private int ulColor;
    private int brColor;
    private final FontRenderer fontRenderer;
    private int border;

    public GuiLabelNutrition(FontRenderer fontRenderer, int id, int x, int y, int width, int height, int textColor) {
        this.fontRenderer = fontRenderer;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.labels = new ArrayList<>();
        this.centered = false;
        this.labelBgEnabled = false;
        this.textColor = textColor;
        this.backColor = -1;
        this.ulColor = -1;
        this.brColor = -1;
        this.border = 0;
    }

    public void addLine(String line) {
        this.labels.add(I18n.format(line));
    }

    public GuiLabelNutrition setCentered() {
        this.centered = true;
        return this;
    }

    public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawLabelBackground(mc, mouseX, mouseY);
            int k = this.y + this.height / 2 + this.border / 2;
            int l = k - this.labels.size() * 10 / 2;

            for (int i1 = 0; i1 < this.labels.size(); ++i1) {
                if (this.centered) {
                    this.drawCenteredString(
                        this.fontRenderer,
                        this.labels.get(i1),
                        this.x + this.width / 2,
                        l + i1 * 10,
                        this.textColor);
                } else {
                    this.drawString(this.fontRenderer, this.labels.get(i1), this.x, l + i1 * 10, this.textColor);
                }
            }
        }
    }

    protected void drawLabelBackground(Minecraft mc, int mouseX, int mouseY) {
        if (this.labelBgEnabled) {
            int k = this.width + this.border * 2;
            int l = this.height + this.border * 2;
            int i1 = this.x - this.border;
            int j1 = this.y - this.border;
            drawRect(i1, j1, i1 + k, j1 + l, this.backColor);
            this.drawHorizontalLine(i1, i1 + k, j1, this.ulColor);
            this.drawHorizontalLine(i1, i1 + k, j1 + l, this.brColor);
            this.drawVerticalLine(i1, j1, j1 + l, this.ulColor);
            this.drawVerticalLine(i1 + k, j1, j1 + l, this.brColor);
        }
    }
}
