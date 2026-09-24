package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ItemHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class ShadowMotionSkill extends Skill {
   private static final ExtraSkillConfig.ShadowMotion CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).ShadowMotion;

   public ShadowMotionSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (!instance.isMastered(entity)) {
         return mode == 0 ? -1 : 0;
      } else if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> "shadow_motion.step";
         case 2 -> "shadow_motion.storage";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCost;
         case 1 -> CONFIG.magiculeCostStep;
         default -> 0.0;
      };
   }

   @Override
   protected boolean isAffectedByAbility(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.REST));
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      Level level = entity.level();
      if (mode != 0) {
         return false;
      }

      if (SkillUtils.shouldCancelTeleportation(entity)) {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
         }

         return false;
      } else {
         if (heldTicks <= 2) {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SQUID_INK, 1.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SQUID_INK, 2.0);
            if (heldTicks == 0) {
               entity.level()
                  .playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.PRESENCE_CONCEALMENT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
         }

         if (level.getMaxLocalRawBrightness(entity.blockPosition()) <= 10 || instance.isMastered(entity) || entity.hasEffect(TensuraMobEffects.SHADOW_STEP)) {
            if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return false;
            }

            if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
               instance.addMasteryPoint(entity);
            }

            if (Alignment.shouldConsumeAir(entity)) {
               entity.setAirSupply(entity.getAirSupply() - 1);
               if (entity.getAirSupply() <= -20) {
                  entity.setAirSupply(0);
                  entity.hurt(TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.SUFFOCATE), 1.0F);
               }
            }

            entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.SHADOW_STEP), 5, 0, false, false, false));
         }

         return true;
      }
   }

   private boolean canShadowStorage(LivingEntity target, LivingEntity entity) {
      if (target.getType().is(TensuraEntityTags.CLONES)) {
         return false;
      } else if (target instanceof Player) {
         return false;
      } else if (!SubordinateHelper.isSubordinate(entity, target)) {
         return false;
      } else if (ExistenceStorage.isSummon(target)) {
         return false;
      } else {
         return target.getType().is(TensuraEntityTags.MONSTER) ? true : !Alignment.shouldConsumeAir(target);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode != 0) {
         if (SkillUtils.shouldCancelTeleportation(entity)) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.spatial_blockade").withStyle(ChatFormatting.RED), true);
            }
         } else {
            Level level = entity.level();
            if (mode != 2) {
               if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.stepRange, false);
                  if (target != null && target.isAlive()) {
                     float radius = -1.5F;
                     float angle = (float) (Math.PI / 180.0) * target.yHeadRot;
                     double extraX = radius * Mth.sin((float)(Math.PI + angle));
                     double extraZ = radius * Mth.cos(angle);
                     BlockPos behindPos = new BlockPos((int)(target.getX() + extraX), (int)target.getY(), (int)(target.getZ() + extraZ));
                     if (level.getBlockState(behindPos).isSolidRender(level, behindPos)
                        && level.getBlockState(behindPos.above()).isSolidRender(level, behindPos.above())) {
                        behindPos = target.blockPosition();
                     }

                     SubordinateHelper.removeSpecificTargetInRadius(entity, 40.0, mob -> true);
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SQUID_INK, 1.0, 10);
                     entity.unRide();
                     if (entity instanceof ServerPlayer player) {
                        double d0 = target.getX() - behindPos.getX();
                        double d2 = target.getZ() - behindPos.getZ();
                        float yRot = Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * 180.0F / (float)Math.PI) - 90.0F);
                        player.teleportTo((ServerLevel)level, behindPos.getX(), behindPos.getY(), behindPos.getZ(), yRot, entity.getXRot());
                     } else {
                        entity.teleportTo(behindPos.getX(), behindPos.getY(), behindPos.getZ());
                     }

                     entity.hasImpulse = true;
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SQUID_INK, 1.0);
                     level.playSound(
                        null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                     );
                  } else {
                     entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
                  }
               }
            } else {
               LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
               if (target == null) {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
               } else if (!this.canShadowStorage(target, entity)) {
                  entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
               } else {
                  level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  ItemStack shadow = new ItemStack((ItemLike)TensuraMaterialItems.SHADOW_STORAGE.get());
                  shadow.set(
                     DataComponents.ITEM_NAME,
                     Component.translatable("tooltip.tensura.shadow_storage.name", new Object[]{target.getName()})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                  );
                  shadow.set(DataComponents.BUCKET_ENTITY_DATA, CustomData.of(target.saveWithoutId(new CompoundTag())));
                  CompoundTag compound = new CompoundTag();
                  compound.putDouble("ShadowEP", EnergyHelper.getMaxEP(target));
                  compound.putDouble("ShadowHP", target.getHealth());
                  compound.putDouble("ShadowSHP", TensuraStorages.getExistenceFrom(target).getSpiritualHealth());
                  compound.putString("EntityType", String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(target.getType())));
                  shadow.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
                  if (!(entity instanceof Player player && player.addItem(shadow))) {
                     ItemHelper.dropItem(entity, entity.getRandom(), shadow, 10, 0.5F);
                  }

                  target.discard();
               }
            }
         }
      }
   }
}
