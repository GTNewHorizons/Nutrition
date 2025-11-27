package ca.wescook.nutrition.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ca.wescook.nutrition.data.PlayerDataHandler;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin {

    @Inject(method = "writeEntityToNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At(value = "TAIL"))
    private void nutrition$writeEntityToNBT(NBTTagCompound tagCompound, CallbackInfo ci) {
        PlayerDataHandler.saveForPlayer((EntityPlayer) (Object) this, tagCompound);
    }

    @Inject(method = "readEntityFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At(value = "TAIL"))
    private void nutrition$readEntityFromNBT(NBTTagCompound tagCompound, CallbackInfo ci) {
        PlayerDataHandler.initializeForPlayer((EntityPlayer) (Object) this, tagCompound);
    }
}
