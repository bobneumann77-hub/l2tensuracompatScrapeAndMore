package io.github.manasmods.tensura.effect;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.awt.Color;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class CorrosionEffect extends TensuraMobEffect {
   private static final EquipmentSlot[] EQUIPMENT_SLOTS = EquipmentSlot.values();

   public CorrosionEffect() {
      super(MobEffectCategory.HARMFUL, new Color(5, 72, 11).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (!(entity.getHealth() > 0.0F)) {
         return false;
      }

      if (entity.level() instanceof ServerLevel level) {
         MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION));
         if (instance == null) {
            return true;
         }

         boolean success = false;
         int durabilityCorrosion = 15 * (pAmplifier + 1);
         float damage = 2.0F * (pAmplifier + 1);
         DamageSource damageSource = TensuraDamageHelper.getUUIDDamageSource(
               TensuraDamageTypes.CORROSION, level, instance.tensura$getSource(), instance.tensura$getSourceAbility()
            )
            .tensura$setDodgeBypass();
         if (entity.hurt(damageSource, damage)) {
            success = true;
         }

         if (!entity.hasInfiniteMaterials()) {
            for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
               ItemStack slotStack = entity.getItemBySlot(slot);
               if (!slotStack.isEmpty()) {
                  slotStack.hurtAndBreak(durabilityCorrosion, entity, slot);
                  success = true;
               }
            }
         }

         if (success) {
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            ((ServerLevel)entity.level())
               .sendParticles(
                  TensuraParticleUtils.getAcidBubble(), entity.getX(), entity.getY() + entity.getBbHeight() / 2.0, entity.getZ(), 20, 0.08, 0.08, 0.08, 0.15
               );
         }
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }
}
