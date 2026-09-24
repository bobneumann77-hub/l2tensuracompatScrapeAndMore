package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class FrostEffect extends TensuraMobEffect {
   private static final ResourceLocation FROST = ResourceLocation.fromNamespaceAndPath("tensura", "frost");

   public FrostEffect() {
      super(MobEffectCategory.HARMFUL, new Color(50, 107, 248).getRGB());
      this.withSoundOnAdded(SoundEvents.PLAYER_HURT_FREEZE);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, FROST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, FROST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, FROST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, FROST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, FROST, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public void onEffectStarted(LivingEntity entity, int i) {
      super.onEffectStarted(entity, i);
      entity.level().playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      effect.setLockedXRot(entity.getXRot());
      effect.setLockedYRot(entity.getYHeadRot());
      effect.markDirty();
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      WebbedEffect.lockRotation(entity);
      if (entity instanceof Player player && player.getAbilities().flying) {
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      }

      return true;
   }

   @Override
   public void onAttributeRemoved(LivingEntity entity, MobEffectInstance instance) {
      if (entity.isAlive()) {
         entity.level().playSound(null, entity.blockPosition(), SoundEvents.GLASS_BREAK, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration > 0;
   }

   public static boolean isFrozen(LivingEntity entity) {
      AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      return speed != null && speed.hasModifier(FROST);
   }
}
