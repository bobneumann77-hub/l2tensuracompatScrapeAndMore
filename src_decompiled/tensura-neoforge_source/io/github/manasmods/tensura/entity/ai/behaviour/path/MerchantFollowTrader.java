package io.github.manasmods.tensura.entity.ai.behaviour.path;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.trading.Merchant;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;

public class MerchantFollowTrader<E extends PathfinderMob & Merchant> extends FollowEntity<E, LivingEntity> {
   public MerchantFollowTrader() {
      this.speedMod(1.2F);
      this.startCondition(entity -> ((Merchant)entity).getTradingPlayer() != null);
      this.following(rec$ -> ((Merchant)rec$).getTradingPlayer());
      this.stopFollowingWithin((entity, owner) -> Math.max(3.0 * entity.getBbWidth(), 4.0));
   }
}
