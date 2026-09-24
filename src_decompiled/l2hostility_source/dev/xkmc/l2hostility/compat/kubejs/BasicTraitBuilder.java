package dev.xkmc.l2hostility.compat.kubejs;

import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public class BasicTraitBuilder extends AbstractTraitBuilder<BasicTraitBuilder> {
   public BasicTraitBuilder(ResourceLocation id) {
      super(id);
   }

   public MobTrait createObject() {
      if (this.color == null) {
         this.color(ChatFormatting.WHITE);
      }

      return new MobTrait(this.color);
   }
}
