package io.github.manasmods.tensura.storage.restriction.template;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class WorldRestrictionInstance {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   @Nullable
   private BlockPos corner1;
   @Nullable
   private BlockPos corner2;
   private ResourceKey<Level> dimension;
   private Map<ManasSkill, Set<Integer>> bannedAbilities = new HashMap<>();

   public WorldRestrictionInstance(@Nullable BlockPos corner1, @Nullable BlockPos corner2, ResourceKey<Level> dimension) {
      this.corner1 = corner1;
      this.corner2 = corner2;
      this.dimension = dimension;
   }

   public WorldRestrictionInstance(ResourceKey<Level> dimension) {
      this(null, null, dimension);
   }

   public boolean isGlobal() {
      return this.corner1 == null || this.corner2 == null;
   }

   public boolean contains(BlockPos pos) {
      if (this.isGlobal()) {
         return true;
      }

      int minX = Math.min(this.corner1.getX(), this.corner2.getX());
      int minY = Math.min(this.corner1.getY(), this.corner2.getY());
      int minZ = Math.min(this.corner1.getZ(), this.corner2.getZ());
      int maxX = Math.max(this.corner1.getX(), this.corner2.getX());
      int maxY = Math.max(this.corner1.getY(), this.corner2.getY());
      int maxZ = Math.max(this.corner1.getZ(), this.corner2.getZ());
      return pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY && pos.getY() <= maxY && pos.getZ() >= minZ && pos.getZ() <= maxZ;
   }

   public boolean isAbilityBanned(ManasSkill ability) {
      Set<Integer> modes = this.bannedAbilities.get(ability);
      return modes != null && modes.isEmpty();
   }

   public boolean isAbilityBanned(ManasSkill ability, int mode) {
      Set<Integer> modes = this.bannedAbilities.get(ability);
      return modes != null && (modes.isEmpty() || modes.contains(mode));
   }

   public Set<Integer> getBannedModes(ManasSkill ability) {
      return this.bannedAbilities.getOrDefault(ability, Set.of());
   }

   public void addBannedAbility(ManasSkill ability, int mode) {
      Set<Integer> modes = this.bannedAbilities.get(ability);
      if (modes == null || !modes.isEmpty()) {
         this.bannedAbilities.computeIfAbsent(ability, k -> new HashSet<>()).add(mode);
      }
   }

   public void addBannedAbility(ManasSkill ability) {
      this.bannedAbilities.put(ability, new HashSet<>());
   }

   public void removeBannedAbility(ManasSkill ability) {
      this.bannedAbilities.remove(ability);
   }

   public void removeBannedMode(ManasSkill ability, int mode) {
      Set<Integer> modes = this.bannedAbilities.get(ability);
      if (modes != null) {
         modes.remove(mode);
         if (modes.isEmpty()) {
            this.bannedAbilities.remove(ability);
         }
      }
   }

   public void clearBannedAbilities() {
      this.bannedAbilities.clear();
   }

   public Component getDataMessage() {
      MutableComponent component = Component.translatable(
         "tensura.world_restriction.get.dimension", new Object[]{Component.literal(this.dimension.location().toString()).withStyle(ChatFormatting.AQUA)}
      );
      if (this.isGlobal()) {
         component.append("\n");
         component.append(Component.translatable("tensura.world_restriction.get.global").withStyle(ChatFormatting.AQUA));
      } else {
         String c1 = "[" + this.corner1.getX() + ", " + this.corner1.getY() + ", " + this.corner1.getZ() + "]";
         String c2 = "[" + this.corner2.getX() + ", " + this.corner2.getY() + ", " + this.corner2.getZ() + "]";
         component.append("\n");
         component.append(Component.translatable("tensura.world_restriction.get.corner1", new Object[]{Component.literal(c1).withStyle(ChatFormatting.AQUA)}));
         component.append("\n");
         component.append(Component.translatable("tensura.world_restriction.get.corner2", new Object[]{Component.literal(c2).withStyle(ChatFormatting.AQUA)}));
      }

      if (!this.bannedAbilities.isEmpty()) {
         component.append("\n");
         component.append(Component.translatable("tensura.world_restriction.get.banned_abilities"));
         this.bannedAbilities.forEach((ability, modes) -> {
            component.append("\n");
            MutableComponent line = ability.getChatDisplayName(true).copy().withStyle(ChatFormatting.AQUA);
            String modeStr = modes.isEmpty() ? "all" : modes.toString();
            line.append(Component.literal(" -> ").withStyle(ChatFormatting.WHITE));
            line.append(Component.literal(modeStr).withStyle(ChatFormatting.RED));
            component.append(line);
         });
      }

      return component;
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      if (this.corner1 != null) {
         JsonObject c1 = new JsonObject();
         c1.addProperty("x", this.corner1.getX());
         c1.addProperty("y", this.corner1.getY());
         c1.addProperty("z", this.corner1.getZ());
         json.add("corner1", c1);
      }

      if (this.corner2 != null) {
         JsonObject c2 = new JsonObject();
         c2.addProperty("x", this.corner2.getX());
         c2.addProperty("y", this.corner2.getY());
         c2.addProperty("z", this.corner2.getZ());
         json.add("corner2", c2);
      }

      json.addProperty("dimension", this.dimension.location().toString());
      if (!this.bannedAbilities.isEmpty()) {
         JsonArray bannedArray = new JsonArray();
         this.bannedAbilities.forEach((ability, modes) -> {
            JsonObject entry = new JsonObject();
            entry.addProperty("id", ability.getRegistryName().toString());
            if (!modes.isEmpty()) {
               JsonArray modeArray = new JsonArray();
               modes.forEach(modeArray::add);
               entry.add("modes", modeArray);
            }

            bannedArray.add(entry);
         });
         json.add("bannedAbilities", bannedArray);
      }

      return json;
   }

   public static WorldRestrictionInstance fromJson(JsonObject json) {
      BlockPos corner1 = null;
      if (json.has("corner1")) {
         JsonObject c1 = json.getAsJsonObject("corner1");
         corner1 = new BlockPos(c1.get("x").getAsInt(), c1.get("y").getAsInt(), c1.get("z").getAsInt());
      }

      BlockPos corner2 = null;
      if (json.has("corner2")) {
         JsonObject c2 = json.getAsJsonObject("corner2");
         corner2 = new BlockPos(c2.get("x").getAsInt(), c2.get("y").getAsInt(), c2.get("z").getAsInt());
      }

      ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(json.get("dimension").getAsString()));
      WorldRestrictionInstance instance = new WorldRestrictionInstance(corner1, corner2, dimension);
      if (json.has("bannedAbilities")) {
         JsonArray bannedArray = json.getAsJsonArray("bannedAbilities");
         bannedArray.forEach(elem -> {
            JsonObject entry = elem.getAsJsonObject();
            ManasSkill ability = (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(entry.get("id").getAsString()));
            if (ability != null) {
               Set<Integer> modes = new HashSet<>();
               if (entry.has("modes")) {
                  entry.getAsJsonArray("modes").forEach(m -> modes.add(m.getAsInt()));
               } else if (entry.has("mode")) {
                  modes.add(entry.get("mode").getAsInt());
               }

               instance.bannedAbilities.put(ability, modes);
            }
         });
      }

      return instance;
   }

   public void saveToFile(Path path) throws IOException {
      Files.createDirectories(path.getParent());

      try (Writer writer = Files.newBufferedWriter(path)) {
         GSON.toJson(this.toJson(), writer);
      }
   }

   public static WorldRestrictionInstance loadFromFile(Path path) throws IOException {
      try (Reader reader = Files.newBufferedReader(path)) {
         JsonObject json = (JsonObject)GSON.fromJson(reader, JsonObject.class);
         return fromJson(json);
      }
   }

   @Nullable
   @Generated
   public BlockPos getCorner1() {
      return this.corner1;
   }

   @Generated
   public void setCorner1(@Nullable BlockPos corner1) {
      this.corner1 = corner1;
   }

   @Nullable
   @Generated
   public BlockPos getCorner2() {
      return this.corner2;
   }

   @Generated
   public void setCorner2(@Nullable BlockPos corner2) {
      this.corner2 = corner2;
   }

   @Generated
   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   @Generated
   public void setDimension(ResourceKey<Level> dimension) {
      this.dimension = dimension;
   }

   @Generated
   public Map<ManasSkill, Set<Integer>> getBannedAbilities() {
      return this.bannedAbilities;
   }
}
