package io.github.manasmods.tensura.ability.skill;

import com.mojang.serialization.Codec;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.advancement.AbilityTrigger;
import io.github.manasmods.tensura.config.ability.SkillConfig;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Skill extends TensuraSkill {
   public static final SkillConfig SKILL_CONFIG = (SkillConfig)ConfigRegistry.getConfig(SkillConfig.class);
   private final Skill.SkillType type;
   private int cachedMaxMastery = Integer.MIN_VALUE;
   private double cachedAcquiringMpCost = Double.NaN;

   public Skill(Skill.SkillType type) {
      this.type = type;
   }

   @Nullable
   public ResourceLocation getSkillIcon() {
      ResourceLocation id = this.getRegistryName();
      return id == null
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/temp_textures/item/confused_rimuru.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/skill/" + this.getType().getNamespace() + "/" + id.getPath().replace('/', '.') + ".png");
   }

   @Nullable
   @Override
   public MutableComponent getColoredName() {
      MutableComponent name = super.getName();
      return name == null ? null : name.withStyle(this.getType().getChatFormatting());
   }

   public int getMaxMastery() {
      if (this.cachedMaxMastery == Integer.MIN_VALUE) {
         this.cachedMaxMastery = switch (this.getType()) {
            case EXTRA -> SKILL_CONFIG.Mastery.masteryExtra;
            case UNIQUE -> SKILL_CONFIG.Mastery.masteryUnique;
            case ULTIMATE -> SKILL_CONFIG.Mastery.masteryUltimate;
            default -> SKILL_CONFIG.Mastery.masteryIntrinsic;
         };
      }

      return this.cachedMaxMastery;
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      if (Double.isNaN(this.cachedAcquiringMpCost)) {
         this.cachedAcquiringMpCost = switch (this.getType()) {
            case INTRINSIC -> SKILL_CONFIG.mpAcquirementIntrinsic;
            case COMMON -> SKILL_CONFIG.mpAcquirementCommon;
            default -> SKILL_CONFIG.mpAcquirementExtra;
         };
      }

      return this.cachedAcquiringMpCost;
   }

   @Override
   protected boolean isAffectedByStatus(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return super.isAffectedByStatus(instance, entity, mode) ? true : entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_SKILL));
   }

   @Override
   public DamageSource createSource(ManasSkillInstance instance, LivingEntity attacker, ResourceKey<DamageType> type, int mode) {
      return super.createSource(instance, attacker, type, mode).tensura$setSkillType(this.getType());
   }

   @Override
   public void addMasteryStatistic(ServerPlayer player) {
      player.awardStat(TensuraStats.SKILL_MASTERED);
      ((AbilityTrigger)TensuraCriteriaTriggers.SKILL_MASTERED.get()).trigger(player, this);
      if (this.getType().equals(Skill.SkillType.UNIQUE)) {
         ((AbilityTrigger)TensuraCriteriaTriggers.UNIQUE_SKILL_MASTERED.get()).trigger(player, this);
      }
   }

   @Generated
   public Skill.SkillType getType() {
      return this.type;
   }

   @Generated
   public int getCachedMaxMastery() {
      return this.cachedMaxMastery;
   }

   @Generated
   public double getCachedAcquiringMpCost() {
      return this.cachedAcquiringMpCost;
   }

   public enum SkillType implements StringRepresentable {
      RESISTANCE("resistance", ChatFormatting.DARK_AQUA),
      INTRINSIC("intrinsic", ChatFormatting.AQUA),
      COMMON("common", ChatFormatting.GREEN),
      EXTRA("extra", ChatFormatting.YELLOW),
      UNIQUE("unique", ChatFormatting.GOLD),
      ULTIMATE("ultimate", ChatFormatting.RED);

      private final String namespace;
      private final ChatFormatting chatFormatting;
      public static final Codec<Skill.SkillType> CODEC = StringRepresentable.fromEnum(Skill.SkillType::values);

      public MutableComponent getName() {
         return Component.translatable("tensura.skill.type." + this.namespace).withStyle(this.chatFormatting);
      }

      @NotNull
      public String getSerializedName() {
         return this.namespace;
      }

      @Generated
      public String getNamespace() {
         return this.namespace;
      }

      @Generated
      public ChatFormatting getChatFormatting() {
         return this.chatFormatting;
      }

      @Generated
      SkillType(final String namespace, final ChatFormatting chatFormatting) {
         this.namespace = namespace;
         this.chatFormatting = chatFormatting;
      }
   }
}
