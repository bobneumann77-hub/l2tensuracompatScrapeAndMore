package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;

public class SeveranceCutterProjectile extends SpaceCutProjectile {
   public SeveranceCutterProjectile(EntityType<? extends SeveranceCutterProjectile> type, Level level) {
      super(type, level);
      this.setPiercingEntity(true);
      this.setPiercingBlock(true);
      this.setSize(3.0F);
   }

   public SeveranceCutterProjectile(Level worldIn, LivingEntity shooter) {
      this((EntityType<? extends SeveranceCutterProjectile>)ProjectileEntityTypes.SEVERANCE_CUTTER.get(), worldIn);
      this.setOwner(shooter);
      this.visible = true;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/slash/absolute_severance.png");
   }

   @Override
   public DamageSource getDamageSource(ResourceKey<DamageType> type, float costMultiplier) {
      DamageSource source = super.getDamageSource(type, costMultiplier);
      return this.getSkill() != null && this.getSkill().getSkill().equals(UniqueSkills.ABSOLUTE_SEVERANCE.get())
         ? source.tensura$setResistanceBypassLevel(1.0F).tensura$setDodgeBypass().tensura$setBarrierBypassLevel(1.0F)
         : source;
   }

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "controller",
            0,
            event -> {
               if (this.getAge() < 8) {
                  return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.absolute_severance.start"));
               } else {
                  return this.getLife() - this.getAge() < 2
                     ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.absolute_severance.end"))
                     : event.setAndContinue(RawAnimation.begin().thenLoop("animation.absolute_severance.loop"));
               }
            }
         )
      );
   }
}
