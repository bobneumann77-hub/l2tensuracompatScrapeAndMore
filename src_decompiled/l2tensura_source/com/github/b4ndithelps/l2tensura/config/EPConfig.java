package com.github.b4ndithelps.l2tensura.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class EPConfig {
   public static final ForgeConfigSpec SPEC;
   public static final IntValue levelInterval;
   public static final DoubleValue epMultiplierPerInterval;
   public static final DoubleValue maxEPMultiplier;
   public static final DoubleValue baseEPCalculationFactor;
   public static final DoubleValue minimumBaseEP;
   public static final BooleanValue enableDebugLogging;

   public static void register() {
      ModLoadingContext.get().registerConfig(Type.COMMON, SPEC, "l2tensura-ep.toml");
   }

   public static double calculateEPMultiplier(int hostilityLevel) {
      if (hostilityLevel <= 0) {
         return 1.0;
      }

      int levelGroups = hostilityLevel / (Integer)levelInterval.get();
      double multiplier = 1.0 + levelGroups * (Double)epMultiplierPerInterval.get();
      return Math.min(multiplier, (Double)maxEPMultiplier.get());
   }

   public static double calculateBaseEP(double maxHealth) {
      double baseEP = maxHealth * (Double)baseEPCalculationFactor.get();
      return Math.max(baseEP, (Double)minimumBaseEP.get());
   }

   public static boolean isDebugLoggingEnabled() {
      return (Boolean)enableDebugLogging.get();
   }

   public static String getConfigInfo() {
      return String.format(
         "EP Config: Interval=%d, Multiplier=%.3f, MaxMultiplier=%.1f, Factor=%.1f, MinEP=%.1f",
         levelInterval.get(),
         epMultiplierPerInterval.get(),
         maxEPMultiplier.get(),
         baseEPCalculationFactor.get(),
         minimumBaseEP.get()
      );
   }

   static {
      Builder builder = new Builder();
      builder.comment("L2Hostility EP Enhancement System Configuration | L2敌意EP增强系统配置").push("ep_enhancement");
      builder.comment("EP Calculation Settings | EP计算设置").push("calculation");
      levelInterval = builder.comment("Level interval for EP multiplier increase (default: 5) | EP倍率增长的等级间隔 (默认: 5)").defineInRange("levelInterval", 5, 1, 50);
      epMultiplierPerInterval = builder.comment("EP multiplier increase per level interval (0.07 = 7% increase) | 每个等级间隔的EP倍率增长 (0.07 = 增长7%)")
         .defineInRange("epMultiplierPerInterval", 0.07, 0.0, 1.0);
      maxEPMultiplier = builder.comment("Maximum EP multiplier cap | EP倍率上限").defineInRange("maxEPMultiplier", 10.0, 1.0, 100.0);
      baseEPCalculationFactor = builder.comment("Base EP calculation factor (Base EP = Max Health × Factor) | 基础EP计算系数 (基础EP = 最大生命值 × 系数)")
         .defineInRange("baseEPCalculationFactor", 5.0, 1.0, 50.0);
      minimumBaseEP = builder.comment("Minimum base EP value | 最小基础EP值").defineInRange("minimumBaseEP", 100.0, 10.0, 10000.0);
      builder.pop();
      builder.comment("Debug Settings | 调试设置").push("debug");
      enableDebugLogging = builder.comment("Enable debug logging for EP calculations | 启用EP计算的调试日志").define("enableDebugLogging", false);
      builder.pop();
      builder.pop();
      SPEC = builder.build();
   }
}
