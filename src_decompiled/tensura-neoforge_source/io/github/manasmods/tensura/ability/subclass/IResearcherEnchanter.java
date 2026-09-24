package io.github.manasmods.tensura.ability.subclass;

import com.mojang.serialization.DataResult;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.menu.ResearcherEnchantingMenu;
import io.github.manasmods.tensura.network.s2c.OpenResearcherEnchantingMenuPayload;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;

public interface IResearcherEnchanter {
   String STORED_ENCHANTMENTS = "StoredEnchantments";
   String SELECTED_ENCHANTMENTS = "SelectedEnchantments";
   String NEW_ENCHANTMENTS = "NewEnchantments";

   int getMaximumBonusLevel();

   List<String> getBlacklistEnchantments();

   List<String> getBlackListBonusLevelEnchantments();

   default float getCurseChancePerEngraving() {
      return 0.05F;
   }

   default void openEnchantingMenu(ServerPlayer player, ManasSkillInstance instance) {
      this.openEnchantingMenu(player, player, instance);
   }

   default void openEnchantingMenu(ServerPlayer player, LivingEntity owner, ManasSkillInstance instance) {
      player.nextContainerCounter();
      ManasSkill skill = instance.getSkill();
      NetworkManager.sendToPlayer(player, new OpenResearcherEnchantingMenuPayload(player.containerCounter, owner.getId(), skill.getRegistryName()));
      player.containerMenu = new ResearcherEnchantingMenu(player.containerCounter, player.getInventory(), owner, skill);
      player.initMenu(player.containerMenu);
      ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
   }

   default boolean isAllowedToCopyEnchantments(LivingEntity entity, ItemStack stack) {
      return stack.is(Items.ENCHANTED_BOOK);
   }

   default int getMaxLevel(Holder<Enchantment> enchantment) {
      String location = enchantment.getRegisteredName();
      if (this.getBlacklistEnchantments().contains(location)) {
         return 0;
      } else {
         return this.getBlackListBonusLevelEnchantments().contains(location)
            ? ((Enchantment)enchantment.value()).getMaxLevel()
            : ((Enchantment)enchantment.value()).getMaxLevel() + this.getMaximumBonusLevel();
      }
   }

