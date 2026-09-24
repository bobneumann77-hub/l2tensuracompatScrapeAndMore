package io.github.manasmods.tensura.ability.magic.spiritual.darkness;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.entity.magic.barrier.DarkCubeEntity;
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

public class DarkCubeMagic extends SpiritualMagic {
   public static final SpiritualMagicConfig.DarkCube CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).DarkCube;

   public DarkCubeMagic() {
      super(Element.DARKNESS, SpiritualMagic.SpiritLevel.MEDIUM);
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
            1.0F,
            25,
            MagicCircleVariant.DARK,
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

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      return !entity.isShiftKeyDown() ? super.onHeld(instance, entity, heldTicks, mode) : true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.isShiftKeyDown()) {
         double distance = instance.isMastered(entity) ? CONFIG.rangeMastered : CONFIG.range;
         BarrierPart part = ObjectSelectionHelper.getTargetingEntity(BarrierPart.class, entity, distance, 0.5, false, false, false);
         if (part != null) {
            if (part.getBarrier() instanceof DarkCubeEntity barrier && barrier.getOwner() == entity && barrier.getLife() - barrier.getAge() > 20) {
               barrier.setRemoveIn(20);
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
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
               double distance = instance.isMastered(entity) ? CONFIG.rangeMastered : CONFIG.range;
               Entity target = ObjectSelectionHelper.getTargetingEntity(entity, distance, false, true);
               Vec3 pos;
               if (target != null) {
                  pos = target.position();
               } else {
                  BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, distance);
                  pos = result.getLocation().add(0.0, 0.25, 0.0);
               }

               DarkCubeEntity cube = new DarkCubeEntity(entity.level(), entity);
               cube.setPos(pos);
               cube.setDamage(instance.isMastered(entity) ? CONFIG.cubeDamageMastered : CONFIG.cubeDamage);
               cube.setSkill(entity, instance, this, mode);
               cube.setLife(CONFIG.cubeDuration);
               cube.setSize(CONFIG.cubeRadius);
               cube.setVisualSize(cube.getSize());
               entity.level().addFreshEntity(cube);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_DARK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            }
         }
      }
   }
}
