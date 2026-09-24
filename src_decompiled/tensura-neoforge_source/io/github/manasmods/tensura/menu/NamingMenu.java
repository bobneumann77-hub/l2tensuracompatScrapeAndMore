package io.github.manasmods.tensura.menu;

import io.github.manasmods.tensura.config.entity.PlayerConfig;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import java.util.List;
import lombok.Generated;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class NamingMenu extends AbstractContainerMenu {
   public static PlayerConfig.Naming CONFIG = ReincarnationMenu.PLAYER_CONFIG.Naming;
   public static List<String> RANDOM_NAMES = CONFIG.randomNames;
   private LivingEntity entity;

   public NamingMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
      this(pContainerId, inventory, inventory.player, buf.readInt());
   }

   public NamingMenu(int pContainerId, Inventory inventory, Player player, int id) {
      super((MenuType)TensuraMenuTypes.NAMING_MENU.get(), pContainerId);
      this.entity = (LivingEntity)inventory.player.level().getEntity(id);
   }

   public boolean stillValid(Player pPlayer) {
      return true;
   }

   public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
      return ItemStack.EMPTY;
   }

   public static String getRandomName(RandomSource random) {
      return CONFIG.randomNames.get(random.nextInt(0, CONFIG.randomNames.size()));
   }

   @Generated
   public LivingEntity getEntity() {
      return this.entity;
   }
}
