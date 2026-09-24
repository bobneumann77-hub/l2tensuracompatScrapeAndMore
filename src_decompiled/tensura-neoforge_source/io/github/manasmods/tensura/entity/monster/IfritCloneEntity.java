package io.github.manasmods.tensura.entity.monster;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomHeldAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CustomRangeAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.InvalidateNeutralAttackTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.OrbitAttack;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;

public class IfritCloneEntity extends IfritEntity {
   public IfritCloneEntity(EntityType<? extends IfritEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 5.0)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(Attributes.MAX_HEALTH, 50.0)
         .add(Attributes.MOVEMENT_SPEED, 0.2F)
         .add(Attributes.FLYING_SPEED, 0.7F)
         .add(Attributes.FOLLOW_RANGE, 64.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.STEP_HEIGHT, 2.0)
         .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 0.5)
         .add(TensuraAttributes.PRESENCE_SENSE, 3.0)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 20.0)
         .add(TensuraAttributes.MAGICULE_REGENERATION_MULTIPLIER, 3.0);
   }

   public void copySkills(LivingEntity owner) {
      this.setCustomName(owner.getName());

      for (MobEffectInstance instance : owner.getActiveEffects()) {
         this.addEffect(instance);
      }

      for (ManasSkillInstance instance : List.copyOf(SkillAPI.getSkillsFrom(owner).getLearnedSkills())) {
         ManasSkillInstance skillInstance = TensuraSkillInstance.fromNBT(instance.toNBT());
         skillInstance.setToggled(true);
         SkillHelper.learnSkill(this, skillInstance);
      }
   }

   @Override
   public BrainActivityGroup<IfritEntity> getFightTasks() {
      return BrainActivityGroup.fightTasks(
         new Behavior[]{
            new InvalidateNeutralAttackTarget().invalidateIf(this::shouldStopTarget),
            new OrbitAttack()
               .requireInSight(false)
               .lookAtTargetWhileOrbiting(true)
               .orbitAttackInterval(entity -> 100 + entity.getRandom().nextInt(80))
               .orbitRadius((entity, target) -> 12.0)
               .orbitHeight((entity, target) -> 9.0)
               .canDoOrbitalAttack((entity, target) -> false)
               .onTick(entity -> {
                  entity.setFlying(true);
                  return true;
               }),
            new FirstApplicableBehaviour(
               new ExtendedBehaviour[]{
                  new CustomRangeAttack(15).maxAttackRadius(7.0F).performAttack((entity, target) -> {
                     entity.combust(this::shouldAttack);
                     entity.playSound((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 10.0F, 0.95F + entity.getRandom().nextFloat() * 0.1F);
                  }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.2).whenStarting(entity -> entity.triggerAnim("miscController", "burst")),
                  new CustomHeldAttack().minAttackRadius(0.0F).maxAttackRadius(40.0F).attackInterval(entity -> 60).onTick((entity, target, tick) -> {
                     if (tick >= 10 && tick <= 25) {
                        if (tick == 10) {
                           entity.setMagicID(0);
                        }

                        entity.flameOrb();
                     }

                     return tick < 40;
                  }).startCondition(entity -> entity.getRandom().nextFloat() <= 0.4).whenStarting(entity -> {
                     entity.setMagicID(1);
                     entity.triggerAnim("heldController", "fire_ball_massive");
                  }).whenStopping(entity -> entity.setMagicID(0)),
                  new CustomRangeAttack(10)
                     .maxAttackRadius(40.0F)
                     .performAttack(IfritEntity::shootFireBolt)
                     .startCondition(entity -> entity.getMagicID() == 0)
                     .whenStarting(entity -> {
                        entity.setMagicID(1);
                        entity.triggerAnim("miscController", entity.getRandom().nextBoolean() ? "fire_ball_right" : "fire_ball_left");
                     })
               }
            )
         }
      );
   }
}
