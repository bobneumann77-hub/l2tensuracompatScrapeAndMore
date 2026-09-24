package io.github.manasmods.tensura.ability.skill.unique;

import com.google.common.collect.BiMap;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ISynthesisSeparation;
import io.github.manasmods.tensura.block.CharybdisCoreBlock;
import io.github.manasmods.tensura.block.entity.CharybdisCoreBlockEntity;
import io.github.manasmods.tensura.block.entity.PrayingPathBlockEntity;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.subclass.IElementalSpirit;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.menu.UncraftingMenu;
import io.github.manasmods.tensura.network.s2c.OpenDegenerateMenuPayload;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.ExistenceStorage;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.spirit.ISpiritWielder;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.phys.BlockHitResult;

public class DegenerateSkill extends Skill implements ISynthesisSeparation {
   private static final UniqueSkillConfig.Degenerate CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Degenerate;

   public DegenerateSkill() {
      super(Skill.SkillType.UNIQUE);
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
         case 0 -> "degenerate.crafting";
         case 1 -> "degenerate.synthesise";
         case 2 -> "degenerate.separate";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCostEffect;
   }

   @Override
   public int getMaximumBonusLevel() {
      return CONFIG.maxBonusLevel;
   }

   @Override
   public List<String> getSeparateBlacklistEnchantments() {
      return CONFIG.separateBlacklist;
   }

   @Override
   public List<String> getSynthesisBlacklistEnchantments() {
      return CONFIG.synthesisBlacklist;
   }

   @Override
   public List<String> getBonusLevelBlackListEnchantments() {
      return CONFIG.maxBonusBlacklist;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      switch (mode) {
         case 0:
            if (entity instanceof ServerPlayer player) {
               player.closeContainer();
               if (player.isShiftKeyDown()) {
                  this.openSynthesisSeparationMenu(player, instance);
               } else {
                  player.nextContainerCounter();
                  NetworkManager.sendToPlayer(
                     player,
                     new OpenDegenerateMenuPayload(OpenDegenerateMenuPayload.MenuType.CRAFTING, player.containerCounter, player.getId(), this.getRegistryName())
                  );
                  player.containerMenu = new UncraftingMenu(player.containerCounter, player.getInventory(), this);
                  player.initMenu(player.containerMenu);
                  ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
               }

               player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
            }
            break;
         case 1:
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 5.0);
            BlockPos pos = result.getBlockPos();
            if (level.getBlockEntity(pos) instanceof CharybdisCoreBlockEntity core) {
               switch ((SculkSensorPhase)level.getBlockState(pos).getValue(CharybdisCoreBlock.MODE)) {
                  case INACTIVE:
                     this.fusingCore(entity, ObjectSelectionHelper.CONFIG.CharybdisCore.charybdisCoreFusingSkillsInactive, core.getEP());
                     break;
                  case ACTIVE:
                     this.fusingCore(entity, ObjectSelectionHelper.CONFIG.CharybdisCore.charybdisCoreFusingSkillsActive, core.getEP());
                     break;
                  case COOLDOWN:
                     this.fusingCore(
                        entity,
                        ObjectSelectionHelper.CONFIG.CharybdisCore.charybdisCoreFusingSkills,
                        ObjectSelectionHelper.CONFIG.CharybdisCore.charybdisCoreFusingEP
                     );
               }

               level.destroyBlock(pos, false);
               TensuraParticleHelper.addServerParticlesAroundPos(level.random, level, pos.getCenter(), ParticleTypes.SCULK_SOUL, 1.0);
               TensuraParticleHelper.addServerParticlesAroundPos(level.random, level, pos.getCenter(), (ParticleOptions)TensuraParticleTypes.SOUL.get(), 1.0);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
               return;
            }

            LivingEntity targetx = ObjectSelectionHelper.getTargetingEntity(entity, 4.0, false);
            if (targetx == null || !targetx.isAlive()) {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
               return;
            }

            if (targetx instanceof Player player && player.getAbilities().invulnerable
               || !RaceUtils.isSpiritual(targetx)
               || ExistenceStorage.isSummon(targetx)
               || targetx.getHealth() > targetx.getMaxHealth() * CONFIG.spiritualEntityHP) {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
               return;
            }

            DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.SYNTHESISE, mode).tensura$setBarrierBypassLevel(3.0F);
            if (targetx.hurt(source, targetx.getMaxHealth() * 10.0F)) {
               if (targetx instanceof IElementalSpirit spirit) {
                  ISpiritWielder spiritWielder = TensuraStorages.getSpiritFrom(entity);
                  if (spiritWielder.getSpiritLevelId(spirit.getElemental()) < spirit.getSpiritLevel().getId()
                     && spiritWielder.setSpiritLevel(spirit.getElemental(), spirit.getSpiritLevel())) {
                     spiritWielder.markDirty();
                     if (entity instanceof Player player) {
                        PrayingPathBlockEntity.grantSpiritMagic(player, spirit.getElemental(), spirit.getSpiritLevel());
                        PrayingPathBlockEntity.grantManipulation(player, spirit.getElemental());
                     }
                  }
               }

               if (!targetx.getType().is(TensuraEntityTags.NO_SYNTHESISE)) {
                  if (!targetx.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER) && (!(targetx instanceof Player) || entity.level().getLevelData().isHardcore())
                     )
                   {
                     boolean canStealSKills = TensuraGameRules.canStealSkill(level);
                     List<ManasSkill> learntSkill = new ArrayList<>();
                     Skills skills = SkillAPI.getSkillsFrom(targetx);

                     for (ManasSkillInstance targetSkill : skills.getLearnedSkills()) {
                        if (!targetSkill.isTemporarySkill()
                           && !(targetSkill.getMastery() < 0.0)
                           && targetSkill.getSkill() != this
                           && !targetSkill.is(TensuraSkillTags.NO_PLUNDERING)
                           && targetSkill.getSkill() instanceof Skill skill) {
                           Changeable<ManasSkill> changeable = Changeable.of(skill);
                           if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                                 .plunder(targetx, entity, canStealSKills, changeable)
                                 .isFalse()
                              && SkillHelper.learnSkill(entity, (ManasSkill)changeable.get(), instance.getRemoveTime())) {
                              learntSkill.add((ManasSkill)changeable.get());
                           }
                        }
                     }

                     if (canStealSKills) {
                        learntSkill.forEach(
                           skillx -> {
                              target.sendSystemMessage(
                                 Component.translatable("tensura.skill.forget.stolen", new Object[]{skillx.getChatDisplayName(true), entity.getName()})
                              );
                              SkillAPI.getSkillsFrom(target).forgetSkill(skillx);
                           }
                        );
                        skills.markDirty();
                     }
                  }

