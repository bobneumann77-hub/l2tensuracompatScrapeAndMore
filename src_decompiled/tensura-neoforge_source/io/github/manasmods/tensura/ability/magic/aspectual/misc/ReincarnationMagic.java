package io.github.manasmods.tensura.ability.magic.aspectual.misc;

import com.mojang.datafixers.util.Pair;
import dev.architectury.registry.menu.MenuRegistry;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.SkillEvents.RemoveSkillEvent;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.ability.skill.intrinsic.AbsorbDissolveSkill;
import io.github.manasmods.tensura.ability.skill.unique.CookSkill;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.data.TensuraBiomeTags;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.item.misc.ResetScrollItem;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ReincarnationMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Reincarnation CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Reincarnation;

   public ReincarnationMagic() {
      super(AspectualMagic.AspectualType.MISC);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public boolean isInstantCast(ManasSkillInstance instance, LivingEntity entity) {
      return false;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.minimalMagicule;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      MagicCircle.castMagicCircle(
         entity.getBbWidth() * 5.0F,
         25,
         MagicCircleVariant.MISC,
         true,
         entity,
         instance.getOrCreateTag(),
         0.0F,
         Vec3.ZERO,
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
      MagicCircle.castMagicCircle(
         "MagicCircleID2",
         entity.getBbWidth() * 3.0F,
         25,
         MagicCircleVariant.MISC,
         true,
         entity,
         instance.getOrCreateTag(),
         0.0F,
         0.0F,
         new Vec3(0.0, entity.getBbHeight() * 1.0F / 3.0F, 0.0),
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
      MagicCircle.castMagicCircle(
         "MagicCircleID3",
         entity.getBbWidth() * 4.0F,
         25,
         MagicCircleVariant.MISC,
         true,
         entity,
         instance.getOrCreateTag(),
         0.0F,
         0.0F,
         new Vec3(0.0, entity.getBbHeight() * 2.0F / 3.0F, 0.0),
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (entity instanceof ServerPlayer player) {
            Level level = entity.level();
            if (SkillUtils.inSpiritualWorld(level.dimension()) || level.getBiome(entity.blockPosition()).is(TensuraBiomeTags.IS_UNSAFE_FOR_SPAWN)) {
               player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.location").withStyle(ChatFormatting.RED), false);
            } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               double maxAP = entity.getAttributeBaseValue(TensuraAttributes.MAX_AURA);
               double maxMP = entity.getAttributeBaseValue(TensuraAttributes.MAX_MAGICULE);
               Races races = RaceAPI.getRaceFrom(player);
               Optional<ManasRaceInstance> optional = races.getRace();
               if (optional.isPresent()) {
                  Skills storage = SkillAPI.getSkillsFrom(player);
                  Iterator<ManasSkillInstance> iterator = storage.getLearnedSkills().iterator();

                  while (iterator.hasNext()) {
                     if (iterator.next() instanceof TensuraSkillInstance oldInstance
                        && this.shouldResetRemove(player, oldInstance)
                        && !((RemoveSkillEvent)SkillEvents.REMOVE_SKILL.invoker()).removeSkill(oldInstance, player, Changeable.of(null)).isFalse()) {
                        oldInstance.onForgetSkill(player);
                        oldInstance.markDirty();
                        iterator.remove();
                     }
                  }

                  storage.markDirty();
               }

               ResetScrollItem.resetRaceFailsafe(player);
               CookSkill.removeCookedHP(player);
               TensuraStorages.resetPlayerData(player);
               TensuraStorages.resetExistence(player, false, false, false);
               TensuraStorages.resetEffect(player);
               player.setRespawnPosition(Level.OVERWORLD, null, 0.0F, false, false);

               for (AttributeInstance attribute : player.getAttributes().attributes.values()) {
                  attribute.removeModifier(TensuraRace.DEFAULT_RACE_ID);
               }

               EnergyHelper.removeSpiritualEPLimit(player);
               AbsorbDissolveSkill.resetSlimeCoreBoost(player);
               float newAP = (float)(maxAP * (1.0F - CONFIG.auraCost));
               float newMP = (float)(maxMP * (1.0F - CONFIG.magiculeCost));
               MenuRegistry.openExtendedMenu(
                  player,
                  new SimpleMenuProvider(
                     (i, inventory, pPlayer) -> new ReincarnationMenu(i, inventory, pPlayer, true, instance.isMastered(entity) ? 2 : 1, newAP, newMP),
                     Component.translatable("tensura.reincarnation")
                  ),
                  buf -> {
                     buf.writeBoolean(true);
                     buf.writeInt(instance.isMastered(entity) ? 2 : 1);
                     buf.writeFloat(newAP);
                     buf.writeFloat(newMP);
                  }
               );
               IExistence existence = TensuraStorages.getExistenceFrom(entity);
               existence.setAura(Math.min(existence.getAura(), newAP));
               existence.setMagicule(Math.min(existence.getMagicule(), newMP));
               ResetScrollItem.resetFlight(player);
               ResetScrollItem.resetWarpPoints(player);
               instance.addMasteryPoint(entity, 20.0);
               player.manasCore$sync(player);
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLASH, 1.0);
            }
         }
      }
   }

   private boolean shouldResetRemove(LivingEntity entity, ManasSkillInstance instance) {
      if (instance.isTemporarySkill()) {
         return true;
      }

      Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
      return optional.isPresent() && optional.get().getObtainedIntrinsicSkills().contains(instance.getSkill());
   }
}
