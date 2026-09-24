package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class DrowsinessEffect extends TensuraMobEffect implements DamageAction {
   protected static final ResourceLocation DROWSINESS = ResourceLocation.fromNamespaceAndPath("tensura", "drowsiness");

   public DrowsinessEffect() {
      super(MobEffectCategory.HARMFUL, new Color(77, 43, 176, 255).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, DROWSINESS, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DROWSINESS, -4.0, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, DROWSINESS, -0.1F, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, DROWSINESS, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, DROWSINESS, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, DROWSINESS, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, DROWSINESS, -0.25, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.DARK_VISION, DROWSINESS, 0.1, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.DROWSINESS));
         if (instance == null) {
            return true;
         }

         float damage = entity.getHealth();
         DamageSource damageSource = TensuraDamageHelper.getUUIDDamageSource(
               TensuraDamageTypes.DROWSY_DEATH, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()
            )
            .tensura$setDodgeBypass();
         entity.hurt(damageSource, damage);
      }

      return true;
   }

   @Override
   public boolean onBeingDamaged(LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.DROWSINESS));
      if (instance == null) {
         return true;
      }

      float effectLevel = instance.getAmplifier() + 1;
      amount.set((Float)amount.get() * (1.0F + 0.2F * effectLevel));
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pAmplifier >= 4 && pDuration % 5 == 0;
   }
}
