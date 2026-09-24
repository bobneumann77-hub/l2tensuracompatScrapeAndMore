package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;

public class AssignProfession extends ExtendedBehaviour<TensuraMerchantEntity> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .hasMemory(MemoryModuleType.POTENTIAL_JOB_SITE)
      .usesMemory(MemoryModuleType.JOB_SITE);
   private Function<TensuraMerchantEntity, Double> distanceRequired = v -> 2.0;
   private byte eventByte = 14;

   public AssignProfession distance(Function<TensuraMerchantEntity, Double> dist) {
      this.distanceRequired = dist;
      return this;
   }

   public AssignProfession event(byte b) {
      this.eventByte = b;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, TensuraMerchantEntity TensuraMerchantEntity) {
      GlobalPos potential = (GlobalPos)BrainUtils.getMemory(TensuraMerchantEntity, MemoryModuleType.POTENTIAL_JOB_SITE);
      if (potential == null) {
         return false;
      }

      double dist = this.distanceRequired.apply(TensuraMerchantEntity);
      return potential.pos().closerToCenterThan(TensuraMerchantEntity.position(), dist);
   }

   protected void start(TensuraMerchantEntity merchant) {
      ServerLevel level = (ServerLevel)merchant.level();
      GlobalPos potential = (GlobalPos)BrainUtils.getMemory(merchant, MemoryModuleType.POTENTIAL_JOB_SITE);
      if (potential != null) {
         BrainUtils.clearMemory(merchant, MemoryModuleType.POTENTIAL_JOB_SITE);
         BrainUtils.setMemory(merchant, MemoryModuleType.JOB_SITE, potential);
         level.broadcastEntityEvent(merchant, this.eventByte);
         if (merchant.getProfession() == VillagerProfession.NONE) {
            ServerLevel poiLevel = level.getServer().getLevel(potential.dimension());
            if (poiLevel != null) {
               poiLevel.getPoiManager()
                  .getType(potential.pos())
                  .flatMap(poiHolder -> BuiltInRegistries.VILLAGER_PROFESSION.stream().filter(p -> p.heldJobSite().test(poiHolder)).findFirst())
                  .ifPresent(profession -> {
                     merchant.setProfession(profession);
                     merchant.onSetProfession(profession);
                  });
            }
         }
      }
   }

   protected boolean canStillUse(ServerLevel level, TensuraMerchantEntity TensuraMerchantEntity, long gameTime) {
      return false;
   }
}
