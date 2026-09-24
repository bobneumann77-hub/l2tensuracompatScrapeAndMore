package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.projectile.KunaiProjectile;
import io.github.manasmods.tensura.entity.projectile.SevererBladeProjectile;
import io.github.manasmods.tensura.entity.projectile.SpearProjectile;
import io.github.manasmods.tensura.entity.projectile.ThrownItemProjectile;
import io.github.manasmods.tensura.entity.projectile.WebBulletProjectile;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.item.weapon.SimpleSpearItem;
import io.github.manasmods.tensura.item.weapon.ranged.KunaiItem;
import io.github.manasmods.tensura.item.weapon.ranged.WebCartridgeItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ThrowerSkill extends Skill {
   private static final UniqueSkillConfig.Thrower CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Thrower;

   public ThrowerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         instance.addMasteryPoint(entity);
         Level level = entity.level();
         ItemStack mainHandStack = entity.getMainHandItem();
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 4.0, false);
         if (target == null) {
            Projectile projectile = getProjectile(level, entity, mainHandStack.copy(), instance);
            float speed = instance.isToggled() ? 1.0F : 3.0F;
            Vec3 vec3 = entity.getViewVector(speed);
            if (projectile instanceof AbstractArrow arrow) {
               if (entity.hasInfiniteMaterials()) {
                  arrow.pickup = Pickup.CREATIVE_ONLY;
               } else {
                  arrow.pickup = Pickup.ALLOWED;
               }
            } else if (projectile instanceof ThrowableItemProjectile itemProjectile) {
               itemProjectile.setItem(mainHandStack);
            }

            projectile.shoot(vec3.x(), vec3.y(), vec3.z(), 2.0F + (float)entity.getDeltaMovement().length() * 2.0F, 0.0F);
            level.addFreshEntity(projectile);
            entity.swing(entity.getUsedItemHand(), true);
            if (!entity.hasInfiniteMaterials()) {
               mainHandStack.shrink(1);
            }
         } else if (target.getType().is(TensuraEntityTags.NO_FORCED_MOVE) || target instanceof Player player && player.getAbilities().invulnerable) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.targeting.not_allowed").setStyle(Style.EMPTY.withColor(ChatFormatting.RED)), true);
            }

            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         } else {
            double scale = CONFIG.entityThrow;
            if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.GRAVITY_DOMINATION.get())) {
               scale += CONFIG.entityThrowDomination;
            } else if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.GRAVITY_MANIPULATION.get())) {
               scale += CONFIG.entityThrowManipulation;
            }

            entity.swing(InteractionHand.MAIN_HAND, true);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.CLOUD, 1.0);
            entity.level()
               .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            Vec3 vec3 = new Vec3(target.getX() - entity.getX(), target.getY() - entity.getY() + 0.5, target.getZ() - entity.getZ())
               .scale(1.0 / target.distanceTo(entity));
            Changeable<Vec3> changeable = Changeable.of(vec3.normalize().scale(scale));
            if (!((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
               .move(target, entity, instance, changeable)
               .isFalse()) {
               target.setDeltaMovement((Vec3)changeable.get());
               target.hasImpulse = true;
               target.hurtMarked = true;
            }
         }
      }
   }

   public static Projectile getProjectile(Level level, LivingEntity entity, ItemStack stack, @Nullable ManasSkillInstance instance) {
      if (stack.getItem() instanceof ArrowItem arrowItem) {
         AbstractArrow arrow = arrowItem.createArrow(level, stack, entity, null);
         double baseDamage = arrow.getBaseDamage()
            + (instance != null && instance.isMastered(entity) ? CONFIG.itemThrowDamageMastered : CONFIG.itemThrowDamage);
         if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.GRAVITY_DOMINATION.get())) {
            baseDamage *= CONFIG.itemThrowDomination;
         } else if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.GRAVITY_MANIPULATION.get())) {
            baseDamage *= CONFIG.itemThrowManipulation;
         }

         arrow.setBaseDamage(baseDamage);
         return arrow;
      } else {
         if (stack.getItem() instanceof ExperienceBottleItem) {
            return new ThrownExperienceBottle(level, entity);
         }

         if (stack.getItem() instanceof EggItem) {
            return new ThrownEgg(level, entity);
         }

         if (stack.getItem() instanceof EnderpearlItem) {
            return new ThrownEnderpearl(level, entity);
         }

         if (stack.getItem() instanceof WindChargeItem) {
            return entity instanceof Player player
               ? new WindCharge(player, entity.level(), entity.position().x(), entity.getEyePosition().y(), entity.position().z())
               : new WindCharge(entity.level(), entity.position().x(), entity.getEyePosition().y(), entity.position().z(), entity.getViewVector(1.0F));
         }

         if (stack.getItem() instanceof SnowballItem) {
            return new Snowball(level, entity);
         }

         if (stack.getItem() instanceof FireworkRocketItem) {
            return new FireworkRocketEntity(level, stack, entity, entity.getX(), entity.getY(), entity.getZ(), true);
         }

         if (stack.getItem() instanceof ThrowablePotionItem) {
            return new ThrownPotion(level, entity);
         }

         if (stack.getItem() instanceof WebCartridgeItem) {
            return new WebBulletProjectile(level, entity, true, stack, stack);
         }

         float baseDamage;
         if (stack.isEmpty()) {
            baseDamage = instance != null && instance.isMastered(entity) ? CONFIG.airThrowDamageMastered : CONFIG.airThrowDamage;
         } else {
            baseDamage = instance != null && instance.isMastered(entity) ? CONFIG.itemThrowDamageMastered : CONFIG.itemThrowDamage;
         }

         float multiplier = 1.0F;
         if (!stack.is(TensuraItemTags.ELEMENTAL_CORES)) {
            if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.GRAVITY_DOMINATION.get())) {
               multiplier = CONFIG.itemThrowDomination;
            } else if (SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.GRAVITY_MANIPULATION.get())) {
               multiplier = CONFIG.itemThrowManipulation;
            }

            baseDamage *= multiplier;
         }

         if (stack.getItem().equals(TensuraToolItems.SEVERER_BLADE.get())) {
            SevererBladeProjectile blade = new SevererBladeProjectile(level, entity, true, stack);
            blade.setSkill(instance);
            blade.setBaseDamage(baseDamage);
            return blade;
         }

         if (stack.getItem() instanceof TridentItem) {
            ThrownTrident trident = new ThrownTrident(level, entity, stack);
            trident.setBaseDamage(baseDamage);
            return trident;
         }

         if (stack.getItem() instanceof SimpleSpearItem) {
            SpearProjectile spear = new SpearProjectile(level, entity, stack, true);
            spear.setBaseDamage(baseDamage);
            if (instance != null && instance.isMastered(entity)) {
               spear.setLoyaltyLevel(Math.max((int)multiplier, spear.getLoyaltyLevel()));
            }

            return spear;
         } else if (stack.getItem() instanceof KunaiItem) {
            ItemStack kunai = stack.copy();
            kunai.setCount(1);
            KunaiProjectile kunaiProjectile = new KunaiProjectile(level, entity, kunai, true);
            kunaiProjectile.setBaseDamage(baseDamage);
            if (instance != null && instance.isMastered(entity)) {
               kunaiProjectile.setLoyaltyLevel(Math.max((int)multiplier, kunaiProjectile.getLoyaltyLevel()));
            }

            return kunaiProjectile;
         } else {
            ThrownItemProjectile projectile = new ThrownItemProjectile(level, entity, stack, true, baseDamage);
            projectile.getSourceItem().setCount(1);
            projectile.setSkill(instance);
            if (stack.has(DataComponents.TOOL)) {
               projectile.setMaxBrokenBlocks(CONFIG.maxBreakableBlocks);
            }

            if (instance != null) {
               if (instance.isMastered(entity)) {
                  projectile.setLoyaltyLevel((int)multiplier);
               }

               if (instance.isToggled()) {
                  Entity target = ObjectSelectionHelper.getTargetingEntity(entity, 50.0, 0.5, true, true);
                  projectile.setHomingTarget(target);
               }
            }

            return projectile;
         }
      }
   }
}
