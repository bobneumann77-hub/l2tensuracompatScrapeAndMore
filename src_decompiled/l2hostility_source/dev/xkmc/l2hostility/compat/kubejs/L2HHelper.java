package dev.xkmc.l2hostility.compat.kubejs;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2damagetracker.compat.CustomAttackListener;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public class L2HHelper {
   @Nullable
   public static MobTraitCap of(Entity e) {
      return e instanceof Mob mob ? (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob).orElse(null) : null;
   }

   public static CustomAttackListener newAttackListener() {
      return new CustomAttackListener();
   }

   public static boolean entityIs(Entity e, String id) {
      return id.startsWith("#")
         ? e.getType().is(TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(id.substring(1))))
         : e.getType() == BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(id));
   }

   public static boolean sourceIs(DamageSource source, String id) {
      return id.startsWith("#")
         ? source.is(TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse(id.substring(1))))
         : ((ResourceKey)source.typeHolder().unwrapKey().orElseThrow()).location().toString().equals(id);
   }
}
