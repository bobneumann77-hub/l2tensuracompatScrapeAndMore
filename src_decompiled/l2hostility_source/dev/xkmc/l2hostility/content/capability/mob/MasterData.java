package dev.xkmc.l2hostility.content.capability.mob;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.chunk.RegionalDifficultyModifier;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.traits.legendary.MasterTrait;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;

@SerialClass
public class MasterData {
   @SerialField
   public ArrayList<MasterData.Minion> data = new ArrayList<>();
   @SerialField(toClient = false)
   private final LinkedHashMap<EntityType<?>, MasterData.Data> map = new LinkedHashMap<>();

   @Nullable
   public static BlockPos getRandomPos(ServerLevel sl, EntityType<?> type, LivingEntity mob, int r, int round) {
      BlockPos pos = mob.blockPosition();
      Vec3 eye = mob.getEyePosition();
      RandomSource rand = mob.getRandom();

      for (int i = 0; i < round; i++) {
         BlockPos p = pos.offset(rand.nextInt(0, r * 2 + 1) - r, rand.nextInt(0, 3), rand.nextInt(0, r * 2 + 1) - r);
         if (sl.noCollision(type.getSpawnAABB(p.getX(), p.getY(), p.getZ()))) {
            Vec3 e = Vec3.atBottomCenterOf(p).add(0.0, type.getHeight() / 2.0F, 0.0);
            BlockHitResult bhit = sl.clip(new ClipContext(eye, e, Block.OUTLINE, Fluid.NONE, CollisionContext.empty()));
            if (bhit.getType() == Type.MISS) {
               return p;
            }
         }
      }

      return null;
   }

   public boolean tick(MobTraitCap cap, Mob mob) {
      EntityConfig.MasterConfig config = MasterTrait.getConfig(mob.getType());
      if (config == null) {
         return false;
      }

      for (EntityConfig.Minion e : config.minions()) {
         this.map.computeIfAbsent(e.type(), k -> new MasterData.Data()).setup(e);
      }

      boolean updated = this.data.removeIf(ex -> {
         ex.tick(mob);
         if (ex.minion == null) {
            return !mob.level().isClientSide();
         }

         MasterData.Data ent = this.map.get(ex.minion.getType());
         if (ent != null) {
            ent.count++;
         }

         return false;
      });
      if (!mob.level().isClientSide()) {
         for (MasterData.Minion e : this.data) {
            if (e.minion != null) {
               if (e.minion.getTarget() == null && mob.getTarget() != null) {
                  e.minion.setTarget(mob.getTarget());
               }

               if (e.id != e.minion.getId()) {
                  e.id = e.minion.getId();
                  updated = true;
               }
            }
         }
      }

      for (MasterData.Data e : this.map.values()) {
         if (e.count < e.config.maxCount() && e.cooldown > 0) {
            e.cooldown--;
         }
      }

      if (mob.level() instanceof ServerLevel sl
         && mob.getTarget() != null
         && mob.getTarget().isAlive()
         && this.data.size() < config.maxTotalCount()
         && mob.tickCount % config.spawnInterval() == 0) {
         for (MasterData.Data e : this.map.values()) {
            if (e.cooldown <= 0
               && this.data.size() < config.maxTotalCount()
               && e.count < e.config.maxCount()
               && mob.getHealth() / mob.getMaxHealth() <= e.config.maxHealthPercentage()
               && cap.getLevel() >= e.config.minLevel()) {
               MasterData.Minion nd = e.spawn(cap, sl, mob);
               if (nd != null) {
                  this.data.add(nd);
                  e.cooldown = e.config.cooldown();
                  return true;
               }
            }
         }
      }

      return updated;
   }

   @SerialClass
   public static class Data {
      private EntityConfig.Minion config;
      private int count;
      @SerialField
      public int cooldown;

      public void setup(EntityConfig.Minion e) {
         this.config = e;
         this.count = 0;
      }

      @Nullable
      public MasterData.Minion spawn(MobTraitCap parent, ServerLevel sl, Mob mob) {
         int r = this.config.spawnRange();
         BlockPos target = MasterData.getRandomPos(sl, this.config.type(), mob, r / 2, 16);
         if (target == null) {
            return null;
         }

         Entity e = this.config.type().create(sl);
         if (e instanceof Mob m && ((GeneralCapabilityHolder)LHMiscs.MOB.type()).isProper(m)) {
            MobTraitCap cap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(m);
            e.moveTo(target.getCenter());
            cap.deinit();
            RegionalDifficultyModifier diff = (p, c) -> {
               if (this.config.copyLevel()) {
                  c.base = parent.getLevel();
               } else {
                  ChunkDifficulty.at(sl, p).ifPresent(x -> x.modifyInstance(p, c));
               }

               if (this.config.copyTrait()) {
                  cap.traits.putAll(parent.traits);
                  cap.copied = true;
                  c.delegateTrait();
               }
            };
            if (this.config.traits() != null) {
               cap.setConfigCache(this.config.traits());
            }

            cap.minion = true;
            cap.asMinion = new MinionData().init(mob, this.config);
            cap.init(sl, m, diff);
            m.setTarget(mob.getTarget());
            sl.addFreshEntity(m);
            MasterData.Minion ans = new MasterData.Minion();
            ans.minion = m;
            ans.uuid = m.getUUID();
            ans.id = m.getId();
            return ans;
         } else {
            return null;
         }
      }
   }

   @SerialClass
   public static class Minion {
      @SerialField(toClient = true)
      public UUID uuid;
      @SerialField(toClient = true)
      public int id;
      public Mob minion;

      public void tick(Mob mob) {
         if (mob.level() instanceof ServerLevel sl) {
            if (this.minion == null && sl.getEntity(this.uuid) instanceof Mob m) {
               this.minion = m;
            }

            if (this.minion != null && !this.minion.isAlive()) {
               this.minion = null;
            }
         } else if (this.minion == null && mob.level().getEntity(this.id) instanceof Mob m && m.getUUID().equals(this.uuid)) {
            this.minion = m;
         }
      }
   }
}
