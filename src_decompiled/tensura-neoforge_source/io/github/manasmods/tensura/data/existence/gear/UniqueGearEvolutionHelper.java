package io.github.manasmods.tensura.data.existence.gear;

import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.registry.item.misc.TensuraArmorMaterials;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ArmorItem.Type;

public class UniqueGearEvolutionHelper {
   public static List<UniqueGearEvolutionData> getLowMagisteelArmors() {
      List<UniqueGearEvolutionData> evolutions = new ArrayList<>();
      evolutions.add(
         new UniqueGearEvolutionData(
            18000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.HIGH_MAGISTEEL.get(), (ArmorMaterial)TensuraArmorMaterials.LOW_MAGISTEEL.get())
         )
      );
      evolutions.add(
         new UniqueGearEvolutionData(
            52000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.PURE_MAGISTEEL.get(), (ArmorMaterial)TensuraArmorMaterials.HIGH_MAGISTEEL.get())
         )
      );
      evolutions.add(
         new UniqueGearEvolutionData(
            225000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.ADAMANTITE.get(), (ArmorMaterial)TensuraArmorMaterials.PURE_MAGISTEEL.get())
         )
      );
      evolutions.add(
         new UniqueGearEvolutionData(
            750000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.HIHIIROKANE.get(), (ArmorMaterial)TensuraArmorMaterials.ADAMANTITE.get())
         )
      );
      return evolutions;
   }

   public static List<UniqueGearEvolutionData> getLowMagisteelWeapons() {
      List<UniqueGearEvolutionData> evolutions = new ArrayList<>();
      evolutions.add(new UniqueGearEvolutionData(18000, getWeaponEvolutionData(TensuraToolTiers.HIGH_MAGISTEEL, TensuraToolTiers.LOW_MAGISTEEL)));
      evolutions.add(new UniqueGearEvolutionData(52000, getWeaponEvolutionData(TensuraToolTiers.PURE_MAGISTEEL, TensuraToolTiers.HIGH_MAGISTEEL)));
      evolutions.add(new UniqueGearEvolutionData(225000, getWeaponEvolutionData(TensuraToolTiers.ADAMANTITE, TensuraToolTiers.PURE_MAGISTEEL)));
      evolutions.add(new UniqueGearEvolutionData(750000, getWeaponEvolutionData(TensuraToolTiers.HIHIIROKANE, TensuraToolTiers.ADAMANTITE)));
      return evolutions;
   }

   public static List<UniqueGearEvolutionData> getHighMagisteelArmors() {
      List<UniqueGearEvolutionData> evolutions = new ArrayList<>();
      evolutions.add(
         new UniqueGearEvolutionData(
            52000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.PURE_MAGISTEEL.get(), (ArmorMaterial)TensuraArmorMaterials.HIGH_MAGISTEEL.get())
         )
      );
      evolutions.add(
         new UniqueGearEvolutionData(
            225000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.ADAMANTITE.get(), (ArmorMaterial)TensuraArmorMaterials.PURE_MAGISTEEL.get())
         )
      );
      evolutions.add(
         new UniqueGearEvolutionData(
            750000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.HIHIIROKANE.get(), (ArmorMaterial)TensuraArmorMaterials.ADAMANTITE.get())
         )
      );
      return evolutions;
   }

   public static List<UniqueGearEvolutionData> getHighMagisteelWeapons() {
      List<UniqueGearEvolutionData> evolutions = new ArrayList<>();
      evolutions.add(new UniqueGearEvolutionData(52000, getWeaponEvolutionData(TensuraToolTiers.PURE_MAGISTEEL, TensuraToolTiers.HIGH_MAGISTEEL)));
      evolutions.add(new UniqueGearEvolutionData(225000, getWeaponEvolutionData(TensuraToolTiers.ADAMANTITE, TensuraToolTiers.PURE_MAGISTEEL)));
      evolutions.add(new UniqueGearEvolutionData(750000, getWeaponEvolutionData(TensuraToolTiers.HIHIIROKANE, TensuraToolTiers.ADAMANTITE)));
      return evolutions;
   }

   public static List<UniqueGearEvolutionData> getPureMagisteelArmors() {
      List<UniqueGearEvolutionData> evolutions = new ArrayList<>();
      evolutions.add(
         new UniqueGearEvolutionData(
            225000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.ADAMANTITE.get(), (ArmorMaterial)TensuraArmorMaterials.PURE_MAGISTEEL.get())
         )
      );
      evolutions.add(
         new UniqueGearEvolutionData(
            750000, getArmorEvolutionData((ArmorMaterial)TensuraArmorMaterials.HIHIIROKANE.get(), (ArmorMaterial)TensuraArmorMaterials.ADAMANTITE.get())
         )
      );
      return evolutions;
   }

   public static List<UniqueGearEvolutionData> getPureMagisteelWeapons() {
      List<UniqueGearEvolutionData> evolutions = new ArrayList<>();
      evolutions.add(new UniqueGearEvolutionData(225000, getWeaponEvolutionData(TensuraToolTiers.ADAMANTITE, TensuraToolTiers.PURE_MAGISTEEL)));
      evolutions.add(new UniqueGearEvolutionData(750000, getWeaponEvolutionData(TensuraToolTiers.HIHIIROKANE, TensuraToolTiers.ADAMANTITE)));
      return evolutions;
   }

   private static List<UniqueGearEvolutionData.Entry> getArmorEvolutionData(ArmorMaterial material, ArmorMaterial prevMaterial) {
      List<UniqueGearEvolutionData.Entry> list = new ArrayList<>();
      double toughness = material.toughness() - prevMaterial.toughness();
      list.add(
         new UniqueGearEvolutionData.Entry(
            Attributes.ARMOR, Math.max(material.getDefense(Type.HELMET) - prevMaterial.getDefense(Type.HELMET) - 1, 1), EquipmentSlotGroup.HEAD
         )
      );
      list.add(new UniqueGearEvolutionData.Entry(Attributes.ARMOR_TOUGHNESS, toughness, EquipmentSlotGroup.HEAD));
      list.add(
         new UniqueGearEvolutionData.Entry(
            Attributes.ARMOR, Math.max(material.getDefense(Type.CHESTPLATE) - prevMaterial.getDefense(Type.CHESTPLATE) - 1, 1), EquipmentSlotGroup.CHEST
         )
      );
      list.add(new UniqueGearEvolutionData.Entry(Attributes.ARMOR_TOUGHNESS, toughness, EquipmentSlotGroup.CHEST));
      list.add(
         new UniqueGearEvolutionData.Entry(
            Attributes.ARMOR, Math.max(material.getDefense(Type.LEGGINGS) - prevMaterial.getDefense(Type.LEGGINGS) - 1, 1), EquipmentSlotGroup.LEGS
         )
      );
      list.add(new UniqueGearEvolutionData.Entry(Attributes.ARMOR_TOUGHNESS, toughness, EquipmentSlotGroup.LEGS));
      list.add(
         new UniqueGearEvolutionData.Entry(
            Attributes.ARMOR, Math.max(material.getDefense(Type.BOOTS) - prevMaterial.getDefense(Type.BOOTS) - 1, 1), EquipmentSlotGroup.FEET
         )
      );
      list.add(new UniqueGearEvolutionData.Entry(Attributes.ARMOR_TOUGHNESS, toughness, EquipmentSlotGroup.FEET));
      return list;
   }

   private static List<UniqueGearEvolutionData.Entry> getWeaponEvolutionData(Tier tier, Tier prevTier) {
      List<UniqueGearEvolutionData.Entry> list = new ArrayList<>();
      list.add(
         new UniqueGearEvolutionData.Entry(
            Attributes.ATTACK_DAMAGE, tier.getAttackDamageBonus() - prevTier.getAttackDamageBonus() - 1.0F, EquipmentSlotGroup.MAINHAND
         )
      );
      return list;
   }
}
