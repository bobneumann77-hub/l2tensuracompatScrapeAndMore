package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CorrosionSkill extends Skill {
   private static final CommonSkillConfig.Corrosion CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).Corrosion;

   public CorrosionSkill() {
      super(Skill.SkillType.COMMON);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (entity instanceof ServerPlayer player) {
         if (player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)MonsterEntityTypes.TEMPEST_SERPENT.get())) >= CONFIG.serpentAcquirement) {
            return true;
         } else if (player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)MonsterEntityTypes.ORC_LORD.get())) >= CONFIG.orcAcquirement) {
            return true;
         } else {
            return player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType)MonsterEntityTypes.ORC_DISASTER.get())) >= CONFIG.orcAcquirement
               ? true
               : player.getStats().getValue(Stats.ITEM_USED.get(Items.ROTTEN_FLESH)) >= CONFIG.rottenFleshAcquirement;
         }
      } else {
         return false;
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      ItemStack itemStack = entity.getMainHandItem();
      if (!itemStack.is(Items.ROTTEN_FLESH)) {
         if (itemStack.is(ItemTags.MEAT)) {
            FoodProperties foodProperties = (FoodProperties)itemStack.getComponents().get(DataComponents.FOOD);
            if (foodProperties != null && foodProperties.effects().isEmpty()) {
               itemStack.shrink(1);
               instance.addMasteryPoint(entity);
               if (entity instanceof Player player) {
                  ItemStack stack = new ItemStack(Items.ROTTEN_FLESH);
                  if (!player.addItem(stack)) {
                     player.drop(stack, false);
                  }
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            }
         }
      }
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance) && !instance.isToggled()) {
         return true;
      }

      if (source.getDirectEntity() != attacker) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      Holder<MobEffect> corrosion = instance.isMastered(attacker) ? TensuraMobEffects.getReference(TensuraMobEffects.CORROSION) : MobEffects.WITHER;
      TensuraMobEffect.addEffect(target, corrosion, CONFIG.corrosionDuration, CONFIG.corrosionLevel - 1, true, false, true, attacker.getUUID(), this, 0);
      attacker.level()
         .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      ((ServerLevel)attacker.level())
         .sendParticles(
            TensuraParticleUtils.getAcidBubble(), target.getX(), target.getY() + target.getBbHeight() / 2.0, target.getZ(), 20, 0.08, 0.08, 0.08, 0.15
         );
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(attacker);
      }

      tag.putInt("activatedTimes", time + 1);
      return true;
   }
}
