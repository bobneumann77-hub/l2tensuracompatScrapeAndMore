package dev.xkmc.l2hostility.content.menu.tab;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.chunk.SectionDifficulty;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.content.logic.MobDifficultyCollector;
import dev.xkmc.l2hostility.content.logic.TraitManager;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.tabs.contents.BaseTextScreen;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import dev.xkmc.l2tabs.tabs.inventory.InvTabData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Player;

public class DifficultyScreen extends BaseTextScreen {
   protected DifficultyScreen(Component title) {
      super(title, L2Tabs.loc("textures/gui/empty.png"));
   }

   public void init() {
      super.init();
      new TabManager(this, new InvTabData()).init(x$0 -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(x$0);
      }, (TabToken)LHMiscs.TAB_DIFFICULTY.get());
   }

   public void render(GuiGraphics g, int mx, int my, float ptick) {
      super.render(g, mx, my, ptick);
      int x = this.leftPos + 8;
      int y = this.topPos + 6;
      List<Pair<Component, Supplier<List<Component>>>> list = new ArrayList<>();
      addDifficultyInfo(list, ChatFormatting.DARK_RED, ChatFormatting.DARK_GREEN, ChatFormatting.DARK_PURPLE);
      addRewardInfo(list);
      List<Component> tooltip = null;

      for (Pair<Component, Supplier<List<Component>>> c : list) {
         if (mx >= x && mx <= x + this.font.width((FormattedText)c.getFirst()) && my >= y && my <= y + 10) {
            tooltip = c.getSecond() == null ? null : (List)((Supplier)c.getSecond()).get();
         }

         g.drawString(this.font, (Component)c.getFirst(), x, y, 0, false);
         y += 10;
      }

      if (tooltip != null && !tooltip.isEmpty()) {
         g.renderComponentTooltip(this.font, tooltip, mx, my);
      }
   }

   public static void addRewardInfo(List<Pair<Component, Supplier<List<Component>>>> list) {
      Player player = Minecraft.getInstance().player;
      assert player != null;
      PlayerDifficulty cap = (PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(player);
      list.add(Pair.of(LangData.INFO_REWARD.get(cap.getRewardCount()).withStyle(ChatFormatting.DARK_GREEN), List::of));
   }

   public static void addDifficultyInfo(List<Pair<Component, Supplier<List<Component>>>> list, ChatFormatting... formats) {
      Player player = Minecraft.getInstance().player;
      assert player != null;
      PlayerDifficulty cap = (PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(player);
      list.add(Pair.of(LangData.INFO_PLAYER_LEVEL.get(cap.getLevel(player).getStr()), (Supplier<List<Component>>)() -> cap.getPlayerDifficultyDetail(player)));
      int perc = Math.round(100.0F * (float)cap.getLevel(player).getExp() / (float)cap.getLevel(player).getMaxExp());
      list.add(Pair.of(LangData.INFO_PLAYER_EXP.get(perc), List::of));
      int maxCap = cap.getRankCap();
      list.add(
         Pair.of(LangData.INFO_PLAYER_CAP.get(maxCap > TraitManager.getMaxLevel() ? LangData.TOOLTIP_LEGENDARY.get().withStyle(formats[2]) : maxCap), List::of)
      );
      Optional<ChunkCapHolder> opt = ChunkDifficulty.at(player.level(), player.blockPosition());
      if (opt.isPresent()) {
         ChunkCapHolder chunk = opt.get();
         SectionDifficulty sec = chunk.getSection(player.blockPosition().getY());
         if (sec.isCleared()) {
            list.add(Pair.of(LangData.INFO_CHUNK_CLEAR.get().withStyle(formats[1]), List::of));
         } else {
            MobDifficultyCollector ins = new MobDifficultyCollector();
            chunk.modifyInstance(player.blockPosition(), ins);
            list.add(
               Pair.of(
                  LangData.INFO_CHUNK_LEVEL.get(ins.getBase()).withStyle(formats[0]), (Supplier<List<Component>>)() -> sec.getSectionDifficultyDetail(player)
               )
            );
            list.add(Pair.of(LangData.INFO_CHUNK_SCALE.get(ins.scale).withStyle(formats[0]), List::of));
         }
      }
   }
}
