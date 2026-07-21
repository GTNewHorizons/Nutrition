package ca.wescook.nutrition.network;

import net.minecraft.entity.player.EntityPlayerMP;

import ca.wescook.nutrition.api.INutritionManager;
import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class CPacketNormalizeNutrients {

    public static void send(float delta) {
        ModPacketHandler.NETWORK_CHANNEL.sendToServer(new Message(delta));
    }

    public static class Message implements IMessage {

        private float nutrientDelta;

        public Message() {/**/}

        public Message(float statsChange) {
            this.nutrientDelta = statsChange;
        }

        // serialized into bytes (on client)
        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeFloat(nutrientDelta);
        }

        // Then deserialized (on server)
        @Override
        public void fromBytes(ByteBuf buf) {
            this.nutrientDelta = buf.readFloat();
        }
    }

    // Handled on server
    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            INutritionManager manager = NutritionManager.instance();

            // Normalize values towards 50 (starting value)
            for (Nutrient nutrient : NutrientList.get()) {
                float currentValue = manager.get(player, nutrient);
                if (currentValue > 50f) {
                    manager.set(player, nutrient, Math.max(50f, currentValue - message.nutrientDelta));
                } else if (currentValue < 50f) {
                    manager.set(player, nutrient, Math.min(50f, currentValue + message.nutrientDelta));
                }
            }

            return null;
        }
    }
}
