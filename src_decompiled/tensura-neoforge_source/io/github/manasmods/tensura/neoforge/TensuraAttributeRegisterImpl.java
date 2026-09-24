package io.github.manasmods.tensura.neoforge;

import io.github.manasmods.manascore.attribute.neoforge.ManasCoreAttributeRegisterImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute.Sentiment;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class TensuraAttributeRegisterImpl {
   public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, "tensura");

   @NotNull
   public static Holder<Attribute> registerPlayerAttribute(
      String modID, String id, String name, double amount, double min, double max, boolean syncable, Sentiment sentiment
   ) {
      Attribute attribute = new RangedAttribute(name, amount, min, max).setSyncable(syncable).setSentiment(sentiment);
      return ManasCoreAttributeRegisterImpl.registerToPlayers(ATTRIBUTES.register(id, () -> attribute));
   }

   @NotNull
   public static Holder<Attribute> registerGenericAttribute(
      String modID, String id, String name, double amount, double min, double max, boolean syncable, Sentiment sentiment
   ) {
      Attribute attribute = new RangedAttribute(name, amount, min, max).setSyncable(syncable).setSentiment(sentiment);
      return ManasCoreAttributeRegisterImpl.registerToGeneric(ATTRIBUTES.register(id, () -> attribute));
   }
}
