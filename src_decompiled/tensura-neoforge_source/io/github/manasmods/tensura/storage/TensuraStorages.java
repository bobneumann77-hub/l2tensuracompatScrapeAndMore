package io.github.manasmods.tensura.storage;

import io.github.manasmods.tensura.ability.magic.summon.ISummoning;
import io.github.manasmods.tensura.storage.ability.AbilityStorage;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.boss.BossFightStorage;
import io.github.manasmods.tensura.storage.boss.template.IBossFightHolder;
import io.github.manasmods.tensura.storage.chunk.ChunkStorage;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.labyrinth.ILabyrinth;
import io.github.manasmods.tensura.storage.labyrinth.LabyrinthStorage;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.storage.player.TensuraPlayerStorage;
import io.github.manasmods.tensura.storage.restriction.WorldRestrictionStorage;
import io.github.manasmods.tensura.storage.restriction.template.IWorldRestriction;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import io.github.manasmods.tensura.storage.spirit.SpiritStorage;
import io.github.manasmods.tensura.storage.unique.ITrulyUnique;
import io.github.manasmods.tensura.storage.unique.UniqueStorage;
import lombok.NonNull;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class TensuraStorages {
   public static void init() {
      AbilityStorage.init();
      ChunkStorage.init();
      EffectStorage.init();
      ExistenceStorage.init();
      SpiritStorage.init();
      TensuraPlayerStorage.init();
      BossFightStorage.init();
      LabyrinthStorage.init();
      UniqueStorage.init();
      WorldRestrictionStorage.init();
   }

   public static IAbility getAbilityFrom(@NonNull LivingEntity entity) {
      if (entity == null) {
         throw new NullPointerException("entity is marked non-null but is null");
      } else {
         return (IAbility)entity.manasCore$getStorage(AbilityStorage.getKey());
      }
   }

   public static ChunkStorage getChunkFrom(@NonNull LevelChunk chunk) {
      if (chunk == null) {
         throw new NullPointerException("chunk is marked non-null but is null");
      }

      ChunkStorage storage = (ChunkStorage)chunk.manasCore$getStorage(ChunkStorage.getKey());
      if (storage != null) {
         storage.initialize();
      }

      return storage;
   }

   public static IEffect getEffectFrom(@NonNull LivingEntity entity) {
      if (entity == null) {
         throw new NullPointerException("entity is marked non-null but is null");
      } else {
         return (IEffect)entity.manasCore$getStorage(EffectStorage.getKey());
      }
   }

   public static IExistence getExistenceFrom(@NonNull LivingEntity entity) {
      if (entity == null) {
         throw new NullPointerException("entity is marked non-null but is null");
      } else {
         return (IExistence)entity.manasCore$getStorage(ExistenceStorage.getKey());
      }
   }

   public static ISpiritWielder getSpiritFrom(@NonNull LivingEntity entity) {
      if (entity == null) {
         throw new NullPointerException("entity is marked non-null but is null");
      } else {
         return (ISpiritWielder)entity.manasCore$getStorage(SpiritStorage.getKey());
      }
   }

   public static ITensuraPlayer getPlayerDataFrom(@NonNull LivingEntity entity) {
      if (entity == null) {
         throw new NullPointerException("entity is marked non-null but is null");
      } else {
         return (ITensuraPlayer)entity.manasCore$getStorage(TensuraPlayerStorage.getKey());
      }
   }

   public static IBossFightHolder getBossFightHolder(@NonNull Level level) {
      if (level == null) {
         throw new NullPointerException("level is marked non-null but is null");
      } else {
         return (IBossFightHolder)level.manasCore$getStorage(BossFightStorage.getKey());
      }
   }

   public static ILabyrinth getLabyrinthFrom(@NonNull Level level) {
      if (level == null) {
         throw new NullPointerException("level is marked non-null but is null");
      } else {
         return (ILabyrinth)level.manasCore$getStorage(LabyrinthStorage.getKey());
      }
   }

   public static ITrulyUnique getUniqueStorageFrom(@NonNull Level level) {
      if (level == null) {
         throw new NullPointerException("level is marked non-null but is null");
      } else {
         return (ITrulyUnique)level.manasCore$getStorage(UniqueStorage.getKey());
      }
   }

   public static IWorldRestriction getWorldRestrictionFrom(@NonNull Level level) {
      if (level == null) {
         throw new NullPointerException("level is marked non-null but is null");
      } else {
         return (IWorldRestriction)level.manasCore$getStorage(WorldRestrictionStorage.getKey());
      }
   }

   public static void resetExistence(LivingEntity entity) {
      resetExistence(entity, true, true, true);
   }

   public static void resetExistence(LivingEntity entity, boolean resetName, boolean resetAlignment, boolean resetAwakening) {
      IExistence existence = getExistenceFrom(entity);
      existence.setTemporaryOwner(null);
      existence.setPermanentOwner(null);
      existence.clearNeutralTargets();
      existence.setSleepModeTime(0);
      existence.setSpiritualForm(false);
      if (existence.getSummonedAbility() != null && existence.getSummonedAbility().getSkill() instanceof ISummoning<?> summoning) {
         summoning.onSummonRemoval(entity, false);
      }

      existence.setSummonedAbility(null, 0);
      existence.setSummonedSecond(0);
      existence.setSummoner(null);
      if (resetName) {
         existence.setName(null);
         existence.setNameable(false);
      }

      if (resetAlignment) {
         existence.setOriginalAlignment(Alignment.DEFAULT);
         existence.setAlignment(Alignment.DEFAULT);
      }

      if (resetAwakening) {
         existence.setHarvestGift(false);
         existence.setHumanKill(0);
         existence.setSoulPoints(0);
         existence.setDemonLordSeed(false);
         existence.setTrueDemonLord(false);
         existence.setBlessed(false);
         existence.setHeroEgg(false);
         existence.setTrueHero(false);
      }

      existence.markDirty();
   }

   public static void resetEffect(LivingEntity entity) {
      IEffect effect = getEffectFrom(entity);
      effect.setSeveranceAmount(0.0F);
      effect.setSeveranceRemoveTime(0);
      effect.setOnBlackFlame(false);
      effect.setIgnorePainNull(false);
      effect.markDirty();
   }

   public static void resetPlayerData(LivingEntity entity) {
      ITensuraPlayer data = getPlayerDataFrom(entity);
      if (data != null) {
         data.setForcedThirdPerson(false);
         data.setDodgeCooldown(0);
         data.setDodgeInvulnerability(0);
         data.setPresenceSenseMode(0);
         data.setAnalysisMode(0);
         data.markDirty();
      }
   }

   public static void resetSpirit(LivingEntity entity) {
      ISpiritWielder spirit = getSpiritFrom(entity);
      spirit.clearSpiritLevel();
      spirit.setSpiritCooldown(0);
      spirit.setColossusWon(false);
      spirit.setColossusPassed(false);
      spirit.setColossusStarted(false);
      spirit.markDirty();
   }
}
