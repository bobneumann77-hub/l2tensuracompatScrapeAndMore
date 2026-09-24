package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.tensura.entity.template.TensuraPartEntity;

public interface IMultipart {
   default boolean shouldCreateParts() {
      return true;
   }

   TensuraPartEntity[] getParts();
}
