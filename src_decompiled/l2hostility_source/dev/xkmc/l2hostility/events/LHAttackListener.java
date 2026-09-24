package dev.xkmc.l2hostility.events;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.init.reg.ench.EnchHolder;
import dev.xkmc.l2core.init.reg.ench.LegacyEnchantment;
import dev.xkmc.l2damagetracker.contents.attack.AttackListener;
import dev.xkmc.l2damagetracker.contents.attack.CreateSourceEvent;
import dev.xkmc.l2damagetracker.contents.attack.DamageData;
import dev.xkmc.l2damagetracker.contents.attack.DamageModifier;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Attack;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Defence;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.Offence;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.OffenceMax;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeWrapper;
import dev.xkmc.l2damagetracker.contents.damage.DefaultDamageState;
import dev.xkmc.l2damagetracker.init.data.L2DamageTypes;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.enchantments.HitTargetEnchantment;
import dev.xkmc.l2hostility.content.item.curio.core.CurseCurioItem;
import dev.xkmc.l2hostility.content.logic.TraitEffectCache;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2library.util.GenericItemStack;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags.DamageTypes;

public class LHAttackListener implements AttackListener {
   private static final ResourceLocation SCALING = L2Hostility.loc("scaling");
   private static final ResourceLocation MASTER_IMMUNE = L2Hostility.loc("master_immune");

   private static boolean masterImmunity(DamageData event) {
      if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return false;
      }

      MobTraitCap attacker = null;
      MobTraitCap target = null;
      if (event.getAttacker() instanceof Mob mob) {
         attacker = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob).orElse(null);
      }

      if (event.getTarget() instanceof Mob mob) {
         target = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob).orElse(null);
      }

      LivingEntity attackerMaster = null;
      LivingEntity targetMaster = null;
      if (attacker != null && attacker.asMinion != null) {
         attackerMaster = attacker.asMinion.master;
         if (event.getTarget() == attackerMaster) {
            return true;
         }
      }

      if (target != null && target.asMinion != null) {
         targetMaster = target.asMinion.master;
         if (event.getAttacker() == targetMaster) {
            return true;
         }
      }

      return attackerMaster != null && attackerMaster == targetMaster ? true : target != null && target.isMasterProtected();
   }

   public boolean onAttack(Attack event) {
      if (masterImmunity(event)) {
         return true;
      }

      DamageSource source = event.getSource();
      LivingEntity target = event.getTarget();
      boolean bypassInvul = source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
      boolean bypassMagic = source.is(DamageTypeTags.BYPASSES_EFFECTS);
      boolean magic = source.is(DamageTypes.IS_MAGIC);
      if (magic && !bypassInvul && !bypassMagic && CurioCompat.hasItemInCurio(target, (Item)LHItems.RING_DIVINITY.get())) {
         return true;
      }

      Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(target);
      if (opt.isPresent()) {
         for (Entry<MobTrait, Integer> e : opt.get().traits.entrySet()) {
            if (e.getKey().onAttackedByOthers(e.getValue(), target, event)) {
               return true;
            }
         }
      }

      return false;
   }

   public void onHurt(Offence data) {
      DamageSource source = data.getSource();
      if (!source.is(L2DamageTypes.NO_SCALE)) {
         LivingEntity attacker = data.getAttacker();
         LivingEntity target = data.getTarget();
         if (attacker != target) {
            Optional<MobTraitCap> targetOpt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(target);
            if (targetOpt.isPresent()) {
               MobTraitCap cap = targetOpt.get();

               for (EnchHolder<HitTargetEnchantment> e : LegacyEnchantment.findAll(data.getWeapon(), HitTargetEnchantment.class, true)) {
                  ((HitTargetEnchantment)e.val()).hitMob(target, cap, e.lv(), data);
               }
            }

            if (attacker != null) {
               Optional<MobTraitCap> attOpt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(attacker);
               if (attOpt.isPresent()) {
                  MobTraitCap cap = attOpt.get();
                  if (!attacker.getType().is(LHTagGen.NO_SCALING)) {
                     int lv = cap.getLevel();
                     double factor;
                     if ((Boolean)LHConfig.SERVER.exponentialDamage.get()) {
                        factor = Math.pow(1.0 + (Double)LHConfig.SERVER.damageFactor.get(), lv) - 1.0;
                     } else {
                        factor = lv * (Double)LHConfig.SERVER.damageFactor.get();
                     }

                     EntityConfig.Config config = cap.getConfigCache(attacker);
                     if (config != null) {
                        factor *= config.attackScale;
                     }

                     double old = factor;

                     for (Entry<MobTrait, Integer> ent : cap.traits.entrySet()) {
                        factor *= ent.getKey().modifyBonusDamage(source, old, ent.getValue());
                     }

                     if (source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
                        factor *= LHConfig.SERVER.dispellDamageFactor.get();
                     }

                     data.addHurtModifier(DamageModifier.multTotal(1.0F + (float)factor, SCALING));
                  }

                  TraitEffectCache traitCache = new TraitEffectCache(target);
                  cap.traitEvent((k, v) -> k.onHurtTarget(v, attacker, data, traitCache));
               }
            }

            if (attacker != null) {
               for (GenericItemStack<CurseCurioItem> e : CurseCurioItem.getFromPlayer(attacker)) {
                  ((CurseCurioItem)e.item()).onHurtTarget(e.stack(), attacker, data);
               }
            }
         }
      }
   }

   public void onHurtMaximized(OffenceMax data) {
      LivingEntity target = data.getTarget();
      ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(target).ifPresent(cap -> cap.traitEvent((k, v) -> k.onHurtByMax(v, target, data)));
   }

   public void onDamage(Defence data) {
      LivingEntity mob = data.getTarget();
      Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob);
      if (opt.isPresent()) {
         MobTraitCap cap = opt.get();
         cap.traitEvent((k, v) -> k.onDamaged(v, mob, data));
      }

      LivingEntity attacker = data.getAttacker();
      if (attacker != null) {
         TraitEffectCache traitCache = new TraitEffectCache(mob);
         ((GeneralCapabilityHolder)LHMiscs.MOB.type())
            .getExisting(attacker)
            .ifPresent(cap -> cap.traitEvent((k, v) -> k.onHurtTargetMax(v, attacker, data, traitCache)));

         for (GenericItemStack<CurseCurioItem> e : CurseCurioItem.getFromPlayer(mob)) {
            ((CurseCurioItem)e.item()).onDamage(e.stack(), mob, data);
         }

         if (masterImmunity(data)) {
            data.addDealtModifier(DamageModifier.nonlinearFinal(10432, ex -> 0.0F, MASTER_IMMUNE));
         }
      }
   }

   public void onCreateSource(CreateSourceEvent event) {
      LivingEntity mob = event.getAttacker();
      Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(mob);
      opt.ifPresent(cap -> cap.traitEvent((k, v) -> k.onCreateSource(v, event.getAttacker(), event)));
      DamageTypeWrapper type = event.getResult();
      if (type != null) {
         DamageTypeWrapper root = type.toRoot();
         if (root == L2DamageTypes.MOB_ATTACK || root == L2DamageTypes.PLAYER_ATTACK) {
            if (CurioCompat.hasItemInCurioOrSlot(mob, (Item)LHItems.IMAGINE_BREAKER.get())) {
               event.enable(DefaultDamageState.BYPASS_MAGIC);
            }

            if (CurioCompat.hasItemInCurioOrSlot(mob, (Item)LHItems.PLATINUM_STAR.get())) {
               event.enable(DefaultDamageState.BYPASS_COOLDOWN);
            }
         }
      }
   }
}
