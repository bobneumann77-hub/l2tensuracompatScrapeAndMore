package dev.xkmc.l2hostility.events;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.util.Proxy;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class ClientGlowingHandler {
   private static int cacheTick;
   private static boolean cacheGlass;

   public static boolean isGlowing(Entity entity) {
      if (!(entity instanceof LivingEntity le)) {
         return false;
      } else {
         if (le instanceof Mob mob && mob.getTags().contains("HostilityGlowing")) {
            Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob);
            if (opt.isPresent()) {
               MobTraitCap cap = opt.get();
               if (cap.isSummoned() || cap.isMasterProtected()) {
                  return true;
               }
            }
         }

         return le.level().isClientSide() ? isGlowingImpl(le) : false;
      }
   }

   private static boolean playerHasGlass(Player player) {
      if (player.tickCount == cacheTick) {
         return cacheGlass;
      }

      cacheGlass = CurioCompat.hasItemInCurioOrSlot(player, (Item)LHItems.DETECTOR_GLASSES.get());
      cacheTick = player.tickCount;
      return cacheGlass;
   }

   private static boolean isGlowingImpl(LivingEntity entity) {
      Player player = Proxy.getPlayer();
      if (player != null && playerHasGlass(player)) {
         boolean glow = entity.isInvisible() || entity.isInvisibleTo(player);
         glow |= player.hasEffect(MobEffects.BLINDNESS);
         glow |= player.hasEffect(MobEffects.DARKNESS);
         float hidden = ((Integer)LHConfig.CLIENT.glowingRangeHidden.get()).intValue() + entity.getBbWidth() * 2.0F;
         float near = ((Integer)LHConfig.CLIENT.glowingRangeNear.get()).intValue() + entity.getBbWidth() * 2.0F;
         double distSqr = entity.distanceToSqr(player);
         return distSqr <= near * near || glow && distSqr <= hidden * hidden;
      } else {
         return false;
      }
   }

   @Nullable
   public static Integer getColor(Entity entity) {
      if (entity instanceof Mob mob) {
         Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob);
         if (opt.isPresent()) {
            MobTraitCap cap = opt.get();
            if (cap.isSummoned()) {
               return 16711680;
            }

            if (cap.isMasterProtected()) {
               return 16755200;
            }
         }
      }

      return null;
   }
}
