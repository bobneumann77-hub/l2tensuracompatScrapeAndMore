package dev.xkmc.l2hostility.content.traits.legendary;

import dev.xkmc.l2damagetracker.contents.attack.DamageData.Attack;
import dev.xkmc.l2hostility.init.data.LHConfig;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;

public class RepellingTrait extends PushPullTrait {
   public RepellingTrait(ChatFormatting style) {
      super(style);
   }

   @Override
   protected int getRange() {
      return (Integer)LHConfig.SERVER.repellRange.get();
   }

   @Override
   protected double getStrength(double dist) {
      return (1.0 - dist) * (Double)LHConfig.SERVER.repellStrength.get();
   }

   @Override
   public boolean onAttackedByOthers(int level, LivingEntity entity, Attack event) {
      return !event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
         && !event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS)
         && event.getSource().is(DamageTypeTags.IS_PROJECTILE);
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc", new Object[]{Component.literal(LHConfig.SERVER.repellRange.get() + "").withStyle(ChatFormatting.AQUA)}
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
