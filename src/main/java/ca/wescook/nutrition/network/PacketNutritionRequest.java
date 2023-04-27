package ca.wescook.nutrition.network;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketNutritionRequest {

    // Empty message just to request information
    public static class Message implements IMessage {

        public Message() {}

        @Override
        public void toBytes(ByteBuf buf) {}

        @Override
        public void fromBytes(ByteBuf buf) {}
    }

    // Handled on server
    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity; // Get Player on server
            ModPacketHandler.NETWORK_CHANNEL.sendTo(new PacketNutritionResponse.Message(player), player);
            return null;
        }
    }
}
