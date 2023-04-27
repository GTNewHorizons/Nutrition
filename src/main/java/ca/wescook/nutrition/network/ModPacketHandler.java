package ca.wescook.nutrition.network;

import ca.wescook.nutrition.Tags;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ModPacketHandler {

    public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MODID);

    // Message IDs
    private static int MESSAGE_NUTRITION_REQUEST = 0;
    private static int MESSAGE_NUTRITION_RESPONSE = 1;

    // Register messages on run
    public static void registerMessages() {
        NETWORK_CHANNEL.registerMessage(PacketNutritionRequest.Handler.class, PacketNutritionRequest.Message.class, MESSAGE_NUTRITION_REQUEST, Side.SERVER);
        NETWORK_CHANNEL.registerMessage(PacketNutritionResponse.Handler.class, PacketNutritionResponse.Message.class, MESSAGE_NUTRITION_RESPONSE, Side.CLIENT);
    }
}