                  CompoundTag tag = instance.getOrCreateTag();
                  if (tag.contains("synthesisedList")) {
                     CompoundTag synthesisedList = (CompoundTag)tag.get("synthesisedList");
                     if (synthesisedList == null) {
                        return;
                     }

                     String targetID = EntityType.getKey(targetx.getType()).toString();
                     if (synthesisedList.contains(targetID)) {
                        return;
                     }

                     synthesisedList.putBoolean(targetID, true);
                  } else {
                     CompoundTag synthesisedList = new CompoundTag();
                     synthesisedList.putBoolean(EntityType.getKey(targetx.getType()).toString(), true);
                     tag.put("synthesisedList", synthesisedList);
                  }

                  if (!TensuraGameRules.canEpSteal(level) && targetx instanceof Player) {
                     entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
                     return;
                  }

                  double difference = Math.min(EnergyHelper.getEPGain(targetx, entity), EnergyHelper.CONFIG.maximumEPSteal);
                  if (EnergyHelper.drainEnergy(
                     targetx, entity, difference * CONFIG.spiritualEntityEP, false, EnergyHelper.DrainType.MAX_EP, EnergyHelper.GainType.MAX
                  )) {
                     IExistence existence = TensuraStorages.getExistenceFrom(targetx);
                     existence.setSkippingEPDrop(true);
                     existence.markDirty();
                  }
               }

               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.synthesizeCooldownMastered : CONFIG.synthesizeCooldown, mode);
               entity.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
               TensuraParticleHelper.addServerParticlesAroundSelf(targetx, ParticleTypes.ANGRY_VILLAGER, 1.0);
            }
            break;
         case 2:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 4.0, false);
            if (target != null) {
               if (!target.isAlive()) {
                  return;
               }

               if (target instanceof Player player && player.getAbilities().invulnerable) {
                  return;
               }

               if (SubordinateHelper.isSubordinate(entity, target) && entity.isShiftKeyDown()) {
                  Predicate<Holder<MobEffect>> predicate = effect -> !effect.is(TensuraTags.MobEffects.SKILL_BUFF);
                  if (TensuraMobEffect.removePredicateEffect(target, predicate, this.getMagiculeCost(entity, instance, mode))) {
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.COMPOSTER, 1.0);
                  }

                  IEffect effect = TensuraStorages.getEffectFrom(target);
                  if (effect.getSeveranceAmount() > 0.0F && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                     effect.setSeveranceAmount(0.0F);
                     effect.markDirty();
                     TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.PORTAL, 1.0);
                  }
               } else {
                  if (this.canBypassSeparation(target)) {
                     entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
                     level.playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                     return;
                  }

                  if (!target.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)
                     && EnergyHelper.getMaxEP(target) < EnergyHelper.getMaxEP(entity) * CONFIG.separateEP) {
                     for (ManasSkillInstance targetSkill : SkillAPI.getSkillsFrom(target).getLearnedSkills()) {
                        if (!targetSkill.isTemporarySkill()
                           && !(targetSkill.getMastery() < 0.0)
                           && targetSkill.getSkill() != this
                           && !targetSkill.is(TensuraSkillTags.NO_PLUNDERING)
                           && (targetSkill.is(TensuraSkillTags.EXTRA_SKILLS) || targetSkill.is(TensuraSkillTags.COMMON_SKILLS))) {
                           boolean steal = TensuraGameRules.canStealSkill(level);
                           Changeable<ManasSkill> changeable = Changeable.of(targetSkill.getSkill());
                           if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                                 .plunder(target, entity, steal, changeable)
                                 .isFalse()
                              && SkillHelper.learnSkill(entity, (ManasSkill)changeable.get(), instance.getRemoveTime())) {
                              if (steal) {
                                 target.sendSystemMessage(
                                    Component.translatable(
                                       "tensura.skill.forget.stolen", new Object[]{((ManasSkill)changeable.get()).getChatDisplayName(true), entity.getName()}
                                    )
                                 );
                                 SkillAPI.getSkillsFrom(target).forgetSkill((ManasSkill)changeable.get());
                              }

                              TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER, 1.0);
                              instance.addMasteryPoint(entity);
                           }
                        }
                     }

                     instance.setCoolDown(instance.isMastered(entity) ? CONFIG.separateCooldownMastered : CONFIG.separateCooldown, mode);
                  } else {
                     entity.sendSystemMessage(Component.translatable("tensura.targeting.ep_not_meet").withStyle(ChatFormatting.RED));
                  }

                  TensuraDamageHelper.markHurt(target, entity);
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               }
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 5.0);
               BlockPos pos = result.getBlockPos();
               BlockState state = level.getBlockState(pos);
               if (state.isAir()) {
                  Predicate<Holder<MobEffect>> predicate = effect -> !effect.is(TensuraTags.MobEffects.SKILL_BUFF);
                  if (TensuraMobEffect.removePredicateEffect(entity, predicate, this.getMagiculeCost(entity, instance, mode))) {
                     instance.setCoolDown(instance.isMastered(entity) ? CONFIG.separateCooldownMastered : CONFIG.separateCooldown, mode);
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.COMPOSTER, 1.0);
                  }

                  IEffect effect = TensuraStorages.getEffectFrom(entity);
                  if (effect.getSeveranceAmount() > 0.0F && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                     effect.setSeveranceAmount(0.0F);
                     effect.markDirty();
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.PORTAL, 1.0);
                  }

                  entity.swing(InteractionHand.MAIN_HAND, true);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               } else if (TensuraGameRules.canSkillGrief(level)) {
                  if (state.is(TensuraBlockTags.ORES)) {
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                        .isFalse()) {
                        instance.addMasteryPoint(entity);
                        if (state.is(TensuraBlockTags.ORES_STONE)) {
                           level.destroyBlock(pos, true, entity);
                           level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
                        } else if (state.is(TensuraBlockTags.ORES_DEEPSLATE)) {
                           level.destroyBlock(pos, true, entity);
                           level.setBlockAndUpdate(pos, Blocks.DEEPSLATE.defaultBlockState());
                        } else if (state.is(TensuraBlockTags.ORES_NETHER)) {
                           level.destroyBlock(pos, true, entity);
                           level.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());
                        }

                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                     }
                  } else if (state.is(Blocks.WET_SPONGE)) {
                     if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                        .isFalse()) {
                        instance.addMasteryPoint(entity);
                        level.destroyBlock(pos, false, entity);
                        level.setBlockAndUpdate(pos, Blocks.SPONGE.defaultBlockState());
                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                     }
                  } else if (state.getBlock() instanceof BrushableBlock block) {
                     if (level.getBlockEntity(pos) instanceof BrushableBlockEntity brushable
                        && entity.level().getServer() != null
                        && entity instanceof Player player
                        && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                           .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                           .isFalse()) {
                        brushable.unpackLootTable(player);
                        if (!brushable.getItem().isEmpty()) {
                           double x = pos.getX() + 0.5F;
                           double y = pos.getY() + 0.5F;
                           double z = pos.getZ() + 0.5F;
                           ItemEntity itemEntity = new ItemEntity(entity.level(), x, y, z, brushable.getItem().split(entity.getRandom().nextInt(21) + 10));
                           itemEntity.setDefaultPickUpDelay();
                           entity.level().addFreshEntity(itemEntity);
                        }

                        instance.addMasteryPoint(entity);
                        level.destroyBlock(pos, false, entity);
                        level.setBlockAndUpdate(pos, block.getTurnsInto().defaultBlockState());
                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                     }
                  } else if (state.getBlock() instanceof WeatheringCopper) {
                     WeatheringCopper.getPrevious(state)
                        .ifPresent(
                           blockState -> {
                              if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                                 .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                                 .isFalse()) {
                                 level.setBlockAndUpdate(pos, blockState);
                                 instance.addMasteryPoint(entity);
                                 ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                                    .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                              }
                           }
                        );
                  } else {
                     Optional.ofNullable((Block)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(state.getBlock()))
                        .map(blockx -> blockx.withPropertiesOf(state))
                        .ifPresent(
                           waxOff -> {
                              if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                                 .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ())
                                 .isFalse()) {
                                 level.setBlockAndUpdate(pos, waxOff);
                                 instance.addMasteryPoint(entity);
                                 ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                                    .grief(instance, level, entity, pos.getX(), pos.getY(), pos.getZ());
                              }
                           }
                        );
                  }

                  entity.swing(InteractionHand.MAIN_HAND, true);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               } else {
                  entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.gamerule").withStyle(ChatFormatting.RED));
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               }
            }
      }
   }

   private void fusingCore(LivingEntity entity, List<? extends String> strings, double EP) {
      List<ManasSkill> list = strings.stream()
         .map(skill -> (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(skill)))
         .filter(Objects::nonNull)
         .toList();
      list.forEach(skill -> SkillHelper.learnSkill(entity, skill));
      if (CONFIG.coreEPReduction) {
         int times = (int)(EnergyHelper.getBaseMaxEP(entity) / EP);
         double percentage = Mth.clamp(0.01 * times, 0.0, EnergyHelper.CONFIG.maxEPReductionPercentage / 100.0);
         EP *= 1.0 - percentage;
      }

      EnergyHelper.gainMagicule(entity, EP, EnergyHelper.GainType.MAX);
      EnergyHelper.gainMagicule(entity, EP, EnergyHelper.GainType.NORMAL);
   }

   private boolean canBypassSeparation(LivingEntity target) {
      return target.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)
         ? true
         : SkillUtils.isSkillToggled(target, (ManasSkill)UniqueSkills.ANTI_SKILL.get())
            || TensuraStorages.getAbilityFrom(target).isAbilityInActivePreset((ManasSkill)UniqueSkills.ANTI_SKILL.get());
   }
}
