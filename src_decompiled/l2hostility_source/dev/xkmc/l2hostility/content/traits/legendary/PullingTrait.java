package dev.xkmc.l2hostility.content.traits.legendary;

import dev.xkmc.l2hostility.init.data.LHConfig;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;

public class PullingTrait extends PushPullTrait {
   public PullingTrait(ChatFormatting style) {
      super(style);
   }

   @Override
   protected int getRange() {
      return (Integer)LHConfig.SERVER.pullingRange.get();
   }

   @Override
   protected double getStrength(double dist) {
      return (1.0 - dist) * dist * (Double)LHConfig.SERVER.pullingStrength.get() * -4.0;
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc", new Object[]{Component.literal(LHConfig.SERVER.pullingRange.get() + "").withStyle(ChatFormatting.AQUA)}
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
