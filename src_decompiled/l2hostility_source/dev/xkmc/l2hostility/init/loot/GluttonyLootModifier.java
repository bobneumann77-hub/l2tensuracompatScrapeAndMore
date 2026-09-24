package dev.xkmc.l2hostility.init.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2library.util.GenericItemStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class GluttonyLootModifier extends LootModifier {
   public static final MapCodec<GluttonyLootModifier> CODEC = RecordCodecBuilder.mapCodec(i -> codecStart(i).apply(i, GluttonyLootModifier::new));

   public GluttonyLootModifier(LootItemCondition... conditionsIn) {
      super(conditionsIn);
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> list, LootContext context) {
      if (context.getParam(LootContextParams.THIS_ENTITY) instanceof LivingEntity le) {
         Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(le);
         if (opt.isPresent() && !le.getType().is(LHTagGen.NO_DROP)) {
            MobTraitCap cap = opt.get();
            double factor = cap.dropRate;
            if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
               Player player = (Player)context.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
               PlayerDifficulty pl = (PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(player);

               for (GenericItemStack<CurseCurioItem> stack : CurseCurioItem.getFromPlayer(player)) {
                  factor *= ((CurseCurioItem)stack.item()).getLootFactor(stack.stack(), pl, cap);
               }
            }

            double chance = factor * cap.getLevel() * (Double)LHConfig.SERVER.gluttonyBottleDropRate.get();
            int base = (int)chance;
            if (context.getRandom().nextDouble() < chance - base) {
               base++;
            }

            if (base > 0) {
               list.add(new ItemStack((ItemLike)LHItems.BOTTLE_CURSE.get(), base));
            }
         }
      }

      return list;
   }

   public MapCodec<GluttonyLootModifier> codec() {
      return CODEC;
   }

   public LootItemCondition[] getConditions() {
      return this.conditions;
   }
}
