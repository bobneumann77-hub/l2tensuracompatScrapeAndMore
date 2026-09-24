package io.github.manasmods.tensura.block.part;

import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum WarpPadPart implements StringRepresentable {
   SMALL_NW(2, 0, 0),
   SMALL_NE(2, 1, 0),
   SMALL_SW(2, 0, 1),
   SMALL_SE(2, 1, 1),
   BIG_NW(3, 0, 0),
   BIG_N(3, 1, 0),
   BIG_NE(3, 2, 0),
   BIG_W(3, 0, 1),
   BIG_C(3, 1, 1),
   BIG_E(3, 2, 1),
   BIG_SW(3, 0, 2),
   BIG_S(3, 1, 2),
   BIG_SE(3, 2, 2);

   public final int size;
   public final int dx;
   public final int dz;

   WarpPadPart(int size, int dx, int dz) {
      this.size = size;
      this.dx = dx;
      this.dz = dz;
   }

   public boolean isBig() {
      return this.size == 3;
   }

   @Nullable
   public static WarpPadPart fromOffset(int size, int dx, int dz) {
      for (WarpPadPart piece : values()) {
         if (piece.size == size && piece.dx == dx && piece.dz == dz) {
            return piece;
         }
      }

      return null;
   }

   @NotNull
   public String getSerializedName() {
      return this.name().toLowerCase(Locale.ROOT);
   }
}
