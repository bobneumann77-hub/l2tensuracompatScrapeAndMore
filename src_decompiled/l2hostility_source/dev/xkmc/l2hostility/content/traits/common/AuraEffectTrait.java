package dev.xkmc.l2hostility.content.traits.common;

import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.l2hostility.content.capability.mob.PerformanceConstants;
import dev.xkmc.l2hostility.content.item.curio.misc.Abrahadabra;
import dev.xkmc.l2hostility.content.item.curio.ring.RingOfReflection;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AuraEffectTrait extends MobTrait {
   private final Holder<MobEffect> eff;

   public AuraEffectTrait(Holder<MobEffect> eff) {
      super(() -> ((MobEffect)eff.value()).getColor());
      this.eff = eff;
   }

   protected boolean canApply(LivingEntity e) {
      return ((RingOfReflection)LHItems.RING_REFLECTION.get()).isOn(e) ? false : !((Abrahadabra)LHItems.ABRAHADABRA.get()).isOn(e);
   }

   @Override
   public void tick(LivingEntity mob, int level) {
      int range = (Integer)LHConfig.SERVER.range.get(this.getRegistryName().getPath()).get();
      if (!mob.level().isClientSide() && mob.tickCount % PerformanceConstants.auraApplyInterval() == 0) {
         AABB box = mob.getBoundingBox().inflate(range);

         for (LivingEntity e : mob.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if (!(e instanceof Player pl && pl.getAbilities().instabuild) && !(e.distanceTo(mob) > range) && this.canApply(e)) {
               EffectUtil.refreshEffect(e, new MobEffectInstance(this.eff, 40, level - 1, true, true), mob);
            }
         }
      }

      if (mob.level().isClientSide()) {
         Vec3 center = mob.position();
         float tpi = (float) (Math.PI * 2);
         Vec3 v0 = new Vec3(0.0, range, 0.0);
         v0 = v0.xRot(tpi / 4.0F).yRot(mob.getRandom().nextFloat() * tpi);
         int k = ((MobEffect)this.eff.value()).getColor();
         mob.level()
            .addAlwaysVisibleParticle(
               ParticleTypes.EFFECT,
               center.x + v0.x,
               center.y + v0.y + 0.5,
               center.z + v0.z,
               (k >> 16 & 0xFF) / 255.0,
               (k >> 8 & 0xFF) / 255.0,
               (k & 0xFF) / 255.0
            );
      }
   }
}
