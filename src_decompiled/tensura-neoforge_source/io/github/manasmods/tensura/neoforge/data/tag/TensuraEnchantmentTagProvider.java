package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TensuraEnchantmentTagProvider extends EnchantmentTagsProvider {
   public TensuraEnchantmentTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "tensura", existingFileHelper);
   }

   protected void addTags(@NotNull Provider provider) {
      this.tooltipOrder(
         provider,
         new ResourceKey[]{
            Enchantments.BINDING_CURSE,
            Enchantments.VANISHING_CURSE,
            Enchantments.RIPTIDE,
            Enchantments.CHANNELING,
            Enchantments.WIND_BURST,
            Enchantments.FROST_WALKER,
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.IMPALING,
            Enchantments.POWER,
            Enchantments.DENSITY,
            Enchantments.BREACH,
            Enchantments.PIERCING,
            Enchantments.SWEEPING_EDGE,
            Enchantments.MULTISHOT,
            Enchantments.FIRE_ASPECT,
            Enchantments.FLAME,
            Enchantments.KNOCKBACK,
            Enchantments.PUNCH,
            Enchantments.PROTECTION,
            Enchantments.BLAST_PROTECTION,
            Enchantments.FIRE_PROTECTION,
            Enchantments.PROJECTILE_PROTECTION,
            Enchantments.FEATHER_FALLING,
            Enchantments.FORTUNE,
            Enchantments.LOOTING,
            Enchantments.SILK_TOUCH,
            Enchantments.LUCK_OF_THE_SEA,
            Enchantments.EFFICIENCY,
            Enchantments.QUICK_CHARGE,
            Enchantments.LURE,
            Enchantments.RESPIRATION,
            Enchantments.AQUA_AFFINITY,
            Enchantments.SOUL_SPEED,
            Enchantments.SWIFT_SNEAK,
            Enchantments.DEPTH_STRIDER,
            Enchantments.THORNS,
            Enchantments.LOYALTY,
            Enchantments.UNBREAKING,
            Enchantments.INFINITY,
            Enchantments.MENDING,
            TensuraEnchantments.BARRIER_PIERCING,
            TensuraEnchantments.BREATHING_SUPPORT,
            TensuraEnchantments.CRUSHING,
            TensuraEnchantments.ENERGY_STEAL,
            TensuraEnchantments.ENERGY_PROTECTION,
            TensuraEnchantments.ELEMENTAL_BOOST,
            TensuraEnchantments.ELEMENTAL_RESISTANCE,
            TensuraEnchantments.HOLY_WEAPON,
            TensuraEnchantments.INTANGIBILITY,
            TensuraEnchantments.MAGIC_WEAPON,
            TensuraEnchantments.MAGIC_CAPACITY,
            TensuraEnchantments.MAGIC_PROTECTION,
            TensuraEnchantments.MAGICULE_ABSORPTION,
            TensuraEnchantments.SEVERANCE,
            TensuraEnchantments.SEVERANCE_PROTECTION,
            TensuraEnchantments.SPIRITUAL_PROTECTION,
            TensuraEnchantments.SLOTTING,
            TensuraEnchantments.SOUL_EATER,
            TensuraEnchantments.STURDY,
            TensuraEnchantments.SWIFT,
            TensuraEnchantments.ENERVATION,
            TensuraEnchantments.LETHARGY,
            TensuraEnchantments.SEALING,
            TensuraEnchantments.STAGNATION,
            TensuraEnchantments.RUINATION,
            TensuraEnchantments.VITALITY,
            TensuraEnchantments.VIGOR,
            TensuraEnchantments.TRANSCENDENCE,
            TensuraEnchantments.GROWTH,
            TensuraEnchantments.RESTORATION,
            TensuraEnchantments.DEAD_END_RAINBOW,
            TensuraEnchantments.HOLY_COAT,
            TensuraEnchantments.MAGIC_INTERFERENCE,
            TensuraEnchantments.TSUKUMOGAMI
         }
      );
      this.tag(TensuraTags.Enchantments.TSUKUMOGAMI).add(TensuraEnchantments.TSUKUMOGAMI);
      this.tag(TensuraTags.Enchantments.INHERITANCE_ENGRAVING)
         .add(
            new ResourceKey[]{
               TensuraEnchantments.DEAD_END_RAINBOW, TensuraEnchantments.HOLY_COAT, TensuraEnchantments.MAGIC_INTERFERENCE, TensuraEnchantments.TSUKUMOGAMI
            }
         );
      this.tag(TensuraTags.Enchantments.ENGRAVING)
         .addTag(TensuraTags.Enchantments.INHERITANCE_ENGRAVING)
         .add(
            new ResourceKey[]{
               TensuraEnchantments.BARRIER_PIERCING,
               TensuraEnchantments.BREATHING_SUPPORT,
               TensuraEnchantments.CRUSHING,
               TensuraEnchantments.ENERGY_STEAL,
               TensuraEnchantments.ENERGY_PROTECTION,
               TensuraEnchantments.ELEMENTAL_BOOST,
               TensuraEnchantments.ELEMENTAL_RESISTANCE,
               TensuraEnchantments.HOLY_WEAPON,
               TensuraEnchantments.INTANGIBILITY,
               TensuraEnchantments.MAGIC_WEAPON,
               TensuraEnchantments.MAGIC_CAPACITY,
               TensuraEnchantments.MAGIC_PROTECTION,
               TensuraEnchantments.MAGICULE_ABSORPTION,
               TensuraEnchantments.SEVERANCE,
               TensuraEnchantments.SEVERANCE_PROTECTION,
               TensuraEnchantments.SPIRITUAL_PROTECTION,
               TensuraEnchantments.SLOTTING,
               TensuraEnchantments.SOUL_EATER,
               TensuraEnchantments.STURDY,
               TensuraEnchantments.SWIFT,
               TensuraEnchantments.ENERVATION,
               TensuraEnchantments.LETHARGY,
               TensuraEnchantments.SEALING,
               TensuraEnchantments.STAGNATION,
               TensuraEnchantments.RUINATION,
               TensuraEnchantments.VITALITY,
               TensuraEnchantments.VIGOR,
               TensuraEnchantments.TRANSCENDENCE,
               TensuraEnchantments.GROWTH,
               TensuraEnchantments.RESTORATION
            }
         );
      this.tag(TensuraTags.Enchantments.ENGRAVING_EXCLUSIVE)
         .add(
            new ResourceKey[]{
               TensuraEnchantments.BARRIER_PIERCING,
               TensuraEnchantments.BREATHING_SUPPORT,
               TensuraEnchantments.CRUSHING,
               TensuraEnchantments.ENERGY_STEAL,
               TensuraEnchantments.ENERGY_PROTECTION,
               TensuraEnchantments.ELEMENTAL_BOOST,
               TensuraEnchantments.ELEMENTAL_RESISTANCE,
               TensuraEnchantments.HOLY_WEAPON,
               TensuraEnchantments.INTANGIBILITY,
               TensuraEnchantments.MAGIC_WEAPON,
               TensuraEnchantments.MAGIC_CAPACITY,
               TensuraEnchantments.MAGIC_PROTECTION,
               TensuraEnchantments.MAGICULE_ABSORPTION,
               TensuraEnchantments.SEVERANCE,
               TensuraEnchantments.SEVERANCE_PROTECTION,
               TensuraEnchantments.SPIRITUAL_PROTECTION,
               TensuraEnchantments.SLOTTING,
               TensuraEnchantments.SOUL_EATER,
               TensuraEnchantments.STURDY,
               TensuraEnchantments.SWIFT
            }
         );
      this.tag(TensuraTags.Enchantments.SEALING_CURSE).add(TensuraEnchantments.SEALING);
      this.tag(TensuraTags.Enchantments.SEALING_EXCLUSIVE)
         .addTag(EnchantmentTags.ARMOR_EXCLUSIVE)
         .addTag(EnchantmentTags.BOOTS_EXCLUSIVE)
         .addTag(EnchantmentTags.RIPTIDE_EXCLUSIVE)
         .addTag(EnchantmentTags.BOW_EXCLUSIVE)
         .addTag(EnchantmentTags.CROSSBOW_EXCLUSIVE)
         .addTag(EnchantmentTags.DAMAGE_EXCLUSIVE)
         .addTag(EnchantmentTags.MINING_EXCLUSIVE)
         .addTag(EnchantmentTags.TOOLTIP_ORDER);
      this.tag(EnchantmentTags.CURSE)
         .addTag(TensuraTags.Enchantments.TSUKUMOGAMI)
         .add(
            new ResourceKey[]{
               TensuraEnchantments.ENERVATION,
               TensuraEnchantments.LETHARGY,
               TensuraEnchantments.SEALING,
               TensuraEnchantments.STAGNATION,
               TensuraEnchantments.RUINATION
            }
         );
      this.tag(net.neoforged.neoforge.common.Tags.Enchantments.WEAPON_DAMAGE_ENHANCEMENTS)
         .add(new ResourceKey[]{TensuraEnchantments.CRUSHING, TensuraEnchantments.SEVERANCE, TensuraEnchantments.STURDY, TensuraEnchantments.SWIFT});
      this.tag(net.neoforged.neoforge.common.Tags.Enchantments.ENTITY_DEFENSE_ENHANCEMENTS)
         .add(
            new ResourceKey[]{
               TensuraEnchantments.ENERGY_PROTECTION,
               TensuraEnchantments.ELEMENTAL_RESISTANCE,
               TensuraEnchantments.SEVERANCE_PROTECTION,
               TensuraEnchantments.SPIRITUAL_PROTECTION,
               TensuraEnchantments.STURDY,
               TensuraEnchantments.HOLY_COAT,
               TensuraEnchantments.MAGIC_INTERFERENCE
            }
         );
   }
}
