package io.github.manasmods.tensura.ability.magic.summon;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.config.ability.magic.SummoningMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.monster.BasiliskEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Objects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SummonBasiliskMagic extends SummoningMagic<BasiliskEntity> {
   public static final SummoningMagicConfig.SummonBasilisk CONFIG = ((SummoningMagicConfig)ConfigRegistry.getConfig(SummoningMagicConfig.class)).SummonBasilisk;

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public Pair<Double, Double> getSummonedCostPerSecond() {
      return Pair.of(0.0, CONFIG.magiculeCostSecond);
   }

   @Override
   public int getSuccessCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown;
   }

   @Override
   public void removeExistingSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      BasiliskEntity basilisk = ObjectSelectionHelper.getTargetingEntity(BasiliskEntity.class, entity, 30.0, 0.2, false);
      if (basilisk != null) {
         if (basilisk.isOwnedBy(entity)) {
            IExistence existence = TensuraStorages.getExistenceFrom(basilisk);
            if (existence.getSummonedSecond() > 0) {
               if (Objects.equals(entity.getUUID(), existence.getSummoner())) {
                  DamageSource source = TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.ENERGY_SOURCE_LOST)
                     .tensura$setCustomMessage("tensura.summon.end")
                     .tensura$setNotActualDeath(true);
                  basilisk.hurt(source, basilisk.getMaxHealth());
                  instance.setCoolDowns(0);
               }
            }
         }
      }
   }

   public void addAdditionalSummonData(ManasSkillInstance instance, LivingEntity entity, BasiliskEntity summon, int mode) {
      if (entity instanceof Player player) {
         summon.tame(player);
      }

      summon.applyBiomeVariant(entity.level());
      summon.skipDropExperience();
      IExistence existence = TensuraStorages.getExistenceFrom(summon);
      existence.setSummonedSecond(CONFIG.summonDuration);
      existence.setSummoner(entity.getUUID());
      existence.setSummonedAbility(this, mode);
      existence.markDirty();
   }

   @Override
   public void summonMagicCircle(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int heldTicks, int mode) {
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(3.0F, 30, pos, MagicCircleVariant.DEMON, entity, instance.getOrCreateTag(), instance, mode, cost);
   }

   @Override
   public EntityType<? extends BasiliskEntity> getSummonedType(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return (EntityType<? extends BasiliskEntity>)MonsterEntityTypes.BASILISK.get();
   }

   @Override
   public ParticleOptions getSummoningParticle(ManasSkillInstance instance, int mode) {
      return TensuraParticleUtils.getWhiteEffect();
   }

   @Override
   public SoundEvent getSummoningSound(ManasSkillInstance instance, int mode) {
      return (SoundEvent)TensuraSoundEvents.CAST_DARK.get();
   }

   @Override
   public SoundEvent getFailSound(ManasSkillInstance instance, int mode) {
      return (SoundEvent)TensuraSoundEvents.LEECH_LIZARD_DEATH.get();
   }
}
