package ca.wescook.nutrition.data;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import ca.wescook.nutrition.network.Sync;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.proxy.ClientProxy;
import ca.wescook.nutrition.utility.Config;
import ca.wescook.nutrition.utility.Log;

public class PlayerDataHandler {

    private static final String NBT_NUTRIENT_DATA = "Nutrients";
    // HELD SERVER SIDE!!!
    private static final Map<EntityPlayer, NutrientManager> MANAGER_STORAGE = new HashMap<>();

    public static void saveForPlayer(EntityPlayer player, NBTTagCompound tagCompound) {
        NutrientManager manager = MANAGER_STORAGE.get(player);
        if (manager != null) {
            NBTTagCompound playerData = new NBTTagCompound();
            for (Nutrient nutrient : NutrientList.get()) {
                if (manager.get(nutrient) != null) {
                    playerData.setFloat(nutrient.name, manager.get(nutrient));
                }
            }
            tagCompound.setTag(NBT_NUTRIENT_DATA, playerData);
        }
    }

    public static void initializeForPlayer(EntityPlayer player, NBTTagCompound tagCompound) {
        if (tagCompound.hasKey(NBT_NUTRIENT_DATA)) {
            NBTTagCompound nutrientTag = tagCompound.getCompoundTag(NBT_NUTRIENT_DATA);
            Map<Nutrient, Float> playerNutrients = new HashMap<>();
            float value;

            // Read in nutrients from save data
            for (Nutrient nutrient : NutrientList.get()) {
                if (nutrientTag.hasKey(nutrient.name)) {
                    value = nutrientTag.getFloat(nutrient.name);
                } else {
                    value = (float) Config.startingNutrition;
                }

                playerNutrients.put(nutrient, value);
            }
            MANAGER_STORAGE.put(player, new NutrientManager(playerNutrients));
        }
    }

    /** Get player nutrition data. SERVER SIDE ONLY!! On client, use {@link ClientProxy#localNutrition}. */
    public static NutrientManager getForPlayer(EntityPlayer player) {
        if (player.worldObj.isRemote) {
            Log.warn("Tried to access server player data from the client! Attempting to use server player data...");
            return ClientProxy.localNutrition;
        }
        NutrientManager manager = MANAGER_STORAGE.get(player);
        if (manager == null) {
            // should only happen on initial data creation
            Log.info(
                "Creating initial nutrient data for player " + player.getGameProfile()
                    .getName());
            setForPlayer(player, manager = new NutrientManager(), true);
        }
        return manager;
    }

    /** Set player nutrition data. SERVER SIDE ONLY!! On client, set {@link ClientProxy#localNutrition}. */
    public static void setForPlayer(EntityPlayer player, NutrientManager manager, boolean sync) {
        if (player.worldObj.isRemote) {
            Log.warn("Tried to set server player data from the client!");
            return;
        }
        MANAGER_STORAGE.put(player, manager);
        if (sync) Sync.serverRequest(player);
    }

    /**
     * Set player nutrition data without syncing. SERVER SIDE ONLY!! On client, set {@link ClientProxy#localNutrition}.
     */
    public static void setForPlayer(EntityPlayer player, NutrientManager manager) {
        setForPlayer(player, manager, false);
    }

    public static void clearData() {
        MANAGER_STORAGE.clear();
    }
}
