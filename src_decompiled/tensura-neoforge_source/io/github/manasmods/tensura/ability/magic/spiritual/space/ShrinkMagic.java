package io.github.manasmods.tensura.ability.magic.spiritual.space;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ShrinkMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Shrink CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Shrink;
   protected static final ResourceLocation SHRINK = ResourceLocation.fromNamespaceAndPath("tensura", "shrink");
   private final List<Holder<Attribute>> attributeList = List.of(
      Attributes.ATTACK_DAMAGE, Attributes.KNOCKBACK_RESISTANCE, Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS
   );

   public ShrinkMagic() {
      super(Element.SPACE, SpiritualMagic.SpiritLevel.MEDIUM);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   private boolean isShrunk(LivingEntity entity) {
      AttributeInstance size = entity.getAttribute(Attributes.SCALE);
      return size == null ? false : size.getModifier(SHRINK) != null;
   }

   @Override
   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return super.canTick(instance, entity) ? true : this.isShrunk(entity);
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            entity.getBbWidth() * 2.0F,
            25,
            MagicCircleVariant.SPACE,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      return this.isShrunk(entity) ? true : super.onHeld(instance, entity, heldTicks, mode);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (this.isShrunk(entity)) {
         instance.onToggleOff(entity);
      } else if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            instance.onToggleOn(entity);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
               );
         }
      }
   }

   @Override
   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      super.onTick(instance, entity);
      if (this.isShrunk(entity)) {
         entity.addEffect(
            new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY),
               210,
               instance.isMastered(entity) ? CONFIG.fragilityLevelMastered - 1 : CONFIG.fragilityLevel - 1,
               true,
               false,
               true
            )
         );
         CompoundTag tag = instance.getOrCreateTag();
         int shrunkTime = tag.getInt("ShrunkTime");
         tag.putInt("ShrunkTime", shrunkTime + 5);
         if (shrunkTime + 5 >= (instance.isMastered(entity) ? CONFIG.shrinkDurationMastered : CONFIG.shrinkDuration)) {
            instance.onToggleOff(entity);
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  1.0F
               );
         }

         instance.markDirty();
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      float size = CONFIG.shrinkSize;
      AttributeHelper.addPermanentAttribute(entity, Attributes.SCALE, SHRINK, size - 1.0F, Operation.ADD_MULTIPLIED_TOTAL);

      for (Holder<Attribute> attribute : this.attributeList) {
         AttributeHelper.addPermanentAttribute(entity, attribute, SHRINK, size - 1.0F, Operation.ADD_MULTIPLIED_TOTAL);
      }

      instance.addMasteryPoint(entity);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttribute(entity, Attributes.SCALE, SHRINK);

      for (Holder<Attribute> attribute : this.attributeList) {
         AttributeHelper.removeAttribute(entity, attribute, SHRINK);
      }

      instance.getOrCreateTag().putInt("ShrunkTime", 0);
      instance.markDirty();
   }
}
