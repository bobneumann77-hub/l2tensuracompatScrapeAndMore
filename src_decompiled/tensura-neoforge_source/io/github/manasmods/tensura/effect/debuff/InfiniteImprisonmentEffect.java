package io.github.manasmods.tensura.effect.debuff;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.IFlying;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class InfiniteImprisonmentEffect extends TensuraMobEffect {
   protected static final ResourceLocation INFINITE = ResourceLocation.fromNamespaceAndPath("tensura", "prison");

   public InfiniteImprisonmentEffect() {
      super(MobEffectCategory.HARMFUL, new Color(112, 5, 5).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.FLYING_SPEED, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_SPEED, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.FOLLOW_RANGE, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, INFINITE, -1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT));
         if (instance == null) {
            return true;
         }

         Entity attacker = instance.tensura$hasSource() ? level.getEntity(instance.tensura$getSource()) : null;
         double MP = 500 * (pAmplifier + 1);
         EnergyHelper.drainEnergy(entity, attacker, MP, false, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NONE);
         if (entity instanceof Player player && player.getAbilities().flying) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
         } else if (entity instanceof IFlying flying && flying.isFlying()) {
            flying.setFlying(false);
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 10 == 0;
   }
}
