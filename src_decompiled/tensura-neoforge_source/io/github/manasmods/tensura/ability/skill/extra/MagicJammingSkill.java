package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class MagicJammingSkill extends Skill {
   public static final ExtraSkillConfig.MagicJamming CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).MagicJamming;
   private static final ResourceLocation MAGIC_JAMMING = ResourceLocation.fromNamespaceAndPath("tensura", "magic_jamming");

   public MagicJammingSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      float multiplier = instance.isMastered(entity) ? CONFIG.inputDamageMitigationMastered : CONFIG.inputDamageMitigation;
      AttributeHelper.addPermanentAttribute(entity, TensuraAttributes.MAGIC_INTERFERENCE, MAGIC_JAMMING, multiplier, Operation.ADD_VALUE);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttribute(entity, TensuraAttributes.MAGIC_INTERFERENCE, MAGIC_JAMMING);
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (!this.isMastered(instance, attacker)) {
         return true;
      }

      if (source.getDirectEntity() != attacker) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      if (EnergyHelper.getMaxEP(target) > EnergyHelper.getMaxEP(attacker) * CONFIG.epMultiplier) {
         return true;
      }

      TensuraMobEffect.removePredicateEffect(target, effect -> effect instanceof ITransformation);
      attacker.level()
         .playSound(
            null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
         );
      target.addEffect(
         new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE), CONFIG.jammingDuration, 1, false, false, false), attacker
      );
      return true;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0, 5.0F)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
         instance.onToggleOff(entity);
      } else {
         double distance = instance.isMastered(entity) ? CONFIG.jammingRadiusMastered : CONFIG.jammingRadius;
         if (entity.getType().equals(MonsterEntityTypes.CHARYBDIS.get())) {
            distance = 30.0;
         }

         List<LivingEntity> list = entity.level()
            .getEntitiesOfClass(
               LivingEntity.class,
               entity.getBoundingBox().inflate(distance),
               living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity) && !living.hasInfiniteMaterials()
            );
         if (!list.isEmpty()) {
            CompoundTag tag = instance.getOrCreateTag();
            int time = tag.getInt("activatedTimes");
            if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
               instance.addMasteryPoint(entity);
            }

            tag.putInt("activatedTimes", time + 1);

            for (LivingEntity target : list) {
               if (!(EnergyHelper.getMaxEP(target) > EnergyHelper.getMaxEP(entity) * 3.5)) {
                  target.addEffect(
                     new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE), CONFIG.jammingArenaDuration, 0, false, false, false
                     ),
                     entity
                  );
                  if (target instanceof Player player && !RaceUtils.canStillFly(player, true, true, true)) {
                     player.setNoGravity(false);
                     if (player.getAbilities().mayfly) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                     }
                  }
               }
            }
         }
      }
   }
}
