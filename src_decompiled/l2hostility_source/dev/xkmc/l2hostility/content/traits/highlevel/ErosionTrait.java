package dev.xkmc.l2hostility.content.traits.highlevel;

import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Offence;
import dev.xkmc.l2hostility.content.item.curio.misc.Abrahadabra;
import dev.xkmc.l2hostility.content.item.traits.DurabilityEater;
import dev.xkmc.l2hostility.content.logic.TraitEffectCache;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class ErosionTrait extends SlotIterateDamageTrait {
   public ErosionTrait(ChatFormatting format) {
      super(format);
   }

   @Override
   public void onHurtTarget(int level, LivingEntity attacker, Offence cache, TraitEffectCache traitCache) {
      if (!((Abrahadabra)LHItems.ABRAHADABRA.get()).isOn(cache.getTarget())) {
         int count = this.process(level, attacker, cache.getTarget());
         if (count < level) {
            cache.addHurtModifier(
               DamageModifier.multTotal(1.0F + (float)((Double)LHConfig.SERVER.erosionDamage.get() * level * (level - count)), this.getRegistryName())
            );
         }
      }
   }

   @Override
   protected void perform(LivingEntity target, EquipmentSlot slot) {
      DurabilityEater.erosion(target, slot);
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc",
               new Object[]{
                  this.mapLevel(access, i -> Component.literal(i + "").withStyle(ChatFormatting.AQUA)),
                  this.mapLevel(
                     access,
                     i -> Component.literal(Math.round((Double)LHConfig.SERVER.erosionDurability.get() * i.intValue() * 100.0) + "%")
                        .withStyle(ChatFormatting.AQUA)
                  ),
                  this.mapLevel(
                     access,
                     i -> Component.literal(Math.round((Double)LHConfig.SERVER.erosionDamage.get() * i.intValue() * 100.0) + "%")
                        .withStyle(ChatFormatting.AQUA)
                  )
               }
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
