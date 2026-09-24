package io.github.manasmods.tensura.entity.magic.beam;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.magic.spiritual.darkness.DarknessCannonMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.awt.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class DarknessCannonProjectile extends BeamProjectile {
   public DarknessCannonProjectile(EntityType<? extends DarknessCannonProjectile> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.beamColorAndSize.put(new Color(255, 0, 0, 200), 0.2F);
      this.beamColorAndSize.put(new Color(0, 0, 0, 100), 0.4F);
      this.beamColorAndSize.put(new Color(170, 9, 0, 30), 0.6F);
   }

   public DarknessCannonProjectile(Level levelIn, LivingEntity shooter) {
      this((EntityType<? extends DarknessCannonProjectile>)MiscEntityTypes.DARKNESS_CANNON.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.DARKNESS_ELEMENTAL;
   }

   @Override
   protected boolean dealDamage(Entity target) {
      if (this.damage <= 0.0F || target instanceof ItemEntity) {
         return false;
      }

      if (!super.dealDamage(target)) {
         return false;
      }

      TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.DARK_RED_LIGHTNING_SPARK.get());
      if (target instanceof LivingEntity living) {
         SpiritualMagicConfig.DarknessCannon CONFIG = DarknessCannonMagic.CONFIG;
         living.addEffect(new MobEffectInstance(MobEffects.WITHER, CONFIG.witherDuration, CONFIG.witherLevel - 1, false, false, false));
         living.addEffect(new MobEffectInstance(MobEffects.HUNGER, CONFIG.hungerDuration, CONFIG.hungerLevel - 1, false, false, false));
         int insanityLevel = 0;
         MobEffectInstance insanity = living.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.INSANITY));
         if (insanity != null) {
            insanityLevel = insanity.getAmplifier() + 1;
         }

         MobEffectInstance newInsanity = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.INSANITY), CONFIG.insanityDuration, insanityLevel, true, false, true
         );
         ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
         TensuraMobEffect.addEffect(living, newInsanity, this.getOwner(), skill, this.getMode());

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.getType().equals(Type.HAND)) {
               ItemStack slotStack = living.getItemBySlot(slot);
               slotStack.hurtAndBreak(CONFIG.durabilityBreak, living, slot);
            }
         }
      }

      return true;
   }

   @Override
   protected boolean canDestroyBlock(BlockPos pos) {
      BlockState state = this.level().getBlockState(pos);
      return state.is(TensuraBlockTags.SKILL_BREAK_EASY) || state.canBeReplaced() && !this.level().getFluidState(pos).isSource();
   }

   @Override
   public void hitParticles(double x, double y, double z) {
      if (this.tickCount % 3 == 0) {
         Vec3 end = new Vec3(x, y, z);
         TensuraParticleHelper.addParticlesAroundPos(
            this.level().random, this.level(), end, (ParticleOptions)TensuraParticleTypes.DARK_RED_LIGHTNING_SPARK.get(), this.getSize(), 3
         );
      }
   }

   @Override
   public void rayParticles(Vec3 pos, int i) {
      if (this.tickCount % this.random.nextInt(10, 16) == 0) {
         TensuraParticleHelper.addParticlesAroundPos(
            this.random, this.level(), pos, (ParticleOptions)TensuraParticleTypes.DARK_RED_LIGHTNING_SPARK.get(), this.getVisualSize(), 1
         );
      }
   }
}
