package io.github.manasmods.tensura.storage.unique;

import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageHolder;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UniqueStorage extends Storage implements ITrulyUnique {
   @Generated
   private static final Logger log = LogManager.getLogger(UniqueStorage.class);
   private static StorageKey<UniqueStorage> key = null;
   private final Map<ResourceLocation, UUID> skillMap = new HashMap<>();

   public static void init() {
      StorageEvents.REGISTER_WORLD_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "unique_storage"),
               UniqueStorage.class,
               level -> level.dimension() != null && level.dimension().equals(Level.OVERWORLD),
               UniqueStorage::new
            )
         );
   }

   protected UniqueStorage(StorageHolder holder) {
      super(holder);
   }

   protected Level getOwner() {
      return (Level)this.holder;
   }

   public void save(CompoundTag tag) {
      ListTag skillList = new ListTag();
      this.skillMap.forEach((skillId, ownerUUID) -> {
         CompoundTag entry = new CompoundTag();
         entry.putString("skill", skillId.toString());
         entry.putUUID("owner", ownerUUID);
         skillList.add(entry);
      });
      tag.put("entries", skillList);
   }

   public void load(CompoundTag tag) {
      if (tag.contains("entries")) {
         ListTag skillList = tag.getList("entries", 10);
         skillList.forEach(e -> {
            CompoundTag entry = (CompoundTag)e;
            ResourceLocation skillId = ResourceLocation.parse(entry.getString("skill"));
            UUID ownerUUID = entry.getUUID("owner");
            this.skillMap.put(skillId, ownerUUID);
         });
      }
   }

   @Override
   public void addSkill(ResourceLocation skillId, UUID ownerUUID) {
      this.skillMap.put(skillId, ownerUUID);
      this.markDirty();
   }

   @Override
   public boolean hasSkill(ResourceLocation skillId) {
      return this.skillMap.containsKey(skillId);
   }

   @Override
   public UUID getOwner(ResourceLocation skillId) {
      return this.skillMap.get(skillId);
   }

   @Override
   public void removeSkill(ResourceLocation skillId) {
      this.skillMap.remove(skillId);
      this.markDirty();
   }

   @Override
   public void removeOwner(UUID ownerUUID) {
      this.skillMap.entrySet().removeIf(entry -> Objects.equals(ownerUUID, entry.getValue()));
      this.markDirty();
   }

   @Generated
   public static StorageKey<UniqueStorage> getKey() {
      return key;
   }

   @Generated
   @Override
   public Map<ResourceLocation, UUID> getSkillMap() {
      return this.skillMap;
   }
}
