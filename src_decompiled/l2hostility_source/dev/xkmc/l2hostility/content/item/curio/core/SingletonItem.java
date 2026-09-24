package dev.xkmc.l2hostility.content.item.curio.core;

import java.util.Optional;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class SingletonItem extends Item implements ICurioItem {
   public SingletonItem(Properties properties) {
      super(properties.stacksTo(1));
   }

   public SingletonItem(Properties properties, int durability) {
      super(properties.durability(durability));
   }

   public boolean canEquip(SlotContext slotContext, ItemStack stack) {
      if (this.allowDuplicate()) {
         return true;
      }

      Optional<SlotResult> repeat = CuriosApi.getCuriosInventory(slotContext.entity()).flatMap(e -> e.findFirstCurio(this));
      if (repeat.isEmpty()) {
         return true;
      }

      SlotContext rep = repeat.get().slotContext();
      return rep.identifier().equals(slotContext.identifier()) && rep.index() == slotContext.index();
   }

   public boolean allowDuplicate() {
      return false;
   }
}
