package io.github.manasmods.tensura.damage;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.extra.LawManipulationSkill;
import io.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TensuraDamageHelper {
   public static DamageSource copy(DamageSource original) {
      DamageSource source = new DamageSource(original.typeHolder(), original.getDirectEntity(), original.getEntity())
         .tensura$setAbilityInstance(original.tensura$getAbilityInstance())
         .tensura$setAbilityMode(original.tensura$getAbilityMode())
         .tensura$setSkillType(original.tensura$getSkillType())
         .tensura$setMagicType(original.tensura$getMagicType())
         .tensura$setElement(original.tensura$getElement())
         .tensura$setResistanceBypassLevel(original.tensura$getResistanceBypassLevel())
         .tensura$setBarrierBypassLevel(original.tensura$getBarrierBypassLevel())
         .tensura$setAuraCost(original.tensura$getAuraCost())
         .tensura$setMagiculeCost(original.tensura$getMagiculeCost());
      return original.tensura$isSlotting() ? source.tensura$setSlotting() : source;
   }

   public static DamageSource getUUIDDamageSource(ResourceKey<DamageType> type, ServerLevel level, @Nullable UUID uuid, @Nullable AbilitySlot slot) {
      Entity effectSource = uuid != null ? level.getEntity(uuid) : null;
      DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(level, type, effectSource);
      if (slot != null) {
         ManasSkill skill = slot.getSkill();
         if (skill != null) {
            return damageSource.tensura$setAbilityInstance(skill.createDefaultInstance()).tensura$setAbilityMode(slot.getMode());
         }
      }

      return damageSource;
   }

   public static DamageSource getAbilityDamageSource(ResourceKey<DamageType> type, LivingEntity attacker, @Nullable AbilitySlot slot) {
      DamageSource damageSource = TensuraDamageTypes.getEntityDamageSource(attacker.level(), type, attacker);
      if (slot != null) {
         ManasSkill skill = slot.getSkill();
         if (skill != null) {
            return damageSource.tensura$setAbilityInstance(skill.createDefaultInstance()).tensura$setAbilityMode(slot.getMode());
         }
      }

      return damageSource;
   }

   public static boolean hurtDouble(Entity target, DamageSource mainSource, float mainDamage, DamageSource otherSource, float otherDamage) {
      boolean success = target.hurt(otherSource, otherDamage);
      if (mainDamage > 0.0F) {
         if (success && target.invulnerableTime < 60) {
            target.invulnerableTime = 0;
            target.hurt(mainSource, mainDamage);
            target.invulnerableTime = 20;
         } else {
            success = target.hurt(mainSource, mainDamage);
         }
      }

      return success;
   }

   public static boolean hurtSplit(Entity target, DamageSource mainSource, float mainPercentage, DamageSource otherSource, float totalDamage) {
      float mainDamage = totalDamage * mainPercentage;
      boolean success = target.hurt(otherSource, totalDamage - mainDamage);
      if (mainDamage > 0.0F) {
         if (success && target.invulnerableTime < 60) {
            target.invulnerableTime = 0;
            target.hurt(mainSource, mainDamage);
            target.invulnerableTime = 20;
         } else {
            success = target.hurt(mainSource, mainDamage);
         }
      }

      return success;
   }

   public static boolean hurtSplitElemental(Entity pTarget, DamageSource source, float mainPercentage, float totalDamage) {
      if (source.tensura$getMagicType() == Magic.MagicType.SPIRITUAL) {
         return hurtSplit(pTarget, source, mainPercentage, copy(source).tensura$setMagicType(Magic.MagicType.ASPECTUAL), totalDamage);
      } else {
         return source.tensura$getMagicType() == null
            ? hurtSplit(
               pTarget,
               source.tensura$setMagicType(Magic.MagicType.SPIRITUAL),
               mainPercentage,
               copy(source).tensura$setMagicType(Magic.MagicType.ASPECTUAL),
               totalDamage
            )
            : hurtSplit(pTarget, copy(source).tensura$setMagicType(Magic.MagicType.SPIRITUAL), mainPercentage, source, totalDamage);
      }
   }

   public static void markHurt(LivingEntity target, @Nullable Entity attacker) {
      if (!target.hasInfiniteMaterials()) {
         if (target.level() instanceof ServerLevel serverLevel && target.invulnerableTime < 10) {
            serverLevel.getChunkSource().broadcastAndSend(target, new ClientboundHurtAnimationPacket(target.getId(), target.getHurtDir()));
         }

         if (attacker instanceof LivingEntity living) {
            if (attacker instanceof Player player) {
               target.setLastHurtByPlayer(player);
            }

            target.setLastHurtByMob(living);
         }
      }
   }

   public static boolean hasSpiritualDamageImmunity(Level level, LivingEntity target, @Nullable Entity attacker) {
      if (!target.isAlive()) {
         return true;
      } else if (target.hasInfiniteMaterials()) {
         return true;
      } else if (target.getType().is(TensuraEntityTags.NO_SPIRITUAL_DAMAGE)) {
         return true;
      } else if (TensuraGameRules.isLabyrinthPvpOff(level, target, attacker)) {
         return true;
      } else {
         return target == attacker
            ? false
            : SkillUtils.isSkillToggled(target, (ManasSkill)UniqueSkills.ANTI_SKILL.get())
               || TensuraStorages.getAbilityFrom(target).isAbilityInActivePreset((ManasSkill)UniqueSkills.ANTI_SKILL.get());
      }
   }

   public static boolean directSpiritualHurt(LivingEntity target, @Nullable Entity attacker, float amount, float resistPercentage) {
      return directSpiritualHurt(target, attacker, null, amount, resistPercentage);
   }

   public static boolean directSpiritualHurt(
      LivingEntity target, @Nullable Entity attacker, @Nullable DamageSource damageSource, float amount, float resistPercentage
   ) {
      Level level = target.level();
      if (level.isClientSide()) {
         return false;
      }

      if (hasSpiritualDamageImmunity(level, target, attacker)) {
         return false;
      }

      float resisted = amount * (1.0F - resistPercentage);
      int protection = TensuraEnchantmentHelper.getEnchantmentLevel(level, TensuraEnchantments.SPIRITUAL_PROTECTION, target);
      if (protection > 0) {
         resisted *= 1.0F - protection * 0.1F;
      }

      if (resisted <= 0.0F) {
         return false;
      }

      DamageSource source = damageSource == null ? TensuraDamageTypes.getEntityDamageSource(level, TensuraDamageTypes.SOUL_SCATTER, attacker) : damageSource;
      Changeable<Float> resistChangeable = Changeable.of(resistPercentage);
      Changeable<Float> damageChangeable = Changeable.of(resisted);
      Changeable<DamageSource> sourceChangeable = Changeable.of(source.tensura$setSpiritual());
      if (!((TensuraEntityEvents.SpiritualHurtEvent)TensuraEntityEvents.SPIRITUAL_HURT_EVENT.invoker())
         .hurt(target, attacker, amount, resistChangeable, damageChangeable, sourceChangeable)
         .isFalse()) {
         for (ManasSkillInstance instance : SkillAPI.getSkillsFrom(target).getLearnedSkills()) {
            if (!(instance.getMastery() >= 0.0)
               && instance.getSkill() instanceof ResistSkill resist
               && resist.getResistType().equals(ResistSkill.ResistType.RESISTANCE)
               && resist.isDamageResisted(target, (DamageSource)sourceChangeable.get(), instance)
               && ((Float)damageChangeable.get()).floatValue() > resist.getDamageAmountForLearning()) {
               resist.addLearnPoint(instance, target, 0, target.getAttributeValue(TensuraAttributes.ABILITY_LEARNING_GAIN));
            }
         }

         IExistence existence = TensuraStorages.getExistenceFrom(target);
         existence.setSpiritualHealth(Math.max(existence.getSpiritualHealth() - ((Float)damageChangeable.get()).floatValue(), 0.0));
         target.level().playSound(null, target, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.NEUTRAL, 1.0F, 1.0F);
         markHurt(target, attacker);
         if (existence.getSpiritualHealth() <= 0.0 && target.isAlive()) {
            target.getCombatTracker().recordDamage((DamageSource)sourceChangeable.get(), target.getHealth());
            target.setHealth(0.0F);
            target.setAbsorptionAmount(0.0F);
            target.die((DamageSource)sourceChangeable.get());
         }

         existence.markDirty();
         target.manasCore$sync();
         return true;
      } else {
         return false;
      }
   }

   public static boolean directSpiritualHurt(LivingEntity target, @Nullable Entity attacker, float amount) {
      return directSpiritualHurt(target, attacker, null, amount);
   }

   public static boolean directSpiritualHurt(LivingEntity target, @Nullable Entity attacker, @Nullable DamageSource source, float amount) {
      if (target.getType().is(TensuraEntityTags.NO_SPIRITUAL_DAMAGE)) {
         return false;
      } else if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
         return false;
      } else if (SkillUtils.isSkillToggled(target, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
         return amount <= target.getHealth() / 2.0F ? false : directSpiritualHurt(target, attacker, source, amount, 0.5F);
      } else {
         return directSpiritualHurt(target, attacker, source, amount, 0.0F);
      }
   }

   public static boolean isSeveranceDamage(DamageSource source, LivingEntity target, boolean countEngraving) {
      if (SkillUtils.shouldCancelSeverance(target, source)) {
         return false;
      }

      if (source.getDirectEntity() instanceof LivingEntity attacker) {
         if (TensuraEnchantmentHelper.getEnchantmentLevel(attacker.level(), TensuraEnchantments.SEVERANCE, attacker) > 0 && countEngraving) {
            return true;
         }

         if (attacker == source.getDirectEntity()) {
            if (attacker.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SEVERANCE_BLADE))) {
               if (isPhysicalAttack(source)) {
                  return true;
               }
            } else {
               MobEffectInstance elemental = attacker.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION));
               if (elemental != null && Element.byId(elemental.tensura$getOrCreateTag().getInt("elemental")).equals(Element.SPACE) && isPhysicalAttack(source)) {
                  return true;
               }
            }
         }
      }

      return source.tensura$getElement() == Element.SPACE;
   }

   public static boolean isUndeadPurifying(DamageSource source) {
      return isLightDamage(source) ? true : isHoly(source);
   }

   public static boolean isLightDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.tensura$getElement() == Element.LIGHT) {
         return true;
      } else if (damageSource.getMsgId().contains("light") && !damageSource.getMsgId().contains("lightning")) {
         return true;
      } else if (damageSource.getMsgId().contains("heaven")) {
         return true;
      } else if (damageSource.getMsgId().contains("sun")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("wisp") ? true : damageSource.getMsgId().contains("paradise");
      }
   }

   public static boolean isDarkDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.tensura$getElement() == Element.DARKNESS) {
         return true;
      } else if (damageSource.getMsgId().contains("dark")) {
         return true;
      } else if (damageSource.getMsgId().contains("hell")) {
         return true;
      } else if (damageSource.getMsgId().contains("abyss")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("ray_of_siphoning") ? true : damageSource.getMsgId().contains("void");
      }
   }

   public static boolean isEarthDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.tensura$getElement() == Element.EARTH) {
         return true;
      } else if (damageSource.getMsgId().contains("earth")) {
         return true;
      } else if (damageSource.getMsgId().contains("stone")) {
         return true;
      } else if (damageSource.getMsgId().contains("rock")) {
         return true;
      } else if (damageSource.getMsgId().contains("dirt")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("magma") ? true : damageSource.is(DamageTypes.IN_WALL);
      }
   }

   public static boolean isFireDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.tensura$getElement() == Element.FLAME) {
         return true;
      } else if (damageSource.type().effects().equals(DamageEffects.BURNING)) {
         return true;
      } else if (damageSource.getMsgId().contains("flame")) {
         return true;
      } else if (damageSource.getMsgId().contains("flaming")) {
         return true;
      } else if (damageSource.getMsgId().contains("scorch")) {
         return true;
      } else if (damageSource.getMsgId().contains("burn")) {
         return true;
      } else if (damageSource.getMsgId().contains("blaze")) {
         return true;
      } else {
         return damageSource.getMsgId().toLowerCase().contains("fire") ? true : damageSource.is(DamageTypeTags.IS_FIRE);
      }
   }

   public static boolean isGravityDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.getMsgId().contains("blackHole")) {
         return true;
      } else if (damageSource.getMsgId().contains("black_hole")) {
         return true;
      } else if (damageSource.getMsgId().contains("oppress")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("star") ? true : damageSource.getMsgId().contains("gravity");
      }
   }

   public static boolean isLightningDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.getMsgId().contains("lightning")) {
         return true;
      } else if (damageSource.getMsgId().contains("thunder")) {
         return true;
      } else if (damageSource.getMsgId().contains("electric")) {
         return true;
      } else if (damageSource.getMsgId().contains("electrocute")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("bolt") && !damageSource.getMsgId().contains("fire") ? true : damageSource.is(DamageTypeTags.IS_LIGHTNING);
      }
   }

   public static boolean isSoundDamage(DamageSource damageSource) {
      if (damageSource.is(TensuraDamageTypes.MIND_REQUIEM)) {
         return true;
      } else if (damageSource.getMsgId().contains("music")) {
         return true;
      } else if (damageSource.getMsgId().contains("sound")) {
         return true;
      } else if (damageSource.getMsgId().contains("shockwave")) {
         return true;
      } else if (damageSource.getMsgId().contains("echo")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("voice") ? true : damageSource.getMsgId().contains("sonic");
      }
   }

   public static boolean isWaterDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.tensura$getElement() == Element.WATER) {
         return true;
      } else if (damageSource.getMsgId().contains("water")) {
         return true;
      } else if (damageSource.getMsgId().contains("icicle")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("ice") ? true : damageSource.getMsgId().contains("aqua");
      }
   }

   public static boolean isWindDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.tensura$getElement() == Element.WIND) {
         return true;
      } else if (damageSource.getMsgId().contains("wind")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("gust") ? true : damageSource.getMsgId().contains("tornado");
      }
   }

   public static boolean isSpatialDamage(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.tensura$getElement() == Element.SPACE) {
         return true;
      } else if (damageSource.getMsgId().contains("sever")) {
         return true;
      } else if (damageSource.getMsgId().contains("dimension")) {
         return true;
      } else if (damageSource.getMsgId().contains("space")) {
         return true;
      } else if (damageSource.getMsgId().contains("ender")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("dragon_breath") ? true : damageSource.getMsgId().contains("spatial");
      }
   }

   public static boolean isAbnormal(DamageSource damageSource) {
      if (damageSource.getMsgId().contains("petrification")) {
         return true;
      } else if (damageSource.getMsgId().contains("petrificate")) {
         return true;
      } else if (damageSource.getMsgId().contains("insane")) {
         return true;
      } else if (damageSource.getMsgId().contains("insanity")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("fear") ? true : damageSource.getMsgId().contains("scare");
      }
   }

   public static boolean isCorrosion(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.getMsgId().contains("corrosion")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("wither") ? true : damageSource.is(DamageTypes.WITHER);
      }
   }

   public static boolean isCold(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.type().effects().equals(DamageEffects.FREEZING)) {
         return true;
      } else if (damageSource.getMsgId().contains("cold")) {
         return true;
      } else if (damageSource.getMsgId().contains("ice")) {
         return true;
      } else if (damageSource.getMsgId().contains("frost")) {
         return true;
      } else if (damageSource.getMsgId().contains("freeze")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("snow") ? true : damageSource.is(DamageTypes.FREEZE);
      }
   }

   public static boolean isHeat(DamageSource damageSource) {
      if (isTensuraMagic(damageSource)) {
         return false;
      } else if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
         return true;
      } else if (isFireDamage(damageSource)) {
         return true;
      } else if (damageSource.getMsgId().contains("hot")) {
         return true;
      } else if (damageSource.getMsgId().contains("warm")) {
         return true;
      } else if (damageSource.getMsgId().contains("heat")) {
         return true;
      } else if (damageSource.getMsgId().contains("hyperthermia")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("megiddo") ? true : damageSource.is(DamageTypes.LAVA);
      }
   }

   public static boolean isHoly(DamageSource damageSource) {
      if (damageSource.tensura$getElement() == Element.HOLY) {
         return true;
      } else if (damageSource.getMsgId().contains("holy")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("divine") ? true : damageSource.is(TensuraDamageTypes.HOLY_DAMAGE);
      }
   }

   public static boolean isPoison(DamageSource damageSource) {
      if (damageSource.is(TensuraDamageTypes.MAGICULE_POISON)) {
         return false;
      } else if (damageSource.getMsgId().contains("poison")) {
         return true;
      } else if (damageSource.getMsgId().contains("venom")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("toxic") ? true : damageSource.getMsgId().contains("toxin");
      }
   }

   public static boolean isTensuraMagic(DamageSource damageSource) {
      if (damageSource.is(TensuraDamageTypes.MAGICULE_POISON)) {
         return false;
      } else {
         return damageSource.tensura$getMagicType() != null && !damageSource.tensura$getMagicType().equals(Magic.MagicType.SPIRITUAL)
            ? true
            : damageSource.getMsgId().contains("magic");
      }
   }

   public static boolean isNaturalEffects(DamageSource damageSource) {
      if (isDarkDamage(damageSource)) {
         return true;
      } else if (isLightDamage(damageSource)) {
         return true;
      } else if (isEarthDamage(damageSource)) {
         return true;
      } else if (isFireDamage(damageSource)) {
         return true;
      } else if (isWaterDamage(damageSource)) {
         return true;
      } else if (isWindDamage(damageSource)) {
         return true;
      } else if (isSpatialDamage(damageSource)) {
         return true;
      } else if (isLightningDamage(damageSource)) {
         return true;
      } else if (isGravityDamage(damageSource)) {
         return true;
      } else if (isHeat(damageSource)) {
         return true;
      } else {
         return isCold(damageSource) ? true : isCorrosion(damageSource);
      }
   }

   public static boolean isPhysicalAttack(DamageSource damageSource) {
      if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return false;
      } else if (damageSource.tensura$isPhysicalConverted()) {
         return true;
      } else if (damageSource.tensura$getMagicType() != null) {
         return false;
      } else if (damageSource.tensura$getElement() != null) {
         return false;
      } else if (damageSource.is(TensuraTags.DamageTypes.IS_PHYSICAL)) {
         return true;
      } else if (damageSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
         return false;
      } else if (damageSource.is(DamageTypeTags.IS_FIRE)) {
         return false;
      } else if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
         return false;
      } else if (damageSource.is(DamageTypeTags.IS_LIGHTNING)) {
         return false;
      } else if (damageSource.is(DamageTypeTags.IS_DROWNING)) {
         return false;
      } else {
         return damageSource.is(DamageTypeTags.IS_FREEZING) ? false : !damageSource.type().effects().equals(DamageEffects.THORNS);
      }
   }

   public static boolean isBattlewill(DamageSource source, @Nullable Entity attacker) {
      if (source.tensura$getAbilityInstance() != null && source.tensura$getAbilityInstance().getSkill() instanceof Battlewill) {
         return true;
      } else {
         return source.getDirectEntity() == attacker && isPhysicalAttack(source) && attacker instanceof LivingEntity entity
            ? entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.AURA_SWORD))
               || entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.OGRE_GUILLOTINE))
            : false;
      }
   }

   public static boolean isPhysicalOrBattlewill(DamageSource source, LivingEntity attacker) {
      return source.tensura$getAbilityInstance() != null && source.tensura$getAbilityInstance().getSkill() instanceof Battlewill
         ? true
         : source.getDirectEntity() == attacker && isPhysicalAttack(source);
   }

   public static boolean isPierce(DamageSource damageSource) {
      if (damageSource.getDirectEntity() instanceof Projectile projectile) {
         if (projectile.getType().getDescriptionId().contains("arrow")) {
            return true;
         } else if (projectile.getType().getDescriptionId().contains("spear")) {
            return true;
         } else if (projectile.getType().getDescriptionId().contains("kunai")) {
            return true;
         } else {
            return projectile.getType().getDescriptionId().contains("trident") ? true : projectile.getType().getDescriptionId().contains("horn");
         }
      } else if (damageSource.getEntity() instanceof LivingEntity living) {
         return living.getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId().contains("spear")
            ? true
            : living.getItemInHand(InteractionHand.MAIN_HAND).getItem().getDescriptionId().contains("trident");
      } else {
         return false;
      }
   }

   public static boolean isSpiritual(DamageSource damageSource) {
      if (damageSource.is(TensuraTags.DamageTypes.IS_SPIRITUAL)) {
         return true;
      } else if (damageSource.is(TensuraTags.DamageTypes.IS_MENTAL)) {
         return true;
      } else if (damageSource.tensura$isSpiritual()) {
         return true;
      } else {
         return damageSource.getMsgId().contains("soul") ? true : damageSource.getMsgId().contains("spirit");
      }
   }

   public static boolean isTemperature(DamageSource damageSource) {
      if (isCold(damageSource)) {
         return true;
      } else if (isHeat(damageSource)) {
         return true;
      } else if (damageSource.getMsgId().contains("thermal")) {
         return true;
      } else {
         return damageSource.getMsgId().contains("thermia") ? true : damageSource.getMsgId().contains("temperature");
      }
   }

   public static boolean isEnergyDrain(DamageSource damageSource) {
      return damageSource.is(TensuraDamageTypes.ENERGY_DRAIN) || damageSource.is(TensuraDamageTypes.ENERGY_SOURCE_LOST);
   }

   public static float getWeaponDamage(LivingEntity attacker, @Nullable Entity target, boolean offhand, @Nullable DamageSource source) {
      return offhand ? getOffWeaponDamage(attacker, target, source) : getMainWeaponDamage(attacker, target, source);
   }

   public static float getMainWeaponDamage(LivingEntity attacker, @Nullable Entity target, @Nullable DamageSource source) {
      if (attacker.level() instanceof ServerLevel level) {
         float var5 = getWeaponBaseDamage(attacker.getMainHandItem(), EquipmentSlotGroup.MAINHAND);
         if (target != null && source != null) {
            var5 = EnchantmentHelper.modifyDamage(level, attacker.getMainHandItem(), target, source, var5);
         }

         return var5;
      } else {
         return 1.0F;
      }
   }

   public static float getOffWeaponDamage(LivingEntity attacker, @Nullable Entity target, @Nullable DamageSource source) {
      if (attacker.level() instanceof ServerLevel level) {
         float var5 = getWeaponBaseDamage(attacker.getOffhandItem(), EquipmentSlotGroup.OFFHAND);
         if (target != null && source != null) {
            var5 = EnchantmentHelper.modifyDamage(level, attacker.getOffhandItem(), target, source, var5);
         }

         return var5;
      } else {
         return 1.0F;
      }
   }

   public static float getWeaponBaseDamage(ItemStack item, EquipmentSlotGroup slot) {
      float damage = 1.0F;
      ItemAttributeModifiers modifiers = (ItemAttributeModifiers)item.get(DataComponents.ATTRIBUTE_MODIFIERS);
      if (modifiers != null) {
         for (Entry entry : modifiers.modifiers()) {
            if (entry.slot().equals(slot) && entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
               switch (entry.modifier().operation()) {
                  case ADD_VALUE:
                     damage += (float)entry.modifier().amount();
                     return damage;
                  case ADD_MULTIPLIED_TOTAL:
                  case ADD_MULTIPLIED_BASE:
                     damage *= (float)entry.modifier().amount();
                     return damage;
                  default:
                     return damage;
               }
            }
         }
      }

      return damage;
   }

   public static void applyElementalResistanceDegradation(DamageSource source) {
      if (source.getEntity() instanceof LivingEntity attacker) {
         if (attacker.getAttributeValue(TensuraAttributes.LAW_DEGRADATION) >= 1.0 && shouldApplyLawDegradation(attacker, source)) {
            source.tensura$setResistanceBypassLevel(2.0F);
            return;
         }

         if (source.tensura$getResistanceBypassLevel() >= 1.0F) {
            return;
         }

         if (isDarkDamage(source) && attacker.getAttributeValue(TensuraAttributes.DARKNESS_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         } else if (isLightDamage(source) && attacker.getAttributeValue(TensuraAttributes.LIGHTNING_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         } else if (isEarthDamage(source) && attacker.getAttributeValue(TensuraAttributes.EARTH_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         } else if (isFireDamage(source) && attacker.getAttributeValue(TensuraAttributes.FLAME_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         } else if (isSpatialDamage(source) && attacker.getAttributeValue(TensuraAttributes.SPACE_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         } else if (isWaterDamage(source) && attacker.getAttributeValue(TensuraAttributes.WATER_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         } else if (isWindDamage(source) && attacker.getAttributeValue(TensuraAttributes.WIND_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         } else if (isLightningDamage(source) && attacker.getAttributeValue(TensuraAttributes.LIGHTNING_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         } else if (isGravityDamage(source) && attacker.getAttributeValue(TensuraAttributes.GRAVITY_RESIST_DEGRADATION) >= 1.0) {
            source.tensura$setResistanceBypassLevel(Math.max(source.tensura$getResistanceBypassLevel(), 1.0F));
         }
      }
   }

   public static boolean shouldApplyLawDegradation(LivingEntity attacker, DamageSource source) {
      ManasSkillInstance sourceAbility = source.tensura$getAbilityInstance();
      if (sourceAbility == null) {
         return false;
      } else if (!sourceAbility.isMastered(attacker)) {
         return false;
      } else {
         return !sourceAbility.is(TensuraSkillTags.BATTLEWILL) && !sourceAbility.is(TensuraSkillTags.MAGIC)
            ? false
            : EnergyHelper.getMaxEP(attacker) >= LawManipulationSkill.CONFIG.resistBypassEP;
      }
   }

   public static int getDamageColor(DamageSource source) {
      if (source == null) {
         return 16777215;
      }

      if (isEnergyDrain(source)) {
         return 15686609;
      }

      if (isTensuraMagic(source)) {
         return 10278389;
      }

      if (isHoly(source)) {
         return ChatFormatting.GOLD.getColor();
      }

      if (isDarkDamage(source)) {
         return Element.DARKNESS.getColor();
      }

      if (isEarthDamage(source)) {
         return Element.EARTH.getColor();
      }

      if (isFireDamage(source)) {
         return Element.FLAME.getColor();
      }

      if (isLightDamage(source)) {
         return Element.LIGHT.getColor();
      }

      if (isSpatialDamage(source)) {
         return Element.SPACE.getColor();
      }

      if (isWaterDamage(source)) {
         return Element.WATER.getColor();
      }

      if (isWindDamage(source)) {
         return Element.WIND.getColor();
      }

      if (isLightningDamage(source)) {
         return 15788046;
      }

      if (isGravityDamage(source)) {
         return 6907099;
      }

      if (isHeat(source)) {
         return 16490018;
      }

      if (isCold(source)) {
         return 5636095;
      }

      if (isTemperature(source)) {
         return 2208948;
      }

      if (isPoison(source)) {
         return 7747016;
      }

      if (isCorrosion(source)) {
         return 9498256;
      }

      if (isSpiritual(source)) {
         return 16089632;
      }

      if (isBattlewill(source, source.getDirectEntity())) {
         return 16766720;
      }

      if (isPierce(source)) {
         return 11184810;
      }

      if (isPhysicalAttack(source)) {
         return 16777215;
      }

      Element element = source.tensura$getElement();
      return element != null ? element.getColor() : 11141375;
   }
}
