package ca.wescook.nutrition.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import ca.wescook.nutrition.api.NutritionManager;
import ca.wescook.nutrition.gui.NutritionGui;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class SPacketNutrients {

    public static void send(EntityPlayer player) {
        Map<Nutrient, Float> values = ((NutritionManager) NutritionManager.instance()).getAll(player);
        ModPacketHandler.NETWORK_CHANNEL.sendTo(new Message(values), (EntityPlayerMP) player);
    }

    public static class Message implements IMessage {

        private Map<Nutrient, Float> values;

        public Message() {/**/}

        // Message data is passed along from server
        public Message(Map<Nutrient, Float> values) {
            this.values = values;
        }

        // Then serialized into bytes (on server)
        @Override
        public void toBytes(ByteBuf buf) {
            for (Map.Entry<Nutrient, Float> entry : this.values.entrySet()) {
                ByteBufUtils.writeUTF8String(buf, entry.getKey().name);
                buf.writeFloat(entry.getValue());
            }
        }

        // Then deserialized (on the client)
        @Override
        public void fromBytes(ByteBuf buf) {
            // Loop through buffer stream to build nutrition data
            this.values = new HashMap<>();
            while (buf.isReadable()) {
                String identifier = ByteBufUtils.readUTF8String(buf);
                Float value = buf.readFloat();
                this.values.put(NutrientList.getByName(identifier), value);
            }
        }
    }

    // This is the client's handling of the information
    public static class Handler implements IMessageHandler<Message, IMessage> {

        @Override
        public IMessage onMessage(final Message message, final MessageContext context) {
            handle(message);
            return null;
        }

        @SideOnly(Side.CLIENT)
        private void handle(final Message message) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            NutritionManager.instance()
                .setAll(player, message.values);

            // If Nutrition GUI is open, update GUI
            GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
            if (currentScreen instanceof NutritionGui) {
                ((NutritionGui) currentScreen).redrawLabels();
            }
        }
    }
}
