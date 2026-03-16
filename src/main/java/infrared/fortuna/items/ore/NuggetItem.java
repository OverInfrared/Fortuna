package infrared.fortuna.items.ore;

import com.google.gson.JsonObject;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.util.Utilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NuggetItem extends FortunaItem implements IFortunaRecipe
{
    public NuggetItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        Color color = dynamicProperties.material().getMainColor();
        Color whiteColor = dynamicProperties.material().getColor("main_white");
        Color lightColor = dynamicProperties.material().getColor("main_light");
        Color darkColor = dynamicProperties.material().getColor("main_dark");

        addRequiredTexture(dynamicProperties.material().getNugget().getTexture() + "_neutral");
        addRequiredTexture(dynamicProperties.material().getNugget().getTexture() + "_light");
        addRequiredTexture(dynamicProperties.material().getNugget().getTexture() + "_white");
        addRequiredTexture(dynamicProperties.material().getNugget().getTexture() + "_dark");
        addRequiredTint(color.getRGB());
        addRequiredTint(lightColor.getRGB());
        addRequiredTint(whiteColor.getRGB());
        addRequiredTint(darkColor.getRGB());
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item ingot = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (ingot == null)
            return new HashMap<>();

        Item nugget = this.asItem();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        // 9 nuggets -> ingot
        recipes.put(getRegistryName() + "_to_ingot",
                helper.shapedNineToBlock(ingot, nugget));

        // ingot -> 9 nuggets
        recipes.put(getRegistryName() + "_from_ingot",
                helper.shapelessNineFromBlock(nugget, ingot));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(
                getRegistryName() + "_to_ingot",
                getRegistryName() + "_from_ingot"
        );
    }
}
