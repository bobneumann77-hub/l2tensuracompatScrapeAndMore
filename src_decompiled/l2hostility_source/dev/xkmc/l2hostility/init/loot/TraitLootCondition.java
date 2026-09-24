package dev.xkmc.l2hostility.init.loot;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

@SerialClass
public class TraitLootCondition implements LootItemCondition {
   @SerialField
   public MobTrait trait;
   @SerialField
   public int minLevel;
   @SerialField
   public int maxLevel;

   @Deprecated
   public TraitLootCondition() {
   }

   public TraitLootCondition(MobTrait trait, int minLevel, int maxLevel) {
      this.trait = trait;
      this.minLevel = minLevel;
      this.maxLevel = maxLevel;
   }

   public LootItemConditionType getType() {
      return (LootItemConditionType)TraitGLMProvider.TRAIT_AND_LEVEL.get();
   }

   public boolean test(LootContext lootContext) {
      if (lootContext.getParam(LootContextParams.THIS_ENTITY) instanceof LivingEntity le) {
         Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(le);
         if (opt.isPresent()) {
            MobTraitCap cap = opt.get();
            if (!cap.hasTrait(this.trait)) {
               return false;
            }

            int lv = cap.getTraitLevel(this.trait);
            return lv >= this.minLevel && lv <= this.maxLevel;
         }
      }

      return false;
   }
}
