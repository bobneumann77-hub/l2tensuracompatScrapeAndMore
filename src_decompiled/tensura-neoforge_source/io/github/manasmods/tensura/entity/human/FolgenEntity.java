package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.template.TensuraTamableEntity;
import io.github.manasmods.tensura.entity.template.subclass.IOtherworlder;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FolgenEntity extends FalmuthKnightEntity implements IOtherworlder {
   public FolgenEntity(EntityType<? extends FolgenEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public static Builder setAttributes() {
      return TensuraTamableEntity.setAttributes()
         .add(Attributes.ARMOR, 30.0)
         .add(Attributes.ATTACK_DAMAGE, 20.0)
         .add(Attributes.ATTACK_KNOCKBACK, 2.0)
         .add(Attributes.MAX_HEALTH, 500.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3F)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
         .add(Attributes.STEP_HEIGHT, 1.0)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 2.0)
         .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.2F)
         .add(TensuraAttributes.SPIRITUAL_HEALTH_REGENERATION, 10.0)
         .add(TensuraAttributes.AURA_REGENERATION_MULTIPLIER, 5.0);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/otherworlder/folgen.png");
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of((ManasSkill)UniqueSkills.SPEARHEAD.get());
   }

   @Override
   public boolean shouldTarget(LivingEntity target) {
      return super.shouldTarget(target);
   }

   @Override
   protected boolean removeWhenNoAction() {
      return false;
   }

   public boolean shouldShowName() {
      return true;
   }

   public FolgenEntity getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
      FolgenEntity entity = (FolgenEntity)((EntityType)HumanEntityTypes.FOLGEN.get()).create(pLevel);
      if (entity == null) {
         return null;
      }

      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         entity.setOwnerUUID(uuid);
         entity.setTame(true, true);
      }

      return entity;
   }

   @Override
   protected float getEquipmentDropChance(EquipmentSlot pSlot) {
      if (this.isTame()) {
         return 0.0F;
      } else {
         float chance = super.getEquipmentDropChance(pSlot);
         if (pSlot.equals(EquipmentSlot.MAINHAND)) {
            return Math.max(0.1F, chance);
         } else {
            return pSlot.equals(EquipmentSlot.OFFHAND) ? Math.max(0.3F, chance) : chance;
         }
      }
   }

   @NotNull
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
      if (this.canRandomizeSpawnData(pReason)) {
         this.summonKnightsRandomPos();
      }

      return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
   }

   protected void summonKnightsRandomPos() {
      if (this.level() instanceof ServerLevel serverLevel) {
         int var13 = Mth.floor(this.getX());
         int j = Mth.floor(this.getY());
         int k = Mth.floor(this.getZ());

         for (int knights = 0; knights < 9; knights++) {
            FalmuthKnightEntity knight = new FalmuthKnightEntity((EntityType<? extends FalmuthKnightEntity>)HumanEntityTypes.FALMUTH_KNIGHT.get(), serverLevel);

            for (int l = 0; l < 50; l++) {
               int i1 = var13 + Mth.nextInt(this.random, 0, 7) * Mth.nextInt(this.random, -1, 1);
               int j1 = j + Mth.nextInt(this.random, 0, 7) * Mth.nextInt(this.random, -1, 1);
               int k1 = k + Mth.nextInt(this.random, 0, 7) * Mth.nextInt(this.random, -1, 1);
               BlockPos blockpos = new BlockPos(i1, j1, k1);
               EntityType<?> entitytype = (EntityType<?>)HumanEntityTypes.FALMUTH_KNIGHT.get();
               if (SpawnPlacements.isSpawnPositionOk(entitytype, this.level(), blockpos)
                  && SpawnPlacements.checkSpawnRules(entitytype, serverLevel, MobSpawnType.REINFORCEMENT, blockpos, this.level().random)) {
                  knight.setPos(i1, j1, k1);
                  if (serverLevel.isUnobstructed(knight) && serverLevel.noCollision(knight)) {
                     knight.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(knight.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                     serverLevel.addFreshEntityWithPassengers(knight);
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      ItemStack weapon = new ItemStack(
         pRandom.nextBoolean() ? (ItemLike)TensuraToolItems.HIGH_MAGISTEEL_LONG_SWORD.get() : (ItemLike)TensuraToolItems.LOW_MAGISTEEL_LONG_SWORD.get()
      );
      if (pRandom.nextInt(10) == 1) {
         weapon.enchant(TensuraEnchantmentHelper.getEnchantment(this.level(), Enchantments.SHARPNESS), 5);
      }

      this.setItemSlot(EquipmentSlot.MAINHAND, weapon);
      this.inventory.setItem(this.getSlotId(EquipmentSlot.MAINHAND), weapon);
      this.updateContainerEquipment();
      if (!(pRandom.nextFloat() >= 0.8F)) {
         ItemStack stack = new ItemStack(Items.SHIELD);
         this.setItemSlot(EquipmentSlot.OFFHAND, stack);
         this.inventory.setItem(this.getSlotId(EquipmentSlot.OFFHAND), stack);
         this.updateContainerEquipment();
      }
   }

   @Override
   public void die(DamageSource source) {
      super.die(source);
      if (!this.level().isClientSide()) {
         if (!this.isAlive()) {
            this.dropSkills(source.getEntity());
         }
      }
   }
}
