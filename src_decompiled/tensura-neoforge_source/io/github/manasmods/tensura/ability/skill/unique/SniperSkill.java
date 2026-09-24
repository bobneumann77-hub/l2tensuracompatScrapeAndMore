package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.entity.projectile.magic.SniperGrenadeProjectile;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SniperSkill extends Skill {
   public static final UniqueSkillConfig.Sniper CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Sniper;
   protected static final ResourceLocation SNIPER = ResourceLocation.fromNamespaceAndPath("tensura", "sniper");

   public SniperSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "sniper.weapon";
         case 1 -> "sniper.spatial";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCostWeapon;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (EnergyHelper.isOutOfEnergy(entity, instance, 0, 0.25F)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_magicule.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
         instance.onToggleOff(entity);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPresenceSense(entity, CONFIG.presenceSense);
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.SPACE_BOOST, CONFIG.manipulationBoost);
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.RESISTANCE_DEGRADATION);
      if (degrade != null) {
         degrade.addOrReplacePermanentModifier(new AttributeModifier(SNIPER, 1.0, Operation.ADD_VALUE));
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(SNIPER, CONFIG.meleeDodge, Operation.ADD_VALUE));
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(SNIPER, CONFIG.projectileDodge, Operation.ADD_VALUE));
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.addOrReplacePermanentModifier(new AttributeModifier(SNIPER, CONFIG.dodgeNegation, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removePresenceSense(entity, CONFIG.presenceSense);
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.SPACE_BOOST, CONFIG.manipulationBoost);
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.RESISTANCE_DEGRADATION);
      if (degrade != null) {
         degrade.removeModifier(SNIPER);
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(SNIPER);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(SNIPER);
      }

      AttributeInstance negate = entity.getAttribute(TensuraAttributes.DODGE_NEGATE_CHANCE);
      if (negate != null) {
         negate.removeModifier(SNIPER);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      if (mode == 1) {
         AttributeInstance warpShot = entity.getAttribute(TensuraAttributes.WARP_SHOT);
         if (warpShot != null) {
            if (warpShot.getModifier(SNIPER) != null) {
               AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.WARP_SHOT, SNIPER, CONFIG.warpShot);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else if (AttributeHelper.addPermanentAttributeIfHigher(entity, TensuraAttributes.WARP_SHOT, SNIPER, CONFIG.warpShot, Operation.ADD_VALUE)) {
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
         }
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         Item pistol = (Item)TensuraToolItems.WALTHER_P99.get();
         boolean hadPistol = entity.getOffhandItem().is(pistol) || entity.getMainHandItem().is(pistol);
         if (!hadPistol && ObjectSelectionHelper.getTargetingEntity(entity, 30.0, 0.0, false, false) == null) {
            if (entity.getMainHandItem().isEmpty()) {
               if (entity instanceof Player player) {
                  player.getCooldowns().addCooldown(pistol, CONFIG.physicalCooldown);
               }

               entity.setItemInHand(InteractionHand.MAIN_HAND, this.createWeapon(pistol, instance));
               entity.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            } else if (entity.getOffhandItem().isEmpty()) {
               if (entity instanceof Player player) {
                  player.getCooldowns().addCooldown(pistol, 10);
               }

               entity.setItemInHand(InteractionHand.OFF_HAND, this.createWeapon(pistol, instance));
               entity.swing(InteractionHand.OFF_HAND, true);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }
         } else {
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.grenadeCooldownMastered : CONFIG.grenadeCooldown, mode);
            SniperGrenadeProjectile grenade = new SniperGrenadeProjectile(entity.level(), entity);
            grenade.setSpeed(2.0F);
            grenade.setExplosionRadius(CONFIG.grenadeExplosion);
            grenade.setSkill(entity, instance, this, mode);
            grenade.setPosAndShoot(entity);
            level.addFreshEntity(grenade);
            entity.swing(InteractionHand.MAIN_HAND, true);
         }
      }
   }

   private ItemStack createWeapon(Item item, ManasSkillInstance instance) {
      ItemStack weapon = new ItemStack(item);
      weapon.set((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), true);
      weapon.set(
         (DataComponentType)TensuraDataComponents.SKILL.get(), instance.isSubInstance() ? instance.getParentSkill().getRegistryName() : instance.getSkillId()
      );
      weapon.set((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), false);
      weapon.set(DataComponents.ITEM_NAME, this.getChatDisplayName(false).append(" ").append(item.getName(weapon)));
      return weapon;
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      AttributeInstance attribute = entity.getAttribute(TensuraAttributes.WARP_SHOT);
      if (attribute != null) {
         if (attribute.getModifier(SNIPER) != null) {
            attribute.removeModifier(SNIPER);
         }
      }
   }
}
