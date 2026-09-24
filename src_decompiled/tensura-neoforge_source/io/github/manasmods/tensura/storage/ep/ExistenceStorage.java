package io.github.manasmods.tensura.storage.ep;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent.LivingDeath;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.EntityEvents.LivingTickEvent;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.summon.ISummoning;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.item.weapon.spell.SimpleSpellCastItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.AreaMagiculeHelper;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ExistenceStorage extends Storage implements IExistence {
   @Generated
   private static final Logger log = LogManager.getLogger(ExistenceStorage.class);
   private static final ResourceLocation SLEEP_MODE = ResourceLocation.fromNamespaceAndPath("tensura", "sleep_mode");
   private static StorageKey<ExistenceStorage> key = null;
   public static final int ENERGY_REGEN = 10;
   public static final int SPIRITUAL_REGEN = 20;
   private double spiritualHealth = 20.0;
   private double gainedEP;
   private double aura = 50.0;
   private double magicule = 50.0;
   private int soulPoints;
   private int humanKill;
   private int harvestTick;
   private int harvestGiftTick;
   private int sleepModeTime;
   private int summonedSecond;
   private boolean demonLordSeed;
   private boolean trueDemonLord;
   private boolean heroEgg;
   private boolean trueHero;
   private boolean skippingEPDrop;
   private boolean nameable;
   private boolean blessed;
   private boolean spiritualForm;
   private boolean harvestGift;
   private String name;
   private Alignment alignment = Alignment.DEFAULT;
   private Alignment originalAlignment = Alignment.DEFAULT;
   @Nullable
   private MobSpawnType spawnType = MobSpawnType.NATURAL;
   private UUID permanentOwner;
   private UUID temporaryOwner;
   private UUID summoner;
   private final Set<UUID> neutralTarget = new HashSet<>();
   private final AbilitySlot summonedAbility = AbilitySlot.getEmpty();

   public static void init() {
      StorageEvents.REGISTER_ENTITY_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "existence_storage"),
               ExistenceStorage.class,
               LivingEntity.class::isInstance,
               target -> new ExistenceStorage((LivingEntity)target)
            )
         );
      EntityEvents.LIVING_POST_TICK.register((LivingTickEvent)entity -> {
         Level level = entity.level();
         if (!level.isClientSide()) {
            MinecraftServer server = level.getServer();
            if (server != null) {
               long tickCount = server.getTickCount();
               boolean spirRegen = tickCount % 20L == 0L;
               boolean energyRegen = tickCount % 10L == 0L;
               boolean isPlayer = entity instanceof Player;
               IExistence storage = TensuraStorages.getExistenceFrom(entity);
               if (spirRegen || energyRegen || isPlayer || storage.getHarvestGiftTick() > 0) {
                  if (spirRegen) {
                     handleSpiritualHealthRegen(server, entity, storage);
                  }

                  if (energyRegen) {
                     handleEnergyRegen(server, level, entity, storage);
                  }

                  handleHarvestFestival(storage, entity);
                  if (isPlayer) {
                     ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(entity);
                     if (data == null) {
                        return;
                     }

                     if (data.getDodgeCooldown() > 0) {
                        data.setDodgeCooldown(data.getDodgeCooldown() - 1);
                        data.markDirty();
                     }

                     if (data.getDodgeInvulnerability() > 0) {
                        data.setDodgeInvulnerability(data.getDodgeInvulnerability() - 1);
                        data.markDirty();
                     }
                  }
               }
            }
         }
      });
      EntityEvents.DEATH_EVENT_LOW.register((LivingDeath)(entity, damageSource) -> {
         Level level = entity.level();
         if (level.isClientSide()) {
            return EventResult.pass();
         }

         MinecraftServer server = level.getServer();
         if (server == null) {
            return EventResult.pass();
         }

         if (RaceAPI.getRaceFrom(entity).getRace().isEmpty()) {
            return EventResult.pass();
         }

         IExistence storage = TensuraStorages.getExistenceFrom(entity);
         applyDeathPenalty(entity, storage, damageSource);
         if (storage.getSleepModeTime() > 0) {
            storage.setSleepModeTime(-1);
            AttributeHelper.removeSleepModeAttribute(entity, SLEEP_MODE);
         }

         if (storage.getHarvestTick() > 0) {
            storage.setSoulPoints(0);
            storage.setHarvestTick(0);
            AttributeHelper.removeSleepModeAttribute(entity, SLEEP_MODE);
         }

         if (storage.getHarvestGiftTick() > 0) {
            storage.setHarvestGiftTick(0);
            AttributeHelper.removeSleepModeAttribute(entity, SLEEP_MODE);
         }

         Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
         race.ifPresent(instance -> storage.setSpiritualForm(instance.is(TensuraRaceTags.SPAWN_AS_SPIRITUAL)));
         if (storage.getSummonedAbility().getSkill() instanceof ISummoning<?> summoning) {
            summoning.onSummonRemoval(entity, true);
         }

         storage.setSummonedAbility(null, 0);
         storage.setSummonedSecond(0);
         storage.setSummoner(null);
         storage.markDirty();
         return EventResult.pass();
      });
   }

   private static void handleSpiritualHealthRegen(MinecraftServer server, LivingEntity entity, IExistence storage) {
      handleSummoningTicking(storage, entity);
      double maxHP = entity.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
      double shp = storage.getSpiritualHealth();
      if (shp != maxHP) {
         storage.setSpiritualHealth(Math.min(maxHP, shp + entity.getAttributeValue(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION)));
         storage.markDirty();
      }
   }

   private static void handleEnergyRegen(MinecraftServer server, Level level, LivingEntity entity, IExistence storage) {
      double ep = storage.getEP();
      if (ep < 0.0) {
         if (isAffectedByEnergyLost(entity)) {
            entity.hurt(TensuraDamageTypes.getDamageSource(level, TensuraDamageTypes.ENERGY_DRAIN), entity.getMaxHealth());
         }
      } else {
         double maxAura = EnergyHelper.getMaxAura(entity);
         handleAuraRegen(storage, entity, maxAura);
         double areaMagicule = AreaMagiculeHelper.getMagicule(entity, true);
         handleGearEnergyRegen(server, entity, areaMagicule * EnergyHelper.CONFIG.areaMagiculeRegen);
         double maxMagicule = EnergyHelper.getMaxMagicule(entity);
         handleSleepMode(storage, entity, maxMagicule);
         handleMagiculeRegen(storage, entity, areaMagicule, maxMagicule);
         if (!entity.hasInfiniteMaterials() && !entity.isInvulnerable() && entity.invulnerableTime <= 0) {
            if (entity.getType().equals(EntityType.PLAYER)) {
               if (!(areaMagicule < EnergyHelper.CONFIG.minimumMagiculePoison)) {
                  int difference = (int)(areaMagicule / ((maxMagicule + maxAura) * 4.0));
                  if (difference > getMagiculePoisonResistance(storage, entity) && isAffectedByEnergyLost(entity)) {
                     ResourceKey<Level> dimension = level.dimension();
                     if (SkillUtils.inSpiritualWorld(dimension)) {
                        Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
                        if (race.isPresent() && dimension.equals(race.get().getRespawnDimension(entity).getFirst())) {
                           return;
                        }
                     }

                     Holder<MobEffect> poison = entity.level().getBiome(entity.getOnPos()).is(TensuraBiomeTags.IS_MIASMIC) && !RaceUtils.isUndead(entity)
                        ? TensuraMobEffects.getReference(TensuraMobEffects.CURSE)
                        : TensuraMobEffects.getReference(TensuraMobEffects.MAGICULE_POISON);
                     entity.addEffect(new MobEffectInstance(poison, 100, difference - 1, true, false, true));
                  }
               }
            }
         }
      }
   }

   private static void handleGearEnergyRegen(MinecraftServer server, LivingEntity user, double arenaMagicule) {
      boolean spiritualTick = server.getTickCount() % 20 == 0;

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = user.getItemBySlot(slot);
         if (stack.has((DataComponentType)TensuraDataComponents.EP.get()) && (!slot.getType().equals(Type.HAND) || !user.swinging)) {
            if (spiritualTick && stack.is(TensuraItemTags.SPELL_CAST_WEAPONS)) {
               SimpleSpellCastItem.tickUnlearntInstance(user.level(), stack, user);
            }

            if (TensuraEnchantmentHelper.getEnchantmentLevel(user.level(), TensuraEnchantments.RUINATION, stack) <= 0) {
               int multiplier = 10;
               int damage = stack.getDamageValue();
               double durabilityEP = (Double)stack.get((DataComponentType)TensuraDataComponents.EP_DURABILITY.get());
               double restoration = TensuraEnchantmentHelper.getEnchantmentLevel(user.level(), TensuraEnchantments.RESTORATION, stack);
               int regen = (int)Math.min(durabilityEP, damage * multiplier);
               if (damage > 0 && regen / multiplier > 0) {
                  stack.setDamageValue(damage - (int)(regen / multiplier * (1.0 + restoration)));
                  stack.set((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), durabilityEP - regen);
               } else {
                  double EP = (Double)stack.get((DataComponentType)TensuraDataComponents.EP.get());
                  if (durabilityEP < EP) {
                     stack.set((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), Math.min(EP, durabilityEP + arenaMagicule));
                  }
               }
            }
         }
      }
   }

   private static void handleAuraRegen(IExistence storage, LivingEntity entity, double maxAura) {
      double aura = storage.getAura();
      if (aura > maxAura) {
         double multiplier = EnergyHelper.CONFIG.auraMultiplierForInsanity;
         if (aura >= maxAura * (1.0 + multiplier) && !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY))) {
            double extraMP = aura - maxAura * (1.0 + multiplier);
            int level = (int)(extraMP / (maxAura * multiplier));
            entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY), 80, level, true, false, true));
         }

         storage.setAura(Math.max(maxAura, aura - EnergyHelper.CONFIG.exceedMaxLost));
         storage.markDirty();
      } else if (aura < maxAura) {
         double regen = EnergyHelper.CONFIG.baseAuraRegen * entity.getAttributeValue(TensuraAttributes.AURA_REGENERATION_MULTIPLIER);
         if (storage.getSleepModeTime() > 0) {
            regen += maxAura * EnergyHelper.CONFIG.sleepModeAura;
         }

         storage.setAura(Math.min(aura + regen, maxAura));
         storage.markDirty();
      }
   }

   private static void handleSleepMode(IExistence storage, LivingEntity entity, double maxMagicule) {
      if (!entity.hasInfiniteMaterials() && !entity.isInvulnerable()) {
         double magicule = storage.getMagicule();
         if (magicule <= 0.0) {
            if (storage.getSleepModeTime() <= 0) {
               Changeable<Integer> sleepTime = Changeable.of(EnergyHelper.CONFIG.sleepModeTick);
               Changeable<Boolean> startMagicule = Changeable.of(true);
               if (!((TensuraEntityEvents.EnterSleepModeEvent)TensuraEntityEvents.ENTER_SLEEP_MODE_EVENT.invoker())
                  .sleep(entity, sleepTime, startMagicule)
                  .isFalse()) {
                  storage.setSleepModeTime((Integer)sleepTime.get());
                  AttributeHelper.applySleepModeAttribute(entity, SLEEP_MODE);
                  if ((Boolean)startMagicule.get()) {
                     storage.setMagicule(1.0);
                  }

                  entity.unRide();
                  storage.markDirty();
               }
            } else if (isAffectedByEnergyLost(entity)) {
               entity.hurt(TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.ENERGY_DRAIN), entity.getMaxHealth());
            }
         }

         if (storage.getSleepModeTime() > 0) {
            if (magicule >= maxMagicule) {
               if (storage.getHarvestTick() <= 0 && storage.getHarvestGiftTick() <= 0) {
                  AttributeHelper.removeSleepModeAttribute(entity, SLEEP_MODE);
               }

               storage.setSleepModeTime(-1);
               storage.markDirty();
            } else {
               storage.setSleepModeTime(storage.getSleepModeTime() - 1);
               storage.markDirty();
               if (entity instanceof Player player && player.getAbilities().flying) {
                  player.getAbilities().flying = false;
                  player.onUpdateAbilities();
               }
            }
         }

         if (storage.getSleepModeTime() == 0) {
            if (storage.getHarvestTick() <= 0 && storage.getHarvestGiftTick() <= 0) {
               AttributeHelper.removeSleepModeAttribute(entity, SLEEP_MODE);
            }

            storage.setSleepModeTime(-1);
            storage.markDirty();
         }
      }
   }

   private static void handleHarvestFestival(IExistence storage, LivingEntity entity) {
      int harvestTick = storage.getHarvestTick();
      if (harvestTick > 0) {
         int max = TensuraRace.BASE_CONFIG.DemonLord.harvestFestivalTick;
         if (entity instanceof Player player) {
            if (harvestTick <= Math.min(200.0, max * 0.1)) {
               player.hurtMarked = true;
               player.setDeltaMovement(new Vec3(0.0, 0.02, 0.0));
               player.hasImpulse = true;
               MobEffectInstance existingFall = player.getEffect(MobEffects.SLOW_FALLING);
               if (existingFall == null || existingFall.getDuration() < 20) {
                  player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false, false));
               }

               if (harvestTick % 10 == 0) {
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.25F);
                  TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions)TensuraParticleTypes.SOUL.get(), 5.0);
                  TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.FLASH, 5.0);
                  applySubordinateHarvestFestival(player, harvestTick);
               }
            } else if (harvestTick <= Math.min(600.0, max * 0.2)) {
               if (harvestTick % 15 == 0) {
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.25F);
                  TensuraParticleHelper.addServerParticlesAroundSelf(player, (ParticleOptions)TensuraParticleTypes.SOUL.get(), 5.0);
                  TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.SOUL, 5.0);
               } else if (harvestTick % 20 == 0) {
                  applySubordinateHarvestFestival(player, harvestTick);
               }
            } else if (harvestTick <= Math.min(1200.0, max * 0.4)) {
               if (harvestTick % 20 == 0) {
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.25F);
                  TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.SOUL, 5.0);
                  applySubordinateHarvestFestival(player, harvestTick);
               }
            } else if (harvestTick <= Math.min(2400.0, max * 0.7)) {
               if (harvestTick % 25 == 0) {
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.25F);
                  AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
                  if (speed != null && !speed.hasModifier(SLEEP_MODE)) {
                     AttributeHelper.applySleepModeAttribute(entity, SLEEP_MODE);
                     player.unRide();
                  }
               }
            } else if (harvestTick % 30 == 0) {
               player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.25F);
               player.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.TRUE_BLINDNESS), 45, 0, false, false, false));
               player.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 45, 7, false, false, false));
            }

            if (player.getAbilities().flying) {
               player.getAbilities().flying = false;
               player.onUpdateAbilities();
            }
         }

         storage.setHarvestTick(harvestTick - 1);
         storage.markDirty();
         if (harvestTick - 1 == 0) {
            if (!RaceUtils.isAlreadyAwakened(entity, storage)) {
               RaceHelper.awakening(entity, false);
            }

            AttributeHelper.removeSleepModeAttribute(entity, SLEEP_MODE);
         }
      }

      if (storage.getHarvestGiftTick() > 0) {
         if (storage.getHarvestGiftTick() % 20 == 0) {
            AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null && !speed.hasModifier(SLEEP_MODE)) {
               AttributeHelper.applySleepModeAttribute(entity, SLEEP_MODE);
               entity.unRide();
            }

            if (entity instanceof Player player && player.getAbilities().flying) {
               player.getAbilities().flying = false;
               player.onUpdateAbilities();
            }
         }

         storage.setHarvestGiftTick(storage.getHarvestGiftTick() - 1);
         storage.markDirty();
         if (storage.getHarvestGiftTick() == 0) {
            RaceHelper.applyHarvestFestivalGift(storage, entity);
            AttributeHelper.removeSleepModeAttribute(entity, SLEEP_MODE);
         }
      }
   }

   private static void handleSummoningTicking(IExistence storage, LivingEntity entity) {
      if (entity.isAlive()) {
         if (storage.getSummoner() != null && storage.getSummonedSecond() > 0) {
            storage.setSummonedSecond(storage.getSummonedSecond() - 1);
            storage.markDirty();
            if (storage.getSummonedSecond() <= 0) {
               ISummoning.removeSummon(entity, storage);
            } else if (SubordinateHelper.getSubordinateOwner(entity) instanceof Player owner) {
               if (entity instanceof Mob mob && mob.isNoAi() && !mob.noPhysics) {
                  ISummoning.removeSummon(entity, storage);
               } else {
                  entity.noPhysics = false;
                  AbilitySlot slot = storage.getSummonedAbility();
                  if (slot.getSkill() instanceof ISummoning<?> summoning) {
                     if (!owner.isAlive()
                        || EnergyHelper.isOutOfEnergy(
                           owner, (Double)summoning.getSummonedCostPerSecond().getFirst(), (Double)summoning.getSummonedCostPerSecond().getSecond()
                        )) {
                        ISummoning.removeSummon(entity, storage);
                     }
                  }
               }
            }
         }
      }
   }

   private static void handleMagiculeRegen(IExistence storage, LivingEntity entity, double areaMagicule, double maxMagicule) {
      double magicule = storage.getMagicule();
      if (storage.isSpiritualForm() && !SkillUtils.inSpiritualWorld(entity.level().dimension()) && isAffectedByEnergyLost(entity)) {
         if (entity.invulnerableTime <= 20) {
            storage.setMagicule(Math.max(-1.0, magicule - EnergyHelper.CONFIG.spiritualMagiculeLost));
            storage.markDirty();
         }
      } else {
         if (magicule > maxMagicule) {
            double multiplier = EnergyHelper.CONFIG.magiculeMultiplierForPoison;
            if (magicule >= maxMagicule * (1.0 + multiplier) && !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGICULE_POISON))) {
               double extraMP = magicule - maxMagicule * (1.0 + multiplier);
               int poisonLevel = (int)(extraMP / (maxMagicule * multiplier));
               entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MAGICULE_POISON), 100, poisonLevel, true, false, true));
            }

            storage.setMagicule(Math.max(maxMagicule, magicule - EnergyHelper.CONFIG.exceedMaxLost));
            storage.markDirty();
         } else if (magicule < maxMagicule) {
            double regen = areaMagicule * EnergyHelper.CONFIG.areaMagiculeRegen * entity.getAttributeValue(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER);
            if (storage.getSleepModeTime() > 0) {
               regen += maxMagicule * EnergyHelper.CONFIG.sleepModeMagicule;
            }

            storage.setMagicule(Math.min(magicule + regen, maxMagicule));
            storage.markDirty();
         }
      }
   }

   private static void applyDeathPenalty(LivingEntity entity, IExistence storage, DamageSource source) {
      if (!source.is(TensuraTags.DamageTypes.STOP_DEATH_PENALTY)) {
         if (!source.isCreativePlayer()) {
            float penalty = entity.level().getGameRules().getInt(TensuraGameRules.EP_DEATH_PENALTY) / 100.0F;
            if (penalty > 0.0F) {
               EnergyHelper.multiplyMaxEP(entity, 1.0F - penalty);
               if (storage.getSoulPoints() > 0) {
                  storage.setSoulPoints((int)(storage.getSoulPoints() * (1.0F - penalty)));
               }
            }
         }
      }
   }

   private static boolean isAffectedByEnergyLost(LivingEntity entity) {
      return entity.isSpectator() ? false : !entity.hasInfiniteMaterials();
   }

   private static int getMagiculePoisonResistance(IExistence existence, LivingEntity entity) {
      int level = 0;
      if (existence.getAlignment() != Alignment.DEFAULT) {
         level++;
      }

      if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.MAGIC_RESISTANCE.get())) {
         level += 2;
      }

      return level;
   }

   protected ExistenceStorage(LivingEntity holder) {
      super(holder);
      if (!holder.getType().equals(EntityType.PLAYER)) {
         this.skippingEPDrop = true;
      }
   }

   @Override
   public double getEP() {
      return this.aura + this.magicule;
   }

   @Override
   public void setEP(double amount) {
      this.aura = amount / 2.0;
      this.magicule = amount / 2.0;
   }

   @Override
   public void setAura(double amount) {
      this.aura = Math.min(amount, 2.147483647E9);
   }

   @Override
   public void setMagicule(double amount) {
      this.magicule = Math.min(amount, 2.147483647E9);
   }

   @Override
   public void setPermanentOwner(UUID owner) {
      if (owner == null || !owner.equals(this.getOwner().getUUID())) {
         this.permanentOwner = owner;
      }
   }

   @Override
   public void setTemporaryOwner(UUID owner) {
      if (owner == null || !owner.equals(this.getOwner().getUUID())) {
         this.temporaryOwner = owner;
      }
   }

   @Override
   public void setSummoner(UUID owner) {
      if (owner == null || !owner.equals(this.getOwner().getUUID())) {
         this.summoner = owner;
      }
   }

   @Override
   public boolean hasHarvestGift() {
      return this.harvestGift;
   }

   public void save(CompoundTag tag) {
      tag.putDouble("spiritualHealth", this.spiritualHealth);
      tag.putDouble("aura", this.aura);
      tag.putDouble("magicule", this.magicule);
      tag.putDouble("gainedEP", this.gainedEP);
      tag.putInt("soulPoint", this.soulPoints);
      tag.putInt("humanKill", this.humanKill);
      tag.putBoolean("demonLordSeed", this.demonLordSeed);
      tag.putBoolean("trueDemonLord", this.trueDemonLord);
      tag.putBoolean("heroEgg", this.heroEgg);
      tag.putBoolean("trueHero", this.trueHero);
      tag.putBoolean("blessed", this.blessed);
      tag.putBoolean("nameable", this.nameable);
      tag.putBoolean("spiritualForm", this.spiritualForm);
      tag.putBoolean("skipEPDrop", this.skippingEPDrop);
      tag.putBoolean("hasHarvestGift", this.harvestGift);
      tag.putInt("sleepMode", this.sleepModeTime);
      tag.putInt("harvestTick", this.harvestTick);
      tag.putInt("summonedSecond", this.summonedSecond);
      if (!this.summonedAbility.isEmpty()) {
         tag.put("summonedAbility", this.summonedAbility.serialize());
      } else if (tag.hasUUID("summonedAbility")) {
         tag.remove("summonedAbility");
      }

      if (this.permanentOwner != null) {
         tag.putUUID("permanentOwner", this.permanentOwner);
      } else if (tag.hasUUID("permanentOwner")) {
         tag.remove("permanentOwner");
      }

      if (this.temporaryOwner != null) {
         tag.putUUID("temporaryOwner", this.temporaryOwner);
      } else if (tag.hasUUID("temporaryOwner")) {
         tag.remove("temporaryOwner");
      }

      if (this.summoner != null) {
         tag.putUUID("summoner", this.summoner);
      } else if (tag.hasUUID("summoner")) {
         tag.remove("summoner");
      }

      if (this.name != null) {
         tag.putString("name", this.name);
      } else if (tag.contains("name", 8)) {
         tag.remove("name");
      }

      if (this.spawnType != null) {
         tag.putString("spawnType", this.spawnType.name());
      }

      tag.putString("originalAlignment", Objects.requireNonNullElse(this.originalAlignment, Alignment.DEFAULT).name());
      tag.putString("alignment", Objects.requireNonNullElse(this.alignment, Alignment.DEFAULT).name());
      ListTag neutralList = new ListTag();

      for (UUID uuid : this.neutralTarget) {
         CompoundTag target = new CompoundTag();
         target.putUUID("target", uuid);
         neutralList.add(target);
      }

      tag.put("neutralList", neutralList);
   }

   public void load(CompoundTag tag) {
      this.spiritualHealth = tag.getDouble("spiritualHealth");
      this.aura = tag.getDouble("aura");
      this.magicule = tag.getDouble("magicule");
      this.gainedEP = tag.getDouble("gainedEP");
      this.soulPoints = tag.getInt("soulPoint");
      this.humanKill = tag.getInt("humanKill");
      this.demonLordSeed = tag.getBoolean("demonLordSeed");
      this.trueDemonLord = tag.getBoolean("trueDemonLord");
      this.heroEgg = tag.getBoolean("heroEgg");
      this.trueHero = tag.getBoolean("trueHero");
      this.blessed = tag.getBoolean("blessed");
      this.nameable = tag.getBoolean("nameable");
      this.spiritualForm = tag.getBoolean("spiritualForm");
      this.skippingEPDrop = tag.getBoolean("skipEPDrop");
      this.harvestGift = tag.getBoolean("hasHarvestGift");
      this.sleepModeTime = tag.getInt("sleepMode");
      this.harvestTick = tag.getInt("harvestTick");
      this.summonedSecond = tag.getInt("summonedSecond");
      if (tag.contains("summonedAbility")) {
         this.summonedAbility.deserialize((CompoundTag)tag.get("summonedAbility"));
      }

      if (tag.contains("name", 8)) {
         this.name = tag.getString("name");
      }

      if (tag.contains("spawnType")) {
         this.spawnType = MobSpawnType.valueOf(tag.getString("spawnType"));
      }

      if (tag.contains("originalAlignment")) {
         this.originalAlignment = Alignment.valueOf(tag.getString("originalAlignment"));
      } else {
         this.originalAlignment = Alignment.DEFAULT;
      }

      if (tag.contains("alignment")) {
         this.alignment = Alignment.valueOf(tag.getString("alignment"));
      } else {
         this.alignment = Alignment.DEFAULT;
      }

      if (tag.hasUUID("permanentOwner")) {
         this.permanentOwner = tag.getUUID("permanentOwner");
      }

      if (tag.hasUUID("temporaryOwner")) {
         this.temporaryOwner = tag.getUUID("temporaryOwner");
      }

      if (tag.hasUUID("summoner")) {
         this.summoner = tag.getUUID("summoner");
      }

      ListTag neutralList = (ListTag)tag.get("neutralList");
      if (neutralList != null) {
         this.neutralTarget.clear();

         for (Tag value : neutralList) {
            if (value instanceof CompoundTag target && target.hasUUID("target")) {
               this.neutralTarget.add(target.getUUID("target"));
            }
         }
      }
   }

   protected LivingEntity getOwner() {
      return (LivingEntity)this.holder;
   }

   @Override
   public Collection<UUID> getTargetNeutralList() {
      return this.neutralTarget;
   }

   @Override
   public boolean isTargetNeutral(UUID target) {
      return this.neutralTarget.contains(target);
   }

   @Override
   public void addNeutralTarget(UUID target) {
      this.neutralTarget.add(target);
   }

   @Override
   public void removeNeutralTarget(UUID target) {
      this.neutralTarget.remove(target);
   }

   @Override
   public void clearNeutralTargets() {
      this.neutralTarget.clear();
   }

   @Override
   public void setSummonedAbility(ManasSkill skill, int mode) {
      this.summonedAbility.setSkillAndMode(skill, mode);
   }

   public static boolean isSummon(LivingEntity entity) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      return existence.getSummoner() != null && existence.getSummonedSecond() > 0;
   }

   public static boolean isInSleepMode(IExistence storage) {
      return storage.getHarvestGiftTick() > 0 || storage.getHarvestTick() > 0 || storage.getSleepModeTime() > 0;
   }

   public static void enterHarvestFestival(IExistence storage, LivingEntity entity) {
      if (!entity.level().isClientSide()) {
         Changeable<Integer> time = Changeable.of(TensuraRace.BASE_CONFIG.DemonLord.harvestFestivalTick);
         Changeable<Integer> soul = Changeable.of(entity.level().getGameRules().getInt(TensuraGameRules.DEMON_LORD_AWAKEN) * 1000);
         if (!((TensuraEntityEvents.EnterHarvestFestivalEvent)TensuraEntityEvents.ENTER_HARVEST_FESTIVAL_EVENT.invoker()).enter(entity, time, soul).isFalse()) {
            for (TamableAnimal sub : entity.level()
               .getEntitiesOfClass(
                  TamableAnimal.class,
                  entity.getBoundingBox().inflate(TensuraRace.BASE_CONFIG.DemonLord.harvestFestivalRange),
                  living -> living.isAlive() && living.isOwnedBy(entity)
               )) {
               sub.setOrderedToSit(false);
               sub.setInSittingPose(false);
               sub.setWandering(false);
               sub.setBehaviour(3);
            }

            storage.setHarvestGift(true);
            storage.setHarvestTick((Integer)time.get());
            storage.setSoulPoints(storage.getSoulPoints() - (Integer)soul.get());
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            storage.markDirty();
         }
      }
   }

   public static void applySubordinateHarvestFestival(LivingEntity entity, int harvestTick) {
      for (Player sub : entity.level().players()) {
         IExistence subExistence = TensuraStorages.getExistenceFrom(sub);
         if (!subExistence.hasHarvestGift()
            && subExistence.getHarvestGiftTick() <= 0
            && subExistence.getHarvestTick() <= 0
            && Objects.equals(subExistence.getPermanentOwner(), entity.getUUID())) {
            subExistence.setHarvestGiftTick(harvestTick);
            subExistence.markDirty();
         }
      }

      List<LivingEntity> list = entity.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(TensuraRace.BASE_CONFIG.DemonLord.harvestFestivalRange),
            living -> !(living instanceof Player) && living.isAlive()
         );
      if (!list.isEmpty()) {
         for (LivingEntity sub : list) {
            IExistence subExistence = TensuraStorages.getExistenceFrom(sub);
            if (!subExistence.hasHarvestGift()
               && subExistence.getHarvestGiftTick() <= 0
               && subExistence.getHarvestTick() <= 0
               && Objects.equals(subExistence.getPermanentOwner(), entity.getUUID())) {
               subExistence.setHarvestGiftTick(harvestTick);
               subExistence.markDirty();
            }
         }
      }
   }

   @Generated
   public static StorageKey<ExistenceStorage> getKey() {
      return key;
   }

   @Generated
   @Override
   public double getSpiritualHealth() {
      return this.spiritualHealth;
   }

   @Generated
   @Override
   public double getGainedEP() {
      return this.gainedEP;
   }

   @Generated
   @Override
   public void setSpiritualHealth(double spiritualHealth) {
      this.spiritualHealth = spiritualHealth;
   }

   @Generated
   @Override
   public void setGainedEP(double gainedEP) {
      this.gainedEP = gainedEP;
   }

   @Generated
   @Override
   public double getAura() {
      return this.aura;
   }

   @Generated
   @Override
   public double getMagicule() {
      return this.magicule;
   }

   @Generated
   @Override
   public int getSoulPoints() {
      return this.soulPoints;
   }

   @Generated
   @Override
   public int getHumanKill() {
      return this.humanKill;
   }

   @Generated
   @Override
   public void setSoulPoints(int soulPoints) {
      this.soulPoints = soulPoints;
   }

   @Generated
   @Override
   public void setHumanKill(int humanKill) {
      this.humanKill = humanKill;
   }

   @Generated
   @Override
   public int getHarvestTick() {
      return this.harvestTick;
   }

   @Generated
   @Override
   public int getHarvestGiftTick() {
      return this.harvestGiftTick;
   }

   @Generated
   @Override
   public int getSleepModeTime() {
      return this.sleepModeTime;
   }

   @Generated
   @Override
   public int getSummonedSecond() {
      return this.summonedSecond;
   }

   @Generated
   @Override
   public void setHarvestTick(int harvestTick) {
      this.harvestTick = harvestTick;
   }

   @Generated
   @Override
   public void setHarvestGiftTick(int harvestGiftTick) {
      this.harvestGiftTick = harvestGiftTick;
   }

   @Generated
   @Override
   public void setSleepModeTime(int sleepModeTime) {
      this.sleepModeTime = sleepModeTime;
   }

   @Generated
   @Override
   public void setSummonedSecond(int summonedSecond) {
      this.summonedSecond = summonedSecond;
   }

   @Generated
   @Override
   public boolean isDemonLordSeed() {
      return this.demonLordSeed;
   }

   @Generated
   @Override
   public boolean isTrueDemonLord() {
      return this.trueDemonLord;
   }

   @Generated
   @Override
   public void setDemonLordSeed(boolean demonLordSeed) {
      this.demonLordSeed = demonLordSeed;
   }

   @Generated
   @Override
   public void setTrueDemonLord(boolean trueDemonLord) {
      this.trueDemonLord = trueDemonLord;
   }

   @Generated
   @Override
   public boolean isHeroEgg() {
      return this.heroEgg;
   }

   @Generated
   @Override
   public boolean isTrueHero() {
      return this.trueHero;
   }

   @Generated
   @Override
   public void setHeroEgg(boolean heroEgg) {
      this.heroEgg = heroEgg;
   }

   @Generated
   @Override
   public void setTrueHero(boolean trueHero) {
      this.trueHero = trueHero;
   }

   @Generated
   @Override
   public boolean isSkippingEPDrop() {
      return this.skippingEPDrop;
   }

   @Generated
   @Override
   public boolean isNameable() {
      return this.nameable;
   }

   @Generated
   @Override
   public boolean isBlessed() {
      return this.blessed;
   }

   @Generated
   @Override
   public boolean isSpiritualForm() {
      return this.spiritualForm;
   }

   @Generated
   @Override
   public void setSkippingEPDrop(boolean skippingEPDrop) {
      this.skippingEPDrop = skippingEPDrop;
   }

   @Generated
   @Override
   public void setNameable(boolean nameable) {
      this.nameable = nameable;
   }

   @Generated
   @Override
   public void setBlessed(boolean blessed) {
      this.blessed = blessed;
   }

   @Generated
   @Override
   public void setSpiritualForm(boolean spiritualForm) {
      this.spiritualForm = spiritualForm;
   }

   @Generated
   @Override
   public void setHarvestGift(boolean harvestGift) {
      this.harvestGift = harvestGift;
   }

   @Generated
   @Override
   public String getName() {
      return this.name;
   }

   @Generated
   @Override
   public void setName(String name) {
      this.name = name;
   }

   @Generated
   @Override
   public Alignment getAlignment() {
      return this.alignment;
   }

   @Generated
   @Override
   public Alignment getOriginalAlignment() {
      return this.originalAlignment;
   }

   @Generated
   @Override
   public void setAlignment(Alignment alignment) {
      this.alignment = alignment;
   }

   @Generated
   @Override
   public void setOriginalAlignment(Alignment originalAlignment) {
      this.originalAlignment = originalAlignment;
   }

   @Nullable
   @Generated
   @Override
   public MobSpawnType getSpawnType() {
      return this.spawnType;
   }

   @Generated
   @Override
   public void setSpawnType(@Nullable MobSpawnType spawnType) {
      this.spawnType = spawnType;
   }

   @Generated
   @Override
   public UUID getPermanentOwner() {
      return this.permanentOwner;
   }

   @Generated
   @Override
   public UUID getTemporaryOwner() {
      return this.temporaryOwner;
   }

   @Generated
   @Override
   public UUID getSummoner() {
      return this.summoner;
   }

   @Generated
   @Override
   public AbilitySlot getSummonedAbility() {
      return this.summonedAbility;
   }
}
