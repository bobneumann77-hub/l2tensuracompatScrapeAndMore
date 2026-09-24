package dev.xkmc.l2hostility.content.traits.highlevel;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Offence;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.item.traits.EffectBooster;
import dev.xkmc.l2hostility.content.logic.TraitEffectCache;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class DrainTrait extends MobTrait {
   public DrainTrait(ChatFormatting format) {
      super(format);
   }

   @Override
   public void postInit(LivingEntity mob, int lv) {
      MobTraitCap cap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(mob);
      Optional<Named<MobTrait>> potions = LHTraits.TRAITS.reg().getTag(LHTraits.POTION);
      if (!potions.isEmpty() && potions.get().size() != 0 && !cap.copied) {
         for (int i = 0; i < 4; i++) {
            Optional<Holder<MobTrait>> opt = potions.get().getRandomElement(mob.getRandom());
            if (opt.isEmpty()) {
               return;
            }

            MobTrait trait = (MobTrait)opt.get().value();
            if (trait.allow(mob) && !cap.hasTrait(trait)) {
               cap.setTrait(trait, lv);
            }
         }
      }
   }

   @Override
   public void onHurtTarget(int level, LivingEntity attacker, Offence cache, TraitEffectCache traitCache) {
      LivingEntity target = cache.getTarget();
      long neg = target.getActiveEffects().stream().filter(e -> ((MobEffect)e.getEffect().value()).getCategory() == MobEffectCategory.HARMFUL).count();
      cache.addHurtModifier(DamageModifier.multTotal((float)(1.0 + (Double)LHConfig.SERVER.drainDamage.get() * level * neg), this.getRegistryName()));
   }

   @Override
   public void postHurtImpl(int level, LivingEntity attacker, LivingEntity target) {
      ArrayList<MobEffectInstance> pos = new ArrayList<>(
         target.getActiveEffects()
            .stream()
            .filter(e -> ((MobEffect)e.getEffect().value()).getCategory() == MobEffectCategory.BENEFICIAL && !e.getEffect().is(LHTagGen.DRAIN_IGNORE))
            .toList()
      );

      for (int i = 0; i < level; i++) {
         if (!pos.isEmpty()) {
            MobEffectInstance ins = pos.remove(attacker.getRandom().nextInt(pos.size()));
            target.removeEffect(ins.getEffect());
         }
      }

      double factor = 1.0 + (Double)LHConfig.SERVER.drainDuration.get() * level;
      int maxTime = level * (Integer)LHConfig.SERVER.drainDurationMax.get();
      EffectBooster.boostTrait(target, factor, maxTime);
   }

   @Override
   public void addDetail(RegistryAccess access, List<Component> list) {
      list.add(
         Component.translatable(
               this.getDescriptionId() + ".desc",
               new Object[]{
                  this.mapLevel(access, i -> Component.literal(i + "").withStyle(ChatFormatting.AQUA)),
                  this.mapLevel(
                     access,
                     i -> Component.literal(Math.round(i.intValue() * (Double)LHConfig.SERVER.drainDamage.get() * 100.0) + "%").withStyle(ChatFormatting.AQUA)
                  ),
                  this.mapLevel(
                     access,
                     i -> Component.literal(Math.round(i.intValue() * (Double)LHConfig.SERVER.drainDuration.get() * 100.0) + "%")
                        .withStyle(ChatFormatting.AQUA)
                  ),
                  this.mapLevel(
                     access,
                     i -> Component.literal(Math.round(i * (Integer)LHConfig.SERVER.drainDurationMax.get() / 20.0F) + "").withStyle(ChatFormatting.AQUA)
                  )
               }
            )
            .withStyle(ChatFormatting.GRAY)
      );
   }
}
