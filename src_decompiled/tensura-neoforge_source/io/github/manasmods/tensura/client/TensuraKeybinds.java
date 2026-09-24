package io.github.manasmods.tensura.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import io.github.manasmods.manascore.keybind.api.KeybindingCategory;
import io.github.manasmods.manascore.keybind.api.KeybindingManager;
import io.github.manasmods.manascore.keybind.api.ManasKeybinding;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.network.c2s.RequestAbilityModeChangePacket;
import io.github.manasmods.tensura.network.c2s.RequestNamingKeyPacket;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class TensuraKeybinds {
   private static final KeybindingCategory TENSURA_KEYBIND = KeybindingCategory.of("tensura");
   public static final ManasKeybinding RELOAD_CONFIGS = new ManasKeybinding(
      "tensura.keybinding.reload_configs", InputConstants.UNKNOWN, TENSURA_KEYBIND, () -> {}
   );
   public static final ManasKeybinding MAIN_GUI = new ManasKeybinding("tensura.keybinding.main_gui", 66, TENSURA_KEYBIND, () -> {});
   public static final ManasKeybinding NAME = new ManasKeybinding(
      "tensura.keybinding.name", 78, TENSURA_KEYBIND, () -> NetworkManager.sendToServer(new RequestNamingKeyPacket()), duration -> {}
   );
   public static final ManasKeybinding ACTIVATE_SLOT_1 = new ManasKeybinding(
      "tensura.keybinding.ability.slot_1", 90, TENSURA_KEYBIND, () -> onAbilityPressed(0), duration -> onAbilityReleased(0)
   );
   public static final ManasKeybinding ACTIVATE_SLOT_2 = new ManasKeybinding(
      "tensura.keybinding.ability.slot_2", 88, TENSURA_KEYBIND, () -> onAbilityPressed(1), duration -> onAbilityReleased(1)
   );
   public static final ManasKeybinding ACTIVATE_SLOT_3 = new ManasKeybinding(
      "tensura.keybinding.ability.slot_3", 67, TENSURA_KEYBIND, () -> onAbilityPressed(2), duration -> onAbilityReleased(2)
   );
   public static final ManasKeybinding NEXT_ABILITY_MODE = new ManasKeybinding("tensura.keybinding.next_mode", 342, TENSURA_KEYBIND, () -> {});
   public static final ManasKeybinding PREVIOUS_ABILITY_MODE = new ManasKeybinding(
      "tensura.keybinding.previous_mode", InputConstants.UNKNOWN, TENSURA_KEYBIND, () -> {}
   );
   public static final ManasKeybinding RACE_ABILITY = new ManasKeybinding(
      "tensura.keybinding.race_ability", 82, TENSURA_KEYBIND, RaceAPI::raceAbilityActivationPacket, duration -> RaceAPI.raceAbilityReleasePacket()
   );
   public static final ManasKeybinding DODGE = new ManasKeybinding("tensura.keybinding.dodge", 86, TENSURA_KEYBIND, () -> {});
   public static final KeyMapping KEY_0 = new KeyMapping("key.number_0", 48, "key.categories.hidden");

   public static void init() {
      KeybindingManager.register(
         new ManasKeybinding[]{
            RELOAD_CONFIGS, MAIN_GUI, NAME, RACE_ABILITY, DODGE, ACTIVATE_SLOT_1, ACTIVATE_SLOT_2, ACTIVATE_SLOT_3, NEXT_ABILITY_MODE, PREVIOUS_ABILITY_MODE
         }
      );
   }

   public static void onAbilityPressed(int slot) {
      if (Platform.getEnvironment() == Env.CLIENT) {
         if (NEXT_ABILITY_MODE.isDown()) {
            NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeModePacket(slot, false));
         } else if (PREVIOUS_ABILITY_MODE.isDown()) {
            NetworkManager.sendToServer(RequestAbilityModeChangePacket.changeModePacket(slot, true));
         } else {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player == null) {
               return;
            }

            AbilitySlot abilitySlot = TensuraStorages.getAbilityFrom(player).getAbilitySlot(slot);
            if (abilitySlot.getSkill() == null) {
               return;
            }

            SkillAPI.skillActivationPacket(abilitySlot.getSkill().getRegistryName(), slot, abilitySlot.getMode());
         }
      }
   }

   public static void onAbilityReleased(int slot) {
      Minecraft minecraft = Minecraft.getInstance();
      Player player = minecraft.player;
      if (player != null) {
         AbilitySlot abilitySlot = TensuraStorages.getAbilityFrom(player).getAbilitySlot(slot);
         if (abilitySlot.getSkill() != null) {
            SkillAPI.skillReleasePacket(abilitySlot.getSkill().getRegistryName(), slot, abilitySlot.getMode());
         }
      }
   }
}
