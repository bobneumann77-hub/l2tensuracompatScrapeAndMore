package io.github.manasmods.tensura.ability.battlewill.melee;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.AuraSlashProjectile;
import io.github.manasmods.tensura.registry.battlewill.MeleeArts;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AuraSlashArt extends Battlewill {
   private static final BattlewillConfig.AuraSlash CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).AuraSlash;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   private boolean canSlash(ItemStack stack) {
      if (stack.is(ItemTags.SWORDS)) {
         return true;
      } else {
         return stack.is(ItemTags.SWORD_ENCHANTABLE) ? true : stack.is(ItemTags.AXES);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      boolean success = false;
      if (this.canSlash(entity.getMainHandItem())) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         success = true;
         AuraSlashProjectile slash = new AuraSlashProjectile(entity.level(), entity);
         slash.setSize(2.0F);
         slash.setSpeed(1.5F);
         float damage = TensuraDamageHelper.getMainWeaponDamage(entity, null, slash.getDamageSource(1.0F));
         slash.setDamage(instance.isMastered(entity) ? damage * CONFIG.attackMultiplierMastered : damage * CONFIG.attackMultiplier);
         slash.setSkill(entity, instance, this, mode);
         slash.setNoGravity(true);
         slash.setPosDirection(entity, TensuraFlyingProjectile.PositionDirection.RIGHT);
         slash.shootFromRot(entity.getLookAngle());
         entity.level().addFreshEntity(slash);
         entity.swing(InteractionHand.MAIN_HAND, true);
      }

      if ((!success || instance.isMastered(entity)) && this.canSlash(entity.getOffhandItem())) {
         if (!success && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         success = true;
         AuraSlashProjectile offSlash = new AuraSlashProjectile(entity.level(), entity);
         offSlash.setSize(2.0F);
         offSlash.setSpeed(1.5F);
         float offDamage = TensuraDamageHelper.getOffWeaponDamage(entity, null, offSlash.getDamageSource(1.0F));
         offSlash.setDamage(instance.isMastered(entity) ? offDamage * CONFIG.attackMultiplierMastered : offDamage * CONFIG.attackMultiplier);
         offSlash.setSkill(entity, instance, this, mode);
         offSlash.setNoGravity(true);
         offSlash.setPosDirection(entity, TensuraFlyingProjectile.PositionDirection.LEFT);
         offSlash.shootFromRot(entity.getLookAngle());
         entity.level().addFreshEntity(offSlash);
         entity.swing(InteractionHand.OFF_HAND, true);
      }

      if (success) {
         instance.addMasteryPoint(entity);
         instance.setCoolDown(instance.isMastered(entity) ? 1 : 2, mode);
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TRIDENT_THROW, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      } else {
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.item").withStyle(ChatFormatting.RED));
      }
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((HeavySlashArt)MeleeArts.HEAVY_SLASH.get()).createLearningInstance(entity));
      }
   }
}
