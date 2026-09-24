package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributeUtils;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AuraSwordEffect extends TensuraMobEffect implements DamageAction {
   private static final BattlewillConfig.AuraSword CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).AuraSword;

   public AuraSwordEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(13, 238, 246).getRGB());
   }

   @Override
   public boolean onDamagingEntity(LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (TensuraDamageHelper.isPhysicalAttack(source) && source.getDirectEntity() == attacker) {
         amount.set((Float)amount.get() + ManasCoreAttributeUtils.getWeaponDamage(attacker, target, source) * CONFIG.attackMultiplier);
         return true;
      } else {
         return true;
      }
   }
}
