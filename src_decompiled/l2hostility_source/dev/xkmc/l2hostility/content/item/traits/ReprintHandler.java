package dev.xkmc.l2hostility.content.item.traits;

import dev.xkmc.l2hostility.init.data.LHTagGen;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;

public class ReprintHandler {
   public static void reprint(RegistryAccess access, ItemStack dst, ItemStack src) {
      if ((dst.isEnchanted() || dst.isEnchantable()) && src.isEnchanted()) {
         RegistryLookup<Enchantment> reg = access.lookupOrThrow(Registries.ENCHANTMENT);
         ItemEnchantments selfEnch = dst.getAllEnchantments(reg);
         ItemEnchantments targetEnch = src.getAllEnchantments(reg);
         Map<Holder<Enchantment>, Integer> newEnch = new LinkedHashMap<>();

         for (Entry<Holder<Enchantment>> pair : targetEnch.entrySet()) {
            Holder<Enchantment> e = (Holder<Enchantment>)pair.getKey();
            if (!e.is(LHTagGen.NO_REPRINT) && dst.isPrimaryItemFor(e) && allow(newEnch, e)) {
               int lv = pair.getIntValue();
               newEnch.compute(e, (k, v) -> v == null ? lv : Math.max(v, lv));
            }
         }

         for (Entry<Holder<Enchantment>> pair : selfEnch.entrySet()) {
            Holder<Enchantment> e = (Holder<Enchantment>)pair.getKey();
            if (dst.isPrimaryItemFor(e) && allow(newEnch, e)) {
               int lv = pair.getIntValue();
               newEnch.compute(e, (k, v) -> v == null ? lv : Math.max(v, lv));
            }
         }

         Mutable builder = new Mutable(ItemEnchantments.EMPTY);

         for (java.util.Map.Entry<Holder<Enchantment>, Integer> e : newEnch.entrySet()) {
            builder.set(e.getKey(), e.getValue());
         }

         EnchantmentHelper.setEnchantments(dst, builder.toImmutable());
      }
   }

   private static boolean allow(Map<Holder<Enchantment>, Integer> map, Holder<Enchantment> ench) {
      if (map.containsKey(ench)) {
         return true;
      }

      for (Holder<Enchantment> e : map.keySet()) {
         if (!Enchantment.areCompatible(e, ench)) {
            return false;
         }
      }

      return true;
   }
}
