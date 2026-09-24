package io.github.manasmods.tensura.storage.player;

import io.github.manasmods.manascore.race.api.ManasRace;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ITensuraPlayer {
   ManasRace getTrackedEvolution();

   void setTrackedEvolution(ManasRace var1);

   int getResetCounter();

   void setResetCounter(int var1);

   int getBonusSkillLock();

   void setBonusSkillLock(int var1);

   int getAnalysisMode();

   void setAnalysisMode(int var1);

   int getPresenceSenseMode();

   void setPresenceSenseMode(int var1);

   int getDodgeCooldown();

   void setDodgeCooldown(int var1);

   int getDodgeInvulnerability();

   void setDodgeInvulnerability(int var1);

   int getInResetProgress();

   void setInResetProgress(int var1);

   boolean isForcedThirdPerson();

   void setForcedThirdPerson(boolean var1);

   String getPreviouslyInBossFight();

   void setPreviouslyInBossFight(String var1);

   int getMaxWarpPoints();

   void setMaxWarpPoints(int var1);

   List<WarpPoint> getWarpPoints();

   void addWarpPoint(String var1, double var2, double var4, double var6, ResourceKey<Level> var8);

   void addWarpPoint(String var1, double var2, double var4, double var6);

   void addWarpPoint(double var1, double var3, double var5);

   void removeWarpPoint(int var1);

   void removeWarpPoint(WarpPoint var1);

   void removeWarpPointOverMax(int var1);

   void clearWarpPoints();

   List<WarpPoint> getWarpPads();

   void addWarpPad(WarpPoint var1);

   void addWarpPad(String var1, double var2, double var4, double var6, ResourceKey<Level> var8);

   void removeWarpPad(int var1);

   void removeWarpPad(WarpPoint var1);

   void clearWarpPads();

   List<ResourceLocation> getKnownSchematics();

   boolean hasSchematic(ResourceLocation var1);

   default boolean hasSchematic(Item schematic) {
      return this.hasSchematic(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(schematic)));
   }

   default boolean hasSchematic(ItemStack schematic) {
      return this.hasSchematic(schematic.getItem());
   }

   boolean hasSchematics(List<ResourceLocation> var1);

   void unlockSchematic(ResourceLocation var1);

   default void unlockSchematic(Item schematic) {
      this.unlockSchematic(Objects.requireNonNull(BuiltInRegistries.ITEM.getKey(schematic)));
   }

   default void unlockSchematic(ItemStack schematic) {
      this.unlockSchematic(schematic.getItem());
   }

   void clearSchematics();

   List<ResourceLocation> getLockedSkills();

   void addLockedSkill(ResourceLocation var1);

   void removeLockedSkill(ResourceLocation var1);

   void limitLockedSkills(Level var1);

   void clearLockedSkills();

   Map<ResourceLocation, Double> getReputations();

   double getReputation(ResourceLocation var1);

   default double getReputation(EntityType<?> type) {
      return this.getReputation(type.arch$registryName());
   }

   void setReputation(ResourceLocation var1, double var2);

   default void setReputation(EntityType<?> type, double reputation) {
      this.setReputation(type.arch$registryName(), reputation);
   }

   void resetReputation(ResourceLocation var1);

   default void resetReputation(EntityType<?> type) {
      this.resetReputation(type.arch$registryName());
   }

   void clearReputation();

   void markDirty();
}
