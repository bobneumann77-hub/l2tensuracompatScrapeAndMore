package io.github.manasmods.tensura.ability.magic.aspectual.illusion;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.ability.skill.intrinsic.PossessionSkill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class PossessionMagic extends AspectualMagic implements ICloning {
   private static final AspectualMagicConfig.Possession CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Possession;

   public PossessionMagic() {
      super(AspectualMagic.AspectualType.ILLUSION);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryGreat;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
      Vec3 pos;
      if (target != null) {
         pos = target.position();
      } else {
         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, CONFIG.range);
         pos = result.getLocation();
      }

      CompoundTag tag = instance.getOrCreateTag();
      tag.putDouble("circleX", pos.x);
      tag.putDouble("circleY", pos.y);
      tag.putDouble("circleZ", pos.z);
      instance.markDirty();
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      CompoundTag tag = instance.getOrCreateTag();
      Vec3 targetPos = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY") + 0.2F, tag.getDouble("circleZ"));
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(2.0F, 30, targetPos, MagicCircleVariant.ILLUSION, entity, instance.getOrCreateTag(), instance, mode, cost);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (entity instanceof Player player) {
            Level level = entity.level();
            if (SkillUtils.inSpiritualWorld(level.dimension())) {
               player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.location").withStyle(ChatFormatting.RED), false);
               level.playSound(
                  null,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            } else if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ENERGY_BLOCKADE))) {
               player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.status").withStyle(ChatFormatting.RED), false);
               level.playSound(
                  null,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            } else {
               IExistence existence = TensuraStorages.getExistenceFrom(player);
               LivingEntity target = ObjectSelectionHelper.getTargetingEntity(player, CONFIG.range, false);
               if (target != null && target.isAlive()) {
                  CompoundTag tag = instance.getOrCreateTag();
                  if (player.level().getEntity(tag.getInt("MagicCircleID")) instanceof MagicCircle magicCircle
                     && !(target.distanceToSqr(magicCircle.position()) > 9.0)) {
                     float multiplier = 1.0F;
                     AttributeInstance illusionBoost = entity.getAttribute(TensuraAttributes.ILLUSION_BOOST);
                     if (illusionBoost != null) {
                        multiplier = (float)(multiplier * illusionBoost.getValue());
                     }

                     if (!PossessionSkill.canPossess(
                        target,
                        player,
                        this,
                        CONFIG.resistanceMultiplier,
                        CONFIG.hpMultiplier * multiplier,
                        CONFIG.shpMultiplier * multiplier,
                        CONFIG.epMultiplier * multiplier,
                        false
                     )) {
                        player.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
                        level.playSound(
                           null,
                           player.getX(),
                           player.getY(),
                           player.getZ(),
                           (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                     } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                        PossessionSkill.turnSpiritual(instance, player, existence, CONFIG.bodyDespawnTick);
                        PossessionSkill.possess(instance, player, existence, target, CONFIG.maxAttack, CONFIG.maxHealth);
                     }
                  } else {
                     player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
                     level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                  }
               } else {
                  player.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               }
            }
         }
      }
   }

   @Override
   public void onCloneTick(CloneEntity clone, LivingEntity owner) {
      if (clone.getLife() <= 0) {
         Optional<ManasSkillInstance> possession = SkillAPI.getSkillsFrom(owner).getSkill(this);
         if (possession.isEmpty()) {
            clone.setLife(CONFIG.bodyDespawnTick * 20);
         } else if (possession.get().getOrCreateTag().hasUUID("OriginalBody")
            && !Objects.equals(possession.get().getOrCreateTag().getUUID("OriginalBody"), clone.getUUID())) {
            clone.setLife(CONFIG.bodyDespawnTick * 20);
         }
      }
   }
}
