package io.github.manasmods.tensura.util;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class MenuHelper {
   public static void sendComingSoonMessage(LivingEntity entity) {
      entity.sendSystemMessage(Component.translatable("tooltip.tensura.coming_soon"));
   }

   public static void sendComingSoonMessage(LivingEntity entity, String name) {
      entity.sendSystemMessage(Component.translatable("tooltip.tensura.coming_soon_feature", new Object[]{name}));
   }

   public static void dropDraggingItem(AbstractContainerMenu menu, Player player) {
      if (player instanceof ServerPlayer) {
         ItemStack itemStack = menu.getCarried();
         if (!itemStack.isEmpty()) {
            if (player.isAlive() && !((ServerPlayer)player).hasDisconnected()) {
               player.getInventory().placeItemBackInInventory(itemStack);
            } else {
               player.drop(itemStack, false);
            }

            menu.setCarried(ItemStack.EMPTY);
         }
      }
   }

   public static String toHumanString(Duration duration) {
      TimeUnit unit = getSmallestUnit(duration);
      long nanos = duration.toNanos();
      double value = (double)nanos / TimeUnit.NANOSECONDS.convert(1L, unit);
      return String.format(Locale.ROOT, "%.4g %s", value, unitToString(unit));
   }

   private static TimeUnit getSmallestUnit(Duration duration) {
      if (duration.toDays() > 0L) {
         return TimeUnit.DAYS;
      } else if (duration.toHours() > 0L) {
         return TimeUnit.HOURS;
      } else if (duration.toMinutes() > 0L) {
         return TimeUnit.MINUTES;
      } else if (duration.toSeconds() > 0L) {
         return TimeUnit.SECONDS;
      } else if (duration.toMillis() > 0L) {
         return TimeUnit.MILLISECONDS;
      } else {
         return duration.toNanos() > 1000L ? TimeUnit.MICROSECONDS : TimeUnit.NANOSECONDS;
      }
   }

   private static String unitToString(TimeUnit unit) {
      return unit.name().toLowerCase(Locale.ROOT);
   }
}
