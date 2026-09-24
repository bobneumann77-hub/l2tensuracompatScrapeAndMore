package io.github.manasmods.tensura.mixin.client;

import io.github.manasmods.tensura.data.TensuraItemTags;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
   @Shadow
   @Nullable
   private InteractionHand usingItemHand;
   @Shadow
   public Input input;

   @ModifyConstant(method = "aiStep()V", constant = @Constant(floatValue = 0.2F, ordinal = 0))
   private float useItemFirst(float constant) {
      LocalPlayer player = (LocalPlayer)this;
      ItemStack useItem = player.getItemInHand(this.usingItemHand);
      if (useItem.is(TensuraItemTags.NO_SLOWNESS_ON_USE)) {
         return 1.0F;
      } else if (useItem.is(TensuraItemTags.STOP_ON_USE)) {
         this.input.up = false;
         this.input.down = false;
         this.input.jumping = false;
         return 0.0F;
      } else {
         return constant;
      }
   }

   @ModifyConstant(method = "aiStep()V", constant = @Constant(floatValue = 0.2F, ordinal = 1))
   private float useItemSecond(float constant) {
      LocalPlayer player = (LocalPlayer)this;
      ItemStack useItem = player.getItemInHand(this.usingItemHand);
      if (useItem.is(TensuraItemTags.NO_SLOWNESS_ON_USE)) {
         return 1.0F;
      } else {
         return useItem.is(TensuraItemTags.STOP_ON_USE) ? 0.0F : constant;
      }
   }
}
