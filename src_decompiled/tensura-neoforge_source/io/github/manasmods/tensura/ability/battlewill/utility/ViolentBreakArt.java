package io.github.manasmods.tensura.ability.battlewill.utility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ViolentBreakArt extends Battlewill {
   public static ImmutableList<Holder<MobEffect>> harmfulEffects;
   private static final BattlewillConfig.ViolentBreak CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).ViolentBreak;

   public ViolentBreakArt() {
      harmfulEffects = ImmutableList.copyOf(
         CONFIG.effectToRemove
            .stream()
            .<ResourceLocation>map(ResourceLocation::parse)
            .map(location -> (Reference)BuiltInRegistries.MOB_EFFECT.getHolder(location).orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList())
      );
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks >= CONFIG.holdTime) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         instance.addMasteryPoint(entity);
         if (!harmfulEffects.isEmpty()) {
            UnmodifiableIterator var7 = harmfulEffects.iterator();

            while (var7.hasNext()) {
               Holder<MobEffect> effect = (Holder<MobEffect>)var7.next();
               TensuraMobEffect.removeLevelsOfEffect(entity, effect, 1);
            }
         }

         int level = CONFIG.strengthenLevel;
         entity.addEffect(
            new MobEffectInstance(
               TensuraMobEffects.getReference(TensuraMobEffects.STRENGTHEN),
               CONFIG.strengthenTime,
               instance.isMastered(entity) ? level * 2 - 1 : level - 1,
               true,
               false,
               true
            )
         );
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F
            );
         return false;
      } else {
         double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
         TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getGoldAura(1.0F, (float)size, -0.3F), 3, 0.03);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         return true;
      }
   }
}
