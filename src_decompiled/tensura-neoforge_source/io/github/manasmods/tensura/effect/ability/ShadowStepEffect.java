package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ShadowStepEffect extends TensuraMobEffect {
   protected static final ResourceLocation SHADOW = ResourceLocation.fromNamespaceAndPath("tensura", "shadow_motion");

   public ShadowStepEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(0, 0, 0).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SHADOW, 0.1F, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.STEP_HEIGHT, SHADOW, 1.0, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.PRESENCE_CONCEALMENT, SHADOW, 5.0, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.DARK_VISION, SHADOW, 0.1, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      SubordinateHelper.presenceConcealing(entity, 40.0);
      if (entity instanceof Player player && !player.isCreative() && !player.isSpectator() && player.getAbilities().flying) {
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      }

      Level pLevel = entity.level();
      RandomSource randomsource = pLevel.random;
      BlockPos pPos = entity.getOnPos();
      MutableBlockPos blockpos = new MutableBlockPos(pPos.getX(), pPos.getY() + 1, pPos.getZ());
      if (!pLevel.getBlockState(blockpos).isSolidRender(pLevel, blockpos)) {
         double d1 = randomsource.nextFloat();
         double d2 = 1.0625;
         double d3 = randomsource.nextFloat();
         pLevel.addParticle(ParticleTypes.SQUID_INK, pPos.getX() + d1, pPos.getY() + d2, pPos.getZ() + d3, 0.0, 0.0, 0.0);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 5 == 0;
   }
}
