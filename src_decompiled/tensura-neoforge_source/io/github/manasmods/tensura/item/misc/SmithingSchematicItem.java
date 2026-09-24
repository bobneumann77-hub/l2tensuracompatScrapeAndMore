package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class SmithingSchematicItem extends Item {
   public SmithingSchematicItem() {
      super(new Properties().rarity(Rarity.UNCOMMON).arch$tab(TensuraCreativeTabs.LEARNABLE).fireResistant().stacksTo(16));
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
      if (pPlayer instanceof ServerPlayer player) {
         ITensuraPlayer cap = TensuraStorages.getPlayerDataFrom(pPlayer);
         if (!cap.hasSchematic(itemStack)) {
            cap.unlockSchematic(itemStack);
            cap.markDirty();
            pPlayer.sendSystemMessage(Component.translatable("tensura.schematic.unlocked"));
            CriteriaTriggers.CONSUME_ITEM.trigger(player, itemStack);
            itemStack.shrink(1);
            boolean full = true;

            for (Item item : BuiltInRegistries.ITEM) {
               if (item.getDefaultInstance().is(TensuraItemTags.SCHEMATICS) && !cap.hasSchematic(item)) {
                  full = false;
                  break;
               }
            }

            if (full) {
               ((PlayerTrigger)TensuraCriteriaTriggers.LEARN_ALL_SCHEMATICS.get()).trigger(player);
            }
         }
      }

      return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
   }
}
