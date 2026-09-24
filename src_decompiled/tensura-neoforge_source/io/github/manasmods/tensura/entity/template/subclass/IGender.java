package io.github.manasmods.tensura.entity.template.subclass;

public interface IGender {
   boolean isMale();

   default boolean isFemale() {
      return !this.isMale();
   }
}
