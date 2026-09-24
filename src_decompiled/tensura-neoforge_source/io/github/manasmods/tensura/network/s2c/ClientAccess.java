package io.github.manasmods.tensura.network.s2c;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.IRefining;
import io.github.manasmods.tensura.ability.subclass.IRepeatCrafting;
import io.github.manasmods.tensura.ability.subclass.ISpatialMovement;
import io.github.manasmods.tensura.ability.subclass.ISubAbilityModeHolder;
import io.github.manasmods.tensura.client.screen.AbilitySelectionScreen;
import io.github.manasmods.tensura.client.screen.HumanoidInventoryScreen;
import io.github.manasmods.tensura.client.screen.HumanoidMainScreen;
import io.github.manasmods.tensura.client.screen.IllusionItemScreen;
import io.github.manasmods.tensura.client.screen.MountScreen;
import io.github.manasmods.tensura.client.screen.RefiningScreen;
import io.github.manasmods.tensura.client.screen.RepeatCraftingScreen;
import io.github.manasmods.tensura.client.screen.ResearcherEnchantingScreen;
import io.github.manasmods.tensura.client.screen.ResearcherStorageScreen;
import io.github.manasmods.tensura.client.screen.SpatialBagScreen;
import io.github.manasmods.tensura.client.screen.SpatialMovementScreen;
import io.github.manasmods.tensura.client.screen.SpatialStorageScreen;
import io.github.manasmods.tensura.client.screen.SubAbilityModeSelectionScreen;
import io.github.manasmods.tensura.client.screen.SynthesisSeparationScreen;
import io.github.manasmods.tensura.client.screen.UncraftingScreen;
import io.github.manasmods.tensura.entity.template.TensuraHumanoidEntity;
import io.github.manasmods.tensura.entity.template.TensuraMountEntity;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.menu.HumanoidInventoryMenu;
import io.github.manasmods.tensura.menu.HumanoidMainMenu;
import io.github.manasmods.tensura.menu.MountMenu;
import io.github.manasmods.tensura.menu.RefiningMenu;
import io.github.manasmods.tensura.menu.RepeatCraftingMenu;
import io.github.manasmods.tensura.menu.ResearcherEnchantingMenu;
import io.github.manasmods.tensura.menu.ResearcherStorageMenu;
import io.github.manasmods.tensura.menu.SpatialBagMenu;
import io.github.manasmods.tensura.menu.SpatialStorageMenu;
import io.github.manasmods.tensura.menu.SynthesisSeparationMenu;
import io.github.manasmods.tensura.menu.UncraftingMenu;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.IntegerValue;
import org.jetbrains.annotations.Nullable;

class ClientAccess {
   static void handle(DisplayTotemEffectPayload packet) {
      Item item = (Item)BuiltInRegistries.ITEM.get(packet.item());
      if (item != null) {
         Minecraft.getInstance().gameRenderer.displayItemActivation(item.getDefaultInstance());
      }
   }

