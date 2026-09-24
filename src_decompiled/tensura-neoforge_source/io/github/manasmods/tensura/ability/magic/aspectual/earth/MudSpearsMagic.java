package io.github.manasmods.tensura.ability.magic.aspectual.earth;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.spike.MudSpikeEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class MudSpearsMagic extends AspectualMagic {
   private static final AspectualMagicConfig.MudSpears CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).MudSpears;

   public MudSpearsMagic() {
      super(AspectualMagic.AspectualType.EARTH);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryHigh;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? (instance.isMastered(entity) ? 1 : -1) : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return mode == 1 ? "mud_spears.spread" : "mud_spears.target";
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public boolean isCastingBlocked(ManasSkillInstance instance, LivingEntity entity) {
      if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_MAGIC))
         || instance.isMastered(entity) && !(entity.getAttributeValue(TensuraAttributes.LAW_DEGRADATION) <= 0.0)) {
         AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
         if (attributeInstance == null) {
            return false;
         }

         if (attributeInstance.hasModifier(this.getCastingResourceLocation())) {
            return false;
         }

         for (AttributeModifier modifier : attributeInstance.getModifiers()) {
            ResourceLocation location = modifier.id();
            if (location.getNamespace().equals("tensura")
               && !location.equals(((MudHandMagic)AspectualMagics.MUD_HAND.get()).getCastingResourceLocation())
               && location.getPath().endsWith("_casting")) {
               return true;
            }
         }

         return false;
      } else {
         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.anti_magic").withStyle(ChatFormatting.RED), true);
         }

         return true;
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (entity instanceof Player player) {
         int cast = this.getCastingTime(instance, player, this.isMudHandActive(entity) ? CONFIG.castTimeMudHand : this.getDefaultCastTime());
         this.applyCastingVisual(instance, player, heldTicks, mode, cast);
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         if (mode == 1) {
            MagicCircle.castMagicCircle(
               CONFIG.spreadRadius,
               25,
               MagicCircleVariant.EARTH,
               entity,
               instance.getOrCreateTag(),
               0.0F,
               Vec3.ZERO,
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         } else {
            MagicCircle.castMagicCircle(
               0.75F,
               25,
               MagicCircleVariant.EARTH,
               entity,
               instance.getOrCreateTag(),
               0.75F,
               Vec3.ZERO,
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0) {
         boolean mudHand = this.isMudHandActive(entity);
         int castTime = this.getCastingTime(instance, entity, mudHand ? CONFIG.castTimeMudHand : this.getDefaultCastTime());
         if (heldTicks < castTime) {
            return;
         }

         Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
         if (target == null) {
            entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
            return;
         }

         if (!target.onGround() && !entity.level().getBlockState(target.getOnPos().below(2)).isSolid()) {
            entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
            return;
         }

         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         instance.addMasteryPoint(entity);
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         this.spawnSpikesTowardTarget(target, entity, instance, this, mode, mudHand ? CONFIG.magicDamage + CONFIG.magicDamageBonus : CONFIG.magicDamage);
      } else {
         if (heldTicks < this.getCastingTime(instance, entity)) {
            return;
         }

         if (!entity.onGround()) {
            entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
            return;
         }

         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return;
         }

         entity.swing(InteractionHand.MAIN_HAND, true);
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         this.spreadSpikes(instance, entity, mode, CONFIG.spreadRadius / 2.0F, 0.75F);
         this.spreadSpikes(instance, entity, mode, CONFIG.spreadRadius, 1.0F);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
      }
   }

   private void spawnSpikesTowardTarget(Entity target, LivingEntity caster, ManasSkillInstance instance, TensuraSkill skill, int mode, float damage) {
      Vec3 start = caster.position();
      Vec3 end = target.position();
      Vec3 direction = end.subtract(start).normalize();
      double spacing = 2.0;
      double distance = start.distanceTo(end);
      int steps = (int)(distance / spacing);

      for (int i = steps - 1; i >= 0; i--) {
         float size = Math.max(target.getBbHeight() / 2.0F / steps * (steps - i), 0.2F);
         Vec3 pos = end.subtract(direction.scale(i * spacing));
         MudSpikeEntity spike = new MudSpikeEntity(caster.level(), caster);
         spike.setPos(ObjectSelectionHelper.getNearestGround(pos, spike.level(), 5.0, spike));
         spike.setSize(size);
         spike.setSkill(caster, instance, skill, mode);
         spike.setDamage(CONFIG.earthDamage);
         spike.setSecondaryDamage(damage);
         spike.setPitch(ObjectSelectionHelper.getYRotFromVector(end.subtract(start).normalize()));
         spike.setYaw(-20.0F);
         caster.level().addFreshEntity(spike);
         spike.triggerAnim("controller", "start");
         EffectStorage.setCameraShake(spike, 3.0, 0.01F, 10);
         caster.level()
            .playSound(null, spike.getX(), spike.getY(), spike.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   private void spreadSpikes(ManasSkillInstance instance, LivingEntity entity, int mode, float radius, float size) {
      Vec3 center = entity.position();
      int amount = (int)radius * 3;

      for (int i = 0; i < amount; i++) {
         double angle = (Math.PI * 2) / amount * i;
         double dx = Math.cos(angle) * radius;
         double dz = Math.sin(angle) * radius;
         double x = center.x() + dx;
         double y = center.y();
         double z = center.z() + dz;
         MudSpikeEntity spike = new MudSpikeEntity(entity.level(), entity);
         spike.setSkill(entity, instance, this, mode);
         spike.setPos(x, y, z);
         spike.setDamage(CONFIG.spreadDamage);
         spike.setSecondaryDamage(CONFIG.spreadDamage);
         spike.setContactDamage(CONFIG.spreadDamage);
         spike.setLife(CONFIG.spreadDuration);
         spike.setYaw(-15.0F);
         spike.setPitch(ObjectSelectionHelper.getYRotFromVector(spike.position().subtract(center).normalize()));
         spike.setSize(size);
         entity.level().addFreshEntity(spike);
         spike.triggerAnim("controller", "start");
         EffectStorage.setCameraShake(spike, 3.0, 0.01F, 10);
         entity.level()
            .playSound(null, spike.getX(), spike.getY(), spike.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }

   protected boolean isMudHandActive(LivingEntity entity) {
      AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attributeInstance != null) {
         for (AttributeModifier modifier : attributeInstance.getModifiers()) {
            ResourceLocation location = modifier.id();
            if (location.equals(((MudHandMagic)AspectualMagics.MUD_HAND.get()).getCastingResourceLocation())) {
               return true;
            }
         }
      }

      Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
      if (target instanceof LivingEntity living) {
         MobEffectInstance instance = living.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE));
         if (instance == null) {
            return false;
         }

         AbilitySlot slot = instance.tensura$getSourceAbility();
         return slot == null ? false : slot.getSkill() == AspectualMagics.MUD_HAND.get();
      } else {
         return false;
      }
   }
}
