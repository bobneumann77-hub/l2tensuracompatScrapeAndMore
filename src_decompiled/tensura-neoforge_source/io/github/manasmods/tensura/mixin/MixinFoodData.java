package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = FoodData.class, priority = 800)
public abstract class MixinFoodData {
   @Unique
   private static Holder<MobEffect> tensura$healthcareHolder;

   @WrapOperation(
      method = "tick(Lnet/minecraft/world/entity/player/Player;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V")
   )
   public void healTick(Player player, float heal, Operation<Void> original) {
      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
      if (optional.isEmpty() || !optional.get().is(TensuraRaceTags.UNABLE_TO_HEAL_WITH_FOOD)) {
         original.call(new Object[]{player, heal});
      }
   }

   @ModifyConstant(method = "tick(Lnet/minecraft/world/entity/player/Player;)V", constant = @Constant(floatValue = 1.0F, ordinal = 0))
   public float tick(float health, Player player) {
      Holder<MobEffect> h = tensura$healthcareHolder;
      if (h == null) {
         h = TensuraMobEffects.getReference(TensuraMobEffects.HEALTHCARE);
         tensura$healthcareHolder = h;
      }

      return player.hasEffect(h) ? 0.2F : health;
   }
}
