package io.github.manasmods.tensura.storage.unique;

import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public interface ITrulyUnique {
   Map<ResourceLocation, UUID> getSkillMap();

   void addSkill(ResourceLocation var1, UUID var2);

   void removeSkill(ResourceLocation var1);

   void removeOwner(UUID var1);

   boolean hasSkill(ResourceLocation var1);

   UUID getOwner(ResourceLocation var1);

   void markDirty();
}
