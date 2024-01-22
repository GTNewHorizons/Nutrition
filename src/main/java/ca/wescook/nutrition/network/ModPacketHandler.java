package ca.wescook.nutrition.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ModPacketHandler {

    public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("nutrition");

    // Message IDs
    private static final int MESSAGE_NUTRITION_REQUEST = 0;
    private static final int MESSAGE_NUTRITION_RESPONSE = 1;
    private static final int MESSAGE_NORMALIZE_SERVER_NUTRIENTS = 2;

    // Register messages on run
    public static void registerMessages() {
        NETWORK_CHANNEL.registerMessage(
            PacketNutritionRequest.Handler.class,
            PacketNutritionRequest.Message.class,
            MESSAGE_NUTRITION_REQUEST,
            Side.SERVER);

        NETWORK_CHANNEL.registerMessage(
            PacketNutritionResponse.Handler.class,
            PacketNutritionResponse.Message.class,
            MESSAGE_NUTRITION_RESPONSE,
            Side.CLIENT);

        NETWORK_CHANNEL.registerMessage(
            PacketNormalizeServerNutrients.Handler.class,
            PacketNormalizeServerNutrients.Message.class,
            MESSAGE_NORMALIZE_SERVER_NUTRIENTS,
            Side.SERVER);
    }
}
