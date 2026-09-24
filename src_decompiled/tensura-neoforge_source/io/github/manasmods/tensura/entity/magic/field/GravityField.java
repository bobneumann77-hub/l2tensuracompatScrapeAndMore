package io.github.manasmods.tensura.entity.magic.field;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class GravityField extends AreaField {
   private static final CommonSkillConfig.GravityField CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).GravityField;

   public GravityField(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public GravityField(Level level, LivingEntity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.GRAVITY_FIELD.get(), level);
      this.setOwner(entity);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.GRAVITY_ELEMENTAL;
   }

   @Override
   protected boolean applyMobEffects(LivingEntity target) {
      if (!(this.getOwner() instanceof LivingEntity owner && (target.isAlliedTo(owner) || target == owner))) {
         if (target.hasInfiniteMaterials()) {
            return false;
         }

         target.addEffect(
            new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), 100, CONFIG.slowFallLevel - 1, true, false, true), this.getOwner()
         );
         target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, CONFIG.speedLevel - 1, true, false, true), this.getOwner());
      } else {
         target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, CONFIG.speedLevel - 1, true, false, true), this.getOwner());
         target.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, CONFIG.slowFallLevel - 1, true, false, true), this.getOwner());
      }

      return true;
   }

   @Override
   public void spawnAmbientParticles() {
      if (this.getVisualSize() != this.getSize() || this.getAge() % 5 == 0) {
         Level level = this.level();
         float radius = this.getVisualSize();
         Vec3 pos = this.position().add(0.0, this.getSize(), 0.0);
         int ySteps = (int)(3.2 * radius);
         int xSteps = (int)(6.4 * radius);
         float yDeg = 180.0F / ySteps * (float) (Math.PI / 180.0);
         float xDeg = 360.0F / xSteps * (float) (Math.PI / 180.0);

         for (int x = 0; x < xSteps; x++) {
            for (int y = -ySteps; y < ySteps; y++) {
               double dx = level.random.nextDouble() * 0.05 - 0.05;
               double dy = level.random.nextDouble() * 0.05 - 0.05;
               double dz = level.random.nextDouble() * 0.05 - 0.05;
               Vec3 offset = new Vec3(0.0, 0.0, radius).yRot(y * yDeg).xRot(x * xDeg).zRot((float) (-Math.PI / 2)).multiply(1.0, 0.85F, 1.0);
               level.addParticle(ParticleTypes.ASH, pos.x + offset.x + dx, pos.y + 1.0 + offset.y + dy, pos.z + offset.z + dz, 0.0, 0.0, 0.0);
               level.addParticle(ParticleTypes.WHITE_ASH, pos.x + offset.x + dx, pos.y + 1.0 + offset.y + dy, pos.z + offset.z + dz, 0.0, 0.0, 0.0);
            }
         }
      }
   }
}
