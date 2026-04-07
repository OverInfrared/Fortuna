package infrared.fortuna.items.ore;

import com.google.gson.JsonObject;
import infrared.fortuna.items.IFortunaItem;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OreFoodItem implements IFortunaRecipe, IFortunaItem
{
    @Override
    public Component getDisplayName()
    {
        return null;
    }

    @Override
    public String getRegistryName()
    {
        return "";
    }

    @Override
    public List<String> getRequiredTextures()
    {
        return List.of();
    }

    @Override
    public List<Integer> getRequiredTints()
    {
        return List.of();
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        return Map.of();
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of();
    }
}