   static CompoundTag getEnchantmentsTag(ItemEnchantments enchantments, Provider registries) {
      DataResult<Tag> result = ItemEnchantments.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), enchantments);
      if (result.result().isPresent()) {
         Tag tag = (Tag)result.result().get();
         if (tag instanceof CompoundTag compound) {
            return compound;
         }
      }

      return new CompoundTag();
   }

   static ItemEnchantments getItemEnchantments(CompoundTag compound, Provider registries) {
      DataResult<ItemEnchantments> result = ItemEnchantments.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), compound);
      return result.result().isPresent() ? (ItemEnchantments)result.result().get() : ItemEnchantments.EMPTY;
   }

   static List<Holder<Enchantment>> getSortedEnchantmentList(ItemEnchantments enchantments) {
      return getSortedEnchantmentList(enchantments, enchant -> true);
   }

   static List<Holder<Enchantment>> getSortedEnchantmentList(ItemEnchantments enchantments, Predicate<Holder<Enchantment>> predicate) {
      return enchantments.keySet()
         .stream()
         .filter(predicate)
         .sorted(Comparator.comparing(enchantment -> enchantment.unwrapKey().map(resourceKey -> resourceKey.location().getPath()).orElse("[unregistered]")))
         .toList();
   }

   static ItemEnchantments getAllEnchantments(LivingEntity entity, ManasSkill skill) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(skill);
      return instance.isEmpty() ? ItemEnchantments.EMPTY : getAllEnchantments(entity, instance.get());
   }

   static ItemEnchantments getAllEnchantments(LivingEntity entity, ManasSkillInstance instance) {
      CompoundTag tag = instance.getTag();
      return tag != null && tag.contains("StoredEnchantments")
         ? getItemEnchantments((CompoundTag)tag.get("StoredEnchantments"), entity.registryAccess())
         : ItemEnchantments.EMPTY;
   }

   static boolean addEnchantments(LivingEntity entity, ItemEnchantments map, ManasSkill skill) {
      return map == null ? false : addEnchantments(entity, new Mutable(map), skill);
   }

   static boolean addEnchantments(LivingEntity entity, Mutable oldMutable, ManasSkill skill) {
      if (!(skill instanceof IResearcherEnchanter enchanter)) {
         return false;
      } else {
         Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(skill);
         if (instance.isEmpty()) {
            return false;
         }

         boolean success = false;
         Mutable mutable = new Mutable(getAllEnchantments(entity, skill));

         for (Holder<Enchantment> enchantment : oldMutable.keySet()) {
            if (!enchanter.getBlacklistEnchantments().contains(enchantment.getRegisteredName())) {
               int oldLevel = oldMutable.getLevel(enchantment);
               int level = Math.min(oldLevel, entity.hasInfiniteMaterials() ? 255 : enchanter.getMaxLevel(enchantment));
               if (level > 0) {
                  int existedLevel = mutable.getLevel(enchantment);
                  if (existedLevel < level) {
                     if (oldLevel == level || !enchanter.getBlackListBonusLevelEnchantments().contains(enchantment.getRegisteredName())) {
                        oldMutable.removeIf(enchantmentHolder -> enchantmentHolder.equals(enchantment));
                     }

                     mutable.set(enchantment, level);
                     success = true;
                  }
               }
            }
         }

         if (success) {
            CompoundTag tag = getEnchantmentsTag(mutable.toImmutable(), entity.registryAccess());
            if (tag.isEmpty()) {
               return false;
            }

            ManasSkillInstance researcher = instance.get();
            researcher.getOrCreateTag().put("StoredEnchantments", tag);
            researcher.markDirty();
            SkillAPI.getSkillsFrom(entity).markDirty();
         }

         return success;
      }
   }

   static ItemEnchantments getSelectedEnchantments(LivingEntity entity, ManasSkill skill) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(skill);
      if (instance.isEmpty()) {
         return ItemEnchantments.EMPTY;
      }

      ManasSkillInstance researcher = instance.get();
      CompoundTag tag = researcher.getOrCreateTag();
      return !tag.contains("SelectedEnchantments")
         ? ItemEnchantments.EMPTY
         : getItemEnchantments((CompoundTag)tag.get("SelectedEnchantments"), entity.registryAccess());
   }

   static ItemEnchantments getNewEnchantments(LivingEntity entity, ManasSkill skill) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(skill);
      if (instance.isEmpty()) {
         return ItemEnchantments.EMPTY;
      }

      ManasSkillInstance researcher = instance.get();
      CompoundTag tag = researcher.getOrCreateTag();
      return !tag.contains("NewEnchantments") ? ItemEnchantments.EMPTY : getItemEnchantments((CompoundTag)tag.get("NewEnchantments"), entity.registryAccess());
   }

   static void setSelectedEnchantment(LivingEntity entity, ItemEnchantments originalMap, Holder<Enchantment> enchantment, int level, ManasSkill skill) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(skill);
      if (!instance.isEmpty()) {
         ManasSkillInstance researcher = instance.get();
         Mutable newMutable = new Mutable(getNewEnchantments(entity, skill));
         Mutable mutable = new Mutable(getSelectedEnchantments(entity, skill));
         if (level <= 0) {
            mutable.removeIf(holder -> holder == enchantment);
         } else {
            if (originalMap.getLevel(enchantment) < level) {
               newMutable.set(enchantment, level);
            }

            mutable.set(enchantment, level);
         }

         CompoundTag tag = getEnchantmentsTag(mutable.toImmutable(), entity.registryAccess());
         if (!tag.isEmpty()) {
            researcher.getOrCreateTag().put("SelectedEnchantments", tag);
            CompoundTag newTag = getEnchantmentsTag(newMutable.toImmutable(), entity.registryAccess());
            if (!newTag.isEmpty()) {
               researcher.getOrCreateTag().put("NewEnchantments", tag);
            }

            researcher.markDirty();
            SkillAPI.getSkillsFrom(entity).markDirty();
         }
      }
   }

   static void addSelectedEnchantments(LivingEntity entity, ItemEnchantments map, ManasSkill skill, boolean clear) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(skill);
      if (!instance.isEmpty()) {
         if (clear) {
            CompoundTag tag = instance.get().getTag();
            if (tag != null && tag.contains("SelectedEnchantments")) {
               tag.remove("SelectedEnchantments");
            }
         }

         if (map != null && !map.isEmpty()) {
            Mutable mutable = new Mutable(getSelectedEnchantments(entity, skill));

            for (Holder<Enchantment> enchantment : map.keySet()) {
               int level = map.getLevel(enchantment);
               if (mutable.getLevel(enchantment) < level) {
                  mutable.set(enchantment, level);
               }
            }

            CompoundTag tag = getEnchantmentsTag(mutable.toImmutable(), entity.registryAccess());
            if (!tag.isEmpty()) {
               ManasSkillInstance researcher = instance.get();
               researcher.getOrCreateTag().put("SelectedEnchantments", tag);
               researcher.markDirty();
               SkillAPI.getSkillsFrom(entity).markDirty();
            }
         }
      }
   }

   static void clearNewEnchantment(LivingEntity entity, ManasSkill skill) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill(skill);
      if (!instance.isEmpty()) {
         CompoundTag tag = getEnchantmentsTag(ItemEnchantments.EMPTY, entity.registryAccess());
         if (!tag.isEmpty()) {
            instance.get().getOrCreateTag().put("NewEnchantments", tag);
            instance.get().markDirty();
            SkillAPI.getSkillsFrom(entity).markDirty();
         }
      }
   }
}
