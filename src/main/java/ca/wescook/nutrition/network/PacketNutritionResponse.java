package ca.wescook.nutrition.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import ca.wescook.nutrition.data.NutrientManager;
import ca.wescook.nutrition.data.PlayerDataHandler;
import ca.wescook.nutrition.gui.NutritionGui;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import ca.wescook.nutrition.proxy.ClientProxy;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketNutritionResponse {

    public static class Message implements IMessage {

        // server only
        EntityPlayer serverPlayer;
        // client only
        Map<Nutrient, Float> clientNutrients;

        public Message() {}

        // Message data is passed along from server
        public Message(EntityPlayer player) {
            serverPlayer = player;
        }

        // Then serialized into bytes (on server)
        @Override
        public void toBytes(ByteBuf buf) {
            // Loop through nutrients from server player, and add to buffer
            Map<Nutrient, Float> nutrientData = PlayerDataHandler.getForPlayer(serverPlayer)
                .get();
            for (Map.Entry<Nutrient, Float> entry : nutrientData.entrySet()) {
                ByteBufUtils.writeUTF8String(buf, entry.getKey().name);
                buf.writeFloat(entry.getValue());
            }
        }

        // Then deserialized (on the client)
        @Override
        public void fromBytes(ByteBuf buf) {
            // Loop through buffer stream to build nutrition data
            clientNutrients = new HashMap<>();
            while (buf.isReadable()) {
                String identifier = ByteBufUtils.readUTF8String(buf);
                Float value = buf.readFloat();
                clientNutrients.put(NutrientList.getByName(identifier), value);
            }
        }
    }

    // This is the client's handling of the information
    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            if (ClientProxy.localNutrition != null) {
                ClientProxy.localNutrition.set(message.clientNutrients);
            } else {
                ClientProxy.localNutrition = new NutrientManager(message.clientNutrients);
            }

            // If Nutrition GUI is open, update GUI
            GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
            if (currentScreen instanceof NutritionGui) {
                ((NutritionGui) currentScreen).redrawLabels();
            }
            return null;
        }
    }
}
