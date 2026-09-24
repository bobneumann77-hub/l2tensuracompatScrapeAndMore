package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.ReflectorEchoProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import java.text.DecimalFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ReflectorSkill extends Skill {
   private static final UniqueSkillConfig.Reflector CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Reflector;
   protected static final ResourceLocation COUNTER = ResourceLocation.fromNamespaceAndPath("tensura", "reflector");
   private final DecimalFormat decimalFormat = new DecimalFormat("#.#");

   public ReflectorSkill() {
      super(Skill.SkillType.UNIQUE);
      double amount = CONFIG.counterSpeedMultiplier - 1.0;
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(Attributes.ATTACK_DAMAGE, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(Attributes.ATTACK_SPEED, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(Attributes.JUMP_STRENGTH, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(TensuraAttributes.DODGE_NEGATE_CHANCE, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(ManasCoreAttributes.GLIDE_SPEED_MULTIPLIER, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, COUNTER, amount, Operation.ADD_MULTIPLIED_TOTAL);
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
         case 0 -> "reflector.reflection";
         case 1 -> "reflector.counter";
         default -> super.getModeId(instance, mode);
      };
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && entity.isShiftKeyDown() && instance.getMastery() >= 0.0;
   }

   public static boolean hasFullCounter(LivingEntity entity) {
      AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      return attributeInstance == null ? false : attributeInstance.hasModifier(COUNTER);
   }

   public void onProjectileHit(
      ManasSkillInstance instance,
      LivingEntity entity,
      EntityHitResult hitResult,
      Projectile projectile,
      Changeable<ProjectileDeflection> deflection,
      Changeable<ProjectileHitResult> result
   ) {
      if (!instance.onCoolDown(1)) {
         if (hasFullCounter(entity)) {
            Vec3 location = hitResult.getLocation();
            projectile.setPos(location);
            Vec3 reverse = projectile.getDeltaMovement().reverse().scale(CONFIG.counterProjectileSpeedMultiplier);
            projectile.setDeltaMovement(reverse);
            projectile.setOwner(entity);
            if (projectile instanceof TensuraFlyingProjectile tensuraProjectile) {
               tensuraProjectile.setDamage(tensuraProjectile.getDamage() * CONFIG.counterDamageMultiplier);
            }

            result.set(ProjectileHitResult.PASS);
            deflection.set(ProjectileDeflection.REVERSE);
            instance.setCoolDown(CONFIG.counterCooldown, 1);
            instance.removeAttributeModifiers(entity, 1);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(),
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               25,
               0.08,
               0.08,
               0.08,
               0.5,
               true
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getGoldWave(1.0F, entity.getBbWidth() * 5.0F, 0.0F, false),
               entity.getX(),
               entity.getY() + entity.getBbHeight() / 2.0F,
               entity.getZ()
            );
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.REFLECTION.get(), TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F
               );
         }
      }
   }

   public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity, DamageSource damageSource, float amount) {
      if (instance.onCoolDown(1)) {
         return true;
      }

      if (!hasFullCounter(entity)) {
         return true;
      }

      if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (damageSource.getDirectEntity() == null) {
         return true;
      }

      if (damageSource.tensura$getBarrierBypassLevel() >= 1.0F) {
         return true;
      }

      Entity sourceEntity = damageSource.getEntity();
      if (sourceEntity != null) {
         sourceEntity.invulnerableTime = 0;
         sourceEntity.hurt(damageSource.tensura$setDodgeBypass(), amount * CONFIG.counterDamageMultiplier);
      }

      instance.setCoolDown(CONFIG.counterCooldown, 1);
      instance.removeAttributeModifiers(entity, 1);
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(),
         entity.getX(),
         entity.getY(),
         entity.getZ(),
         25,
         0.08,
         0.08,
         0.08,
         0.5,
         true
      );
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getGoldWave(1.0F, entity.getBbWidth() * 5.0F, 0.0F, false),
         entity.getX(),
         entity.getY() + entity.getBbHeight() / 2.0F,
         entity.getZ()
      );
      entity.swing(InteractionHand.MAIN_HAND, true);
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.REFLECTION.get(), TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
      return false;
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      if (mode != 0 && !instance.onCoolDown(mode)) {
         return instance.isMastered(entity) ? (CONFIG.counterSpeedMultiplierMastered - 1.0) / (CONFIG.counterSpeedMultiplier - 1.0) : 1.0;
      } else {
         return 0.0;
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode == 0) {
         return false;
      }

      if (instance.onCoolDown(mode)) {
         return false;
      }

      if (instance.isMastered(entity)) {
         return true;
      }

      if (entity instanceof Player player && player.getAbilities().flying) {
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      }

      return true;
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity owner, DamageSource source, Changeable<Float> amount) {
      CompoundTag tag = instance.getOrCreateTag();
      float newEcho = tag.getFloat("echo") + (Float)amount.get();
      if (newEcho > this.getEchoLimit(owner)) {
         newEcho = this.getEchoLimit(owner);
      }

      tag.putFloat("echo", newEcho);
      instance.markDirty();
      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode != 1 && !entity.isShiftKeyDown()) {
         CompoundTag tag = instance.getOrCreateTag();
         float echoPoints = tag.getFloat("echo");
         if (echoPoints > 0.0F) {
            instance.addMasteryPoint(entity);
            double scale = tag.getDouble("scale");
            if (scale < 0.1 || echoPoints <= 1.0F) {
               scale = 1.0;
            }

            ReflectorEchoProjectile echo = new ReflectorEchoProjectile(entity.level(), entity);
            echo.setSpeed(1.5F);
            echo.setNoGravity(true);
            echo.setPiercingEntity(true);
            float baseDamage = echoPoints * (float)scale;
            float multiplier = instance.isMastered(entity) ? CONFIG.reflectionDamageMultiplierMastered : CONFIG.reflectionDamageMultiplier;
            echo.setDamage(baseDamage * multiplier);
            echo.setSkill(entity, instance, this, mode);
            echo.setPosAndShoot(entity);
            entity.level().addFreshEntity(echo);
            tag.putFloat("echo", echoPoints - baseDamage);
            if (entity instanceof Player player) {
               player.displayClientMessage(
                  Component.translatable("tensura.skill.reflector.remaining_echo", new Object[]{this.decimalFormat.format(echoPoints - baseDamage)})
                     .withStyle(ChatFormatting.RED),
                  true
               );
            }

            instance.markDirty();
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.REFLECTION.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.skill.reflector.remaining_echo", new Object[]{0}).withStyle(ChatFormatting.RED), true);
         }
      }
   }

   public float getEchoLimit(LivingEntity entity) {
      int bonus = (int)(TensuraStorages.getExistenceFrom(entity).getEP() * CONFIG.bonusPointMultiplier);
      return CONFIG.maximumPoint + bonus;
   }

   public void onRespawn(ManasSkillInstance instance, ServerPlayer owner, boolean conqueredEnd) {
      if (!conqueredEnd) {
         instance.getOrCreateTag().putFloat("echo", 0.0F);
         instance.markDirty();
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      double newScale = tag.getDouble("scale") + delta * 0.1;
      if (newScale > 1.0) {
         newScale = 0.1;
      } else if (newScale < 0.1) {
         newScale = 1.0;
      }

      if (tag.getDouble("scale") != newScale) {
         tag.putDouble("scale", newScale);
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.power_scale", new Object[]{this.decimalFormat.format(newScale * 100.0) + "%"})
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
               true
            );
         }

         instance.markDirty();
      }
   }
}
