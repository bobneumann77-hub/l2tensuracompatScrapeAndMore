package io.github.manasmods.tensura.client;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class TensuraColors {
   public static final int AQUA = 5636095;
   public static final int BLUE = 5416173;
   public static final int COPPER = 14187310;
   public static final int CYAN = 2208948;
   public static final int DARK_GRAY = 5592405;
   public static final int DARK_PURPLE = 11141375;
   public static final int GOLD = 16766720;
   public static final int GRAY = 8750469;
   public static final int GREEN = 5635925;
   public static final int LIGHT_BLUE = 10278389;
   public static final int LIGHT_GRAY = 11184810;
   public static final int LIGHT_GREEN = 9498256;
   public static final int LIGHT_PURPLE = 16733695;
   public static final int LIGHT_RED = 16213349;
   public static final int LIGHT_YELLOW = 16775073;
   public static final int MAGENTA = 16711935;
   public static final int MAROON = 9109549;
   public static final int ORANGE = 16490018;
   public static final int PINK = 15686609;
   public static final int PURE_RED = 16711680;
   public static final int PURE_WHITE = 16777215;
   public static final int PURE_BLACK = 0;
   public static final int PURPLE = 7747016;
   public static final int PURPLE_BLUE = 6907099;
   public static final int RED = 16733525;
   public static final int SANDY_BROWN = 15247701;
   public static final int VIVID_ORANGE = 16089632;
   public static final int YELLOW = 15788046;

   public static int getARGB(int color, int alpha) {
      return alpha << 24 | color & 16777215;
   }

   public static int getARGB(int color, float alpha) {
      return getARGB(color, Math.round(alpha * 255.0F));
   }

   public static int getRGB(float r, float g, float b) {
      r = Math.max(0.0F, Math.min(1.0F, r));
      g = Math.max(0.0F, Math.min(1.0F, g));
      b = Math.max(0.0F, Math.min(1.0F, b));
      int ri = (int)(r * 255.0F + 0.5F);
      int gi = (int)(g * 255.0F + 0.5F);
      int bi = (int)(b * 255.0F + 0.5F);
      return ri << 16 | gi << 8 | bi;
   }

   public static int getARGB(float a, float r, float g, float b) {
      a = Math.max(0.0F, Math.min(1.0F, a));
      r = Math.max(0.0F, Math.min(1.0F, r));
      g = Math.max(0.0F, Math.min(1.0F, g));
      b = Math.max(0.0F, Math.min(1.0F, b));
      int ai = (int)(a * 255.0F + 0.5F);
      int ri = (int)(r * 255.0F + 0.5F);
      int gi = (int)(g * 255.0F + 0.5F);
      int bi = (int)(b * 255.0F + 0.5F);
      return ai << 24 | ri << 16 | gi << 8 | bi;
   }

   public static int getARGBWithAlpha(int color, float alpha) {
      alpha = Math.max(0.0F, Math.min(1.0F, alpha));
      int a = color >> 24 & 0xFF;
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      a = (int)(a * alpha);
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static int getTonedARGB(int color, float tone) {
      tone = Math.max(0.0F, Math.min(1.0F, tone));
      int a = color >> 24 & 0xFF;
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      r = (int)(r * tone);
      g = (int)(g * tone);
      b = (int)(b * tone);
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static int getTonedRGB(int color, float tone) {
      tone = Math.max(0.0F, Math.min(1.0F, tone));
      int r = color >> 16 & 0xFF;
      int g = color >> 8 & 0xFF;
      int b = color & 0xFF;
      r = (int)(r * tone);
      g = (int)(g * tone);
      b = (int)(b * tone);
      return r << 16 | g << 8 | b;
   }

   public static int getMixedColor(int c1, int c2) {
      int a1 = c1 >> 24 & 0xFF;
      int r1 = c1 >> 16 & 0xFF;
      int g1 = c1 >> 8 & 0xFF;
      int b1 = c1 & 0xFF;
      int a2 = c2 >> 24 & 0xFF;
      int r2 = c2 >> 16 & 0xFF;
      int g2 = c2 >> 8 & 0xFF;
      int b2 = c2 & 0xFF;
      int a = (a1 + a2) / 2;
      int r = (r1 + r2) / 2;
      int g = (g1 + g2) / 2;
      int b = (b1 + b2) / 2;
      return a << 24 | r << 16 | g << 8 | b;
   }

   public static int getColor(Component component) {
      return getColor(component.getStyle());
   }

   public static int getColor(Style style) {
      return getColor(style.getColor());
   }

   public static int getColor(TextColor textColor) {
      return textColor == null ? 16777215 : textColor.getValue();
   }
}
