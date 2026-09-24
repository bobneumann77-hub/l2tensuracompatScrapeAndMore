package io.github.manasmods.tensura.ability.magic.aspectual.space;

import com.mojang.datafixers.util.Pair;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.menu.SpatialBagMenu;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.network.s2c.OpenSpatialStorageMenuPayload;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SpatialStorageMagic extends AspectualMagic implements ISpatialStorage {
   private static final AspectualMagicConfig.SpatialStorage CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).SpatialStorage;

   public SpatialStorageMagic() {
      super(AspectualMagic.AspectualType.SPACE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? (instance.isMastered(entity) ? 1 : -1) : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return mode == 1 ? "spatial_storage.dress" : "spatial_storage.bag";
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.SPACE,
            entity,
            instance.getOrCreateTag(),
            0.75F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            if (mode == 1 && instance.isMastered(entity)) {
               SpatialStorageContainer container = this.getSpatialStorage(instance, entity.registryAccess());

               for (int i = 1; i <= 4; i++) {
                  ItemStack stack = container.getItem(container.getContainerSize() - i).copy();
                  EquipmentSlot slot = getSlotId(i);
                  container.setItem(container.getContainerSize() - i, entity.getItemBySlot(slot));
                  entity.setItemSlot(slot, stack);
               }

               this.saveContainer(instance, entity, container);
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            } else if (instance.isMastered(entity) && !entity.isShiftKeyDown()) {
               this.openSpatialStorage(entity, instance);
            } else {
               if (entity instanceof Player player) {
                  ItemStack stack = ((Item)TensuraMaterialItems.SPATIAL_BAG.get()).getDefaultInstance();
                  stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), this.getRegistryName());
                  stack.set((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), true);
                  player.addItem(stack);
               }

               instance.addMasteryPoint(entity);
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
         }
      }
   }

   @Override
   public void openSpatialStoragePage(ServerPlayer player, LivingEntity owner, ManasSkillInstance instance, int page) {
      player.nextContainerCounter();
      ManasSkill skill = instance.getSkill();
      SpatialStorageContainer container = this.getSpatialStorage(instance, owner.registryAccess());
      NetworkManager.sendToPlayer(
         player,
         new OpenSpatialStorageMenuPayload(
            OpenSpatialStorageMenuPayload.StorageType.SPATIAL_BAG,
            player.containerCounter,
            container.getContainerSize(),
            container.getMaxStackSize(),
            page,
            owner.getId(),
            skill.getRegistryName()
         )
      );
      player.containerMenu = new SpatialBagMenu(player.containerCounter, player.getInventory(), owner, container, skill, instance.isMastered(owner), page);
      player.initMenu(player.containerMenu);
   }

   @NotNull
   @Override
   public SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance, Provider provide) {
      boolean mastered = instance.getMastery() >= this.getMaxMastery();
      SpatialStorageContainer container = new SpatialStorageContainer(
         mastered ? CONFIG.spatialSlots + 4 : CONFIG.spatialSlots, mastered ? CONFIG.spatialStackMastered : CONFIG.spatialStack
      );
      container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10), provide);
      return container;
   }

   public static EquipmentSlot getSlotId(int id) {
      return switch (id) {
         case 1 -> EquipmentSlot.FEET;
         case 2 -> EquipmentSlot.LEGS;
         case 3 -> EquipmentSlot.CHEST;
         default -> EquipmentSlot.HEAD;
      };
   }
}
