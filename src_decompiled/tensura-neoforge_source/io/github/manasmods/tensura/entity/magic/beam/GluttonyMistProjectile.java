package io.github.manasmods.tensura.entity.magic.beam;

import io.github.manasmods.tensura.ability.skill.unique.GluttonySkill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class GluttonyMistProjectile extends PredatorMistProjectile {
   public GluttonyMistProjectile(EntityType<? extends GluttonyMistProjectile> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
   }

   public GluttonyMistProjectile(Level level, LivingEntity entity) {
      this((EntityType<? extends GluttonyMistProjectile>)MiscEntityTypes.GLUTTONY_MIST.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected void devourTarget(LivingEntity target, LivingEntity owner) {
      UniqueSkillConfig.Gluttony CONFIG = GluttonySkill.CONFIG;
      if (!target.getType().is(TensuraEntityTags.NO_ENERGY_DRAIN)) {
         EnergyHelper.drainEnergy(target, owner, CONFIG.predationEPDrain, false, EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL);
      }

      if (target.isAlive()) {
         if (target.getRandom().nextFloat() < CONFIG.predationSkillChance / 100.0F) {
            this.devourRandomSkill(target, owner, CONFIG.predationSkillNumber);
         }

         MobEffectInstance instance = new MobEffectInstance(
            TensuraMobEffects.getReference(TensuraMobEffects.CORROSION),
            CONFIG.predationCorrosionDuration,
            CONFIG.predationCorrosionLevel - 1,
            true,
            false,
            true
         );
         TensuraMobEffect.addEffect(target, instance, owner, this.getSkill() != null ? this.getSkill().getSkill() : null, this.getMode());
      } else {
         this.devourAllSkills(target, owner);
         this.devourEP(target, owner, CONFIG.predationEPSteal);
         if (owner instanceof Player player) {
            for (ItemEntity item : owner.level().getEntitiesOfClass(ItemEntity.class, AABB.ofSize(target.position(), 2.0, 2.0, 2.0))) {
               if (this.addItemToSpatialStorage(player, item.getItem())) {
                  item.discard();
               } else if (player.addItem(item.getItem())) {
                  item.discard();
               } else {
                  item.teleportTo(player.position().x(), player.position().y(), player.position().z());
               }
            }
         }
      }
   }
}
