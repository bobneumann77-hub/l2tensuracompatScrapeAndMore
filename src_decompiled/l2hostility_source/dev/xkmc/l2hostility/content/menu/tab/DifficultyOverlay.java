package dev.xkmc.l2hostility.content.menu.tab;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2itemselector.overlay.InfoSideBar;
import dev.xkmc.l2itemselector.overlay.SideBar.IntSignature;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

public class DifficultyOverlay extends InfoSideBar<IntSignature> {
   public DifficultyOverlay() {
      super(40.0F, 3.0F);
   }

   protected List<Component> getText() {
      List<Pair<Component, Supplier<List<Component>>>> comp = new ArrayList<>();
      DifficultyScreen.addDifficultyInfo(comp, ChatFormatting.RED, ChatFormatting.GREEN, ChatFormatting.GOLD);
      return comp.stream().<Component>map(Pair::getFirst).toList();
   }

   protected boolean isOnHold() {
      return true;
   }

   public IntSignature getSignature() {
      return new IntSignature(0);
   }

   public boolean isScreenOn() {
      if (Minecraft.getInstance().screen != null) {
         return false;
      }

      LocalPlayer player = Minecraft.getInstance().player;
      return player == null ? false : player.getMainHandItem().is((Item)LHItems.DETECTOR.get()) || player.getOffhandItem().is((Item)LHItems.DETECTOR.get());
   }
}
