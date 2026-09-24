package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MagicAuraSkill extends Skill {
   public static final ExtraSkillConfig.MagicAura CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).MagicAura;

   public MagicAuraSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return entity instanceof ServerPlayer player ? player.getStats().getValue(Stats.CUSTOM.get(TensuraStats.MAGIC_MASTERED)) >= CONFIG.magicMastered : false;
   }

   public int getModes(ManasSkillInstance instance) {
      return 7;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (!instance.isMastered(entity)) {
         return mode == 0 ? -1 : 0;
      } else if (reverse) {
         return mode == 0 ? 6 : mode - 1;
      } else {
         return mode == 6 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "magic_aura.default";
         case 1 -> "magic_aura.holy";
         case 2 -> "magic_aura.earth";
         case 3 -> "magic_aura.fire";
         case 4 -> "magic_aura.space";
         case 5 -> "magic_aura.water";
         case 6 -> "magic_aura.wind";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 0 ? CONFIG.magiculeCost : CONFIG.magiculeCostElemental;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_AURA))) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         MobEffectInstance magicAura = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_AURA), CONFIG.auraDuration, 0, false, false, false
         );
         magicAura.tensura$setSourceAbility(instance.getSkill(), mode);
         CompoundTag tag = magicAura.tensura$getOrCreateTag();
         tag.putInt("element", mode);
         entity.addEffect(magicAura);
      } else {
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_AURA));
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 0.5F
            );
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? false : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_AURA));
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      instance.addMasteryPoint(entity);
   }
}
