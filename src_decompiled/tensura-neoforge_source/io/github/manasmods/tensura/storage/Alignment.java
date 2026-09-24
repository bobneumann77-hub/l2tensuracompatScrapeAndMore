package io.github.manasmods.tensura.storage;

import com.mojang.serialization.Codec;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.storage.ep.IExistence;
import lombok.Generated;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public enum Alignment implements StringRepresentable {
   DEFAULT("default", 16777215, true, true, false, false, true, 2.0, 1.0),
   MAJIN("majin", 16711935, false, false, true, true, false, 1.0, 2.0),
   HOLY("holy", 16766720, false, false, false, false, true, 1.0, 2.0),
   CHAOS("chaos", 16733525, false, false, false, true, true, 1.0, 2.0);

   private final String id;
   private final int color;
   private final boolean needAir;
   private final boolean needFood;
   private final boolean affectedByHolyExposure;
   private final boolean canBecomeDemonLord;
   private final boolean canBecomeHero;
   private final double auraGainMultiplier;
   private final double magiculeGainMultiplier;
   public static final Codec<Alignment> CODEC = StringRepresentable.fromEnum(Alignment::values);

   Alignment(
      String id,
      int color,
      boolean air,
      boolean food,
      boolean holyAffected,
      boolean demonLord,
      boolean hero,
      double auraGainMultiplier,
      double magiculeGainMultiplier
   ) {
      this.id = id;
      this.color = color;
      this.needAir = air;
      this.needFood = food;
      this.affectedByHolyExposure = holyAffected;
      this.canBecomeDemonLord = demonLord;
      this.canBecomeHero = hero;
      this.auraGainMultiplier = auraGainMultiplier;
      this.magiculeGainMultiplier = magiculeGainMultiplier;
   }

   public Component getName() {
      return Component.translatable("tensura.alignment." + this.id);
   }

   public static boolean shouldConsumeAir(LivingEntity entity) {
      if (!entity.hasInfiniteMaterials() && !entity.isSpectator()) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (!existence.getAlignment().isNeedAir()) {
            return false;
         } else {
            return RaceUtils.isSpiritual(entity)
               ? false
               : TensuraEnchantmentHelper.getEnchantmentLevel(entity.level(), TensuraEnchantments.BREATHING_SUPPORT, entity) <= 0;
         }
      } else {
         return false;
      }
   }

   @NotNull
   public String getSerializedName() {
      return this.id;
   }

   @Generated
   public String getId() {
      return this.id;
   }

   @Generated
   public int getColor() {
      return this.color;
   }

   @Generated
   public boolean isNeedAir() {
      return this.needAir;
   }

   @Generated
   public boolean isNeedFood() {
      return this.needFood;
   }

   @Generated
   public boolean isAffectedByHolyExposure() {
      return this.affectedByHolyExposure;
   }

   @Generated
   public boolean isCanBecomeDemonLord() {
      return this.canBecomeDemonLord;
   }

   @Generated
   public boolean isCanBecomeHero() {
      return this.canBecomeHero;
   }

   @Generated
   public double getAuraGainMultiplier() {
      return this.auraGainMultiplier;
   }

   @Generated
   public double getMagiculeGainMultiplier() {
      return this.magiculeGainMultiplier;
   }
}
