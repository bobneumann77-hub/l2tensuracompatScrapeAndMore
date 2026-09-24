package io.github.manasmods.tensura.enchantment.template;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public interface EnchantmentPostDamageEffect extends EnchantmentEntityEffect {
   void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5, float var6);
}
