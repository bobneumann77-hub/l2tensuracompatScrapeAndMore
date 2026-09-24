package io.github.manasmods.tensura.ability.magic.aspectual;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.util.Arrays;
import java.util.Comparator;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class AspectualMagic extends Magic {
   private final AspectualMagic.AspectualType aspectualType;

   public AspectualMagic(AspectualMagic.AspectualType type) {
      super(Magic.MagicType.ASPECTUAL);
      this.aspectualType = type;
   }

   @Nullable
   @Override
   public MutableComponent getColoredName() {
      MutableComponent name = super.getName();
      return name == null ? null : name.withColor(this.getAspectualType().getColor());
   }

   @Override
   public DamageSource createSource(ManasSkillInstance instance, LivingEntity attacker, ResourceKey<DamageType> type, int mode) {
      return super.createSource(instance, attacker, type, mode).tensura$setElement(this.getAspectualType().getElement());
   }

   @Override
   public boolean isCastingBlocked(ManasSkillInstance instance, LivingEntity entity) {
      if (super.isCastingBlocked(instance, entity)) {
         return true;
      }

      if (entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_MAGIC))) {
         if (instance.isMastered(entity) && entity.getAttributeValue(TensuraAttributes.LAW_DEGRADATION) > 0.0) {
            return false;
         }

         if (entity instanceof Player player) {
            player.displayClientMessage(Component.translatable("tensura.ability.activation_failed.anti_magic").withStyle(ChatFormatting.RED), true);
         }

         return true;
      } else {
         return false;
      }
   }

   @Generated
   public AspectualMagic.AspectualType getAspectualType() {
      return this.aspectualType;
   }

   public enum AspectualType {
      BARRIER(0, "barrier", 2208948),
      EARTH(1, Element.EARTH),
      ENHANCEMENT(2, "enhancement", 14187310),
      EXPLOSION(3, "explosion", 16490018),
      FIRE(4, Element.FLAME),
      GRAVITY(5, "gravity", 6907099),
      ICE(6, "ice", 5636095),
      ILLUSION(7, "illusion", 10278389),
      LIGHTNING(8, "lightning", 15788046),
      MENTAL(9, "mental", 7747016),
      RECOVERY(10, "recovery", 9498256),
      SPACE(11, Element.SPACE),
      WATER(12, Element.WATER),
      WIND(13, Element.WIND),
      MISC(14, "misc", Element.UNIDENTIFIED);

      private static final AspectualMagic.AspectualType[] BY_ID = Arrays.stream(values())
         .sorted(Comparator.comparingInt(AspectualMagic.AspectualType::getId))
         .toArray(AspectualMagic.AspectualType[]::new);
      private final int id;
      private final String namespace;
      private final Element element;
      private final int color;

      AspectualType(int id, String namespace, int color) {
         this.id = id;
         this.namespace = namespace;
         this.element = Element.UNIDENTIFIED;
         this.color = color;
      }

      AspectualType(int id, String namespace, Element element) {
         this.id = id;
         this.namespace = namespace;
         this.element = element;
         this.color = element.getColor();
      }

      AspectualType(int id, Element element) {
         this(id, element.getNamespace(), element);
      }

      public static AspectualMagic.AspectualType byId(int id) {
         return BY_ID[id % BY_ID.length];
      }

      public MutableComponent getName() {
         return Component.translatable("tensura.magic.elemental." + this.namespace);
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
      public Element getElement() {
         return this.element;
      }

      @Generated
      public int getColor() {
         return this.color;
      }
   }
}
