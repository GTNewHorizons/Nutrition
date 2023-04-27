package ca.wescook.nutrition.nutrients;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

// Nutrient object represents a type of food group
public class Nutrient {

    public String name;
    public ItemStack icon;
    public int color;
    public float decay;
    public boolean visible;
    public List<String> foodOreDict = new ArrayList<>();
    public List<ItemStack> foodItems = new ArrayList<>();
}
