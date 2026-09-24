package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.magic.misc.MagicLandmineEntity;
import io.github.manasmods.tensura.entity.projectile.magic.FusionistProjectile;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class FusionistSkill extends Skill {
   private static final UniqueSkillConfig.Fusionist CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Fusionist;

   public FusionistSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "fusionist.disassemble";
         case 1 -> "fusionist.fuse";
         case 2 -> "fusionist.projectile";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostFuse;
         case 2 -> CONFIG.magiculeCostProjectile;
         default -> 0.0;
      };
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      switch (mode) {
         case 0:
            if (entity.isShiftKeyDown()) {
               ItemStack stack = entity.getMainHandItem();
               if (stack.getItem() instanceof BlockItem item && !item.getBlock().defaultBlockState().is(TensuraBlockTags.SKILL_UNBREAKABLE)) {
                  stack.shrink(1);
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  tag.putInt("matters", tag.getInt("matters") + 1);
                  if (entity instanceof Player player) {
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.fusionist.matter_amount", new Object[]{tag.getInt("matters")}).withStyle(ChatFormatting.RED),
                        true
                     );
                  }

                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  instance.markDirty();
                  return;
               }
            }

            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(
               level, entity, Fluid.NONE, Math.max(5.0, entity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE))
            );
            BlockPos pos = result.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) {
               return;
            }

            if (state.is(TensuraBlockTags.SKILL_UNBREAKABLE)) {
               if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
               }

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

            if (!TensuraGameRules.canSkillGrief(level)) {
               entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.gamerule").withStyle(ChatFormatting.RED));
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

            if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
               .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
               .isFalse()) {
               if (entity instanceof Player player) {
                  state.getBlock().playerWillDestroy(level, pos, state, player);
               }

               level.destroyBlock(pos, false, entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               tag.putInt("matters", tag.getInt("matters") + 1);
               if (entity instanceof Player player) {
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.fusionist.matter_amount", new Object[]{tag.getInt("matters")}).withStyle(ChatFormatting.RED), true
                  );
               }

               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               instance.markDirty();
               ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                  .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
            }
            break;
         case 1:
            double range = Math.max(CONFIG.range, entity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE));
            if (instance.isMastered(entity)) {
               MagicLandmineEntity landmine = ObjectSelectionHelper.getTargetingEntity(MagicLandmineEntity.class, entity, range, 0.5, false, false, false);
               if (landmine != null && landmine.getOwner() == entity) {
                  if (landmine.getRadius() >= CONFIG.maxBlastRadius) {
                     if (entity instanceof Player player) {
                        player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
                     }

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

                  if (tag.getInt("matters") < CONFIG.bonusBlastCost && !entity.hasInfiniteMaterials()) {
                     if (entity instanceof Player player) {
                        player.displayClientMessage(Component.translatable("tensura.skill.fusionist.out_of_matter").withStyle(ChatFormatting.RED), true);
                     }

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
                  } else {
                     landmine.setDamage(landmine.getDamage() + CONFIG.bonusBlastDamage);
                     landmine.setRadius(landmine.getRadius() + CONFIG.bonusBlastRadius);
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     if (!entity.hasInfiniteMaterials()) {
                        tag.putInt("matters", tag.getInt("matters") - CONFIG.bonusBlastCost);
                     }

                     level.playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                  }

                  return;
               }
            }

            if (tag.hasUUID("mineId") && level instanceof ServerLevel serverLevel) {
               if (serverLevel.getEntity(tag.getUUID("mineId")) instanceof MagicLandmineEntity landmine && landmine.getOwner() == entity) {
                  landmine.trigger();
                  tag.remove("mineId");
                  tag.remove("mineX");
                  tag.remove("mineY");
                  tag.remove("mineZ");
                  instance.addMasteryPoint(entity);
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  return;
               }

               tag.remove("mineId");
            } else if (tag.contains("mineY")) {
               AABB aabb = new AABB(new BlockPos((int)tag.getDouble("mineX"), (int)tag.getDouble("mineY"), (int)tag.getDouble("mineZ"))).inflate(0.5);
               List<MagicLandmineEntity> list = level.getEntitiesOfClass(MagicLandmineEntity.class, aabb, entityData -> entityData.getOwner() == entity);
               if (!list.isEmpty()) {
                  for (MagicLandmineEntity landmine : list) {
                     landmine.trigger();
                  }

                  tag.remove("mineX");
                  tag.remove("mineY");
                  tag.remove("mineZ");
                  instance.addMasteryPoint(entity);
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  return;
               }
            }

            if (tag.getInt("matters") < CONFIG.fuseMatterCost && !entity.hasInfiniteMaterials()) {
               if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("tensura.skill.fusionist.out_of_matter").withStyle(ChatFormatting.RED), true);
               }

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
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, range);
               BlockPos pos = result.getBlockPos();
               if (level.getBlockState(pos).isAir()) {
                  return;
               }

               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               MagicLandmineEntity landmine = new MagicLandmineEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, entity);
               landmine.setDamage(instance.isMastered(entity) ? CONFIG.fuseBlastDamageMastered : CONFIG.fuseBlastDamage);
               landmine.setSkill(entity, instance, this, mode);
               landmine.setLimitedGriefing(true);
               landmine.setExplosionType(MagicLandmineEntity.ExplosionType.MAGIC_FULL);
               landmine.setRadius(CONFIG.fuseBlastRadius);
               level.addFreshEntity(landmine);
               entity.swing(InteractionHand.MAIN_HAND, true);
               tag.putUUID("mineId", landmine.getUUID());
               tag.putDouble("mineX", landmine.getX());
               tag.putDouble("mineY", landmine.getY());
               tag.putDouble("mineZ", landmine.getZ());
               if (!entity.hasInfiniteMaterials()) {
                  tag.putInt("matters", tag.getInt("matters") - CONFIG.fuseMatterCost);
               }

               instance.markDirty();
               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TNT_PRIMED, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
            break;
         case 2:
            if (tag.getInt("matters") < CONFIG.projectileMatterCost && !entity.hasInfiniteMaterials()) {
               if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("tensura.skill.fusionist.out_of_matter").withStyle(ChatFormatting.RED), true);
               }

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
            } else {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               FusionistProjectile sphere = new FusionistProjectile(level, entity);
               sphere.setSpeed(1.5F);
               sphere.setExplosionRadius(CONFIG.projectileBlastRadius);
               sphere.setSkill(entity, instance, this, mode);
               sphere.setPosAndShoot(entity);
               level.addFreshEntity(sphere);
               if (!entity.hasInfiniteMaterials()) {
                  tag.putInt("matters", tag.getInt("matters") - CONFIG.projectileMatterCost);
               }

               instance.addMasteryPoint(entity);
               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ARROW_SHOOT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
      }
   }
}
