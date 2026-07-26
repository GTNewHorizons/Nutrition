package ca.wescook.nutrition.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;

import ca.wescook.nutrition.NutritionConfig;
import ca.wescook.nutrition.NutritionConfig.Gui.ButtonOrigin;
import ca.wescook.nutrition.gui.GuiButtonNutrition;
import ca.wescook.nutrition.gui.NutritionGui;
import ca.wescook.nutrition.mixin.GuiContainerAccessor;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventNutritionButton {

    private GuiButtonNutrition buttonNutrition;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void guiOpen(GuiScreenEvent.InitGuiEvent.Post event) {
        // If any inventory except player inventory is opened, get out
        GuiScreen gui = event.gui;
        if (!(gui instanceof GuiInventory)) return;

        // Get button position
        int[] pos = calculateButtonPosition(gui);
        int x = pos[0];
        int y = pos[1];

        // Create button
        buttonNutrition = new GuiButtonNutrition(x, y);
        // noinspection unchecked
        event.buttonList.add(buttonNutrition);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void guiButtonClick(GuiScreenEvent.ActionPerformedEvent.Post event) {
        // Only run on GuiInventory
        if (!(event.gui instanceof GuiInventory)) return;

        // If nutrition button is clicked
        if (event.button.equals(buttonNutrition)) {
            Minecraft.getMinecraft()
                .displayGuiScreen(new NutritionGui(event.gui));
        } else {
            // Presumably recipe book button was clicked - recalculate nutrition button position
            int[] pos = calculateButtonPosition(event.gui);
            int xPosition = pos[0];
            int yPosition = pos[1];
            buttonNutrition.setPosition(xPosition, yPosition);
        }
    }

    // Return array [x,y] of button coordinates
    @SideOnly(Side.CLIENT)
    private int[] calculateButtonPosition(GuiScreen gui) {
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        // Get bounding box of origin
        if (NutritionConfig.gui.buttonOrigin == ButtonOrigin.Screen) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            width = scaledResolution.getScaledWidth();
            height = scaledResolution.getScaledHeight();
        } else if (NutritionConfig.gui.buttonOrigin == ButtonOrigin.Gui && gui instanceof GuiInventory) {
            width = ((GuiContainerAccessor) gui).nutrition$getXSize();
            height = ((GuiContainerAccessor) gui).nutrition$getYSize();
        }

        // Calculate anchor position from origin (e.g. x/y pixels at right side of gui)
        // The x/y is still relative to the top/left corner of the screen at this point
        switch (NutritionConfig.gui.buttonAnchor) {
            case Top -> {
                x = width / 2;
                y = 0;
            }
            case Right -> {
                x = width;
                y = height / 2;
            }
            case Bottom -> {
                x = width / 2;
                y = height;
            }
            case Left -> {
                x = 0;
                y = height / 2;
            }
            case TopLeft -> {
                x = 0;
                y = 0;
            }
            case TopRight -> {
                x = width;
                y = 0;
            }
            case BottomRight -> {
                x = width;
                y = height;
            }
            case BottomLeft -> {
                x = 0;
                y = height;
            }
            case Center -> {
                x = width / 2;
                y = height / 2;
            }
        }

        // If origin=gui, add the offset to the button's position
        if (NutritionConfig.gui.buttonOrigin == ButtonOrigin.Gui && gui instanceof GuiInventory) {
            x += ((GuiContainerAccessor) gui).nutrition$getGuiLeft();
            y += ((GuiContainerAccessor) gui).nutrition$getGuiTop();
        }

        // Then add the offset as defined in the config file
        x += NutritionConfig.gui.buttonX;
        y += NutritionConfig.gui.buttonY;

        return new int[] { x, y };
    }
}
