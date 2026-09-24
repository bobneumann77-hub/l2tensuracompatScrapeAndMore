package dev.xkmc.l2hostility.content.traits.base;

import dev.xkmc.l2hostility.content.logic.TraitManager;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.TooltipFlag;

public class AttributeTrait extends MobTrait {
   private final AttributeTrait.AttributeEntry[] entries;

   public AttributeTrait(ChatFormatting style, AttributeTrait.AttributeEntry... entries) {
      super(style);
      this.entries = entries;
   }

   public AttributeTrait(IntSupplier color, AttributeTrait.AttributeEntry... entries) {
      super(color);
      this.entries = entries;
   }

   @Override
   public void initialize(LivingEntity le, int level) {
      for (AttributeTrait.AttributeEntry e : this.entries) {
         TraitManager.addAttribute(le, e.attribute, e.name(), e.factor.getAsDouble() * level, e.op());
      }
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      for (AttributeTrait.AttributeEntry e : this.entries) {
         double val = e.factor.getAsDouble();
         if (val != 0.0) {
            list.add(
               this.mapLevel(
                     access,
                     i -> ((Attribute)e.attribute().value()).toValueComponent(e.op, val * i.intValue(), TooltipFlag.NORMAL).withStyle(ChatFormatting.AQUA)
                  )
                  .append(CommonComponents.SPACE)
                  .append(Component.translatable(((Attribute)e.attribute.value()).getDescriptionId()).withStyle(ChatFormatting.BLUE))
            );
         }
      }
   }

   public record AttributeEntry(String name, Holder<Attribute> attribute, DoubleSupplier factor, Operation op) {
   }
}
