package infrared.fortuna.items;

import com.google.gson.JsonObject;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.OreMaterial;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.Set;

public class FortunaTool extends FortunaItem implements IFortunaRecipe
{
    public FortunaTool(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);
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
