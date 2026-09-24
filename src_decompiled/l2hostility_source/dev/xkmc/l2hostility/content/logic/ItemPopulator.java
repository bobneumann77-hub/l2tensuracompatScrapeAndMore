package dev.xkmc.l2hostility.content.logic;

import dev.shadowsoffire.apotheosis.Apoth.Components;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.compat.curios.EntitySlotAccess;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.config.WeaponConfig;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.mob_weapon_api.example.vanilla.VanillaMobManager;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public class ItemPopulator {
   static void populateArmors(LivingEntity le, int lv) {
      if (!isApothBoss(le)) {
         RandomSource r = le.getRandom();
         ServerPlayer sp = PlayerFinder.getNearestPlayer(le.level(), le) instanceof ServerPlayer player ? player : null;

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == Type.HUMANOID_ARMOR && le.getItemBySlot(slot).isEmpty()) {
               ItemStack stack = WeaponConfig.getRandomArmor(slot, lv, r, sp);
               if (!stack.isEmpty()) {
                  le.setItemSlot(slot, stack);
                  if (le instanceof Mob mob) {
                     mob.setDropChance(slot, ((Double)LHConfig.SERVER.equipmentDropRate.get()).floatValue());
                  }
               }
            }
         }
      }
   }

   static void populateWeapons(LivingEntity le, MobTraitCap cap, RandomSource r, @Nullable ServerPlayer sp) {
      if (!isApothBoss(le) && !isApothWeapon(le.getMainHandItem())) {
         if (le instanceof Drowned && le.getMainHandItem().isEmpty()) {
            double factor = cap.getLevel() * (Double)LHConfig.SERVER.drownedTridentChancePerLevel.get();
            if (factor > le.getRandom().nextDouble()) {
               le.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TRIDENT));
            }
         }

         if (le.getType().is(LHTagGen.MELEE_WEAPON_TARGET) && le.getMainHandItem().isEmpty()) {
            ItemStack stack = WeaponConfig.getRandomMeleeWeapon(cap.getLevel(), r, sp);
            if (!stack.isEmpty()) {
               le.setItemSlot(EquipmentSlot.MAINHAND, stack);
               if (le instanceof Mob mob) {
                  mob.setDropChance(EquipmentSlot.MAINHAND, ((Double)LHConfig.SERVER.equipmentDropRate.get()).floatValue());
               }
            }
         }

         if (le.getType().is(LHTagGen.RANGED_WEAPON_TARGET)) {
            ItemStack stack = WeaponConfig.getRandomRangedWeapon(cap.getLevel(), r, sp);
            if (!stack.isEmpty()) {
               le.setItemSlot(EquipmentSlot.MAINHAND, stack);
               if (le instanceof Mob mob) {
                  mob.setDropChance(EquipmentSlot.MAINHAND, ((Double)LHConfig.SERVER.equipmentDropRate.get()).floatValue());
               }
            }
         }

         ArrayList<WeaponConfig.ItemConfig> list = new ArrayList<>();

         for (Entry<HolderSet<EntityType<?>>, ArrayList<WeaponConfig.ItemConfig>> ent : ((WeaponConfig)L2Hostility.WEAPON.getMerged())
            .special_weapons
            .entrySet()) {
            if (le.getType().is(ent.getKey())) {
               for (WeaponConfig.ItemConfig e : ent.getValue()) {
                  boolean nonEmpty = false;

                  for (ItemStack stack : e.stack()) {
                     nonEmpty |= !stack.isEmpty();
                  }

                  if (nonEmpty) {
                     list.add(e);
                  }
               }
            }
         }

         if (!list.isEmpty()) {
            list.add(WeaponConfig.ItemConfig.EMPTY);
            ItemStack stack = WeaponConfig.getRandomWeapon(list, cap.getLevel(), le.getRandom(), sp);
            if (!stack.isEmpty()) {
               le.setItemSlot(EquipmentSlot.MAINHAND, stack);
               if (le instanceof PathfinderMob e && VanillaMobManager.attachGoal(e, stack)) {
                  e.addTag("mob_weapon_api_applied");
               }

               if (le instanceof Mob mob) {
                  mob.setDropChance(EquipmentSlot.MAINHAND, ((Double)LHConfig.SERVER.equipmentDropRate.get()).floatValue());
               }
            }
         }
      }
   }

   static void generateItems(MobTraitCap cap, LivingEntity le, EntityConfig.ItemPool pool) {
      if (cap.getLevel() >= pool.level()) {
         if (!(le.getRandom().nextFloat() > pool.chance())) {
            EntitySlotAccess slot = CurioCompat.decode(pool.slot(), le);
            if (slot != null) {
               ArrayList<EntityConfig.ItemEntry> list = pool.entries();
               int total = 0;

               for (EntityConfig.ItemEntry e : list) {
                  total += e.weight();
               }

               if (total > 0) {
                  total = le.getRandom().nextInt(total);

                  for (EntityConfig.ItemEntry e : list) {
                     total -= e.weight();
                     if (total <= 0) {
                        slot.set(e.stack().copy());
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   public static void fillEnch(RegistryAccess access, int level, RandomSource source, ItemStack stack, EquipmentSlot slot) {
      if (!isApothWeapon(stack)) {
         WeaponConfig config = (WeaponConfig)L2Hostility.WEAPON.getMerged();
         if (slot != EquipmentSlot.OFFHAND) {
            ArrayList<WeaponConfig.EnchConfig> list = slot == EquipmentSlot.MAINHAND ? config.weapon_enchantments : config.armor_enchantments;
            Mutable map = new Mutable(stack.getAllEnchantments(access.lookupOrThrow(Registries.ENCHANTMENT)));

            for (WeaponConfig.EnchConfig e : list) {
               int elv = e.level() <= 0 ? 1 : e.level();
               if (elv <= level) {
                  for (ResourceLocation key : e.enchantments()) {
                     if (!(e.chance() < source.nextDouble())) {
                        Holder<Enchantment> holder = access.holderOrThrow(ResourceKey.create(Registries.ENCHANTMENT, key));
                        if (stack.isPrimaryItemFor(holder) && isValid(map.keySet(), holder)) {
                           int max = Math.min(level / elv, ((Enchantment)holder.value()).getMaxLevel());
                           map.set(holder, Math.max(max, map.getLevel(holder)));
                        }
                     }
                  }
               }
            }

            EnchantmentHelper.setEnchantments(stack, map.toImmutable());
         }
      }
   }

   private static boolean isValid(Set<Holder<Enchantment>> old, Holder<Enchantment> ench) {
      for (Holder<Enchantment> other : old) {
         if (ench.equals(other)) {
            return true;
         }
      }

      for (Holder<Enchantment> other : old) {
         if (!Enchantment.areCompatible(ench, other)) {
            return false;
         }
      }

      return true;
   }

   public static void postFill(MobTraitCap cap, LivingEntity le) {
      if ((Boolean)LHConfig.SERVER.enableEquipmentDatapack.get()) {
         ServerPlayer sp = PlayerFinder.getNearestPlayer(le.level(), le) instanceof ServerPlayer player ? player : null;
         RandomSource r = le.getRandom();
         populateWeapons(le, cap, r, sp);

         for (EquipmentSlot e : EquipmentSlot.values()) {
            ItemStack stack = le.getItemBySlot(e);
            if (stack.isEnchantable()) {
               if ((Boolean)LHConfig.SERVER.allowExtraEnchantments.get()) {
                  fillEnch(le.level().registryAccess(), cap.getLevel(), le.getRandom(), stack, e);
               }

               le.setItemSlot(e, stack);
            }
         }

         EntityConfig.Config config = cap.getConfigCache(le);
         if (config != null && !config.items.isEmpty()) {
            for (EntityConfig.ItemPool pool : config.items) {
               generateItems(cap, le, pool);
            }
         }
      }
   }

   private static boolean isApothBoss(LivingEntity mob) {
      return !ModList.get().isLoaded("apotheosis") ? false : mob.getPersistentData().getBoolean("apoth.boss");
   }

   private static boolean isApothWeapon(ItemStack stack) {
      return !ModList.get().isLoaded("apotheosis") ? false : (Boolean)stack.getOrDefault(Components.FROM_BOSS, false);
   }
}
