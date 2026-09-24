package io.github.manasmods.tensura.handler;

import com.mojang.datafixers.util.Pair;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.InteractionEvent.InteractEntity;
import dev.architectury.event.events.common.InteractionEvent.LeftClickBlock;
import dev.architectury.event.events.common.InteractionEvent.RightClickBlock;
import dev.architectury.event.events.common.InteractionEvent.RightClickItem;
import dev.architectury.event.events.common.PlayerEvent.DropItem;
import dev.architectury.event.events.common.PlayerEvent.PickupItemPredicate;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.EntityEvents.LivingEffectAddedEvent;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.data.existence.gear.GearExistenceData;
import io.github.manasmods.tensura.data.existence.gear.UniqueGearEvolutionData;
import io.github.manasmods.tensura.enchantment.SlottingHelper;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.item.armor.custom.HolyArmamentsArmorItem;
import io.github.manasmods.tensura.item.misc.PouchItem;
import io.github.manasmods.tensura.item.weapon.TwoHandedSwordItem;
import io.github.manasmods.tensura.network.c2s.RequestSpellChangePacket;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ItemHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class GearHandler {
   private static WeakReference<Registry<GearExistenceData>> cachedGearRegistryRef = new WeakReference<>(null);
   private static Map<ResourceLocation, List<GearExistenceData>> gearByItem = Map.of();

   private static Map<ResourceLocation, List<GearExistenceData>> getGearByItem(Registry<GearExistenceData> registry) {
      Registry<GearExistenceData> cached = cachedGearRegistryRef.get();
      if (cached == registry) {
         return gearByItem;
      }

      Map<ResourceLocation, List<GearExistenceData>> map = new HashMap<>();

      for (GearExistenceData data : registry) {
         map.computeIfAbsent(data.gear(), k -> new ArrayList<>()).add(data);
      }

      gearByItem = map;
      cachedGearRegistryRef = new WeakReference<>(registry);
      return map;
   }

   public static void init() {
      EntityEvents.LIVING_EFFECT_ADDED.register((LivingEffectAddedEvent)(entity, source, changeableInstance) -> {
         MobEffectInstance changeable = (MobEffectInstance)changeableInstance.get();
         if (changeable == null) {
            return EventResult.pass();
         }

         Item helmet = entity.getItemBySlot(EquipmentSlot.HEAD).getItem();
         Holder<MobEffect> effect = changeable.getEffect();
         if (helmet.equals(TensuraArmorItems.ANTI_MAGIC_MASK)) {
            if (effect.equals(MobEffects.CONFUSION) || effect.equals(MobEffects.BLINDNESS) || effect.equals(MobEffects.DARKNESS)) {
               return EventResult.interruptFalse();
            }
         } else {
            if (effect.equals(MobEffects.CONFUSION) && helmet.equals(TensuraArmorItems.ANGRY_PIERROT_MASK)) {
               return EventResult.interruptFalse();
            }

            if (effect.equals(MobEffects.BLINDNESS) && helmet.equals(TensuraArmorItems.CRAZY_PIERROT_MASK)) {
               return EventResult.interruptFalse();
            }

            if (effect.equals(MobEffects.DARKNESS) && helmet.equals(TensuraArmorItems.WONDER_PIERROT_MASK)) {
               return EventResult.interruptFalse();
            }
         }

         return EventResult.pass();
      });
      TensuraEntityEvents.EQUIPMENT_CHANGE_EVENT
         .register(
            (TensuraEntityEvents.EquipmentChangeEvent)(entity, from, to, slot) -> {
               if (to.is(TensuraItemTags.BODY_ARMOR_ITEMS)
                  && !entity.getType().is(TensuraEntityTags.CLONES)
                  && !entity.hasInfiniteMaterials()
                  && !SkillUtils.hasSkill(entity, (ManasSkill)IntrinsicSkills.BODY_ARMOR.get())) {
                  ItemHelper.breakItem(to, entity, slot);
               } else {
                  initiateGearExistence(entity.level(), to);
                  updateTsukumogami(entity, to);
                  switch (slot) {
                     case MAINHAND:
                        if (to.getItem() instanceof TwoHandedSwordItem swordItem) {
                           swordItem.setTwoHanded(to, entity.getOffhandItem().isEmpty());
                        }
                        break;
                     case OFFHAND:
                        if (entity.getMainHandItem().getItem() instanceof TwoHandedSwordItem swordItem) {
                           swordItem.setTwoHanded(entity.getMainHandItem(), to.isEmpty());
                        }
                        break;
                     default:
                        HolyArmamentsArmorItem.updateFlight(entity, from, to);
                  }
               }
            }
         );
      TensuraEntityEvents.PRE_ITEM_HURT_EVENT.register((TensuraEntityEvents.PreItemHurtEvent)(stack, owner, slot, durability) -> {
         if (owner.getActiveEffects().isEmpty()) {
            return EventResult.pass();
         }

         MobEffectInstance reinforcement = owner.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.REINFORCEMENT));
         if (reinforcement == null) {
            return EventResult.pass();
         }

         durability.set((int)(((Integer)durability.get()).intValue() * (1.0 - (reinforcement.getAmplifier() + 1) * 0.25)));
         return EventResult.pass();
      });
      InteractionEvent.LEFT_CLICK_BLOCK
         .register((LeftClickBlock)(player, hand, pos, face) -> shouldCancelPlayerInteraction(player) ? EventResult.interruptFalse() : EventResult.pass());
      InteractionEvent.RIGHT_CLICK_BLOCK.register((RightClickBlock)(player, hand, pos, face) -> {
         ItemStack stack = player.getItemInHand(hand);
         if (shouldCancelPlayerInteraction(player)) {
            return EventResult.interruptFalse();
         } else if (player.level().isClientSide() && stack.is(TensuraItemTags.SPELL_CAST_WEAPONS) && TensuraKeybinds.NEXT_ABILITY_MODE.isDown()) {
            NetworkManager.sendToServer(new RequestSpellChangePacket(1.0, true, hand));
            return EventResult.interruptFalse();
         } else {
            return EventResult.pass();
         }
      });
      InteractionEvent.RIGHT_CLICK_ITEM.register((RightClickItem)(player, hand) -> {
         ItemStack stack = player.getItemInHand(hand);
         if (shouldCancelPlayerInteraction(player)) {
            return CompoundEventResult.interruptFalse(stack);
         } else if ((Float)stack.getOrDefault((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), 0.0F) >= 0.5F) {
            return CompoundEventResult.interruptFalse(stack);
         } else if (player.level().isClientSide() && stack.is(TensuraItemTags.SPELL_CAST_WEAPONS) && TensuraKeybinds.NEXT_ABILITY_MODE.isDown()) {
            NetworkManager.sendToServer(new RequestSpellChangePacket(1.0, true, hand));
            return CompoundEventResult.interruptFalse(stack);
         } else {
            SlottingHelper.onUse(player, hand);
            return CompoundEventResult.pass();
         }
      });
      InteractionEvent.INTERACT_ENTITY
         .register((InteractEntity)(player, entity, hand) -> shouldCancelPlayerInteraction(player) ? EventResult.interruptFalse() : EventResult.pass());
      PlayerEvent.PICKUP_ITEM_PRE
         .register(
            (PickupItemPredicate)(player, entity, stack) -> {
               if (entity.hasPickUpDelay()) {
                  return EventResult.pass();
               }

               if (stack.is(TensuraItemTags.DUMMY_REMOVE_ON_LEAVING_HAND)
                  && (Boolean)stack.getOrDefault((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), false)) {
                  entity.discard();
                  return EventResult.interruptFalse();
               }

               if (stack.is(TensuraItemTags.COINS)) {
                  if (player.getOffhandItem().getItem() instanceof PouchItem pouch) {
                     if (pouch.onPickUpCoin(player, player.getOffhandItem(), entity, stack)) {
                        return EventResult.interruptFalse();
                     }
                  } else {
                     for (ItemStack itemStack : player.getInventory().items) {
                        if (itemStack.getItem() instanceof PouchItem pouch && pouch.onPickUpCoin(player, itemStack, entity, stack)) {
                           return EventResult.interruptFalse();
                        }
                     }
                  }
               }

               return EventResult.pass();
            }
         );
      PlayerEvent.DROP_ITEM
         .register(
            (DropItem)(player, entity) -> {
               if (entity.getItem().is(TensuraItemTags.DUMMY_REMOVE_ON_LEAVING_HAND)
                  && (Boolean)entity.getItem().getOrDefault((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), false)) {
                  entity.discard();
                  return EventResult.interruptFalse();
               } else {
                  return EventResult.pass();
               }
            }
         );
   }

   private static boolean shouldCancelPlayerInteraction(Player player) {
      return !player.getActiveEffects().isEmpty() && player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INFINITE_IMPRISONMENT))
         ? false
         : SkillUtils.shouldCancelInteraction(player);
   }

   public static void updateTsukumogami(LivingEntity entity, ItemStack stack) {
      if (EnchantmentHelper.hasTag(stack, TensuraTags.Enchantments.TSUKUMOGAMI)) {
         if (entity.getType().is(TensuraEntityTags.NO_TSUKUMOGAMI_UPDATE)) {
            return;
         }

         if (stack.has((DataComponentType)TensuraDataComponents.OWNER.get())) {
            if (entity.hasInfiniteMaterials()) {
               stack.set((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), 0.0F);
            } else if (entity instanceof CloneEntity clone && clone.getOwnerUUID() != null) {
               if (!Objects.equals(stack.get((DataComponentType)TensuraDataComponents.OWNER.get()), clone.getOwnerUUID().toString())) {
                  stack.set((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), 1.0F);
               }
            } else if (!Objects.equals(stack.get((DataComponentType)TensuraDataComponents.OWNER.get()), entity.getStringUUID())) {
               stack.set((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), 1.0F);
            } else {
               double weaponEP = (Double)stack.getOrDefault((DataComponentType)TensuraDataComponents.EP.get(), 0.0);
               float ratio = weaponEP <= 0.0 ? 1.0F : (float)(EnergyHelper.getMaxEP(entity) / weaponEP);
               float inactive = 1.0F - Mth.ceil(ratio * 10.0F) / 10.0F;
               stack.set((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), Math.round(inactive * 10.0F) / 10.0F);
            }
         } else if (entity.hasInfiniteMaterials()) {
            stack.set((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), 0.0F);
         } else {
            String owner = entity instanceof CloneEntity clone && clone.getOwnerUUID() != null ? clone.getOwnerUUID().toString() : entity.getStringUUID();
            stack.set((DataComponentType)TensuraDataComponents.OWNER.get(), owner);
            double weaponEP = (Double)stack.getOrDefault((DataComponentType)TensuraDataComponents.EP.get(), 0.0);
            float ratio = weaponEP <= 0.0 ? 1.0F : (float)(EnergyHelper.getMaxEP(entity) / weaponEP);
            float inactive = 1.0F - Mth.ceil(ratio * 10.0F) / 10.0F;
            stack.set((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), Math.round(inactive * 10.0F) / 10.0F);
         }
      }
   }

   public static void initiateGearExistence(Level level, ItemStack stack) {
      if (!stack.isStackable()) {
         if (!stack.has((DataComponentType)TensuraDataComponents.MAX_EP.get())) {
            stack.set((DataComponentType)TensuraDataComponents.MAX_EP.get(), 0.0);
            Registry<GearExistenceData> registry = level.registryAccess().registryOrThrow(TensuraCustomData.GEAR_EXISTENCE);
            ResourceLocation itemId = stack.getItem().arch$registryName();
            List<GearExistenceData> matches = getGearByItem(registry).get(itemId);
            if (matches != null) {
               for (GearExistenceData data : matches) {
                  if (data.engravings().isPresent()) {
                     for (Entry<Holder<Enchantment>, Integer> entry : data.engravings().get().entrySet()) {
                        stack.enchant(entry.getKey(), entry.getValue());
                     }
                  }

                  if (data.maxEP() > 0) {
                     stack.set((DataComponentType)TensuraDataComponents.MAX_EP.get(), (double)data.maxEP());
                     stack.set((DataComponentType)TensuraDataComponents.EP.get(), (double)data.minEP());
                     stack.set((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), (double)data.minEP());
                     stack.set((DataComponentType)TensuraDataComponents.EP_GAIN.get(), data.epGain());
                     if (data.evolution().isPresent()) {
                        stack.set((DataComponentType)TensuraDataComponents.EVOLUTION.get(), data.evolution().get());
                     }

                     if (data.uniqueEvolution().isPresent()) {
                        stack.set((DataComponentType)TensuraDataComponents.UNIQUE_EVOLUTIONS.get(), data.uniqueEvolution().get());
                     }
                  }
               }
            }
         }
      }
   }

   public static void initiateGearEvolution(Level level, ItemStack stack) {
      if (!stack.isStackable()) {
         Registry<GearExistenceData> registry = level.registryAccess().registryOrThrow(TensuraCustomData.GEAR_EXISTENCE);
         ResourceLocation itemId = stack.getItem().arch$registryName();
         List<GearExistenceData> matches = getGearByItem(registry).get(itemId);
         if (matches != null) {
            for (GearExistenceData data : matches) {
               if (data.engravings().isPresent()) {
                  for (Entry<Holder<Enchantment>, Integer> entry : data.engravings().get().entrySet()) {
                     stack.enchant(entry.getKey(), entry.getValue());
                  }
               }

               if (data.maxEP() > 0) {
                  stack.set((DataComponentType)TensuraDataComponents.MAX_EP.get(), (double)data.maxEP());
                  stack.set((DataComponentType)TensuraDataComponents.EP_GAIN.get(), data.epGain());
                  if (data.evolution().isPresent()) {
                     stack.set((DataComponentType)TensuraDataComponents.EVOLUTION.get(), data.evolution().get());
                  }

                  if (data.uniqueEvolution().isPresent()) {
                     stack.set((DataComponentType)TensuraDataComponents.UNIQUE_EVOLUTIONS.get(), data.uniqueEvolution().get());
                  }
               }
            }
         }
      }
   }

   public static void applyUniqueGearEvolution(ItemStack stack, double newEP) {
      List<UniqueGearEvolutionData> evolutionList = (List<UniqueGearEvolutionData>)stack.get((DataComponentType)TensuraDataComponents.UNIQUE_EVOLUTIONS.get());
      if (evolutionList != null && !evolutionList.isEmpty()) {
         evolutionList = new ArrayList<>(evolutionList);
         UniqueGearEvolutionData evolutionData = evolutionList.stream()
            .filter(data -> newEP >= data.EP())
            .min(Comparator.comparingDouble(UniqueGearEvolutionData::EP))
            .orElse(null);
         if (evolutionData != null) {
            ItemAttributeModifiers currentModifiers = (ItemAttributeModifiers)stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (currentModifiers == null || currentModifiers.modifiers().isEmpty()) {
               currentModifiers = stack.getItem().getDefaultAttributeModifiers();
            }

            if (!currentModifiers.modifiers().isEmpty()) {
               Builder modifierBuilder = getEvolvedAttributeModifiers(currentModifiers, evolutionData);
               ItemAttributeModifiers oneHandedModifiers = (ItemAttributeModifiers)stack.get(
                  (DataComponentType)TensuraDataComponents.ONE_HANDED_MODIFIERS.get()
               );
               if (oneHandedModifiers != null) {
                  stack.set(
                     (DataComponentType)TensuraDataComponents.ONE_HANDED_MODIFIERS.get(),
                     getEvolvedAttributeModifiers(oneHandedModifiers, evolutionData).build()
                  );
               }

               stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifierBuilder.build());
               if (stack.getComponents().has((DataComponentType)TensuraDataComponents.TWO_HANDED_MODIFIERS.get())) {
                  stack.set((DataComponentType)TensuraDataComponents.TWO_HANDED_MODIFIERS.get(), modifierBuilder.build());
               }

               evolutionList.remove(evolutionData);
               stack.set((DataComponentType)TensuraDataComponents.UNIQUE_EVOLUTIONS.get(), evolutionList);
            }
         }
      }
   }

   public static Builder getEvolvedAttributeModifiers(ItemAttributeModifiers currentModifiers, UniqueGearEvolutionData data) {
      Map<Pair<Holder<Attribute>, EquipmentSlotGroup>, Double> evolutionAttributeMap = data.attributes()
         .stream()
         .collect(Collectors.toMap(entryx -> Pair.of(entryx.attribute(), entryx.slot()), UniqueGearEvolutionData.Entry::amount));
      Builder modifierBuilder = ItemAttributeModifiers.builder();

      for (net.minecraft.world.item.component.ItemAttributeModifiers.Entry entry : currentModifiers.modifiers()) {
         Pair<Holder<Attribute>, EquipmentSlotGroup> attributeKey = Pair.of(entry.attribute(), entry.slot());
         AttributeModifier modifier = entry.modifier();
         if (evolutionAttributeMap.containsKey(attributeKey) && modifier.operation() == Operation.ADD_VALUE) {
            double evolutionAmount = evolutionAttributeMap.get(attributeKey);
            double newValue = modifier.amount() + evolutionAmount;
            AttributeModifier newModifier = new AttributeModifier(modifier.id(), newValue, modifier.operation());
            modifierBuilder.add(entry.attribute(), newModifier, entry.slot());
         } else {
            modifierBuilder.add(entry.attribute(), modifier, entry.slot());
         }
      }

      return modifierBuilder;
   }
}
