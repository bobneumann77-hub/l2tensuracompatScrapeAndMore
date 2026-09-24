package io.github.manasmods.tensura;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.injectables.annotations.ExpectPlatform.Transformed;
import io.github.manasmods.tensura.neoforge.TensuraAttributeRegisterImpl;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attribute.Sentiment;
import org.jetbrains.annotations.NotNull;

public class TensuraAttributeRegister {
   @ExpectPlatform
   @NotNull
   @Transformed
   public static Holder<Attribute> registerPlayerAttribute(
      String modID, String id, String name, double amount, double min, double max, boolean syncable, Sentiment sentiment
   ) {
      return TensuraAttributeRegisterImpl.registerPlayerAttribute(modID, id, name, amount, min, max, syncable, sentiment);
   }

   @ExpectPlatform
   @NotNull
   @Transformed
   public static Holder<Attribute> registerGenericAttribute(
      String modID, String id, String name, double amount, double min, double max, boolean syncable, Sentiment sentiment
   ) {
      return TensuraAttributeRegisterImpl.registerGenericAttribute(modID, id, name, amount, min, max, syncable, sentiment);
   }
}
