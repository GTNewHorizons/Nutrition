package ca.wescook.nutrition.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import org.lwjgl.opengl.GL11;

import ca.wescook.nutrition.network.Sync;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.proxy.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NutritionGui extends GuiScreenDynamic {

    private GuiButton buttonClose;

    ///////////////////
    // Magic Numbers //
    ///////////////////

    // Gui Container
    private static final int GUI_BASE_WIDTH = 184;
    private static final int GUI_BASE_HEIGHT = 72;
    private static final int NUTRITION_DISTANCE = 20; // Vertical distance between each nutrient

    // Nutrition Title
    private static final int TITLE_VERTICAL_OFFSET = 18;

    // Nutrition icon positions
    private static final int NUTRITION_ICON_HORIZONTAL_OFFSET = 10;
    private static final int NUTRITION_ICON_VERTICAL_OFFSET = 32;

    // Nutrition bar positions
    private static final int NUTRITION_BAR_WIDTH = 130;
    private static final int NUTRITION_BAR_HEIGHT = 13;
    private static final int NUTRITION_BAR_HORIZONTAL_OFFSET = 40;
    private static final int NUTRITION_BAR_VERTICAL_OFFSET = 33;

    // Nutrition label positions
    private static final int LABEL_NAME_HORIZONTAL_OFFSET = 30;
    private static final int LABEL_VALUE_HORIZONTAL_OFFSET = 43;
    private static final int LABEL_VERTICAL_OFFSET = 41;
    private int labelCharacterPadding = 0; // Add padding for long nutrient names

    // Close button position
    private static final int CLOSE_BUTTON_WIDTH = 70;
    private static final int CLOSE_BUTTON_HEIGHT = 20;
    private static final int CLOSE_BUTTON_OFFSET = 12;

    private GuiScreen previousScreen = null;

    public NutritionGui() {}

    public NutritionGui(GuiScreen prevScreen) {
        this.previousScreen = prevScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks); // Background
        drawNutritionBars(); // Nutrition bars
        super.drawLabels(mouseX, mouseY); // Labels/buttons
    }

    private void drawNutritionBars() {
        int i = 0;
        for (Nutrient nutrient : NutrientList.getVisible()) {
            // Calculate percentage width for nutrition bars
            float currentNutrient = (ClientProxy.localNutrition != null
                && ClientProxy.localNutrition.get(nutrient) != null)
                    ? Math.round(ClientProxy.localNutrition.get(nutrient))
                    : 0; // Display empty if null
            int nutritionBarDisplayWidth = (int) (currentNutrient / 100 * NUTRITION_BAR_WIDTH);

            // Draw icons
            itemRender.renderItemIntoGUI(
                mc.fontRenderer,
                mc.renderEngine,
                nutrient.icon,
                left + NUTRITION_ICON_HORIZONTAL_OFFSET,
                top + NUTRITION_ICON_VERTICAL_OFFSET + (i * NUTRITION_DISTANCE));

            GL11.glDisable(GL11.GL_LIGHTING);
            // Draw black background
            drawRect(
                left + NUTRITION_BAR_HORIZONTAL_OFFSET + labelCharacterPadding - 1,
                top + NUTRITION_BAR_VERTICAL_OFFSET + (i * NUTRITION_DISTANCE) - 1,
                left + NUTRITION_BAR_HORIZONTAL_OFFSET + NUTRITION_BAR_WIDTH + labelCharacterPadding + 1,
                top + NUTRITION_BAR_VERTICAL_OFFSET + (i * NUTRITION_DISTANCE) + NUTRITION_BAR_HEIGHT + 1,
                0xFF000000);

            // Draw colored bar
            drawRect(
                left + NUTRITION_BAR_HORIZONTAL_OFFSET + labelCharacterPadding,
                top + NUTRITION_BAR_VERTICAL_OFFSET + (i * NUTRITION_DISTANCE),
                left + NUTRITION_BAR_HORIZONTAL_OFFSET + nutritionBarDisplayWidth + labelCharacterPadding,
                top + NUTRITION_BAR_VERTICAL_OFFSET + (i * NUTRITION_DISTANCE) + NUTRITION_BAR_HEIGHT,
                nutrient.color);
            GL11.glEnable(GL11.GL_LIGHTING);

            i++;
        }
    }

    // Called when GUI is opened or resized
    @Override
    public void initGui() {
        // Sync Nutrition info from server to client
        Sync.clientRequest();

        // Calculate label offset for long nutrition names
        for (Nutrient nutrient : NutrientList.getVisible()) {
            // Get width of localized string
            int nutrientWidth = mc.fontRenderer
                .getStringWidth(I18n.format("nutrient." + "nutrition" + ":" + nutrient.name));
            nutrientWidth = (nutrientWidth / 4) * 4; // Round to nearest multiple of 4
            if (nutrientWidth > labelCharacterPadding) labelCharacterPadding = nutrientWidth;
        }

        // Update dynamic GUI size
        super.updateContainerSize(
            GUI_BASE_WIDTH + labelCharacterPadding,
            GUI_BASE_HEIGHT + (NutrientList.getVisible()
                .size() * NUTRITION_DISTANCE));

        // Add Close button
        buttonList.add(
            buttonClose = new GuiButton(
                0,
                (width / 2) - (CLOSE_BUTTON_WIDTH / 2),
                bottom - CLOSE_BUTTON_HEIGHT - CLOSE_BUTTON_OFFSET,
                CLOSE_BUTTON_WIDTH,
                CLOSE_BUTTON_HEIGHT,
                I18n.format("gui." + "nutrition" + ":close")));

        // Draw labels
        redrawLabels();
    }

    // Called when needing to propagate the window with new information
    public void redrawLabels() {
        // Clear existing labels for nutrition value or screen changes
        labelList.clear();

        // Draw title
        String nutritionTitle = I18n.format("gui." + "nutrition" + ":nutrition_title");
        GuiLabelNutrition label;
        labelList.add(
            label = new GuiLabelNutrition(
                mc.fontRenderer,
                0,
                (width / 2) - (mc.fontRenderer.getStringWidth(nutritionTitle) / 2),
                top + TITLE_VERTICAL_OFFSET,
                0,
                0,
                0xffffffff));
        label.addLine(nutritionTitle);

        // Nutrients names and values
        int i = 0;
        for (Nutrient nutrient : NutrientList.getVisible()) {
            // Create labels for each nutrient type name
            labelList.add(
                label = new GuiLabelNutrition(
                    mc.fontRenderer,
                    0,
                    left + LABEL_NAME_HORIZONTAL_OFFSET,
                    top + LABEL_VERTICAL_OFFSET + (i * NUTRITION_DISTANCE),
                    0,
                    0,
                    0xffffffff));
            // Add name from localization file
            label.addLine(I18n.format("nutrient." + "nutrition" + ":" + nutrient.name));

            // Create percent value labels for each nutrient value
            labelList.add(
                label = new GuiLabelNutrition(
                    mc.fontRenderer,
                    0,
                    left + LABEL_VALUE_HORIZONTAL_OFFSET + labelCharacterPadding,
                    top + LABEL_VERTICAL_OFFSET + (i * NUTRITION_DISTANCE),
                    0,
                    0,
                    0xffffffff));
            // Ensure local nutrition data exists
            if (ClientProxy.localNutrition != null && ClientProxy.localNutrition.get(nutrient) != null) {
                label.addLine(Math.round(ClientProxy.localNutrition.get(nutrient)) + "%%");
            } else {
                label.addLine(I18n.format("gui." + "nutrition" + ":updating"));
            }
            i++;
        }
    }

    // Called when button/element is clicked
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            // Close GUI
            mc.displayGuiScreen(this.previousScreen);
            if (mc.currentScreen == null) mc.setIngameFocus();
        }
    }

    // Close GUI if inventory key is hit again
    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

        // If escape key (1), or player inventory key (E), or Nutrition GUI key (N) is pressed
        if (keyCode == 1 || keyCode == mc.gameSettings.keyBindInventory.getKeyCode()
            || keyCode == ClientProxy.keyNutritionGui.getKeyCode()) {
            // Close GUI
            mc.thePlayer.closeScreen();
            if (mc.currentScreen == null) mc.setIngameFocus();
        }
    }

    // Opening Nutrition menu doesn't pause game
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
