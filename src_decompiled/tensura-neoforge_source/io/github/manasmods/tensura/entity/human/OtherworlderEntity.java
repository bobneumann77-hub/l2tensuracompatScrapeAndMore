package io.github.manasmods.tensura.entity.human;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.subclass.IOtherworlder;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;

public class OtherworlderEntity extends PlayerLikeEntity implements IOtherworlder {
   public OtherworlderEntity(EntityType<? extends OtherworlderEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   @Override
   public ResourceLocation getTextureLocation() {
      return null;
   }

   @Override
   public List<ManasSkill> getUniqueSkills() {
      return List.of();
   }

   @Override
   protected boolean removeWhenNoAction() {
      return false;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      TensuraBehaviourHelper.saveGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      TensuraBehaviourHelper.readGlobalPos(this, compound, MemoryModuleType.HOME, "Home");
   }

   public boolean shouldShowName() {
      return true;
   }

   @Override
   public boolean canMate(Animal pOtherAnimal) {
      return false;
   }

   public void die(DamageSource source) {
      TensuraBehaviourHelper.releaseHome(this);
      super.die(source);
      if (!this.level().isClientSide()) {
         if (!this.isAlive()) {
            this.dropSkills(source.getEntity());
         }
      }
   }
}
