package dev.xkmc.l2hostility.content.logic;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@SerialClass
public class DifficultyLevel {
   @SerialField
   public int level;
   protected long experience;
   @SerialField
   public int extraLevel;

   public static DifficultyLevel merge(DifficultyLevel difficulty, int extraLevel) {
      DifficultyLevel ans = new DifficultyLevel();
      ans.level = difficulty.level;
      ans.experience = difficulty.experience;
      ans.extraLevel = difficulty.extraLevel + extraLevel;
      return ans;
   }

   public static int ofAny(LivingEntity entity) {
      return entity instanceof Player player
         ? ((PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(player)).getLevel(player).getLevel()
         : ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(entity).map(MobTraitCap::getLevel).orElse(0);
   }

   public void grow(double growFactor, MobTraitCap cap) {
      if (this.level >= (Integer)LHConfig.SERVER.maxPlayerLevel.get()) {
         this.level = (Integer)LHConfig.SERVER.maxPlayerLevel.get();
         this.experience = 0L;
      } else {
         int lv = Math.min(this.level + 10, cap.getLevel());
         this.experience += (int)(growFactor * lv * lv);

         for (int factor = (Integer)LHConfig.SERVER.killsPerLevel.get(); this.experience >= (long)this.level * this.level * factor; this.level++) {
            this.experience = this.experience - (long)this.level * this.level * factor;
         }

         if (this.level >= (Integer)LHConfig.SERVER.maxPlayerLevel.get()) {
            this.level = (Integer)LHConfig.SERVER.maxPlayerLevel.get();
            this.experience = 0L;
         }
      }
   }

   public void decay() {
      double rate = (Double)LHConfig.SERVER.playerDeathDecay.get();
      if (rate < 1.0) {
         this.level = Math.max(0, this.level - Math.max(1, (int)Math.ceil(this.level * (1.0 - rate))));
      }

      this.experience = 0L;
   }

   public long getMaxExp() {
      int factor = (Integer)LHConfig.SERVER.killsPerLevel.get();
      return Math.max(1L, (long)this.level * this.level * factor);
   }

   public int getLevel() {
      return Math.max(0, this.level + this.extraLevel);
   }

   public long getExp() {
      return this.experience;
   }

   public String getStr() {
      return this.extraLevel == 0 ? this.level + "" : (this.extraLevel > 0 ? this.level + "+" + this.extraLevel : "" + this.level + this.extraLevel);
   }
}
