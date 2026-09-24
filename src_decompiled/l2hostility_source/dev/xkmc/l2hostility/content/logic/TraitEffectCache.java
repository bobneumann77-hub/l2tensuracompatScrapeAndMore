package dev.xkmc.l2hostility.content.logic;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2library.util.GenericItemStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public class TraitEffectCache {
   public final LivingEntity target;
   @Nullable
   private List<GenericItemStack<CurseCurioItem>> curios;
   @Nullable
   private List<Mob> reflectedTargets;

   public TraitEffectCache(LivingEntity target) {
      this.target = target;
   }

   public boolean reflectTrait(MobTrait trait) {
      for (GenericItemStack<CurseCurioItem> e : this.buildCurio()) {
         if (((CurseCurioItem)e.item()).reflectTrait(trait)) {
            return true;
         }
      }

      return false;
   }

   private List<GenericItemStack<CurseCurioItem>> buildCurio() {
      if (this.curios == null) {
         this.curios = CurseCurioItem.getFromPlayer(this.target);
      }

      return this.curios;
   }

   public List<Mob> getTargets() {
      if (this.reflectedTargets == null) {
         this.reflectedTargets = new ArrayList<>();
         int radius = (Integer)LHConfig.SERVER.ringOfReflectionRadius.get();

         for (Entity e : this.target.level().getEntities(this.target, this.target.getBoundingBox().inflate(radius))) {
            if (e instanceof Mob mob && ((GeneralCapabilityHolder)LHMiscs.MOB.type()).isProper(mob) && !(mob.distanceTo(this.target) > radius)) {
               this.reflectedTargets.add(mob);
            }
         }
      }

      return this.reflectedTargets;
   }
}