   static void handle(DisplayTotemStackEffectPayload packet) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         ItemStack stack = ItemStack.parseOptional(player.registryAccess(), packet.item());
         Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
      }
   }

   @Nullable
   static Entity getEntityFromId(int id) {
      if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.getId() == id) {
         return Minecraft.getInstance().player;
      }

      Level level = Minecraft.getInstance().level;
      return level == null ? null : level.getEntity(id);
   }

   public static void handleOpenIllusionItemScreenPayload(OpenIllusionItemScreenPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.getId() == message.entityId()) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(message.skill());
            if (skill != null) {
               Minecraft.getInstance().setScreen(new IllusionItemScreen(skill));
            }
         }
      }
   }

   public static void handleAbilitySlotUpdate(UpdateAbilitySlotScreenPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.getId() == message.entityId()) {
         if (Minecraft.getInstance().screen instanceof AbilitySelectionScreen screen) {
            ManasSkill slotted = Objects.equals(message.ability(), ResourceLocation.withDefaultNamespace("empty"))
               ? null
               : (ManasSkill)SkillAPI.getSkillRegistry().get(message.ability());
            switch (message.slot()) {
               case 0:
                  screen.slot1 = slotted;
                  if (screen.slot1 != null) {
                     screen.slot1Instance = screen.abilities
                        .stream()
                        .filter(ability -> ability.getSkill().equals(slotted))
                        .findFirst()
                        .orElse(slotted.createDefaultInstance());
                  }
                  break;
               case 1:
                  screen.slot2 = slotted;
                  if (screen.slot2 != null) {
                     screen.slot2Instance = screen.abilities
                        .stream()
                        .filter(ability -> ability.getSkill().equals(slotted))
                        .findFirst()
                        .orElse(slotted.createDefaultInstance());
                  }
                  break;
               case 2:
                  screen.slot3 = slotted;
                  if (screen.slot3 != null) {
                     screen.slot3Instance = screen.abilities
                        .stream()
                        .filter(ability -> ability.getSkill().equals(slotted))
                        .findFirst()
                        .orElse(slotted.createDefaultInstance());
                  }
            }
         }
      }
   }

   public static void handleOpenHumanoidInventoryMenuPayload(OpenHumanoidMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         ClientLevel level = player.clientLevel;
         if (level.getEntity(message.entityId()) instanceof TensuraHumanoidEntity humanoid) {
            SimpleContainer simpleContainer = new SimpleContainer(message.size());
            int page = message.page();
            if (page == -1) {
               HumanoidMainMenu menu = new HumanoidMainMenu(message.containerId(), player.getInventory(), simpleContainer, humanoid);
               player.containerMenu = menu;
               Minecraft.getInstance().setScreen(new HumanoidMainScreen(menu, player.getInventory(), humanoid));
            } else {
               HumanoidInventoryMenu menu = new HumanoidInventoryMenu(
                  message.containerId(), player.getInventory(), simpleContainer, humanoid, message.page(), message.chestSlots()
               );
               player.containerMenu = menu;
               Minecraft.getInstance().setScreen(new HumanoidInventoryScreen(menu, player.getInventory(), humanoid, message.page()));
            }
         }
      }
   }

   public static void handleOpenMountMenuPayload(OpenMountMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         ClientLevel level = player.clientLevel;
         if (level.getEntity(message.entityId()) instanceof TensuraMountEntity mount) {
            SimpleContainer simpleContainer = new SimpleContainer(message.size());
            MountMenu menu = new MountMenu(
               message.containerId(),
               player.getInventory(),
               simpleContainer,
               mount,
               message.page(),
               message.saddle(),
               message.armor(),
               message.weapon(),
               message.chestSlots()
            );
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new MountScreen(menu, player.getInventory(), mount, message.page()));
         }
      }
   }

   public static void handleOpenResearcherStorageMenuPayload(OpenSpatialStorageMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            SpatialStorageContainer container = new SpatialStorageContainer(message.size(), message.stackSize());
            LivingEntity entity = (LivingEntity)(player.level().getEntity(message.entityId()) instanceof LivingEntity owner ? owner : player);
            ResearcherStorageMenu menu = new ResearcherStorageMenu(
               message.containerId(), player.getInventory(), entity, container, optional.get().getSkill(), message.page()
            );
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new ResearcherStorageScreen(menu, player.getInventory(), container.getContainerSize(), message.page()));
         }
      }
   }

   public static void handleOpenResearcherEnchantingMenuPayload(OpenResearcherEnchantingMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            LivingEntity entity = (LivingEntity)(player.level().getEntity(message.entityId()) instanceof LivingEntity owner ? owner : player);
            ResearcherEnchantingMenu menu = new ResearcherEnchantingMenu(message.containerId(), player.getInventory(), entity, optional.get().getSkill());
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new ResearcherEnchantingScreen(menu, player.getInventory()));
         }
      }
   }

   public static <S extends ManasSkill & IRefining<S>> void handleOpenRefiningMenuPayload(OpenSpatialStorageMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            SpatialStorageContainer container = new SpatialStorageContainer(message.size(), message.stackSize());
            LivingEntity entity = (LivingEntity)(player.level().getEntity(message.entityId()) instanceof LivingEntity owner ? owner : player);
            RefiningMenu<S> menu = new RefiningMenu<>(message.containerId(), player.getInventory(), entity, container, (S)optional.get().getSkill());
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new RefiningScreen(menu, player.getInventory()));
         }
      }
   }

   public static <S extends ManasSkill & IRepeatCrafting<S>> void handleOpenRepeatCraftingMenuPayload(OpenSpatialStorageMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            SpatialStorageContainer container = new SpatialStorageContainer(message.size(), message.stackSize());
            LivingEntity entity = (LivingEntity)(player.level().getEntity(message.entityId()) instanceof LivingEntity owner ? owner : player);
            RepeatCraftingMenu<S> menu = new RepeatCraftingMenu<>(message.containerId(), player.getInventory(), entity, container, (S)optional.get().getSkill());
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new RepeatCraftingScreen(menu, player.getInventory()));
         }
      }
   }

   public static void handleOpenSpatialStorageMenuPayload(OpenSpatialStorageMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            SpatialStorageContainer container = new SpatialStorageContainer(message.size(), message.stackSize());
            LivingEntity entity = (LivingEntity)(player.level().getEntity(message.entityId()) instanceof LivingEntity owner ? owner : player);
            SpatialStorageMenu menu = new SpatialStorageMenu(
               message.containerId(), player.getInventory(), entity, container, optional.get().getSkill(), message.page()
            );
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new SpatialStorageScreen(menu, player.getInventory(), container.getContainerSize(), message.page()));
         }
      }
   }

   public static void handleOpenSpatialBagMenuPayload(OpenSpatialStorageMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            SpatialStorageContainer container = new SpatialStorageContainer(message.size(), message.stackSize());
            LivingEntity entity = (LivingEntity)(player.level().getEntity(message.entityId()) instanceof LivingEntity owner ? owner : player);
            boolean mastered = optional.get().isMastered(entity);
            SpatialBagMenu menu = new SpatialBagMenu(
               message.containerId(), player.getInventory(), entity, container, optional.get().getSkill(), mastered, message.page()
            );
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new SpatialBagScreen(menu, player.getInventory(), container.getContainerSize(), mastered, message.page()));
         }
      }
   }

   public static void handleOpenSynthesisSeparationMenuPayload(OpenDegenerateMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.getId() == message.entityId()) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            SynthesisSeparationMenu menu = new SynthesisSeparationMenu(message.containerId(), player.getInventory(), optional.get().getSkill());
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new SynthesisSeparationScreen(menu, player.getInventory()));
         }
      }
   }

   public static void handleOpenUncraftingMenuPayload(OpenDegenerateMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.getId() == message.entityId()) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty()) {
            UncraftingMenu menu = new UncraftingMenu(message.containerId(), player.getInventory(), optional.get().getSkill());
            player.containerMenu = menu;
            Minecraft.getInstance().setScreen(new UncraftingScreen(menu, player.getInventory()));
         }
      }
   }

   public static void handleOpenSpatialMovementMenuPayload(OpenSpatialMovementMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.getId() == message.entityId()) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty() && optional.get().getSkill() instanceof ISpatialMovement) {
            Minecraft.getInstance().setScreen(new SpatialMovementScreen(optional.get().getSkill(), message.dimensionLocations()));
         }
      }
   }

   public static void handleOpenSubAbilitySelectionMenuPayload(OpenSubAbilitySelectionMenuPayload message) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.getId() == message.entityId()) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(message.skill());
         if (!optional.isEmpty() && optional.get().getSkill() instanceof ISubAbilityModeHolder) {
            Minecraft.getInstance().setScreen(new SubAbilityModeSelectionScreen(optional.get().getSkill()));
         }
      }
   }

   public static void updatePartHurtAnimation(HurtLivingPartPayload message) {
      if (Minecraft.getInstance().level != null) {
         Entity part = Minecraft.getInstance().level.getEntity(message.part());
         Entity parent = Minecraft.getInstance().level.getEntity(message.parent());
         if (part instanceof ILivingPartEntity multipart && parent instanceof LivingEntity living) {
            multipart.onServerHurt(living);
         }
      }
   }

   public static void updateGameruleClientSide(SendIntegerGameruleUpdatePayload message) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         ((IntegerValue)level.getGameRules().getRule(message.key().getKey())).set(message.value(), null);
      }
   }

   public static void updateGameruleClientSide(SendBooleanGameruleUpdatePayload message) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         ((BooleanValue)level.getGameRules().getRule(message.key().getKey())).set(message.value(), null);
      }
   }
}
