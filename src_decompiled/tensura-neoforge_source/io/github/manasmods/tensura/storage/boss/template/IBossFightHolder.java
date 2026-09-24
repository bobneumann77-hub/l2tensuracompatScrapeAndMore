package io.github.manasmods.tensura.storage.boss.template;

import java.util.Map;

public interface IBossFightHolder {
   Map<String, BossFightInstance> getBossFights();

   BossFightInstance getBossFight(String var1);

   void addBossFight(String var1, BossFightInstance var2);

   void removeBossFight(String var1);

   boolean isLoaded();

   void setLoaded(boolean var1);

   void reloadFromJson();

   void markDirty();
}
