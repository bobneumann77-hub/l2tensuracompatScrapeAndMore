package dev.xkmc.l2hostility.content.item.consumable;

import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.content.logic.LevelEditor;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class BottleOfCurse extends DrinkableBottleItem {
   public BottleOfCurse(Properties prop) {
      super(prop);
   }

   @Override
   protected void doServerLogic(ServerPlayer player) {
      PlayerDifficulty cap = (PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(player);
      LevelEditor editor = cap.getLevelEditor(player);
      editor.addBase((Integer)LHConfig.SERVER.bottleOfCurseLevel.get());
      cap.sync(player);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      if (!(Boolean)LHConfig.SERVER.banBottles.get()) {
         list.add(LangData.ITEM_BOTTLE_CURSE.get(LHConfig.SERVER.bottleOfCurseLevel.get()).withStyle(ChatFormatting.GRAY));
      }
   }
}
