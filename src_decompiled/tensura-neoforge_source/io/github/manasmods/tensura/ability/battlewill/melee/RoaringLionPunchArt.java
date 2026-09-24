package io.github.manasmods.tensura.ability.battlewill.melee;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class RoaringLionPunchArt extends Battlewill {
   private static final BattlewillConfig.RoaringLionPunch CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).RoaringLionPunch;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!entity.getMainHandItem().isEmpty() && !entity.getMainHandItem().is(TensuraItemTags.FIST_WEAPONS)) {
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.item").withStyle(ChatFormatting.RED));
      } else {
         double reach = 3.0 + entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, reach, false);
         if (target == null) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED), true);
            }
         } else {
            double auraCost = Math.min(
               EnergyHelper.getMaxAura(entity) * CONFIG.maxAuraMultiplier, instance.isMastered(entity) ? CONFIG.maxAuraUsedMastered : CONFIG.maxAuraUsed
            );
            if (!EnergyHelper.isOutOfEnergy(entity, auraCost, 0.0)) {
               ServerLevel level = (ServerLevel)entity.level();
               float damage = (float)(entity.getAttributeValue(Attributes.ATTACK_DAMAGE) + auraCost / this.getAuraCost(entity, instance, mode));
               DamageSource source = this.createSource(instance, entity, DamageTypes.PLAYER_ATTACK, mode);
               if (target.hurt(source, damage)) {
                  EnchantmentHelper.doPostAttackEffectsWithItemSource(level, target, source, entity.getMainHandItem());
                  TensuraEnchantmentHelper.doAdditionalAfterDamage(level, target, entity, source, entity.getMainHandItem(), damage);
               }

               TensuraEnchantmentHelper.doAdditionalAfterAttack(level, target, entity, source, entity.getMainHandItem(), damage);
               SkillHelper.knockBack(entity, target, 0.015F * damage);
               instance.addMasteryPoint(entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
         }
      }
   }
}
