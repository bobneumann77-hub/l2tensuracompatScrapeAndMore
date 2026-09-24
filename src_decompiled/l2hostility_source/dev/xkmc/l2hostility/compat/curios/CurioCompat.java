package dev.xkmc.l2hostility.compat.curios;

import com.google.common.collect.Multimap;
import dev.xkmc.l2hostility.content.item.curio.core.EquipCurioItem;
import dev.xkmc.l2hostility.init.data.LHConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotAttribute;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class CurioCompat {
   private static final ResourceLocation DUMMY_ID = ResourceLocation.fromNamespaceAndPath("curios", "dummy_id");

   public static boolean hasItemInCurioOrSlot(LivingEntity player, Item item) {
      for (EquipmentSlot e : EquipmentSlot.values()) {
         if (player.getItemBySlot(e).is(item)) {
            return true;
         }
      }

      return hasItemInCurio(player, item);
   }

   public static boolean hasItemInCurioChecked(LivingEntity le, Item item) {
      if (!(LHConfig.SERVER.getSpec() instanceof ModConfigSpec spec && spec.isLoaded())) {
         return false;
      } else {
         return !LHConfig.SERVER.enableCurioCheckFilter.get() || !(le instanceof Enemy) && !(le instanceof Animal) ? hasItemInCurio(le, item) : false;
      }
   }

   public static boolean hasItemInCurio(LivingEntity player, Item item) {
      return ModList.get().isLoaded("curios") ? hasItemImpl(player, item) : false;
   }

   public static List<ItemStack> getItems(LivingEntity player, Predicate<ItemStack> pred) {
      List<ItemStack> ans = new ArrayList<>();

      for (EquipmentSlot e : EquipmentSlot.values()) {
         ItemStack stack = player.getItemBySlot(e);
         if (stack.getItem() instanceof EquipCurioItem && pred.test(stack)) {
            ans.add(stack);
         }
      }

      if (ModList.get().isLoaded("curios")) {
         getItemImpl(ans, player, pred);
      }

      return ans;
   }

   public static List<EntitySlotAccess> getItemAccess(LivingEntity player) {
      List<EntitySlotAccess> ans = new ArrayList<>();

      for (EquipmentSlot e : EquipmentSlot.values()) {
         ans.add(new EquipmentSlotAccess(player, e));
      }

      if (ModList.get().isLoaded("curios")) {
         getItemAccessImpl(ans, player);
      }

      return ans;
   }

   @Nullable
   public static EntitySlotAccess decode(String id, LivingEntity le) {
      try {
         String[] strs = id.split("/");
         if (strs[0].equals("equipment")) {
            return new EquipmentSlotAccess(le, EquipmentSlot.byName(strs[1]));
         }

         if (strs[0].equals("curios")) {
            Optional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(le);
            if (opt.isEmpty()) {
               return null;
            }

            Optional<ICurioStacksHandler> handler = opt.get().getStacksHandler(strs[1]);
            if (handler.isEmpty()) {
               return null;
            }

            int index = strs.length == 2 ? 0 : Integer.parseInt(strs[2]);
            return new CurioCompat.CurioSlotAccess(le, handler.get().getStacks(), index, strs[1]);
         }
      } catch (Exception var6) {
      }

      return null;
   }

   private static boolean hasItemImpl(LivingEntity player, Item item) {
      Optional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(player);
      return opt.isPresent() && opt.get().isEquipped(item);
   }

   private static void getItemImpl(List<ItemStack> list, LivingEntity player, Predicate<ItemStack> pred) {
      Optional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(player);
      if (!opt.isEmpty()) {
         for (ICurioStacksHandler e : opt.get().getCurios().values()) {
            for (int i = 0; i < e.getStacks().getSlots(); i++) {
               ItemStack stack = e.getStacks().getStackInSlot(i);
               if (pred.test(stack)) {
                  list.add(stack);
               }
            }
         }
      }
   }

   private static void getItemAccessImpl(List<EntitySlotAccess> list, LivingEntity player) {
      Optional<ICuriosItemHandler> opt = CuriosApi.getCuriosInventory(player);
      if (!opt.isEmpty()) {
         for (ICurioStacksHandler e : opt.get().getCurios().values()) {
            for (int i = 0; i < e.getStacks().getSlots(); i++) {
               list.add(new CurioCompat.CurioSlotAccess(player, e.getStacks(), i, e.getIdentifier()));
            }
         }
      }
   }

   public static boolean isSlotAdder(EntitySlotAccess access) {
      if (access instanceof CurioCompat.CurioSlotAccess slot) {
         ItemStack stack = access.get();
         Optional<ICurio> opt = CuriosApi.getCurio(stack);
         if (opt.isEmpty()) {
            return false;
         }

         Multimap<Holder<Attribute>, AttributeModifier> multimap = CuriosApi.getAttributeModifiers(
            new SlotContext(slot.id, slot.player, 0, false, true), DUMMY_ID, stack
         );

         for (Holder<Attribute> e : multimap.keySet()) {
            if (e.value() instanceof SlotAttribute) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private record CurioSlotAccess(LivingEntity player, IDynamicStackHandler handler, int slot, String id) implements EntitySlotAccess {
      @Override
      public ItemStack get() {
         return this.handler.getSlots() <= this.slot ? ItemStack.EMPTY : this.handler.getStackInSlot(this.slot);
      }

      @Override
      public void set(ItemStack stack) {
         if (this.handler.getSlots() <= this.slot) {
            if (this.player instanceof Player pl) {
               pl.getInventory().placeItemBackInInventory(stack);
            } else {
               this.player.spawnAtLocation(stack);
            }
         } else {
            this.handler.setStackInSlot(this.slot, stack);
         }
      }

      @Override
      public String getID() {
         return "curios/" + this.id + "/" + this.slot;
      }
   }
}
