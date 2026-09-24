package io.github.manasmods.tensura.ability.skill.intrinsic;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.beam.BeamProjectile;
import io.github.manasmods.tensura.entity.magic.field.cloud.BloodMistCloud;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class BloodMistSkill extends Skill {
   private static final IntrinsicSkillConfig.BloodMist CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).BloodMist;

   public BloodMistSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public boolean canActivateSkill(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (TensuraStorages.getExistenceFrom(entity).isSpiritualForm()) {
         entity.sendSystemMessage(
            Component.translatable("tensura.ability.activation_failed.status", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
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
         return false;
      } else {
         return super.canActivateSkill(instance, entity, mode);
      }
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (mode == 0) {
         if (!instance.isMastered(entity)) {
            return -1;
         } else {
            return EnergyHelper.getMaxEP(entity) < CONFIG.rayAcquirement ? -1 : 1;
         }
      } else {
         return 0;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "default";
         case 1 -> "blood_mist.ray";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.rayMagiculeCost : CONFIG.magiculeCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 1) {
         return false;
      }

      if (heldTicks % 10 == 0) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         if (!entity.hasInfiniteMaterials()) {
            entity.hurt(TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.BLOOD_DRAIN), CONFIG.rayHPCost);
         }
      }

      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      BeamProjectile.spawnLastingBeam(
         (EntityType<? extends BeamProjectile>)MiscEntityTypes.BLOOD_RAY.get(),
         CONFIG.rayDamage,
         0.2F,
         21,
         CONFIG.rayRange,
         0.0F,
         entity.getEyePosition(),
         entity,
         instance,
         mode,
         cost,
         cost,
         heldTicks
      );
      if (heldTicks % 5 == 0) {
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BLOOD_RAY.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }

      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      if (mode == 1) {
         tag.putInt("BeamID", 0);
         instance.markDirty();
      } else {
         if (tag.contains("mistY")) {
            AABB aabb = new AABB(new BlockPos((int)tag.getDouble("mistX"), (int)tag.getDouble("mistY"), (int)tag.getDouble("mistZ"))).inflate(0.5);
            List<BloodMistCloud> list = level.getEntitiesOfClass(BloodMistCloud.class, aabb, mistx -> mistx.getOwner() == entity);
            if (!list.isEmpty()) {
               entity.swing(InteractionHand.MAIN_HAND, true);

               for (BloodMistCloud mist : list) {
                  mist.bloodExplosion();
               }

               tag.remove("mistX");
               tag.remove("mistY");
               tag.remove("mistZ");
               instance.markDirty();
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.mistCooldown / 2 : CONFIG.mistCooldown, mode);
               return;
            }
         }

         BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, CONFIG.mistRange);
         BlockPos pos = result.getBlockPos();
         instance.addMasteryPoint(entity);
         BloodMistCloud mist = new BloodMistCloud(entity.level(), entity);
         mist.setDamage(CONFIG.mistDamage);
         mist.setSize(CONFIG.mistRadius);
         mist.setHeight(1.0F);
         mist.setSkill(entity, instance, this, mode);
         mist.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
         level.addFreshEntity(mist);
         entity.hurt(TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.BLOOD_DRAIN), 20.0F);
         entity.swing(InteractionHand.MAIN_HAND, true);
         tag.putDouble("mistX", mist.getX());
         tag.putDouble("mistY", mist.getY());
         tag.putDouble("mistZ", mist.getZ());
         instance.markDirty();
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.MIST_RELEASE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
      }
   }
}
