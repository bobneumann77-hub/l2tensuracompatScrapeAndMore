package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class GiantificationSkill extends Skill {
   private static final IntrinsicSkillConfig.Giantification CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).Giantification;
   public static final ResourceLocation GIANTIFICATION = ResourceLocation.fromNamespaceAndPath("tensura", "giantification");

   public GiantificationSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.isMastered(entity) && !entity.isShiftKeyDown();
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      this.deactivate(entity);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.isShiftKeyDown()) {
         this.deactivate(entity);
      } else if (!instance.isMastered(entity)) {
         this.activate(entity, CONFIG.maxSize);
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (entity.isShiftKeyDown()) {
         this.deactivate(entity);
      } else if (instance.isMastered(entity)) {
         CompoundTag tag = instance.getTag();
         int size = tag != null ? tag.getInt("size") : CONFIG.maxSize;
         this.activate(entity, size);
      }
   }

   protected void activate(LivingEntity entity, double size) {
      AttributeInstance scale = entity.getAttribute(Attributes.SCALE);
      if (scale != null) {
         AttributeModifier modifier = scale.getModifier(GIANTIFICATION);
         if (modifier != null && modifier.amount() == size / 2.0) {
            return;
         }

         scale.addOrReplacePermanentModifier(new AttributeModifier(GIANTIFICATION, size / 2.0, Operation.ADD_VALUE));
      }

      AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack != null) {
         AttributeModifier armorModifier = new AttributeModifier(GIANTIFICATION, CONFIG.damage, Operation.ADD_VALUE);
         attack.addOrReplacePermanentModifier(armorModifier);
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.TRANSFORM_BERSERKER.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
         EffectStorage.setCameraShake(entity, 5.0, 0.1F, 10);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), 3.0);
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(),
            entity.getX(),
            entity.getY() + entity.getBbHeight() / 2.0F,
            entity.getZ(),
            25,
            0.08,
            0.08,
            0.08,
            0.2,
            true
         );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getGoldWave(0.9F, entity.getBbWidth() * 6.0F, 0.1F, true),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.66F,
            entity.getZ()
         );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getBlueWave(0.75F, entity.getBbWidth() * 7.0F, 0.1F, true),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.33F,
            entity.getZ()
         );
      }

      AttributeInstance step = entity.getAttribute(Attributes.STEP_HEIGHT);
      if (step != null) {
         step.addOrReplacePermanentModifier(new AttributeModifier(GIANTIFICATION, Math.max(size, 0.0) / 2.0, Operation.ADD_VALUE));
      }

      AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) {
         speed.addOrReplacePermanentModifier(new AttributeModifier(GIANTIFICATION, Math.max(size, 0.0) * 0.02, Operation.ADD_VALUE));
      }

      AttributeInstance jump = entity.getAttribute(Attributes.JUMP_STRENGTH);
      if (jump != null) {
         jump.addOrReplacePermanentModifier(new AttributeModifier(GIANTIFICATION, Math.max(size, 0.0) * 0.1, Operation.ADD_VALUE));
      }

      AttributeInstance fall = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);
      if (fall != null) {
         fall.addOrReplacePermanentModifier(new AttributeModifier(GIANTIFICATION, Math.max(size, 0.0), Operation.ADD_VALUE));
      }

      float range = CONFIG.rangeMultiplier;
      AttributeInstance entityRange = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
      if (entityRange != null) {
         entityRange.addOrReplacePermanentModifier(new AttributeModifier(GIANTIFICATION, Math.max(size * range, 0.0), Operation.ADD_VALUE));
      }

      AttributeInstance blockRange = entity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
      if (blockRange != null) {
         blockRange.addOrReplacePermanentModifier(new AttributeModifier(GIANTIFICATION, Math.max(size * range, 0.0), Operation.ADD_VALUE));
      }
   }

   protected void deactivate(LivingEntity entity) {
      AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack != null) {
         if (!attack.removeModifier(GIANTIFICATION)) {
            return;
         }

         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }

      AttributeInstance scale = entity.getAttribute(Attributes.SCALE);
      if (scale != null) {
         scale.removeModifier(GIANTIFICATION);
      }

      AttributeInstance step = entity.getAttribute(Attributes.STEP_HEIGHT);
      if (step != null) {
         step.removeModifier(GIANTIFICATION);
      }

      AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) {
         speed.removeModifier(GIANTIFICATION);
      }

      AttributeInstance jump = entity.getAttribute(Attributes.JUMP_STRENGTH);
      if (jump != null) {
         jump.removeModifier(GIANTIFICATION);
      }

      AttributeInstance fall = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);
      if (fall != null) {
         fall.removeModifier(GIANTIFICATION);
      }

      AttributeInstance entityRange = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
      if (entityRange != null) {
         entityRange.removeModifier(GIANTIFICATION);
      }

      AttributeInstance blockRange = entity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
      if (blockRange != null) {
         blockRange.removeModifier(GIANTIFICATION);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (!instance.isMastered(entity)) {
         return false;
      }

      CompoundTag tag = instance.getTag();
      int size = tag != null ? tag.getInt("size") : CONFIG.maxSize;
      if (entity instanceof Player player) {
         player.displayClientMessage(
            Component.translatable("tensura.skill.power_scale", new Object[]{size}).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
         );
      }

      return true;
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity living, double delta, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      int newScale = tag.getInt("size") + (int)delta;
      if (newScale > CONFIG.maxSize) {
         newScale = CONFIG.maxSize;
      } else if (newScale < CONFIG.minSize) {
         newScale = CONFIG.minSize;
      }

      if (tag.getInt("size") != newScale) {
         tag.putInt("size", newScale);
         instance.markDirty();
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      return attack != null && attack.getModifier(GIANTIFICATION) != null;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
   }
}
