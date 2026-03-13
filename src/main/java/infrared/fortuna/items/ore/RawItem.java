package infrared.fortuna.items.ore;

import com.google.gson.JsonObject;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.enums.ore.MaterialOreRaw;
import infrared.fortuna.materials.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RawItem extends FortunaItem implements IFortunaRecipe
{
    public RawItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        OreMaterial material = dynamicProperties.material();
        MaterialOreRaw rawOre = material.getMaterialType();

        addRequiredTexture(rawOre.isOxidizable() ? rawOre.getTexture() + "_base" : rawOre.getTexture());
        addRequiredTint(material.getColor().getRGB());

        if (rawOre.isOxidizable())
        {
            addRequiredTexture(rawOre.getTexture() + "_oxidized");
            addRequiredTint(material.getSecondaryColor().getRGB());
            addRequiredTexture(rawOre.getTexture() + "_transition");
            addRequiredTint(material.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
        }
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item ingot = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (ingot == null)
            return new HashMap<>();

        Item rawItem = this.asItem();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        recipes.put(getRegistryName() + "_smelting",
                helper.smelting(rawItem, ingot, 0.7f, 200));

        recipes.put(getRegistryName() + "_blasting",
                helper.blasting(rawItem, ingot, 0.7f, 100));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(
                getRegistryName() + "_smelting",
                getRegistryName() + "_blasting"
        );
    }
}
