package io.github.manasmods.tensura.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SourceMessageDamageSource extends DamageSource {
   public SourceMessageDamageSource(Holder<DamageType> holder) {
      super(holder);
   }

   public SourceMessageDamageSource(Holder<DamageType> holder, @Nullable Entity entity) {
      super(holder, entity);
   }

   public SourceMessageDamageSource(Holder<DamageType> holder, Vec3 vec3) {
      super(holder, vec3);
   }

   public SourceMessageDamageSource(Holder<DamageType> holder, @Nullable Entity entity, @Nullable Entity entity2) {
      super(holder, entity, entity2);
   }

   @NotNull
   public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
      String customMessage = this.tensura$getCustomMessage();
      if (customMessage != null) {
         if (this.getEntity() == null && this.getDirectEntity() == null) {
            LivingEntity killer = livingEntity.getKillCredit();
            if (killer != null) {
               return Component.translatable(customMessage, new Object[]{livingEntity.getDisplayName(), killer.getDisplayName()});
            }
         }

         return Component.translatable(customMessage, new Object[]{livingEntity.getDisplayName()});
      } else {
         String message = "death.attack." + this.type().msgId();
         if (this.getEntity() == null && this.getDirectEntity() == null) {
            LivingEntity killer = livingEntity.getKillCredit();
            String string2 = message + ".player";
            return killer != null
               ? Component.translatable(string2, new Object[]{livingEntity.getDisplayName(), killer.getDisplayName()})
               : Component.translatable(message, new Object[]{livingEntity.getDisplayName()});
         }

         ItemStack stack;
         if (this.getEntity() instanceof LivingEntity attacker) {
            stack = attacker.getMainHandItem();
         } else {
            stack = ItemStack.EMPTY;
         }

         message = message + ".source";
         Component component = this.getEntity() == null ? this.getDirectEntity().getDisplayName() : this.getEntity().getDisplayName();
         return !stack.isEmpty() && stack.has(DataComponents.CUSTOM_NAME)
            ? Component.translatable(message + ".item", new Object[]{livingEntity.getDisplayName(), component, stack.getDisplayName()})
            : Component.translatable(message, new Object[]{livingEntity.getDisplayName(), component});
      }
   }
}
