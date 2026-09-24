package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.entity.human.golem.BoneGolemEntity;
import io.github.manasmods.tensura.entity.variant.BoneGolemVariant;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PossessionSkill extends Skill implements ICloning {
   public static final IntrinsicSkillConfig.Possession CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).Possession;
   private static final Map<Holder<Attribute>, Double> STAT_MAP = Map.of(
      Attributes.MAX_HEALTH,
      1.0,
      Attributes.ATTACK_DAMAGE,
      0.1,
      Attributes.KNOCKBACK_RESISTANCE,
      0.0,
      Attributes.MOVEMENT_SPEED,
      0.1,
      Attributes.JUMP_STRENGTH,
      0.42,
      Attributes.SAFE_FALL_DISTANCE,
      3.0,
      ManasCoreAttributes.SWIM_SPEED_MULTIPLIER,
      1.0
   );
   private static final List<Holder<Attribute>> STAT_LIST = List.of(
      Attributes.MAX_HEALTH,
      Attributes.ATTACK_DAMAGE,
      Attributes.ARMOR,
      Attributes.KNOCKBACK_RESISTANCE,
      Attributes.MOVEMENT_SPEED,
      Attributes.JUMP_STRENGTH,
      Attributes.SAFE_FALL_DISTANCE,
      ManasCoreAttributes.SWIM_SPEED_MULTIPLIER
   );

   public PossessionSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.POSSESSION.get());
   }

   public static boolean canPossess(
      LivingEntity target,
      Player player,
      ManasSkill skill,
      double resistanceMultiplier,
      double hpMultiplier,
      double shpMultiplier,
      double epMultiplier,
      boolean usableOnGolem
   ) {
      if (target.isInvulnerable()) {
         return false;
      }

      if (target.getType().is(TensuraEntityTags.NO_POSSESSION)) {
         return false;
      }

      if (target.getType().is(TensuraEntityTags.SPIRITUAL)) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(target);
      if (existence.isSpiritualForm()) {
         return false;
      }

      if (player.isCreative()) {
         return true;
      }

      if (target.getType().equals(HumanEntityTypes.BONE_GOLEM.get())) {
         return !usableOnGolem
            ? false
            : EnergyHelper.getBaseMaxEP(target)
               <= player.getAttributeValue(TensuraAttributes.MAX_AURA) + player.getAttributeValue(TensuraAttributes.MAX_MAGICULE);
      }

      if (target instanceof CloneEntity clone) {
         if (!clone.getSkill().getSkill().equals(skill)) {
            return false;
         }

         if (clone.getOwner() != null && clone.getOwner().equals(player)) {
            return true;
         }
      }

      if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
         return false;
      }

      double amplifier = 1.0;
      if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
         amplifier = resistanceMultiplier;
      }

      if (target instanceof Player targetPlayer && !targetPlayer.isCreative() && !target.isSpectator()) {
         int requirement = 0;
         if (target.getHealth() < target.getMaxHealth() * hpMultiplier * amplifier) {
            requirement++;
         }

         if (existence.getSpiritualHealth() < target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) * shpMultiplier * amplifier) {
            requirement++;
         }

         if (existence.getEP() < EnergyHelper.getMaxEP(player) * epMultiplier * amplifier) {
            requirement++;
         }

         return requirement >= 2;
      } else if (target.getHealth() < target.getMaxHealth() * hpMultiplier * amplifier) {
         return true;
      } else {
         return existence.getSpiritualHealth() < target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) * shpMultiplier * amplifier
            ? true
            : existence.getEP() < EnergyHelper.getMaxEP(player) * epMultiplier * amplifier;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity instanceof Player player) {
         Level level = entity.level();
         if (SkillUtils.inSpiritualWorld(level.dimension())) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.location").withStyle(ChatFormatting.RED), false);
            level.playSound(
               null,
               player.getX(),
               player.getY(),
               player.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
         } else if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ENERGY_BLOCKADE))) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.status").withStyle(ChatFormatting.RED), false);
            level.playSound(
               null,
               player.getX(),
               player.getY(),
               player.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
         } else {
            IExistence existence = TensuraStorages.getExistenceFrom(player);
            if (existence.isSpiritualForm()) {
               LivingEntity target = ObjectSelectionHelper.getTargetingEntity(player, CONFIG.range, false);
               if (target == null || !target.isAlive()) {
                  player.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  return;
               }

               if (!canPossess(target, player, this, CONFIG.resistanceMultiplier, CONFIG.hpMultiplier, CONFIG.shpMultiplier, CONFIG.epMultiplier, true)) {
                  player.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
                  return;
               }

               possess(instance, player, existence, target, CONFIG.maxAttack, CONFIG.maxHealth);
            } else {
               turnSpiritual(instance, player, existence, CONFIG.bodyDespawnTick);
            }
         }
      }
   }

   public static void summonBody(ManasSkillInstance instance, Player player, double EP, int bodyDespawnTick) {
      CloneEntity clone = new CloneEntity((EntityType<? extends CloneEntity>)HumanEntityTypes.CLONE.get(), player.level());
      clone.setLife(bodyDespawnTick * 20);
      clone.tame(player);
      clone.setSkill(instance);
      clone.setStatic(true);
      clone.setHealth(player.getHealth());
      clone.setRemainingFireTicks(player.getRemainingFireTicks());
      CloneEntity.copyEffects(player, clone);
      EnergyHelper.setMaxMagicule(clone, Math.max(EP / 100.0, 100.0));
      EnergyHelper.setMaxAura(clone, 50.0);
      IExistence cloneExistence = TensuraStorages.getExistenceFrom(clone);
      cloneExistence.setMagicule(EP / 100.0);
      cloneExistence.markDirty();
      CloneEntity.copyStatusEffect(player, clone, CloneEntity.CopySkill.INTRINSIC, true);
      clone.copyStatsAndSkills(player, CloneEntity.CopySkill.INTRINSIC, true);
      clone.copyEquipments(player);

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         player.setItemSlot(slot, ItemStack.EMPTY);
      }

      CompoundTag tag = instance.getTag();
      if (tag == null || !tag.contains("OriginalBody")) {
         clone.setLife(-1);
         instance.getOrCreateTag().putUUID("OriginalBody", clone.getUUID());
         instance.markDirty();
      }

      clone.moveTo(player.position().x, player.position().y, player.position().z, player.getYRot(), player.getXRot());
      player.level().addFreshEntity(clone);
   }

   public static void turnSpiritual(ManasSkillInstance instance, Player player, IExistence existence, int bodyDespawnTick) {
      if (!existence.isSpiritualForm()) {
         double EP = existence.getEP();
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
         if (optional.isPresent()) {
            CompoundTag tag = optional.get().getTag();
            if (tag != null && tag.getBoolean("BoneGolem")) {
               double baseEP = player.getAttributeValue(TensuraAttributes.MAX_AURA) + player.getAttributeValue(TensuraAttributes.MAX_MAGICULE);
               BoneGolemVariant variant = BoneGolemVariant.byLowest(baseEP, player.getMaxHealth());
               BoneGolemEntity golem = new BoneGolemEntity((EntityType<? extends BoneGolemEntity>)HumanEntityTypes.BONE_GOLEM.get(), player.level());
               golem.setVariant(variant);
               golem.moveTo(player.position().x, player.position().y, player.position().z, player.getYRot(), player.getXRot());
               golem.setDeltaMovement(player.calculateViewVector(player.getXRot(), player.getYRot()).normalize().multiply(0.005, 0.0, 0.005));
               player.level().addFreshEntity(golem);
               double ep = variant.getEP();
               EnergyHelper.setBaseMaxEP(golem, ep);
               IExistence golemExistence = TensuraStorages.getExistenceFrom(golem);
               golemExistence.setSpiritualHealth(60.0);
               golemExistence.setMagicule(ep / 2.0);
               golemExistence.setAura(ep / 2.0);
               golemExistence.markDirty();
               golem.copyStatAndRace(player);
               tag.remove("BoneGolem");
               instance.markDirty();
            } else {
               summonBody(instance, player, EP, bodyDespawnTick);
            }
         } else {
            summonBody(instance, player, EP, bodyDespawnTick);
         }

         existence.setSpiritualForm(true);
         existence.markDirty();
         if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
         }

         optional.ifPresent(manasRaceInstance -> {
            for (AttributeInstance attribute : player.getAttributes().attributes.values()) {
               attribute.removeModifier(TensuraRace.DEFAULT_RACE_ID);
            }

            manasRaceInstance.addAttributeModifiers(player);
         });
         player.level()
            .playSound(
               null, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         Skills storage = SkillAPI.getSkillsFrom(player);

         for (ManasSkillInstance temp : List.copyOf(storage.getLearnedSkills())) {
            if (temp.isTemporarySkill()) {
               if (temp.getTag() != null) {
                  temp.getTag().remove("SpatialStorage");
               }

               storage.forgetSkill(temp);
            }
         }
      }
   }

   public static void possess(ManasSkillInstance instance, Player player, IExistence existence, LivingEntity target, double maxAttack, double maxHealth) {
      Level level = player.level();
      if (((TensuraEntityEvents.PossessionEvent)TensuraEntityEvents.POSSESSION_EVENT.invoker()).possess(target, player).isFalse()) {
         player.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
         level.playSound(
            null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
      } else {
         ((ServerPlayer)player)
            .teleportTo((ServerLevel)level, target.position().x, target.position().y, target.position().z, target.getYRot(), target.getXRot());
         player.hurtMarked = true;
         level.playSound(
            null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
         double size = target.getAttributeValue(Attributes.SCALE) * 4.0;
         TensuraParticleHelper.addServerAuraParticles(target, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
         copyStatsAndSkills(target, player, maxAttack, maxHealth);
         CloneEntity.copyEffects(target, player);
         if (target instanceof CloneEntity clone && clone.isOwnedBy(player)) {
            clone.copyInventoryOntoOwner(player, false);
            clone.resetOwner(null);
         }

         IExistence targetExistence = TensuraStorages.getExistenceFrom(target);
         if (target instanceof Player targetPlayer) {
            targetExistence.setSpiritualForm(true);
            targetExistence.markDirty();
            Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(targetPlayer).getRace();
            optional.ifPresent(manasRaceInstance -> {
               for (AttributeInstance attribute : player.getAttributes().attributes.values()) {
                  attribute.removeModifier(TensuraRace.DEFAULT_RACE_ID);
               }

               manasRaceInstance.addAttributeModifiers(player);
            });
            targetPlayer.getAbilities().mayfly = true;
            targetPlayer.getAbilities().flying = true;
            targetPlayer.onUpdateAbilities();
            Skills storage = SkillAPI.getSkillsFrom(targetPlayer);

            for (ManasSkillInstance temp : List.copyOf(storage.getLearnedSkills())) {
               if (temp.isTemporarySkill()) {
                  if (temp.getTag() != null) {
                     temp.getTag().remove("SpatialStorage");
                  }

                  storage.forgetSkill(temp);
               }
            }
         } else {
            target.skipDropExperience();
            DamageSource source = TensuraDamageTypes.getDamageSource(level, TensuraDamageTypes.SOUL_SCATTER).tensura$setBarrierBypassLevel(3.0F);
            if (!target.hurt(source, target.getMaxHealth() * 10.0F)) {
               target.die(source);
               target.discard();
            } else {
               target.deathTime = 19;
            }
         }

         instance.addMasteryPoint(player);
         existence.setSpiritualForm(false);
         existence.markDirty();
         if (!RaceUtils.canStillFly(player, false, true, true)) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
         }
      }
   }

   @Override
   public void onCloneTick(CloneEntity clone, LivingEntity owner) {
      if (clone.getLife() <= 0) {
         Optional<ManasSkillInstance> possession = SkillAPI.getSkillsFrom(owner).getSkill(this);
         if (possession.isEmpty()) {
            clone.setLife(CONFIG.bodyDespawnTick * 20);
         } else if (possession.get().getOrCreateTag().hasUUID("OriginalBody")
            && !Objects.equals(possession.get().getOrCreateTag().getUUID("OriginalBody"), clone.getUUID())) {
            clone.setLife(CONFIG.bodyDespawnTick * 20);
         }
      }
   }

   public static boolean canCopySkill(ManasSkillInstance instance, LivingEntity target, boolean clone) {
      if (clone) {
         return instance.isTemporarySkill();
      }

      if (target.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
         return false;
      }

      if (target instanceof Player player) {
         if (instance.isTemporarySkill()) {
            return true;
         }

         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
         return optional.isPresent() && optional.get().getObtainedIntrinsicSkills().contains(instance.getSkill());
      } else {
         return true;
      }
   }

   public static void copyStatsAndSkills(LivingEntity target, Player owner, double maxAttack, double maxHealth) {
      if (target.getType().equals(HumanEntityTypes.BONE_GOLEM.get()) || target.getType().equals(HumanEntityTypes.CLONE.get())) {
         AttributeInstance attribute = target.getAttribute(Attributes.ARMOR);
         double value;
         if (attribute == null) {
            value = 0.0;
         } else {
            value = attribute.getBaseValue();
            AttributeModifier raceStat = attribute.getModifier(TensuraRace.DEFAULT_RACE_ID);
            if (raceStat != null) {
               value += raceStat.amount();
            }
         }

         AttributeHelper.addPermanentAttribute(
            owner, Attributes.ARMOR, TensuraRace.DEFAULT_RACE_ID, value - owner.getAttributeBaseValue(Attributes.ARMOR), Operation.ADD_VALUE
         );
         if (target.getType().equals(HumanEntityTypes.BONE_GOLEM.get())) {
            RaceAPI.getRaceFrom(owner).getRace().ifPresent(instancex -> {
               instancex.getOrCreateTag().putBoolean("BoneGolem", true);
               instancex.markDirty();
            });
            AttributeInstance maxHP = target.getAttribute(Attributes.MAX_HEALTH);
            if (maxHP != null) {
               double health = maxHP.getBaseValue();
               AttributeModifier raceStat = maxHP.getModifier(TensuraRace.DEFAULT_RACE_ID);
               if (raceStat != null) {
                  health += raceStat.amount();
               }

               maxHealth = Math.max(maxHealth, health);
            }
         }
      }

      for (Entry<Holder<Attribute>, Double> map : getStatMap().entrySet()) {
         AttributeInstance attribute = target.getAttribute(map.getKey());
         double value;
         if (attribute == null) {
            value = map.getValue();
         } else {
            value = attribute.getBaseValue();
            AttributeModifier raceStat = attribute.getModifier(TensuraRace.DEFAULT_RACE_ID);
            if (raceStat != null) {
               value += raceStat.amount();
            }
         }

         if (map.getKey().equals(Attributes.MOVEMENT_SPEED) && !(target instanceof Player)) {
            value = ICloning.getConvertedMovementSpeed(value, false);
         } else if (map.getKey().equals(Attributes.ATTACK_DAMAGE) && isCloneNotOwned(owner, target)) {
            value = Math.min(value, maxAttack);
         } else if (map.getKey().equals(Attributes.MAX_HEALTH) && isCloneNotOwned(owner, target)) {
            value = Math.min(value, maxHealth);
         } else if (map.getKey().equals(Attributes.JUMP_STRENGTH) && !(target instanceof CloneEntity)) {
            value = ICloning.getConvertedJumpStrength(value, false);
         }

         AttributeHelper.addPermanentAttribute(
            owner, map.getKey(), TensuraRace.DEFAULT_RACE_ID, value - owner.getAttributeBaseValue(map.getKey()), Operation.ADD_VALUE
         );
      }

      owner.setHealth(Math.max(target.getHealth(), 0.0F));

      for (MobEffectInstance instance : target.getActiveEffects()) {
         owner.addEffect(new MobEffectInstance(instance));
      }

      boolean clone = target instanceof CloneEntity;

      for (ManasSkillInstance instance : List.copyOf(SkillAPI.getSkillsFrom(target).getLearnedSkills())) {
         if (!(instance.getMastery() < 0.0) && canCopySkill(instance, target, clone)) {
            ManasSkillInstance copy = TensuraSkillInstance.fromNBT(instance.toNBT());
            if (!copy.isTemporarySkill()) {
               copy.setRemoveTime(-2);
            }

            SkillHelper.learnSkill(owner, copy, !copy.isTemporarySkill() ? -2 : copy.getRemoveTime());
         }
      }
   }

   private static boolean isCloneNotOwned(LivingEntity owner, LivingEntity target) {
      return !(target instanceof CloneEntity clone && clone.isOwnedBy(owner));
   }

   public static Map<Holder<Attribute>, Double> getStatMap() {
      return STAT_MAP;
   }

   public static List<Holder<Attribute>> getStatList() {
      return STAT_LIST;
   }
}
