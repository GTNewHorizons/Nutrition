package ca.wescook.nutrition.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ModPacketHandler {

    public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("nutrition");

    // Message IDs
    private static final int MESSAGE_S2C_SYNC_NUTRIENTS = 0;

    // Register messages on run
    public static void registerMessages() {
        NETWORK_CHANNEL.registerMessage(
            SPacketNutrients.Handler.class,
            SPacketNutrients.Message.class,
            MESSAGE_S2C_SYNC_NUTRIENTS,
            Side.CLIENT);
    }
}
