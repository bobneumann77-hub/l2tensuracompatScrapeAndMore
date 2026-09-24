package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.item.misc.ElementCoreItem;
import io.github.manasmods.tensura.network.c2s.RequestNamingMenuPacket;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IElementalSpirit extends INameEvolution, ISpiritual {
   Element getElemental();

   SpiritualMagic.SpiritLevel getSpiritLevel();

   default boolean convertElementalCore(LivingEntity spirit, Player player, InteractionHand hand, Item magicCore) {
      ItemStack stack = player.getItemInHand(hand);
      if (stack.getItem() instanceof ElementCoreItem core) {
         if (core.equals(magicCore) && stack.getDamageValue() <= 0) {
            return false;
         }

         double cost = TensuraBehaviourHelper.CONFIG.MobSpecific.elementalCoreCost;
         if (TensuraStorages.getExistenceFrom(spirit).getMagicule() < cost) {
            spirit.level().playSound(null, spirit, (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            return true;
         }

         if (EnergyHelper.drainEnergy(spirit, player, cost, false, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NONE)) {
            player.setItemInHand(hand, new ItemStack(magicCore));
            spirit.level().playSound(null, spirit, (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   default void onPreNamed(
      IExistence existence, Player owner, Changeable<Double> epGain, Changeable<Double> cost, RequestNamingMenuPacket.NamingType namingType, String name
   ) {
      if (existence.getAlignment() == Alignment.DEFAULT) {
         if (TensuraStorages.getExistenceFrom(owner).getAlignment() == Alignment.MAJIN) {
            existence.setAlignment(Alignment.MAJIN);
         }
      }
   }
}
