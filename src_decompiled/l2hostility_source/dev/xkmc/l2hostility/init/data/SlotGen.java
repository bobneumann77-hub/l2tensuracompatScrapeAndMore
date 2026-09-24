package dev.xkmc.l2hostility.init.data;

import dev.xkmc.l2hostility.compat.data.CataclysmData;
import dev.xkmc.l2hostility.init.L2Hostility;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosDataProvider;

public class SlotGen extends CuriosDataProvider {
   public SlotGen(String modId, PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<Provider> registries) {
      super(modId, output, fileHelper, registries);
   }

   public void generate(Provider ovd, ExistingFileHelper helper) {
      this.createSlot("hostility_curse").icon(L2Hostility.loc("slot/empty_hostility_slot")).order(131);
      this.createEntities("hostility_entity")
         .addEntities(new EntityType[]{EntityType.PLAYER})
         .addSlots(new String[]{"head", "charm", "ring", "hands", "hostility_curse"});
      if (ModList.get().isLoaded("cataclysm")) {
         CataclysmData.genSlot(this);
      }
   }
}
