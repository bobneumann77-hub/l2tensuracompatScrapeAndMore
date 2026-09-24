package io.github.manasmods.tensura.data.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public record BlockMagiculeModifier(BlockPos pos, int priority, List<DataPackMagiculeModifier> modifiers, double effectDistance) implements MagiculeModifier {
   public static final Codec<BlockMagiculeModifier> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(BlockMagiculeModifier::pos),
            Codec.intRange(0, 255).optionalFieldOf("priority", -1).forGetter(BlockMagiculeModifier::priority),
            DataPackMagiculeModifier.CODEC.listOf().optionalFieldOf("modifiers", new ArrayList()).forGetter(BlockMagiculeModifier::modifiers),
            Codec.doubleRange(0.0, 1024.0).optionalFieldOf("effectDistance", 16.0).forGetter(BlockMagiculeModifier::effectDistance)
         )
         .apply(instance, BlockMagiculeModifier::new)
   );

   @Override
   public double getMagicule(double oldMagicule) {
      for (DataPackMagiculeModifier modifier : this.modifiers) {
         switch (modifier.mode()) {
            case ADD:
               oldMagicule += modifier.value();
               break;
            case MULTIPLY:
               oldMagicule *= modifier.value();
         }
      }

      return oldMagicule;
   }

   @Override
   public int getPriority() {
      return this.priority;
   }

   public CompoundTag save(CompoundTag tag) {
      DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE, this);
      result.resultOrPartial().ifPresent(encodedTag -> {
         if (encodedTag instanceof CompoundTag compound) {
            tag.put("modifier", compound);
         } else {
            tag.put("modifier", encodedTag);
         }
      });
      return tag;
   }

   public static Optional<BlockMagiculeModifier> load(CompoundTag tag) {
      if (tag.contains("modifier", 10)) {
         Tag modifier = tag.get("modifier");
         DataResult<BlockMagiculeModifier> result = CODEC.parse(NbtOps.INSTANCE, modifier);
         return result.resultOrPartial();
      } else {
         return Optional.empty();
      }
   }

   public static BlockMagiculeModifier getSimpleMagiculeAddition(BlockPos pos, double addition, double distance) {
      return new BlockMagiculeModifier(pos, 1, List.of(new DataPackMagiculeModifier(DataPackMagiculeModifier.Mode.ADD, addition)), distance);
   }
}
