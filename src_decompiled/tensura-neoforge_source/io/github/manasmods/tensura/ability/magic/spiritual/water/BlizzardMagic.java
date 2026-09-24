package io.github.manasmods.tensura.ability.magic.spiritual.water;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.BlizzardEntity;
import io.github.manasmods.tensura.entity.projectile.magic.IceLanceProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class BlizzardMagic extends SpiritualMagic {
   public static final SpiritualMagicConfig.Blizzard CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Blizzard;

   public BlizzardMagic() {
      super(Element.WATER, SpiritualMagic.SpiritLevel.GREATER);
   }

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

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (this.hasBlizzard(entity)) {
         if (!entity.isShiftKeyDown()) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode, 0.1F)) {
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
               instance.setCoolDown(CONFIG.icicleCooldown, mode);
               IceLanceProjectile lance = new IceLanceProjectile(entity.level(), entity);
               lance.setSize(CONFIG.icicleSize / 40.0F);
               lance.setDelaySizeChange(CONFIG.icicleSize / 40.0F);
               lance.setDelayTick(40);
               lance.setLookDistance(40.0F);
               lance.setNoGravity(true);
               lance.setSpeed(1.75F);
               lance.setDamage(CONFIG.icicleDamage);
               lance.setSkill(entity, instance, this, mode, 0.2F);
               lance.setOwnerOffset(new Vec3(0.0, CONFIG.icicleSize, 1.0));
               lance.setPos(entity.getEyePosition().add(0.0, lance.getBbHeight() / -2.0F, 0.0).add(lance.getOwnerOffset()));
               entity.level().addFreshEntity(lance);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
         } else {
            for (BlizzardEntity blizzard : entity.level()
               .getEntitiesOfClass(
                  BlizzardEntity.class, entity.getBoundingBox().move(0.0, -CONFIG.blizzardRadius, 0.0), blizzardx -> blizzardx.getOwner() == entity
               )) {
               blizzard.setRemoveIn(20);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            }
         }
      }
   }

   @Override
   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!this.hasBlizzard(entity)) {
         super.addHeldAttributeModifiers(instance, entity, mode);
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      if (!entity.isShiftKeyDown() || !this.hasBlizzard(entity)) {
         super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
         if (castTime > 1) {
            MagicCircle.castMagicCircle(
               CONFIG.blizzardRadius / 2.0F,
               25,
               MagicCircleVariant.WATER,
               entity,
               instance.getOrCreateTag(),
               0.0F,
               Vec3.ZERO,
               instance,
               mode,
               Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
            );
         }
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      return this.hasBlizzard(entity) ? false : super.onHeld(instance, entity, heldTicks, mode);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (!entity.isShiftKeyDown()) {
         if (!this.hasBlizzard(entity)) {
            if (heldTicks >= this.getCastingTime(instance, entity)) {
               if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  instance.addMasteryPoint(entity);
                  BlizzardEntity blizzard = new BlizzardEntity(entity.level(), entity);
                  blizzard.setLife(instance.isMastered(entity) ? CONFIG.blizzardDurationMastered : CONFIG.blizzardDuration);
                  blizzard.setSize(CONFIG.blizzardRadius);
                  blizzard.setDamage(CONFIG.blizzardDamage);
                  blizzard.setFollowOwner(true);
                  MobEffectInstance effectInstance = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.CHILL), CONFIG.chillDuration, 1, true, false, true
                  );
                  blizzard.setMobEffect(effectInstance);
                  blizzard.setEffectStack(true);
                  blizzard.setSkill(entity, instance, this, mode);
                  blizzard.setPos(entity.getX(), entity.getY() + entity.getBbHeight() / 2.0F - blizzard.getSize(), entity.getZ());
                  entity.level().addFreshEntity(blizzard);
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.CAST_ICE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.WIND_BLOW.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }
            }
         }
      }
   }

   private boolean hasBlizzard(LivingEntity owner) {
      return !owner.level()
         .getEntitiesOfClass(BlizzardEntity.class, owner.getBoundingBox().move(0.0, -CONFIG.blizzardRadius, 0.0), blizzard -> blizzard.getOwner() == owner)
         .isEmpty();
   }
}
