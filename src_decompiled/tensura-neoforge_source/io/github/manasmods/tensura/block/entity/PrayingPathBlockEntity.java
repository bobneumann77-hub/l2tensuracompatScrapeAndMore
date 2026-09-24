package io.github.manasmods.tensura.block.entity;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.handler.RacesHandler;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class PrayingPathBlockEntity extends BlockEntity {
   private UUID currentPrayingPlayer = null;
   private int currentPrayingTick = 0;

   public PrayingPathBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super((BlockEntityType)TensuraBlockEntities.PRAYING_PATH.get(), pPos, pBlockState);
   }

   @Nullable
   private ServerPlayer getCurrentPrayingPlayer(Level level) {
      return this.currentPrayingPlayer == null ? null : (ServerPlayer)level.getPlayerByUUID(this.currentPrayingPlayer);
   }

   private void resetPraying() {
      this.currentPrayingTick = 0;
      this.currentPrayingPlayer = null;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Nullable
   private static Pair<Element, SpiritualMagic.SpiritLevel> getRandomSpiritLevel(Player player) {
      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(player).getRace();
      if (race.isEmpty()) {
         return null;
      }

      float chance = player.getRandom().nextFloat() * 100.0F;
      Element element = Element.byId(player.getRandom().nextInt(7));
      ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
      int currentLevel = spirit.getSpiritLevelId(element);
      double lord;
      double greater;
      double medium;
      double lesser;
      if (race.get().getRace() instanceof TensuraRace tensuraRace && tensuraRace.hasGuaranteeElemental()) {
         for (Element guaranteeElement : Element.values()) {
            for (SpiritualMagic.SpiritLevel spiritLevel : SpiritualMagic.SpiritLevel.values()) {
               if (tensuraRace.getElementalSpiritsChance(guaranteeElement, spiritLevel) == 100.0) {
                  int currentSpiritLevel = spirit.getSpiritLevelId(guaranteeElement);
                  if (currentSpiritLevel < spiritLevel.getId()) {
                     element = guaranteeElement;
                     currentLevel = currentSpiritLevel;
                     break;
                  }
               }
            }
         }

         lord = tensuraRace.getElementalSpiritsChance(element, SpiritualMagic.SpiritLevel.LORD);
         greater = lord + tensuraRace.getElementalSpiritsChance(element, SpiritualMagic.SpiritLevel.GREATER);
         medium = greater + tensuraRace.getElementalSpiritsChance(element, SpiritualMagic.SpiritLevel.MEDIUM);
         lesser = medium + tensuraRace.getElementalSpiritsChance(element, SpiritualMagic.SpiritLevel.LESSER);
      } else {
         lord = TensuraRace.getDefaultElementalSpiritsChance(SpiritualMagic.SpiritLevel.LORD);
         greater = lord + TensuraRace.getDefaultElementalSpiritsChance(SpiritualMagic.SpiritLevel.GREATER);
         medium = greater + TensuraRace.getDefaultElementalSpiritsChance(SpiritualMagic.SpiritLevel.MEDIUM);
         lesser = medium + TensuraRace.getDefaultElementalSpiritsChance(SpiritualMagic.SpiritLevel.LESSER);
      }

      if (lord > 0.0 && chance < lord && currentLevel >= 2) {
         return Pair.of(element, SpiritualMagic.SpiritLevel.LORD);
      } else if (greater > 0.0 && chance < greater) {
         return Pair.of(element, SpiritualMagic.SpiritLevel.GREATER);
      } else if (medium > 0.0 && chance < medium) {
         return Pair.of(element, SpiritualMagic.SpiritLevel.MEDIUM);
      } else {
         return lesser > 0.0 && chance < lesser ? Pair.of(element, SpiritualMagic.SpiritLevel.LESSER) : null;
      }
   }

   private static void rollSpirits(ServerPlayer player) {
      player.awardStat(Stats.CUSTOM.get(TensuraStats.SPIRIT_PRAY_TIME));
      IExistence existence = TensuraStorages.getExistenceFrom(player);
      if (!existence.isBlessed()) {
         Pair<Element, SpiritualMagic.SpiritLevel> elemental = getRandomSpiritLevel(player);
         if (elemental == null) {
            RacesHandler.grantHeroEgg(player, Element.UNIDENTIFIED, SpiritualMagic.SpiritLevel.LESSER);
            player.awardStat(Stats.CUSTOM.get(TensuraStats.SPIRIT_PRAY_FAIL_TIME));
            player.sendSystemMessage(Component.translatable("tensura.magic.spiritual.chosen.failed").withStyle(ChatFormatting.RED));
            TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.ANGRY_VILLAGER, 1.0);
            player.level()
               .playSound(
                  null, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F
               );
         } else {
            grantSpirit(player, (Element)elemental.getFirst(), (SpiritualMagic.SpiritLevel)elemental.getSecond());
         }
      } else {
         boolean chosen = false;

         for (Element elemental : Element.values()) {
            if (elemental.isChosenHeroElemental()) {
               if (chosen) {
                  continue;
               }

               chosen = true;
            } else if (elemental.getElementType() != Element.ElementType.COMMON) {
               continue;
            }

            SpiritualMagic.SpiritLevel level = SpiritualMagic.SpiritLevel.GREATER;
            grantSpirit(player, elemental, level);
         }

         existence.setBlessed(false);
         existence.markDirty();
      }
   }

   private static void grantSpirit(ServerPlayer player, Element elemental, SpiritualMagic.SpiritLevel level) {
      ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
      int currentLevel = spirit.getSpiritLevelId(elemental);
      if (level.getId() <= currentLevel) {
         RacesHandler.grantHeroEgg(player, elemental, level);
         player.awardStat(Stats.CUSTOM.get(TensuraStats.SPIRIT_PRAY_FAIL_TIME));
         player.sendSystemMessage(
            Component.translatable("tensura.magic.spiritual.chosen.duplicated", new Object[]{level.getSpiritName(elemental)}).withStyle(ChatFormatting.RED)
         );
         TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.ANGRY_VILLAGER, 1.0);
         player.level()
            .playSound(
               null, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.PLAYERS, 1.0F, 1.0F
            );
      } else {
         if (spirit.setSpiritLevel(elemental, level)) {
            spirit.markDirty();
            grantSpiritMagic(player, elemental, level);
            grantManipulation(player, elemental);
            player.sendSystemMessage(
               Component.translatable("tensura.magic.spiritual.chosen", new Object[]{level.getSpiritName(elemental)})
                  .setStyle(Style.EMPTY.withColor(elemental.getColor()))
            );
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            player.awardStat(Stats.CUSTOM.get(TensuraStats.SPIRIT_CONTRACTED_TIME));
            ((PlayerTrigger)TensuraCriteriaTriggers.SPIRIT_CONTRACTED.get()).trigger(player);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.FLASH, 1.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(player, ParticleTypes.END_ROD, 2.0);
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 9, false, false, false));
         }
      }
   }

   public static void grantSpiritMagic(Player player, Element elemental, SpiritualMagic.SpiritLevel level) {
      for (ManasSkill manasSkill : SkillAPI.getSkillRegistry()) {
         if (manasSkill instanceof SpiritualMagic skill && skill.getElemental() == elemental && skill.getLevel().getId() <= level.getId()) {
            SkillHelper.learnSkill(player, skill);
         }
      }
   }

   public static void grantManipulation(Player player, Element elemental) {
      ResourceLocation location = elemental.getManipulation();
      if (location != null) {
         ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(location);
         ManasSkillInstance manipulation = new TensuraSkillInstance(skill);
         manipulation.setMastery(-100.0);
         SkillHelper.learnSkill(player, manipulation);
      }
   }

   public static void tick(Level level, BlockPos pos, BlockState state, PrayingPathBlockEntity path) {
      if (!level.isClientSide()) {
         AABB aabb = new AABB(pos.above());
         ServerPlayer player = path.getCurrentPrayingPlayer(level);
         if (player == null) {
            List<ServerPlayer> players = level.getEntitiesOfClass(ServerPlayer.class, aabb, entity -> entity.onGround() && entity.isShiftKeyDown());
            Iterator cooldown = players.iterator();
            if (cooldown.hasNext()) {
               ServerPlayer prayer = (ServerPlayer)cooldown.next();
               path.currentPrayingPlayer = prayer.getUUID();
               path.currentPrayingTick = 0;
            }
         }

         if (player == null) {
            if (path.currentPrayingTick > 0) {
               path.currentPrayingTick = 0;
            }
         } else {
            if (player.onGround() && player.isShiftKeyDown() && aabb.contains(player.position().add(0.0, 0.25, 0.0))) {
               ISpiritWielder spirit = TensuraStorages.getSpiritFrom(player);
               int cooldown = TensuraRace.BASE_CONFIG.Spirit.prayingCooldown;
               if (spirit.getSpiritCooldown() > 0) {
                  path.resetPraying();
                  if (spirit.getSpiritCooldown() <= cooldown - 3) {
                     player.displayClientMessage(Component.translatable("tensura.magic.spiritual.chosen.cooldown").withStyle(ChatFormatting.RED), true);
                  }

                  return;
               }

               if (isSidePrayingPath(level, pos, path)) {
                  return;
               }

               int tick = path.currentPrayingTick;
               int prayingTime = TensuraRace.BASE_CONFIG.Spirit.prayingTime;
               if (tick < prayingTime) {
                  path.currentPrayingTick++;
                  player.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.TRUE_BLINDNESS), 40, 0, false, false, false));
                  if (tick % 2 == 0) {
                     TensuraParticleHelper.addServerParticlesAroundSelf(
                        player,
                        TensuraParticleUtils.getPrayingSpirit(
                           player.getX(), player.getRandomY() + player.getBbHeight() / 4.0F, player.getZ(), 2.0, player.getRandom()
                        ),
                        1.0,
                        1
                     );
                     TensuraParticleHelper.addServerParticlesAroundSelf(
                        player,
                        TensuraParticleUtils.getPrayingSpirit(
                           player.getX(), player.getRandomY() + player.getBbHeight() / 3.0F, player.getZ(), 1.5, player.getRandom()
                        ),
                        1.0,
                        1
                     );
                     TensuraParticleHelper.addServerParticlesAroundSelf(
                        player,
                        TensuraParticleUtils.getPrayingSpirit(
                           player.getX(), player.getRandomY() + player.getBbHeight() / 2.0F, player.getZ(), 1.0, player.getRandom()
                        ),
                        1.0,
                        1
                     );
                  }

                  return;
               }

               rollSpirits(player);
               spirit.setSpiritCooldown(cooldown);
               spirit.markDirty();
               path.resetPraying();
            } else {
               path.resetPraying();
            }
         }
      }
   }

   private static boolean isSidePrayingPath(Level level, BlockPos pos, PrayingPathBlockEntity path) {
      for (Direction direction : Plane.HORIZONTAL) {
         if (level.getBlockEntity(pos.relative(direction)) instanceof PrayingPathBlockEntity prayingPath
            && prayingPath.currentPrayingTick > path.currentPrayingTick) {
            path.resetPraying();
            return true;
         }
      }

      return false;
   }
}
