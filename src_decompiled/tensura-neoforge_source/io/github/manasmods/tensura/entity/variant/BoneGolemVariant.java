package io.github.manasmods.tensura.entity.variant;

import io.github.manasmods.manascore.attribute.api.ManasCoreAttributes;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.tensura.entity.monster.ArchDaemonEntity;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.util.AttributeHelper;
import java.util.Arrays;
import java.util.Comparator;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum BoneGolemVariant {
   DEFAULT(0, "default", 0.0, 20.0F, 0.0F, 0.0F, 1.0F, 1.0F, Blocks.BONE_BLOCK),
   LOW_MAGISTEEL,
   HIGH_MAGISTEEL,
   MITHRIL,
   PURE_MAGISTEEL,
   ORICHALCUM,
   ADAMANTITE,
   HIHIIROKANE;

   private static final BoneGolemVariant[] BY_ID = Arrays.stream(values())
      .sorted(Comparator.comparingInt(BoneGolemVariant::getId))
      .toArray(BoneGolemVariant[]::new);
   private final int id;
   private final String name;
   private final double EP;
   private final float HP;
   private final float armor;
   private final float knockbackResistance;
   private final float attack;
   private final float speed;
   private final Block block;
   private final ResourceLocation texture;

   BoneGolemVariant(int id, String location, double EP, float HP, float armor, float knockbackResistance, float attack, float speed, Block block) {
      this.id = id;
      this.name = location;
      this.EP = EP;
      this.HP = HP;
      this.armor = armor;
      this.knockbackResistance = knockbackResistance;
      this.attack = attack;
      this.speed = speed;
      this.block = block;
      this.texture = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/golem/bone/bone_golem_" + location + ".png");
   }

   public Builder getAttributeSupplier() {
      return AttributeSupplier.builder()
         .add(Attributes.MAX_HEALTH, this.HP)
         .add(Attributes.ARMOR, this.armor)
         .add(Attributes.KNOCKBACK_RESISTANCE, this.knockbackResistance)
         .add(Attributes.ATTACK_DAMAGE, this.attack)
         .add(Attributes.MOVEMENT_SPEED, this.speed * 0.2F)
         .add(ManasCoreAttributes.SWIM_SPEED_MULTIPLIER, this.speed);
   }

   public AttributeMap getAttributes() {
      return AttributeHelper.getAttributeMap(this.getAttributeSupplier().build());
   }

   public ResourceLocation getTextureLocation() {
      return this.texture;
   }

   public Item getDrop() {
      return switch (this) {
         case LOW_MAGISTEEL -> (Item)TensuraMaterialItems.LOW_MAGISTEEL_BONE_GOLEM.get();
         case HIGH_MAGISTEEL -> (Item)TensuraMaterialItems.HIGH_MAGISTEEL_BONE_GOLEM.get();
         case MITHRIL -> (Item)TensuraMaterialItems.MITHRIL_BONE_GOLEM.get();
         case PURE_MAGISTEEL -> (Item)TensuraMaterialItems.PURE_MAGISTEEL_BONE_GOLEM.get();
         case ORICHALCUM -> (Item)TensuraMaterialItems.ORICHALCUM_BONE_GOLEM.get();
         case ADAMANTITE -> (Item)TensuraMaterialItems.ADAMANTITE_BONE_GOLEM.get();
         case HIHIIROKANE -> (Item)TensuraMaterialItems.HIHIIROKANE_BONE_GOLEM.get();
         default -> Items.SKELETON_SKULL;
      };
   }

   public static BoneGolemVariant byId(int id) {
      return BY_ID[id % BY_ID.length];
   }

   public static BoneGolemVariant byLowest(double ep, float HP) {
      return Arrays.stream(values())
         .sorted(Comparator.comparingDouble(BoneGolemVariant::getEP).reversed())
         .filter(golem -> golem.getEP() <= ep && golem.getHP() <= HP)
         .findFirst()
         .orElse(DEFAULT);
   }

   public static void removeBoneGolemFromRace(Player player, ManasRaceInstance race) {
      CompoundTag tag = race.getTag();
      if (tag != null && tag.getBoolean("BoneGolem")) {
         double baseEP = player.getAttributeValue(TensuraAttributes.MAX_AURA) + player.getAttributeValue(TensuraAttributes.MAX_MAGICULE);
         BoneGolemVariant variant = byLowest(baseEP, player.getMaxHealth());
         ItemStack drop = variant.getDrop().getDefaultInstance();
         if (!player.addItem(drop)) {
            player.drop(drop, false);
         }

         tag.remove("BoneGolem");
         race.markDirty();
      }
   }

   @Generated
   public int getId() {
      return this.id;
   }

   @Generated
   public String getName() {
      return this.name;
   }

   @Generated
   public double getEP() {
      return this.EP;
   }

   @Generated
   public float getHP() {
      return this.HP;
   }

   @Generated
   public float getArmor() {
      return this.armor;
   }

   @Generated
   public float getKnockbackResistance() {
      return this.knockbackResistance;
   }

   @Generated
   public float getAttack() {
      return this.attack;
   }

   @Generated
   public float getSpeed() {
      return this.speed;
   }

   @Generated
   public Block getBlock() {
      return this.block;
   }

   @Generated
   public ResourceLocation getTexture() {
      return this.texture;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      LOW_MAGISTEEL = new BoneGolemVariant(
         1,
         "low_magisteel",
         ArchDaemonEntity.CONFIG.LowMagisteel.EP,
         ArchDaemonEntity.CONFIG.LowMagisteel.HP,
         ArchDaemonEntity.CONFIG.LowMagisteel.Armor,
         ArchDaemonEntity.CONFIG.LowMagisteel.Knockback,
         ArchDaemonEntity.CONFIG.LowMagisteel.Attack,
         ArchDaemonEntity.CONFIG.LowMagisteel.Speed,
         (Block)TensuraBlocks.LOW_MAGISTEEL_BLOCK.get()
      );
      HIGH_MAGISTEEL = new BoneGolemVariant(
         2,
         "high_magisteel",
         ArchDaemonEntity.CONFIG.HighMagisteel.EP,
         ArchDaemonEntity.CONFIG.HighMagisteel.HP,
         ArchDaemonEntity.CONFIG.HighMagisteel.Armor,
         ArchDaemonEntity.CONFIG.HighMagisteel.Knockback,
         ArchDaemonEntity.CONFIG.HighMagisteel.Attack,
         ArchDaemonEntity.CONFIG.HighMagisteel.Speed,
         (Block)TensuraBlocks.HIGH_MAGISTEEL_BLOCK.get()
      );
      MITHRIL = new BoneGolemVariant(
         3,
         "mithril",
         ArchDaemonEntity.CONFIG.Mithril.EP,
         ArchDaemonEntity.CONFIG.Mithril.HP,
         ArchDaemonEntity.CONFIG.Mithril.Armor,
         ArchDaemonEntity.CONFIG.Mithril.Knockback,
         ArchDaemonEntity.CONFIG.Mithril.Attack,
         ArchDaemonEntity.CONFIG.Mithril.Speed,
         (Block)TensuraBlocks.MITHRIL_BLOCK.get()
      );
      PURE_MAGISTEEL = new BoneGolemVariant(
         4,
         "pure_magisteel",
         ArchDaemonEntity.CONFIG.PureMagisteel.EP,
         ArchDaemonEntity.CONFIG.PureMagisteel.HP,
         ArchDaemonEntity.CONFIG.PureMagisteel.Armor,
         ArchDaemonEntity.CONFIG.PureMagisteel.Knockback,
         ArchDaemonEntity.CONFIG.PureMagisteel.Attack,
         ArchDaemonEntity.CONFIG.PureMagisteel.Speed,
         (Block)TensuraBlocks.PURE_MAGISTEEL_BLOCK.get()
      );
      ORICHALCUM = new BoneGolemVariant(
         5,
         "orichalcum",
         ArchDaemonEntity.CONFIG.Orichalcum.EP,
         ArchDaemonEntity.CONFIG.Orichalcum.HP,
         ArchDaemonEntity.CONFIG.Orichalcum.Armor,
         ArchDaemonEntity.CONFIG.Orichalcum.Knockback,
         ArchDaemonEntity.CONFIG.Orichalcum.Attack,
         ArchDaemonEntity.CONFIG.Orichalcum.Speed,
         (Block)TensuraBlocks.ORICHALCUM_BLOCK.get()
      );
      ADAMANTITE = new BoneGolemVariant(
         6,
         "adamantite",
         ArchDaemonEntity.CONFIG.Adamantite.EP,
         ArchDaemonEntity.CONFIG.Adamantite.HP,
         ArchDaemonEntity.CONFIG.Adamantite.Armor,
         ArchDaemonEntity.CONFIG.Adamantite.Knockback,
         ArchDaemonEntity.CONFIG.Adamantite.Attack,
         ArchDaemonEntity.CONFIG.Adamantite.Speed,
         (Block)TensuraBlocks.ADAMANTITE_BLOCK.get()
      );
      HIHIIROKANE = new BoneGolemVariant(
         7,
         "hihiirokane",
         ArchDaemonEntity.CONFIG.Hihiirokane.EP,
         ArchDaemonEntity.CONFIG.Hihiirokane.HP,
         ArchDaemonEntity.CONFIG.Hihiirokane.Armor,
         ArchDaemonEntity.CONFIG.Hihiirokane.Knockback,
         ArchDaemonEntity.CONFIG.Hihiirokane.Attack,
         ArchDaemonEntity.CONFIG.Hihiirokane.Speed,
         (Block)TensuraBlocks.HIHIIROKANE_BLOCK.get()
      );
   }
}
