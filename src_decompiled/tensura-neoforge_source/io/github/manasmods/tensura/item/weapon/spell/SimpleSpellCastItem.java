package io.github.manasmods.tensura.item.weapon.spell;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillActivationEvent;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillReleaseEvent;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillUpdateCooldownEvent;
import io.github.manasmods.manascore.skill.impl.TickingSkill;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class SimpleSpellCastItem extends Item {
   public static final ResourceLocation BASE_CHANT_SPEED_ID = ResourceLocation.withDefaultNamespace("base_chant_speed");
   private final int staffCooldown;
   private final int magicSlot;
   private static final Map<UUID, ManasSkillInstance> ACTIVE_CASTS = new ConcurrentHashMap<>();

   public SimpleSpellCastItem(int staffCooldown, int magicSlot, Properties properties) {
      super(properties);
      this.staffCooldown = staffCooldown;
      this.magicSlot = magicSlot;
   }

   public SimpleSpellCastItem(int staffCooldown, int magicSlot, double chantSpeed, Properties pProperties) {
      this(staffCooldown, magicSlot, pProperties.attributes(createAttributes(chantSpeed)));
   }

   public SimpleSpellCastItem(int staffCooldown, int magicSlot, Tier pTier, int damage, double chantSpeed, Properties pProperties) {
      this(staffCooldown, magicSlot, pTier, damage, -3.0F, 0.5, chantSpeed, pProperties);
   }

   public SimpleSpellCastItem(int staffCooldown, int magicSlot, Tier pTier, int damage, float speed, double range, double chantSpeed, Properties pProperties) {
      this(staffCooldown, magicSlot, pProperties.attributes(createAttributes(pTier, damage, speed, range, chantSpeed)));
   }

   public static ItemAttributeModifiers createAttributes(Tier tier, int damage, float speed, double range, double chantSpeed) {
      return ItemAttributeModifiers.builder()
         .add(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(BASE_ATTACK_DAMAGE_ID, damage + tier.getAttackDamageBonus(), Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(
            Attributes.ENTITY_INTERACTION_RANGE,
            new AttributeModifier(TensuraSwordItem.BASE_ATTACK_RANGE_ID, range, Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .add(TensuraAttributes.CHANT_SPEED, new AttributeModifier(BASE_CHANT_SPEED_ID, chantSpeed, Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
         .build();
   }

   public static ItemAttributeModifiers createAttributes(double chantSpeed) {
      return ItemAttributeModifiers.builder()
         .add(TensuraAttributes.CHANT_SPEED, new AttributeModifier(BASE_CHANT_SPEED_ID, chantSpeed, Operation.ADD_VALUE), EquipmentSlotGroup.HAND)
         .build();
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
      return 72000;
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      return UseAnim.BOW;
   }

   public int getMagicSlot(ItemStack itemStack) {
      return this.magicSlot;
   }

   public static int getMagicSlots(Level level, ItemStack stack) {
      int slot = 0;
      if (stack.getItem() instanceof SimpleSpellCastItem castItem) {
         slot = castItem.getMagicSlot(stack);
      }

      return slot + TensuraEnchantmentHelper.getEnchantmentLevel(level, TensuraEnchantments.MAGIC_CAPACITY, stack);
   }

   public void renderTooltip(ItemStack stack, List<Component> lines) {
      List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
      if (skills != null && !skills.isEmpty()) {
         ResourceLocation selected = (ResourceLocation)stack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL.get(), skills.getFirst());
         ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(selected);
         lines.add(Component.empty());
         lines.add(Component.translatable("tooltip.tensura.spell_cast_item.list").withStyle(ChatFormatting.GRAY));

         for (ResourceLocation location : skills) {
            if (Objects.equals(location, selected) && manasSkill != null) {
               MutableComponent name;
               if (manasSkill instanceof TensuraSkill skill) {
                  Component modeName = skill.getModeName(
                     manasSkill.createDefaultInstance(), (Integer)stack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0)
                  );
                  name = Component.literal(" [").append(skill.getColoredName()).append(" - ").append(modeName).append("]");
               } else {
                  Component modeName = Component.translatable("tensura.skill.mode.default");
                  name = Component.literal(" [").append(manasSkill.getName()).append(" - ").append(modeName).append("]");
               }

               lines.addLast(name.withStyle(ChatFormatting.GOLD));
            } else {
               ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(location);
               if (skill != null) {
                  lines.addLast(Component.literal(" ").append(skill.getChatDisplayName(false)));
               }
            }
         }
      }
   }

   public static ManasSkillInstance getMagicInstance(Skills storage, ItemStack itemStack, ManasSkill skill, boolean createNew) {
      ManasSkillInstance magic = null;
      Optional<ManasSkillInstance> optional = storage.getSkill(skill);
      if (optional.isPresent()) {
         magic = optional.get();
         if (itemStack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag tag = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
            if (tag.contains(skill.getRegistryName().toString())) {
               tag.remove(skill.getRegistryName().toString());
               if (tag.isEmpty()) {
                  itemStack.remove(DataComponents.CUSTOM_DATA);
               } else {
                  itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
               }
            }
         }
      } else if (itemStack.has(DataComponents.CUSTOM_DATA)) {
         CompoundTag tag = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
         if (!tag.contains(skill.getRegistryName().toString())) {
            magic = skill.createDefaultInstance();
            if (magic.is(TensuraSkillTags.UNLEARNT_CAST_EXCLUDED)) {
               return null;
            }

            magic.setRemoveTime(-3);
         } else {
            magic = ManasSkillInstance.fromNBT(tag.getCompound(skill.getRegistryName().toString()));
            if (magic.is(TensuraSkillTags.UNLEARNT_CAST_EXCLUDED)) {
               return null;
            }
         }

         itemStack.set((DataComponentType)TensuraDataComponents.MISC_SWITCH.get(), true);
      } else if (createNew) {
         magic = skill.createDefaultInstance();
         if (magic.is(TensuraSkillTags.UNLEARNT_CAST_EXCLUDED)) {
            return null;
         }

         magic.setRemoveTime(-3);
         itemStack.set((DataComponentType)TensuraDataComponents.MISC_SWITCH.get(), true);
      }

      return magic;
   }

   public static void saveCreatedMagic(ItemStack itemStack, ManasSkillInstance skill, boolean stop) {
      CompoundTag tag = ((CustomData)itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
      tag.put(skill.getSkillId().toString(), skill.toNBT());
      itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
      if (stop) {
         itemStack.remove((DataComponentType)TensuraDataComponents.MISC_SWITCH.get());
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack itemStack = player.getItemInHand(interactionHand);
      if (level.isClientSide()) {
         return InteractionResultHolder.sidedSuccess(itemStack, true);
      }

      if (player.getCooldowns().isOnCooldown(this)) {
         return InteractionResultHolder.fail(itemStack);
      }

      player.startUsingItem(interactionHand);
      if (player.getTicksUsingItem() == 0) {
         List<ResourceLocation> skills = (List<ResourceLocation>)itemStack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
         if (skills == null || skills.isEmpty()) {
            return InteractionResultHolder.fail(itemStack);
         }

         ResourceLocation selected = (ResourceLocation)itemStack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL.get(), skills.getFirst());
         ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(selected);
         if (manasSkill == null) {
            return InteractionResultHolder.fail(itemStack);
         }

         int mode = (Integer)itemStack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0);
         Skills storage = SkillAPI.getSkillsFrom(player);
         ManasSkillInstance skill = getMagicInstance(storage, itemStack, manasSkill, true);
         if (skill == null) {
            return InteractionResultHolder.fail(itemStack);
         }

         boolean tempSkill = skill.getRemoveTime() == -3;
         Changeable<ManasSkillInstance> changeable = Changeable.of(skill);
         if (((SkillActivationEvent)SkillEvents.ACTIVATE_SKILL.invoker()).activateSkill(changeable, player, -1, mode).isFalse()) {
            return InteractionResultHolder.fail(itemStack);
         }

         skill = (ManasSkillInstance)changeable.get();
         if (skill == null) {
            return InteractionResultHolder.fail(itemStack);
         }

         if (!skill.canInteractSkill(player)) {
            return InteractionResultHolder.fail(itemStack);
         }

         if (mode < 0 || mode >= skill.getModes()) {
            return InteractionResultHolder.fail(itemStack);
         }

         if (skill.onCoolDown(mode) && !skill.canIgnoreCoolDown(player, mode)) {
            return InteractionResultHolder.fail(itemStack);
         }

         skill.onPressed(player, -1, mode);
         skill.addHeldAttributeModifiers(player, mode);
         if (tempSkill) {
            saveCreatedMagic(itemStack, skill, false);
         } else {
            TickingSkill.addTickingSkill(player, skill.getSkill(), mode, -1);
            storage.checkAndMarkDirty(skill);
         }
      }

      return InteractionResultHolder.consume(itemStack);
   }

   public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int i) {
      if (!level.isClientSide()) {
         if ((Boolean)stack.getOrDefault((DataComponentType)TensuraDataComponents.MISC_SWITCH.get(), false)) {
            CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
               List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
               if (skills != null && !skills.isEmpty()) {
                  ResourceLocation selected = (ResourceLocation)stack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL.get(), skills.getFirst());
                  ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(selected);
                  if (manasSkill != null) {
                     int mode = (Integer)stack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0);
                     UUID key = entity.getUUID();
                     ManasSkillInstance instance = ACTIVE_CASTS.get(key);
                     if (instance == null) {
                        CompoundTag tag = data.copyTag();
                        if (!tag.contains(manasSkill.getRegistryName().toString())) {
                           return;
                        }

                        instance = ManasSkillInstance.fromNBT(tag.getCompound(manasSkill.getRegistryName().toString()));
                        ACTIVE_CASTS.put(key, instance);
                     }

                     if (!instance.canInteractSkill(entity)) {
                        if (!instance.onCoolDown(mode) || instance.canIgnoreCoolDown(entity, mode)) {
                           instance.onRelease(entity, i, -1, mode);
                        }

                        manasSkill.removeAttributeModifiers(instance, entity, mode);
                     } else if (!instance.onHeld(entity, entity.getTicksUsingItem(), mode)) {
                        manasSkill.removeAttributeModifiers(instance, entity, mode);
                     }
                  }
               }
            }
         }
      }
   }

   public void onCastRelease(ItemStack stack, Level level, LivingEntity livingEntity) {
      if (!level.isClientSide() && !stack.isEmpty()) {
         if (livingEntity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(this)) {
               player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            } else {
               List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
               if (skills != null && !skills.isEmpty()) {
                  ResourceLocation selected = (ResourceLocation)stack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL.get(), skills.getFirst());
                  ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(selected);
                  if (manasSkill != null) {
                     Skills storage = SkillAPI.getSkillsFrom(player);
                     int mode = (Integer)stack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0);
                     ManasSkillInstance cached = ACTIVE_CASTS.remove(player.getUUID());
                     Optional<ManasSkillInstance> optional = storage.getSkill(manasSkill);
                     ManasSkillInstance skill;
                     if (cached != null) {
                        skill = cached;
                     } else if (optional.isPresent()) {
                        skill = optional.get();
                     } else {
                        if (!(Boolean)stack.getOrDefault((DataComponentType)TensuraDataComponents.MISC_SWITCH.get(), false)) {
                           player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                           return;
                        }

                        CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                        if (!tag.contains(manasSkill.getRegistryName().toString())) {
                           return;
                        }

                        skill = ManasSkillInstance.fromNBT(tag.getCompound(manasSkill.getRegistryName().toString()));
                     }

                     boolean tempSkill = skill.getRemoveTime() == -3;
                     Changeable<Integer> heldTicks = Changeable.of(livingEntity.getTicksUsingItem());
                     if (!tempSkill) {
                        int heldTick = -1;
                        if (!storage.getHeldSkills().isEmpty()) {
                           for (TickingSkill tickingSkill : List.copyOf(storage.getHeldSkills())) {
                              if (tickingSkill.matches(skill.getSkill(), mode)) {
                                 heldTick = tickingSkill.getDuration();
                                 break;
                              }
                           }
                        }

                        if (heldTick < 0) {
                           return;
                        }

                        heldTicks.set(heldTick);
                     }

                     Changeable<ManasSkillInstance> changeable = Changeable.of(skill);
                     if (!((SkillReleaseEvent)SkillEvents.RELEASE_SKILL.invoker()).releaseSkill(changeable, player, -1, mode, heldTicks).isFalse()) {
                        skill = (ManasSkillInstance)changeable.get();
                        if (skill == null) {
                           return;
                        }

                        if (skill.canInteractSkill(player) && mode < skill.getModes() && (!skill.onCoolDown(mode) || skill.canIgnoreCoolDown(player, mode))) {
                           skill.onRelease(player, (Integer)heldTicks.get(), -1, mode);
                           if (tempSkill) {
                              saveCreatedMagic(stack, skill, true);
                           } else {
                              storage.checkAndMarkDirty(skill);
                           }
                        }
                     }

                     skill.removeAttributeModifiers(player, mode);
                     if (tempSkill) {
                        saveCreatedMagic(stack, skill, true);
                     } else {
                        if (!storage.getHeldSkills().isEmpty()) {
                           for (TickingSkill tickingSkill : List.copyOf(storage.getHeldSkills())) {
                              if (tickingSkill.matches(skill.getSkill(), mode)) {
                                 storage.getHeldSkills().remove(tickingSkill);
                              }
                           }
                        }

                        storage.checkAndMarkDirty(skill);
                     }

                     player.getCooldowns().addCooldown(stack.getItem(), this.staffCooldown);
                  }
               } else {
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               }
            }
         }
      }
   }

   public void onPostMagicBinding(Player player, ItemStack stack) {
   }

   public void changeMode(Player player, ItemStack stack) {
      List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
      if (skills != null && !skills.isEmpty()) {
         ResourceLocation selected = (ResourceLocation)stack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL.get(), skills.getFirst());
         ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(selected);
         if (skill != null) {
            Skills storage = SkillAPI.getSkillsFrom(player);
            ManasSkillInstance instance = getMagicInstance(storage, stack, skill, false);
            if (instance != null) {
               if (instance.canInteractSkill(player)) {
                  int mode = (Integer)stack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0);
                  if (instance.getModes() > 1) {
                     if (skill instanceof TensuraSkill tensuraSkill) {
                        int nextMode = tensuraSkill.nextMode(player, instance, mode, false);
                        if (nextMode == -1) {
                           player.displayClientMessage(
                              Component.translatable("tensura.skill.mode.cannot_change", new Object[]{instance.getChatDisplayName(false)})
                                 .withStyle(ChatFormatting.RED),
                              true
                           );
                        } else {
                           stack.set((DataComponentType)TensuraDataComponents.MODE.get(), nextMode);
                           player.displayClientMessage(
                              Component.translatable(
                                    "tensura.skill.mode.changed",
                                    new Object[]{instance.getChatDisplayName(false), tensuraSkill.getModeName(instance, nextMode)}
                                 )
                                 .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
                              true
                           );
                        }
                     }
                  } else {
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.mode.no_mode", new Object[]{instance.getChatDisplayName(false)}).withStyle(ChatFormatting.RED),
                        true
                     );
                  }

                  player.getCooldowns().addCooldown(stack.getItem(), 3);
               }
            }
         }
      }
   }

   public void changeMagic(Player player, ItemStack stack, double delta) {
      List<ResourceLocation> skills = new ArrayList<>(
         (Collection<? extends ResourceLocation>)stack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL_LIST.get(), List.of())
      );
      if (!skills.isEmpty()) {
         ResourceLocation selected = (ResourceLocation)stack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL.get(), skills.getFirst());
         int newSkill = 0;
         int index = skills.indexOf(selected);
         if (index != -1) {
            newSkill = (int)(index + delta);
            int max = Math.min(skills.size(), getMagicSlots(player.level(), stack));
            if (newSkill >= max) {
               newSkill = 0;
            } else if (newSkill < 0) {
               newSkill = max - 1;
            }
         }

         ResourceLocation skill = skills.get(newSkill);
         Skills storage = SkillAPI.getSkillsFrom(player);
         ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(skill);
         ManasSkillInstance instance = getMagicInstance(storage, stack, manasSkill, true);
         if (instance == null) {
            stack.set((DataComponentType)TensuraDataComponents.MODE.get(), 0);
            stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), skill);
            if (manasSkill == null) {
               return;
            }

            MutableComponent name = manasSkill.getChatDisplayName(false);
            player.displayClientMessage(
               Component.translatable("tensura.skill.preset.changed_spell", new Object[]{name}).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), true
            );
         } else {
            Changeable<ManasSkillInstance> instanceChangeable = Changeable.of(instance);
            Changeable<Integer> modeChangeable = Changeable.of(0);
            if (instance.getSkill() instanceof TensuraSkill tensuraSkill
               && (
                  !tensuraSkill.canBeSlotted(instance, player, 0)
                     || !tensuraSkill.onAbilityEquipped(player, instanceChangeable, modeChangeable, Changeable.of(0), Changeable.of(0))
               )) {
               if ((Integer)modeChangeable.get() == -1) {
                  skills.removeIf(resourceLocation -> resourceLocation.equals(skill));
                  stack.set((DataComponentType)TensuraDataComponents.SKILL_LIST.get(), skills);
                  this.changeMagic(player, stack, delta);
               }

               return;
            }

            stack.set((DataComponentType)TensuraDataComponents.MODE.get(), (Integer)modeChangeable.get());
            stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), ((ManasSkillInstance)instanceChangeable.get()).getSkillId());
            MutableComponent name;
            if (manasSkill instanceof TensuraSkill tensuraSkill) {
               name = Component.literal("[")
                  .append(tensuraSkill.getColoredName())
                  .append(" - ")
                  .append(tensuraSkill.getModeName(instance, (Integer)modeChangeable.get()))
                  .append("]");
            } else if (manasSkill != null) {
               name = manasSkill.getChatDisplayName(false);
            } else {
               name = Component.translatable("tensura.skill.empty");
            }

            player.displayClientMessage(
               Component.translatable("tensura.skill.preset.changed_spell", new Object[]{name}).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)), true
            );
         }
      }
   }

   public static void tickUnlearntInstance(Level level, ItemStack stack, LivingEntity entity) {
      if (!level.isClientSide()) {
         CustomData data = (CustomData)stack.get(DataComponents.CUSTOM_DATA);
         if (data != null) {
            CompoundTag tag = data.copyTag();
            tag.getAllKeys()
               .forEach(
                  key -> {
                     CompoundTag compoundTag = tag.getCompound(key);
                     ResourceLocation skillLocation = ResourceLocation.tryParse(compoundTag.getString("skill"));
                     if (skillLocation != null) {
                        ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(skillLocation);
                        if (skill != null) {
                           ManasSkillInstance instance = skill.createDefaultInstance();
                           instance.deserialize(compoundTag);

                           for (int i = 0; i < instance.getModes(); i++) {
                              if (instance.onCoolDown(i)) {
                                 int currentCooldown = instance.getCoolDown(i);
                                 Changeable<Integer> newCooldown = Changeable.of(Math.max(0, currentCooldown - 1));
                                 if (!((SkillUpdateCooldownEvent)SkillEvents.SKILL_UPDATE_COOLDOWN.invoker())
                                    .cooldown(instance, entity, i, currentCooldown, newCooldown)
                                    .isFalse()) {
                                    instance.setCoolDown((Integer)newCooldown.get(), i);
                                 }
                              }
                           }

                           saveCreatedMagic(stack, instance, false);
                        }
                     }
                  }
               );
         }
      }
   }
}
