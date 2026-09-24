package dev.xkmc.l2hostility.content.traits.highlevel;

import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class SlotIterateDamageTrait extends MobTrait {
   public SlotIterateDamageTrait(ChatFormatting format) {
      super(format);
   }

   @Override
   public void postHurtImpl(int level, LivingEntity attacker, LivingEntity target) {
      this.process(level, attacker, target);
   }

   protected int process(int level, LivingEntity attacker, LivingEntity target) {
      List<EquipmentSlot> list = new ArrayList<>();
      int other = 0;

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         ItemStack stack = target.getItemBySlot(slot);
         if (stack.isDamageableItem()) {
            list.add(slot);
         } else if (!stack.isEmpty()) {
            other++;
         }
      }

      int count = Math.min(level, list.size());

      for (int i = 0; i < count; i++) {
         int index = attacker.getRandom().nextInt(list.size());
         this.perform(target, list.remove(index));
      }

      return count + other;
   }

   protected abstract void perform(LivingEntity var1, EquipmentSlot var2);
}
