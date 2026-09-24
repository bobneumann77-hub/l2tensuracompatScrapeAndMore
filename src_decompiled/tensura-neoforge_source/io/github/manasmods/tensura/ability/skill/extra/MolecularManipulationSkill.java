package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class MolecularManipulationSkill extends Skill {
   private static final ExtraSkillConfig.MolecularManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).MolecularManipulation;

   public MolecularManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "molecular_manipulation.block";
         case 1 -> "molecular_manipulation.entity";
         default -> super.getModeId(instance, mode);
      };
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      double newRange = tag.getDouble("range") + delta;
      if (newRange > CONFIG.maxRange) {
         newRange = CONFIG.maxRange;
      } else if (newRange < 0.0) {
         newRange = 0.0;
      }

      tag.putDouble("range", newRange);
      instance.markDirty();
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      switch (mode) {
         case 0:
            this.breakBlock(entity, instance, mode);
            break;
         case 1:
            Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.maxRange, 1.0, false, true, true);
            if (target == null) {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
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

            if (!entity.hasInfiniteMaterials()) {
               if (target.getType().is(TensuraEntityTags.FULL_GRAVITY_CONTROL) || target.getType().is(TensuraEntityTags.NO_FORCED_MOVE)) {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
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

               if (target instanceof Player player && player.getAbilities().invulnerable) {
                  return;
               }

               if (target instanceof LivingEntity living
                  && (
                     SkillUtils.isSkillToggled(living, (ManasSkill)ExtraSkills.GRAVITY_DOMINATION.get())
                        || SkillUtils.isSkillToggled(living, (ManasSkill)ExtraSkills.GRAVITY_MANIPULATION.get())
                  )) {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
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
            }

            double maxSize = instance.isMastered(entity) ? CONFIG.maxSizeMastered : CONFIG.maxSize;
            if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.GRAVITY_DOMINATION.get())) {
               maxSize += CONFIG.maxSizeDomination;
            } else if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.GRAVITY_MANIPULATION.get())) {
               maxSize += CONFIG.maxSizeManipulation;
            }

            if (target.getBoundingBox().getSize() > maxSize) {
               return;
            }

            CompoundTag tag = instance.getOrCreateTag();
            tag.putUUID("target", target.getUUID());
            double range = Math.min(CONFIG.maxRange, target.position().subtract(entity.getEyePosition()).length());
            tag.putDouble("range", (int)range);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 1) {
         return false;
      }

      if (entity.level() instanceof ServerLevel level) {
         CompoundTag tag = instance.getOrCreateTag();
         double range = tag.getDouble("range");
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.range", new Object[]{range}).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
            );
         }

         if (tag.contains("target")) {
            Entity target = level.getEntity(tag.getUUID("target"));
            if (target != null) {
               Vec3 viewVector = entity.getViewVector(1.0F).scale(range);
               double x = entity.getX() + viewVector.x;
               double y = entity.getEyeY() + viewVector.y;
               double z = entity.getZ() + viewVector.z;
               Vec3 targetPos = new Vec3(x, y, z);
               if (targetPos.distanceTo(target.position()) <= 6.0) {
                  if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
                     instance.addMasteryPoint(entity);
                  }

                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
                  Vec3 vec3 = targetPos.subtract(target.position()).normalize().scale(0.5);
                  if (vec3.length() > 0.2) {
                     Changeable<Vec3> changeable = Changeable.of(vec3);
                     if (((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
                        .move(target, entity, instance, changeable)
                        .isFalse()) {
                        return false;
                     }

                     target.setDeltaMovement((Vec3)changeable.get());
                  }

                  target.resetFallDistance();
                  target.hurtMarked = true;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void breakBlock(LivingEntity entity, ManasSkillInstance instance, int mode) {
      Level level = entity.level();
      if (entity instanceof Player player) {
         if (!TensuraGameRules.canSkillGrief(level)) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
         } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, player, Fluid.NONE, Block.OUTLINE, CONFIG.maxRange);
            if (result.getType() == Type.BLOCK) {
               BlockPos pos = result.getBlockPos();
               BlockState state = level.getBlockState(pos);
               if (state.is(TensuraBlockTags.SKILL_UNOBTAINABLE) || state.getBlock().defaultDestroyTime() <= -1.0F) {
                  player.displayClientMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED), true);
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
                  return;
               }

               if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                  .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                  .isFalse()) {
                  if (shouldBreakInstead(level, pos, state)) {
                     level.destroyBlock(pos, true, player);
                     state.getBlock().playerWillDestroy(level, pos, state, player);
                  } else {
                     state.getBlock().playerWillDestroy(level, pos, state, player);
                     BlockEntity blockentity = level.getBlockEntity(pos);
                     if (blockentity instanceof BeehiveBlockEntity beehiveblockentity) {
                        beehiveblockentity.emptyAllLivingFromHive(player, state, BeeReleaseStatus.EMERGENCY);
                     }

                     if (!state.is(BlockTags.CROPS) && !(blockentity instanceof ShulkerBoxBlockEntity box && !box.isEmpty())) {
                        ItemStack stack = new ItemStack(state.getBlock());
                        if (!player.addItem(stack)) {
                           player.drop(stack, false);
                        }

                        level.destroyBlock(pos, false, player);
                     } else {
                        level.destroyBlock(pos, !player.isCreative(), player);
                     }
                  }

                  instance.addMasteryPoint(entity);
                  ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                     .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
               }

               for (ItemEntity item : player.level().getEntitiesOfClass(ItemEntity.class, AABB.ofSize(Vec3.atCenterOf(pos), 2.5, 2.5, 2.5))) {
                  if (player.addItem(item.getItem())) {
                     item.discard();
                  } else {
                     item.teleportTo(player.position().x(), player.position().y(), player.position().z());
                  }
               }

               player.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
               ((ServerLevel)player.level())
                  .sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.08, 0.08, 0.08, 0.1);
            }
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      CompoundTag tag = instance.getOrCreateTag();
      tag.remove("target");
      instance.markDirty();
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      tag.remove("target");
      instance.markDirty();
   }

   public static boolean shouldBreakInstead(Level level, BlockPos pos, BlockState state) {
      if (state.is(TensuraBlockTags.MULTI_BLOCK_IGNORE)) {
         return true;
      }

      if (state.hasProperty(SlabBlock.TYPE) && state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
         return true;
      }

      for (Property<?> property : state.getProperties()) {
         if (property.getName().equals("half")) {
            return true;
         }

         if (property.getName().equals("part")) {
            return true;
         }
      }

      BlockEntity blockentity = level.getBlockEntity(pos);
      if (blockentity == null) {
         return false;
      }

      ResourceLocation location = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockentity.getType());
      return location == null ? false : !location.getNamespace().equals("minecraft") && !location.getNamespace().equals("tensura");
   }

   public static void learnMolecularManipulation(ManasSkillInstance instance, LivingEntity entity) {
      if (!SkillUtils.hasSkillPermanently(entity, (ManasSkill)ExtraSkills.MOLECULAR_MANIPULATION.get())) {
         int skills = instance.is(TensuraSkillTags.ELEMENTAL_MANIPULATION) ? 1 : 0;

         for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(entity).getLearnedSkills()) {
            if (!skill.isTemporarySkill() && skill.isMastered(entity) && skill.is(TensuraSkillTags.ELEMENTAL_MANIPULATION)) {
               skills++;
            }
         }

         if (!(skills < CONFIG.masteredManipulationAcquirement)) {
            SkillHelper.learnSkill(entity, ((MolecularManipulationSkill)ExtraSkills.MOLECULAR_MANIPULATION.get()).createLearningInstance(entity));
         }
      }
   }
}
