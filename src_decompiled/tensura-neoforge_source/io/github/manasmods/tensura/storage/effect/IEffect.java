package io.github.manasmods.tensura.storage.effect;

public interface IEffect {
   float getSeveranceAmount();

   void setSeveranceAmount(float var1);

   void increaseSeveranceAmount(float var1);

   int getSeveranceRemoveTime();

   void setSeveranceRemoveTime(int var1);

   float getLockedXRot();

   void setLockedXRot(float var1);

   float getLockedYRot();

   void setLockedYRot(float var1);

   float getCameraShakeLevel();

   void setCameraShakeLevel(float var1);

   int getCameraShakeDuration();

   void setCameraShakeDuration(int var1);

   boolean isOnBlackFlame();

   void setOnBlackFlame(boolean var1);

   boolean isIgnorePainNull();

   void setIgnorePainNull(boolean var1);

   boolean isSleptAtThunderNight();

   void setSleptAtThunderNight(boolean var1);

   boolean isMeatShield();

   void setMeatShield(boolean var1);

   void markDirty();
}
