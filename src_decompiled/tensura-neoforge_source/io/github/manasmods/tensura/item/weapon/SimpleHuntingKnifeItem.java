package io.github.manasmods.tensura.item.weapon;

import lombok.Generated;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;

public class SimpleHuntingKnifeItem extends TensuraSwordItem {
   protected int bonusLootLevel = 0;

   public SimpleHuntingKnifeItem(Tier pTier, int bonusLootLevel, Properties properties) {
      super(pTier, 0, -2.0F, -1.0, -1.0, 0.0, 0.0, properties);
      this.setBonusLootLevel(bonusLootLevel);
   }

   @Generated
   public int getBonusLootLevel() {
      return this.bonusLootLevel;
   }

   @Generated
   public void setBonusLootLevel(int bonusLootLevel) {
      this.bonusLootLevel = bonusLootLevel;
   }
}
