package dev.xkmc.l2hostility.content.item.curio.misc;

import dev.xkmc.l2complements.init.registrate.LCEffects;
import dev.xkmc.l2core.base.effects.EffectUtil;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Offence;
import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.content.item.curio.core.EquipCurioItem;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class FlamingThorn extends CurseCurioItem implements EquipCurioItem {
   public FlamingThorn(Properties properties) {
      super(properties);
   }

   @Override
   public void onHurtTarget(ItemStack stack, LivingEntity user, Offence cache) {
      LivingEntity target = cache.getTarget();
      int size = target.getActiveEffectsMap().size();
      if (size != 0) {
         int time = (Integer)LHConfig.SERVER.flameThornTime.get();
         EffectUtil.addEffect(target, new MobEffectInstance(LCEffects.FLAME, time, size - 1), user);
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      list.add(LangData.ITEM_FLAME_THORN.get(Math.round(((Integer)LHConfig.SERVER.flameThornTime.get()).intValue() * 0.05F)).withStyle(ChatFormatting.GOLD));
   }
}
