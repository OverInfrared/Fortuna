package infrared.fortuna.recipes;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FortunaRecipeProvider extends RecipeProvider
{
    public FortunaRecipeProvider(HolderLookup.Provider registries)
    {
        super(registries, new NoOpRecipeOutput());
    }

    @Override
    public void buildRecipes()
    {
        // Not used — recipes are built on demand via IFortunaRecipe
    }

    public JsonObject smelting(ItemLike input, ItemLike output, float xp, int time)
    {
        SmeltingRecipe recipe = new SmeltingRecipe("", CookingBookCategory.MISC,
                Ingredient.of(input), output.asItem().getDefaultInstance(), xp, time);
        return encodeRecipe(recipe);
    }

    public JsonObject blasting(ItemLike input, ItemLike output, float xp, int time)
    {
        BlastingRecipe recipe = new BlastingRecipe("", CookingBookCategory.MISC,
                Ingredient.of(input), output.asItem().getDefaultInstance(), xp, time);
        return encodeRecipe(recipe);
    }

    public JsonObject shapelessNineFromBlock(ItemLike ingot, ItemLike block)
    {
        ShapelessRecipe recipe = new ShapelessRecipe("", CraftingBookCategory.MISC,
                ingot.asItem().getDefaultInstance().copyWithCount(9),
                List.of(Ingredient.of(block)));
        return encodeRecipe(recipe);
    }

    public JsonObject shapedNineToBlock(ItemLike block, ItemLike ingot)
    {
        // Construct directly — 3x3 grid of the ingot
        ShapedRecipe recipe = new ShapedRecipe("", CraftingBookCategory.MISC,
                ShapedRecipePattern.of(
                        java.util.Map.of('#', Ingredient.of(ingot)),
                        "###", "###", "###"
                ),
                block.asItem().getDefaultInstance());
        return encodeRecipe(recipe);
    }

    private JsonObject encodeRecipe(Recipe<?> recipe)
    {
        return Recipe.CODEC
                .encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), recipe)
                .getOrThrow()
                .getAsJsonObject();
    }

    private static class NoOpRecipeOutput implements RecipeOutput
    {
        @Override
        public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement)
        {
        }

        @Override
        public Advancement.Builder advancement()
        {
            return Advancement.Builder.recipeAdvancement();
        }

        @Override
        public void includeRootAdvancement()
        {
        }
    }
}