package dev.xkmc.l2hostility.events;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.util.DCStack;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.item.consumable.BookCopy;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.registrate.LHEffects;
import dev.xkmc.l2hostility.init.registrate.LHEnchantments;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.mob_weapon_api.example.vanilla.VanillaMobManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

@EventBusSubscriber(modid = "l2hostility", bus = Bus.GAME)
public class MiscHandlers {
   public static void copyCap(LivingEntity self, LivingEntity sub) {
      if (((GeneralCapabilityHolder)LHMiscs.MOB.type()).isProper(self) && ((GeneralCapabilityHolder)LHMiscs.MOB.type()).isProper(sub)) {
         MobTraitCap selfCap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(self);
         MobTraitCap subCap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(sub);
         subCap.copyFrom(self, sub, selfCap);
      }
   }

   @SubscribeEvent
   public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
      Entity e = event.getEntity();
      if (e instanceof ItemEntity ie && ie.getItem().getEnchantmentLevel(LHEnchantments.VANISH.holder()) > 0) {
         event.setCanceled(true);
      }

      if (e instanceof PathfinderMob mob && e.getTags().contains("mob_weapon_api_applied")) {
         ItemStack stack = mob.getMainHandItem();
         VanillaMobManager.attachGoal(mob, stack);
      }
   }

   @SubscribeEvent
   public static void onAnvilCraft(AnvilUpdateEvent event) {
      ItemStack copy = event.getLeft();
      ItemStack book = event.getRight();
      if (copy.getItem() instanceof BookCopy && book.getItem() instanceof EnchantedBookItem) {
         ItemEnchantments map = EnchantmentHelper.getEnchantmentsForCrafting(book);

         for (Holder<Enchantment> e : map.keySet()) {
            if (e.is(LHTagGen.NO_REPRINT)) {
               return;
            }
         }

         int cost = 0;

         for (Entry<Holder<Enchantment>> e : map.entrySet()) {
            cost += BookCopy.cost((Enchantment)((Holder)e.getKey()).value(), e.getIntValue());
         }

         ItemStack result = book.copy();
         if (!(Boolean)LHConfig.SERVER.bookOfReprintSpread.get()) {
            result.setCount(book.getCount() + copy.getCount());
         }

         event.setOutput(result);
         event.setMaterialCost(book.getCount());
         event.setCost(cost);
      }
   }

   @SubscribeEvent
   public static void onAnvilTake(AnvilRepairEvent event) {
      ItemStack copy = event.getLeft();
      ItemStack book = event.getRight();
      if (copy.getItem() instanceof BookCopy && book.getItem() instanceof EnchantedBookItem && (Boolean)LHConfig.SERVER.bookOfReprintSpread.get()) {
         for (int i = 0; i < copy.getCount(); i++) {
            ItemStack result = book.copy();
            result.setCount(1);
            event.getEntity().getInventory().placeItemBackInInventory(result);
         }
      }
   }

   public static boolean useOnSkip(UseOnContext ctx, ItemStack stack) {
      Player player = ctx.getPlayer();
      if (player == null) {
         return false;
      } else {
         return !ctx.getPlayer().hasEffect(LHEffects.ANTIBUILD) ? false : stack.getItem() instanceof BlockItem || stack.is(LHTagGen.ANTIBUILD_BAN);
      }
   }

   public static boolean predicateSlotValid(SlotContext slotContext, ItemStack stack) {
      DCStack sealed = (DCStack)LHItems.DC_SEAL_STACK.get(stack);
      return sealed == null ? false : CuriosApi.isStackValid(slotContext, sealed.stack());
   }
}
