package io.github.manasmods.tensura.client.entity.misc;

import io.github.manasmods.tensura.entity.magic.misc.WarpPortalEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WarpPortalModel extends DefaultedEntityGeoModel<WarpPortalEntity> {
   public WarpPortalModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "warp_portal"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }
}
