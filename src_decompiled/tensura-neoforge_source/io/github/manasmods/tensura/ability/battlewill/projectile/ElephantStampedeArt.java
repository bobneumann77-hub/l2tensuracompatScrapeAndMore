package io.github.manasmods.tensura.ability.battlewill.projectile;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.entity.projectile.magic.AuraBulletProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ElephantStampedeArt extends Battlewill {
   private static final BattlewillConfig.ElephantStampede CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).ElephantStampede;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks > 0 && heldTicks % CONFIG.holdTime == 0) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         entity.swing(InteractionHand.MAIN_HAND, true);
         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0) {
            instance.addMasteryPoint(entity);
         }

         this.spawnAuraBullets(instance, entity, entity.getEyePosition().add(0.0, 6.0, 0.0), mode);
      }

      TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.ELECTRIC_SPARK, 1.0);
      return true;
   }

   private void spawnAuraBullets(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int mode) {
      for (int i = 0; i < CONFIG.bulletNumber; i++) {
         Vec3 arrowPos = entity.getEyePosition().add(new Vec3(0.0, 1.0, 0.0).zRot((45 * i - 22.5F) * (float) (Math.PI / 180.0)).xRot((float) (Math.PI / 2)));
         AuraBulletProjectile bullet = new AuraBulletProjectile(entity.level(), entity);
         bullet.setSpeed(0.75F);
         bullet.setPos(arrowPos);
         bullet.shootFromRot(pos.subtract(arrowPos).normalize());
         bullet.setDamage(instance.isMastered(entity) ? CONFIG.baseDamage * 2.0F : CONFIG.baseDamage);
         bullet.setExplosionRadius(4.0F);
         bullet.setSize(0.5F);
         bullet.setColor(7747016);
         bullet.setSkill(entity, instance, this, mode);
         entity.level().addFreshEntity(bullet);
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }
}
