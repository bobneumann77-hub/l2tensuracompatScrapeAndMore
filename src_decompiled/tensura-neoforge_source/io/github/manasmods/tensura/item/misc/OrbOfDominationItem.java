package io.github.manasmods.tensura.item.misc;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.item.tool.custom.DragonKnuckleItem;
import io.github.manasmods.tensura.network.s2c.DisplayTotemEffectPayload;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;

public class OrbOfDominationItem extends Item {
   public OrbOfDominationItem() {
      super(
         new Properties()
            .rarity(Rarity.EPIC)
            .arch$tab(TensuraCreativeTabs.GEARS)
            .fireResistant()
            .stacksTo(1)
            .attributes(DragonKnuckleItem.createAttributes(-0.8, -3.0F, 0.0))
      );
   }

   public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
      if (pAttacker instanceof Player player
         && (player.isCreative() || EnergyHelper.getMaxEP(pAttacker) < 800000.0)
         && !((TensuraEntityEvents.ForceTameEvent)TensuraEntityEvents.FORCE_TAME_EVENT.invoker()).tame(pTarget, player, false).isFalse()) {
         pTarget = ILivingPartEntity.checkForHead(pTarget);
         if (pTarget instanceof ISubordinate subordinate && !subordinate.isTame()) {
            subordinate.tame(player);
            subordinate.setOrderedToSit(true);
            subordinate.setInSittingPose(true);
            if (pTarget instanceof Mob mob) {
               mob.setTarget(null);
               mob.getNavigation().stop();
            }

            pTarget.level().broadcastEntityEvent(pTarget, (byte)7);
         }

         IExistence existence = TensuraStorages.getExistenceFrom(pTarget);
         existence.setTemporaryOwner(pAttacker.getUUID());
         existence.markDirty();
         if (player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new DisplayTotemEffectPayload(pStack.getItem().arch$registryName()));
         }

         if (!player.hasInfiniteMaterials()) {
            pStack.shrink(1);
         }
      }

      return super.hurtEnemy(pStack, pTarget, pAttacker);
   }
}
