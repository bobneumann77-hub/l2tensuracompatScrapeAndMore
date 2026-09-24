package io.github.manasmods.tensura.item.consumable;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import lombok.Generated;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

public class HealingPotionItem extends ManaPotionItem {
   private final float hpAmount;
   private boolean healPercentage = false;

   public HealingPotionItem(int nutrition, float saturationModifier, float healAmount, float magiculeAmount) {
      super(nutrition, saturationModifier, magiculeAmount);
      this.hpAmount = healAmount;
   }

   public ManaPotionItem setHealPercentage() {
      this.healPercentage = true;
      return this;
   }

   @Override
   public void applyEffect(LivingEntity entity, float multiplier) {
      if (entity.isAlive()) {
         if (this.getHpAmount() > 0.0F) {
            float amount = this.isHealPercentage() ? this.getHpAmount() * entity.getMaxHealth() : this.getHpAmount();
            entity.heal(amount * multiplier);
         }

         this.regenerateMagicule(entity, multiplier);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
            );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getGreenWave(0.9F, entity.getBbWidth() * 2.5F, -0.5F, true),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.33,
            entity.getZ()
         );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getGreenWave(0.9F, entity.getBbWidth() * 2.5F, -0.5F, true),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.66,
            entity.getZ()
         );
      }
   }

   @Generated
   public float getHpAmount() {
      return this.hpAmount;
   }

   @Generated
   public boolean isHealPercentage() {
      return this.healPercentage;
   }
}
