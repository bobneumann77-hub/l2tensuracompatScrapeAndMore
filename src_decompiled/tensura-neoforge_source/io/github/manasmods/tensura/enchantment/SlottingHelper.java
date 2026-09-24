package io.github.manasmods.tensura.enchantment;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.slotting.SlottingCombination;
import io.github.manasmods.tensura.data.slotting.SlottingProjectile;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.item.misc.ElementCoreItem;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.BundleContents.Mutable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.math.Fraction;

public class SlottingHelper {
   public static final Map<ElementCoreItem, Integer> ELEMENT_INDEX_MAP = Map.of(
      (ElementCoreItem)TensuraMaterialItems.ELEMENT_CORE_EARTH.get(),
      0,
      (ElementCoreItem)TensuraMaterialItems.ELEMENT_CORE_FIRE.get(),
      1,
      (ElementCoreItem)TensuraMaterialItems.ELEMENT_CORE_SPACE.get(),
      2,
      (ElementCoreItem)TensuraMaterialItems.ELEMENT_CORE_WATER.get(),
      3,
      (ElementCoreItem)TensuraMaterialItems.ELEMENT_CORE_WIND.get(),
      4
   );

   public static int getElementalSlots(Level level, ItemStack stack) {
      return TensuraEnchantmentHelper.getEnchantmentLevel(level, TensuraEnchantments.SLOTTING, stack);
   }

   public static Iterable<ItemStack> getContents(ItemStack pStack) {
      BundleContents contents = (BundleContents)pStack.get(DataComponents.BUNDLE_CONTENTS);
      return contents == null ? List.of() : contents.itemsCopy();
   }

   public static int getContentSize(ItemStack pStack) {
      BundleContents contents = (BundleContents)pStack.get(DataComponents.BUNDLE_CONTENTS);
      return contents == null ? 0 : contents.size();
   }

