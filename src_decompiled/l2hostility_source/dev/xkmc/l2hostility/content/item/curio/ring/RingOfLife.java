package dev.xkmc.l2hostility.content.item.curio.ring;

import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Defence;
import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class RingOfLife extends CurseCurioItem {
   public RingOfLife(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> list, TooltipFlag flag) {
      int perc = (int)Math.round((Double)LHConfig.SERVER.ringOfLifeMaxDamage.get() * 100.0);
      list.add(LangData.ITEM_RING_LIFE.get(perc).withStyle(ChatFormatting.GOLD));
   }

   @Override
   public void onDamage(ItemStack stack, LivingEntity user, Defence event) {
      boolean bypassInvul = event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY);
      boolean bypassMagic = event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS);
      if (!bypassInvul && !bypassMagic) {
         ResourceLocation rl = this.getID();
         float max = (float)(user.getMaxHealth() * (Double)LHConfig.SERVER.ringOfLifeMaxDamage.get());
         event.addDealtModifier(DamageModifier.nonlinearMiddle(213, dmg -> Math.min(dmg, max), rl));
      }
   }
}
