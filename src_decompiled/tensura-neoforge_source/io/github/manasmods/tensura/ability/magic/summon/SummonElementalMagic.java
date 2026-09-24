package io.github.manasmods.tensura.ability.magic.summon;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleType;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import java.util.Objects;
import lombok.Generated;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public abstract class SummonElementalMagic extends SummoningMagic<TamableAnimal> {
   private final SpiritualMagic.SpiritLevel level;

   public SummonElementalMagic(SpiritualMagic.SpiritLevel level) {
      this.level = level;
   }

   public int getModes(ManasSkillInstance instance) {
      return 5;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      int currentSpirit = mode;
      int nextMode = -1;

      for (int tries = 0; nextMode == -1 && tries <= 5; tries++) {
         if (reverse) {
            currentSpirit = currentSpirit - 1 <= -1 ? 4 : currentSpirit - 1;
         } else {
            currentSpirit = currentSpirit + 1 >= 5 ? 0 : currentSpirit + 1;
         }

         if (this.containsSpirit(instance, entity, currentSpirit)) {
            nextMode = currentSpirit;
         }
      }

      return nextMode;
   }

   private boolean containsSpirit(ManasSkillInstance instance, LivingEntity entity, int spirit) {
      int level = this.getLevel().getId();
      List<Element> elementals = Element.getCommonElemental();
      ISpiritWielder spiritWielder = TensuraStorages.getSpiritFrom(entity);
      if (spiritWielder.getSpiritLevelId(elementals.get(spirit)) >= level) {
         return true;
      } else {
         CompoundTag tag = instance.getTag();
         if (tag == null) {
            return false;
         } else {
            return !tag.contains("SpiritTamed") ? false : tag.getCompound("SpiritTamed").getInt(elementals.get(spirit).getNamespace()) >= level;
         }
      }
   }

   @Override
   public boolean onAbilityEquipped(
      Player player, Changeable<ManasSkillInstance> instance, Changeable<Integer> mode, Changeable<Integer> slot, Changeable<Integer> preset
   ) {
      int correctMode = this.nextMode(player, (ManasSkillInstance)instance.get(), (Integer)mode.get() - 1, false);
      mode.set(correctMode);
      if (correctMode == -1) {
         player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void removeExistingSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      TamableAnimal summon = ObjectSelectionHelper.getTargetingEntity(TamableAnimal.class, entity, 30.0, 0.2, false);
      if (summon != null) {
         if (summon instanceof IElementalSpirit spirit) {
            if (summon.isOwnedBy(entity)) {
               IExistence existence = TensuraStorages.getExistenceFrom(summon);
               if (existence.getSummonedSecond() > 0) {
                  if (Objects.equals(entity.getUUID(), existence.getSummoner())) {
                     if (spirit.getSpiritLevel() == this.getLevel()) {
                        DamageSource source = TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.ENERGY_SOURCE_LOST)
                           .tensura$setCustomMessage("tensura.summon.end")
                           .tensura$setNotActualDeath(true);
                        summon.hurt(source, summon.getMaxHealth());
                        instance.setCoolDowns(0);
                     }
                  }
               }
            }
         }
      }
   }

   protected int getSpiritDuration() {
      return 1200;
   }

   public void addAdditionalSummonData(ManasSkillInstance instance, LivingEntity entity, TamableAnimal summon, int mode) {
      if (entity instanceof Player player) {
         summon.tame(player);
      }

      summon.skipDropExperience();
      IExistence existence = TensuraStorages.getExistenceFrom(summon);
      existence.setSummonedSecond(this.getSpiritDuration());
      existence.setSummoner(entity.getUUID());
      existence.setSummonedAbility(this, mode);
      existence.markDirty();
   }

   @Override
   public void summonMagicCircle(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int heldTicks, int mode) {
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(3.0F, 30, pos, this.getSummoningCircle(instance, mode), entity, instance.getOrCreateTag(), instance, mode, cost);
   }

   protected MagicCircleVariant getSummoningCircle(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> MagicCircleVariant.EARTH;
         default -> MagicCircleVariant.FLAME;
         case 2 -> MagicCircleVariant.SPACE;
         case 3 -> MagicCircleVariant.WATER;
         case 4 -> MagicCircleVariant.WIND;
      };
   }

   @Override
   public ParticleOptions getSummoningParticle(ManasSkillInstance instance, int mode) {
      return (ParticleOptions)(switch (mode) {
         case 0 -> new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DRIPSTONE_BLOCK.defaultBlockState());
         default -> (TensuraParticleType)TensuraParticleTypes.RED_FIRE.get();
         case 2 -> ParticleTypes.REVERSE_PORTAL;
         case 3 -> TensuraParticleUtils.getWaterBubble();
         case 4 -> TensuraParticleUtils.getGreenGust();
      });
   }

   @Override
   public SoundEvent getSummoningSound(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> (SoundEvent)TensuraSoundEvents.CAST_EARTH.get();
         default -> (SoundEvent)TensuraSoundEvents.CAST_FIRE.get();
         case 2 -> (SoundEvent)TensuraSoundEvents.CAST_SPACE.get();
         case 3 -> (SoundEvent)TensuraSoundEvents.CAST_WATER.get();
         case 4 -> (SoundEvent)TensuraSoundEvents.CAST_WIND.get();
      };
   }

   @Override
   public SoundEvent getFailSound(ManasSkillInstance instance, int mode) {
      return (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get();
   }

   public static void addSpiritSummonLevel(ManasSkillInstance instance, IElementalSpirit spirit) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.contains("SpiritTamed")) {
         CompoundTag spiritTamed = tag.getCompound("SpiritTamed");
         int currentLevel = spiritTamed.getInt(spirit.getElemental().getNamespace());
         if (currentLevel >= spirit.getSpiritLevel().getId()) {
            return;
         }

         spiritTamed.putInt(spirit.getElemental().getNamespace(), spirit.getSpiritLevel().getId());
         instance.markDirty();
      } else {
         CompoundTag spiritTamed = new CompoundTag();
         spiritTamed.putInt(spirit.getElemental().getNamespace(), spirit.getSpiritLevel().getId());
         tag.put("SpiritTamed", spiritTamed);
         instance.markDirty();
      }
   }

   @Generated
   public SpiritualMagic.SpiritLevel getLevel() {
      return this.level;
   }
}
