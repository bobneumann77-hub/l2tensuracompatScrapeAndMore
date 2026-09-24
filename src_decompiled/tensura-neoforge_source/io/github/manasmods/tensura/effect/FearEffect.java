package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class FearEffect extends TensuraMobEffect {
   public FearEffect() {
      super(MobEffectCategory.HARMFUL, new Color(58, 75, 82).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath("tensura", "fear"), -0.05F, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
      if (i > 0) {
         super.addAttributeModifiers(attributeMap, i);
      }
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.ABNORMAL_CONDITION_RESISTANCE.get())) {
         pAmplifier -= 2;
      }

      if (pAmplifier < 4) {
         return true;
      }

      MobEffectInstance fear = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.FEAR));
      if (fear == null) {
         return true;
      }

      if (!fear.tensura$hasSource()) {
         return true;
      }

      if (entity.level() instanceof ServerLevel level) {
         Entity source = level.getEntity(fear.tensura$getSource());
         DamageSource damageSource = TensuraDamageHelper.getUUIDDamageSource(
            TensuraDamageTypes.FEAR, level, fear.tensura$getSource(), fear.tensura$getSourceAbility()
         );
         if (source != null && entity instanceof Player target && target.distanceTo(source) < 7.0F) {
            target.hurt(damageSource, 2 * (pAmplifier - 3));
         }

         if (pAmplifier < 9) {
            return true;
         }

         float damage = 3 * (pAmplifier - 8);
         entity.hurt(damageSource, damage);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 40 == 0;
   }
}
