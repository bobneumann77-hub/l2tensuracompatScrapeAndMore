package io.github.manasmods.tensura.ability.subclass;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.menu.RefiningMenu;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.network.s2c.OpenSpatialStorageMenuPayload;
import io.github.manasmods.tensura.recipe.RefiningRecipe;
import io.github.manasmods.tensura.recipe.input.RefiningRecipeInput;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public interface IRefining<S extends ManasSkill & IRefining<S>> extends ISpatialStorage {
   default int getSpatialStorageIdOffset() {
      return 0;
   }

   default boolean isAutoRefiningAllowed() {
      return false;
   }

   default boolean hasAutoCraftingTab() {
      return false;
   }

   default void openRefiningMenu(LivingEntity entity, ManasSkillInstance instance) {
      this.openRefiningMenu(entity, entity, instance);
   }

   default void openRefiningMenu(LivingEntity entity, LivingEntity owner, ManasSkillInstance instance) {
      if (entity instanceof ServerPlayer player) {
         player.closeContainer();
         this.openRefiningMenu(player, owner, instance);
         player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
      }
   }

   default void openRefiningMenu(ServerPlayer player, LivingEntity owner, ManasSkillInstance instance) {
      player.nextContainerCounter();
      S skill = (S)instance.getSkill();
      SpatialStorageContainer container = this.getSpatialStorage(instance, owner.registryAccess());
      NetworkManager.sendToPlayer(
         player,
         new OpenSpatialStorageMenuPayload(
            OpenSpatialStorageMenuPayload.StorageType.REFINING,
            player.containerCounter,
            container.getContainerSize(),
            container.getMaxStackSize(),
            0,
            owner.getId(),
            skill.getRegistryName()
         )
      );
      player.containerMenu = new RefiningMenu(player.containerCounter, player.getInventory(), owner, container, skill);
      player.initMenu(player.containerMenu);
      ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
   }

   default boolean onRefiningTick(ManasSkillInstance instance, Player player) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.getBoolean("Brewing")) {
         tag.putInt("startRefine", player.tickCount);
         instance.markDirty();
         if (player.containerMenu instanceof RefiningMenu<?> menu) {
            if (this.onRefining(instance, menu, player.level(), player, menu.brewingContainer, menu.resultContainer)) {
               player.playNotifySound(SoundEvents.VILLAGER_WORK_CLERIC, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               if (!tag.getBoolean("RepeatBrewing")) {
                  tag.putBoolean("Brewing", false);
                  instance.markDirty();
               }

               return true;
            }

            tag.putBoolean("Brewing", false);
            tag.putBoolean("RepeatBrewing", false);
            instance.markDirty();
         } else {
            if (this.onRefining(player.level(), player, instance, this.getSpatialStorage(instance, player.level().registryAccess()))) {
               if (!tag.getBoolean("RepeatBrewing")) {
                  tag.putBoolean("Brewing", false);
                  instance.markDirty();
               }

               return true;
            }

            tag.putBoolean("Brewing", false);
            tag.putBoolean("RepeatBrewing", false);
            instance.markDirty();
         }
      } else if (tag.getBoolean("RepeatBrewing")) {
         tag.putBoolean("RepeatBrewing", false);
         instance.markDirty();
      }

      return false;
   }

   default boolean onRefining(
      ManasSkillInstance instance, RefiningMenu<?> pMenu, Level pLevel, Player pPlayer, SimpleContainer pContainer, ResultContainer pResult
   ) {
      if (pLevel.isClientSide()) {
         return false;
      }

      MinecraftServer server = pLevel.getServer();
      if (server == null) {
         return false;
      }

      RefiningRecipeInput input = new RefiningRecipeInput(pContainer);
      Optional<RecipeHolder<RefiningRecipe>> optional = server.getRecipeManager().getRecipeFor((RecipeType)TensuraRecipes.REFINING_TYPE.get(), input, pLevel);
      if (optional.isEmpty()) {
         return false;
      }

      ServerPlayer serverPlayer = (ServerPlayer)pPlayer;
      ItemStack stack = ItemStack.EMPTY;
      RecipeHolder<RefiningRecipe> recipe = optional.get();
      if (pResult.setRecipeUsed(pLevel, serverPlayer, recipe)) {
         stack = ((RefiningRecipe)recipe.value()).assemble(input, pLevel.registryAccess());
      }

      if (stack.isEmpty()) {
         return false;
      }

      ItemStack result = pResult.getItem(0);
      if (!result.isEmpty() && !ItemStack.isSameItemSameComponents(pResult.getItem(0), stack)) {
         return false;
      }

      if (pResult.isEmpty()) {
         pResult.setItem(0, stack);
      } else {
         stack.grow(result.getCount());
         if (stack.getCount() > Math.max(pContainer.getMaxStackSize(), 16)) {
            return false;
         }

         pResult.setItem(0, stack);
      }

      pMenu.setRemoteSlot(0, stack);
      serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(pMenu.containerId, pMenu.incrementStateId(), 0, stack));
      ((RefiningRecipe)recipe.value()).takeItemsFrom(pContainer);
      pContainer.setChanged();
      if (pContainer.isEmpty()) {
         instance.getOrCreateTag().putBoolean("Brewing", false);
         instance.markDirty();
      }

      return true;
   }

   default boolean onRefining(Level pLevel, Player pPlayer, ManasSkillInstance instance, SpatialStorageContainer spatialContainer) {
      if (pLevel.isClientSide()) {
         return false;
      } else {
         MinecraftServer server = pLevel.getServer();
         if (server == null) {
            return false;
         } else if (!(instance.getSkill() instanceof ISpatialStorage spatialStorage)) {
            return false;
         } else {
            SpatialStorageContainer var14 = new SpatialStorageContainer(8, 128);

            for (int input = 0; input < 8; input++) {
               var14.setItem(input, spatialContainer.getItem(input + this.getSpatialStorageIdOffset()));
            }

            RefiningRecipeInput input = new RefiningRecipeInput(var14);
            Optional<RecipeHolder<RefiningRecipe>> optional = server.getRecipeManager()
               .getRecipeFor((RecipeType)TensuraRecipes.REFINING_TYPE.get(), input, pLevel);
            if (optional.isEmpty()) {
               return false;
            }

            RecipeHolder<RefiningRecipe> recipe = optional.get();
            ItemStack stack = ((RefiningRecipe)recipe.value()).assemble(input, pLevel.registryAccess());
            if (stack.isEmpty()) {
               return false;
            }

            ItemStack result = spatialContainer.getItem(this.getSpatialStorageIdOffset() + 8);
            if (!result.isEmpty() && !ItemStack.isSameItemSameComponents(result, stack)) {
               return false;
            }

            if (result.isEmpty()) {
               spatialStorage.setItemInSpatialStorage(instance, pPlayer, stack, 19);
            } else {
               stack.grow(result.getCount());
               if (stack.getCount() > Math.max(var14.getMaxStackSize(), 16)) {
                  return false;
               }

               spatialStorage.setItemInSpatialStorage(instance, pPlayer, stack, 19);
            }

            ((RefiningRecipe)recipe.value()).takeItemsFrom(var14);
            var14.setChanged();

            for (int i = 0; i < 8; i++) {
               spatialStorage.setItemInSpatialStorage(instance, pPlayer, var14.getItem(i), i + this.getSpatialStorageIdOffset());
            }

            if (var14.isEmpty()) {
               instance.getOrCreateTag().putBoolean("Brewing", false);
               instance.markDirty();
            }

            return true;
         }
      }
   }
}
