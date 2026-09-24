package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import lombok.Generated;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class ElementCoreItem extends Item {
   private final Element element;

   public ElementCoreItem() {
      this(Element.UNIDENTIFIED);
   }

   public ElementCoreItem(Element element) {
      super(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant().durability(500));
      this.element = element;
   }

   @Generated
   public Element getElement() {
      return this.element;
   }
}
