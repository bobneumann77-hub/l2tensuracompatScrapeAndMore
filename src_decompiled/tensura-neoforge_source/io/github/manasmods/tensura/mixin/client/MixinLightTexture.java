package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.Holder;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public class MixinLightTexture {
   @Unique
   private static float tensura$frameNightVision = 0.0F;
   @Unique
   private static float tensura$frameDarkVision = 0.0F;

   @Inject(method = "updateLightTexture(F)V", at = @At("HEAD"))
   private void cacheTensuraVision(float partialTick, CallbackInfo ci) {
      Player player = Minecraft.getInstance().player;
      if (player == null) {
         tensura$frameNightVision = 0.0F;
         tensura$frameDarkVision = 0.0F;
      } else {
         tensura$frameNightVision = this.tensura$getNightVision(player, 0.0F);
         tensura$frameDarkVision = this.tensura$getDarkVision(player, 0.0F);
      }
   }

   @WrapOperation(
      method = "updateLightTexture(F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/core/Holder;)Z", ordinal = 0)
   )
   private boolean hasNightVision(LocalPlayer instance, Holder<MobEffect> holder, Operation<Boolean> original) {
      return tensura$frameNightVision > 0.0F ? true : (Boolean)original.call(new Object[]{instance, holder});
   }

   @WrapOperation(
      method = "updateLightTexture(F)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/GameRenderer;getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F",
         ordinal = 0
      )
   )
   private float getNightVisionScale(LivingEntity instance, float f, Operation<Float> original) {
      return tensura$frameNightVision > 0.0F ? tensura$frameNightVision : (Float)original.call(new Object[]{instance, f});
   }

   @WrapOperation(
      method = "updateLightTexture(F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LightTexture;calculateDarknessScale(Lnet/minecraft/world/entity/LivingEntity;FF)F")
   )
   private float calculateDarknessScale(LightTexture instance, LivingEntity livingEntity, float f, float g, Operation<Float> original) {
      return tensura$frameDarkVision > 0.0F ? tensura$frameDarkVision : (Float)original.call(new Object[]{instance, livingEntity, f, g});
   }

   @WrapOperation(method = "updateLightTexture(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LightTexture;getDarknessGamma(F)F"))
   private float getDarknessGamma(LightTexture instance, float f, Operation<Float> original) {
      return tensura$frameDarkVision > 0.0F ? 1.0F : (Float)original.call(new Object[]{instance, f});
   }

   @WrapOperation(
      method = "updateLightTexture(F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;forceBrightLightmap()Z")
   )
   private boolean forceBrightLightmap(DimensionSpecialEffects instance, Operation<Boolean> original) {
      return tensura$frameDarkVision > 0.0F ? false : (Boolean)original.call(new Object[]{instance});
   }

   @Unique
   private float tensura$getNightVision(LivingEntity player, float original) {
      if (player.getAttributeValue(TensuraAttributes.PRESENCE_SENSE) > 0.0) {
         return 1.0F;
      }

      if (player.getItemBySlot(EquipmentSlot.HEAD).is((Item)TensuraArmorItems.ANTI_MAGIC_MASK.get())) {
         return 1.0F;
      }

      if (player.getItemBySlot(EquipmentSlot.HEAD).is((Item)TensuraArmorItems.TEARY_PIERROT_MASK.get())) {
         return 1.0F;
      }

      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(player).getRace();
      return race.isPresent() && race.get().is(TensuraRaceTags.CAN_BREATH_WATER) && player.isEyeInFluid(FluidTags.WATER) ? 1.0F : original;
   }

   @Unique
   private float tensura$getDarkVision(LivingEntity player, float original) {
      double darkVision = player.getAttributeValue(TensuraAttributes.DARK_VISION);
      return darkVision > 0.0 ? (float)darkVision : original;
   }
}
