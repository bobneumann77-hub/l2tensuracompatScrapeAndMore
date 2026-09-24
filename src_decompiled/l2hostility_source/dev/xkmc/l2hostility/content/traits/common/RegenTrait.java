package dev.xkmc.l2hostility.content.traits.common;

import dev.xkmc.l2complements.init.registrate.LCEffects;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.neoforged.neoforge.common.CommonHooks;

public class RegenTrait extends MobTrait {
   public RegenTrait(ChatFormatting style) {
      super(style);
   }

   @Override
   public void tick(LivingEntity mob, int level) {
      if (!mob.level().isClientSide()) {
         if (mob.tickCount % 20 == 0) {
            mob.heal((float)(mob.getMaxHealth() * (Double)LHConfig.SERVER.regen.get() * level));
         }
      }
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc",
               new Object[]{
                  this.mapLevel(
                     access,
                     i -> Component.literal((int)Math.round((Double)LHConfig.SERVER.regen.get() * 100.0 * i.intValue()) + "").withStyle(ChatFormatting.AQUA)
                  )
               }
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }

   @Override
   public boolean allow(LivingEntity le, int difficulty, int maxModLv) {
      return this.validTarget(le) && super.allow(le, difficulty, maxModLv);
   }

   public boolean validTarget(LivingEntity le) {
      return le instanceof EnderDragon ? false : CommonHooks.canMobEffectBeApplied(le, new MobEffectInstance(LCEffects.CURSE, 100));
   }
}
