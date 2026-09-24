package io.github.manasmods.tensura.block.part;

import lombok.Generated;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum KilnPart implements StringRepresentable {
   BASE("base"),
   TOP("top");

   private final String name;

   @NotNull
   public String getSerializedName() {
      return this.name;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Generated
   KilnPart(final String name) {
      this.name = name;
   }

   @Generated
   public String getName() {
      return this.name;
   }
}
