package io.github.manasmods.tensura.ability.skill.unique;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.manascore.skill.impl.TickingSkill;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.network.s2c.OpenIllusionItemScreenPayload;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public class FalsifierSkill extends Skill {
   private static final UniqueSkillConfig.Falsifier CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Falsifier;
   protected static final ResourceLocation FAKE = ResourceLocation.fromNamespaceAndPath("tensura", "falsifier");

   public FalsifierSkill() {
      super(Skill.SkillType.UNIQUE);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, FAKE, CONFIG.fakeSpeedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
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
         case 0 -> "falsifier.concealment";
         case 1 -> "falsifier.illusion";
         case 2 -> "falsifier.fake_death";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return true;
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER));
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER));
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      switch (mode) {
         case 0:
            int cooldown = instance.isMastered(entity) ? CONFIG.concealmentCooldownMastered : CONFIG.concealmentCooldown;
            if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER))) {
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
               entity.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER));
               instance.setCoolDown(cooldown, mode);
            } else {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  return;
               }

               instance.addMasteryPoint(entity);
               entity.addEffect(
                  new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER), CONFIG.concealmentDuration, 0, false, false, false)
               );
               instance.setCoolDown(cooldown + CONFIG.concealmentDuration / 20, mode);
               entity.level()
                  .playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.PRESENCE_CONCEALMENT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            }
            break;
         case 1:
            if (!(entity instanceof ServerPlayer player)) {
               return;
            }

            NetworkManager.sendToPlayer(player, new OpenIllusionItemScreenPayload(player.getId(), this.getRegistryName()));
            player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
      }
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(entity, instance, 2)) {
         return true;
      }

      if (!isFakingDeath(entity)) {
         return true;
      }

      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (source.tensura$getBarrierBypassLevel() >= 2.0F) {
         return true;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER))) {
         return true;
      }

      Level level = entity.level();
      CloneEntity clone = new CloneEntity((EntityType<? extends CloneEntity>)HumanEntityTypes.CLONE.get(), level);
      clone.setStatic(true);
      if (entity instanceof Player player) {
         clone.tame(player);
      }

      clone.setSkill(instance);
      clone.copyStatsAndSkills(entity, CloneEntity.CopySkill.NONE, true);
      clone.setHealth(entity.getHealth());
      clone.setPos(entity.position());
      CloneEntity.copyRotation(entity, clone);
      level.addFreshEntity(clone);
      amount.set((Float)amount.get() * CONFIG.fakeInputMultiplier);
      clone.hurt(source.tensura$setBarrierBypassLevel(3.0F), clone.getMaxHealth() * 10.0F);
      entity.addEffect(
         new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER), CONFIG.fakeConcealmentDuration, 0, false, false, false)
      );
      SubordinateHelper.presenceConcealing(entity, 40.0);
      if (level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)) {
         clone.getCombatTracker().recordDamage(source.tensura$setBarrierBypassLevel(3.0F), 1.0F);
         Component deathMessage = clone.getCombatTracker().getDeathMessage();

         for (Player everyone : level.players()) {
            if (everyone != entity) {
               everyone.sendSystemMessage(deathMessage);
            }
         }
      }

      return false;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 2) {
         return false;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER))) {
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER), 20, 0, false, false, false));
      }

      return heldTicks <= CONFIG.fakeMaxTime;
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 2 && TickingSkill.isTickingSkill(entity, this, 2)) {
         if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.FALSIFIER))) {
            instance.setCoolDown(this.isMastered(instance, entity) ? 5 : 10, mode);
         }
      }
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      if (mode != 2) {
         return 0.0;
      } else {
         return instance.isMastered(entity) ? (CONFIG.fakeSpeedMultiplierMastered - 1.0) / (CONFIG.fakeSpeedMultiplier - 1.0) : 1.0;
      }
   }

   public static boolean isFakingDeath(LivingEntity entity) {
      AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      return attributeInstance == null ? false : attributeInstance.getModifier(FAKE) != null;
   }
}
