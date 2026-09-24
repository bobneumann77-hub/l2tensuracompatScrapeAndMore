package dev.xkmc.l2hostility.content.traits.highlevel;

import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Offence;
import dev.xkmc.l2hostility.content.item.traits.ReprintHandler;
import dev.xkmc.l2hostility.content.logic.TraitEffectCache;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHEnchantments;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;

public class ReprintTrait extends MobTrait {
   public ReprintTrait(ChatFormatting format) {
      super(format);
   }

   @Override
   public void onHurtTarget(int level, LivingEntity attacker, Offence cache, TraitEffectCache traitCache) {
      long total = 0L;
      int maxLv = 0;
      RegistryLookup<Enchantment> access = attacker.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack dst = attacker.getItemBySlot(slot);
         ItemStack src = cache.getTarget().getItemBySlot(slot);
         ItemEnchantments targetEnch = src.getAllEnchantments(access);

         for (Entry<Holder<Enchantment>> e : targetEnch.entrySet()) {
            int lv = e.getIntValue();
            maxLv = Math.max(maxLv, lv);
            if (lv >= 30) {
               total = -1L;
            } else if (total >= 0L) {
               total += 1L << lv - 1;
            }
         }

         if (cache.getSource().getDirectEntity() == attacker) {
            ReprintHandler.reprint(attacker.level().registryAccess(), dst, src);
         }
      }

      int bypass = (Integer)LHConfig.SERVER.reprintBypass.get();
      if (maxLv >= bypass) {
         ItemStack weapon = attacker.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!weapon.isEmpty() && (weapon.isEnchanted() || weapon.isEnchantable()) && weapon.isPrimaryItemFor(LCEnchantments.VOID_TOUCH.holder())) {
            Mutable map = new Mutable(weapon.getAllEnchantments(access));
            map.set(LCEnchantments.VOID_TOUCH.holder(), 20);
            map.set(LHEnchantments.VANISH.holder(), 1);
            EnchantmentHelper.setEnchantments(weapon, map.toImmutable());
         }
      }

      float factor = total >= 0L ? (float)total : (float)Math.pow(2.0, maxLv - 1);
      cache.addHurtModifier(DamageModifier.multTotal(1.0F + (float)((Double)LHConfig.SERVER.reprintDamage.get() * factor), this.getRegistryName()));
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc",
               new Object[]{
                  this.mapLevel(
                     access,
                     i -> Component.literal(Math.round((Double)LHConfig.SERVER.reprintDamage.get() * i.intValue() * 100.0) + "%")
                        .withStyle(ChatFormatting.AQUA)
                  )
               }
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
