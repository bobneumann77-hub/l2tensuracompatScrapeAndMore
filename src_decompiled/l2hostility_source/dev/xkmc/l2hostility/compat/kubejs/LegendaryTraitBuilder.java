package dev.xkmc.l2hostility.compat.kubejs;

import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.content.traits.legendary.LegendaryTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public class LegendaryTraitBuilder extends AbstractTraitBuilder<LegendaryTraitBuilder> {
   public LegendaryTraitBuilder(ResourceLocation id) {
      super(id);
   }

   public MobTrait createObject() {
      if (this.color == null) {
         this.color(ChatFormatting.WHITE);
      }

      return new LegendaryTrait(this.color);
   }
}
