package io.github.manasmods.tensura.ability.subclass;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.menu.SpatialStorageMenu;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.network.s2c.OpenSpatialStorageMenuPayload;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface ISpatialStorage {
   SpatialStorageContainer getSpatialStorage(ManasSkillInstance var1, Provider var2);

   default void openSpatialStorage(LivingEntity opener, ManasSkillInstance instance) {
      this.openSpatialStorage(opener, opener, instance);
   }

   default void openSpatialStorage(LivingEntity opener, LivingEntity owner, ManasSkillInstance instance) {
      if (opener instanceof ServerPlayer player) {
         player.closeContainer();
         this.openSpatialStoragePage(player, owner, instance, 0);
         player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
      }
   }

   default void openSpatialStoragePage(ServerPlayer player, LivingEntity owner, ManasSkillInstance instance, int page) {
      player.nextContainerCounter();
      ManasSkill skill = instance.getSkill();
      SpatialStorageContainer container = this.getSpatialStorage(instance, owner.registryAccess());
      NetworkManager.sendToPlayer(
         player,
         new OpenSpatialStorageMenuPayload(
            OpenSpatialStorageMenuPayload.StorageType.DEFAULT,
            player.containerCounter,
            container.getContainerSize(),
            container.getMaxStackSize(),
            page,
            owner.getId(),
            skill.getRegistryName()
         )
      );
      player.containerMenu = new SpatialStorageMenu(player.containerCounter, player.getInventory(), owner, container, skill, page);
      player.initMenu(player.containerMenu);
      ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
   }

   default boolean addItemToSpatialStorage(ManasSkillInstance instance, LivingEntity entity, ItemStack stack) {
      SpatialStorageContainer container = this.getSpatialStorage(instance, entity.registryAccess());
      if (container.canAddItem(stack)) {
         container.addItem(stack);
         this.saveContainer(instance, entity, container);
         return true;
      } else {
         this.saveContainer(instance, entity, container);
         return false;
      }
   }

   default void setItemInSpatialStorage(ManasSkillInstance instance, LivingEntity entity, ItemStack stack, int slot) {
      SpatialStorageContainer container = this.getSpatialStorage(instance, entity.registryAccess());
      container.setItem(slot, stack);
      this.saveContainer(instance, entity, container);
   }

   default void moveItemsToSpatialStorage(ManasSkillInstance from, ManasSkillInstance to, LivingEntity entity, boolean openNewStorage) {
      ISpatialStorage newStorage = (ISpatialStorage)to.getSkill();
      SpatialStorageContainer container = this.getSpatialStorage(from, entity.registryAccess());
      if (!container.isEmpty()) {
         for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && !newStorage.addItemToSpatialStorage(to, entity, stack) && entity instanceof Player player && !player.addItem(stack)) {
               player.drop(stack, false);
            }
         }

         container.clearContent();
         from.getOrCreateTag().remove("SpatialStorage");
         from.markDirty();
      }

      if (openNewStorage) {
         newStorage.openSpatialStorage(entity, to);
      }
   }

   default void dropAllItems(ManasSkillInstance instance, Player player) {
      SpatialStorageContainer container = this.getSpatialStorage(instance, player.registryAccess());
      if (!container.isEmpty()) {
         for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && !player.addItem(stack)) {
               player.drop(stack, false);
            }
         }

         container.clearContent();
         instance.getOrCreateTag().remove("SpatialStorage");
         instance.markDirty();
      }
   }

   default void saveContainer(ManasSkillInstance instance, LivingEntity entity, SpatialStorageContainer container) {
      CompoundTag tag = instance.getOrCreateTag();
      tag.put("SpatialStorage", container.createTag(entity.registryAccess()));
      instance.markDirty();
      SkillAPI.getSkillsFrom(entity).markDirty();
   }
}
