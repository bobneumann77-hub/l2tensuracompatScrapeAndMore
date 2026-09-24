package io.github.manasmods.tensura.particle;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.particle.option.CloudParticleOptions;
import io.github.manasmods.tensura.particle.option.NumberParticleOptions;
import io.github.manasmods.tensura.particle.option.ShockWaveParticleOptions;
import io.github.manasmods.tensura.particle.option.SimpleAuraParticleOptions;
import io.github.manasmods.tensura.particle.option.SimpleBubbleParticleOptions;
import io.github.manasmods.tensura.particle.option.SimpleEffectParticleOptions;
import io.github.manasmods.tensura.particle.option.SimpleGustParticleOptions;
import io.github.manasmods.tensura.particle.option.SonicBoomParticleOptions;
import io.github.manasmods.tensura.particle.option.SpiritParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class TensuraParticleUtils {
   public static SimpleAuraParticleOptions getAcidAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(0.42F, 0.87F, 0.23F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getBlackAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(0.05F, 0.05F, 0.05F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getChaosEaterAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(0.32F, 0.121F, 0.105F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getCrimsonAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(0.69F, 0.13F, 0.13F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getEarthAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(0.66F, 0.47F, 0.32F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getFlameOrangeAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(0.98F, 0.54F, 0.09F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getGoldAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(1.0F, 0.85F, 0.29F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getParalyzingAura(float alpha, float size, float gravity, int life) {
      return new SimpleAuraParticleOptions(0.95F, 0.95F, 0.2F, alpha, size, gravity, life);
   }

   public static SimpleAuraParticleOptions getPinkAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(0.87F, 0.64F, 0.9F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getPoisonAura(float alpha, float size, float gravity, int life) {
      return new SimpleAuraParticleOptions(0.54F, 0.16F, 0.71F, alpha, size, gravity, life);
   }

   public static SimpleAuraParticleOptions getPurpleAura(float alpha, float size, float gravity) {
      return new SimpleAuraParticleOptions(0.25F, 0.14F, 0.36F, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getRedAura(float alpha, float size, float gravity) {
      Vec3 vec3 = Vec3.fromRGB24(16711680);
      return new SimpleAuraParticleOptions((float)vec3.x, (float)vec3.y, (float)vec3.z, alpha, size, gravity);
   }

   public static SimpleAuraParticleOptions getWhiteAura(float alpha, float size, float gravity, int life) {
      return new SimpleAuraParticleOptions(1.0F, 1.0F, 1.0F, alpha, size, gravity, life);
   }

   public static SimpleBubbleParticleOptions getWhiteBubble() {
      return new SimpleBubbleParticleOptions(0.84F, 0.85F, 0.82F, 2.0F, 0.25F);
   }

   public static SimpleBubbleParticleOptions getAcidBubble() {
      return new SimpleBubbleParticleOptions(0.42F, 0.87F, 0.23F, 3.0F, 0.35F);
   }

   public static SimpleBubbleParticleOptions getBogBubble() {
      return new SimpleBubbleParticleOptions(0.2F, 0.17F, 0.14F, 3.0F, 0.45F);
   }

   public static SimpleBubbleParticleOptions getMudBubble() {
      return new SimpleBubbleParticleOptions(0.64F, 0.39F, 0.2F, 3.0F, 0.45F);
   }

   public static SimpleBubbleParticleOptions getParalyzingBubble() {
      return new SimpleBubbleParticleOptions(0.95F, 0.95F, 0.2F, 3.0F, 0.2F);
   }

   public static SimpleBubbleParticleOptions getPoisonBubble() {
      return new SimpleBubbleParticleOptions(0.54F, 0.16F, 0.71F, 3.0F, 0.35F);
   }

   public static SimpleBubbleParticleOptions getWaterBubble(int life) {
      return new SimpleBubbleParticleOptions(0.18F, 0.7F, 0.95F, 2.0F, 0.1F, life);
   }

   public static SimpleBubbleParticleOptions getWaterBubble() {
      return getWaterBubble(10);
   }

   public static CloudParticleOptions getAcidCloud() {
      return new CloudParticleOptions(0.42F, 0.87F, 0.23F, 1.0F, 0.05F, 1.0F, 0.1F, 60);
   }

   public static CloudParticleOptions getBlackCloud(float alpha, float minAlpha, float size) {
      return new CloudParticleOptions(0.0F, 0.0F, 0.0F, alpha, minAlpha, size, -0.1F, 60);
   }

   public static CloudParticleOptions getBloodMist(int life) {
      Vec3 vec3 = Vec3.fromRGB24(16711680);
      return new CloudParticleOptions((float)vec3.x, (float)vec3.y, (float)vec3.z, 1.0F, 0.05F, 0.5F, 0.1F, life);
   }

   public static CloudParticleOptions getHealingCloud() {
      return new CloudParticleOptions(0.63F, 1.0F, 0.63F, 1.0F, 0.05F, 1.0F, 0.1F, 60);
   }

   public static CloudParticleOptions getHypnosisCloud() {
      return new CloudParticleOptions(0.65F, 0.19F, 0.65F, 0.5F, 0.05F, 1.0F, -0.1F, 60);
   }

   public static CloudParticleOptions getMiasmicMist() {
      return new CloudParticleOptions(0.36F, 0.12F, 0.72F, 0.5F, 0.05F, 1.0F, -0.05F, 40);
   }

   public static CloudParticleOptions getSleepMist() {
      return new CloudParticleOptions(0.26F, 0.14F, 0.76F, 0.5F, 0.05F, 1.0F, 0.1F, 40);
   }

   public static SimpleEffectParticleOptions getWhiteEffect() {
      return new SimpleEffectParticleOptions(0.84F, 0.85F, 0.82F, 1.0F, 0.25F);
   }

   public static SimpleEffectParticleOptions getAcidEffect() {
      return new SimpleEffectParticleOptions(0.42F, 0.87F, 0.23F, 1.15F, 0.35F);
   }

   public static SimpleEffectParticleOptions getBogEffect() {
      return new SimpleEffectParticleOptions(0.2F, 0.17F, 0.14F, 1.15F, 0.45F);
   }

   public static SimpleEffectParticleOptions getMudEffect() {
      return new SimpleEffectParticleOptions(0.64F, 0.39F, 0.2F, 1.15F, 0.45F);
   }

   public static SimpleEffectParticleOptions getParalyzingEffect(int life) {
      return new SimpleEffectParticleOptions(0.95F, 0.95F, 0.2F, 2.0F, 0.01F, life);
   }

   public static SimpleEffectParticleOptions getPoisonEffect(int life) {
      return new SimpleEffectParticleOptions(0.54F, 0.16F, 0.71F, 2.0F, 0.01F, life);
   }

   public static SimpleEffectParticleOptions getSteamEffect() {
      return new SimpleEffectParticleOptions(0.83F, 0.93F, 0.98F, 1.15F, -0.35F);
   }

   public static SimpleEffectParticleOptions getWaterEffect(int life) {
      return new SimpleEffectParticleOptions(0.18F, 0.7F, 0.95F, 1.15F, 0.1F, life);
   }

   public static SimpleEffectParticleOptions getWaterEffect() {
      return getWaterEffect(10);
   }

   public static SimpleGustParticleOptions getGust() {
      return getGust(1.0F);
   }

   public static SimpleGustParticleOptions getGust(float scale) {
      return new SimpleGustParticleOptions(1.0F, 1.0F, 1.0F, scale, -0.2F);
   }

   public static SimpleGustParticleOptions getGreenGust() {
      return new SimpleGustParticleOptions(0.3F, 0.93F, 0.58F, 0.5F, 1.0F, -0.2F, 7);
   }

   public static SimpleGustParticleOptions getYellowGust() {
      return new SimpleGustParticleOptions(1.0F, 0.92F, 0.0F, 0.35F, 1.0F, -0.2F);
   }

   public static NumberParticleOptions getNumber(float value, float red, float green, float blue, int index) {
      return getNumber(value, TensuraColors.getRGB(red, green, blue), index);
   }

   public static NumberParticleOptions getNumber(float value, int rgb, int index) {
      return getNumber(value, rgb, TensuraColors.getTonedRGB(rgb, 0.25F), index);
   }

   public static NumberParticleOptions getNumber(float value, int rgb, int shadow, int index) {
      if (index >= 1) {
         index--;
      }

      return new NumberParticleOptions(value, rgb, shadow, 0.9F, 1.0F, -0.1F, 35, index);
   }

   public static NumberParticleOptions getNumber(float value, int rgb, float alpha, float scale, float gravity, int index) {
      if (index >= 1) {
         index--;
      }

      return new NumberParticleOptions(value, rgb, TensuraColors.getTonedRGB(rgb, 0.25F), alpha, scale, gravity, 35, index);
   }

   public static ShockWaveParticleOptions getColorlessWave(float alpha, float size) {
      return new ShockWaveParticleOptions(1.0F, 1.0F, 1.0F, alpha, size, 0.1F, false);
   }

   public static ShockWaveParticleOptions getColorlessReversedWave(float alpha, float size) {
      return new ShockWaveParticleOptions(1.0F, 1.0F, 1.0F, alpha, size, -0.5F, true);
   }

   public static ShockWaveParticleOptions getColorlessWave(float alpha, float size, float gravity, boolean reversed) {
      return new ShockWaveParticleOptions(1.0F, 1.0F, 1.0F, alpha, size, gravity, reversed);
   }

   public static ShockWaveParticleOptions getDrowsinessWave(float size, float gravity) {
      return new ShockWaveParticleOptions(0.43F, 0.33F, 0.47F, 0.5F, size, gravity, false);
   }

   public static ShockWaveParticleOptions getSlothRestWave(float size) {
      return new ShockWaveParticleOptions(0.6F, 0.69F, 0.87F, 0.6F, size, -0.5F, true);
   }

   public static ShockWaveParticleOptions getBlackWave(float alpha, float size, float gravity, boolean reversed) {
      return new ShockWaveParticleOptions(0.0F, 0.0F, 0.0F, alpha, size, gravity, reversed);
   }

   public static ShockWaveParticleOptions getBlueWave(float alpha, float size, float gravity, boolean reversed) {
      return new ShockWaveParticleOptions(0.35F, 0.62F, 0.98F, alpha, size, gravity, reversed);
   }

   public static ShockWaveParticleOptions getGoldWave(float alpha, float size, float gravity, boolean reversed) {
      return new ShockWaveParticleOptions(1.0F, 0.85F, 0.29F, alpha, size, gravity, reversed);
   }

   public static ShockWaveParticleOptions getGreenWave(float alpha, float size, float gravity, boolean reversed) {
      return new ShockWaveParticleOptions(0.27F, 0.96F, 0.16F, alpha, size, gravity, reversed);
   }

   public static ShockWaveParticleOptions getLightGreenWave(float alpha, float size, float gravity, boolean reversed) {
      return new ShockWaveParticleOptions(0.63F, 1.0F, 0.63F, alpha, size, gravity, reversed);
   }

   public static ShockWaveParticleOptions getPurpleWave(float alpha, float size, float gravity, boolean reversed) {
      return new ShockWaveParticleOptions(0.64F, 0.19F, 0.99F, alpha, size, gravity, reversed);
   }

   public static ShockWaveParticleOptions getRedWave(float alpha, float size, float gravity, boolean reversed) {
      Vec3 vec3 = Vec3.fromRGB24(16711680);
      return new ShockWaveParticleOptions((float)vec3.x, (float)vec3.y, (float)vec3.z, alpha, size, gravity, reversed);
   }

   public static ShockWaveParticleOptions getYellowWave(float alpha, float size, float gravity, boolean reversed) {
      return new ShockWaveParticleOptions(0.99F, 0.81F, 0.03F, alpha, size, gravity, reversed);
   }

   public static SonicBoomParticleOptions getColorlessSonic(float alpha, float size) {
      return new SonicBoomParticleOptions(1.0F, 1.0F, 1.0F, alpha, size);
   }

   public static SonicBoomParticleOptions getMindRequiemSonic(float size) {
      return new SonicBoomParticleOptions(1.0F, 0.7F, 0.0F, 1.0F, size);
   }

   public static SpiritParticleOptions getSpirit(double x, double y, double z, double radius, float red, float green, float blue) {
      return new SpiritParticleOptions(x, y, z, radius, red, green, blue, 1.0F, 0.5F, 50);
   }

   public static SpiritParticleOptions getPrayingSpirit(double x, double y, double z, double radius, RandomSource source) {
      Element element = Element.getCommandSuggestElemental().get(source.nextInt(7));
      float red = (element.getColor() >> 16 & 0xFF) / 255.0F;
      float green = (element.getColor() >> 8 & 0xFF) / 255.0F;
      float blue = (element.getColor() & 0xFF) / 255.0F;
      return new SpiritParticleOptions(x, y, z, radius, red, green, blue, 1.0F, 0.5F, 50);
   }
}
