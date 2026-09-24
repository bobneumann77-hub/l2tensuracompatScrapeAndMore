package io.github.manasmods.tensura.ability.magic.aspectual.earth;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.AreaField;
import io.github.manasmods.tensura.entity.magic.field.MudHands;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class MudHandMagic extends AspectualMagic {
   public static final AspectualMagicConfig.MudHand CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).MudHand;

   public MudHandMagic() {
      super(AspectualMagic.AspectualType.EARTH);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      instance.getOrCreateTag().putInt("FieldID", 0);
      instance.markDirty();
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      MagicCircle.castMagicCircle(
         0.75F,
         25,
         MagicCircleVariant.EARTH,
         entity,
         instance.getOrCreateTag(),
         0.75F,
         Vec3.ZERO,
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
      if (heldTicks < castTime) {
         this.applyCastingVisual(instance, entity, heldTicks, mode);
         return true;
      }

      if (heldTicks == castTime) {
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
         if (target == null) {
            entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            return false;
         }

         if (!target.isAlive() || !this.isTargetNearGround(target)) {
            entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            return false;
         }

         instance.addMasteryPoint(entity);
         MobEffectInstance slow = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 60, CONFIG.bindLevel - 1, false, false, false
         );
         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         if (AreaField.getLastingField(
            (EntityType<? extends AreaField>)MiscEntityTypes.MUD_HANDS.get(),
            0.0F,
            target.getBbWidth() / 0.6F,
            25,
            20,
            slow,
            target.position(),
            entity,
            instance,
            mode,
            cost,
            Pair.of(0.0, 0.0),
            heldTicks
         ) instanceof MudHands hands) {
            hands.triggerAnim("controller", "start");
            hands.setVisualSize(target.getBbHeight() / 1.8F);
            hands.setTarget(target);
            target.setPos(hands.position());
            target.hurtMarked = true;
         }
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         if (!(entity.level().getEntity(tag.getInt("FieldID")) instanceof MudHands hands)) {
            tag.putInt("FieldID", 0);
            instance.markDirty();
            return false;
         }

         if (heldTicks % 20 != 0) {
            hands.increaseLife(1);
         }
      }

      int tick = heldTicks - castTime;
      int holdDuration = instance.isMastered(entity) ? CONFIG.maxHoldMastered : CONFIG.maxHold;
      this.renderRemainingTime(entity, tick, holdDuration);
      return tick < holdDuration;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      if (entity.level().getEntity(tag.getInt("FieldID")) instanceof MudHands hands) {
         if (hands.isAlive()) {
            Entity target = hands.getTarget();
            if (target != null && target.isAlive() && !(target.distanceToSqr(hands) > 16.0)) {
               if (target instanceof LivingEntity living) {
                  MobEffectInstance slow = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CONFIG.slowDuration, CONFIG.slowLevel - 1, true, false, true);
                  living.addEffect(slow, entity);
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
            }
         }
      } else {
         tag.putInt("FieldID", 0);
         instance.markDirty();
      }
   }

   private boolean isTargetNearGround(LivingEntity target) {
      if (target.onGround()) {
         return true;
      } else if (target.level().getBlockState(target.getOnPos().below(1)).isSolid()) {
         return true;
      } else {
         return target.level().getBlockState(target.getOnPos().below(2)).isSolid() ? true : target.level().getBlockState(target.getOnPos().below(3)).isSolid();
      }
   }
}
