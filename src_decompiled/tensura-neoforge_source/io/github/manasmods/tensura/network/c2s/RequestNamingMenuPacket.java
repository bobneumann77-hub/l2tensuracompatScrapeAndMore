package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.INameEvolution;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.menu.NamingMenu;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceHelper;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RequestNamingMenuPacket(int targetID, String name, RequestNamingMenuPacket.NamingType namingType) implements CustomPacketPayload {
   public static final Type<RequestNamingMenuPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_naming_menu"));
   public static final StreamCodec<FriendlyByteBuf, RequestNamingMenuPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestNamingMenuPacket::encode, RequestNamingMenuPacket::new
   );

   public RequestNamingMenuPacket(FriendlyByteBuf buf) {
      this(buf.readInt(), buf.readUtf(64), (RequestNamingMenuPacket.NamingType)buf.readEnum(RequestNamingMenuPacket.NamingType.class));
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.targetID);
      buf.writeUtf(this.name);
      buf.writeEnum(this.namingType);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(() -> {
            ServerPlayer player = (ServerPlayer)context.getPlayer();
            if (player != null) {
               if (player.level().getEntity(this.targetID) instanceof LivingEntity sub) {
                  if (sub.distanceToSqr(player) > 256.0) {
                     return;
                  }

                  name(sub, player, this.namingType, this.name);
               }
            }
         });
      }
   }

   @NotNull
   public Type<RequestNamingMenuPacket> type() {
      return TYPE;
   }

   public static void name(LivingEntity sub, @Nullable ServerPlayer owner, RequestNamingMenuPacket.NamingType type, String string) {
      if (sub != null) {
         if (owner == null || RequestNamingKeyPacket.canName(owner, sub)) {
            double subEP = sub.getAttributeBaseValue(TensuraAttributes.MAX_AURA) + sub.getAttributeBaseValue(TensuraAttributes.MAX_MAGICULE);

            double epToGain = switch (type) {
               case LOW -> subEP * NamingMenu.CONFIG.subdueGain;
               case MEDIUM -> subEP * NamingMenu.CONFIG.evolveGain;
               case HIGH -> subEP * NamingMenu.CONFIG.endowGain;
            };
            Changeable<Double> epGain = Changeable.of(Math.min(NamingMenu.CONFIG.maxEPGain, epToGain));
            Changeable<Double> cost = Changeable.of(Math.min(epToGain, NamingMenu.CONFIG.maxCost));
            Changeable<RequestNamingMenuPacket.NamingType> namingType = Changeable.of(type);
            Changeable<String> name = Changeable.of(string);
            if (!((TensuraEntityEvents.NamingEvent)TensuraEntityEvents.NAMING_EVENT.invoker()).name(sub, owner, epGain, cost, namingType, name).isFalse()) {
               if (owner != null) {
                  IExistence ownerExistence = TensuraStorages.getExistenceFrom(owner);
                  if (!owner.hasInfiniteMaterials()) {
                     if (EnergyHelper.getBaseMaxMagicule(owner) <= (Double)cost.get()) {
                        owner.displayClientMessage(Component.translatable("tensura.skill.lack_magicule").withStyle(ChatFormatting.RED), false);
                        return;
                     }

                     if (ownerExistence.getAlignment().equals(Alignment.DEFAULT)
                        ? ownerExistence.getEP() < (Double)cost.get()
                        : ownerExistence.getMagicule() < (Double)cost.get()) {
                        owner.displayClientMessage(Component.translatable("tensura.skill.lack_magicule").withStyle(ChatFormatting.RED), false);
                        return;
                     }
                  }
               }

               IExistence existence = TensuraStorages.getExistenceFrom(sub);
               if (sub instanceof INameEvolution nameEvolution) {
                  nameEvolution.onPreNamed(existence, owner, epGain, cost, (RequestNamingMenuPacket.NamingType)namingType.get(), (String)name.get());
               }

               existence.setName((String)name.get());
               sub.setCustomName(Component.literal((String)name.get()));
               if (owner != null) {
                  existence.setPermanentOwner(owner.getUUID());
                  existence.setTemporaryOwner(null);
                  existence.setSummoner(null);
                  ((PlayerTrigger)TensuraCriteriaTriggers.NAME_ENTITY.get()).trigger(owner);
                  owner.awardStat(TensuraStats.ENTITY_NAMED);
               }

               existence.markDirty();
               sub.level().playSound(null, sub.getX(), sub.getY(), sub.getZ(), TensuraSoundEvents.BUFF_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
               sub.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false, false));
               TensuraParticleHelper.addServerParticlesAroundSelf(sub, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(sub, ParticleTypes.FLASH, 1.0);
               if (sub instanceof Player player) {
                  if (owner != null) {
                     player.sendSystemMessage(
                        Component.translatable("tensura.naming.name_success", new Object[]{name.get(), owner.getName()})
                           .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                     );
                  } else {
                     player.sendSystemMessage(
                        Component.translatable("tensura.naming.name_success.no_namer", new Object[]{name.get()})
                           .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))
                     );
                  }

                  ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
                  playerData.setTrackedEvolution(null);
                  playerData.markDirty();
               } else if (owner != null) {
                  if (sub instanceof ISubordinate tamable) {
                     tamable.tame(owner);
                  } else if (sub instanceof AbstractHorse horse) {
                     horse.tameWithName(owner);
                  }
               }

               namingReward(sub, existence, (Double)epGain.get(), (RequestNamingMenuPacket.NamingType)namingType.get());
               sub.heal(sub.getMaxHealth());
               TensuraMobEffect.removePredicateEffect(sub, effect -> ((MobEffect)effect.value()).getCategory().equals(MobEffectCategory.HARMFUL));
               if (owner != null && !owner.hasInfiniteMaterials()) {
                  IExistence ownerExistence = TensuraStorages.getExistenceFrom(owner);
                  if (ownerExistence.getAlignment().equals(Alignment.DEFAULT)) {
                     double mpCost = Math.min(ownerExistence.getMagicule(), (Double)cost.get());
                     double auraCost = (Double)cost.get() - mpCost;
                     ownerExistence.setMagicule(ownerExistence.getMagicule() - mpCost);
                     if (auraCost > 0.0) {
                        ownerExistence.setAura(ownerExistence.getAura() - auraCost);
                     }

                     if (shouldConsumeMax((RequestNamingMenuPacket.NamingType)namingType.get(), owner)) {
                        AttributeInstance mpInstance = owner.getAttribute(TensuraAttributes.MAX_MAGICULE);
                        if (mpInstance != null) {
                           double newMP = Math.max(((RangedAttribute)TensuraAttributes.MAX_MAGICULE.value()).getMinValue(), mpInstance.getBaseValue() - mpCost);
                           mpInstance.setBaseValue(newMP);
                        }

                        if (auraCost > 0.0) {
                           AttributeInstance apInstance = owner.getAttribute(TensuraAttributes.MAX_AURA);
                           if (apInstance != null) {
                              double newAP = Math.max(((RangedAttribute)TensuraAttributes.MAX_AURA.value()).getMinValue(), apInstance.getBaseValue() - auraCost);
                              apInstance.setBaseValue(newAP);
                           }
                        }
                     }
                  } else {
                     ownerExistence.setMagicule(ownerExistence.getMagicule() - (Double)cost.get());
                     if (shouldConsumeMax((RequestNamingMenuPacket.NamingType)namingType.get(), owner)) {
                        AttributeInstance instance = owner.getAttribute(TensuraAttributes.MAX_MAGICULE);
                        if (instance == null) {
                           return;
                        }

                        double newMP = Math.max(
                           ((RangedAttribute)TensuraAttributes.MAX_MAGICULE.value()).getMinValue(), instance.getBaseValue() - (Double)cost.get()
                        );
                        instance.setBaseValue(newMP);
                     }
                  }

                  ownerExistence.markDirty();
               }
            }
         }
      }
   }

   private static boolean shouldConsumeMax(RequestNamingMenuPacket.NamingType type, Player owner) {
      float chance = owner.getRandom().nextFloat() * 100.0F;
      if (type == RequestNamingMenuPacket.NamingType.HIGH) {
         return chance <= NamingMenu.CONFIG.endowLostChance;
      } else {
         return type == RequestNamingMenuPacket.NamingType.MEDIUM ? chance <= NamingMenu.CONFIG.evolveLostChance : chance <= NamingMenu.CONFIG.subdueLostChance;
      }
   }

   public static void namingReward(LivingEntity entity, IExistence existence, double gainedEP, RequestNamingMenuPacket.NamingType type) {
      EnergyHelper.removeSpiritualEPLimit(entity);
      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (magicule != null) {
         magicule.setBaseValue(magicule.getBaseValue() + gainedEP / 2.0);
         existence.setMagicule(Math.max(existence.getMagicule(), magicule.getValue()));
      }

      AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
      if (aura != null) {
         aura.setBaseValue(aura.getBaseValue() + gainedEP / 2.0);
         existence.setAura(Math.max(existence.getAura(), aura.getValue()));
      }

      if (!type.equals(RequestNamingMenuPacket.NamingType.LOW)) {
         if (!entity.getType().equals(EntityType.PLAYER)) {
            RaceHelper.evolveMobs(entity);
         }

         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
         if (optional.isPresent() && optional.get().getRace() instanceof TensuraRace tensuraRace) {
            ManasRace race = tensuraRace.getHarvestFestivalEvolution(optional.get(), entity);
            if (race != null) {
               RaceHelper.evolveRace(entity, race, true);
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.BUFF_ACTIVATE, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
         }

         if (type == RequestNamingMenuPacket.NamingType.HIGH) {
            Skills storage = SkillAPI.getSkillsFrom(entity);

            for (ManasSkillInstance instance : storage.getLearnedSkills()) {
               if (!(instance.getMastery() >= 0.0) && !entity.getRandom().nextBoolean()) {
                  int chance = entity.getRandom().nextInt(100);
                  double points = chance < 50 ? 25.0 : (chance < 75 ? 50.0 : (chance < 95 ? 75.0 : 100.0));
                  if (instance.getSkill() instanceof TensuraSkill skill) {
                     skill.addLearnPoint(instance, entity, 0, points);
                  }
               }
            }

            storage.markDirty();
         }
      }
   }

   public enum NamingType {
      LOW,
      MEDIUM,
      HIGH;
   }
}
