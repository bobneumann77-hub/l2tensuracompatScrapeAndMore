package io.github.manasmods.tensura.effect.ability;

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

public class SeveranceBladeEffect extends TensuraMobEffect {
   private static final ResourceLocation SEVERANCE_BLADE = ResourceLocation.fromNamespaceAndPath("tensura", "severance_blade");

   public SeveranceBladeEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(75, 222, 148).getRGB());
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, SEVERANCE_BLADE, 10.0, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION, SEVERANCE_BLADE, 1.0, Operation.ADD_VALUE);
   }

   public void addAttributeModifiers(AttributeMap attributeMap, int i) {
      for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
         if (attributeInstance != null && (!entry.getKey().equals(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION) || i >= 4)) {
            attributeInstance.removeModifier(entry.getValue().id());
            attributeInstance.addPermanentModifier(entry.getValue().create(i));
         }
      }
   }
}
