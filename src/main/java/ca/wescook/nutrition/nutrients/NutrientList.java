package ca.wescook.nutrition.nutrients;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

// Maintains information about nutrients (name, color, icon)
// Stored client and server-side
public class NutrientList {

    private static ImmutableList<Nutrient> NUTRIENTS;

    // Register list of JSON objects
    public static void register(List<Nutrient> nutrientsIn) {
        NUTRIENTS = ImmutableList.copyOf(nutrientsIn);
    }

    // Return all nutrients
    public static List<Nutrient> get() {
        return NUTRIENTS;
    }

    // Return all visible nutrients
    public static List<Nutrient> getVisible() {
        List<Nutrient> visibleNutrients = new ArrayList<>();
        for (Nutrient nutrient : NUTRIENTS) {
            if (nutrient.visible) {
                visibleNutrients.add(nutrient);
            }
        }
        return visibleNutrients;
    }

    // Return nutrient by name (null if not found)
    public static Nutrient getByName(String name) {
        for (Nutrient nutrient : NUTRIENTS) {
            if (nutrient.name.equals(name)) {
                return nutrient;
            }
        }
        return null;
    }
}
