package io.github.manasmods.tensura.ability.magic.aspectual.misc;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Objects;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class SearchEnemyMagic extends AspectualMagic {
   public static final AspectualMagicConfig.SearchEnemy CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).SearchEnemy;

   public SearchEnemyMagic() {
      super(AspectualMagic.AspectualType.MISC);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? (instance.isMastered(entity) ? 1 : -1) : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return mode == 1 ? "search_enemy.constant" : super.getModeId(instance, mode);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0 && entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ENEMY_SEARCH))) {
         entity.swing(InteractionHand.MAIN_HAND, true);
         instance.setCoolDown(CONFIG.castTime / 20, mode);
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.ENEMY_SEARCH));
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode == 1) {
         if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
            return false;
         }

         if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
            return false;
         }

         int castTime = this.getCastingTime(instance, entity);
         if (heldTicks >= castTime && heldTicks % 20 == 0) {
            if (this.isOutOfEnergy(entity, instance, (double)0.0, (double)CONFIG.magiculeCostConstant)) {
               return false;
            }

            MobEffectInstance search = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ENEMY_SEARCH), 25, 0, true, false, true);
            TensuraMobEffect.addEffect(entity, search, entity, this);
         }

         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      } else {
         return super.onHeld(instance, entity, heldTicks, mode);
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode != 1 && !instance.onCoolDown(0)) {
         if (heldTicks >= this.getCastingTime(instance, entity)) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               MobEffectInstance search = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.ENEMY_SEARCH), CONFIG.searchDuration, 0, true, false, true
               );
               TensuraMobEffect.addEffect(entity, search, entity, this);
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            }
         }
      }
   }

   @Override
   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      if (!(instance.getMastery() < 0.0) && !instance.isMastered(entity)) {
         MobEffectInstance search = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.ENEMY_SEARCH));
         if (search == null) {
            return false;
         } else {
            return !Objects.equals(search.tensura$getSource(), entity.getUUID())
               ? false
               : search.tensura$getSourceAbility() != null && search.tensura$getSourceAbility().getSkill() == this;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      instance.addMasteryPoint(entity);
   }
}
