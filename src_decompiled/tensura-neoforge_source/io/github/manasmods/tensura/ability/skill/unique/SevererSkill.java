package io.github.manasmods.tensura.ability.skill.unique;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.SevererBladeProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SevererSkill extends Skill {
   private static final UniqueSkillConfig.Severer CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Severer;
   private static final Vec3 SEVERANCE_UP = new Vec3(0.0, 1.0, 0.0);

   public SevererSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "severer.sword";
         case 1 -> "severer.blade_storm";
         case 2 -> "severer.severance";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostStorm;
         case 2 -> CONFIG.magiculeCostSeverance;
         default -> CONFIG.magiculeCostDummy;
      };
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      switch (mode) {
         case 0:
            if (entity.getMainHandItem().isEmpty()) {
               this.spawnDummySword(instance, entity, InteractionHand.MAIN_HAND, mode);
            } else if (entity.getOffhandItem().isEmpty()) {
               this.spawnDummySword(instance, entity, InteractionHand.OFF_HAND, mode);
            }
            break;
         case 1:
            ItemStack spatialBlade = null;
            Item blade = (Item)TensuraToolItems.SPATIAL_BLADE.get();
            if (entity.getItemInHand(InteractionHand.MAIN_HAND).is(blade)) {
               spatialBlade = entity.getItemInHand(InteractionHand.MAIN_HAND);
               entity.swing(InteractionHand.MAIN_HAND, true);
            } else if (entity.getItemInHand(InteractionHand.OFF_HAND).is(blade)) {
               spatialBlade = entity.getItemInHand(InteractionHand.OFF_HAND);
               entity.swing(InteractionHand.OFF_HAND, true);
            }

            if (spatialBlade == null) {
               if (entity.getMainHandItem().isEmpty()) {
                  this.spawnDummySword(instance, entity, InteractionHand.MAIN_HAND, mode);
               } else {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.item").withStyle(ChatFormatting.RED));
               }

               return;
            }

            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            MagicCircle.castMagicCircle(
               4.0F,
               20,
               MagicCircleVariant.SPACE,
               true,
               entity,
               -1.0F,
               0.0F,
               Vec3.ZERO,
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.stormCooldownMastered : CONFIG.stormCooldown, mode);
            int bladeAmount = instance.isMastered(entity) ? CONFIG.stormNumberMastered : CONFIG.stormNumber;
            this.spawnSeveranceBlade(entity, spatialBlade, instance, mode, bladeAmount);
            break;
         case 2:
            if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SEVERANCE_BLADE))) {
               return;
            }

            entity.swing(InteractionHand.MAIN_HAND, true);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
            int severance = instance.isMastered(entity) ? CONFIG.severanceLevelMastered - 1 : CONFIG.severanceLevel - 1;
            entity.addEffect(
               new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.SEVERANCE_BLADE), CONFIG.severanceDuration, severance, true, false, true)
            );
      }
   }

   private void spawnDummySword(ManasSkillInstance instance, LivingEntity entity, InteractionHand hand, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         instance.addMasteryPoint(entity);
         Item bladeItem = (Item)TensuraToolItems.SPATIAL_BLADE.get();
         ItemStack blade = new ItemStack(bladeItem);
         blade.set((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), true);
         blade.set((DataComponentType)TensuraDataComponents.SKILL.get(), instance.getSkillId());
         if (instance.isSubInstance()) {
            blade.set((DataComponentType)TensuraDataComponents.SECONDARY_SKILL.get(), instance.getParentSkill().getRegistryName());
         }

         blade.set(DataComponents.ITEM_NAME, this.getChatDisplayName(false).append(" ").append(bladeItem.getName(blade)));
         blade.enchant(
            TensuraEnchantmentHelper.getEnchantment(entity.level(), TensuraEnchantments.SEVERANCE),
            instance.isMastered(entity) ? CONFIG.engravingLevelMastered : CONFIG.engravingLevel
         );
         entity.setItemInHand(hand, blade);
         entity.swing(hand, true);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }

   private void spawnSeveranceBlade(LivingEntity entity, ItemStack stack, ManasSkillInstance instance, int mode, int arrowAmount) {
      int arrowRot = 360 / arrowAmount;
      Vec3 baseOffset = entity.getEyePosition().add(entity.getLookAngle().normalize());
      float xRotRad = -entity.getXRot() * (float) (Math.PI / 180.0);
      float yRotRad = -entity.getYRot() * (float) (Math.PI / 180.0);

      for (int i = 0; i < arrowAmount; i++) {
         Vec3 arrowOffset = SEVERANCE_UP.zRot((arrowRot * i - arrowRot / 2.0F) * (float) (Math.PI / 180.0));
         Vec3 arrowPos = baseOffset.add(arrowOffset.xRot(xRotRad).yRot(yRotRad));
         SevererBladeProjectile blade = new SevererBladeProjectile(entity.level(), entity, stack);
         blade.setSkill(instance);
         blade.setMode(mode);
         blade.setPos(arrowPos);
         blade.setOwnerOffset(arrowOffset);
         blade.setLookDistance(instance.isMastered(entity) ? 30.0F : 20.0F);
         blade.setDelayTick(20);
         blade.updateShootRotation();
         entity.level().addFreshEntity(blade);
         entity.level()
            .playSound(null, arrowPos.x(), arrowPos.y(), arrowPos.z(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }
}
