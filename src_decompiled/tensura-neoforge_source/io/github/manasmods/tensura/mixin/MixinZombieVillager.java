package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.registry.entity.ai.TensuraVillagerProfessions;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillager.class)
public class MixinZombieVillager {
   @Inject(
      method = "finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/monster/ZombieVillager;setVillagerData(Lnet/minecraft/world/entity/npc/VillagerData;)V",
         shift = Shift.AFTER
      )
   )
   private void finalizeSpawn(
      ServerLevelAccessor serverLevelAccessor,
      DifficultyInstance difficultyInstance,
      MobSpawnType mobSpawnType,
      SpawnGroupData spawnGroupData,
      CallbackInfoReturnable<SpawnGroupData> cir
   ) {
      ZombieVillager villager = (ZombieVillager)this;
      if (this.tensura$isExcludedProfession(villager.getVillagerData())) {
         villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.NITWIT));
      }
   }

   @Unique
   private boolean tensura$isExcludedProfession(VillagerData data) {
      if (data.getProfession().equals(TensuraVillagerProfessions.BATTLEWILL_TRAINER.get())) {
         return true;
      } else if (data.getProfession().equals(TensuraVillagerProfessions.GUARD.get())) {
         return true;
      } else if (data.getProfession().equals(TensuraVillagerProfessions.LUMBERJACK.get())) {
         return true;
      } else if (data.getProfession().equals(TensuraVillagerProfessions.MAGIC_TRAINER.get())) {
         return true;
      } else if (data.getProfession().equals(TensuraVillagerProfessions.MINER.get())) {
         return true;
      } else {
         return data.getProfession().equals(TensuraVillagerProfessions.MERCHANT.get())
            ? true
            : data.getProfession().equals(TensuraVillagerProfessions.ROYAL_GUARD.get());
      }
   }
}
