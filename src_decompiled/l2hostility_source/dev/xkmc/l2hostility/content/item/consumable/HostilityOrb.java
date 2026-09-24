package dev.xkmc.l2hostility.content.item.consumable;

import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.chunk.SectionDifficulty;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class HostilityOrb extends Item {
   public HostilityOrb(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!(Boolean)LHConfig.SERVER.allowHostilityOrb.get()) {
         return InteractionResultHolder.pass(stack);
      }

      if (CurioCompat.hasItemInCurioOrSlot(player, (Item)LHItems.DETECTOR.get())
         && CurioCompat.hasItemInCurioOrSlot(player, (Item)LHItems.DETECTOR_GLASSES.get())) {
         if (!level.isClientSide()) {
            boolean success = false;
            int r = (Integer)LHConfig.SERVER.orbRadius.get();

            for (int x = -r; x <= r; x++) {
               for (int y = -r; y <= r; y++) {
                  for (int z = -r; z <= r; z++) {
                     BlockPos pos = player.blockPosition().offset(x * 16, y * 16, z * 16);
                     if (!level.isOutsideBuildHeight(pos)) {
                        Optional<ChunkCapHolder> opt = ChunkDifficulty.at(level, pos);
                        if (opt.isPresent()) {
                           SectionDifficulty sec = opt.get().getSection(pos.getY());
                           if (!sec.isCleared()) {
                              success = true;
                              sec.setClear(opt.get(), pos);
                           }
                        }
                     }
                  }
               }
            }

            if (success) {
               stack.shrink(1);
            }
         }

         return InteractionResultHolder.success(stack);
      } else {
         return InteractionResultHolder.pass(stack);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
      if ((Boolean)LHConfig.SERVER.allowHostilityOrb.get()) {
         int r = (Integer)LHConfig.SERVER.orbRadius.get() * 2 + 1;
         list.add(LangData.orbUse().withStyle(ChatFormatting.DARK_GREEN));
         list.add(LangData.ITEM_ORB.get(r, r, r).withStyle(ChatFormatting.GRAY));
      }
   }
}
