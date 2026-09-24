package dev.xkmc.l2hostility.content.item.curio.misc;

import dev.xkmc.l2core.util.DCStack;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.compat.curios.EntitySlotAccess;
import dev.xkmc.l2hostility.content.item.curio.core.SingletonItem;
import dev.xkmc.l2hostility.content.item.traits.SealedItem;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import org.apache.commons.lang3.function.Consumers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class PocketOfRestoration extends SingletonItem implements ICurioItem {
   public static void setData(ItemStack stack, ItemStack sealed, String id, long time) {
      DCStack data = (DCStack)LHItems.DC_SEAL_STACK.get(sealed);
      if (data != null) {
         LHItems.DC_SEAL_STACK.set(stack, data);
         LHItems.DC_SEAL_TIME.set(stack, (Integer)LHItems.DC_SEAL_TIME.getOrDefault(sealed, 0));
         LHItems.DC_UNSEAL_START.set(stack, time);
         LHItems.DC_UNSEAL_SLOT.set(stack, id);
      }
   }

   public PocketOfRestoration(Properties properties, int durability) {
      super(properties, durability);
   }

   public void curioTick(SlotContext slotContext, ItemStack stack) {
      LivingEntity le = slotContext.entity();
      if (le.level() instanceof ServerLevel sl) {
         if (slotContext.entity().isAlive()) {
            List<EntitySlotAccess> list = CurioCompat.getItemAccess(le);
            DCStack seal = (DCStack)LHItems.DC_SEAL_STACK.get(stack);
            if (seal == null) {
               if (stack.getDamageValue() + 1 < stack.getMaxDamage()) {
                  for (EntitySlotAccess e : list) {
                     if (e.get().getItem() instanceof SealedItem) {
                        ItemStack item = e.get();
                        e.set(ItemStack.EMPTY);
                        String id = e.getID();
                        long time = le.level().getGameTime();
                        stack.hurtAndBreak(1, sl, le, Consumers.nop());
                        setData(stack, item, id, time);
                        return;
                     }
                  }
               }
            } else {
               long time = (Long)LHItems.DC_UNSEAL_START.getOrDefault(stack, 0L);
               int dur = (Integer)LHItems.DC_SEAL_TIME.getOrDefault(stack, 0);
               String str = (String)LHItems.DC_UNSEAL_SLOT.get(stack);
               if (le.level().getGameTime() >= time + dur && str != null) {
                  ItemStack result = seal.stack();
                  EntitySlotAccess slot = CurioCompat.decode(str, le);
                  if (slot != null && slot.get().isEmpty()) {
                     slot.set(result);
                     stack.remove(LHItems.DC_SEAL_STACK);
                     stack.remove(LHItems.DC_SEAL_TIME);
                     stack.remove(LHItems.DC_UNSEAL_START);
                     stack.remove(LHItems.DC_UNSEAL_SLOT);
                  } else if (le instanceof Player player && player.addItem(result)) {
                     stack.remove(LHItems.DC_SEAL_STACK);
                     stack.remove(LHItems.DC_SEAL_TIME);
                     stack.remove(LHItems.DC_UNSEAL_START);
                     stack.remove(LHItems.DC_UNSEAL_SLOT);
                  }
               }
            }
         }
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(LangData.POCKET_OF_RESTORATION.get().withStyle(ChatFormatting.GOLD));
      DCStack seal = (DCStack)LHItems.DC_SEAL_STACK.get(stack);
      if (seal != null) {
         list.add(LangData.TOOLTIP_SEAL_DATA.get().withStyle(ChatFormatting.GRAY));
         list.add(seal.stack().getHoverName());
      }
   }

   @Override
   public boolean allowDuplicate() {
      return true;
   }
}
