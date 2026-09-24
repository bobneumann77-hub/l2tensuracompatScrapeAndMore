package dev.xkmc.l2hostility.compat.kubejs;

import dev.xkmc.l2hostility.content.traits.base.AttributeTrait;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AttributeTraitBuilder extends AbstractTraitBuilder<AttributeTraitBuilder> {
   private final List<AttributeTrait.AttributeEntry> list = new ArrayList<>();

   public AttributeTraitBuilder(ResourceLocation id) {
      super(id);
   }

   public AttributeTraitBuilder attribute(String name, String attribute, double factor, String operation) {
      Operation op = switch (operation) {
         case "%", "+%", "base", "BASE", "mult_base", "MULT_BASE", "ADD_MULTIPLIED_BASE", "multiply_base", "MULTIPLY_BASE" -> Operation.ADD_MULTIPLIED_BASE;
         case "*", "x", "*%", "x%", "total", "TOTAL", "mult_total", "MULT_TOTAL", "ADD_MULTIPLIED_TOTAL", "multiply_total", "MULTIPLY_TOTAL" -> Operation.ADD_MULTIPLIED_TOTAL;
         default -> Operation.ADD_VALUE;
      };
      this.list
         .add(
            new AttributeTrait.AttributeEntry(
               name, (Holder<Attribute>)BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(attribute)).orElseThrow(), () -> factor, op
            )
         );
      return this;
   }

   public MobTrait createObject() {
      if (this.color == null) {
         this.color(ChatFormatting.BLUE);
      }

      return new AttributeTrait(this.color, this.list.toArray(AttributeTrait.AttributeEntry[]::new));
   }
}
