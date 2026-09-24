package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class BatsModeEffect extends TensuraMobEffect implements ITransformation {
   public BatsModeEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(0, 0, 0).getRGB());
      this.addAttributeModifier(Attributes.ARMOR, ResourceLocation.fromNamespaceAndPath("tensura", "bats"), -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ResourceLocation.fromNamespaceAndPath("tensura", "bats"), -1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.SCALE, ResourceLocation.fromNamespaceAndPath("tensura", "bats"), -0.5, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.PRESENCE_CONCEALMENT, ResourceLocation.fromNamespaceAndPath("tensura", "bats"), 1.0, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (!this.shouldRemoveBats(entity) && !this.failedToActivate(entity, TensuraMobEffects.getReference(TensuraMobEffects.BATS_MODE))) {
         TensuraParticleHelper.addParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.BATS_MODE.get());
         TensuraParticleHelper.addParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.BATS_MODE.get(), 1.5);
         return true;
      }

      if (entity instanceof Player player && !player.isSpectator() && !player.isCreative()) {
         player.getAbilities().mayfly = false;
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      }

      return false;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 5 == 0;
   }

   private boolean shouldRemoveBats(LivingEntity entity) {
      if (SkillUtils.shouldCancelJump(entity)) {
         return true;
      } else {
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
         if (optional.isEmpty()) {
            return false;
         } else {
            return optional.get().getRace() != TensuraRaces.VAMPIRE.get()
               ? false
               : entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE) && RaceUtils.isUnderSun(entity);
         }
      }
   }
}
