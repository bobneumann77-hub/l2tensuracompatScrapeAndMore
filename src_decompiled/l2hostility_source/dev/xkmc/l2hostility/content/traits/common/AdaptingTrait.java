package dev.xkmc.l2hostility.content.traits.common;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Defence;
import dev.xkmc.l2hostility.content.capability.mob.CapStorageData;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;

public class AdaptingTrait extends MobTrait {
   public AdaptingTrait(ChatFormatting style) {
      super(style);
   }

   @Override
   public void onDamaged(int level, LivingEntity entity, Defence event) {
      if (!event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !event.getSource().is(DamageTypeTags.BYPASSES_EFFECTS)) {
         MobTraitCap cap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(entity);
         AdaptingTrait.Data data = cap.getOrCreateData(this.getRegistryName(), AdaptingTrait.Data::new);
         String id = event.getSource().getMsgId();
         if (data.memory.contains(id)) {
            data.memory.remove(id);
            data.memory.addFirst(id);
            int val = data.adaption.compute(id, (k, oldx) -> oldx == null ? 1 : oldx + 1);
            double factor = Math.pow((Double)LHConfig.SERVER.adaptFactor.get(), val - 1);
            event.addDealtModifier(DamageModifier.multTotal((float)factor, this.getRegistryName()));
         } else {
            data.memory.addFirst(id);
            data.adaption.put(id, 1);
            if (data.memory.size() > level) {
               String old = data.memory.removeLast();
               data.adaption.remove(old);
            }
         }
      }
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc",
               new Object[]{
                  Component.literal((int)Math.round(100.0 * (1.0 - (Double)LHConfig.SERVER.adaptFactor.get())) + "").withStyle(ChatFormatting.AQUA),
                  this.mapLevel(access, i -> Component.literal(i + "").withStyle(ChatFormatting.AQUA))
               }
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }

   @SerialClass
   public static class Data extends CapStorageData {
      @SerialField
      public final ArrayList<String> memory = new ArrayList<>();
      @SerialField
      public final HashMap<String, Integer> adaption = new HashMap<>();
   }
}
