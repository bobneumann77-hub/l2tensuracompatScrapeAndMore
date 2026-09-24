package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.misc.WarpPortalEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import java.awt.Color;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Portal.Transition;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarpingEffect extends TensuraMobEffect implements Portal {
   public WarpingEffect() {
      super(MobEffectCategory.NEUTRAL, new Color(183, 50, 255).getRGB());
   }

   @NotNull
   public Transition getLocalTransition() {
      return Transition.CONFUSION;
   }

   public int getPortalTransitionTime(ServerLevel serverLevel, Entity entity) {
      return 1000;
   }

   @Nullable
   public DimensionTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos blockPos) {
      return null;
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      WarpPortalEntity.setInsidePortal(entity, this, entity.blockPosition(), entity.getDimensionChangingDelay());
      if (entity.tickCount % 4 == 0) {
         TensuraParticleHelper.spawnServerParticles(
            entity.level(), ParticleTypes.PORTAL, entity.getX(), entity.getY() + entity.getBbHeight() / 3.0F, entity.getZ(), 10, 0.08, 0.08, 0.08, 0.3, false
         );
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return true;
   }

   @Override
   public void onEffectRemoved(LivingEntity entity, MobEffectInstance instance) {
      if (instance.getDuration() > 1) {
         entity.level().playSound(null, entity, (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         TensuraParticleHelper.spawnParticlesLikeServer(
            entity.level(),
            ParticleTypes.REVERSE_PORTAL,
            entity.getX(),
            entity.getY() + entity.getBbHeight() / 2.0F,
            entity.getZ(),
            30,
            0.1,
            0.1,
            0.1,
            0.5,
            false
         );
      } else if (!entity.level().isClientSide()) {
         UUID uuid = instance.tensura$getSource();
         Entity owner = uuid != null ? ((ServerLevel)entity.level()).getEntity(uuid) : null;
         if (instance.tensura$getSourceAbility() != null && instance.tensura$getSourceAbility().getSkill() != null && owner instanceof LivingEntity warper) {
            Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(warper).getSkill(instance.tensura$getSourceAbility().getSkill());
            if (optional.isEmpty()) {
               return;
            }

            optional.get().addMasteryPoint(warper);
            optional.get().markDirty();
         }

         CompoundTag tag = instance.tensura$getOrCreateTag();
         WarpPoint point = WarpPoint.fromNBT(tag.getCompound("WarpPoint"), entity.registryAccess());
         entity.level().playSound(null, entity.blockPosition(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         point.warp(entity, owner, false, true);
         entity.level().playSound(null, entity.blockPosition(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            ParticleTypes.REVERSE_PORTAL,
            entity.getX(),
            entity.getY() + entity.getBbHeight() / 2.0F,
            entity.getZ(),
            55,
            0.08,
            0.08,
            0.08,
            1.0,
            false
         );
      }
   }
}
