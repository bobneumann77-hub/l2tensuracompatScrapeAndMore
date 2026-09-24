package io.github.manasmods.tensura.ability.magic.aspectual.barrier;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.AntiMagicAreaEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AntiMagicAreaMagic extends AspectualMagic {
   private static final AspectualMagicConfig.AntiMagicArea CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).AntiMagicArea;

   public AntiMagicAreaMagic() {
      super(AspectualMagic.AspectualType.BARRIER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryHigh;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      return !entity.isShiftKeyDown() ? super.onHeld(instance, entity, heldTicks, mode) : true;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            instance.isMastered(entity) ? CONFIG.radiusMastered : CONFIG.radius,
            25,
            MagicCircleVariant.BARRIER,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            new Vec3(0.0, 0.1, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.isShiftKeyDown()) {
         double distance = CONFIG.radiusMastered;
         AntiMagicAreaEntity barrier = ObjectSelectionHelper.getTargetingEntity(AntiMagicAreaEntity.class, entity, distance, 0.5, false, false, false);
         if (barrier != null) {
            if (barrier.getOwner() == entity && barrier.getLife() - barrier.getAge() > 20) {
               barrier.setRemoveIn(48);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.5F,
                     0.5F
                  );
               entity.level()
                  .playSound(
                     null,
                     barrier.getX(),
                     barrier.getY(),
                     barrier.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            }
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (!entity.isShiftKeyDown()) {
         if (heldTicks >= this.getCastingTime(instance, entity)) {
            AntiMagicAreaEntity barrier = new AntiMagicAreaEntity(entity.level(), entity);
            barrier.setLife(CONFIG.duration);
            barrier.setSize(instance.isMastered(entity) ? CONFIG.radiusMastered : CONFIG.radius);
            barrier.setVisualSize(barrier.getSize());
            barrier.setAge(0);
            barrier.setSkill(entity, instance, this, mode);
            barrier.setPos(entity.getX(), entity.getY() - barrier.getSize(), entity.getZ());
            entity.level().addFreshEntity(barrier);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            entity.swing(InteractionHand.MAIN_HAND, true);
         }
      }
   }
}
