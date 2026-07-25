package ca.wescook.nutrition;

import com.gtnewhorizon.gtnhlib.config.Config;

@Config(modid = Nutrition.MODID, filename = "Nutrition")
public class NutritionConfig {

    @Config.Name("Nutrition")
    public static final Nutrition nutrition = new Nutrition();
    @Config.Name("Nutrient Decay")
    public static final Decay decay = new Decay();
    @Config.Name("Death Penalty")
    public static final DeathPenalty death = new DeathPenalty();
    @Config.Name("Gui")
    public static final Gui gui = new Gui();
    @Config.Name("Debug")
    public static final Debug debug = new Debug();
    @Config.Name("Witchery Compat")
    public static final Witchery witchery = new Witchery();

    @Config.Comment("Nutrition section")
    public static class Nutrition {

        @Config.DefaultFloat(1)
        @Config.RangeFloat(min = 0.0f, max = 100.0f)
        @Config.Comment("Value to multiply base nutrition by for each food (eg. 0.5 to halve nutrition gain).")
        public float nutritionMultiplier;

        @Config.DefaultInt(50)
        @Config.RangeInt(min = 0, max = 100)
        @Config.Comment("The starting nutrition level for new players.")
        public int startingNutrition;

        @Config.DefaultInt(15)
        @Config.RangeInt(min = 0, max = 100)
        @Config.Comment("The nutrition value subtracted from foods per additional nutrient, as a percentage.\n"
            + "This is to prevent large, complex foods from being too powerful.\n"
            + "(eg. 1 nutrient = 0% loss, 2 nutrients = 15% loss, 3 nutrients = 30% loss)")
        public int lossPerNutrient;

        @Config.DefaultBoolean(false)
        @Config.Comment("Allow player to continue eating even while full.\n"
            + "This setting may upset balance (and tummies), but is necessary for playing in peaceful mode.")
        public boolean allowOvereating;
    }

    @Config.Comment("Nutrition decay section")
    public static class Decay {

        @Config.DefaultBoolean(true)
        @Config.Comment("Enable nutrition decay when hunger drains.")
        public boolean enable;

        @Config.DefaultFloat(1.0f)
        @Config.RangeFloat(min = -100.0f, max = 100.0f)
        @Config.Comment("Global value to multiply decay rate by (eg. 0.5 halves the rate, 2.0 doubles it).\n"
            + "This can also be set per-nutrient.")
        public float decayMultiplier;
    }

    @Config.Comment("Death penalty section")
    public static class DeathPenalty {

        @Config.DefaultInt(30)
        @Config.RangeInt(min = 0, max = 100)
        @Config.Comment("The minimum nutrition value that the death penalty may reduce to.")
        public int deathPenaltyMin;

        @Config.DefaultBoolean(true)
        @Config.Comment("On death, should nutrition be reset to deathPenaltyMin if it's fallen below that value?\n"
            + "This is recommended to prevent death loops caused by negative effects.")
        public boolean deathPenaltyReset;

        @Config.DefaultInt(15)
        @Config.RangeInt(min = 0, max = 100)
        @Config.Comment("The nutrition value subtracted from each nutrient upon death.")
        public int deathPenaltyLoss;
    }

    @Config.Comment("GUI section")
    public static class Gui {

        @Config.DefaultBoolean(true)
        @Config.Comment("If the nutrition GUI should be enabled.")
        public boolean enable;

        @Config.DefaultBoolean(true)
        @Config.Comment("If the nutrition button should be shown on player inventory (hotkey will still function).")
        public boolean enableButton;

        @Config.DefaultBoolean(true)
        @Config.Comment("If foods should show their nutrients on hover.")
        public boolean enableTooltips;

        @Config.DefaultEnum("Gui")
        @Config.Comment("The origin defines the object which the nutrition button will be placed relative to.")
        public ButtonOrigin buttonOrigin;

        @Config.DefaultEnum("TopLeft")
        @Config.Comment("The anchor defines which side of the origin to position the button against.")
        public ButtonAnchor buttonAnchor;

        @Config.DefaultInt(132)
        @Config.RangeInt(min = -1000, max = 1000)
        @Config.Comment("The nutrition button's X position, relative to its anchor point.")
        public int buttonX;

        @Config.DefaultInt(61)
        @Config.RangeInt(min = -1000, max = 1000)
        @Config.Comment("The nutrition button's Y position, relative to its anchor point.")
        public int buttonY;

        public enum ButtonOrigin {
            Gui,
            Screen,
        }

        public enum ButtonAnchor {
            Top,
            Right,
            Bottom,
            Left,
            TopLeft,
            TopRight,
            BottomRight,
            BottomLeft,
            Center,
        }
    }

    @Config.Comment("Debug section")
    public static class Debug {

        @Config.DefaultBoolean(false)
        @Config.Comment("Log foods which cannot be found but are still listed in nutrients file.")
        public boolean logMissingFood;

        @Config.DefaultBoolean(false)
        @Config.Comment("Log foods which have been found but do not have any associated nutrients.")
        public boolean logMissingNutrients;
    }

    @Config.Comment("Witchery compat section")
    public static class Witchery {

        @Config.DefaultBoolean(true)
        @Config.Comment("Enable witchery mod compatibility.")
        public boolean enable;

        @Config.DefaultFloat(30.0f)
        @Config.RangeFloat(min = 0.0f, max = 100.0f)
        @Config.Comment("Nutrition value for a lvl 1 vampire full of blood.")
        public float vampireMinNutrition;

        @Config.DefaultFloat(90.0f)
        @Config.RangeFloat(min = 0.0f, max = 100.0f)
        @Config.Comment("Nutrition value for a lvl 10 vampire full of blood.")
        public float vampireMaxNutrition;
    }
}
