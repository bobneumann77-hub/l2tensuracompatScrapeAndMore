package io.github.manasmods.tensura.ability.magic.summon;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.SpawnPointHelper;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.config.ability.magic.SummoningMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.beam.SummoningBeam;
import io.github.manasmods.tensura.entity.monster.ArchDaemonEntity;
import io.github.manasmods.tensura.entity.variant.DaemonVariant;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class SummonDaemonMagic extends SummoningMagic<TamableAnimal> {
   public static final SummoningMagicConfig.SummonDaemon CONFIG = ((SummoningMagicConfig)ConfigRegistry.getConfig(SummoningMagicConfig.class)).SummonDaemon;

   public int getModes(ManasSkillInstance instance) {
      return 4;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> 3;
            default -> 0;
            case 2 -> 1;
            case 3 -> instance.isMastered(entity) ? 2 : 1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> instance.isMastered(entity) ? 2 : 3;
            case 2 -> 3;
            default -> 0;
         };
      }
   }

   @Override
   public Component getModeName(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> Component.translatable("entity.tensura.greater_daemon");
         case 2 -> Component.translatable("entity.tensura.arch_daemon");
         case 3 -> Component.translatable("tensura.skill.mode.summon_daemon.random");
         default -> Component.translatable("entity.tensura.lesser_daemon");
      };
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public boolean isInstantCast(ManasSkillInstance instance, LivingEntity entity) {
      return false;
   }

   @Override
   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return false;
   }

   @Override
   public int getSuccessCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown;
   }

   @Override
   public boolean canRemoveSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return false;
   }

   @Override
   public boolean isSummoningDisabled(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (mode != -1 && !instance.onCoolDown(mode)) {
         if (entity.level().dimension() == TensuraDimensions.HELL) {
            if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.location").withStyle(ChatFormatting.RED), true);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   private boolean canBeSacrificed(LivingEntity target, TamableAnimal summon) {
      if (!target.getType().is(TensuraEntityTags.NO_SACRIFICE) && !RaceUtils.isSpiritual(target)) {
         if (target.getHealth() <= target.getMaxHealth() * CONFIG.sacrificeHP) {
            return true;
         } else {
            return TensuraStorages.getExistenceFrom(target).getSpiritualHealth()
                  <= target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH) * CONFIG.sacrificeSHP
               ? true
               : EnergyHelper.getMaxEP(target) < EnergyHelper.getMaxEP(summon) * CONFIG.sacrificeEP;
         }
      } else {
         return false;
      }
   }

   private boolean canCopySkill(ManasSkillInstance manasSkill) {
      if (manasSkill.isTemporarySkill() || manasSkill.getMastery() < 0.0) {
         return false;
      } else if (manasSkill.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      } else if (!manasSkill.is(TensuraSkillTags.SKILLS)) {
         return false;
      } else {
         return manasSkill.is(TensuraSkillTags.UNIQUE_SKILLS) ? false : !manasSkill.is(TensuraSkillTags.ULTIMATE_SKILLS);
      }
   }

   public boolean isSummoningRequirementFulfilled(ManasSkillInstance instance, LivingEntity entity, TamableAnimal summon, int mode) {
      double cost = EnergyHelper.getMaxEP(summon) * CONFIG.costMultiplier;
      double sacrificeEP = 0.0;
      List<ManasSkill> sacrificeSkills = new ArrayList<>();

      for (Mob sacrifice : entity.level()
         .getEntitiesOfClass(Mob.class, summon.getBoundingBox().inflate(CONFIG.sacrificeRadius), target -> this.canBeSacrificed(target, summon))) {
         DamageSource source = TensuraDamageTypes.getEntityDamageSource(entity.level(), TensuraDamageTypes.SOUL_SCATTER, entity);
         if (sacrifice.hurt(source, sacrifice.getMaxHealth())) {
            sacrificeEP += EnergyHelper.getEPGain(sacrifice);

            for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(sacrifice).getLearnedSkills()) {
               if (this.canCopySkill(skill) && !sacrificeSkills.contains(skill.getSkill())) {
                  sacrificeSkills.add(skill.getSkill());
               }
            }
         }
      }

      if (this.isOutOfEnergy(entity, instance, (double)0.0, (double)(cost - sacrificeEP))) {
         return false;
      }

      EnergyHelper.gainMagicule(summon, sacrificeEP * CONFIG.sacrificeEPBoost, EnergyHelper.GainType.MAX);

      for (ManasSkill skill : sacrificeSkills) {
         ManasSkillInstance skillInstance = skill.createDefaultInstance();
         if (skillInstance.canBeToggled(summon)) {
            skillInstance.setToggled(true);
         }

         if (SkillHelper.learnSkill(summon, skillInstance, -2)) {
            skillInstance.onToggleOn(summon);
         }
      }

      return true;
   }

   private Pair<Double, Double> getPlayerSummonEPRange(int mode) {
      return switch (mode) {
         case 1 -> Pair.of(CONFIG.greaterEP, CONFIG.archEP);
         case 2 -> Pair.of(CONFIG.archEP, CONFIG.archEPMax);
         default -> Pair.of(CONFIG.lesserEP, CONFIG.greaterEP);
      };
   }

   @Override
   public void startSummoning(ManasSkillInstance instance, LivingEntity entity, int mode) {
      int summonType = mode;
      if (mode == 3) {
         float random = entity.getRandom().nextFloat();
         if (random <= CONFIG.archChance) {
            summonType = 2;
         } else if (random <= CONFIG.greaterChance) {
            summonType = 1;
         } else {
            summonType = 0;
         }
      }

      super.startSummoning(instance, entity, summonType);
      if (this.canSummonPlayers(instance, entity, summonType)) {
         CompoundTag tag = instance.getOrCreateTag();
         tag.putInt("SummonType", summonType);
         if (!this.isSummoningDisabled(instance, entity, summonType)) {
            MinecraftServer minecraftserver = ((ServerLevel)entity.level()).getServer();
            ServerLevel hell = minecraftserver.getLevel(TensuraDimensions.HELL);
            if (hell != null) {
               int offsetX = 0;
               int offsetZ = 0;

               for (int i = 0; i < 20; i++) {
                  ChunkAccess chunk = hell.getChunk(offsetX, offsetZ, ChunkStatus.FULL, true);
                  if (chunk == null) {
                     offsetX += hell.getRandom().nextInt(1, 3);
                     offsetZ += hell.getRandom().nextInt(1, 3);
                  } else {
                     int posX = (offsetX << 4) + 8;
                     int posZ = (offsetZ << 4) + 8;
                     BlockPos pos = new BlockPos(posX, chunk.getHeight(Types.WORLD_SURFACE, posX, posZ), posZ);
                     if (!hell.getBlockState(pos.below()).isSolid()) {
                        offsetX += hell.getRandom().nextInt(1, 3);
                        offsetZ += hell.getRandom().nextInt(1, 3);
                     } else {
                        List<SummoningBeam> list = hell.getEntitiesOfClass(SummoningBeam.class, new AABB(pos).inflate(8.0));
                        if (list.isEmpty()) {
                           SummoningBeam beam = new SummoningBeam(hell, entity);
                           Pair<Double, Double> pair = this.getPlayerSummonEPRange(summonType);
                           beam.setMinEP((Double)pair.getFirst());
                           beam.setMaxEP((Double)pair.getSecond());
                           beam.setLife(50);
                           beam.setSize(3.0F);
                           beam.setPos(pos.getCenter());
                           hell.addFreshEntity(beam);
                           hell.playSound(null, beam.getX(), beam.getY(), beam.getZ(), SoundEvents.BEACON_ACTIVATE, TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
                           hell.setChunkForced(offsetX, offsetZ, true);
                           tag.putUUID("BeamUUID", beam.getUUID());
                           instance.markDirty();
                           break;
                        }

                        offsetX += hell.getRandom().nextInt(1, 3);
                        offsetZ += hell.getRandom().nextInt(1, 3);
                     }
                  }
               }
            }
         }
      }
   }

   public void addAdditionalSummonData(ManasSkillInstance instance, LivingEntity entity, TamableAnimal summon, int mode) {
      IExistence existence = TensuraStorages.getExistenceFrom(summon);
      existence.setSummonedSecond(CONFIG.summonDuration);
      existence.setSummonedAbility(this, mode);
      existence.markDirty();
   }

   @Override
   public void createSummon(ManasSkillInstance instance, LivingEntity entity, int mode, Vec3 position, String summonId) {
      if (this.canSummonPlayers(instance, entity, mode)) {
         MinecraftServer minecraftserver = ((ServerLevel)entity.level()).getServer();
         ServerLevel hell = minecraftserver.getLevel(TensuraDimensions.HELL);
         if (hell != null) {
            CompoundTag tag = instance.getOrCreateTag();
            if (tag.contains("BeamUUID") && hell.getEntity(tag.getUUID("BeamUUID")) instanceof SummoningBeam beam) {
               beam.findSummon();
               beam.setRemoveIn(40);
               Player foundSummon = beam.getFoundSummon();
               if (foundSummon != null) {
                  CloneEntity body = new CloneEntity((EntityType<? extends CloneEntity>)HumanEntityTypes.CLONE.get(), entity.level());
                  body.tame(foundSummon);
                  body.setSkill(instance);
                  body.setStatic(true);
                  body.setNoAi(true);
                  body.noPhysics = true;
                  EnergyHelper.setMaxMagicule(body, EnergyHelper.getBaseMaxMagicule(foundSummon));
                  EnergyHelper.setMaxAura(body, EnergyHelper.getBaseMaxAura(foundSummon));
                  body.copyStatsAndSkills(foundSummon, CloneEntity.CopySkill.NONE, true);
                  body.setPos(position.add(0.0, -1.5 * body.getBbHeight(), 0.0));
                  body.lookAt(Anchor.EYES, entity.getEyePosition());
                  entity.level().addFreshEntity(body);
                  tag.putUUID(summonId, body.getUUID());
                  return;
               }
            }
         }
      }

      CompoundTag tag = instance.getOrCreateTag();
      if (tag.contains("SummonType")) {
         mode = tag.getInt("SummonType");
      }

      super.createSummon(instance, entity, mode, position, summonId);
   }

   @Override
   public boolean callForthSummon(ManasSkillInstance instance, LivingEntity entity, int mode, UUID summonUUID, int summoningTime) {
      Level level = entity.level();
      Entity summon = ((ServerLevel)level).getEntity(summonUUID);
      if (summon instanceof TamableAnimal mob) {
         summon.setPos(summon.position().add(0.0, mob.getBbHeight() * 1.5 / 39.0, 0.0));
         TensuraParticleHelper.addServerParticlesAroundSelf(mob, this.getSummoningParticle(instance, mode), 3.0);
         mob.lookAt(entity, 30.0F, 30.0F);
         if (summoningTime == 40) {
            if (this.isSummoningRequirementFulfilled(instance, entity, mob, mode)) {
               instance.addMasteryPoint(entity);
               if (this.canSummonPlayers(instance, entity, mode)) {
                  MinecraftServer minecraftserver = ((ServerLevel)entity.level()).getServer();
                  ServerLevel hell = minecraftserver.getLevel(TensuraDimensions.HELL);
                  if (hell != null) {
                     CompoundTag tag = instance.getOrCreateTag();
                     if (hell.getEntity(tag.getUUID("BeamUUID")) instanceof SummoningBeam beam) {
                        Player foundSummon = beam.getFoundSummon();
                        if (foundSummon != null) {
                           SpawnPointHelper.teleportToAcrossDimensions(
                              foundSummon, (ServerLevel)level, mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot()
                           );
                           foundSummon.playSound(this.getSummoningSound(instance, mode), 3.0F, 1.0F);
                           TensuraParticleHelper.addServerParticlesAroundSelf(foundSummon, ParticleTypes.FLASH, 2.0);
                           TensuraParticleHelper.addServerParticlesAroundSelf(foundSummon, ParticleTypes.FLASH, 3.0);
                           instance.setCoolDowns(this.getSuccessCooldown(instance, entity));
                           this.onPostSummon(instance, entity, foundSummon, mob, mode);
                           mob.discard();
                           return true;
                        }
                     }
                  }
               }

               summon.noPhysics = false;
               mob.setNoAi(false);
               mob.playSound(this.getSummoningSound(instance, mode), 3.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH, 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(mob, ParticleTypes.FLASH, 3.0);
               this.onPostSummon(instance, entity, mob, mode);
               instance.setCoolDowns(this.getSuccessCooldown(instance, entity));
               return true;
            }

            this.removeFailedSummon(instance, entity, mode);
            return false;
         }

         mob.noPhysics = true;
      }

      return false;
   }

   public void onPostSummon(ManasSkillInstance instance, LivingEntity entity, TamableAnimal summon, int mode) {
      super.onPostSummon(instance, entity, summon, mode);
      IExistence existence = TensuraStorages.getExistenceFrom(summon);
      double EP = EnergyHelper.getMaxEP(summon);
      double multiplier = CONFIG.obeyMultiplier;
      if (summon instanceof ArchDaemonEntity daemon) {
         if (daemon.getVariant().equals(DaemonVariant.Linage.RED) || daemon.getVariant().equals(DaemonVariant.Linage.YELLOW)) {
            multiplier = CONFIG.obeyMultiplierNonNegotiable;
         } else if (daemon.getVariant().equals(DaemonVariant.Linage.BLACK) || daemon.getVariant().equals(DaemonVariant.Linage.PURPLE)) {
            multiplier = CONFIG.obeyMultiplierWhimsical;
         }
      }

      if (EnergyHelper.getMaxEP(entity) >= EP * multiplier) {
         if (entity instanceof Player player) {
            summon.tame(player);
         }

         existence.setSummoner(entity.getUUID());
         existence.markDirty();
      } else {
         summon.setTarget(entity);
         summon.addEffect(
            new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), existence.getSummonedSecond() * 20, 0, false, false, false)
         );
         entity.sendSystemMessage(Component.translatable("tensura.summon.disobey", new Object[]{summon.getDisplayName()}).withStyle(ChatFormatting.RED));
      }
   }

   private void onPostSummon(ManasSkillInstance instance, LivingEntity entity, Player summon, TamableAnimal clone, int mode) {
      this.removeAttributeModifiers(instance, entity, mode);
      IExistence existence = TensuraStorages.getExistenceFrom(summon);
      existence.setSummonedSecond(CONFIG.summonDuration);
      existence.setSummonedAbility(this, mode);
      existence.setSpiritualForm(false);
      double EP = EnergyHelper.getMaxEP(summon);
      if (EnergyHelper.getMaxEP(entity) >= EP * CONFIG.obeyMultiplier) {
         existence.setSummoner(entity.getUUID());
         existence.markDirty();
      }

      double sacrificeEP = TensuraStorages.getExistenceFrom(clone).getGainedEP();
      if (sacrificeEP > 0.0) {
         AttributeHelper.addPermanentAttribute(summon, TensuraAttributes.MAX_MAGICULE, ISummoning.SUMMONING_BOOST, sacrificeEP, Operation.ADD_VALUE);
         AttributeInstance limited = entity.getAttribute(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE);
         if (limited != null && limited.hasModifier(EnergyHelper.SPIRITUAL_EP_LIMITED)) {
            AttributeModifier modifier = new AttributeModifier(ISummoning.SUMMONING_BOOST, sacrificeEP, Operation.ADD_VALUE);
            limited.addOrReplacePermanentModifier(modifier);
         }
      }

      for (ManasSkillInstance skill : SkillAPI.getSkillsFrom(clone).getLearnedSkills()) {
         if (skill.getRemoveTime() == -2) {
            ManasSkillInstance copy = skill.copy();
            copy.getOrCreateTag().putBoolean(ISummoning.SUMMONING_BOOST.toString(), true);
            if (SkillHelper.learnSkill(summon, copy)) {
               copy.onToggleOn(summon);
            }
         }
      }
   }

   @Override
   public void onSummonRemoval(LivingEntity entity, boolean onDeath) {
      if (entity instanceof Player) {
         AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
         if (magicule != null) {
            magicule.removeModifier(SUMMONING_BOOST);
         }

         AttributeInstance limited = entity.getAttribute(TensuraAttributes.LIMITED_SPIRITUAL_MAX_MAGICULE);
         if (limited != null) {
            limited.removeModifier(SUMMONING_BOOST);
         }

         if (!onDeath) {
            Skills storage = SkillAPI.getSkillsFrom(entity);

            for (ManasSkillInstance copy : List.copyOf(storage.getLearnedSkills())) {
               Optional<ManasSkillInstance> optional = storage.getSkill(copy.getSkill());
               if (!optional.isEmpty()) {
                  ManasSkillInstance instance = optional.get();
                  if (instance.isTemporarySkill() && instance.getTag() != null && instance.getTag().contains(ISummoning.SUMMONING_BOOST.toString())) {
                     storage.forgetSkill(instance);
                  }
               }
            }
         }
      }
   }

   @Override
   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity subordinate, DamageSource source) {
   }

   @Override
   public void summonMagicCircle(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int heldTicks, int mode) {
      Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
      MagicCircle.castMagicCircle(3.0F, 30, pos, MagicCircleVariant.DEMON, entity, instance.getOrCreateTag(), instance, mode, cost);
      MinecraftServer minecraftserver = ((ServerLevel)entity.level()).getServer();
      ServerLevel hell = minecraftserver.getLevel(TensuraDimensions.HELL);
      if (hell != null) {
         CompoundTag tag = instance.getOrCreateTag();
         if (tag.contains("BeamUUID")) {
            if (hell.getEntity(tag.getUUID("BeamUUID")) instanceof SummoningBeam beam) {
               beam.increaseLife(1);
            }
         }
      }
   }

   @Override
   public EntityType<? extends TamableAnimal> getSummonedType(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return switch (mode) {
         case 1 -> (EntityType)MonsterEntityTypes.GREATER_DAEMON.get();
         case 2 -> (EntityType)MonsterEntityTypes.ARCH_DAEMON.get();
         default -> (EntityType)MonsterEntityTypes.LESSER_DAEMON.get();
      };
   }

   @Override
   public ParticleOptions getSummoningParticle(ManasSkillInstance instance, int mode) {
      return TensuraParticleUtils.getBlackAura(1.0F, 1.0F, -0.3F);
   }

   @Override
   public SoundEvent getSummoningSound(ManasSkillInstance instance, int mode) {
      return (SoundEvent)TensuraSoundEvents.CAST_DARK.get();
   }

   @Override
   public SoundEvent getFailSound(ManasSkillInstance instance, int mode) {
      return SoundEvents.GOAT_SCREAMING_DEATH;
   }
}
