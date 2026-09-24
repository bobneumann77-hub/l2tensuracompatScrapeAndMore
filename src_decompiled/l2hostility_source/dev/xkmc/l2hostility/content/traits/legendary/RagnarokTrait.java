package dev.xkmc.l2hostility.content.traits.legendary;

import dev.xkmc.l2core.events.SchedulerHandler;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.compat.curios.EntitySlotAccess;
import dev.xkmc.l2hostility.content.item.traits.SealedItem;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RagnarokTrait extends LegendaryTrait {
   private static boolean allowSeal(EntitySlotAccess access) {
      ItemStack stack = access.get();
      if (stack.isEmpty()) {
         return false;
      }

      if (stack.is((Item)LHItems.SEAL.get())) {
         return false;
      }

      if (stack.is(LHTagGen.NO_SEAL)) {
         return false;
      }

      if (!(Boolean)LHConfig.SERVER.ragnarokSealBackpack.get()) {
         ResourceLocation rl = BuiltInRegistries.ITEM.getKey(stack.getItem());
         if (rl.toString().contains("backpack")) {
            return false;
         }
      }

      return !LHConfig.SERVER.ragnarokSealSlotAdder.get() ? !CurioCompat.isSlotAdder(access) : true;
   }

   public RagnarokTrait(ChatFormatting format) {
      super(format);
   }

   @Override
   public void postHurtImpl(int level, LivingEntity attacker, LivingEntity target) {
      SchedulerHandler.schedule(() -> this.sealItems(level, target));
   }

   public void sealItems(int level, LivingEntity target) {
      List<EntitySlotAccess> list = new ArrayList<>(CurioCompat.getItemAccess(target).stream().filter(RagnarokTrait::allowSeal).toList());
      int count = Math.min(level, list.size());
      int time = (Integer)LHConfig.SERVER.ragnarokTime.get() * level;

      for (int i = 0; i < count; i++) {
         int index = target.getRandom().nextInt(list.size());
         EntitySlotAccess slot = list.remove(index);
         slot.modify(e -> SealedItem.sealItem(e, time));
      }
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc",
               new Object[]{
                  this.mapLevel(access, i -> Component.literal(i + "").withStyle(ChatFormatting.AQUA)),
                  this.mapLevel(
                     access, i -> Component.literal(Math.round((Integer)LHConfig.SERVER.ragnarokTime.get() * i / 20.0F) + "").withStyle(ChatFormatting.AQUA)
                  )
               }
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
