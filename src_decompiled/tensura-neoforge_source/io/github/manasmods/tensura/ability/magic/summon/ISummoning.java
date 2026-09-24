package io.github.manasmods.tensura.ability.magic.summon;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.SpawnPointHelper;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface ISummoning<T extends Mob> {
   ResourceLocation SUMMONING_BOOST = ResourceLocation.fromNamespaceAndPath("tensura", "summoning_boost");

   @Nullable
   EntityType<? extends T> getSummonedType(ManasSkillInstance var1, LivingEntity var2, int var3);

   void addAdditionalSummonData(ManasSkillInstance var1, LivingEntity var2, T var3, int var4);

   void summonMagicCircle(ManasSkillInstance var1, LivingEntity var2, Vec3 var3, int var4, int var5);

   ParticleOptions getSummoningParticle(ManasSkillInstance var1, int var2);

   SoundEvent getSummoningSound(ManasSkillInstance var1, int var2);

   SoundEvent getFailSound(ManasSkillInstance var1, int var2);

   default boolean canRemoveSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return true;
   }

   default int getSuccessCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return 0;
   }

   default Pair<Double, Double> getSummonedCostPerSecond() {
      return Pair.of(0.0, 0.0);
   }

   default boolean isSummoningDisabled(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == -1 || instance.onCoolDown(mode);
   }

   default boolean isSummoningRequirementFulfilled(ManasSkillInstance instance, LivingEntity entity, T summon, int mode) {
      return !EnergyHelper.isOutOfEnergy(entity, instance, mode);
   }

   default boolean canSummonPlayers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.level().getGameRules().getBoolean(TensuraGameRules.PLAYER_SUMMONING);
   }

   default void startSummoning(ManasSkillInstance instance, LivingEntity entity, int mode) {
      BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, 10.0);
      Vec3 pos = result.getLocation();
      CompoundTag tag = instance.getOrCreateTag();
      tag.remove("SummonUUID");
      tag.putDouble("circleX", pos.x);
      tag.putDouble("circleY", pos.y);
      tag.putDouble("circleZ", pos.z);
      instance.markDirty();
   }

   default void createSummon(ManasSkillInstance instance, LivingEntity entity, int mode, Vec3 position, String summonId) {
      EntityType<? extends T> type = this.getSummonedType(instance, entity, mode);
      if (type != null) {
         Level level = entity.level();
         T mob = (T)type.create(level);
         if (mob != null) {
            mob.setNoAi(true);
            mob.noPhysics = true;
            mob.setPos(position.add(0.0, -1.5 * mob.getBbHeight(), 0.0));
            mob.finalizeSpawn(
               (ServerLevelAccessor)level, level.getCurrentDifficultyAt(ObjectSelectionHelper.getBlockPos(position)), MobSpawnType.MOB_SUMMONED, null
            );
            this.addAdditionalSummonData(instance, entity, mob, mode);
            level.addFreshEntity(mob);
            mob.lookAt(Anchor.EYES, entity.getEyePosition());
            instance.getOrCreateTag().putUUID(summonId, mob.getUUID());
         }
      }
   }

   default boolean callForthSummon(ManasSkillInstance instance, LivingEntity entity, int mode, UUID summonUUID, int summoningTime) {
      Level level = entity.level();
      Entity summon = ((ServerLevel)level).getEntity(summonUUID);
      if (summon instanceof Mob mob) {
         summon.setPos(summon.position().add(0.0, mob.getBbHeight() * 1.5 / 39.0, 0.0));
         TensuraParticleHelper.addServerParticlesAroundSelf(mob, this.getSummoningParticle(instance, mode), 3.0);
         mob.lookAt(entity, 30.0F, 30.0F);
         if (summoningTime == 40) {
            if (this.isSummoningRequirementFulfilled(instance, entity, (T)mob, mode)) {
               instance.addMasteryPoint(entity);
               summon.noPhysics = false;
               mob.setNoAi(false);
               mob.playSound(this.getSummoningSound(instance, mode), 3.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH, 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH, 3.0);
               this.onPostSummon(instance, entity, (T)mob, mode);
               instance.setCoolDowns(this.getSuccessCooldown(instance, entity));
               return true;
            }

            this.removeFailedSummon(instance, entity, mode);
            return false;
         }

         mob.noPhysics = true;
      }

      return false;
   }

   default void onPostSummon(ManasSkillInstance instance, LivingEntity entity, T summon, int mode) {
   }

   default void removeExistingSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
   }

   default void removeFailedSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      CompoundTag tag = instance.getTag();
      if (tag != null && tag.hasUUID("SummonUUID")) {
         Entity summon = ((ServerLevel)entity.level()).getEntity(tag.getUUID("SummonUUID"));
         if (summon instanceof Mob mob) {
            if (!mob.isNoAi()) {
               return;
            }

            summon.discard();
            mob.playSound(this.getFailSound(instance, mode), 3.0F, 1.0F);
            TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH);
            TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH, 2.0);
            tag.remove("SummonUUID");
            instance.markDirty();
         }
      }
   }

   default void onSummonDeath(ManasSkillInstance instance, LivingEntity summon) {
      CompoundTag tag = instance.getTag();
      if (tag != null) {
         if (tag.hasUUID("SummonUUID")) {
            if (Objects.equals(summon.getUUID(), tag.getUUID("SummonUUID"))) {
               tag.remove("SummonUUID");
               instance.setCoolDowns(0);
            }
         }
      }
   }

   default void onSummonRemoval(LivingEntity entity, boolean onDeath) {
   }

   static void removeSummon(LivingEntity summon, IExistence existence) {
      if (existence.getSummonedAbility().getSkill() instanceof ISummoning<?> summoning) {
         summoning.onSummonRemoval(summon, false);
      }

      if (summon instanceof ServerPlayer player) {
         existence.setSpiritualForm(true);
         summon.sendSystemMessage(Component.translatable("tensura.summon.time_out").withStyle(ChatFormatting.RED));
         UUID summoner = existence.getSummoner();
         if (summoner != null) {
            Entity summonerEntity = ((ServerLevel)player.level()).getEntity(summoner);
            if (summonerEntity != null) {
               summonerEntity.sendSystemMessage(
                  Component.translatable("tensura.summon.end", new Object[]{summon.getDisplayName()}).withStyle(ChatFormatting.RED)
               );
            }
         }

         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
         if (optional.isEmpty()) {
            return;
         }

         Pair<ResourceKey<Level>, BlockState> pair = optional.get().getRespawnDimension(player);
         SpawnPointHelper.teleportToNewSpawn(player, (ResourceKey)pair.getFirst(), (BlockState)pair.getSecond());
         Tuple<ServerLevel, Vec3> spawn = SpawnPointHelper.getSpawn(player.level(), (ResourceKey)pair.getFirst(), (BlockState)pair.getSecond());
         if (spawn == null) {
            return;
         }

         Vec3 pos = (Vec3)spawn.getB();
         if (player.getRespawnDimension().equals(((ServerLevel)spawn.getA()).dimension()) && player.getRespawnPosition() != null) {
            pos = player.getRespawnPosition().getBottomCenter();
         }

         SpawnPointHelper.teleportToAcrossDimensions(player, (ServerLevel)spawn.getA(), pos.x, pos.y, pos.z, player.getXRot(), player.getYRot());
      } else {
         DamageSource source = TensuraDamageTypes.getDamageSource(summon.level(), TensuraDamageTypes.ENERGY_DRAIN).tensura$setNotActualDeath(true);
         if (existence.getSummonedSecond() <= 0) {
            source = source.tensura$setCustomMessage("tensura.summon.end");
         }

         summon.hurt(source, summon.getMaxHealth());
      }

      existence.setSummonedAbility(null, 0);
      existence.setSummoner(null);
   }
}
