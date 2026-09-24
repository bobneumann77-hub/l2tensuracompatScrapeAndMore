package dev.xkmc.l2hostility.compat.jei;

import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.network.LootDataToClient;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEICompat implements IModPlugin {
   public static final ResourceLocation ID = L2Hostility.loc("main");
   public final GLMRecipeCategory LOOT = new GLMRecipeCategory();

   public ResourceLocation getPluginUid() {
      return ID;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
      registration.addRecipeCategories(new IRecipeCategory[]{this.LOOT.init(helper)});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      registration.addRecipes(this.LOOT.getRecipeType(), List.of(new EnvyLootRecipe(), new GluttonyLootRecipe()));
      registration.addRecipes(this.LOOT.getRecipeType(), LootDataToClient.LIST_CACHE.stream().filter(ITraitLootRecipe::isValid).toList());
   }
}
