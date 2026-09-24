package io.github.manasmods.tensura.neoforge.handler;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.ChorusFruit;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderEntity;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.EnderPearl;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.SpreadPlayersCommand;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent.TeleportCommand;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;

@EventBusSubscriber(modid = "tensura")
public class CommonEventHandlers {
   @SubscribeEvent(priority = EventPriority.HIGHEST)
   private static void onEffectRemoved(Remove event) {
      if (event.getCure() == EffectCures.MILK && event.getEffect().is(TensuraTags.MobEffects.INCURABLE_BY_MILK)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onPostDamage(Post e) {
      if (e.getNewDamage() != 0.0F) {
         ((TensuraEntityEvents.LivingPostDamageEvent)TensuraEntityEvents.LIVING_POST_DAMAGE.invoker()).damage(e.getEntity(), e.getSource(), e.getNewDamage());
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   private static void onShieldBlock(LivingShieldBlockEvent event) {
      DamageSource source = event.getDamageSource();
      if (event.getBlocked() && source.getDirectEntity() instanceof LivingEntity attacker) {
         int level = TensuraEnchantmentHelper.getEnchantmentLevel(attacker.level(), TensuraEnchantments.INTANGIBILITY, attacker.getMainHandItem());
         if (level > 0) {
            event.setBlocked(false);
         }
      } else if (!event.getBlocked()) {
         LivingEntity entity = event.getEntity();
         if (source.is(DamageTypeTags.BYPASSES_SHIELD)) {
            return;
         }

         if (source.getDirectEntity() instanceof AbstractArrow abstractarrow && abstractarrow.getPierceLevel() > 0) {
            return;
         }

         if (!entity.isUsingItem()) {
            return;
         }

         ItemStack useItem = entity.getUseItem();
         if (!useItem.is(TensuraItemTags.SHIELDS)) {
            return;
         }

         if (useItem.getUseDuration(entity) - entity.getUseItemRemainingTicks() < 5) {
            return;
         }

         Vec3 sourcePosition = source.getSourcePosition();
         if (sourcePosition != null) {
            Vec3 vector = sourcePosition.vectorTo(entity.position());
            vector = new Vec3(vector.x, 0.0, vector.z).normalize();
            if (vector.dot(entity.calculateViewVector(0.0F, entity.getYHeadRot())) < 0.0) {
               event.setShieldDamage(1.0F);
               event.setBlocked(true);
            }
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.NORMAL)
   private static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      ((TensuraEntityEvents.EquipmentChangeEvent)TensuraEntityEvents.EQUIPMENT_CHANGE_EVENT.invoker())
         .change(event.getEntity(), event.getFrom(), event.getTo(), event.getSlot());
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onTeleportEnder(EnderEntity e) {
      Changeable<Vec3> position = Changeable.of(e.getTarget());
      if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
         .transmission(e.getEntity(), e.getEntity(), position, WarpPoint.TransmissionType.ENDER)
         .isFalse()) {
         e.setCanceled(true);
      }

      e.setTargetX(((Vec3)position.get()).x());
      e.setTargetY(((Vec3)position.get()).y());
      e.setTargetZ(((Vec3)position.get()).z());
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onTeleportPearl(EnderPearl e) {
      Changeable<Vec3> position = Changeable.of(e.getTarget());
      if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
         .transmission(e.getEntity(), null, position, WarpPoint.TransmissionType.PEARL)
         .isFalse()) {
         e.setCanceled(true);
      }

      e.setTargetX(((Vec3)position.get()).x());
      e.setTargetY(((Vec3)position.get()).y());
      e.setTargetZ(((Vec3)position.get()).z());
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onTeleportChorus(ChorusFruit e) {
      Changeable<Vec3> position = Changeable.of(e.getTarget());
      if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
         .transmission(e.getEntity(), null, position, WarpPoint.TransmissionType.CHORUS)
         .isFalse()) {
         e.setCanceled(true);
      }

      e.setTargetX(((Vec3)position.get()).x());
      e.setTargetY(((Vec3)position.get()).y());
      e.setTargetZ(((Vec3)position.get()).z());
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onTeleportCommand(TeleportCommand e) {
      Changeable<Vec3> position = Changeable.of(e.getTarget());
      if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
         .transmission(e.getEntity(), null, position, WarpPoint.TransmissionType.COMMANDS)
         .isFalse()) {
         e.setCanceled(true);
      }

      e.setTargetX(((Vec3)position.get()).x());
      e.setTargetY(((Vec3)position.get()).y());
      e.setTargetZ(((Vec3)position.get()).z());
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onTeleportSpread(SpreadPlayersCommand e) {
      Changeable<Vec3> position = Changeable.of(e.getTarget());
      if (((TensuraEntityEvents.SpatialMovementEvent)TensuraEntityEvents.INSTANT_TRANSMISSION_EVENT.invoker())
         .transmission(e.getEntity(), null, position, WarpPoint.TransmissionType.COMMANDS)
         .isFalse()) {
         e.setCanceled(true);
      }

      e.setTargetX(((Vec3)position.get()).x());
      e.setTargetY(((Vec3)position.get()).y());
      e.setTargetZ(((Vec3)position.get()).z());
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onChangingDimension(EntityTravelToDimensionEvent e) {
      if (((TensuraEntityEvents.DimensionTravelEvent)TensuraEntityEvents.DIMENSION_TRAVEL_EVENT.invoker())
         .travel(e.getEntity(), null, e.getDimension())
         .isFalse()) {
         e.setCanceled(true);
      }
   }
}
