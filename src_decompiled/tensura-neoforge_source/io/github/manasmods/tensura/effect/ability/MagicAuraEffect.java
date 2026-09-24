package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributeUtils;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.extra.MagicAuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.enchantment.SlottingHelper;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.awt.Color;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class MagicAuraEffect extends TensuraMobEffect implements DamageAction {
   public static final ResourceLocation MAGIC_AURA = ResourceLocation.fromNamespaceAndPath("tensura", "magic_aura");

   public MagicAuraEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(229, 180, 9).getRGB());
      this.addAttributeModifier(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION, MAGIC_AURA, 1.0, Operation.ADD_VALUE);
   }

   @Override
   public boolean onPlayerAttack(Player attacker, Entity entity) {
      MobEffectInstance magicAura = attacker.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_AURA));
      if (magicAura == null) {
         return true;
      }

      ItemStack stack = attacker.getMainHandItem();
      if (SlottingHelper.getContentSize(stack) > 0) {
         return true;
      }

      DamageSource source = this.getAuraSource(attacker, magicAura);
      float amount = ManasCoreAttributeUtils.getAttackDamage(attacker);
      if (attacker.level() instanceof ServerLevel level) {
         amount = EnchantmentHelper.modifyDamage(level, attacker.getOffhandItem(), entity, source, amount);
      }

      entity.hurt(source, amount * MagicAuraSkill.CONFIG.auraMultiplier);
      entity.invulnerableTime = 0;
      return true;
   }

   private DamageSource getAuraSource(LivingEntity attacker, MobEffectInstance instance) {
      CompoundTag tag = instance.tensura$getOrCreateTag();
      int element = tag.getInt("element");

      ResourceKey<DamageType> type = switch (element) {
         case 1 -> TensuraDamageTypes.HOLY_DAMAGE;
         case 2 -> TensuraDamageTypes.EARTH_ELEMENTAL;
         case 3 -> TensuraDamageTypes.FIRE_ELEMENTAL;
         case 4 -> TensuraDamageTypes.SPACE_ELEMENTAL;
         case 5 -> TensuraDamageTypes.WATER_ELEMENTAL;
         case 6 -> TensuraDamageTypes.WIND_ELEMENTAL;
         default -> TensuraDamageTypes.MAGIC_GENERIC;
      };
      DamageSource source = TensuraDamageHelper.getAbilityDamageSource(type, attacker, instance.tensura$getSourceAbility());
      if (element == 1) {
         source = source.tensura$setElement(Element.HOLY);
      }

      return source;
   }
}
