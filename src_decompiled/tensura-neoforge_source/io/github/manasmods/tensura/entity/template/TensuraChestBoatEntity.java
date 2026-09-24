package io.github.manasmods.tensura.entity.template;

import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class TensuraChestBoatEntity extends ChestBoat {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE = SynchedEntityData.defineId(TensuraChestBoatEntity.class, EntityDataSerializers.INT);

   public TensuraChestBoatEntity(EntityType<? extends TensuraChestBoatEntity> entityType, Level level) {
      super(entityType, level);
      this.blocksBuilding = true;
   }

   public TensuraChestBoatEntity(Level worldIn, double x, double y, double z) {
      this((EntityType<? extends TensuraChestBoatEntity>)MiscEntityTypes.CHEST_BOAT_ENTITY.get(), worldIn);
      this.setPos(x, y, z);
      this.xo = x;
      this.yo = y;
      this.zo = z;
   }

   protected void addAdditionalSaveData(CompoundTag compound) {
      compound.putString("Type", this.getTensuraBoatType().getName());
   }

   protected void readAdditionalSaveData(CompoundTag compound) {
      if (compound.contains("Type", 8)) {
         this.setBoatType(TensuraBoatEntity.Type.byName(compound.getString("Type")));
      }
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ID_TYPE, 0);
   }

   public Item getDropItem() {
      switch (this.getTensuraBoatType()) {
         case PALM:
            return (Item)TensuraBlocks.Items.PALM_CHEST_BOAT.get();
         default:
            throw new MatchException(null, null);
      }
   }

   public void setBoatType(TensuraBoatEntity.Type boatType) {
      this.entityData.set(DATA_ID_TYPE, boatType.ordinal());
   }

   public TensuraBoatEntity.Type getTensuraBoatType() {
      return TensuraBoatEntity.Type.byId((Integer)this.entityData.get(DATA_ID_TYPE));
   }
}
