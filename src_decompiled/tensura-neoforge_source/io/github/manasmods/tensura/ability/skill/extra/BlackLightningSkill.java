package io.github.manasmods.tensura.ability.skill.extra;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.entity.magic.beam.BeamProjectile;
import io.github.manasmods.tensura.entity.magic.lightning.BlackLightningBolt;
import io.github.manasmods.tensura.entity.magic.misc.DeathTornadoEntity;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BlackLightningSkill extends Skill {
   public static final ExtraSkillConfig.BlackLightning CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).BlackLightning;

   public BlackLightningSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 5;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (!this.canAdvanceModes(entity)) {
         return -1;
      }

      if (reverse) {
         return switch (mode) {
            case 0 -> this.canEquipDeathStorm(entity) ? 4 : (instance.isMastered(entity) ? 3 : 0);
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> instance.isMastered(entity) ? 3 : 2;
            default -> -1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> instance.isMastered(entity) ? 3 : (this.canEquipDeathStorm(entity) ? 4 : 0);
            case 3 -> this.canEquipDeathStorm(entity) ? 4 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(4);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "black_lightning.default";
         case 1 -> "black_lightning.weak";
         case 2 -> "black_lightning.strong";
         case 3 -> "black_lightning.blast";
         case 4 -> "black_lightning.storm";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostWeak;
         case 2 -> CONFIG.magiculeCostStrong;
         case 3 -> CONFIG.magiculeCostBlast;
         case 4 -> CONFIG.magiculeCostStorm;
         default -> CONFIG.magiculeCost;
      };
   }

   public boolean canAdvanceModes(LivingEntity entity) {
      return SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.MOLECULAR_MANIPULATION.get());
   }

   public boolean canEquipDeathStorm(LivingEntity entity) {
      return SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.WIND_DOMINATION.get())
         ? true
         : SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.WIND_MANIPULATION.get());
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getOrCreateTag().getInt("StormingTick") > 0;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 3) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      BeamProjectile.spawnLastingBeam(
         (EntityType<? extends BeamProjectile>)MiscEntityTypes.BLACK_LIGHTNING_BLAST.get(),
         CONFIG.blastLightningDamage,
         CONFIG.blastMagicDamage,
         1.0F,
         5,
         CONFIG.blastRange,
         4.0F,
         entity.getEyePosition(),
         entity,
         instance,
         mode,
         cost,
         cost,
         heldTicks
      );
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(), TensuraSkill.ABILITY_SOUND, 0.8F, 0.5F
         );
      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      switch (mode) {
         case 3:
            instance.getOrCreateTag().putInt("BeamID", 0);
            instance.markDirty();
            break;
         case 4:
            CompoundTag tag = instance.getOrCreateTag();
            if (tag.getInt("StormingTick") > 0) {
               tag.putInt("StormingTick", 0);
               this.stopDeathStorm(entity, level);
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            } else {
               if (!entity.level().getBlockState(entity.blockPosition().above(50)).canBeReplaced()) {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.location").withStyle(ChatFormatting.RED));
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
                  return;
               }

               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               if (this.learnMode(instance, entity, mode)) {
                  return;
               }

               instance.addMasteryPoint(entity);
               tag.putInt("StormingTick", CONFIG.stormStrikes);
               if (level instanceof ServerLevel serverLevel && !serverLevel.isThundering()) {
                  serverLevel.setWeatherParameters(0, 24000, true, true);
               }

               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }

            instance.markDirty();
            entity.swing(InteractionHand.MAIN_HAND, true);
            break;
         default:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            Entity target = ObjectSelectionHelper.getTargetingEntity(entity, 60.0, false, false);
            Vec3 pos;
            if (target != null) {
               pos = target.position();
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 50.0);
               pos = result.getLocation();
            }

            instance.addMasteryPoint(entity);
            BlackLightningBolt bolt = new BlackLightningBolt(level, entity);
            if (entity instanceof ServerPlayer serverPlayer) {
               bolt.setCause(serverPlayer);
            }

            bolt.setSkill(entity, instance, this, mode);
            float radius = 0.0F;
            switch (mode) {
               case 0:
                  radius = CONFIG.defaultRange;
                  bolt.setTensuraDamage(CONFIG.defaultLightningDamage);
                  bolt.setSecondaryDamage(CONFIG.defaultMagicDamage);
                  bolt.setAdditionalVisual(5);
                  instance.setCoolDown(CONFIG.defaultCooldown, mode);
                  break;
               case 1:
                  radius = CONFIG.weakRange;
                  bolt.setTensuraDamage(CONFIG.weakLightningDamage);
                  bolt.setSecondaryDamage(CONFIG.weakMagicDamage);
                  bolt.setAdditionalVisual(3);
                  instance.setCoolDown(CONFIG.weakCooldown, mode);
                  break;
               case 2:
                  radius = CONFIG.strongRange;
                  bolt.setTensuraDamage(CONFIG.strongLightningDamage);
                  bolt.setSecondaryDamage(CONFIG.strongMagicDamage);
                  bolt.setAdditionalVisual(10);
                  instance.setCoolDown(CONFIG.strongCooldown, mode);
            }

            bolt.setRadius(radius);
            bolt.setSkill(instance);
            bolt.setMode(mode);
            bolt.setPos(pos);
            level.addFreshEntity(bolt);
            entity.swing(InteractionHand.MAIN_HAND, true);
      }
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      if (!entity.level().getBlockState(entity.blockPosition().above(50)).canBeReplaced()) {
         this.stopDeathStorm(entity, level);
         tag.putInt("StormingTick", 0);
         entity.sendSystemMessage(
            Component.translatable("tensura.ability.activation_failed.named", new Object[]{this.getModeName(instance, 4)}).withStyle(ChatFormatting.RED)
         );
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
      } else {
         int stormTick = tag.getInt("StormingTick");
         List<Vec3> effectPositions = new ArrayList<>();

         for (LivingEntity target : entity.level()
            .getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(CONFIG.stormRadius), targetx -> this.canDeathStormTarget(entity, targetx))) {
            Vec3 pos = target.position();
            if (this.canSpawnEffect(pos, effectPositions)) {
               this.spawnLightning(entity, instance, target.position(), stormTick);
               effectPositions.add(pos);
            }
         }

         int spawnedTornado = 0;

         for (int tornadoAttempts = 0; spawnedTornado < CONFIG.stormTornadoNumber && tornadoAttempts < CONFIG.stormTornadoNumber * 10; tornadoAttempts++) {
            Vec3 target = this.getRandomGroundPosition(entity.level(), entity.blockPosition(), entity);
            if (this.canSpawnTornado(level, target)) {
               this.spawnTornado(entity, instance, target);
               effectPositions.add(target);
               spawnedTornado++;
            }
         }

         int spawned = 0;

         for (int boltAttempts = 0; spawned < CONFIG.stormBoltNumber && boltAttempts < CONFIG.stormBoltNumber * 10; boltAttempts++) {
            Vec3 target = this.getRandomGroundPosition(entity.level(), entity.blockPosition(), entity);
            if (this.canSpawnEffect(target, effectPositions)) {
               this.spawnLightning(entity, instance, target, stormTick);
               effectPositions.add(target);
               spawned++;
            }
         }

         level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         tag.putInt("StormingTick", stormTick - 1);
         instance.markDirty();
         if (stormTick - 1 <= 0) {
            this.stopDeathStorm(entity, level);
         }
      }
   }

   private void stopDeathStorm(LivingEntity entity, Level level) {
      if (level instanceof ServerLevel serverLevel && serverLevel.isThundering()) {
         serverLevel.setWeatherParameters(0, 24000, false, false);
      }

      for (DeathTornadoEntity tornado : level.getEntitiesOfClass(DeathTornadoEntity.class, entity.getBoundingBox().inflate(CONFIG.stormRadius))) {
         if (tornado.getOwner() == entity && tornado.getLife() - tornado.getAge() > 40) {
            tornado.setAge(tornado.getLife() - 40);
         }
      }
   }

   private boolean canDeathStormTarget(LivingEntity owner, LivingEntity target) {
      if (target == owner) {
         return false;
      } else if (target.isAlliedTo(owner)) {
         return false;
      } else if (TensuraStorages.getExistenceFrom(target).isTargetNeutral(owner.getUUID())) {
         return false;
      } else {
         return target.isAlive() && !target.hasInfiniteMaterials()
            ? target.getSoundSource() == SoundSource.HOSTILE || target.getSoundSource() == SoundSource.NEUTRAL
            : false;
      }
   }

   private boolean canSpawnEffect(Vec3 vec, List<Vec3> existing) {
      double minDist = CONFIG.stormBoltDistance;
      double minSq = minDist * minDist;

      for (Vec3 pos : existing) {
         if (pos.distanceToSqr(vec) < minSq) {
            return false;
         }
      }

      return true;
   }

   private Vec3 getRandomGroundPosition(Level level, BlockPos center, Entity entity) {
      double x = center.getX() - CONFIG.stormRadius + level.getRandom().nextDouble() * CONFIG.stormRadius * 2.0;
      double z = center.getZ() - CONFIG.stormRadius + level.getRandom().nextDouble() * CONFIG.stormRadius * 2.0;
      return ObjectSelectionHelper.getNearestGround(new Vec3(x, center.getY(), z), level, CONFIG.stormRadius, entity);
   }

   private void spawnLightning(LivingEntity entity, ManasSkillInstance instance, Vec3 target, int stormTick) {
      if (entity.getRandom().nextInt(3) == 0 || stormTick % 2 != 0) {
         BlackLightningBolt bolt = new BlackLightningBolt(entity.level(), entity);
         if (entity instanceof ServerPlayer serverPlayer) {
            bolt.setCause(serverPlayer);
         }

         bolt.setTensuraDamage(CONFIG.stormBoltDamage);
         bolt.setSecondaryDamage(CONFIG.stormBoltMagicDamage);
         bolt.setAdditionalVisual(4);
         bolt.setShouldPlaceFire(false);
         bolt.setSkill(instance);
         bolt.setMode(4);
         bolt.setMpCost(this.getMagiculeCost(entity, instance, 4) / 50.0);
         bolt.setApCost(this.getMagiculeCost(entity, instance, 4) / 50.0);
         bolt.setRadius(CONFIG.stormBoltRange);
         bolt.setSkill(instance);
         bolt.setPos(target);
         entity.level().addFreshEntity(bolt);
      }
   }

   private boolean canSpawnTornado(Level level, Vec3 vec3) {
      for (DeathTornadoEntity tornado : level.getEntitiesOfClass(
         DeathTornadoEntity.class, AABB.ofSize(vec3, CONFIG.stormTornadoDistance * 2.0, CONFIG.stormTornadoDistance, CONFIG.stormTornadoDistance * 2.0)
      )) {
         if (tornado.getLife() - tornado.getAge() > 30) {
            return false;
         }
      }

      return true;
   }

   private void spawnTornado(LivingEntity entity, ManasSkillInstance instance, Vec3 target) {
      DeathTornadoEntity tornado = new DeathTornadoEntity(entity.level(), entity);
      tornado.setSkill(entity, instance, this, 4);
      tornado.setDamage(CONFIG.stormTornadoDamage);
      tornado.setSecondaryDamage(CONFIG.stormTornadoMagicDamage);
      tornado.setSize(CONFIG.stormTornadoSize);
      tornado.setVisualSize(2.0F);
      tornado.setSkill(instance);
      tornado.setMode(4);
      tornado.setPos(target);
      entity.level().addFreshEntity(tornado);
   }

   public boolean onDeath(ManasSkillInstance instance, LivingEntity owner, DamageSource source) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.getInt("StormingTick") > 0) {
         tag.putInt("StormingTick", 0);
         this.stopDeathStorm(owner, owner.level());
      }

      return true;
   }
}
