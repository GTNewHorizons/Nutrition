package ca.wescook.nutrition.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;

import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.proxy.ClientProxy;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketUpdateServerNutrients {

    public static class Message implements IMessage {

        // server only
        Map<Nutrient, Float> serverNutrients;

        public Message() {}

        // serialized into bytes (on client)
        @Override
        public void fromBytes(ByteBuf buf) {
            // Loop through nutrients from client player, and add to buffer
            Map<Nutrient, Float> nutrientData = ClientProxy.localNutrition.get();
            for (Map.Entry<Nutrient, Float> entry : nutrientData.entrySet()) {
                ByteBufUtils.writeUTF8String(buf, entry.getKey().name);
                buf.writeFloat(entry.getValue());
            }
        }

        // Then deserialized (on server)
        @Override
        public void toBytes(ByteBuf buf) {
            // Loop through buffer stream to build nutrition data
            serverNutrients = new HashMap<>();
            while (buf.isReadable()) {
                String identifier = ByteBufUtils.readUTF8String(buf);
                Float value = buf.readFloat();
                serverNutrients.put(NutrientList.getByName(identifier), value);
            }
        }
    }

    // Handled on server
    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            PlayerDataHandler.getForPlayer(player)
                .set(message.serverNutrients);
            return null;
        }
    }
}
