package io.github.manasmods.tensura.ability.magic.spiritual.necromancy;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

public class NecromancyMagic extends SpiritualMagic {
   public NecromancyMagic(SpiritualMagic.SpiritLevel level) {
      super(Element.UNIDENTIFIED, level);
   }

   @Nullable
   @Override
   public MutableComponent getColoredName() {
      MutableComponent name = super.getName();
      return name == null ? null : name.withColor(11141375);
   }
}
