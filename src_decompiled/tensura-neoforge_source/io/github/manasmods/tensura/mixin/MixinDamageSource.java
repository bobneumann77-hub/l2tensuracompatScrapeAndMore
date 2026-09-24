package io.github.manasmods.tensura.mixin;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.damage.TensuraDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public class MixinDamageSource implements TensuraDamageSource {
   @Unique
   @Nullable
   private ManasSkillInstance tensura$instance = null;
   @Unique
   private int tensura$abilityMode = -1;
   @Unique
   @Nullable
   private Skill.SkillType tensura$skillType = null;
   @Unique
   @Nullable
   private Magic.MagicType tensura$magicType = null;
   @Unique
   @Nullable
   private Element tensura$element = null;
   @Unique
   private boolean tensura$slotting = false;
   @Unique
   private boolean tensura$spiritual = false;
   @Unique
   private boolean tensura$physicalConverted = false;
   @Unique
   private float tensura$barrierBypass = 0.0F;
   @Unique
   private float tensura$resistBypass = 0.0F;
   @Unique
   private boolean tensura$dodgeBypass = false;
   @Unique
   private double tensura$aura = 0.0;
   @Unique
   private double tensura$magicule = 0.0;
   @Unique
   private String tensura$customMessage = null;
   @Unique
   private boolean tensura$notActualDeath = false;

   @Override
   public DamageSource tensura$setAbilityInstance(@Nullable ManasSkillInstance instance) {
      this.tensura$instance = instance;
      return (DamageSource)this;
   }

   @Override
   public ManasSkillInstance tensura$getAbilityInstance() {
      return this.tensura$instance;
   }

   @Override
   public DamageSource tensura$setAbilityMode(int mode) {
      this.tensura$abilityMode = mode;
      return (DamageSource)this;
   }

   @Override
   public int tensura$getAbilityMode() {
      return this.tensura$abilityMode;
   }

   @Override
   public DamageSource tensura$setSkillType(@Nullable Skill.SkillType type) {
      this.tensura$skillType = type;
      return (DamageSource)this;
   }

   @Override
   public Skill.SkillType tensura$getSkillType() {
      return this.tensura$skillType;
   }

   @Override
   public DamageSource tensura$setMagicType(@Nullable Magic.MagicType type) {
      this.tensura$magicType = type;
      return (DamageSource)this;
   }

   @Override
   public Magic.MagicType tensura$getMagicType() {
      return this.tensura$magicType;
   }

   @Override
   public DamageSource tensura$setElement(@Nullable Element element) {
      this.tensura$element = element;
      return (DamageSource)this;
   }

   @Override
   public Element tensura$getElement() {
      return this.tensura$element;
   }

   @Override
   public DamageSource tensura$setSlotting() {
      this.tensura$slotting = true;
      return (DamageSource)this;
   }

   @Override
   public boolean tensura$isSlotting() {
      return this.tensura$slotting;
   }

   @Override
   public DamageSource tensura$setSpiritual() {
      this.tensura$spiritual = true;
      return (DamageSource)this;
   }

   @Override
   public boolean tensura$isSpiritual() {
      return this.tensura$spiritual;
   }

   @Override
   public DamageSource tensura$setPhysicalConverted() {
      this.tensura$physicalConverted = true;
      return (DamageSource)this;
   }

   @Override
   public boolean tensura$isPhysicalConverted() {
      return this.tensura$physicalConverted;
   }

   @Override
   public DamageSource tensura$setBarrierBypassLevel(float level) {
      this.tensura$barrierBypass = level;
      return (DamageSource)this;
   }

   @Override
   public float tensura$getBarrierBypassLevel() {
      return this.tensura$barrierBypass;
   }

   @Override
   public DamageSource tensura$setResistanceBypassLevel(float level) {
      this.tensura$resistBypass = level;
      return (DamageSource)this;
   }

   @Override
   public float tensura$getResistanceBypassLevel() {
      return this.tensura$resistBypass;
   }

   @Override
   public DamageSource tensura$setDodgeBypass() {
      this.tensura$dodgeBypass = true;
      return (DamageSource)this;
   }

   @Override
   public boolean tensura$isDodgeBypass() {
      return this.tensura$dodgeBypass;
   }

   @Override
   public DamageSource tensura$setAuraCost(double cost) {
      this.tensura$aura = cost;
      return (DamageSource)this;
   }

   @Override
   public double tensura$getAuraCost() {
      return this.tensura$aura;
   }

   @Override
   public DamageSource tensura$setMagiculeCost(double cost) {
      this.tensura$magicule = cost;
      return (DamageSource)this;
   }

   @Override
   public double tensura$getMagiculeCost() {
      return this.tensura$magicule;
   }

   @Override
   public DamageSource tensura$setCustomMessage(String string) {
      this.tensura$customMessage = string;
      return (DamageSource)this;
   }

   @Override
   public String tensura$getCustomMessage() {
      return this.tensura$customMessage;
   }

   @Override
   public DamageSource tensura$setNotActualDeath(boolean not) {
      this.tensura$notActualDeath = not;
      return (DamageSource)this;
   }

   @Override
   public boolean tensura$isNotActualDeath() {
      return this.tensura$notActualDeath;
   }
}
