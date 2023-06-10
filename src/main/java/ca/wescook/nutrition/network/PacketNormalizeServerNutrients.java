package ca.wescook.nutrition.network;

import net.minecraft.entity.player.EntityPlayerMP;

import ca.wescook.nutrition.data.NutrientManager;
import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketNormalizeServerNutrients {

    public static class Message implements IMessage {

        float nutrientDelta;

        public Message() {}

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
            NutrientManager manager = PlayerDataHandler.getForPlayer(player);

            // Normalize values towards 50 (starting value)
            boolean wasChanged = false;
            for (Nutrient nutrient : NutrientList.get()) {
                Float currentValue = manager.get(nutrient);
                if (currentValue > 50f) {
                    wasChanged = true;
                    manager.set(nutrient, Math.max(50f, currentValue - message.nutrientDelta));
                } else if (currentValue < 50f) {
                    wasChanged = true;
                    manager.set(nutrient, Math.min(50f, currentValue + message.nutrientDelta));
                }
            }
            // Only update client if change was actually made
            if (wasChanged) {
                Sync.serverRequest(player);
            }
            return null;
        }
    }
}
