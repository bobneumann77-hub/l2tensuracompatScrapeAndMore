package io.github.manasmods.tensura.handler;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.PlayerRespawn;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.RaceEvents;
import io.github.manasmods.manascore.race.api.RaceEvents.RaceAbilityActivationEvent;
import io.github.manasmods.manascore.race.api.RaceEvents.RaceAbilityReleaseEvent;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.ability.magic.summon.SummonGreaterElementalMagic;
import io.github.manasmods.tensura.ability.magic.summon.SummonMediumElementalMagic;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.event.TensuraSpiritEvents;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.magic.SummoningMagics;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RacesHandler {
   public static void init() {
      PlayerEvent.PLAYER_JOIN.register(ReincarnationMenu::checkForFirstLogin);
      PlayerEvent.PLAYER_RESPAWN.register((PlayerRespawn)(newPlayer, conqueredEnd, removalReason) -> {
         if (!conqueredEnd && !newPlayer.level().isClientSide()) {
            if (!ReincarnationMenu.checkForFirstLogin(newPlayer)) {
               TensuraStorages.resetEffect(newPlayer);
               IExistence existence = TensuraStorages.getExistenceFrom(newPlayer);
               existence.setMagicule(newPlayer.getAttributeValue(TensuraAttributes.MAX_MAGICULE));
               existence.setAura(newPlayer.getAttributeValue(TensuraAttributes.MAX_AURA));
               existence.setSpiritualHealth(newPlayer.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH));
               existence.setSkippingEPDrop(false);
               existence.setTemporaryOwner(null);
               existence.clearNeutralTargets();
               existence.setHumanKill(0);
               existence.markDirty();
               Skills storage = SkillAPI.getSkillsFrom(newPlayer);

               for (ManasSkillInstance copy : List.copyOf(storage.getLearnedSkills())) {
                  Optional<ManasSkillInstance> optional = storage.getSkill(copy.getSkill());
                  if (!optional.isEmpty()) {
                     ManasSkillInstance instance = optional.get();
                     if (instance.isTemporarySkill()) {
                        if (instance.getTag() == null || instance.getTag().getInt("OldRemoval") != -1) {
                           storage.forgetSkill(instance);
                        } else if (instance.getTag().contains("OldMastery")) {
                           instance.setRemoveTime(-1);
                           instance.setMastery(instance.getTag().getInt("OldMastery"));
                           if (instance.getMastery() < 0.0 && instance.isToggled()) {
                              instance.setToggled(false);
                              instance.onToggleOff(newPlayer);
                           }

                           instance.getTag().remove("OldMastery");
                        }
                     } else if (instance.isToggled()) {
                        if (instance.canInteractSkill(newPlayer)) {
                           instance.onToggleOn(newPlayer);
                        } else {
                           instance.setToggled(false);
                        }
                     }
                  }
               }
            }
         }
      });
      RaceEvents.ACTIVATE_ABILITY
         .register(
            (RaceAbilityActivationEvent)(instance, owner) -> {
               if (owner instanceof Player player
                  && owner.getVehicle() instanceof ITensuraMount mount
                  && Objects.equals(SubordinateHelper.getSubordinateOwnerUUID((LivingEntity)mount), owner.getUUID())) {
                  if (mount.canActivateMountAbility(player)) {
                     mount.mountAbility(player);
                  }

                  return EventResult.interruptFalse();
               } else if (instance.isOnCooldown()) {
                  if (owner instanceof Player player) {
                     player.displayClientMessage(
                        Component.translatable("tensura.race.cooldown", new Object[]{instance.getChatDisplayName(false)}).withStyle(ChatFormatting.RED), true
                     );
                  }

                  return EventResult.pass();
               } else {
                  return EventResult.pass();
               }
            }
         );
      RaceEvents.RELEASE_ABILITY.register((RaceAbilityReleaseEvent)(instance, owner, tick) -> {
         if (owner.getVehicle() instanceof ITensuraMount mount && SubordinateHelper.getSubordinateOwnerUUID((LivingEntity)mount) == owner.getUUID()) {
            if (mount.canActivateMountAbility(owner)) {
               mount.mountAbilityRelease(owner);
            }

            return EventResult.interruptFalse();
         } else {
            return EventResult.pass();
         }
      });
      TensuraSpiritEvents.SPIRIT_UPDATE.register((TensuraSpiritEvents.SpiritLevelUpdate)(player, element, level) -> {
         if (((Element)element.get()).getElementType().equals(Element.ElementType.COMMON)) {
            if (((SpiritualMagic.SpiritLevel)level.get()).getId() >= 2) {
               SkillHelper.learnSkill(player, ((SummonMediumElementalMagic)SummoningMagics.SUMMON_MEDIUM_ELEMENTAL.get()).createDefaultInstance());
            }

            if (((SpiritualMagic.SpiritLevel)level.get()).getId() >= 3) {
               SkillHelper.learnSkill(player, ((SummonGreaterElementalMagic)SummoningMagics.SUMMON_GREATER_ELEMENTAL.get()).createDefaultInstance());
            }
         }

         grantHeroEgg(player, (Element)element.get(), (SpiritualMagic.SpiritLevel)level.get());
         return EventResult.pass();
      });
   }

   public static void grantHeroEgg(LivingEntity player, Element element, SpiritualMagic.SpiritLevel level) {
      IExistence existence = TensuraStorages.getExistenceFrom(player);
      if (!existence.isHeroEgg() && !existence.isTrueHero() && !existence.isTrueDemonLord() && existence.getHarvestTick() <= 0) {
         Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(player).getRace();
         if (!race.isEmpty()) {
            if (!(
               race.get().getRace() instanceof TensuraRace tensuraRace
                  && !tensuraRace.getAlignment().isCanBecomeHero()
                  && !existence.getAlignment().isCanBecomeHero()
            )) {
               int greaterLevel = TensuraRace.BASE_CONFIG.Hero.heroSpiritLevel;
               ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
               int greater = 0;
               int greaterHero = 0;

               for (Element magicElemental : Element.values()) {
                  if (spirit.getSpiritLevelId(magicElemental) >= greaterLevel || magicElemental.getId() == element.getId() && level.getId() >= greaterLevel) {
                     if (magicElemental.isChosenHeroElemental()) {
                        greaterHero++;
                     } else {
                        greater++;
                     }
                  }
               }

               if (greater + greaterHero >= 7 && player instanceof ServerPlayer serverPlayer) {
                  ((PlayerTrigger)TensuraCriteriaTriggers.SPIRIT_BLESSED.get()).trigger(serverPlayer);
               }

               if (greater >= TensuraRace.BASE_CONFIG.Hero.heroCommonSpiritNumber) {
                  if (greaterHero >= TensuraRace.BASE_CONFIG.Hero.heroSpiritNumber) {
                     existence.setHeroEgg(true);
                     existence.markDirty();
                     player.sendSystemMessage(Component.translatable("tensura.evolve.hero.egg").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                     player.level()
                        .playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0F, 1.0F);
                     ManasSkill skill = (ManasSkill)IntrinsicSkills.EYE_OF_TRUTH.get();
                     if (SkillHelper.learnSkill(player, skill)) {
                        Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
                        if (optional.isEmpty()) {
                           return;
                        }

                        optional.get().addIntrinsicSkill(skill);
                        optional.get().markDirty();
                        RaceAPI.getRaceFrom(player).markDirty();
                     }
                  }
               }
            }
         }
      }
   }
}
