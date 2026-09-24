package dev.xkmc.l2hostility.compat.kubejs;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2serial.util.Wrappers;
import java.util.function.IntSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractTraitBuilder<T extends AbstractTraitBuilder<T>> extends BuilderBase<MobTrait> {
   protected IntSupplier color;

   public AbstractTraitBuilder(ResourceLocation id) {
      super(id);
   }

   public T self() {
      return (T)Wrappers.cast(this);
   }

   public T color(ChatFormatting chat) {
      this.color = chat::getColor;
      return this.self();
   }

   public T colorRGB(int color) {
      this.color = () -> color;
      return this.self();
   }
}
