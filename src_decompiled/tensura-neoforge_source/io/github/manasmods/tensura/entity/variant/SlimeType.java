package io.github.manasmods.tensura.entity.variant;

import java.util.Arrays;
import java.util.Comparator;

public enum SlimeType {
   PRODUCE(0),
   MERGE(1),
   SUMMONED(2);

   private static final SlimeType[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(SlimeType::getId)).toArray(SlimeType[]::new);
   private final int id;

   SlimeType(int id) {
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   public static SlimeType byId(int id) {
      return BY_ID[id % BY_ID.length];
   }
}
