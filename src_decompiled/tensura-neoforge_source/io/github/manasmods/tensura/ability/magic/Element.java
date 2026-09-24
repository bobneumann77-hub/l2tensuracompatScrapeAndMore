package io.github.manasmods.tensura.ability.magic;

import com.mojang.serialization.Codec;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.Generated;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Element implements StringRepresentable {
   DARKNESS(0, "darkness", 9109549, Element.ElementType.HERO, null, TensuraDamageTypes.DARKNESS_ELEMENTAL, MagicCircleVariant.DARK),
   EARTH(1, "earth", 15247701, Element.ElementType.COMMON, ExtraSkills.EARTH_MANIPULATION.getId(), TensuraDamageTypes.EARTH_ELEMENTAL, MagicCircleVariant.EARTH),
   FLAME(2, "fire", 16733525, Element.ElementType.COMMON, ExtraSkills.FLAME_MANIPULATION.getId(), TensuraDamageTypes.FIRE_ELEMENTAL, MagicCircleVariant.FLAME),
   LIGHT(3, "light", 16775073, Element.ElementType.HERO, null, TensuraDamageTypes.LIGHT_ELEMENTAL, MagicCircleVariant.LIGHT),
   SPACE(
      4, "space", 16733695, Element.ElementType.COMMON, ExtraSkills.SPATIAL_MANIPULATION.getId(), TensuraDamageTypes.SPACE_ELEMENTAL, MagicCircleVariant.SPACE
   ),
   WATER(5, "water", 5416173, Element.ElementType.COMMON, ExtraSkills.WATER_MANIPULATION.getId(), TensuraDamageTypes.WATER_ELEMENTAL, MagicCircleVariant.WATER),
   WIND(6, "wind", 5635925, Element.ElementType.COMMON, ExtraSkills.WIND_MANIPULATION.getId(), TensuraDamageTypes.WIND_ELEMENTAL, MagicCircleVariant.WIND),
   HOLY(7, "holy", 16766720, Element.ElementType.NON_ELEMENTAL, null, TensuraDamageTypes.HOLY_DAMAGE, MagicCircleVariant.HOLY),
   UNIDENTIFIED(8, "unidentified", 16777215, Element.ElementType.OTHERS, null, null, MagicCircleVariant.MISC);

   public static final Codec<Element> CODEC = StringRepresentable.fromEnum(Element::values);
   private static final Element[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Element::getId)).toArray(Element[]::new);
   private final int id;
   private final String namespace;
   private final int color;
   private final Element.ElementType elementType;
   @Nullable
   private final ResourceLocation manipulation;
   @Nullable
   private final ResourceKey<DamageType> defaultDamage;
   private final MagicCircleVariant magicCircleVariant;

   Element(
      int id,
      String namespace,
      int color,
      Element.ElementType type,
      ResourceLocation manipulation,
      ResourceKey<DamageType> defaultDamage,
      MagicCircleVariant magicCircleVariant
   ) {
      this.id = id;
      this.namespace = namespace;
      this.color = color;
      this.elementType = type;
      this.manipulation = manipulation;
      this.defaultDamage = defaultDamage;
      this.magicCircleVariant = magicCircleVariant;
   }

   @NotNull
   public String getSerializedName() {
      return this.namespace;
   }

   public static Element byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   public MutableComponent getName() {
      return Component.translatable("tensura.magic.elemental." + this.namespace);
   }

   public boolean isElemental() {
      return this.elementType != Element.ElementType.NON_ELEMENTAL && this.elementType != Element.ElementType.OTHERS;
   }

   public boolean isChosenHeroElemental() {
      return this.elementType == Element.ElementType.HERO;
   }

   public static List<Element> getCommonElemental() {
      return List.of(EARTH, FLAME, SPACE, WATER, WIND);
   }

   public static List<Element> getCommandSuggestElemental() {
      return List.of(DARKNESS, EARTH, FLAME, LIGHT, SPACE, WATER, WIND);
   }

   @Generated
   public int getId() {
      return this.id;
   }

   @Generated
   public String getNamespace() {
      return this.namespace;
   }

   @Generated
   public int getColor() {
      return this.color;
   }

   @Generated
   public Element.ElementType getElementType() {
      return this.elementType;
   }

   @Nullable
   @Generated
   public ResourceLocation getManipulation() {
      return this.manipulation;
   }

   @Nullable
   @Generated
   public ResourceKey<DamageType> getDefaultDamage() {
      return this.defaultDamage;
   }

   @Generated
   public MagicCircleVariant getMagicCircleVariant() {
      return this.magicCircleVariant;
   }

   public enum ElementType {
      COMMON,
      HERO,
      NEW_ELEMENTAL,
      NON_ELEMENTAL,
      OTHERS;
   }
}
