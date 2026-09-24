package io.github.manasmods.tensura.storage.spirit;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import org.jetbrains.annotations.Nullable;

public interface ISpiritWielder {
   int getSpiritCooldown();

   void setSpiritCooldown(int var1);

   int getSpiritLevelId(Element var1);

   @Nullable
   SpiritualMagic.SpiritLevel getSpiritLevel(Element var1);

   boolean setSpiritLevel(Element var1, SpiritualMagic.SpiritLevel var2);

   void clearSpiritLevel();

   boolean isColossusStarted();

   void setColossusStarted(boolean var1);

   boolean isColossusPassed();

   void setColossusPassed(boolean var1);

   boolean isColossusWon();

   void setColossusWon(boolean var1);

   void markDirty();
}
