package io.github.manasmods.tensura.particle;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TensuraParticleHelper {
   public static void spawnParticlesLikeServer(
      Level level, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force
   ) {
      if (level.isClientSide()) {
         for (int i = 0; i < count; i++) {
            double d1 = level.random.nextGaussian() * deltaX;
            double d3 = level.random.nextGaussian() * deltaY;
            double d5 = level.random.nextGaussian() * deltaZ;
            double d6 = level.random.nextGaussian() * speed;
            double d7 = level.random.nextGaussian() * speed;
            double d8 = level.random.nextGaussian() * speed;
            level.addParticle(particle, force, x + d1, y + d3, z + d5, d6, d7, d8);
         }
      }
   }

   public static void spawnServerParticles(Level level, ParticleOptions particle, double x, double y, double z) {
      spawnServerParticles(level, particle, x, y, z, false);
   }

   public static void spawnServerParticles(Level level, ParticleOptions particle, double x, double y, double z, boolean force) {
      spawnServerParticles(level, particle, x, y, z, 1, 0.0, 0.0, 0.0, 0.0, force);
   }

   public static void spawnServerParticles(
      Level level, ParticleOptions particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force
   ) {
      spawnServerParticles(level, particle, x, y, z, count, deltaX, deltaY, deltaZ, speed, force, player -> player.distanceToSqr(x, y, z) <= 4096.0);
   }

   public static void spawnServerParticles(
      Level level,
      ParticleOptions particle,
      double x,
      double y,
      double z,
      int count,
      double deltaX,
      double deltaY,
      double deltaZ,
      double speed,
      boolean force,
      Predicate<ServerPlayer> predicate
   ) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (predicate.test(player)) {
               serverLevel.sendParticles(player, particle, force, x, y, z, count, deltaX, deltaY, deltaZ, speed);
            }
         });
      }
   }

   public static void spawnParticlesToOnePLayer(
      Player player,
      ParticleOptions particle,
      double x,
      double y,
      double z,
      int count,
      double deltaX,
      double deltaY,
      double deltaZ,
      double speed,
      boolean force
   ) {
      if (player instanceof ServerPlayer serverPlayer) {
         if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(serverPlayer, particle, force, x, y, z, count, deltaX, deltaY, deltaZ, speed);
         }
      }
   }

   public static void addParticlesAroundSelf(Entity entity, ParticleOptions pParticleOption) {
      addParticlesAroundSelf(entity, pParticleOption, 1.0);
   }

   public static void addParticlesAroundSelf(Entity entity, ParticleOptions pParticleOption, double randomScale) {
      if (entity.level().isClientSide()) {
         RandomSource random = entity instanceof LivingEntity living ? living.getRandom() : RandomSource.create();

         for (int i = 0; i < 5; i++) {
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            entity.level().addParticle(pParticleOption, entity.getRandomX(randomScale), entity.getRandomY(), entity.getRandomZ(randomScale), d0, d1, d2);
         }
      }
   }

   public static void addParticlesAroundPos(RandomSource random, Level level, Vec3 pos, ParticleOptions pParticleOption, double randomScale, int particleAmount) {
      if (level.isClientSide()) {
         for (int i = 0; i < particleAmount; i++) {
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            double x = pos.x + (2.0 * random.nextDouble() - 1.0) * randomScale;
            double y = pos.y + (2.0 * random.nextDouble() - 1.0) * randomScale;
            double z = pos.z + (2.0 * random.nextDouble() - 1.0) * randomScale;
            level.addParticle(pParticleOption, x, y, z, d0, d1, d2);
         }
      }
   }

   public static void addServerParticlesAroundPos(RandomSource random, Level level, Vec3 pos, ParticleOptions pParticleOption, double randomScale) {
      if (level instanceof ServerLevel serverLevel) {
         for (int i = 0; i < 5; i++) {
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            double x = pos.x + (2.0 * random.nextDouble() - 1.0) * randomScale;
            double y = pos.y + (2.0 * random.nextDouble() - 1.0) * randomScale;
            double z = pos.z + (2.0 * random.nextDouble() - 1.0) * randomScale;
            serverLevel.sendParticles(pParticleOption, x, y, z, 0, d0, d1, d2, 1.0);
         }
      }
   }

   public static void addServerParticlesAroundSelf(Entity entity, ParticleOptions pParticleOption) {
      addServerParticlesAroundSelf(entity, pParticleOption, 1.0);
   }

   public static void addServerParticlesAroundSelf(Entity entity, ParticleOptions pParticleOption, double randomScale) {
      addServerParticlesAroundSelf(entity, pParticleOption, randomScale, 5);
   }

   public static void addServerParticlesAroundSelf(Entity entity, ParticleOptions pParticleOption, double randomScale, int amount) {
      if (entity.level() instanceof ServerLevel serverLevel) {
         RandomSource random = entity instanceof LivingEntity living ? living.getRandom() : RandomSource.create();

         for (int i = 0; i < amount; i++) {
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            serverLevel.sendParticles(pParticleOption, entity.getRandomX(randomScale), entity.getRandomY(), entity.getRandomZ(randomScale), 0, d0, d1, d2, 1.0);
         }
      }
   }

   public static void addServerParticlesAroundSelfToOnePlayer(ServerPlayer player, Entity entity, ParticleOptions pParticleOption, double randomScale) {
      if (entity.level() instanceof ServerLevel serverLevel) {
         RandomSource random = entity instanceof LivingEntity living ? living.getRandom() : RandomSource.create();

         for (int i = 0; i < 5; i++) {
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            serverLevel.sendParticles(
               player, pParticleOption, true, entity.getRandomX(randomScale), entity.getRandomY(), entity.getRandomZ(randomScale), 0, d0, d1, d2, 1.0
            );
         }
      }
   }

   public static void addServerParticlesAroundSelfToPredicatePlayer(
      Predicate<ServerPlayer> predicate, Entity entity, ParticleOptions pParticleOption, double randomScale
   ) {
      if (entity.level() instanceof ServerLevel serverLevel) {
         RandomSource random = entity instanceof LivingEntity living ? living.getRandom() : RandomSource.create();

         for (int i = 0; i < 5; i++) {
            double d0 = random.nextGaussian() * 0.02;
            double d1 = random.nextGaussian() * 0.02;
            double d2 = random.nextGaussian() * 0.02;
            serverLevel.getServer()
               .getPlayerList()
               .getPlayers()
               .forEach(
                  player -> {
                     if (predicate.test(player)) {
                        serverLevel.sendParticles(
                           player,
                           pParticleOption,
                           true,
                           entity.getRandomX(randomScale),
                           entity.getRandomY(),
                           entity.getRandomZ(randomScale),
                           0,
                           d0,
                           d1,
                           d2,
                           1.0
                        );
                     }
                  }
               );
         }
      }
   }

   public static void addServerAuraParticles(LivingEntity entity, ParticleOptions pParticleOption, int count, double speed) {
      addServerAuraParticles(entity, pParticleOption, count, speed, 1.0F);
   }

   public static void addServerAuraParticles(LivingEntity entity, ParticleOptions pParticleOption, int count, double speed, float rangeMultiplier) {
      Level level = entity.level();
      if (!level.isClientSide()) {
         double offsetX = entity.getBbWidth() / 2.0F * rangeMultiplier;
         double offsetY = entity.getBbHeight();
         double offsetZ = entity.getBbWidth() / 2.0F * rangeMultiplier;

         for (int i = 0; i < count; i++) {
            float yRandom = level.random.nextFloat();
            double dy = entity.getY() + yRandom * offsetY;
            double dx = entity.getX() + (level.random.nextDouble() * 2.0 - 1.0) * (yRandom > 0.5 ? offsetX / 2.0 : offsetX);
            double dz = entity.getZ() + (level.random.nextDouble() * 2.0 - 1.0) * (yRandom > 0.5 ? offsetZ / 2.0 : offsetZ);
            double vx = (level.random.nextDouble() * 2.0 - 1.0) * 0.1;
            double vy = (level.random.nextDouble() * 2.0 - 1.0) * 0.1;
            double vz = (level.random.nextDouble() * 2.0 - 1.0) * 0.1;
            spawnServerParticles(level, pParticleOption, dx, dy, dz, 1, vx, vy, vz, speed, false);
         }
      }
   }

   public static void spawnServerGroundSlamParticle(LivingEntity entity, int amount, float radius) {
      for (int i = 0; i < amount; i++) {
         for (int i1 = 0; i1 < 20 + entity.getRandom().nextInt(12); i1++) {
            double motionX = entity.getRandom().nextGaussian() * 0.07;
            double motionY = entity.getRandom().nextGaussian() * 0.07;
            double motionZ = entity.getRandom().nextGaussian() * 0.07;
            float angle = (float) (Math.PI / 180.0) * entity.yBodyRot + i1;
            double extraX = radius * Mth.sin((float)(Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            BlockPos ground = entity.getOnPos().offset((int)extraX, -1, (int)extraZ);
            BlockState groundState = entity.level().getBlockState(ground);
            if (!entity.level().isClientSide()) {
               ((ServerLevel)entity.level())
                  .sendParticles(
                     new BlockParticleOption(ParticleTypes.BLOCK, groundState),
                     entity.getX() + extraX,
                     ground.getY() + 1.5F,
                     entity.getZ() + extraZ,
                     0,
                     motionX,
                     motionY,
                     motionZ,
                     1.0
                  );
            }
         }
      }
   }

   public static void spawnGroundSlamParticle(LivingEntity entity, int amount, float radius) {
      if (entity.level().isClientSide()) {
         for (int i = 0; i < amount; i++) {
            for (int i1 = 0; i1 < 20 + entity.getRandom().nextInt(12); i1++) {
               double motionX = entity.getRandom().nextGaussian() * 0.07;
               double motionY = entity.getRandom().nextGaussian() * 0.07;
               double motionZ = entity.getRandom().nextGaussian() * 0.07;
               float angle = (float) (Math.PI / 180.0) * entity.yBodyRot + i1;
               double extraX = radius * Mth.sin((float)(Math.PI + angle));
               double extraY = 0.8F;
               double extraZ = radius * Mth.cos(angle);
               BlockPos groundPos = new BlockPos(Mth.floor(entity.getX() + extraX), Mth.floor(entity.getY() + extraY) - 1, Mth.floor(entity.getZ() + extraZ));
               BlockState groundState = entity.level().getBlockState(groundPos);
               if (!groundState.isFaceSturdy(entity.level(), groundPos, Direction.UP)) {
                  entity.level()
                     .addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, groundState),
                        true,
                        entity.getX() + extraX,
                        groundPos.getY() + extraY,
                        entity.getZ() + extraZ,
                        motionX,
                        motionY,
                        motionZ
                     );
               }
            }
         }
      }
   }

   public static void spawnSlamParticle(Entity entity, ParticleOptions options, int amount, float radius) {
      if (!entity.level().isClientSide()) {
         RandomSource random = RandomSource.create();

         for (int i = 0; i < amount; i++) {
            for (int i1 = 0; i1 < 20 + random.nextInt(12); i1++) {
               double motionX = random.nextGaussian() * 0.07;
               double motionY = random.nextGaussian() * 0.07;
               double motionZ = random.nextGaussian() * 0.07;
               float angle = (float) Math.PI + i1;
               double extraX = radius * Mth.sin((float)(Math.PI + angle));
               double extraY = 0.8F;
               double extraZ = radius * Mth.cos(angle);
               BlockPos groundPos = new BlockPos(Mth.floor(entity.getX() + extraX), Mth.floor(entity.getY() + extraY), Mth.floor(entity.getZ() + extraZ));
               BlockState groundState = entity.level().getBlockState(groundPos);
               if (!groundState.isFaceSturdy(entity.level(), groundPos, Direction.UP)) {
                  entity.level()
                     .addParticle(options, true, entity.getX() + extraX, groundPos.getY() + extraY, entity.getZ() + extraZ, motionX, motionY, motionZ);
               }
            }
         }
      }
   }

   public static void particleCloud(
      Level level, RandomSource random, ParticleOptions particleoptions, double xPos, double yPos, double zPos, double ySpeed, double xzSpeed, double radius
   ) {
      if (level.isClientSide()) {
         int i = Mth.ceil((float) Math.PI * radius * radius);

         for (int j = 0; j < i; j++) {
            float angle = random.nextFloat() * (float) (Math.PI * 2);
            double randomRad = Mth.sqrt(random.nextFloat()) * radius;
            double x = xPos + Mth.cos(angle) * randomRad;
            double z = zPos + Mth.sin(angle) * randomRad;
            double xSpeed = (0.5 - random.nextDouble()) * xzSpeed;
            double zSpeed = (0.5 - random.nextDouble()) * xzSpeed;
            level.addParticle(particleoptions, x, yPos, z, xSpeed, ySpeed, zSpeed);
         }
      }
   }

   public static void particleCloud(
      Level level, RandomSource random, ParticleOptions particleoptions, BlockPos pos, double ySpeed, double xzSpeed, double radius
   ) {
      particleCloud(level, random, particleoptions, pos.getX(), pos.getY(), pos.getZ(), ySpeed, xzSpeed, radius);
   }

   public static void serverParticleCloud(
      Level level, RandomSource random, ParticleOptions particleoptions, double xPos, double yPos, double zPos, double ySpeed, double xzSpeed, double radius
   ) {
      if (level instanceof ServerLevel serverLevel) {
         int i = Mth.ceil((float) Math.PI * radius * radius);

         for (int j = 0; j < i; j++) {
            float angle = random.nextFloat() * (float) (Math.PI * 2);
            double randomRad = Mth.sqrt(random.nextFloat()) * radius;
            double x = xPos + Mth.cos(angle) * randomRad;
            double z = zPos + Mth.sin(angle) * randomRad;
            double xSpeed = (0.5 - random.nextDouble()) * xzSpeed;
            double zSpeed = (0.5 - random.nextDouble()) * xzSpeed;
            serverLevel.sendParticles(particleoptions, x, yPos, z, 0, xSpeed, ySpeed, zSpeed, 1.0);
         }
      }
   }

   public static void spawnEnchantingTableParticle(Level level, Vec3 pos, ParticleOptions particle, int frequency) {
      if (level.isClientSide()) {
         RandomSource random = level.getRandom();

         for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            if (random.nextInt(frequency) == 0) {
               double vx = offset.getX() + random.nextFloat() - 0.5;
               double vy = offset.getY() - random.nextFloat() - 1.0;
               double vz = offset.getZ() + random.nextFloat() - 0.5;
               level.addParticle(particle, pos.x(), pos.y(), pos.z(), vx, vy, vz);
            }
         }
      }
   }
}
