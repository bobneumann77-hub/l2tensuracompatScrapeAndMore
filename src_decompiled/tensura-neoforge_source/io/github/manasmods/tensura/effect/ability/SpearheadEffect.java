package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.tensura.ability.skill.unique.SpearheadSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.awt.Color;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;

public class SpearheadEffect extends TensuraMobEffect {
   protected static final ResourceLocation SPEAR = ResourceLocation.fromNamespaceAndPath("tensura", "spearhead");
   private static final Map<UUID, BlockHitResult> POV_CACHE = new ConcurrentHashMap<>();
   private static final Map<UUID, Long> POV_CACHE_TICK = new ConcurrentHashMap<>();

   public SpearheadEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(25, 93, 140).getRGB());
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, SPEAR, SpearheadSkill.CONFIG.allyAttack, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.ARMOR, SPEAR, SpearheadSkill.CONFIG.allyArmor, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEAR, SpearheadSkill.CONFIG.allySpeed, Operation.ADD_VALUE);
      this.addAttributeModifier(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, SPEAR, SpearheadSkill.CONFIG.allySwim, Operation.ADD_VALUE);
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.SPEARHEAD));
      if (instance == null) {
         return true;
      }

      if (entity instanceof PathfinderMob mob && mob.level() instanceof ServerLevel level) {
         IEffect effect = TensuraStorages.getEffectFrom(mob);
         if (!effect.isMeatShield()) {
            return true;
         }

         if (SubordinateHelper.isOrderedToStay(mob)) {
            return true;
         }

         LivingEntity owner = SubordinateHelper.getSubordinateOwner(mob);
         if (owner == null) {
            return true;
         }

         long gameTime = level.getGameTime();
         UUID ownerKey = owner.getUUID();
         Long lastTick = POV_CACHE_TICK.get(ownerKey);
         BlockHitResult result;
         if (lastTick != null && lastTick == gameTime) {
            result = POV_CACHE.get(ownerKey);
         } else {
            result = ObjectSelectionHelper.getPlayerPOVHitResult(level, owner, Fluid.NONE, 4.0);
            POV_CACHE.put(ownerKey, result);
            POV_CACHE_TICK.put(ownerKey, gameTime);
         }

         BlockPos pos = result.getBlockPos();
         mob.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.2);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 10 == 0;
   }

   @Override
   public void onAttributeRemoved(LivingEntity entity, MobEffectInstance instance) {
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      effect.setMeatShield(false);
      effect.markDirty();
   }
}
