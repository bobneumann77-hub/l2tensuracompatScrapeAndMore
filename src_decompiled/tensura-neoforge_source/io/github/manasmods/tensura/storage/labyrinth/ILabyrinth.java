package io.github.manasmods.tensura.storage.labyrinth;

import net.minecraft.world.phys.Vec3;

public interface ILabyrinth {
   Vec3 getEntrancePos();

   void setEntrancePos(Vec3 var1);

   Vec3 getVoidSavePos();

   void setVoidSavePos(Vec3 var1);

   Vec3 getVoidSavePosPassed();

   void setVoidSavePosPassed(Vec3 var1);

   Vec3 getColossusPos();

   void setColossusPos(Vec3 var1);

   double getAreaRadius();

   void setAreaRadius(double var1);

   Vec3 getPassedEntrancePos();

   void setPassedEntrancePos(Vec3 var1);

   boolean isLoaded();

   void setLoaded(boolean var1);

   boolean isColossusFirstSpawn();

   void setColossusFirstSpawned(boolean var1);

   boolean isColossusSpawned();

   void setColossusSpawned(boolean var1);

   double getVoidHeight();

   void setVoidHeight(double var1);

   int getLastPlacedBlockIndex();

   void setLastPlacedBlockIndex(int var1);

   void markDirty();
}
