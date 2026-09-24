package io.github.manasmods.tensura.entity.magic.breath;

import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

public class BreathPart extends TensuraPartEntity {
   public final String id;
   private final EntityDimensions size;

   public BreathPart(BreathEntity breath, String name, float width, float height) {
      super(breath);
      this.id = name;
      this.size = EntityDimensions.scalable(width, height);
      this.refreshDimensions();
   }

   @Override
   public EntityDimensions getDimensions(Pose pose) {
      return this.size;
   }
}
