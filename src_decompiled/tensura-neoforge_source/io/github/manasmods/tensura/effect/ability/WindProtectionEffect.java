package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.tensura.ability.magic.aspectual.wind.WindProtectionMagic;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.awt.Color;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect.AttributeTemplate;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class WindProtectionEffect extends TensuraMobEffect {
   public static ResourceLocation WIND_PROTECTION = ResourceLocation.fromNamespaceAndPath("tensura", "wind_protection");
   protected static ResourceLocation WIND_PROTECTION_UPGRADED = ResourceLocation.fromNamespaceAndPath("tensura", "wind_protection_upgraded");

   public WindProtectionEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(20, 224, 126, 255).getRGB());
      this.addAttributeModifier(Attributes.MOVEMENT_SPEED, WIND_PROTECTION, WindProtectionMagic.CONFIG.effectSpeed, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE, WIND_PROTECTION, WindProtectionMagic.CONFIG.effectDodge, Operation.ADD_VALUE);
      this.addAttributeModifier(
         Attributes.BURNING_TIME, WIND_PROTECTION_UPGRADED, WindProtectionMagic.CONFIG.effectBurn * -1.0F, Operation.ADD_MULTIPLIED_TOTAL
      );
      this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, WIND_PROTECTION_UPGRADED, WindProtectionMagic.CONFIG.effectKnockback, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.FLAME_BOOST, WIND_PROTECTION_UPGRADED, WindProtectionMagic.CONFIG.effectFlameBoost, Operation.ADD_VALUE);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
      for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
         if (entry.getValue().id().equals(WIND_PROTECTION)) {
            AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
            if (attributeInstance != null) {
               attributeInstance.removeModifier(entry.getValue().id());
               attributeInstance.addPermanentModifier(entry.getValue().create(i));
            }
         } else if (entry.getValue().id().equals(WIND_PROTECTION_UPGRADED)) {
            int level = i - 1;
            if (i >= 0) {
               AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
               if (attributeInstance != null) {
                  attributeInstance.removeModifier(entry.getValue().id());
                  attributeInstance.addPermanentModifier(entry.getValue().create(level));
               }
            }
         }
      }
   }
}
