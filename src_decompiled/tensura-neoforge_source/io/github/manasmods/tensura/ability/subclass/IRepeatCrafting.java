package io.github.manasmods.tensura.ability.subclass;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.menu.RepeatCraftingMenu;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.menu.container.TensuraCraftingContainer;
import io.github.manasmods.tensura.network.s2c.OpenSpatialStorageMenuPayload;
import io.github.manasmods.tensura.recipe.SmithingBenchRecipe;
import io.github.manasmods.tensura.recipe.input.SmithingBenchRecipeInput;
import io.github.manasmods.tensura.registry.recipe.TensuraRecipes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CraftingInput.Positioned;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public interface IRepeatCrafting<S extends ManasSkill & IRepeatCrafting<S>> extends ISpatialStorage {
   default void openRepeatCraftingMenu(LivingEntity entity, ManasSkillInstance instance) {
      this.openRepeatCraftingMenu(entity, entity, instance);
   }

   default void openRepeatCraftingMenu(LivingEntity entity, LivingEntity owner, ManasSkillInstance instance) {
      if (entity instanceof ServerPlayer player) {
         player.closeContainer();
         this.openRepeatCraftingMenu(player, owner, instance);
         player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
      }
   }

   default void openRepeatCraftingMenu(ServerPlayer player, LivingEntity owner, ManasSkillInstance instance) {
      player.nextContainerCounter();
      S skill = (S)instance.getSkill();
      SpatialStorageContainer container = this.getSpatialStorage(instance, owner.registryAccess());
      NetworkManager.sendToPlayer(
         player,
         new OpenSpatialStorageMenuPayload(
            OpenSpatialStorageMenuPayload.StorageType.REPEAT_CRAFTING,
            player.containerCounter,
            container.getContainerSize(),
            container.getMaxStackSize(),
            0,
            owner.getId(),
            skill.getRegistryName()
         )
      );
      player.containerMenu = new RepeatCraftingMenu(player.containerCounter, player.getInventory(), owner, container, skill);
      player.initMenu(player.containerMenu);
      ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
   }

   default boolean onRepeatCraftingTick(ManasSkillInstance instance, Player player) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.getBoolean("Repeating")) {
         if (!(player.containerMenu instanceof RepeatCraftingMenu<?> craftingMenu)) {
            return this.onRepeatCrafting(player.level(), player, instance);
         }

         if (this.onRepeatCrafting(instance, craftingMenu, player.level(), player, craftingMenu.craftSlots, craftingMenu.craftResultSlots)) {
            player.playNotifySound(SoundEvents.VILLAGER_WORK_TOOLSMITH, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            return true;
         }
      }

      return false;
   }

   @Nullable
   default RecipeHolder<SmithingBenchRecipe> getCopySmithingRecipe(Level pLevel, ItemStack toCopy, CraftingContainer container) {
      if (!toCopy.isEmpty()) {
         SmithingBenchRecipeInput input = new SmithingBenchRecipeInput(container, List.of(), false);

         for (RecipeHolder<SmithingBenchRecipe> recipe : pLevel.getRecipeManager().getAllRecipesFor((RecipeType)TensuraRecipes.SMITHING_BENCH_TYPE.get())) {
            if (((SmithingBenchRecipe)recipe.value()).getOutput().is(toCopy.getItem()) && ((SmithingBenchRecipe)recipe.value()).matches(input, pLevel)) {
               return recipe;
            }
         }
      }

      return null;
   }

   default boolean onRepeatCrafting(
      ManasSkillInstance instance, RepeatCraftingMenu<?> pMenu, Level pLevel, Player pPlayer, CraftingContainer pContainer, ResultContainer pResult
   ) {
      if (pLevel.isClientSide()) {
         return false;
      }

      MinecraftServer server = pLevel.getServer();
      if (server == null) {
         return false;
      }

      ServerPlayer serverPlayer = (ServerPlayer)pPlayer;
      ItemStack stack = ItemStack.EMPTY;
      RecipeHolder<SmithingBenchRecipe> smithingRecipe = this.getCopySmithingRecipe(pLevel, pMenu.copySlots.getItem(0), pContainer);
      if (smithingRecipe != null) {
         stack = ((SmithingBenchRecipe)smithingRecipe.value()).getResultItem(pLevel.registryAccess());
      }

      if (stack.isEmpty()) {
         CraftingInput craftingInput = pContainer.asCraftInput();
         Optional<RecipeHolder<CraftingRecipe>> optional = pLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInput, pLevel);
         if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipe = optional.get();
            CraftingRecipe craftingRecipe = (CraftingRecipe)recipe.value();
            if (pResult.setRecipeUsed(pLevel, serverPlayer, recipe)) {
               ItemStack itemStack2 = craftingRecipe.assemble(craftingInput, pLevel.registryAccess());
               if (itemStack2.isItemEnabled(pLevel.enabledFeatures())) {
                  stack = itemStack2;
               }
            }
         }
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
      this.onTakeResultItems(pPlayer, pContainer, smithingRecipe);
      return true;
   }

   default boolean onRepeatCrafting(Level pLevel, Player pPlayer, ManasSkillInstance instance) {
      if (pLevel.isClientSide()) {
         return false;
      }

      MinecraftServer server = pLevel.getServer();
      if (server == null) {
         return false;
      }

      SpatialStorageContainer container = this.getSpatialStorage(instance, pLevel.registryAccess());
      TensuraCraftingContainer craftingContainer = new TensuraCraftingContainer(pPlayer.containerMenu, 3, 3);

      for (int i = 0; i < 9; i++) {
         craftingContainer.setItem(i, container.getItem(i));
      }

      ItemStack stack = ItemStack.EMPTY;
      RecipeHolder<SmithingBenchRecipe> smithingRecipe = this.getCopySmithingRecipe(pLevel, container.getItem(10), craftingContainer);
      if (smithingRecipe != null) {
         stack = ((SmithingBenchRecipe)smithingRecipe.value()).getResultItem(pLevel.registryAccess());
      }

      if (stack.isEmpty()) {
         CraftingInput craftingInput = craftingContainer.asCraftInput();
         Optional<RecipeHolder<CraftingRecipe>> optional = pLevel.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInput, pLevel);
         if (optional.isPresent()) {
            CraftingRecipe craftingRecipe = (CraftingRecipe)optional.get().value();
            ItemStack itemStack2 = craftingRecipe.assemble(craftingInput, pLevel.registryAccess());
            if (itemStack2.isItemEnabled(pLevel.enabledFeatures())) {
               stack = itemStack2;
            }
         }
      }

      if (stack.isEmpty()) {
         return false;
      }

      ItemStack result = container.getItem(9);
      if (!result.isEmpty() && !ItemStack.isSameItemSameComponents(result, stack)) {
         return false;
      }

      if (result.isEmpty()) {
         this.setItemInSpatialStorage(instance, pPlayer, stack, 9);
      } else {
         stack.grow(result.getCount());
         if (stack.getCount() > Math.max(container.getMaxStackSize(), 16)) {
            return false;
         }

         this.setItemInSpatialStorage(instance, pPlayer, stack, 9);
      }

      this.onTakeResultItems(pPlayer, craftingContainer, smithingRecipe);

      for (int i = 0; i < 9; i++) {
         this.setItemInSpatialStorage(instance, pPlayer, craftingContainer.getItem(i), i);
      }

      return true;
   }

   default void onTakeResultItems(Player player, CraftingContainer pContainer, @Nullable RecipeHolder<SmithingBenchRecipe> recipe) {
      if (recipe != null) {
         ((SmithingBenchRecipe)recipe.value()).takeItemsFrom(pContainer);
         pContainer.setChanged();
      } else {
         this.onTakeCraftingResult(player, pContainer);
      }
   }

   default void onTakeCraftingResult(Player player, CraftingContainer craftSlots) {
      Positioned positioned = craftSlots.asPositionedCraftInput();
      CraftingInput craftingInput = positioned.input();
      int i = positioned.left();
      int j = positioned.top();
      NonNullList<ItemStack> nonNullList = player.level().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, craftingInput, player.level());

      for (int k = 0; k < craftingInput.height(); k++) {
         for (int l = 0; l < craftingInput.width(); l++) {
            int m = l + i + (k + j) * craftSlots.getWidth();
            ItemStack itemStack2 = craftSlots.getItem(m);
            ItemStack itemStack3 = (ItemStack)nonNullList.get(l + k * craftingInput.width());
            if (!itemStack2.isEmpty()) {
               craftSlots.removeItem(m, 1);
               itemStack2 = craftSlots.getItem(m);
            }

            if (!itemStack3.isEmpty()) {
               if (itemStack2.isEmpty()) {
                  craftSlots.setItem(m, itemStack3);
               } else if (ItemStack.isSameItemSameComponents(itemStack2, itemStack3)) {
                  itemStack3.grow(itemStack2.getCount());
                  craftSlots.setItem(m, itemStack3);
               } else if (!player.getInventory().add(itemStack3)) {
                  player.drop(itemStack3, false);
               }
            }
         }
      }
   }
}
