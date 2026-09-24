package io.github.manasmods.tensura.ability.magic.spiritual.earth;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.spike.DripstoneSpikeEntity;
import io.github.manasmods.tensura.entity.magic.spike.MagicSpikeEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class EarthSpikesMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.EarthSpikes CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).EarthSpikes;

   public EarthSpikesMagic() {
      super(Element.EARTH, SpiritualMagic.SpiritLevel.MEDIUM);
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
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            Level level = entity.level();
            Entity targetingEntity = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
            Vec3 pos;
            if (targetingEntity != null && targetingEntity.onGround() && level.getBlockState(targetingEntity.getOnPos().below()).isSolid()) {
               pos = targetingEntity.position();
            } else {
               BlockHitResult targetPos = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, 20.0);
               pos = targetPos.getLocation();
               if (level.getBlockState(targetPos.getBlockPos()).canBeReplaced()) {
                  pos = pos.add(0.0, -1.0, 0.0);
                  if (!level.getBlockState(targetPos.getBlockPos().below(2)).isSolid() && !level.getBlockState(targetPos.getBlockPos().below(3)).isSolid()) {
                     pos = null;
                  }
               } else if (!level.getBlockState(targetPos.getBlockPos().below()).isSolid() && !level.getBlockState(targetPos.getBlockPos().below(2)).isSolid()) {
                  pos = null;
               }
            }

            if (pos != null) {
               instance.addMasteryPoint(entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               spawnSpike(pos, entity, instance, CONFIG.spikeDamage, this, mode);
               if (instance.isMastered(entity)) {
                  for (LivingEntity target : entity.level()
                     .getEntitiesOfClass(
                        LivingEntity.class,
                        new AABB(ObjectSelectionHelper.getBlockPos(pos)).inflate(2.5),
                        living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
                     )) {
                     if (target.onGround() && target != targetingEntity) {
                        spawnSpike(target.position(), entity, instance, CONFIG.spikeDamage, this, mode);
                     }
                  }
               }
            }
         }
      }
   }

   public static MagicSpikeEntity spawnSpike(Vec3 pos, LivingEntity entity, ManasSkillInstance instance, float damage, TensuraSkill skill, int mode) {
      MagicSpikeEntity spike = new DripstoneSpikeEntity(entity.level(), entity);
      spike.setPos(pos);
      spike.setDamage(damage);
      spike.setYaw(-30.0F);
      spike.setPitch(ObjectSelectionHelper.getYRotFromVector(spike.position().subtract(entity.position()).normalize()));
      spike.setHeight(CONFIG.spikeHeight);
      spike.setSkill(entity, instance, skill, mode);
      entity.level().addFreshEntity(spike);
      spike.triggerAnim("controller", "start");
      EffectStorage.setCameraShake(spike, 3.0, 0.005F, 5);
      entity.level()
         .playSound(null, spike.getX(), spike.getY(), spike.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      return spike;
   }
}
