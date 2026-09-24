package dev.xkmc.l2hostility.content.item.curio.core;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Defence;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Offence;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2library.util.GenericItemStack;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import top.theillusivec4.curios.api.SlotContext;

public class CurseCurioItem extends MultiSlotItem {
   public static List<GenericItemStack<CurseCurioItem>> getFromPlayer(LivingEntity player) {
      List<ItemStack> list = CurioCompat.getItems(player, ex -> ex.getItem() instanceof CurseCurioItem);
      Map<CurseCurioItem, GenericItemStack<CurseCurioItem>> ans = new LinkedHashMap<>();

      for (ItemStack e : list) {
         if (e.getItem() instanceof CurseCurioItem item) {
            ans.put(item, new GenericItemStack(item, e));
         }
      }

      return new ArrayList<>(ans.values());
   }

   public CurseCurioItem(Properties props) {
      super(props);
   }

   public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation uuid, ItemStack stack) {
      Multimap<Holder<Attribute>, AttributeModifier> map = LinkedHashMultimap.create();
      int lv = this.getExtraLevel();
      if (lv > 0) {
         map.put(LHMiscs.ADD_LEVEL, new AttributeModifier(uuid, lv, Operation.ADD_VALUE));
      }

      return map;
   }

   public int getExtraLevel() {
      return 0;
   }

   public double getLootFactor(ItemStack stack, PlayerDifficulty player, MobTraitCap mob) {
      return 1.0;
   }

   public double getGrowFactor(ItemStack stack, PlayerDifficulty player, MobTraitCap mob) {
      return 1.0;
   }

   public boolean reflectTrait(MobTrait trait) {
      return false;
   }

   public void onHurtTarget(ItemStack stack, LivingEntity user, Offence cache) {
   }

   public void onDamage(ItemStack stack, LivingEntity user, Defence event) {
   }

   protected ResourceLocation getID() {
      return BuiltInRegistries.ITEM.getKey(this);
   }
}
