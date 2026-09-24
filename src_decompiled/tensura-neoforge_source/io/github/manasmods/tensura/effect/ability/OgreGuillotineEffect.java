package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import java.awt.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class OgreGuillotineEffect extends TensuraMobEffect {
   private static final BattlewillConfig.OgreSwordGuillotine CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).OgreSwordGuillotine;
   public static final ResourceLocation GUILLOTINE = ResourceLocation.fromNamespaceAndPath("tensura", "guillotine");

   public OgreGuillotineEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(96, 65, 231).getRGB());
      this.addAttributeModifier(Attributes.ATTACK_SPEED, GUILLOTINE, CONFIG.attackSpeedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ATTACK_DAMAGE, GUILLOTINE, CONFIG.attackMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
      this.addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, GUILLOTINE, CONFIG.reachMultiplier - 1.0, Operation.ADD_MULTIPLIED_BASE);
   }
}
