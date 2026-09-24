package dev.xkmc.l2hostility.content.traits.legendary;

import dev.xkmc.l2hostility.content.item.curio.misc.Abrahadabra;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHEnchantments;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;

public abstract class PushPullTrait extends LegendaryTrait {
   public PushPullTrait(ChatFormatting style) {
      super(style);
   }

   protected abstract int getRange();

   protected abstract double getStrength(double var1);

   @Override
   public void tick(LivingEntity mob, int level) {
      int r = this.getRange();
      List<? extends LivingEntity> list;
      if (mob.level().isClientSide()) {
         list = mob.level()
            .getEntities(
               EntityTypeTest.forClass(Player.class),
               mob.getBoundingBox().inflate(r),
               ex -> ex.isLocalPlayer() && !ex.getAbilities().instabuild && !ex.isSpectator()
            );
      } else {
         list = mob.level()
            .getEntities(
               EntityTypeTest.forClass(LivingEntity.class),
               mob.getBoundingBox().inflate(r),
               ex -> ex instanceof Player pl && !pl.getAbilities().instabuild && !ex.isSpectator() || ex instanceof Mob m && m.getTarget() == mob
            );
      }

      for (LivingEntity e : list) {
         double dist = mob.distanceTo(e) / r;
         if (dist > 1.0) {
            return;
         }

         if (!((Abrahadabra)LHItems.ABRAHADABRA.get()).isOn(e)) {
            double strength = this.getStrength(dist);
            int lv = 0;

            for (ItemStack armor : e.getArmorSlots()) {
               lv += armor.getEnchantmentLevel(LHEnchantments.INSULATOR.holder());
            }

            if (lv > 0) {
               strength *= Math.pow((Double)LHConfig.SERVER.insulatorFactor.get(), lv);
            }

            Vec3 vec = e.position().subtract(mob.position()).normalize().scale(strength);
            e.push(vec.x, vec.y, vec.z);
         }
      }
   }
}
