package io.github.manasmods.tensura.race.vampire;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.VampireConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.race.template.EvolutionRequirement;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VampireRace extends GhoulRace implements ITransformation {
   public VampireRace(Difficulty difficulty) {
      super(difficulty);
   }

   public VampireRace() {
      super(Difficulty.EASY);
      this.applyDefaultAttributeModifiers();
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((VampireConfig)ConfigRegistry.getConfig(VampireConfig.class)).Vampire;
   }

   @Override
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.VAMPIRE_OVERCOMER.get();
   }

   @Override
   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.VAMPIRE_OVERCOMER.get());
   }

   public List<ManasRace> getPreviousEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      return List.of((ManasRace)TensuraRaces.GHOUL.get(), (ManasRace)TensuraRaces.HUMAN.get());
   }

   @Override
   public Map<EvolutionRequirement, Float> getEvolutionRequirements(ManasRaceInstance previous, LivingEntity entity) {
      VampireConfig.Vampire config = ((VampireConfig)ConfigRegistry.getConfig(VampireConfig.class)).Vampire;
      return previous.getRace().equals(TensuraRaces.HUMAN.get())
         ? Map.of(new EvolutionRequirement.ItemConsumeRequirement((Item)TensuraMobDropItems.ZANE_BLOOD.get(), config.bloodRequirementHuman), 100.0F)
         : Map.of(new EvolutionRequirement.ItemConsumeRequirement((Item)TensuraMobDropItems.ZANE_BLOOD.get(), config.bloodRequirement), 100.0F);
   }

   @Override
   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = super.getIntrinsicSkills(instance, entity);
      list.add((ManasSkill)ExtraSkills.STEEL_STRENGTH.get());
      list.add((ManasSkill)ExtraSkills.SHADOW_MOTION.get());
      list.add((ManasSkill)CommonSkills.COERCION.get());
      list.add((ManasSkill)CommonSkills.PARALYSIS.get());
      list.add((ManasSkill)CommonSkills.STRENGTH.get());
      list.add((ManasSkill)CommonSkills.SELF_REGENERATION.get());
      list.add((ManasSkill)IntrinsicSkills.CHARM.get());
      return list;
   }

   public void onActivateAbility(ManasRaceInstance instance, LivingEntity entity) {
      if (entity instanceof Player player) {
         if (player.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE))) {
            player.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE));
            if (RaceUtils.canStillFly(player, true, false, true)) {
               return;
            }

            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
         } else {
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(player, 5.0, false);
            if (target != null
               && !RaceUtils.isBloodless(target)
               && (player.getHealth() < player.getMaxHealth() || player.getFoodData().needsFood() || player.isCreative())) {
               DamageSource source = TensuraDamageTypes.getEntityDamageSource(player.level(), TensuraDamageTypes.BLOOD_DRAIN, player);
               if (target.hurt(source, 2.0F)) {
                  player.heal(2.0F);
                  player.getFoodData().eat(2, 0.0F);
                  player.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
               }

               return;
            }

            if (player.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)
               && this.getClass() == VampireRace.class
               && RaceUtils.isUnderSun(player)) {
               return;
            }

            if (this.failedToActivate(player, TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE))) {
               return;
            }

            player.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE), 1728000, 0, false, false, false));
            if (player.isSpectator() || player.isCreative()) {
               return;
            }

            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
         }
      }
   }

   @Override
   public boolean isAlreadyTransformed(LivingEntity entity, Holder<MobEffect> effect) {
      return false;
   }

   @Override
   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      if (RaceUtils.isUnderSun(entity)) {
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 3, true, false, true));
         entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3, true, false, true));
         entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 3, true, false, true));
         if (shouldBurn(entity)) {
            entity.setRemainingFireTicks(40);
         }
      } else if (entity.level().isNight() && entity.level().getMoonPhase() == 4) {
         entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 3, true, false, true));
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 40, 3, true, false, true));
      }
   }

   public static boolean shouldBurn(LivingEntity entity) {
      if (entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)) {
         return true;
      } else {
         ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
         if (!helmet.isEmpty()) {
            helmet.hurtAndBreak(entity.getRandom().nextInt(2), entity, EquipmentSlot.HEAD);
            return false;
         } else {
            return true;
         }
      }
   }
}
