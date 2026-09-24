package io.github.manasmods.tensura.entity.magic.beam;

import io.github.manasmods.tensura.ability.magic.spiritual.wind.ElectroBlastMagic;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.awt.Color;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ElectroBlastProjectile extends BeamProjectile {
   public ElectroBlastProjectile(EntityType<? extends ElectroBlastProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.beamColorAndSize.put(new Color(2, 218, 255, 200), 0.2F);
      this.beamColorAndSize.put(new Color(67, 48, 243, 100), 0.4F);
      this.beamColorAndSize.put(new Color(191, 183, 255, 30), 0.6F);
   }

   public ElectroBlastProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends ElectroBlastProjectile>)MiscEntityTypes.ELECTRO_BLAST.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceLocation[] getTextureLocation() {
      return new ResourceLocation[]{ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/beam/electric_beam.png")};
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHTNING_ELEMENTAL;
   }

   @Override
   protected boolean dealDamage(Entity target) {
      if (this.damage <= 0.0F || target instanceof ItemEntity) {
         return false;
      }

      if (super.dealDamage(target)) {
         TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.LIGHTNING_EFFECT.get());
         if (target instanceof LivingEntity living) {
            living.addEffect(
               new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS),
                  ElectroBlastMagic.CONFIG.paralysisDuration,
                  ElectroBlastMagic.CONFIG.paralysisLevel - 1,
                  true,
                  false,
                  true
               )
            );
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      if (this.tickCount % 3 == 0) {
         Vec3 end = new Vec3(x, y, z);
         TensuraParticleHelper.addParticlesAroundPos(
            this.level().random, this.level(), end, (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(), this.getSize(), 3
         );
      }
   }

   @Override
   public void rayParticles(Vec3 pos, int i) {
      if (this.tickCount % this.random.nextInt(10, 16) == 0) {
         TensuraParticleHelper.addParticlesAroundPos(
            this.random, this.level(), pos, (ParticleOptions)TensuraParticleTypes.LIGHTNING_SPARK.get(), this.getVisualSize(), 1
         );
      }
   }
}
