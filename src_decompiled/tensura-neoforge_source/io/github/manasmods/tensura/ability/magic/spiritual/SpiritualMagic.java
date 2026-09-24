package io.github.manasmods.tensura.ability.magic.spiritual;

import com.mojang.serialization.Codec;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpiritualMagic extends Magic {
   private final Element elemental;
   private final SpiritualMagic.SpiritLevel level;

   public SpiritualMagic(Element elemental, SpiritualMagic.SpiritLevel level) {
      super(Magic.MagicType.SPIRITUAL);
      this.elemental = elemental;
      this.level = level;
   }

   @Nullable
   @Override
   public MutableComponent getColoredName() {
      MutableComponent name = super.getName();
      return name == null ? null : name.withColor(this.getElemental().getColor());
   }

   public int getMaxMastery() {
      return switch (this.getLevel()) {
         case LESSER -> MAGIC_CONFIG.SpiritualMagic.masteryLesser;
         case MEDIUM -> MAGIC_CONFIG.SpiritualMagic.masteryMedium;
         case GREATER -> MAGIC_CONFIG.SpiritualMagic.masteryGreater;
         case LORD -> MAGIC_CONFIG.SpiritualMagic.masteryLord;
      };
   }

   @Override
   public DamageSource createSource(ManasSkillInstance instance, LivingEntity attacker, ResourceKey<DamageType> type, int mode) {
      return super.createSource(instance, attacker, type, mode).tensura$setElement(this.getElemental());
   }

   @Generated
   public Element getElemental() {
      return this.elemental;
   }

   @Generated
   public SpiritualMagic.SpiritLevel getLevel() {
      return this.level;
   }

   public enum SpiritLevel implements StringRepresentable {
      LESSER(1, "lesser", ChatFormatting.YELLOW),
      MEDIUM(2, "medium", ChatFormatting.GOLD),
      GREATER(3, "greater", ChatFormatting.RED),
      LORD(4, "lord", ChatFormatting.DARK_RED);

      public static final Codec<SpiritualMagic.SpiritLevel> CODEC = StringRepresentable.fromEnum(SpiritualMagic.SpiritLevel::values);
      private final int id;
      private final String namespace;
      private final ChatFormatting chatFormatting;

      public static SpiritualMagic.SpiritLevel byId(int id) {
         if (id % 4 == 0) {
            return LORD;
         } else if (id % 3 == 0) {
            return GREATER;
         } else {
            return id % 2 == 0 ? MEDIUM : LESSER;
         }
      }

      public MutableComponent getName() {
         return Component.translatable("tensura.magic.spiritual.level." + this.namespace);
      }

      public MutableComponent getSpiritName(Element elemental) {
         return this.equals(LORD)
            ? Component.translatable("tensura.magic.spiritual.spirit_name.lord", new Object[]{this.getName(), elemental.getName()})
            : Component.translatable("tensura.magic.spiritual.spirit_name", new Object[]{this.getName(), elemental.getName()});
      }

      @NotNull
      public String getSerializedName() {
         return this.namespace;
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
      public ChatFormatting getChatFormatting() {
         return this.chatFormatting;
      }

      @Generated
      SpiritLevel(final int id, final String namespace, final ChatFormatting chatFormatting) {
         this.id = id;
         this.namespace = namespace;
         this.chatFormatting = chatFormatting;
      }
   }
}
