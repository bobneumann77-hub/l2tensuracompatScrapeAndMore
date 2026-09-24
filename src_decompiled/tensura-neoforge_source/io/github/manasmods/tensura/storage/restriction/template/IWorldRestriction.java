package io.github.manasmods.tensura.storage.restriction.template;

import java.util.Map;

public interface IWorldRestriction {
   Map<String, WorldRestrictionInstance> getRestrictions();

   WorldRestrictionInstance getRestriction(String var1);

   void addRestriction(String var1, WorldRestrictionInstance var2);

   void removeRestriction(String var1);

   void reloadFromJson();

   void markDirty();
}
