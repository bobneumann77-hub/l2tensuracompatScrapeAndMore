package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.common.ThoughtCommunicationSkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class SpearheadSkill extends Skill {
   public static final UniqueSkillConfig.Spearhead CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Spearhead;

   public SpearheadSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return this.isInSlot(entity, instance);
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      List<LivingEntity> list = entity.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CONFIG.allyRadius),
            living -> !living.is(entity) && living.isAlive() && living.isAlliedTo(entity)
         );
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            target.addEffect(
               new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.SPEARHEAD), 240, instance.isMastered(entity) ? 1 : 0, false, false, false),
               entity
            );
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity instanceof Player player) {
         if (!player.isSecondaryUseActive()) {
            List<Mob> list = player.level()
               .getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(CONFIG.allyRadius), living -> SubordinateHelper.isSubordinate(player, living));
            if (list.isEmpty()) {
               player.displayClientMessage(
                  Component.translatable("tensura.telepathy.subordinate_all.not_found").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true
               );
               return;
            }

            CompoundTag tag = instance.getOrCreateTag();
            int command = tag.getInt("command");
            command = command == 4 ? 1 : command + 1;
            tag.putInt("command", command);

            for (Mob mob : list) {
               MutableComponent message = switch (command) {
                  case 2 -> {
                     SubordinateHelper.setFollow(mob);
                     yield Component.translatable("tensura.telepathy.subordinate_all.follow");
                  }
                  case 3 -> {
                     SubordinateHelper.setWander(mob);
                     yield Component.translatable("tensura.telepathy.subordinate_all.wander");
                  }
                  case 4 -> {
                     SubordinateHelper.setFollow(mob);
                     IEffect effect = TensuraStorages.getEffectFrom(mob);
                     effect.setMeatShield(true);
                     effect.markDirty();
                     yield Component.translatable("tensura.telepathy.subordinate_all.meat_shield");
                  }
                  default -> {
                     SubordinateHelper.setStay(mob);
                     IEffect effect = TensuraStorages.getEffectFrom(mob);
                     effect.setMeatShield(false);
                     effect.markDirty();
                     yield Component.translatable("tensura.telepathy.subordinate_all.stay");
                  }
               };
               player.swing(InteractionHand.MAIN_HAND, true);
               player.displayClientMessage(message.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true);
               player.level()
                  .playSound(
                     null,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            }
         } else {
            ThoughtCommunicationSkill.movementTelepathy(instance, entity);
         }
      }
   }

   @Override
   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity entity, DamageSource source) {
      if (!source.tensura$isNotActualDeath()) {
         if (!entity.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
            if (!(entity instanceof Player) || entity.level().getLevelData().isHardcore()) {
               double fallenRange = CONFIG.fallenRange;
               if (!(owner.distanceToSqr(entity) > fallenRange * fallenRange)) {
                  List<ManasSkillInstance> targetSkills = List.copyOf(
                     SkillAPI.getSkillsFrom(entity).getLearnedSkills().stream().filter(this::canCollect).toList()
                  );
                  if (!targetSkills.isEmpty()) {
                     for (ManasSkillInstance targetInstance : targetSkills) {
                        if (!targetInstance.isTemporarySkill() && !(targetInstance.getMastery() < 0.0) && targetInstance.getSkill() != this) {
                           Changeable<ManasSkill> changeable = Changeable.of(targetInstance.getSkill());
                           if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                              .plunder(entity, owner, true, changeable)
                              .isFalse()) {
                              ManasSkillInstance newSkill = ((ManasSkill)changeable.get()).createDefaultInstance();
                              MutableComponent message = Component.translatable(
                                    "tensura.skill.acquire_fallen", new Object[]{newSkill.getChatDisplayName(true), entity.getName()}
                                 )
                                 .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
                              if (SkillHelper.learnSkill(owner, newSkill, instance.getRemoveTime(), message)) {
                                 entity.sendSystemMessage(
                                    Component.translatable(
                                       "tensura.skill.forget.stolen", new Object[]{((ManasSkill)changeable.get()).getChatDisplayName(true), owner.getName()}
                                    )
                                 );
                                 SkillAPI.getSkillsFrom(entity).forgetSkill((ManasSkill)changeable.get());
                                 instance.addMasteryPoint(entity);
                                 if (owner instanceof Player player) {
                                    player.playNotifySound(SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public boolean canCollect(ManasSkillInstance instance) {
      if (instance.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      } else {
         return !instance.is(TensuraSkillTags.SKILLS) ? false : !instance.is(TensuraSkillTags.ULTIMATE_SKILLS);
      }
   }
}
