package io.github.manasmods.tensura.ability.magic.spiritual.necromancy;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.entity.human.undead.UndeadHumanoidEntity;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class CreateLesserUndeadMagic extends CreateUndeadMagic<UndeadHumanoidEntity> {
   public static final SpiritualMagicConfig.CreateLesserUndead CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).CreateLesserUndead;

   public CreateLesserUndeadMagic() {
      super(SpiritualMagic.SpiritLevel.LESSER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "create_undead.zombie";
         case 1 -> "create_undead.skeleton";
         case 2 -> "create_undead.random";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public int getSuccessCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown;
   }

   @Override
   protected int getSpawnNumber(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return !instance.isMastered(entity) || !entity.isShiftKeyDown() && !instance.getOrCreateTag().contains("SummonUUID_1")
         ? CONFIG.undeadNumber
         : CONFIG.undeadNumberMastered;
   }

   @Nullable
   @Override
   public EntityType<? extends UndeadHumanoidEntity> getSummonedType(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return switch (mode) {
         case 0 -> (EntityType)HumanEntityTypes.ZOMBIE.get();
         case 1 -> (EntityType)HumanEntityTypes.SKELETON.get();
         default -> entity.getRandom().nextBoolean() ? (EntityType)HumanEntityTypes.ZOMBIE.get() : (EntityType)HumanEntityTypes.SKELETON.get();
      };
   }

   @Override
   public void removeExistingSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      TamableAnimal summon = ObjectSelectionHelper.getTargetingEntity(TamableAnimal.class, entity, 30.0, 0.2, false);
      if (summon != null) {
         if (summon.isOwnedBy(entity)) {
            IExistence existence = TensuraStorages.getExistenceFrom(summon);
            if (existence.getSummonedSecond() > 0) {
               if (Objects.equals(entity.getUUID(), existence.getSummoner())) {
                  DamageSource source = TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.ENERGY_SOURCE_LOST);
                  summon.hurt(source, summon.getMaxHealth());
                  instance.setCoolDowns(0);
               }
            }
         }
      }
   }

   public void addAdditionalSummonData(ManasSkillInstance instance, LivingEntity entity, UndeadHumanoidEntity summon, int mode) {
      if (entity instanceof Player player) {
         summon.tame(player);
      }

      summon.setBurnInSunlight(true);
      summon.skipDropExperience();
      AttributeInstance hp = summon.getAttribute(Attributes.MAX_HEALTH);
      if (hp != null) {
         hp.setBaseValue(CONFIG.undeadHP);
         summon.setHealth(CONFIG.undeadHP);
      }

      AttributeInstance attack = summon.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack != null) {
         attack.setBaseValue(CONFIG.undeadAttack);
      }

      IExistence existence = TensuraStorages.getExistenceFrom(summon);
      existence.setSummoner(entity.getUUID());
      existence.setSummonedSecond(CONFIG.undeadDuration);
      existence.setSummonedAbility(this, mode);
      existence.markDirty();
   }

   @Override
   public ParticleOptions getSummoningParticle(ManasSkillInstance instance, int mode) {
      return ParticleTypes.SOUL;
   }

   @Override
   public SoundEvent getSummoningSound(ManasSkillInstance instance, int mode) {
      return SoundEvents.SOUL_SAND_BREAK;
   }

   @Override
   public SoundEvent getFailSound(ManasSkillInstance instance, int mode) {
      return (SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get();
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
         if (!race.isEmpty() && race.get().is(TensuraRaceTags.NECROMANCER)) {
            SkillHelper.learnSkill(entity, ((CreateGreaterUndeadMagic)SpiritualMagics.CREATE_GREATER_UNDEAD.get()).createLearningInstance(entity));
         }
      }
   }
}
