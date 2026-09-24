package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ISpatialMovement;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.projectile.magic.SpatialArrowProjectile;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TravelerSkill extends Skill implements ISpatialMovement {
   private static final UniqueSkillConfig.Traveler CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Traveler;
   protected static final ResourceLocation TRAVELER = ResourceLocation.fromNamespaceAndPath("tensura", "traveler");

   public TravelerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 5;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> instance.isMastered(entity) ? 4 : 3;
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 3;
            default -> 1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> instance.isMastered(entity) ? 4 : 0;
            default -> 0;
         };
      }
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(3, 4);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "traveler.instant_motion";
         case 1 -> "traveler.teleport";
         case 2 -> "sniper.spatial";
         case 3 -> "traveler.stardust_arrow";
         case 4 -> "traveler.stardust_rain";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public int getSpatialMovementModeIndex() {
      return 1;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 3 -> CONFIG.magiculeCostArrow;
         case 4 -> CONFIG.magiculeCostRain;
         default -> 0.0;
      };
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 3 -> CONFIG.auraCostArrow;
         case 4 -> CONFIG.auraCostRain;
         default -> 0.0;
      };
   }

   @Override
   public double getWarpCost(double distance) {
      return CONFIG.magiculeCostTeleport * distance;
   }

   @Override
   public double getPortalCost(double distance) {
      return CONFIG.magiculeCostPortal * distance;
   }

   @Override
   public int getWarpChargeTick(ManasSkillInstance instance, LivingEntity entity) {
      return CONFIG.warpChargeTick;
   }

   @Override
   public int getWarpCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.teleportCooldownMastered : CONFIG.teleportCooldown;
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         if (entity instanceof ServerPlayer player) {
            ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
            data.setMaxWarpPoints(data.getMaxWarpPoints() + 5);
            data.markDirty();
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.SPACE_BOOST, CONFIG.manipulationBoost);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.SPACE_BOOST, CONFIG.manipulationBoost);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      switch (mode) {
         case 0:
            if (SkillUtils.shouldCancelTeleportation(entity)) {
               if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
               }

               return;
            }

            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, CONFIG.motionRange);
            BlockPos pos = result.getBlockPos().relative(result.getDirection());
            if (level.getBlockState(pos).is(TensuraBlockTags.SKILL_NOT_TELEPORTABLE)) {
               level.playSound(
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

            if (EnergyHelper.isOutOfEnergy(entity, 0.0, CONFIG.magiculeCostMotion * Math.sqrt(entity.distanceToSqr(pos.getX(), pos.getY(), pos.getZ())))) {
               return;
            }

            ISpatialMovement.warp(entity, pos.getCenter().x(), pos.getY(), pos.getCenter().z());
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.motionCooldownMastered : CONFIG.motionCooldown, mode);
            break;
         case 1:
            if (entity instanceof ServerPlayer player) {
               if (SkillUtils.shouldCancelTeleportation(entity)) {
                  player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
                  return;
               }

               this.openSpatialMovementMenu(player, this);
            }
            break;
         case 2:
            AttributeInstance warpShot = entity.getAttribute(TensuraAttributes.WARP_SHOT);
            if (warpShot == null) {
               return;
            }

            if (warpShot.getModifier(TRAVELER) != null) {
               AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.WARP_SHOT, TRAVELER, CONFIG.warpShot);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else if (AttributeHelper.addPermanentAttributeIfHigher(entity, TensuraAttributes.WARP_SHOT, TRAVELER, CONFIG.warpShot, Operation.ADD_VALUE)) {
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
            break;
         case 3:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            if (this.learnMode(instance, entity, mode)) {
               return;
            }

            SpatialArrowProjectile arrow = new SpatialArrowProjectile(level, entity);
            arrow.setSkill(entity, instance, this, mode);
            arrow.setDamage(CONFIG.arrowDamage);
            if (!this.warpShotArrow(entity, arrow)) {
               arrow.setPos(entity.getX(), entity.getEyeY() - 0.2F, entity.getZ());
               Vec3 vector = entity.getViewVector(2.0F);
               arrow.shoot(vector.x(), vector.y(), vector.z(), 2.0F, 0.0F);
            }

            level.addFreshEntity(arrow);
            level.playSound(
               null, arrow.getX(), arrow.getY(), arrow.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
            break;
         case 4:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            if (this.learnMode(instance, entity, mode)) {
               return;
            }

            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.rainRange, false, false);
            if (target == null) {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
               entity.playSound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get());
               return;
            }

            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.rainCooldownMastered : CONFIG.rainCooldown, mode);
            Vec3 pos = target.position().add(0.0, target.getEyeHeight(), 0.0);
            int arrowAmount = CONFIG.rainNumber;
            RandomSource arrowRand = entity.getRandom();

            for (int i = 0; i < arrowAmount; i++) {
               Vec3 arrowPos = pos.add(
                  new Vec3(0.0, arrowRand.nextDouble() - 0.5, 0.6)
                     .normalize()
                     .scale(target.getBbWidth() + 6.0F)
                     .yRot(360.0F * i * (float) (Math.PI / 180.0) / arrowAmount)
               );
               SpatialArrowProjectile arrow = new SpatialArrowProjectile(level, entity);
               arrow.setSkill(entity, instance, this, mode);
               arrow.setIgnoreInvulnerabilityOnHit(true);
               arrow.setSpeed(1.0F);
               arrow.setPos(arrowPos);
               arrow.shootFromRot(pos.subtract(arrowPos).normalize());
               arrow.setLife(50);
               arrow.setDamage(CONFIG.rainDamage);
               arrow.setSkill(entity, instance, this, mode, 1.0F / arrowAmount);
               level.addFreshEntity(arrow);
               level.playSound(
                  null, arrow.getX(), arrow.getY(), arrow.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }
      }
   }

   private boolean warpShotArrow(LivingEntity owner, SpatialArrowProjectile arrow) {
      if (!owner.isShiftKeyDown()) {
         return false;
      }

      Entity entity = ObjectSelectionHelper.getTargetingEntity(owner, 30.0, 0.0, false, false);
      if (entity == null) {
         return false;
      }

      arrow.shootFromBehind(entity, 2.0F, 0.0F);
      return true;
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      AttributeInstance attribute = entity.getAttribute(TensuraAttributes.WARP_SHOT);
      if (attribute != null && attribute.getModifier(TRAVELER) != null) {
         attribute.removeModifier(TRAVELER);
      }

      if (!(instance.getMastery() < 0.0)) {
         if (entity instanceof ServerPlayer player) {
            ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
            data.setMaxWarpPoints(Math.max(data.getMaxWarpPoints() - 5, 1));
            data.removeWarpPointOverMax(5);
            data.markDirty();
         }
      }
   }
}
