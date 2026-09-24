package io.github.manasmods.tensura.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.network.c2s.RequestNamingMenuPacket;
import io.github.manasmods.tensura.storage.player.WarpPoint;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TensuraEntityEvents {
   public static Event<TensuraEntityEvents.AddFreshEvent> ADD_FRESH = EventFactory.createEventResult(new TensuraEntityEvents.AddFreshEvent[0]);
   public static Event<TensuraEntityEvents.AttributeBaseValueChangedEvent> ATTRIBUTE_BASE_CHANGE_EVENT = EventFactory.createEventResult(
      new TensuraEntityEvents.AttributeBaseValueChangedEvent[0]
   );
   public static Event<TensuraEntityEvents.EnterSleepModeEvent> ENTER_SLEEP_MODE_EVENT = EventFactory.createEventResult(
      new TensuraEntityEvents.EnterSleepModeEvent[0]
   );
   public static Event<TensuraEntityEvents.EnterHarvestFestivalEvent> ENTER_HARVEST_FESTIVAL_EVENT = EventFactory.createEventResult(
      new TensuraEntityEvents.EnterHarvestFestivalEvent[0]
   );
   public static Event<TensuraEntityEvents.NamingEvent> NAMING_EVENT = EventFactory.createEventResult(new TensuraEntityEvents.NamingEvent[0]);
   public static Event<TensuraEntityEvents.PostTameEvent> POST_TAME_EVENT = EventFactory.createLoop(new TensuraEntityEvents.PostTameEvent[0]);
   public static Event<TensuraEntityEvents.AwakeningEvent> AWAKENING_EVENT = EventFactory.createEventResult(new TensuraEntityEvents.AwakeningEvent[0]);
   public static Event<TensuraEntityEvents.HarvestFestivalRewardEvent> HARVEST_FESTIVAL_REWARD_EVENT = EventFactory.createEventResult(
      new TensuraEntityEvents.HarvestFestivalRewardEvent[0]
   );
   public static Event<TensuraEntityEvents.EnergyDrainEvent> ENERGY_DRAIN_EVENT = EventFactory.createEventResult(new TensuraEntityEvents.EnergyDrainEvent[0]);
   public static Event<TensuraEntityEvents.ForceMovementEvent> FORCE_MOVEMENT_EVENT = EventFactory.createEventResult(
      new TensuraEntityEvents.ForceMovementEvent[0]
   );
   public static Event<TensuraEntityEvents.ForceTameEvent> FORCE_TAME_EVENT = EventFactory.createEventResult(new TensuraEntityEvents.ForceTameEvent[0]);
   public static Event<TensuraEntityEvents.PossessionEvent> POSSESSION_EVENT = EventFactory.createEventResult(new TensuraEntityEvents.PossessionEvent[0]);
   public static Event<TensuraEntityEvents.LivingPostDamageEvent> LIVING_POST_DAMAGE = EventFactory.createLoop(new TensuraEntityEvents.LivingPostDamageEvent[0]);
   public static Event<TensuraEntityEvents.SpiritualHurtEvent> SPIRITUAL_HURT_EVENT = EventFactory.createEventResult(
      new TensuraEntityEvents.SpiritualHurtEvent[0]
   );
   public static Event<TensuraEntityEvents.EngraveEvent> ENGRAVE_EVENT = EventFactory.createEventResult(new TensuraEntityEvents.EngraveEvent[0]);
   public static Event<TensuraEntityEvents.EquipmentChangeEvent> EQUIPMENT_CHANGE_EVENT = EventFactory.createLoop(
      new TensuraEntityEvents.EquipmentChangeEvent[0]
   );
   public static Event<TensuraEntityEvents.PreItemHurtEvent> PRE_ITEM_HURT_EVENT = EventFactory.createEventResult(new TensuraEntityEvents.PreItemHurtEvent[0]);
   public static Event<TensuraEntityEvents.DimensionTravelEvent> DIMENSION_TRAVEL_EVENT = EventFactory.createEventResult(
      new TensuraEntityEvents.DimensionTravelEvent[0]
   );
   public static Event<TensuraEntityEvents.SpatialMovementEvent> INSTANT_TRANSMISSION_EVENT = EventFactory.createEventResult(
      new TensuraEntityEvents.SpatialMovementEvent[0]
   );

   @FunctionalInterface
   public interface AddFreshEvent {
      EventResult add(Entity var1, LevelAccessor var2);
   }

   @FunctionalInterface
   public interface AttributeBaseValueChangedEvent {
      EventResult change(LivingEntity var1, AttributeInstance var2, double var3, double var5);
   }

   @FunctionalInterface
   public interface AwakeningEvent {
      EventResult awaken(LivingEntity var1, Changeable<Boolean> var2);
   }

   @FunctionalInterface
   public interface DimensionTravelEvent {
      EventResult travel(Entity var1, @Nullable Entity var2, ResourceKey<Level> var3);
   }

   @FunctionalInterface
   public interface EnergyDrainEvent {
      EventResult drain(
         LivingEntity var1,
         @Nullable Entity var2,
         Changeable<EnergyHelper.DrainType> var3,
         Changeable<EnergyHelper.GainType> var4,
         Changeable<Double> var5,
         Changeable<Boolean> var6
      );
   }

   @FunctionalInterface
   public interface EngraveEvent {
      EventResult engrave(LivingEntity var1, ItemStack var2, Changeable<Holder<Enchantment>> var3, Changeable<Integer> var4);
   }

   @FunctionalInterface
   public interface EnterHarvestFestivalEvent {
      EventResult enter(LivingEntity var1, Changeable<Integer> var2, Changeable<Integer> var3);
   }

   @FunctionalInterface
   public interface EnterSleepModeEvent {
      EventResult sleep(LivingEntity var1, Changeable<Integer> var2, Changeable<Boolean> var3);
   }

   @FunctionalInterface
   public interface EquipmentChangeEvent {
      void change(LivingEntity var1, ItemStack var2, ItemStack var3, EquipmentSlot var4);
   }

   @FunctionalInterface
   public interface ForceMovementEvent {
      EventResult move(Entity var1, @Nullable Entity var2, @Nullable ManasSkillInstance var3, Changeable<Vec3> var4);
   }

   @FunctionalInterface
   public interface ForceTameEvent {
      EventResult tame(LivingEntity var1, @Nullable Entity var2, boolean var3);
   }

   @FunctionalInterface
   public interface HarvestFestivalRewardEvent {
      EventResult reward(LivingEntity var1);
   }

   @FunctionalInterface
   public interface LivingPostDamageEvent {
      void damage(LivingEntity var1, DamageSource var2, float var3);
   }

   @FunctionalInterface
   public interface NamingEvent {
      EventResult name(
         LivingEntity var1,
         @Nullable Player var2,
         Changeable<Double> var3,
         Changeable<Double> var4,
         Changeable<RequestNamingMenuPacket.NamingType> var5,
         Changeable<String> var6
      );
   }

   @FunctionalInterface
   public interface PossessionEvent {
      EventResult possess(LivingEntity var1, @Nullable Entity var2);
   }

   @FunctionalInterface
   public interface PostTameEvent {
      void tame(LivingEntity var1, Player var2);
   }

   @FunctionalInterface
   public interface PreItemHurtEvent {
      EventResult hurt(ItemStack var1, LivingEntity var2, EquipmentSlot var3, Changeable<Integer> var4);
   }

   @FunctionalInterface
   public interface SpatialMovementEvent {
      EventResult transmission(Entity var1, @Nullable Entity var2, Changeable<Vec3> var3, WarpPoint.TransmissionType var4);
   }

   @FunctionalInterface
   public interface SpiritualHurtEvent {
      EventResult hurt(LivingEntity var1, @Nullable Entity var2, float var3, Changeable<Float> var4, Changeable<Float> var5, Changeable<DamageSource> var6);
   }
}
