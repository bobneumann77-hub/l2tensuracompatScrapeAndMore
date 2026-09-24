package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class SleepEffect extends TensuraMobEffect implements DamageAction {
   public static final ResourceLocation SLEEP = ResourceLocation.fromNamespaceAndPath("tensura", "sleep");

   public SleepEffect() {
      super(MobEffectCategory.HARMFUL, new Color(0, 0, 0).getRGB());
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.FLYING_SPEED, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.FOLLOW_RANGE, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, SLEEP, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.DARK_VISION, SLEEP, 0.6F, Operation.ADD_VALUE);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
      super.addAttributeModifiers(attributeMap, 0);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity instanceof Player player && player.getAbilities().flying) {
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      } else if (entity instanceof IFlying flying && flying.isFlying()) {
         flying.setFlying(false);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 10 == 0;
   }

   @Override
   public void onPostBeingDamaged(LivingEntity entity, DamageSource source, float amount) {
      MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.SLEEP));
      if (instance != null) {
         int level = instance.getAmplifier() - 1;
         entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.SLEEP));
         if (level >= 0) {
            TensuraMobEffect.addEffect(
               entity,
               TensuraMobEffects.getReference(TensuraMobEffects.SLEEP),
               instance.getDuration(),
               level,
               instance.isAmbient(),
               instance.isVisible(),
               instance.showIcon(),
               instance.tensura$getSource(),
               instance.tensura$getSourceAbility()
            );
         }
      }
   }

   public static boolean isSleeping(LivingEntity entity) {
      AttributeInstance sleep = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      return sleep != null && sleep.hasModifier(SLEEP);
   }

   public static boolean isForcedSleeping(LivingEntity entity) {
      return ExistenceStorage.isInSleepMode(TensuraStorages.getExistenceFrom(entity)) ? true : isSleeping(entity);
   }
}
