package io.github.manasmods.tensura.storage.ep;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import java.util.Collection;
import java.util.UUID;
import net.minecraft.world.entity.MobSpawnType;
import org.jetbrains.annotations.Nullable;

public interface IExistence {
   double getSpiritualHealth();

   void setSpiritualHealth(double var1);

   double getEP();

   void setEP(double var1);

   double getGainedEP();

   void setGainedEP(double var1);

   boolean isSkippingEPDrop();

   void setSkippingEPDrop(boolean var1);

   int getSleepModeTime();

   void setSleepModeTime(int var1);

   int getHarvestTick();

   void setHarvestTick(int var1);

   int getHarvestGiftTick();

   void setHarvestGiftTick(int var1);

   boolean hasHarvestGift();

   void setHarvestGift(boolean var1);

   double getAura();

   void setAura(double var1);

   void setMagicule(double var1);

   double getMagicule();

   Alignment getAlignment();

   void setAlignment(Alignment var1);

   Alignment getOriginalAlignment();

   void setOriginalAlignment(Alignment var1);

   @Nullable
   MobSpawnType getSpawnType();

   void setSpawnType(MobSpawnType var1);

   int getSoulPoints();

   void setSoulPoints(int var1);

   int getHumanKill();

   void setHumanKill(int var1);

   boolean isDemonLordSeed();

   void setDemonLordSeed(boolean var1);

   boolean isTrueDemonLord();

   void setTrueDemonLord(boolean var1);

   boolean isBlessed();

   void setBlessed(boolean var1);

   boolean isHeroEgg();

   void setHeroEgg(boolean var1);

   boolean isTrueHero();

   void setTrueHero(boolean var1);

   boolean isNameable();

   void setNameable(boolean var1);

   @Nullable
   String getName();

   void setName(@Nullable String var1);

   @Nullable
   UUID getPermanentOwner();

   void setPermanentOwner(@Nullable UUID var1);

   @Nullable
   UUID getTemporaryOwner();

   void setTemporaryOwner(@Nullable UUID var1);

   Collection<UUID> getTargetNeutralList();

   boolean isTargetNeutral(UUID var1);

   void addNeutralTarget(UUID var1);

   void removeNeutralTarget(UUID var1);

   void clearNeutralTargets();

   boolean isSpiritualForm();

   void setSpiritualForm(boolean var1);

   int getSummonedSecond();

   void setSummonedSecond(int var1);

   AbilitySlot getSummonedAbility();

   void setSummonedAbility(ManasSkill var1, int var2);

   @Nullable
   UUID getSummoner();

   void setSummoner(@Nullable UUID var1);

   void markDirty();
}
