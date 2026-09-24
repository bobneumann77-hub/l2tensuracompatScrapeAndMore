package io.github.manasmods.tensura.ability.magic;

import com.mojang.serialization.Codec;
import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.manascore.skill.impl.TickingSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.advancement.AbilityTrigger;
import io.github.manasmods.tensura.config.ability.MagicConfig;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Magic extends TensuraSkill {
   public static final MagicConfig MAGIC_CONFIG = (MagicConfig)ConfigRegistry.getConfig(MagicConfig.class);
   private ResourceLocation cachedCastingLocation;
   private final Magic.MagicType type;
   public static final ResourceLocation CASTING = ResourceLocation.fromNamespaceAndPath("tensura", "casting");

   public Magic(Magic.MagicType type) {
      this.type = type;
      double speed = MAGIC_CONFIG.castingSpeed - 1.0;
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, CASTING, speed, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, CASTING, MAGIC_CONFIG.castingRange, Operation.ADD_VALUE);
      this.addHeldAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, CASTING, MAGIC_CONFIG.castingRange, Operation.ADD_VALUE);
      this.addHeldAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, CASTING, speed, Operation.ADD_MULTIPLIED_TOTAL);
      this.addHeldAttributeModifier(ManasCoreAttributes.LAVA_SPEED_MULTIPLIER, CASTING, speed, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Nullable
   public ResourceLocation getSkillIcon() {
      ResourceLocation id = this.getRegistryName();
      return id == null
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/temp_textures/item/confused_rimuru.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/magic/" + this.getType().getNamespace() + "/" + id.getPath().replace('/', '.') + ".png");
   }

   @Override
   public DamageSource createSource(ManasSkillInstance instance, LivingEntity attacker, ResourceKey<DamageType> type, int mode) {
      return super.createSource(instance, attacker, type, mode).tensura$setMagicType(this.getType());
   }

   public boolean isInstantCast(ManasSkillInstance instance, LivingEntity entity) {
      return MagicUtils.hasChantAnnulment(entity) && instance.isMastered(entity);
   }

   public int getDefaultCastTime() {
      return 40;
   }

   public int getMasteryCastTime() {
      return this.getDefaultCastTime();
   }

   public int getCastingTime(ManasSkillInstance instance, LivingEntity entity) {
      return this.getCastingTime(instance, entity, instance.isMastered(entity));
   }

   public int getCastingTime(ManasSkillInstance instance, LivingEntity entity, boolean mastered) {
      return this.getCastingTime(instance, entity, mastered ? this.getMasteryCastTime() : this.getDefaultCastTime());
   }

   public int getCastingTime(ManasSkillInstance instance, LivingEntity entity, int time) {
      return this.getCastingTime(instance, entity, time, true);
   }

   public int getCastingTime(ManasSkillInstance instance, LivingEntity entity, int time, boolean chantAnnulment) {
      if (chantAnnulment && this.isInstantCast(instance, entity)) {
         return 1;
      }

      int castTime = instance.getRemoveTime() == -3 ? (int)(time * MAGIC_CONFIG.unlearntCastMultiplier) : time;
      return MagicUtils.getChantTime(entity, castTime);
   }

   public ResourceLocation getCastingResourceLocation() {
      ResourceLocation cached = this.cachedCastingLocation;
      if (cached == null) {
         cached = ResourceLocation.fromNamespaceAndPath("tensura", this.getRegistryName().getPath() + "_casting");
         this.cachedCastingLocation = cached;
      }

      return cached;
   }

   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (this.getCastingTime(instance, entity) > 1) {
         if (!this.attributeModifiers.isEmpty() && !this.isCastingBlocked(instance, entity)) {
            AttributeMap attributeMap = entity.getAttributes();

            for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
               AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
               if (attributeInstance != null) {
                  attributeInstance.removeModifier(entry.getValue().id());
                  double amount = entry.getValue().amount() * instance.getAttributeModifierAmplifier(entity, entry.getKey(), entry.getValue(), mode);
                  AttributeModifier modifier = new AttributeModifier(this.getCastingResourceLocation(), amount, entry.getValue().operation());
                  attributeInstance.addOrUpdateTransientModifier(modifier);
               }
            }
         }
      }
   }

   public void removeAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!this.attributeModifiers.isEmpty() && !this.isCastingBlocked(instance, entity)) {
         AttributeMap map = entity.getAttributes();
         List<AttributeInstance> dirtyInstances = null;

         for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = map.getInstance(entry.getKey());
            if (attributeInstance != null) {
               attributeInstance.removeModifier(this.getCastingResourceLocation());
               if (dirtyInstances == null) {
                  dirtyInstances = new ArrayList<>();
               }

               dirtyInstances.add(attributeInstance);
            }
         }

         if (dirtyInstances != null && entity instanceof ServerPlayer player) {
            ClientboundUpdateAttributesPacket packet = new ClientboundUpdateAttributesPacket(player.getId(), dirtyInstances);
            player.connection.send(packet);
         }

         CompoundTag tag = instance.getTag();
         if (tag != null) {
            tag.putInt("MagicCircleID", 0);
            instance.markDirty();
         }
      }
   }

   public boolean isCastingBlocked(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      if (attributeInstance == null) {
         return false;
      }

      if (attributeInstance.hasModifier(this.getCastingResourceLocation())) {
         return false;
      }

      for (AttributeModifier modifier : attributeInstance.getModifiers()) {
         ResourceLocation location = modifier.id();
         if (location.getNamespace().equals("tensura") && location.getPath().endsWith("_casting")) {
            return true;
         }
      }

      return false;
   }

   public static boolean isStaffCasting(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.is(TensuraSkillTags.MAGIC)) {
         return false;
      }

      ItemStack stack = entity.getUseItem();
      if (stack.isEmpty()) {
         return false;
      }

      List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
      return skills != null && !skills.isEmpty()
         ? Objects.equals(stack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL.get(), skills.getFirst()), instance.getSkillId())
         : false;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      if (TickingSkill.isTickingSkill(entity, this)) {
         return false;
      }

      AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      return attributeInstance == null ? false : attributeInstance.hasModifier(this.getCastingResourceLocation());
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      this.removeAttributeModifiers(instance, entity, 0);
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      }

      if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
         return false;
      }

      this.applyCastingVisual(instance, entity, heldTicks, mode);
      return true;
   }

   protected void applyCastingVisual(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (entity instanceof Player player) {
         int cast = this.getCastingTime(instance, player);
         this.applyCastingVisual(instance, player, heldTicks, mode, cast);
      }
   }

   protected void applyCastingVisual(ManasSkillInstance instance, Player player, int heldTicks, int mode, int castTime) {
      if (castTime > 0) {
         if (heldTicks % 4 == 0 || heldTicks >= castTime) {
            String max = SkillUtils.ROUND_DOUBLE.format(castTime / 20.0F);
            String sec = heldTicks >= castTime ? max : SkillUtils.ROUND_DOUBLE.format(heldTicks / 20.0F);
            player.displayClientMessage(
               Component.translatable("tensura.magic.cast_time.max", new Object[]{sec, max}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), true
            );
         }
      }
   }

   protected void renderRemainingTime(LivingEntity entity, int tick, int max) {
      if (entity instanceof Player player) {
         if (tick % 4 != 0 && tick < max) {
            return;
         }

         String maxSec = SkillUtils.ROUND_DOUBLE.format(max / 20.0F);
         String sec = tick >= max ? maxSec : SkillUtils.ROUND_DOUBLE.format(tick / 20.0F);
         player.displayClientMessage(
            Component.translatable("tensura.magic.cast_time.remaining", new Object[]{sec, maxSec}).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), true
         );
      }
   }

   @Override
   public boolean canActivateSkill(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!super.canActivateSkill(instance, entity, mode)) {
         return false;
      } else {
         return entity.getAirSupply() >= entity.getMaxAirSupply() && !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SILENCE))
            ? true
            : this.isInstantCast(instance, entity);
      }
   }

   @Override
   public float getLearningCostMultiplier(int mode) {
      return MAGIC_CONFIG.learningCostMultiplier;
   }

   @Override
   public boolean isOutOfEnergy(LivingEntity entity, ManasSkillInstance instance, double apCost, double mpCost) {
      if ((!(mpCost <= 0.0) || !(apCost <= 0.0)) && !entity.hasInfiniteMaterials()) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (instance.getRemoveTime() == -3) {
            apCost *= MAGIC_CONFIG.unlearntCostMultiplier;
            mpCost *= MAGIC_CONFIG.unlearntCostMultiplier;
         }

         boolean enoughAP = apCost <= 0.0;
         if (!enoughAP) {
            if (existence.getAura() - apCost >= 0.0) {
               enoughAP = true;
            } else if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.lack_aura").withStyle(ChatFormatting.RED), true);
            }
         }

         boolean enoughMP = mpCost <= 0.0;
         if (!enoughMP) {
            mpCost *= entity.getAttributeValue(TensuraAttributes.MAGIC_COST_MULTIPLIER);
            ItemStack stack = entity.getUseItem();
            if (!stack.isEmpty()
               && instance.getMastery() >= 0.0
               && stack.is(TensuraItemTags.SPELL_CAST_WEAPONS)
               && stack.has((DataComponentType)TensuraDataComponents.EP_DURABILITY.get())) {
               double EP = (Double)stack.getOrDefault((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), 0.0);
               double castCost = Math.min(EP, mpCost);
               double newCost = mpCost - castCost;
               if (newCost <= 0.0) {
                  enoughMP = true;
                  stack.set((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), EP - castCost);
                  mpCost = 0.0;
               } else if (existence.getMagicule() - newCost >= 0.0) {
                  enoughMP = true;
                  stack.set((DataComponentType)TensuraDataComponents.EP_DURABILITY.get(), EP - castCost);
                  mpCost = newCost;
               } else if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("tensura.skill.lack_magicule").withStyle(ChatFormatting.RED), true);
               }
            } else if (existence.getMagicule() - mpCost >= 0.0) {
               enoughMP = true;
            } else if (entity instanceof Player player) {
               player.displayClientMessage(Component.translatable("tensura.skill.lack_magicule").withStyle(ChatFormatting.RED), true);
            }
         }

         if (enoughAP && enoughMP) {
            existence.setAura(existence.getAura() - apCost);
            existence.setMagicule(existence.getMagicule() - mpCost);
            existence.markDirty();
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void addLearningStatistic(ManasSkillInstance instance, ServerPlayer player) {
      if (!instance.isTemporarySkill() && !instance.isSubInstance() && !(instance.getMastery() < 0.0)) {
         player.awardStat(TensuraStats.MAGIC_LEARNT);
         ((AbilityTrigger)TensuraCriteriaTriggers.MAGIC_LEARNT.get()).trigger(player, this);
      }
   }

   @Override
   public void addMasteryStatistic(ServerPlayer player) {
      player.awardStat(TensuraStats.MAGIC_MASTERED);
      ((AbilityTrigger)TensuraCriteriaTriggers.MAGIC_MASTERED.get()).trigger(player, this);
   }

   @Generated
   public Magic.MagicType getType() {
      return this.type;
   }

   public enum MagicType implements StringRepresentable {
      ASPECTUAL("aspectual", ChatFormatting.GREEN),
      SPIRITUAL("spiritual", ChatFormatting.DARK_PURPLE),
      SUMMONING("summoning", ChatFormatting.BLUE),
      MISC("misc", ChatFormatting.RED);

      private final String namespace;
      private final ChatFormatting chatFormatting;
      public static final Codec<Magic.MagicType> CODEC = StringRepresentable.fromEnum(Magic.MagicType::values);

      public MutableComponent getName() {
         return Component.translatable("tensura.magic.type." + this.namespace).withStyle(this.chatFormatting);
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
      MagicType(final String namespace, final ChatFormatting chatFormatting) {
         this.namespace = namespace;
         this.chatFormatting = chatFormatting;
      }
   }
}
