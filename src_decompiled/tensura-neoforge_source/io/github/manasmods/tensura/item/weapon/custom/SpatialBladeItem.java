package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.enchantment.SlottingHelper;
import io.github.manasmods.tensura.entity.projectile.SevererBladeProjectile;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ItemHelper;
import java.util.List;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SpatialBladeItem extends TensuraSwordItem {
   private final ItemAttributeModifiers hiltAttributeModifiers = TensuraSwordItem.createAttributes(Tiers.IRON, -5, -2.0F, -1.0, 0.0, 0.0, 0.0);
   private static final Component TOOLTIP_BLADE = Component.translatable("tooltip.tensura.spatial_blade.tooltip.blade").withStyle(ChatFormatting.GRAY);
   private static final Component TOOLTIP_HILT = Component.translatable("tooltip.tensura.spatial_blade.tooltip.hilt").withStyle(ChatFormatting.GRAY);

   public SpatialBladeItem() {
      super(
         TensuraToolTiers.HIGH_MAGISTEEL,
         -4,
         -2.4F,
         0.0,
         0.0,
         0.0,
         0.0,
         new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(2500).rarity(Rarity.RARE).fireResistant()
      );
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag pIsAdvanced) {
      list.add(TOOLTIP_BLADE);
      list.add(TOOLTIP_HILT);
   }

   private ItemStack getAmmoStack(ItemStack stack, Player player, InteractionHand hand) {
      if (player.hasInfiniteMaterials()) {
         return new ItemStack((ItemLike)TensuraToolItems.SEVERER_BLADE.get());
      }

      if (stack.has((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get())) {
         if (stack.has((DataComponentType)TensuraDataComponents.SKILL.get())) {
            ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get((ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.SKILL.get()));
            if (skill != null && SkillUtils.hasSkill(player, skill)) {
               return EnergyHelper.isOutOfEnergy(player, 0.0, 20.0) ? ItemStack.EMPTY : new ItemStack((ItemLike)TensuraToolItems.SEVERER_BLADE.get());
            }

            if (stack.has((DataComponentType)TensuraDataComponents.SECONDARY_SKILL.get())) {
               ManasSkill secondary = (ManasSkill)SkillAPI.getSkillRegistry()
                  .get((ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.SECONDARY_SKILL.get()));
               if (secondary != null && SkillUtils.hasSkill(player, secondary)) {
                  return EnergyHelper.isOutOfEnergy(player, 0.0, 20.0) ? ItemStack.EMPTY : new ItemStack((ItemLike)TensuraToolItems.SEVERER_BLADE.get());
               }
            }
         }

         ItemHelper.breakItem(stack, player, LivingEntity.getSlotForHand(hand));
         return ItemStack.EMPTY;
      } else {
         return this.findAmmo(player);
      }
   }

   public boolean isHiltOnly(ItemStack stack) {
      return Boolean.TRUE.equals(stack.get((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get()));
   }

   public ItemStack findAmmo(Player entity) {
      for (int i = 0; i < entity.getInventory().getContainerSize(); i++) {
         ItemStack stack = entity.getInventory().getItem(i);
         if (stack.is((Item)TensuraToolItems.SEVERER_BLADE.get())) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (SlottingHelper.getContentSize(stack) > 0 && !player.isSecondaryUseActive()) {
         return InteractionResultHolder.consume(player.getItemInHand(hand));
      }

      ItemStack ammo = this.getAmmoStack(stack, player, hand);
      boolean hilt = this.isHiltOnly(stack);
      if (hilt) {
         if (!ammo.isEmpty()) {
            ammo.shrink(1);
            stack.set((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), false);
            stack.set(
               DataComponents.ATTRIBUTE_MODIFIERS, (ItemAttributeModifiers)stack.get((DataComponentType)TensuraDataComponents.ONE_HANDED_MODIFIERS.get())
            );
            level.playSound(
               null,
               player.getX(),
               player.getY(),
               player.getZ(),
               SoundEvents.ARMOR_EQUIP_IRON,
               SoundSource.PLAYERS,
               0.5F,
               0.4F + (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
         } else {
            return super.use(level, player, hand);
         }
      } else {
         level.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(),
            TensuraSkill.ABILITY_SOUND,
            0.5F,
            0.4F + (level.getRandom().nextFloat() * 0.4F + 0.8F)
         );
         boolean left = hand == InteractionHand.OFF_HAND && player.getMainArm() == HumanoidArm.RIGHT
            || hand == InteractionHand.MAIN_HAND && player.getMainArm() == HumanoidArm.LEFT;
         SevererBladeProjectile blade = new SevererBladeProjectile(level, player, !left, stack);
         if (stack.get((DataComponentType)TensuraDataComponents.SKILL.get()) != null) {
            blade.setSkill(
               (ManasSkillInstance)SkillAPI.getSkillsFrom(player)
                  .getSkill((ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.SKILL.get()))
                  .orElse(null)
            );
         }

         Vec3 vec3 = player.getViewVector(1.0F);
         blade.shoot(vec3.x(), vec3.y(), vec3.z(), 3.0F, 0.0F);
         level.addFreshEntity(blade);
         stack.set((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), true);
         stack.set((DataComponentType)TensuraDataComponents.ONE_HANDED_MODIFIERS.get(), (ItemAttributeModifiers)stack.get(DataComponents.ATTRIBUTE_MODIFIERS));
         stack.set(DataComponents.ATTRIBUTE_MODIFIERS, this.getHiltAttributeModifiers());
         stack.hurtAndBreak(20, player, LivingEntity.getSlotForHand(hand));
         player.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
      }
   }

   @Generated
   public ItemAttributeModifiers getHiltAttributeModifiers() {
      return this.hiltAttributeModifiers;
   }
}
