package dev.xkmc.l2hostility.content.traits.common;

import dev.xkmc.l2damagetracker.contents.attack.DamageData.OffenceMax;
import dev.xkmc.l2hostility.content.item.curio.misc.Abrahadabra;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class GravityTrait extends AuraEffectTrait {
   public GravityTrait(Holder<MobEffect> eff) {
      super(eff);
   }

   @Override
   public void onHurtByMax(int level, LivingEntity mob, OffenceMax cache) {
      LivingEntity e = cache.getAttacker();
      if (e != null && !e.onGround()) {
         if (((Abrahadabra)LHItems.ABRAHADABRA.get()).isOn(e)) {
            return;
         }

         e.push(0.0, -level, 0.0);
         if (e instanceof ServerPlayer) {
            e.hurtMarked = true;
         }
      }
   }
}
