package io.github.manasmods.tensura.neoforge.handler;

import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.IMultipart;
import io.github.manasmods.tensura.recipe.SpecialRecipeRegister;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@EventBusSubscriber(modid = "tensura")
public class RegistryHandlers {
   @SubscribeEvent
   private static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
      SpecialRecipeRegister.registerBrewingRecipe(new SpecialRecipeRegister.BrewingRegisterStrategy() {
         @Override
         public void registerMix(Holder<Potion> input, Item ingredient, Holder<Potion> output) {
            event.getBuilder().addMix(input, ingredient, output);
         }

         @Override
         public void registerStart(Item ingredient, Holder<Potion> potion) {
            event.getBuilder().addStartMix(ingredient, potion);
         }

         @Override
         public void registerContainer(Item input) {
            event.getBuilder().addContainer(input);
         }

         @Override
         public void registerContainerMix(Item input, Item ingredient, Item output) {
            event.getBuilder().addContainerRecipe(input, ingredient, output);
         }
      });
   }

   @SubscribeEvent
   private static void onEntityJoin(EntityJoinLevelEvent event) {
      if (event.getEntity() instanceof IMultipart multipart) {
         Int2ObjectMap<TensuraPartEntity> parts = event.getLevel().tensura$getParts();

         for (TensuraPartEntity part : multipart.getParts()) {
            parts.put(part.getId(), part);
         }
      }
   }

   @SubscribeEvent
   private static void onEntityLeave(EntityLeaveLevelEvent event) {
      if (event.getEntity() instanceof IMultipart multipart) {
         Int2ObjectMap<TensuraPartEntity> parts = event.getLevel().tensura$getParts();

         for (TensuraPartEntity part : multipart.getParts()) {
            parts.remove(part.getId());
         }
      }
   }
}
