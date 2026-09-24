package io.github.manasmods.tensura.ability.magic.spiritual.space;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
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
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SwipeMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Swipe CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Swipe;

   public SwipeMagic() {
      super(Element.SPACE, SpiritualMagic.SpiritLevel.GREATER);
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
            MagicCircleVariant.SPACE,
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
      if (SkillUtils.shouldCancelTeleportation(entity)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
         }
      } else if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            Level level = entity.level();
            double range = instance.isMastered(entity) ? CONFIG.rangeMastered : CONFIG.range;
            Vec3 target = entity.position().add(entity.getLookAngle().scale(range));
            Vec3 source = entity.getEyePosition();
            Vec3 offSetToTarget = target.subtract(source);
            Vec3 normalizes = offSetToTarget.normalize();
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.SWIPE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
            );
            boolean touchedEntity = false;
            float damage = instance.isMastered(entity) ? CONFIG.damageMastered : CONFIG.damage;

            for (int i = 1; i < Mth.floor(offSetToTarget.length()); i++) {
               Vec3 particlePos = source.add(normalizes.scale(i));
               AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(particlePos)).inflate(0.5);
               List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, livingx -> !livingx.is(entity));
               if (!list.isEmpty()) {
                  for (LivingEntity living : list) {
                     Changeable<Vec3> position = Changeable.of(entity.position().add(normalizes.scale(2.0)));
                     if (!living.getType().is(TensuraEntityTags.NO_FORCED_WARP)) {
                        if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
                           .transmission(living, entity, position, WarpPoint.TransmissionType.ABILITY)
                           .isFalse()) {
                           return;
                        }

                        TensuraParticleHelper.addServerParticlesAroundSelf(living, ParticleTypes.REVERSE_PORTAL);
                        TensuraParticleHelper.addServerParticlesAroundSelf(living, ParticleTypes.END_ROD);
                        living.unRide();
                        living.teleportTo(((Vec3)position.get()).x(), ((Vec3)position.get()).y(), ((Vec3)position.get()).z());
                        living.hasImpulse = true;
                     } else {
                        position = Changeable.of(living.position().subtract(normalizes.scale(2.0)));
                        if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
                           .transmission(entity, entity, position, WarpPoint.TransmissionType.ABILITY)
                           .isFalse()) {
                           return;
                        }

                        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.REVERSE_PORTAL);
                        TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.END_ROD);
                        entity.resetFallDistance();
                        entity.unRide();
                        entity.teleportTo(((Vec3)position.get()).x(), ((Vec3)position.get()).y(), ((Vec3)position.get()).z());
                        entity.hasImpulse = true;
                     }

                     touchedEntity = true;
                     TensuraParticleHelper.addServerParticlesAroundSelf(living, ParticleTypes.REVERSE_PORTAL);
                     TensuraParticleHelper.addServerParticlesAroundSelf(living, ParticleTypes.END_ROD);
                     DamageSource damageSource = this.createSource(instance, entity, TensuraDamageTypes.SPACE_ELEMENTAL, mode).tensura$setDodgeBypass();
                     living.hurt(damageSource, damage);
                  }
               }
            }

            if (!touchedEntity) {
               Changeable<Vec3> position = Changeable.of(target.add(normalizes.scale(2.0)));
               if (!((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
                  .transmission(entity, entity, position, WarpPoint.TransmissionType.ABILITY)
                  .isFalse()) {
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.REVERSE_PORTAL);
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.END_ROD);
                  entity.resetFallDistance();
                  entity.unRide();
                  entity.teleportTo(((Vec3)position.get()).x(), ((Vec3)position.get()).y(), ((Vec3)position.get()).z());
                  entity.hasImpulse = true;
                  level.playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.SWIPE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.REVERSE_PORTAL);
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.END_ROD);
               }
            }
         }
      }
   }
}
