package dev.xkmc.l2hostility.content.traits.base;

import dev.xkmc.l2core.init.reg.registrate.NamedEntry;
import dev.xkmc.l2core.util.ServerProxy;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Attack;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Defence;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Offence;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.OffenceMax;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.config.TraitConfig;
import dev.xkmc.l2hostility.content.config.TraitExclusion;
import dev.xkmc.l2hostility.content.logic.InheritContext;
import dev.xkmc.l2hostility.content.logic.TraitEffectCache;
import dev.xkmc.l2hostility.content.logic.TraitManager;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHDamageTypes;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class MobTrait extends NamedEntry<MobTrait> implements ItemLike {
   private final IntSupplier color;

   public MobTrait(ChatFormatting format) {
      this(format::getColor);
   }

   public MobTrait(IntSupplier color) {
      super(LHTraits.TRAITS);
      this.color = color;
   }

   public TraitConfig getConfig(RegistryAccess access) {
      TraitConfig ans = (TraitConfig)LHTraits.DATA.get(access, this.holder());
      return ans == null ? TraitConfig.DEFAULT : ans;
   }

   public TraitExclusion getExclusion(RegistryAccess access) {
      TraitExclusion ans = (TraitExclusion)LHTraits.EXCLUSION.get(access, this.holder());
      return ans == null ? TraitExclusion.DEFAULT : ans;
   }

   public int getCost(RegistryAccess access, double factor) {
      return Math.max(1, (int)Math.round(this.getConfig(access).cost() * factor));
   }

   public int getMaxLevel(RegistryAccess access) {
      return this.getConfig(access).max_rank();
   }

   @Deprecated
   public TraitConfig getConfig() {
      return this.getConfig(ServerProxy.getRegistryAccess());
   }

   @Deprecated
   public int getMaxLevel() {
      return this.getConfig().max_rank();
   }

   public boolean allow(LivingEntity le, int difficulty, int maxModLv) {
      if (this.isBanned()) {
         return false;
      } else {
         TraitConfig config = this.getConfig(le.level().registryAccess());
         if (difficulty < config.min_level()) {
            return false;
         } else {
            return !EntityConfig.allow(le.getType(), this) ? false : config.allows(this.getRegistryName(), le.getType());
         }
      }
   }

   public final boolean allow(LivingEntity le) {
      return this.allow(le, Integer.MAX_VALUE, TraitManager.getMaxLevel() + 1);
   }

   public void initialize(LivingEntity mob, int level) {
   }

   public void postInit(LivingEntity mob, int lv) {
   }

   public void tick(LivingEntity mob, int level) {
   }

   public void onHurtTarget(int level, LivingEntity attacker, Offence e, TraitEffectCache traitCache) {
   }

   public void onHurtTargetMax(int level, LivingEntity attacker, OffenceMax e, TraitEffectCache traitCache) {
      if (e.getDamageOriginal() > 0.0F && !e.getSource().is(LHDamageTypes.KILLER_AURA)) {
         this.postHurtPlayer(level, attacker, traitCache);
      }
   }

   public void postHurtPlayer(int level, LivingEntity attacker, TraitEffectCache traitCache) {
      if (traitCache.reflectTrait(this)) {
         for (Mob e : traitCache.getTargets()) {
            this.postHurtImpl(level, attacker, e);
         }
      } else {
         this.postHurtImpl(level, attacker, traitCache.target);
      }
   }

   public void postHurtImpl(int level, LivingEntity attacker, LivingEntity target) {
   }

   public boolean onAttackedByOthers(int level, LivingEntity entity, Attack event) {
      return false;
   }

   public void onDamaged(int level, LivingEntity entity, Defence event) {
   }

   public void onCreateSource(int level, LivingEntity attacker, CreateSourceEvent event) {
   }

   public void onHurtByMax(int level, LivingEntity mob, OffenceMax cache) {
   }

   public void onDeath(int level, LivingEntity entity, LivingDeathEvent event) {
   }

   public MutableComponent getFullDesc(@Nullable Integer value) {
      MutableComponent ans = this.getDesc();
      if (value != null) {
         ans = ans.append(CommonComponents.SPACE).append(Component.translatable("enchantment.level." + value));
      }

      return ans.withStyle(Style.EMPTY.withColor(this.color.getAsInt()));
   }

   public int getColor() {
      return this.color.getAsInt();
   }

   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(Component.translatable(this.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
   }

   protected MutableComponent mapLevel(RegistryAccess access, Function<Integer, MutableComponent> func) {
      MutableComponent comp = null;

      for (int i = 1; i <= this.getMaxLevel(access); i++) {
         if (comp == null) {
            comp = func.apply(i);
         } else {
            comp = comp.append(Component.literal("/").withStyle(ChatFormatting.GRAY)).append((Component)func.apply(i));
         }
      }

      assert comp != null;
      return comp;
   }

   public Item asItem() {
      return (Item)BuiltInRegistries.ITEM.get(this.getRegistryName());
   }

   public boolean isBanned() {
      if (!(LHConfig.SERVER.getSpec() instanceof ModConfigSpec spec && spec.isLoaded())) {
         return false;
      } else {
         return LHConfig.SERVER.map.containsKey(this.getRegistryName().getPath())
            ? !(Boolean)LHConfig.SERVER.map.get(this.getRegistryName().getPath()).get()
            : false;
      }
   }

   public String toString() {
      return this.getID();
   }

   public int inherited(MobTraitCap mobTraitCap, int rank, InheritContext ctx) {
      return rank;
   }

   public boolean is(TagKey<MobTrait> tag) {
      return this.holder().is(tag);
   }

   public double modifyBonusDamage(DamageSource source, double factor, int lv) {
      return 1.0;
   }
}
