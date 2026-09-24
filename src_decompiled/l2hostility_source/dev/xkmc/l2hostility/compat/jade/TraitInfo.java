package dev.xkmc.l2hostility.compat.jade;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class TraitInfo implements IEntityComponentProvider {
   public static final ResourceLocation ID = L2Hostility.loc("mob");

   public void appendTooltip(ITooltip list, EntityAccessor entity, IPluginConfig config) {
      if (entity.getEntity() instanceof LivingEntity le) {
         Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(le);
         opt.ifPresent(cap -> list.addAll(cap.getTitle(true, true)));
      }
   }

   public ResourceLocation getUid() {
      return ID;
   }
}
