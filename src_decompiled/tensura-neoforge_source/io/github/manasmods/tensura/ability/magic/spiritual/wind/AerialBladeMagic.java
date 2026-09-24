package io.github.manasmods.tensura.ability.magic.spiritual.wind;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class AerialBladeMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.AerialBlade CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).AerialBlade;

   public AerialBladeMagic() {
      super(Element.WIND, SpiritualMagic.SpiritLevel.GREATER);
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
            instance.isMastered(entity) ? CONFIG.rangeMastered : CONFIG.range,
            25,
            MagicCircleVariant.WIND,
            entity,
            instance.getOrCreateTag(),
            0.0F,
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
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            Level level = entity.level();
            entity.swing(InteractionHand.MAIN_HAND, true);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WIND_CHARGE_BURST, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            double radius = instance.isMastered(entity) ? CONFIG.rangeMastered : CONFIG.range;
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, radius);
            BlockPos ahead = result.getBlockPos();
            Vec3 aheadVec = new Vec3(ahead.getX(), ahead.getY(), ahead.getZ());
            RandomSource random = entity.getRandom();
            TensuraParticleHelper.addServerParticlesAroundPos(random, level, aheadVec, ParticleTypes.GUST, 3.0);
            TensuraParticleHelper.addServerParticlesAroundPos(random, level, aheadVec, TensuraParticleUtils.getGust(), 3.0);
            TensuraParticleHelper.addServerParticlesAroundPos(random, level, aheadVec, TensuraParticleUtils.getGust(3.0F), 4.0);
            TensuraParticleHelper.addServerParticlesAroundPos(random, level, aheadVec, ParticleTypes.SWEEP_ATTACK, 4.0);
            TensuraParticleHelper.addServerParticlesAroundPos(random, level, aheadVec, ParticleTypes.SWEEP_ATTACK, 5.0);
            AABB entityInflation = entity.getBoundingBox().inflate(radius);

            for (LivingEntity target : entity.level()
               .getEntitiesOfClass(
                  LivingEntity.class,
                  entityInflation,
                  living -> !living.is(entity)
                     && living.isAlive()
                     && !living.isAlliedTo(entity)
                     && !living.hasInfiniteMaterials()
                     && !living.getType().is(TensuraEntityTags.NO_FORCED_MOVE)
               )) {
               Changeable<Vec3> changeable = Changeable.of(aheadVec.subtract(target.position()).normalize().scale(2.0));
               if (!((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
                  .move(target, entity, instance, changeable)
                  .isFalse()) {
                  target.setDeltaMovement((Vec3)changeable.get());
                  target.hurtMarked = true;
               }
            }

            AABB box = new AABB(ahead).inflate(radius);
            List<LivingEntity> list = entity.level()
               .getEntitiesOfClass(
                  LivingEntity.class, box.minmax(entityInflation), living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
               );
            if (!list.isEmpty()) {
               for (LivingEntity target : list) {
                  if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
                     DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.WIND_ELEMENTAL, mode);
                     target.hurt(source, CONFIG.damage);
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.GUST, 3.0);
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, TensuraParticleUtils.getGust(), 3.0);
                  }
               }
            }
         }
      }
   }
}
