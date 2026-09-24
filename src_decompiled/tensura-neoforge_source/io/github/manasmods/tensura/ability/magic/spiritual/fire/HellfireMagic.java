package io.github.manasmods.tensura.ability.magic.spiritual.fire;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.Hellfire;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class HellfireMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Hellfire CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Hellfire;

   public HellfireMagic() {
      super(Element.FLAME, SpiritualMagic.SpiritLevel.GREATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            1.5F,
            25,
            MagicCircleVariant.FLAME,
            entity,
            instance.getOrCreateTag(),
            1.0F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            double distance = instance.isMastered(entity) ? CONFIG.rangeMastered : CONFIG.range;
            Entity target = ObjectSelectionHelper.getTargetingEntity(entity, distance, false, true);
            Vec3 pos;
            if (target != null) {
               pos = target.position().add(0.0, target.getBbHeight() / 2.0F, 0.0);
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, distance);
               pos = result.getLocation().add(0.0, 0.5, 0.0);
            }

            Hellfire sphere = new Hellfire(entity.level(), entity);
            sphere.setDamage(instance.isMastered(entity) ? CONFIG.sphereDamageMastered : CONFIG.sphereDamage);
            sphere.setSkill(entity, instance, this, mode);
            sphere.setLife(80);
            sphere.setSize(instance.isMastered(entity) ? CONFIG.sphereRadiusMastered : CONFIG.sphereRadius);
            sphere.setPos(pos.x, pos.y - sphere.getSize(), pos.z);
            entity.level().addFreshEntity(sphere);
            entity.level()
               .playSound(
                  null, sphere.getX(), sphere.getY(), sphere.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }
}
