package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.misc.MadOrbsEntity;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.FlameOrbProjectile;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Iterator;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class BerserkSkill extends Skill {
   public static final ResourceLocation BERSERK = ResourceLocation.fromNamespaceAndPath("tensura", "berserk");
   private static final UniqueSkillConfig.Berserk CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Berserk;

   public BerserkSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
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
         case 0 -> "berserk.rage";
         case 1 -> "berserk.mad_ogre";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 0 ? CONFIG.magiculeCostRage : CONFIG.magiculeCostMadOgre;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 1 && this.canTick(instance, entity);
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE));
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE))) {
         CompoundTag tag = instance.getOrCreateTag();
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPermanentAttributeIfHigher(entity, TensuraAttributes.FLAME_BOOST, BERSERK, CONFIG.flameAuraBoost - 1.0, Operation.ADD_VALUE);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.FLAME_BOOST, BERSERK, CONFIG.flameAuraBoost - 1.0);
      entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE));
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (source.is(TensuraDamageTypes.FIRE_ELEMENTAL)) {
         return true;
      }

      if (TensuraDamageHelper.isPhysicalOrBattlewill(source, entity) && !EnergyHelper.isOutOfEnergy(entity, 0.0, CONFIG.magiculeCostFlameAura)) {
         target.invulnerableTime = 0;
         target.setRemainingFireTicks(CONFIG.flameAuraBurnTick);
         DamageSource flameSource = TensuraDamageTypes.getEntityDamageSource(entity.level(), TensuraDamageTypes.FIRE_ELEMENTAL, entity);
         if (target.hurt(
               flameSource.tensura$setMagicType(Magic.MagicType.SPIRITUAL)
                  .tensura$setElement(Element.FLAME)
                  .tensura$setMagiculeCost(CONFIG.magiculeCostFlameAura),
               (Float)amount.get() * CONFIG.flameAuraDamage
            )
            && target.isAlive()) {
            target.invulnerableTime = 0;
            target.hurtTime = 0;
         }
      }

      return true;
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      if (!instance.isMastered(entity)) {
         return true;
      }

      if (!entity.isShiftKeyDown()) {
         return true;
      }

      if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE))) {
         return true;
      }

      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (!(source.tensura$getBarrierBypassLevel() >= 2.0F) && source.getDirectEntity() != null) {
         EffectStorage.setCameraShake(entity, 5.0, 0.02F, 15);
         Iterator var5 = entity.level()
            .getEntitiesOfClass(MadOrbsEntity.class, entity.getBoundingBox().inflate(1.0), orbx -> entity.equals(orbx.getOwner()))
            .iterator();
         if (var5.hasNext()) {
            MadOrbsEntity orb = (MadOrbsEntity)var5.next();
            if (orb.getSpheres() <= 0) {
               return true;
            }

            amount.set((Float)amount.get() * CONFIG.defenceMultiplier);
            orb.setSpheres(orb.getSpheres() - 1);
            if (TensuraGameRules.canSkillGrief(entity.level())) {
               if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                  .grief(instance, entity.level(), entity, orb.getX(), orb.getY(), orb.getZ())
                  .isFalse()) {
                  entity.level().explode(entity, orb.getX(), orb.getY(), orb.getZ(), CONFIG.orbBlast, true, ExplosionInteraction.NONE);
                  ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                     .grief(instance, entity.level(), entity, orb.getX(), orb.getY(), orb.getZ());
               }
            } else {
               entity.level().explode(entity, orb.getX(), orb.getY(), orb.getZ(), CONFIG.orbBlast, false, ExplosionInteraction.NONE);
            }

            TensuraParticleHelper.spawnServerParticles(
               entity.level(), (ParticleOptions)TensuraParticleTypes.HEAT_EFFECT.get(), orb.getX(), orb.getY(), orb.getZ(), 30, 0.08, 0.08, 0.08, 0.3, false
            );
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION_EMITTER, 1.0);
            return true;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      if (mode == 0) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN))) {
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN));
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         } else {
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            int strength = instance.isMastered(entity) ? CONFIG.rageLevelMastered - 1 : CONFIG.rageLevel - 1;
            entity.addEffect(
               new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN), CONFIG.rageDuration, strength, true, false, true)
            );
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         }
      } else {
         if (!entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE))) {
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 600, 0, true, false, true));
            int duration = CONFIG.madOgreDuration;
            entity.addEffect(
               new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE),
                  duration,
                  instance.isMastered(entity) ? CONFIG.madOgreLevelMastered - 1 : CONFIG.madOgreLevel - 1,
                  true,
                  false,
                  true
               )
            );
            this.addMasteryPoint(instance, entity, this.getDefaultMasteryIncrement(instance, entity, mode) * CONFIG.masteryGainMultiplier);
            instance.setCoolDown(duration / 20 + CONFIG.cooldown, mode);
            EffectStorage.setCameraShake(entity, 10.0, 0.1F, 10);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.TRANSFORM_OGRE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
            );
            if (instance.isMastered(entity)) {
               this.summonOrbs(entity, level);
            }
         } else if (entity.isShiftKeyDown() || !instance.isMastered(entity)) {
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
            entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAD_OGRE));
            instance.setCoolDown(CONFIG.cooldown, mode);
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
         } else if (instance.isMastered(entity)) {
            Iterator var8 = level.getEntitiesOfClass(MadOrbsEntity.class, entity.getBoundingBox().inflate(1.0), orbx -> entity.equals(orbx.getOwner()))
               .iterator();
            if (var8.hasNext()) {
               MadOrbsEntity orb = (MadOrbsEntity)var8.next();
               if (this.shootOrbs(orb, entity, instance, mode)) {
                  entity.swing(InteractionHand.MAIN_HAND, true);
               }

               return;
            }

            this.summonOrbs(entity, entity.level());
            entity.swing(InteractionHand.MAIN_HAND, true);
         }
      }
   }

   public void summonOrbs(LivingEntity entity, Level level) {
      MadOrbsEntity orbs = new MadOrbsEntity(level, entity);
      orbs.setPos(entity.getX(), entity.getEyePosition().y(), entity.getZ());
      orbs.setSize((float)(0.75 * entity.getAttributeValue(Attributes.SCALE)));
      orbs.setLife(CONFIG.madOgreDuration);
      orbs.updateAngle(entity);
      level.addFreshEntity(orbs);
   }

   public boolean shootOrbs(MadOrbsEntity madOrbs, LivingEntity entity, ManasSkillInstance instance, int mode) {
      if (madOrbs.getSpheres() <= 0) {
         return false;
      }

      madOrbs.setSpheres(madOrbs.getSpheres() - 1);
      FlameOrbProjectile orb = new FlameOrbProjectile(madOrbs.level(), entity);
      orb.setSize((float)(0.75 * entity.getAttributeValue(Attributes.SCALE)));
      orb.setNoGravity(true);
      orb.setSpeed(1.0F);
      orb.setDamage(CONFIG.orbDamage);
      orb.setExplosionRadius(CONFIG.orbBlast);
      orb.setBurnTicks(100);
      orb.setSkill(entity, instance, this, mode, 0.15F);
      orb.setPosDirection(entity, TensuraFlyingProjectile.PositionDirection.MIDDLE);
      orb.shootFromRot(entity.getLookAngle());
      entity.level().addFreshEntity(orb);
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F);
      return true;
   }
}
