package dev.xkmc.l2hostility.content.traits.common;

import dev.xkmc.l2damagetracker.contents.attack.DamageData.Attack;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.OffenceMax;
import dev.xkmc.l2hostility.content.logic.TraitEffectCache;
import dev.xkmc.l2hostility.content.traits.base.SelfEffectTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class FieryTrait extends SelfEffectTrait {
   public FieryTrait() {
      super(MobEffects.FIRE_RESISTANCE);
   }

   @Override
   public void onHurtTargetMax(int level, LivingEntity attacker, OffenceMax cache, TraitEffectCache traitCache) {
      if (cache.getDamageOriginal() > 0.0F && cache.getSource().getDirectEntity() instanceof LivingEntity le) {
         le.setRemainingFireTicks((Integer)LHConfig.SERVER.fieryTime.get() * 20);
      }
   }

   @Override
   public boolean onAttackedByOthers(int level, LivingEntity entity, Attack event) {
      if (event.getSource().getDirectEntity() instanceof LivingEntity le) {
         le.setRemainingFireTicks((Integer)LHConfig.SERVER.fieryTime.get() * 20);
      }

      return event.getSource().is(DamageTypeTags.IS_FIRE);
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc", new Object[]{Component.literal(LHConfig.SERVER.fieryTime.get() + "").withStyle(ChatFormatting.AQUA)}
            )
            .withStyle(ChatFormatting.GRAY)
      );
      super.addDetail(access, list);
   }
}
