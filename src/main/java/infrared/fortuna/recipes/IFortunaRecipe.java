package infrared.fortuna.recipes;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;

import java.util.Map;
import java.util.Set;

/**
 * Implement on any block or item that should generate recipes at runtime.
 * Returns a map of recipe name -> recipe JSON, since a single item
 * might need multiple recipes (crafting, smelting, blasting, etc.)
 */
public interface IFortunaRecipe {
    Map<String, JsonObject> getRecipes(HolderLookup.Provider registries);

    Set<String> getRecipeNames();
}