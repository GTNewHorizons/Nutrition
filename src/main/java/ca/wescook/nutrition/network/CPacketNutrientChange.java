package ca.wescook.nutrition.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;

import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CPacketNutrientChange {

    public static void send(Nutrient nutrient, float value) {
        ModPacketHandler.NETWORK_CHANNEL.sendToServer(new Message(nutrient, value));
    }

    public static void sendBulk(Map<Nutrient, Float> values) {
        ModPacketHandler.NETWORK_CHANNEL.sendToServer(new Message(values));
    }

    public static class Message implements IMessage {

        private Map<Nutrient, Float> values;

        public Message() {/**/}

        public Message(Nutrient nutrient, float value) {
            this.values = new HashMap<>();
            this.values.put(nutrient, value);
        }

        public Message(Map<Nutrient, Float> values) {
            this.values = values;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            for (var entry : this.values.entrySet()) {
                ByteBufUtils.writeUTF8String(buf, entry.getKey().name);
                buf.writeFloat(entry.getValue());
            }
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.values = new HashMap<>();
            while (buf.isReadable()) {
                String identifier = ByteBufUtils.readUTF8String(buf);
                Nutrient nutrient = NutrientList.getByName(identifier);
                float value = buf.readFloat();
                this.values.put(nutrient, value);
            }
        }
    }

    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(Message message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            for (var entry : message.values.entrySet()) {
                NutritionManager.instance()
                    .set(player, entry.getKey(), entry.getValue());
            }

            SPacketNutrients.send(player);
            return null;
        }
    }
}
