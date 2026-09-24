package dev.xkmc.l2hostility.content.config;

import dev.xkmc.l2core.serial.config.BaseConfig;
import dev.xkmc.l2core.serial.config.CollectType;
import dev.xkmc.l2core.serial.config.ConfigCollect;
import dev.xkmc.l2core.serial.configval.BooleanValueCondition;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

@SerialClass
public class WeaponConfig extends BaseConfig {
   @SerialField
   @ConfigCollect(CollectType.COLLECT)
   public final ArrayList<WeaponConfig.ItemConfig> melee_weapons = new ArrayList<>();
   @SerialField
   @ConfigCollect(CollectType.COLLECT)
   public final ArrayList<WeaponConfig.ItemConfig> armors = new ArrayList<>();
   @SerialField
   @ConfigCollect(CollectType.COLLECT)
   public final ArrayList<WeaponConfig.ItemConfig> ranged_weapons = new ArrayList<>();
   @SerialField
   @ConfigCollect(CollectType.MAP_COLLECT)
   public final LinkedHashMap<HolderSet<EntityType<?>>, ArrayList<WeaponConfig.ItemConfig>> special_weapons = new LinkedHashMap<>();
   @SerialField
   @ConfigCollect(CollectType.COLLECT)
   public final ArrayList<WeaponConfig.EnchConfig> weapon_enchantments = new ArrayList<>();
   @SerialField
   @ConfigCollect(CollectType.COLLECT)
   public final ArrayList<WeaponConfig.EnchConfig> armor_enchantments = new ArrayList<>();

   public static ItemStack getRandomMeleeWeapon(int level, RandomSource r, @Nullable ServerPlayer player) {
      WeaponConfig config = (WeaponConfig)L2Hostility.WEAPON.getMerged();
      return getRandomWeapon(config.melee_weapons, level, r, player);
   }

   public static ItemStack getRandomArmor(EquipmentSlot slot, int level, RandomSource r, @Nullable ServerPlayer player) {
      WeaponConfig config = (WeaponConfig)L2Hostility.WEAPON.getMerged();
      return getRandomArmors(slot, config.armors, level, r, player);
   }

   public static ItemStack getRandomRangedWeapon(int level, RandomSource r, @Nullable ServerPlayer player) {
      WeaponConfig config = (WeaponConfig)L2Hostility.WEAPON.getMerged();
      return getRandomWeapon(config.ranged_weapons, level, r, player);
   }

   public static ItemStack getRandomWeapon(ArrayList<WeaponConfig.ItemConfig> entries, int level, RandomSource r, @Nullable ServerPlayer player) {
      int total = 0;
      List<WeaponConfig.ItemConfig> list = new ArrayList<>();

      for (WeaponConfig.ItemConfig e : entries) {
         if (e.test(level, player)) {
            list.add(e);
            total += e.weight();
         }
      }

      if (total == 0) {
         return ItemStack.EMPTY;
      }

      int val = r.nextInt(total);

      for (WeaponConfig.ItemConfig e : list) {
         val -= e.weight();
         if (val <= 0) {
            return e.stack.get(r.nextInt(e.stack.size())).copy();
         }
      }

      return ItemStack.EMPTY;
   }

   private static ItemStack getRandomArmors(
      EquipmentSlot slot, ArrayList<WeaponConfig.ItemConfig> entries, int level, RandomSource r, @Nullable ServerPlayer player
   ) {
      int total = 0;
      List<WeaponConfig.ItemConfig> list = new ArrayList<>();

      for (WeaponConfig.ItemConfig e : entries) {
         if (e.test(level, player)) {
            ArrayList<ItemStack> sub = new ArrayList<>();

            for (ItemStack item : e.stack) {
               if (item.isEmpty() || item.getItem() instanceof ArmorItem eq && eq.getEquipmentSlot() == slot || item.getEquipmentSlot() == slot) {
                  sub.add(item);
               }
            }

            if (!sub.isEmpty()) {
               list.add(new WeaponConfig.ItemConfig(sub, e.level, e.weight));
               total += e.weight();
            }
         }
      }

      if (total == 0) {
         return ItemStack.EMPTY;
      }

      int val = r.nextInt(total);

      for (WeaponConfig.ItemConfig e : list) {
         val -= e.weight();
         if (val <= 0) {
            return e.stack.get(r.nextInt(e.stack.size())).copy();
         }
      }

      return ItemStack.EMPTY;
   }

   public WeaponConfig putMeleeWeapon(int level, int weight, Item... items) {
      ArrayList<ItemStack> list = new ArrayList<>();

      for (Item e : items) {
         list.add(e.getDefaultInstance());
      }

      this.melee_weapons.add(new WeaponConfig.ItemConfig(list, level, weight));
      return this;
   }

   public WeaponConfig putArmor(int level, int weight, Item... items) {
      ArrayList<ItemStack> list = new ArrayList<>();

      for (Item e : items) {
         list.add(e.getDefaultInstance());
      }

      this.armors.add(new WeaponConfig.ItemConfig(list, level, weight));
      return this;
   }

   public WeaponConfig putRangedWeapon(int level, int weight, Item... items) {
      ArrayList<ItemStack> list = new ArrayList<>();

      for (Item e : items) {
         list.add(e.getDefaultInstance());
      }

      this.ranged_weapons.add(new WeaponConfig.ItemConfig(list, level, weight));
      return this;
   }

   @SafeVarargs
   public final WeaponConfig putWeaponEnch(int level, float chance, ResourceKey<Enchantment>... items) {
      ArrayList<ResourceLocation> list = new ArrayList<>(Stream.of(items).map(ResourceKey::location).toList());
      this.weapon_enchantments.add(new WeaponConfig.EnchConfig(list, level, chance));
      return this;
   }

   @SafeVarargs
   public final WeaponConfig putArmorEnch(int level, float chance, ResourceKey<Enchantment>... items) {
      ArrayList<ResourceLocation> list = new ArrayList<>(Stream.of(items).map(ResourceKey::location).toList());
      this.armor_enchantments.add(new WeaponConfig.EnchConfig(list, level, chance));
      return this;
   }

   public record EnchConfig(ArrayList<ResourceLocation> enchantments, int level, float chance) {
   }

   public record ItemCondition(ArrayList<ResourceLocation> advancements, @Nullable BooleanValueCondition config) {
      public boolean test(@Nullable ServerPlayer sp) {
         if (this.config != null && !this.config.test(null)) {
            return false;
         }

         if (!this.advancements.isEmpty()) {
            if (sp == null) {
               return false;
            }

            MinecraftServer server = sp.level().getServer();
            if (server == null) {
               return false;
            }

            ServerAdvancementManager manager = server.getAdvancements();
            PlayerAdvancements spAdv = sp.getAdvancements();

            for (ResourceLocation e : this.advancements) {
               AdvancementHolder adv = manager.get(e);
               if (adv == null) {
                  return false;
               }

               if (!spAdv.getOrStartProgress(adv).isDone()) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public record ItemConfig(ArrayList<ItemStack> stack, int level, int weight, @Nullable WeaponConfig.ItemCondition condition) {
      public static final WeaponConfig.ItemConfig EMPTY = new WeaponConfig.ItemConfig(new ArrayList<>(List.of(ItemStack.EMPTY)), 0, 1000);

      public ItemConfig(ArrayList<ItemStack> stack, int level, int weight) {
         this(stack, level, weight, null);
      }

      public boolean test(int lv, @Nullable ServerPlayer player) {
         return lv < this.level() ? false : this.condition == null || this.condition.test(player);
      }
   }
}
