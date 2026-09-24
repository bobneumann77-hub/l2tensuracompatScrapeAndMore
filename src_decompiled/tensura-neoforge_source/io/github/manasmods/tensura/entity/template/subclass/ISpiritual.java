package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.item.misc.BoneGolemItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ISpiritual {
   default float getPhysicalAttackInput(DamageSource damageSource) {
      return damageSource.getDirectEntity() instanceof LivingEntity living && RaceUtils.isSpiritual(living)
         ? 1.0F
         : RaceUtils.getPhysicalAttackInputMultiplier(damageSource);
   }

   default InteractionResult getGolemInteraction(Player player, InteractionHand hand, TamableAnimal animal) {
      ItemStack stack = player.getItemInHand(hand);
      if (!animal.isNoAi() && stack.getItem() instanceof BoneGolemItem item) {
         IExistence existence = TensuraStorages.getExistenceFrom(animal);
         if (existence.getSummonedSecond() > 0 || existence.isSpiritualForm()) {
            double EP = animal.getAttributeValue(TensuraAttributes.MAX_AURA) + animal.getAttributeValue(TensuraAttributes.MAX_MAGICULE);
            if (item.getVariant().getEP() <= EP) {
               stack.consume(1, player);
               existence.setPermanentOwner(player.getUUID());
               existence.setSpiritualForm(false);
               existence.setSummonedSecond(0);
               existence.setSummonedAbility(null, 0);
               existence.setSummoner(null);
               this.applyHigherStats(animal.getAttributes(), item.getVariant().getAttributes());
               animal.setHealth(animal.getMaxHealth());
               animal.level().playSound(null, animal, (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
               TensuraParticleHelper.addServerAuraParticles(
                  animal, TensuraParticleUtils.getBlackAura(1.0F, (float)animal.getAttributeValue(Attributes.SCALE) * 4.0F, -0.3F), 30, 0.01, 3.0F
               );
            } else {
               player.displayClientMessage(
                  Component.translatable("tensura.message.bone_golem.low_ep", new Object[]{animal.getName(), stack.getDisplayName()})
                     .withStyle(ChatFormatting.RED),
                  true
               );
               animal.level().playSound(null, animal, (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            player.swing(hand, true);
            return InteractionResult.sidedSuccess(animal.level().isClientSide);
         }
      }

      return InteractionResult.PASS;
   }

   default void applyHigherStats(AttributeMap original, AttributeMap copy) {
      copy.attributes.values().forEach(copyInstance -> {
         AttributeInstance instance = original.getInstance(copyInstance.getAttribute());
         if (instance != null) {
            if (!(instance.getBaseValue() > copyInstance.getBaseValue())) {
               instance.setBaseValue(copyInstance.getBaseValue());
            }
         }
      });
   }
}