   public static Optional<TooltipComponent> tooltipCore(ItemStack pStack) {
      if (getContentSize(pStack) <= 0) {
         return Optional.empty();
      } else {
         return !pStack.has(DataComponents.HIDE_TOOLTIP) && !pStack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)
            ? Optional.ofNullable((BundleContents)pStack.get(DataComponents.BUNDLE_CONTENTS)).map(BundleTooltip::new)
            : Optional.empty();
      }
   }

   public static void hurtAllCores(LivingEntity user, ItemStack itemStack, int amount, EquipmentSlot slot) {
      if (amount > 0) {
         if (!user.hasInfiniteMaterials()) {
            if (!itemStack.is(TensuraItemTags.INFINITY_ELEMENTAL_CORES)) {
               BundleContents contents = (BundleContents)itemStack.get(DataComponents.BUNDLE_CONTENTS);
               if (contents != null) {
                  List<ItemStack> copy = new ArrayList<>();
                  contents.itemsCopy().forEach(stack -> {
                     stack.hurtAndBreak(amount, user, slot);
                     if (!stack.isEmpty()) {
                        copy.add(stack);
                     }
                  });
                  itemStack.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(copy));
               }
            }
         }
      }
   }

   public static boolean putToolUponCore(ItemStack tool, Slot slot, ClickAction clickAction, Player player) {
      if (clickAction != ClickAction.SECONDARY) {
         return false;
      }

      BundleContents bundleContents = (BundleContents)tool.get(DataComponents.BUNDLE_CONTENTS);
      if (bundleContents == null) {
         bundleContents = BundleContents.EMPTY;
      }

      ItemStack slotItem = slot.getItem();
      Mutable mutable = new Mutable(bundleContents);
      if (slotItem.isEmpty()) {
         playRemoveOneSound(player);
         ItemStack removedCore = mutable.removeOne();
         if (removedCore != null) {
            ItemStack toInsert = slot.safeInsert(removedCore);
            mutable.tryInsert(toInsert);
         }
      } else if (slotItem.getItem().canFitInsideContainerItems() && slotItem.is(TensuraItemTags.ELEMENTAL_CORES)) {
         int i = tryTransfer(mutable, slot, player, getElementalSlots(player.level(), tool));
         if (i > 0) {
            playInsertSound(player);
         }
      }

      tool.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
      return true;
   }

   public static boolean putCoreOnTool(ItemStack tool, ItemStack core, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
      if (clickAction == ClickAction.SECONDARY && slot.allowModification(player)) {
         BundleContents bundleContents = (BundleContents)tool.get(DataComponents.BUNDLE_CONTENTS);
         if (bundleContents == null) {
            bundleContents = BundleContents.EMPTY;
         }

         Mutable mutable = new Mutable(bundleContents);
         if (core.isEmpty()) {
            ItemStack removedCore = mutable.removeOne();
            if (removedCore != null) {
               playRemoveOneSound(player);
               slotAccess.set(removedCore);
            }
         } else if (core.is(TensuraItemTags.ELEMENTAL_CORES)) {
            int i = tryInsert(mutable, core, getElementalSlots(player.level(), tool));
            if (i > 0) {
               playInsertSound(player);
            }
         }

         tool.set(DataComponents.BUNDLE_CONTENTS, mutable.toImmutable());
         return true;
      } else {
         return false;
      }
   }

   public static int tryInsert(Mutable mutable, ItemStack itemStack, int slots) {
      if (!itemStack.isEmpty() && itemStack.getItem().canFitInsideContainerItems()) {
         int i = Math.min(itemStack.getCount(), getMaxAmountToAdd(mutable, itemStack, slots));
         if (i == 0) {
            return 0;
         }

         mutable.weight = mutable.weight.add(getBundleWeight(itemStack).multiplyBy(Fraction.getFraction(i, 1)));
         mutable.items.addFirst(itemStack.split(i));
         return i;
      } else {
         return 0;
      }
   }

   public static int tryTransfer(Mutable mutable, Slot slot, Player player, int slots) {
      ItemStack itemStack = slot.getItem();
      int i = getMaxAmountToAdd(mutable, itemStack, slots);
      return tryInsert(mutable, slot.safeTake(itemStack.getCount(), i, player), slots);
   }

   private static int getMaxAmountToAdd(Mutable mutable, ItemStack itemStack, int slots) {
      Fraction fraction = Fraction.getFraction(slots, 1).subtract(mutable.weight);
      return Math.max(fraction.divideBy(getBundleWeight(itemStack)).intValue(), 0);
   }

   private static Fraction getBundleWeight(ItemStack itemStack) {
      BundleContents bundleContents = (BundleContents)itemStack.get(DataComponents.BUNDLE_CONTENTS);
      return bundleContents != null ? Fraction.getFraction(1, 16).add(bundleContents.weight()) : Fraction.getFraction(1, itemStack.getMaxStackSize());
   }

   private static void playRemoveOneSound(Entity pEntity) {
      pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
   }

   private static void playInsertSound(Entity pEntity) {
      pEntity.playSound(SoundEvents.END_PORTAL_FRAME_FILL, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
   }

   public static boolean onUse(LivingEntity entity, InteractionHand pHand) {
      if (entity.isShiftKeyDown()) {
         return false;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))) {
         return false;
      }

      ItemStack pStack = entity.getItemInHand(pHand);
      if (pStack.is(TensuraItemTags.SLOTTING_CAST_EXCLUDED) || getElementalSlots(entity.level(), pStack) <= 0) {
         return false;
      }

      if (getContentSize(pStack) <= 0) {
         return false;
      }

      entity.startUsingItem(pHand);
      return true;
   }

   public static boolean onRelease(ItemStack stack, LivingEntity living, int pTimeLeft) {
      if (!living.isShiftKeyDown() && !stack.is(TensuraItemTags.SLOTTING_CAST_EXCLUDED)) {
         int amount = getContentSize(stack);
         if (amount <= 0) {
            return false;
         }

         int useTicks = stack.getUseDuration(living) - pTimeLeft;
         if (useTicks < 10) {
            return false;
         }

         if (amount >= 3 && living instanceof ServerPlayer player) {
            ((PlayerTrigger)TensuraCriteriaTriggers.MAX_SLOTTING_USED.get()).trigger(player);
         }

         int[] cores = new int[5];

         for (ItemStack core : getContents(stack)) {
            Integer index = ELEMENT_INDEX_MAP.get(core.getItem());
            if (index != null) {
               cores[index]++;
            }
         }

         Registry<SlottingCombination> registry = living.level().registryAccess().registryOrThrow(TensuraCustomData.SLOTTING);
         Optional<SlottingCombination> slotting = registry.stream().filter(combinationx -> Arrays.equals(combinationx.getCores(), cores)).findFirst();
         if (slotting.isPresent()) {
            SlottingCombination combination = slotting.get();
            InteractionHand hand = living.getUsedItemHand();
            if (combination.projectile().isEmpty()) {
               hurtAllCores(living, stack, combination.projectileCost(), LivingEntity.getSlotForHand(hand));
               living.level().playSound(null, living.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
               return true;
            }

            SlottingProjectile slottingProjectile = combination.projectile().get();
            EntityType<?> entityType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(slottingProjectile.entity());
            if (entityType.create(living.level()) instanceof Projectile projectile) {
               hurtAllCores(living, stack, combination.projectileCost(), LivingEntity.getSlotForHand(hand));
               living.swing(hand);
               if (projectile instanceof TensuraFlyingProjectile flyingProjectile) {
                  flyingProjectile.setOwner(living);
                  float damage = slottingProjectile.damage();
                  damage *= TensuraDamageHelper.getWeaponDamage(
                     living, null, living.getUsedItemHand() == InteractionHand.OFF_HAND, flyingProjectile.getDamageSource()
                  );
                  flyingProjectile.setDamage(damage);
                  flyingProjectile.setSpeed(slottingProjectile.speed());
                  flyingProjectile.setKnockForce(slottingProjectile.knockback());
                  flyingProjectile.setExplosionRadius(slottingProjectile.explosion());
                  flyingProjectile.setBurnTicks(slottingProjectile.burn());
                  flyingProjectile.setNoGravity(slottingProjectile.noGravity());
                  if (slottingProjectile.effect().isPresent()) {
                     Optional<Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(slottingProjectile.effect().get().id());
                     if (effect.isPresent()) {
                        flyingProjectile.setMobEffect(
                           new MobEffectInstance((Holder)effect.get(), slottingProjectile.effect().get().ticks(), slottingProjectile.effect().get().level())
                        );
                        flyingProjectile.setEffectRange(slottingProjectile.effect().get().range());
                     }
                  }

                  flyingProjectile.setPosDirection(
                     living,
                     hand == InteractionHand.MAIN_HAND ? TensuraFlyingProjectile.PositionDirection.RIGHT : TensuraFlyingProjectile.PositionDirection.LEFT
                  );
                  flyingProjectile.shootFromRot(living.getLookAngle());
               } else {
                  Vec3 vector3f = living.getViewVector(2.0F);
                  projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), 2.2F, 0.0F);
               }

               living.level().addFreshEntity(projectile);
               living.level().playSound(null, living.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
