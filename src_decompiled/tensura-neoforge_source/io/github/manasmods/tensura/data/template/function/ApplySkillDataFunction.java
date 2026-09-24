package io.github.manasmods.tensura.data.template.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.registry.data.TensuraLootFunctions;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplySkillDataFunction extends LootItemConditionalFunction {
   public static final MapCodec<ApplySkillDataFunction> CODEC = RecordCodecBuilder.mapCodec(
      instance -> commonFields(instance)
         .and(
            instance.group(
               ResourceLocation.CODEC.optionalFieldOf("skill").forGetter(arg -> arg.skill),
               TagKey.codec(SkillAPI.getSkillRegistry().key()).optionalFieldOf("tag").forGetter(arg -> arg.tag)
            )
         )
         .apply(instance, ApplySkillDataFunction::new)
   );
   private final Optional<ResourceLocation> skill;
   private final Optional<TagKey<ManasSkill>> tag;

   ApplySkillDataFunction(List<LootItemCondition> list, Optional<ResourceLocation> skill, Optional<TagKey<ManasSkill>> tag) {
      super(list);
      this.skill = skill;
      this.tag = tag;
   }

   public LootItemFunctionType<ApplySkillDataFunction> getType() {
      return (LootItemFunctionType<ApplySkillDataFunction>)TensuraLootFunctions.APPLY_SKILL_DATA.get();
   }

   public ItemStack run(ItemStack itemStack, LootContext context) {
      ItemStack stack = itemStack.copy();
      RandomSource randomSource = context.getRandom();
      if (this.skill.isPresent()) {
         stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), this.skill.get());
      } else if (this.tag.isPresent()) {
         RegistryAccess registryAccess = context.getLevel().registryAccess();
         Optional<Named<ManasSkill>> optional = registryAccess.registryOrThrow(SkillAPI.getSkillRegistry().key()).getTag(this.tag.get());
         if (optional.isPresent()) {
            List<Holder<ManasSkill>> random = optional.get().stream().toList();
            stack.set(
               (DataComponentType)TensuraDataComponents.SKILL.get(), ((ManasSkill)random.get(randomSource.nextInt(random.size())).value()).getRegistryName()
            );
         }
      }

      return stack;
   }

   public static ApplySkillDataFunction.Builder apply(ResourceLocation tag) {
      return new ApplySkillDataFunction.Builder(tag);
   }

   public static ApplySkillDataFunction.Builder applyTag(TagKey<ManasSkill> tag) {
      return new ApplySkillDataFunction.Builder(tag);
   }

   public static class Builder extends net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction.Builder<ApplySkillDataFunction.Builder> {
      private Optional<TagKey<ManasSkill>> tag = Optional.empty();
      private Optional<ResourceLocation> skill = Optional.empty();

      public Builder(TagKey<ManasSkill> arg) {
         this.tag = Optional.of(arg);
      }

      public Builder(ResourceLocation arg) {
         this.skill = Optional.of(arg);
      }

      public ApplySkillDataFunction build() {
         return new ApplySkillDataFunction(this.getConditions(), this.skill, this.tag);
      }

      protected ApplySkillDataFunction.Builder getThis() {
         return this;
      }
   }
}
