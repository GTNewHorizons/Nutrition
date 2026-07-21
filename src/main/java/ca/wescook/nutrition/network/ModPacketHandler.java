package ca.wescook.nutrition.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ModPacketHandler {

    public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("nutrition");

    // Message IDs
    private static final int MESSAGE_C2S_NUTRIENT_CHANGE = 0;
    private static final int MESSAGE_C2S_NUTRIENT_NORMALIZE = 1;
    private static final int MESSAGE_S2C_SYNC_NUTRIENTS = 2;

    // Register messages on run
    public static void registerMessages() {
        NETWORK_CHANNEL.registerMessage(
            CPacketNutrientChange.Handler.class,
            CPacketNutrientChange.Message.class,
            MESSAGE_C2S_NUTRIENT_CHANGE,
            Side.SERVER);

        NETWORK_CHANNEL.registerMessage(
            CPacketNormalizeNutrients.Handler.class,
            CPacketNormalizeNutrients.Message.class,
            MESSAGE_C2S_NUTRIENT_NORMALIZE,
            Side.SERVER);

        NETWORK_CHANNEL.registerMessage(
            SPacketNutrients.Handler.class,
            SPacketNutrients.Message.class,
            MESSAGE_S2C_SYNC_NUTRIENTS,
            Side.CLIENT);
    }
}
