package io.github.manasmods.tensura.ability.magic.summon;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class SummoningMagic<T extends Mob> extends Magic implements ISummoning<T> {
   public SummoningMagic() {
      super(Magic.MagicType.SUMMONING);
   }

   @Nullable
   @Override
   public MutableComponent getColoredName() {
      MutableComponent name = super.getName();
      return name == null ? null : name.withStyle(this.getType().getChatFormatting());
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masterySummoning;
   }

   @Override
   public boolean isCastingBlocked(ManasSkillInstance instance, LivingEntity entity) {
      if (super.isCastingBlocked(instance, entity)) {
         return true;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_MAGIC))) {
         if (instance.isMastered(entity) && entity.getAttributeValue(TensuraAttributes.LAW_DEGRADATION) > 0.0) {
            return false;
         }

         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.anti_magic").withStyle(ChatFormatting.RED), true);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.isShiftKeyDown();
   }

   @Override
   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!instance.onCoolDown(mode)) {
         super.addHeldAttributeModifiers(instance, entity, mode);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode != -1) {
         if (entity.isShiftKeyDown() && this.canRemoveSummon(instance, entity, mode)) {
            this.removeExistingSummon(instance, entity, mode);
         } else {
            this.startSummoning(instance, entity, mode);
         }
      }
   }

   @Override
   public boolean isSummoningDisabled(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == -1 || instance.onCoolDown(mode) || entity.isShiftKeyDown() && this.canRemoveSummon(instance, entity, mode);
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (this.isSummoningDisabled(instance, entity, mode)) {
         this.removeFailedSummon(instance, entity, mode);
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      int castTime = this.getCastingTime(instance, entity);
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      Vec3 vec3 = new Vec3(tag.getDouble("circleX"), tag.getDouble("circleY"), tag.getDouble("circleZ"));
      BlockPos pos = ObjectSelectionHelper.getBlockPos(vec3);
      if (level.getBlockState(pos.below()).isSolid() && level.getBlockState(pos.below(2)).isSolid()) {
         this.summonMagicCircle(instance, entity, vec3, heldTicks, mode);
         if (heldTicks >= castTime) {
            if (heldTicks == castTime + 1) {
               this.createSummon(instance, entity, mode, vec3, "SummonUUID");
            }

            int summoningTime = heldTicks - castTime;
            if (tag.hasUUID("SummonUUID")) {
               this.callForthSummon(instance, entity, mode, tag.getUUID("SummonUUID"), summoningTime);
            }

            this.applyCastingVisual(instance, entity, heldTicks, mode);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), this.getSummoningSound(instance, mode), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            return summoningTime < 40;
         } else {
            this.applyCastingVisual(instance, entity, heldTicks, mode);
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), this.getSummoningSound(instance, mode), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onPostSummon(ManasSkillInstance instance, LivingEntity entity, T summon, int mode) {
      this.removeAttributeModifiers(instance, entity, mode);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      this.removeFailedSummon(instance, entity, mode);
   }

   @Override
   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity subordinate, DamageSource source) {
      this.onSummonDeath(instance, subordinate);
   }
}
