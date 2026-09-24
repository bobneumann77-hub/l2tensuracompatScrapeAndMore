package io.github.manasmods.tensura.ability.magic.spiritual.wind;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;

public class WindMagic extends SpiritualMagic {
   private static final SpiritualMagicConfig.Wind CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Wind;

   public WindMagic() {
      super(Element.WIND, SpiritualMagic.SpiritLevel.LESSER);
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

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            MagicCircleVariant.WIND,
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

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            entity.resetFallDistance();
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            ExplosionDamageCalculator selfCalculator = new SimpleExplosionDamageCalculator(
               true, false, Optional.of(1.25F), BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
            );
            Entity charge = entity instanceof Player player ? new WindCharge(player, entity.level(), entity.getX(), entity.getY(), entity.getZ()) : null;
            entity.level()
               .explode(
                  charge,
                  null,
                  selfCalculator,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  CONFIG.power,
                  false,
                  ExplosionInteraction.TRIGGER,
                  ParticleTypes.GUST_EMITTER_SMALL,
                  ParticleTypes.GUST_EMITTER_LARGE,
                  SoundEvents.WIND_CHARGE_BURST
               );
         }
      }
   }
}
