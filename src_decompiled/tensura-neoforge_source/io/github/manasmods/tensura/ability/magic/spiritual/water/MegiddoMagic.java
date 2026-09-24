package io.github.manasmods.tensura.ability.magic.spiritual.water;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.MegiddoBubbleEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class MegiddoMagic extends SpiritualMagic {
   public static final SpiritualMagicConfig.Megiddo CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Megiddo;

   public MegiddoMagic() {
      super(Element.WATER, SpiritualMagic.SpiritLevel.GREATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "megiddo.single";
         case 1 -> "megiddo.autonomous";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return mode == 1 ? CONFIG.magiculeCostAuto : CONFIG.magiculeCostSingle;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 0) {
         MegiddoBubbleEntity bubble = this.getMegiddoBubble(entity);
         if (bubble == null) {
            return;
         }

         if (entity.isShiftKeyDown()) {
            if (bubble.getLife() - bubble.getAge() > 50) {
               bubble.setRemoveIn(50);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            }

            return;
         }

         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.singleRange, false, true);
         if (target == null) {
            return;
         }

         if (!target.isAlive()) {
            return;
         }

         if (EnergyHelper.isOutOfEnergy(entity, instance, mode, 0.1F)) {
            return;
         }

         instance.setCoolDown(CONFIG.singleCooldown, mode);
         if (bubble.startNewBeam(target)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.MEGIDDO_SHOOT.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
               );
         } else {
            entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.location_time").withStyle(ChatFormatting.RED));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  0.5F,
                  1.0F
               );
         }
      }
   }

   @Override
   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!this.hasMegiddo(entity)) {
         super.addHeldAttributeModifiers(instance, entity, mode);
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            5.0F,
            25,
            MagicCircleVariant.WATER,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            new Vec3(0.0, mode == 1 && this.spawnMegiddoBelow(entity) ? -2.0 : 30.0, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      return this.hasMegiddo(entity) ? false : super.onHeld(instance, entity, heldTicks, mode);
   }

   private boolean spawnMegiddoBelow(LivingEntity entity) {
      if (entity.isShiftKeyDown()) {
         return false;
      } else if (entity.onGround()) {
         return false;
      } else {
         return entity instanceof Player player && !player.getAbilities().flying
            ? false
            : entity.level().clip(new ClipContext(entity.position(), entity.position().add(0.0, -10.0, 0.0), Block.OUTLINE, Fluid.NONE, entity)).getType()
               == Type.MISS;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (!this.hasMegiddo(entity)) {
         if (heldTicks >= this.getCastingTime(instance, entity)) {
            if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               entity.swing(InteractionHand.MAIN_HAND, true);
               if (!WaterMagic.isWaterEvaporated(entity, entity.level())) {
                  instance.addMasteryPoint(entity);
                  if (mode == 1) {
                     instance.setCoolDown(instance.isMastered(entity) ? 10 : 15, mode);
                     double yOffset = this.spawnMegiddoBelow(entity) ? -5.0 : 20.0;

                     for (int i = 0; i < 9; i++) {
                        int radius = i == 0 ? 3 : 2;
                        MegiddoBubbleEntity bubble = this.summonMegiddo(
                           entity, instance, instance.isMastered(entity) ? CONFIG.autoBeamMastered : CONFIG.autoBeam, radius, CONFIG.autoDuration, mode
                        );
                        bubble.setChainCharge(3);
                        Vec3 pos = entity.position().add(0.0, yOffset, 0.0);
                        if (radius == 2) {
                           Vec3 offset = new Vec3(0.0, 0.0, 15.0)
                              .yRot(0.0F)
                              .xRot(i * 45 * (float) (Math.PI / 180.0))
                              .zRot((float) (-Math.PI / 2))
                              .multiply(1.0, 0.85F, 1.0);
                           pos = pos.add(offset).add(0.0, (entity.getRandom().nextFloat() - 0.5) * 4.0, 0.0);
                        }

                        bubble.setPos(pos);
                        entity.level().addFreshEntity(bubble);
                     }
                  } else {
                     MegiddoBubbleEntity bubble = this.summonMegiddo(
                        entity, instance, instance.isMastered(entity) ? CONFIG.singleBeamMastered : CONFIG.singleBeam, 3.0F, CONFIG.singleDuration, mode
                     );
                     bubble.setFollowOwner(true);
                     bubble.setPos(entity.getX(), entity.getY() + 20.0, entity.getZ());
                     entity.level().addFreshEntity(bubble);
                  }
               }
            }
         }
      }
   }

   private MegiddoBubbleEntity summonMegiddo(LivingEntity entity, ManasSkillInstance instance, int charge, float radius, int life, int mode) {
      MegiddoBubbleEntity bubble = new MegiddoBubbleEntity(entity.level(), entity);
      bubble.setCharge(charge);
      bubble.setSize(radius);
      bubble.setLife(life);
      bubble.setSkill(entity, instance, this, mode);
      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      return bubble;
   }

   private boolean hasMegiddo(LivingEntity entity) {
      return this.getMegiddoBubble(entity) != null;
   }

   private MegiddoBubbleEntity getMegiddoBubble(LivingEntity owner) {
      AABB bubbleBox = new AABB(new BlockPos((int)owner.getX(), (int)(owner.getY() + 20.0), (int)owner.getZ()));

      for (MegiddoBubbleEntity bubble : owner.level().getEntitiesOfClass(MegiddoBubbleEntity.class, bubbleBox.inflate(5.0))) {
         if (bubble.getOwner() == owner) {
            return bubble;
         }
      }

      return null;
   }
}
