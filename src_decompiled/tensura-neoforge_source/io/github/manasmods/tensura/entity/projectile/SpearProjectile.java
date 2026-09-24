package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpearProjectile extends AbstractArrow {
   protected static final EntityDataAccessor<Integer> LOYALTY_LEVEL = SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Integer> PIERCING_LEVEL = SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<ItemStack> SOURCE_ITEM = SynchedEntityData.defineId(SpearProjectile.class, EntityDataSerializers.ITEM_STACK);
   private boolean finishPiercing;
   @Nullable
   private IntOpenHashSet piercingIgnoreEntityIds;
   public int clientSideReturnKunaiTickCount;

   public SpearProjectile(EntityType<? extends SpearProjectile> type, Level level) {
      super(type, level);
   }

   public SpearProjectile(Level level, LivingEntity shooter, ItemStack pStack, boolean right) {
      super((EntityType)ProjectileEntityTypes.SPEAR.get(), shooter, level, pStack, null);
      this.setSourceItem(pStack);
      float rot = shooter.yHeadRot + (right ? 60 : -60);
      this.setPos(
         shooter.getX() - shooter.getBbWidth() * 0.5 * Mth.sin(rot * (float) (Math.PI / 180.0)),
         shooter.getEyeY() - 0.2F,
         shooter.getZ() + shooter.getBbWidth() * 0.5 * Mth.cos(rot * (float) (Math.PI / 180.0))
      );
   }

   public SpearProjectile(Level pLevel, double pX, double pY, double pZ, ItemStack source, @Nullable ItemStack ammo) {
      super((EntityType)ProjectileEntityTypes.SPEAR.get(), pX, pY, pZ, pLevel, source, ammo);
      this.setSourceItem(source);
   }

   @NotNull
   public ItemStack getPickupItem() {
      return this.getSourceItem().copy();
   }

   @NotNull
   public ItemStack getPickupItemStackOrigin() {
      return this.getSourceItem();
   }

   @NotNull
   protected ItemStack getDefaultPickupItem() {
      return ((Item)TensuraToolItems.DIAMOND_SPEAR.get()).getDefaultInstance();
   }

   protected boolean canHitEntity(Entity entity) {
      return super.canHitEntity(entity) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()));
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
      return this.finishPiercing ? null : super.findHitEntity(pStartVec, pEndVec);
   }

   private boolean isAcceptableReturnOwner() {
      Entity entity = this.getOwner();
      if (entity == null) {
         return false;
      } else {
         return !entity.isAlive() ? false : !(entity instanceof ServerPlayer) || !entity.isSpectator();
      }
   }

   public void tick() {
      if (this.inGroundTime > 4) {
         this.finishPiercing = true;
      }

      Entity owner = this.getOwner();
      int i = this.getLoyaltyLevel();
      if (i > 0 && (this.finishPiercing || this.isNoPhysics()) && owner != null && this.isAlive()) {
         if (!this.isAcceptableReturnOwner()) {
            if (!this.level().isClientSide && this.pickup == Pickup.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.discard();
         } else {
            this.setNoPhysics(true);
            Vec3 vec3 = owner.getEyePosition().subtract(this.position());
            this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * i, this.getZ());
            if (this.level().isClientSide) {
               this.yOld = this.getY();
            }

            double d0 = 0.05 * i;
            this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
            if (this.clientSideReturnKunaiTickCount == 0) {
               this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
            }

            this.clientSideReturnKunaiTickCount++;
         }
      }

      super.tick();
   }

   protected void onHitEntity(EntityHitResult pResult) {
      Entity entity = pResult.getEntity();
      ItemStack stack = this.getSourceItem();
      if (this.level() instanceof ServerLevel level) {
         Entity var10 = this.getOwner();
         DamageSource damagesource = TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), TensuraDamageTypes.SPEAR, var10, this);
         float damage = (float)(this.getBaseDamage() + TensuraDamageHelper.getWeaponBaseDamage(stack, EquipmentSlotGroup.MAINHAND) + 1.0);
         damage = EnchantmentHelper.modifyDamage(level, stack, entity, damagesource, damage);
         if (this.getPiercingLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
               this.piercingIgnoreEntityIds = new IntOpenHashSet(this.getPiercingLevel());
            }

            if (this.piercingIgnoreEntityIds.size() + 1 > this.getPiercingLevel()) {
               this.finishPiercing = true;
            } else {
               this.piercingIgnoreEntityIds.add(entity.getId());
            }
         } else {
            this.finishPiercing = true;
         }

         if (entity.hurt(damagesource, damage)) {
            if (entity.getType() == EntityType.ENDERMAN) {
               return;
            }

            if (entity instanceof LivingEntity livingEntity) {
               EnchantmentHelper.doPostAttackEffectsWithItemSource(level, entity, damagesource, stack);
               if (var10 instanceof LivingEntity livingOwner) {
                  TensuraEnchantmentHelper.doAdditionalAfterDamage(level, livingEntity, livingOwner, damagesource, stack, damage);
               }

               this.doPostHurtEffects(livingEntity);
            }
         }

         if (entity.getType() != EntityType.ENDERMAN && var10 instanceof LivingEntity livingOwner && entity instanceof LivingEntity livingEntity) {
            TensuraEnchantmentHelper.doAdditionalAfterAttack(level, livingEntity, livingOwner, damagesource, stack, damage);
         }

         if (this.finishPiercing) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
         }
      }
   }

   protected boolean tryPickup(Player pPlayer) {
      return super.tryPickup(pPlayer) || this.isNoPhysics() && this.ownedBy(pPlayer) && pPlayer.addItem(this.getPickupItem());
   }

   @NotNull
   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.TRIDENT_HIT_GROUND;
   }

   public void playerTouch(Player pEntity) {
      if (this.ownedBy(pEntity) || this.getOwner() == null) {
         super.playerTouch(pEntity);
      }
   }

   public ItemStack getSourceItem() {
      ItemStack stack = (ItemStack)this.entityData.get(SOURCE_ITEM);
      return stack != null && !stack.isEmpty() ? stack : this.getDefaultPickupItem();
   }

   public void setSourceItem(ItemStack pStack) {
      this.entityData.set(SOURCE_ITEM, pStack);
      this.setLoyaltyLevel(TensuraEnchantmentHelper.getEnchantmentLevel(this.level(), Enchantments.LOYALTY, pStack));
      this.setPiercingLevel(TensuraEnchantmentHelper.getEnchantmentLevel(this.level(), Enchantments.PIERCING, pStack));
   }

   public int getLoyaltyLevel() {
      return (Integer)this.entityData.get(LOYALTY_LEVEL);
   }

   public void setLoyaltyLevel(int level) {
      this.entityData.set(LOYALTY_LEVEL, level);
   }

   public void setPiercingLevel(int level) {
      this.entityData.set(PIERCING_LEVEL, level);
   }

   public int getPiercingLevel() {
      return (Integer)this.entityData.get(PIERCING_LEVEL);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(LOYALTY_LEVEL, 0);
      builder.define(PIERCING_LEVEL, 0);
      builder.define(SOURCE_ITEM, this.getDefaultPickupItem());
   }

   public void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.contains("item", 10)) {
         this.setSourceItem(ItemStack.parse(this.registryAccess(), pCompound.getCompound("item")).orElse(this.getDefaultPickupItem()));
      } else {
         this.setSourceItem(this.getDefaultPickupItem());
      }

      this.setLoyaltyLevel(pCompound.getInt("Loyalty"));
      this.setPiercingLevel(pCompound.getInt("Piercing"));
      this.finishPiercing = pCompound.getBoolean("DealtDamage");
   }

   public void addAdditionalSaveData(CompoundTag pCompound) {
      this.setPickupItemStack(this.getSourceItem());
      super.addAdditionalSaveData(pCompound);
      pCompound.putBoolean("DealtDamage", this.finishPiercing);
      pCompound.putInt("Loyalty", this.getLoyaltyLevel());
      pCompound.putInt("Piercing", this.getPiercingLevel());
      ItemStack stack = this.getSourceItem();
      if (stack != null && !stack.isEmpty()) {
         pCompound.put("item", stack.saveOptional(this.registryAccess()));
      } else {
         pCompound.remove("item");
      }
   }

   public void tickDespawn() {
      int i = this.getLoyaltyLevel();
      if ((this.pickup != Pickup.ALLOWED || i <= 0) && ++this.life >= TensuraProjectile.CONFIG.spearDespawnTick) {
         this.discard();
      }
   }

   protected float getWaterInertia() {
      return 0.79F + TensuraEnchantmentHelper.getEnchantmentLevel(this.level(), Enchantments.IMPALING, this.getSourceItem()) * 0.04F;
   }
}
