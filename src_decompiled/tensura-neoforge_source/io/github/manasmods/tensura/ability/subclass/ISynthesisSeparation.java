package io.github.manasmods.tensura.ability.subclass;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.menu.SynthesisSeparationMenu;
import io.github.manasmods.tensura.network.s2c.OpenDegenerateMenuPayload;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;

public interface ISynthesisSeparation {
   int getMaximumBonusLevel();

   List<String> getSeparateBlacklistEnchantments();

   List<String> getSynthesisBlacklistEnchantments();

   List<String> getBonusLevelBlackListEnchantments();

   default void openSynthesisSeparationMenu(ServerPlayer player, ManasSkillInstance instance) {
      player.nextContainerCounter();
      ManasSkill skill = instance.getSkill();
      NetworkManager.sendToPlayer(
         player, new OpenDegenerateMenuPayload(OpenDegenerateMenuPayload.MenuType.ENCHANTING, player.containerCounter, player.getId(), skill.getRegistryName())
      );
      player.containerMenu = new SynthesisSeparationMenu(player.containerCounter, player.getInventory(), skill);
      player.initMenu(player.containerMenu);
      ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
   }
}
