package dev.xkmc.l2hostility.content.traits.legendary;

import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Defence;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.l2hostility.init.data.LHConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.Tags.DamageTypes;

public class DementorTrait extends LegendaryTrait {
   public DementorTrait(ChatFormatting style) {
      super(style);
   }

   @Override
   public void onCreateSource(int level, LivingEntity attacker, CreateSourceEvent event) {
      if (event.getResult() == L2DamageTypes.MOB_ATTACK) {
         event.enable(DefaultDamageState.BYPASS_ARMOR);
      }
   }

   @Override
   public double modifyBonusDamage(DamageSource source, double factor, int lv) {
      return source.getMsgId().equals("mob") && source.is(DamageTypeTags.BYPASSES_ARMOR) ? (Double)LHConfig.SERVER.dementorDamageFactor.get() : 1.0;
   }

   @Override
   public void onDamaged(int level, LivingEntity entity, Defence event) {
      if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         && !event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS)
         && !event.getSource().is(DamageTypes.IS_MAGIC)) {
         double def = (Double)LHConfig.SERVER.dementorDamageReductionBase.get();
         event.addDealtModifier(
            DamageModifier.nonlinearPre(7436, val -> (float)(val < def ? val / def : Math.log(val) / Math.log(def)), this.getRegistryName())
         );
      }
   }
}
