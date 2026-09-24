package dev.xkmc.l2hostility.content.traits.legendary;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.item.curio.misc.Abrahadabra;
import dev.xkmc.l2hostility.content.logic.TraitEffectCache;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHDamageTypes;
import dev.xkmc.l2hostility.init.network.TraitEffectToClient;
import dev.xkmc.l2hostility.init.network.TraitEffects;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class KillerAuraTrait extends LegendaryTrait {
   public KillerAuraTrait(ChatFormatting format) {
      super(format);
   }

   @Override
   public double modifyBonusDamage(DamageSource source, double factor, int lv) {
      return source.is(LHDamageTypes.KILLER_AURA) ? (Double)LHConfig.SERVER.killerAuraDamageFactor.get() : 1.0;
   }

   @Override
   public void tick(LivingEntity mob, int level) {
      int itv = (Integer)LHConfig.SERVER.killerAuraInterval.get() / level;
      int damage = (Integer)LHConfig.SERVER.killerAuraDamage.get() * level;
      int range = (Integer)LHConfig.SERVER.killerAuraRange.get();
      if (!mob.level().isClientSide() && mob.tickCount % itv == 0) {
         MobTraitCap cap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(mob);
         AABB box = mob.getBoundingBox().inflate(range);

         for (LivingEntity e : mob.level().getEntitiesOfClass(LivingEntity.class, box)) {
            if ((
                  e instanceof Player pl && !pl.getAbilities().instabuild
                     || e instanceof Mob target && target.getTarget() == mob
                     || mob instanceof Mob mobmob && mobmob.getTarget() == e
               )
               && !(e.distanceTo(mob) > range)
               && !mob.isAlliedTo(e)
               && !((Abrahadabra)LHItems.ABRAHADABRA.get()).isOn(e)) {
               TraitEffectCache cache = new TraitEffectCache(e);
               cap.traitEvent((k, v) -> k.postHurtPlayer(v, mob, cache));
               e.hurt(new DamageSource(LHDamageTypes.forKey(mob.level(), LHDamageTypes.KILLER_AURA), null, mob), damage);
            }
         }

         L2Hostility.HANDLER.toTrackingPlayers(TraitEffectToClient.of(mob, this, TraitEffects.AURA), mob);
      }

      if (mob.level().isClientSide()) {
         Vec3 center = mob.position();
         float tpi = (float) (Math.PI * 2);
         Vec3 v0 = new Vec3(0.0, range, 0.0);
         v0 = v0.xRot(tpi / 4.0F).yRot(mob.getRandom().nextFloat() * tpi);
         mob.level().addAlwaysVisibleParticle(ParticleTypes.FLAME, center.x + v0.x, center.y + v0.y + 0.5, center.z + v0.z, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc",
               new Object[]{
                  this.mapLevel(access, i -> Component.literal((Integer)LHConfig.SERVER.killerAuraDamage.get() * i + "").withStyle(ChatFormatting.AQUA)),
                  Component.literal(LHConfig.SERVER.killerAuraRange.get() + "").withStyle(ChatFormatting.AQUA),
                  this.mapLevel(
                     access,
                     i -> Component.literal(Math.round(((Integer)LHConfig.SERVER.killerAuraInterval.get()).intValue() * 5.0 / i.intValue()) * 0.01 + "")
                        .withStyle(ChatFormatting.AQUA)
                  )
               }
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
