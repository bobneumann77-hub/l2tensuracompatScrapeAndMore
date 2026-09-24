package io.github.manasmods.tensura.mixin.warden;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.data.TensuraTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Warden.class)
public abstract class MixinWarden {
   @ModifyReturnValue(method = "canTargetEntity(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
   public boolean canTarget(boolean original, Entity entity) {
      if (entity instanceof LivingEntity target) {
         if (SkillUtils.shouldCancelInteraction(target)) {
            return false;
         }

         if (SkillUtils.canBlockSoundDetect(target)) {
            return false;
         }
      }

      return original;
   }

   @ModifyReturnValue(method = "isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("RETURN"))
   public boolean isInvulnerableTo(boolean original, DamageSource pSource) {
      return pSource.is(TensuraTags.DamageTypes.IS_MAGIC_FIRE) ? false : original;
   }
}
