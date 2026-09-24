package dev.xkmc.l2hostility.init.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.capability.player.PlayerCapabilityHolder;
import dev.xkmc.l2hostility.compat.jei.ITraitLootRecipe;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.capability.player.PlayerDifficulty;
import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.l2library.util.GenericItemStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TraitLootModifier extends LootModifier implements ITraitLootRecipe {
   public static final MapCodec<TraitLootModifier> CODEC = RecordCodecBuilder.mapCodec(
      i -> codecStart(i)
         .and(
            i.group(
               LHTraits.TRAITS.get().byNameCodec().optionalFieldOf("trait").forGetter(e -> Optional.ofNullable(e.trait)),
               Codec.DOUBLE.fieldOf("chance").forGetter(e -> e.chance),
               Codec.DOUBLE.fieldOf("rankBonus").forGetter(e -> e.rankBonus),
               ItemStack.CODEC.fieldOf("result").forGetter(e -> e.result)
            )
         )
         .apply(i, TraitLootModifier::new)
   );
   @Nullable
   public final MobTrait trait;
   public final double chance;
   public final double rankBonus;
   public final ItemStack result;

   public TraitLootModifier(MobTrait trait, double chance, double rankBonus, ItemStack result, LootItemCondition... conditionsIn) {
      super(conditionsIn);
      this.trait = trait;
      this.chance = chance;
      this.rankBonus = rankBonus;
      this.result = result;
   }

   private TraitLootModifier(LootItemCondition[] conditionsIn, Optional<MobTrait> trait, double chance, double rankBonus, ItemStack result) {
      super(conditionsIn);
      this.trait = trait.orElse(null);
      this.chance = chance;
      this.rankBonus = rankBonus;
      this.result = result;
   }

   @NotNull
   protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> list, LootContext context) {
      if (context.getParam(LootContextParams.THIS_ENTITY) instanceof LivingEntity le) {
         Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(le);
         if (opt.isPresent() && !le.getType().is(LHTagGen.NO_DROP)) {
            MobTraitCap cap = opt.get();
            if (this.trait == null || cap.hasTrait(this.trait)) {
               double factor = cap.dropRate;
               if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
                  Player player = (Player)context.getParam(LootContextParams.LAST_DAMAGE_PLAYER);
                  PlayerDifficulty pl = (PlayerDifficulty)((PlayerCapabilityHolder)LHMiscs.PLAYER.type()).getOrCreate(player);

                  for (GenericItemStack<CurseCurioItem> stack : CurseCurioItem.getFromPlayer(player)) {
                     factor *= ((CurseCurioItem)stack.item()).getLootFactor(stack.stack(), pl, cap);
                  }
               }

               int lv = this.trait == null ? 0 : cap.getTraitLevel(this.trait);
               double rate = this.chance + lv * this.rankBonus;
               int count = 0;

               for (int i = 0; i < this.result.getCount() * factor; i++) {
                  if (context.getRandom().nextDouble() < rate) {
                     count++;
                  }
               }

               if (count > 0) {
                  ItemStack ans = this.result.copy();
                  if ((Boolean)LHConfig.SERVER.nidhoggurCapAtItemMaxStack.get()) {
                     count = Math.min(count, ans.getMaxStackSize());
                  }

                  ans.setCount(count);
                  list.add(ans);
               }
            }
         }
      }

      return list;
   }

   public MapCodec<TraitLootModifier> codec() {
      return CODEC;
   }

   public LootItemCondition[] getConditions() {
      return this.conditions;
   }

   @Override
   public List<ItemStack> getResults() {
      return List.of(this.result);
   }

   @Override
   public List<ItemStack> getCurioRequired() {
      List<ItemStack> ans = new ArrayList<>();
      if ((Boolean)LHConfig.SERVER.disableHostilityLootCurioRequirement.get()) {
         return ans;
      }

      for (LootItemCondition c : this.getConditions()) {
         if (c instanceof PlayerHasItemCondition item) {
            ans.add(item.item.getDefaultInstance());
         }
      }

      return ans;
   }

   @Override
   public List<ItemStack> getInputs() {
      Set<MobTrait> set = new LinkedHashSet<>();
      List<ItemStack> ans = new ArrayList<>();
      if (this.trait != null) {
         set.add(this.trait);
      }

      for (LootItemCondition c : this.getConditions()) {
         if (c instanceof TraitLootCondition cl) {
            set.add(cl.trait);
         }
      }

      for (MobTrait e : set) {
         ans.add(e.asItem().getDefaultInstance());
      }

      return ans;
   }

   @Override
   public boolean isValid() {
      if (this.trait == null) {
         return true;
      }

      if (this.trait.isBanned()) {
         return false;
      }

      int max = this.trait.getMaxLevel();

      for (LootItemCondition c : this.getConditions()) {
         if (c instanceof TraitLootCondition cl) {
            if (cl.trait.isBanned()) {
               return false;
            }

            if (cl.trait == this.trait && cl.minLevel > max) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public void addTooltip(Consumer<Component> list) {
      int max = this.trait == null ? 0 : this.trait.getConfig().max_rank();
      int min = 1;
      int minLevel = 0;
      List<TraitLootCondition> other = new ArrayList<>();
      List<PlayerHasItemCondition> itemReq = new ArrayList<>();
      List<MobHealthCondition> health = new ArrayList<>();

      for (LootItemCondition c : this.getConditions()) {
         if (c instanceof TraitLootCondition cl) {
            if (cl.trait == this.trait) {
               max = Math.min(max, cl.maxLevel);
               min = Math.max(min, cl.minLevel);
            } else {
               other.add(cl);
            }
         } else if (c instanceof MobCapLootCondition cl) {
            minLevel = cl.minLevel;
         } else if (c instanceof PlayerHasItemCondition cl) {
            itemReq.add(cl);
         } else if (c instanceof MobHealthCondition cl) {
            health.add(cl);
         }
      }

      if (minLevel > 0) {
         list.accept(LangData.LOOT_MIN_LEVEL.get(Component.literal(minLevel + "").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.LIGHT_PURPLE));
      }

      for (MobHealthCondition e : health) {
         list.accept(LangData.LOOT_MIN_HEALTH.get(Component.literal(e.minHealth + "").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.LIGHT_PURPLE));
      }

      if (this.trait != null) {
         for (int lv = min; lv <= max; lv++) {
            list.accept(
               LangData.LOOT_CHANCE
                  .get(
                     Component.literal(Math.round((this.chance + this.rankBonus * lv) * 100.0) + "%").withStyle(ChatFormatting.AQUA),
                     this.trait.getDesc().withStyle(ChatFormatting.GOLD),
                     Component.literal(lv + "").withStyle(ChatFormatting.AQUA)
                  )
                  .withStyle(ChatFormatting.GRAY)
            );
         }
      } else {
         list.accept(
            LangData.LOOT_NO_TRAIT.get(Component.literal(Math.round(this.chance * 100.0) + "%").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY)
         );
      }

      for (TraitLootCondition c : other) {
         int cmin = Math.max(c.minLevel, 1);
         int cmax = Math.min(c.maxLevel, c.trait.getMaxLevel());
         String str = cmax == cmin ? cmin + "" : (cmax >= c.trait.getMaxLevel() ? cmin + "+" : cmin + "-" + cmax);
         list.accept(
            LangData.LOOT_OTHER_TRAIT
               .get(c.trait.getDesc().withStyle(ChatFormatting.GOLD), Component.literal(str).withStyle(ChatFormatting.AQUA))
               .withStyle(ChatFormatting.RED)
         );
      }

      for (PlayerHasItemCondition e : itemReq) {
         MutableComponent name = e.item.getDescription().copy().withStyle(ChatFormatting.LIGHT_PURPLE);
         list.accept(LangData.TOOLTIP_JEI_REQUIRED.get(name).withStyle(ChatFormatting.YELLOW));
      }
   }
}
