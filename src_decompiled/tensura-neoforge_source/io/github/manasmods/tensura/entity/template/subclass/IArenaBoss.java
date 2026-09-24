package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.storage.boss.template.BossFightInstance;
import net.minecraft.server.level.ServerLevel;

public interface IArenaBoss {
   String getBossFightId();

   void setBossFightId(String var1);

   void onSpawned(ServerLevel var1, String var2, BossFightInstance var3);
}
