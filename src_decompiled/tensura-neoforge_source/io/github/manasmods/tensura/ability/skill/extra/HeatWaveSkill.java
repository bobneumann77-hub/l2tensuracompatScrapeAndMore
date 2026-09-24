package io.github.manasmods.tensura.ability.skill.extra;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.entity.projectile.magic.HeatSphereProjectile;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class HeatWaveSkill extends Skill {
   public static final ExtraSkillConfig.HeatWave CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).HeatWave;

   public HeatWaveSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "heat_wave.sphere";
         case 1 -> "heat_wave.storm";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.magiculeCostStorm : CONFIG.magiculeCostSphere;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 1) {
         return false;
      }

      if (heldTicks % 15 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      float radius = CONFIG.stormRadius;
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      BarrierEntity.spawnLastingBarrier(
         (EntityType<? extends BarrierEntity>)MiscEntityTypes.HEAT_STORM.get(),
         CONFIG.stormDamage,
         CONFIG.stormRadius,
         0.0F,
         30,
         20.0F,
         entity.position().add(0.0, entity.getBbHeight() / 2.0F - radius, 0.0),
         entity,
         instance,
         mode,
         cost,
         Pair.of((Double)cost.getFirst() / 10.0, (Double)cost.getSecond() / 10.0),
         heldTicks
      );
      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.sphereCooldownMastered : CONFIG.sphereCooldown, mode);
            HeatSphereProjectile sphere = new HeatSphereProjectile(entity.level(), entity);
            sphere.setDamage(CONFIG.sphereDamage);
            sphere.setSpeed(1.0F);
            sphere.setEffectRange(2.5F);
            sphere.setPiercingEntity(true);
            sphere.setBurnTicks(CONFIG.sphereBurnTick);
            sphere.setNoGravity(true);
            sphere.setSkill(entity, instance, this, mode);
            sphere.setPosAndShoot(entity);
            entity.level().addFreshEntity(sphere);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }
}
