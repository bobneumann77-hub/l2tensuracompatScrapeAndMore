package io.github.manasmods.tensura.util;

import io.github.manasmods.tensura.mixin.accessor.AccessorGameRuleCategory;
import io.github.manasmods.tensura.mixin.accessor.AccessorSoundSource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.GameRules.Category;
import sun.misc.Unsafe;

public class TensuraEnumHelper {
   public static Category createGameRuleCategory(String enumConstant, String name) {
      Category[] values = AccessorGameRuleCategory.getValues();
      int lastOrdinal = values[values.length - 1].ordinal();
      List<Category> valuesMutable = new ArrayList<>(Arrays.stream(values).toList());
      Category variant = AccessorGameRuleCategory.create(enumConstant, lastOrdinal + 1, name);
      valuesMutable.add(variant);
      Category[] newValues = valuesMutable.toArray(new Category[0]);
      AccessorGameRuleCategory.setValues(newValues);
      clearEnumCache(Category.class);
      return variant;
   }

   public static SoundSource createSoundSource(String enumConstant, String name) {
      SoundSource[] values = AccessorSoundSource.getValues();
      int lastOrdinal = values[values.length - 1].ordinal();
      List<SoundSource> valuesMutable = new ArrayList<>(Arrays.stream(values).toList());
      SoundSource variant = AccessorSoundSource.create(enumConstant, lastOrdinal + 1, name);
      valuesMutable.add(variant);
      SoundSource[] newValues = valuesMutable.toArray(new SoundSource[0]);
      AccessorSoundSource.setValues(newValues);
      clearEnumCache(SoundSource.class);
      return variant;
   }

   private static void clearEnumCache(Class<?> enumClass) {
      clearClassField(enumClass, "enumConstants");
      clearClassField(enumClass, "enumConstantDirectory");
   }

   private static void clearClassField(Class<?> enumClass, String fieldName) {
      try {
         Field field = Class.class.getDeclaredField(fieldName);
         field.setAccessible(true);
         field.set(enumClass, null);
      } catch (Throwable reflective) {
         try {
            Unsafe unsafe = getUnsafe();
            Field field = Class.class.getDeclaredField(fieldName);
            long offset = unsafe.objectFieldOffset(field);
            unsafe.putObject(enumClass, offset, null);
         } catch (Throwable var7) {
         }
      }
   }

   private static Unsafe getUnsafe() throws ReflectiveOperationException {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      return (Unsafe)f.get(null);
   }
}
