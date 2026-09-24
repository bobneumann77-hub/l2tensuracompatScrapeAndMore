package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.registry.item.misc.TensuraArmorMaterials;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;

public class DragonSkinSkill extends Skill {
   private static final IntrinsicSkillConfig.DragonSkin CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).DragonSkin;
   protected static final ResourceLocation DRAGON_ARMOR = ResourceLocation.fromNamespaceAndPath("tensura", "dragon_skin");

   public DragonSkinSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return !this.hasArmor(entity);
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
   }

   private boolean hasArmor(LivingEntity entity) {
      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor == null) {
         return false;
      }

      boolean hasArmor = false;

      for (Type type : Type.values()) {
         if (!type.equals(Type.BODY) && armor.hasModifier(ResourceLocation.withDefaultNamespace("armor." + type.getName()))) {
            hasArmor = true;
            break;
         }
      }

      return hasArmor;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null && armor.getModifier(DRAGON_ARMOR) == null) {
         armor.addOrReplacePermanentModifier(new AttributeModifier(DRAGON_ARMOR, this.calculateArmor(entity), Operation.ADD_VALUE));
      }

      AttributeInstance toughness = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
      if (toughness != null && toughness.getModifier(DRAGON_ARMOR) == null) {
         toughness.addOrReplacePermanentModifier(new AttributeModifier(DRAGON_ARMOR, this.calculateToughness(entity), Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         armor.removeModifier(DRAGON_ARMOR);
      }

      AttributeInstance toughness = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
      if (toughness != null) {
         toughness.removeModifier(DRAGON_ARMOR);
      }
   }

   protected int calculateArmor(LivingEntity entity) {
      int armor = 0;

      for (Type type : Type.values()) {
         if (!type.equals(Type.BODY)) {
            Map<Type, Integer> map = ((ArmorMaterial)this.getMaterial(entity).value()).defense();
            armor += map.get(type);
         }
      }

      return armor;
   }

   protected float calculateToughness(LivingEntity entity) {
      return ((ArmorMaterial)this.getMaterial(entity).value()).toughness() * 4.0F;
   }

   protected Holder<ArmorMaterial> getMaterial(LivingEntity entity) {
      double EP = EnergyHelper.getMaxEP(entity);
      if (EP >= CONFIG.hihiirokaneEP) {
         return TensuraArmorMaterials.HIHIIROKANE;
      } else if (EP >= CONFIG.adamantiteEP) {
         return TensuraArmorMaterials.ADAMANTITE;
      } else {
         return EP >= CONFIG.magisteelEP ? TensuraArmorMaterials.PURE_MAGISTEEL : TensuraArmorMaterials.HIGH_MAGISTEEL;
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (this.hasArmor(entity)) {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.lack_requirement.toggled_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         instance.setToggled(false);
         instance.onToggleOff(entity);
         instance.markDirty();
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }
   }
}
