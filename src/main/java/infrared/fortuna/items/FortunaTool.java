package infrared.fortuna.items;

import com.google.gson.JsonObject;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.enums.DynamicToolType;
import infrared.fortuna.materials.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FortunaTool extends FortunaItem implements IFortunaRecipe
{
    private final DynamicToolType dynamicToolType;

    public FortunaTool(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties, DynamicToolType dynamicToolType)
    {
        super(dynamicProperties, applyToolProperties(properties, dynamicToolType, dynamicProperties.material().getToolMaterial()));
        this.dynamicToolType = dynamicToolType;

        OreMaterial material = dynamicProperties.material();

        addRequiredTexture(dynamicToolType.getHiltTexture());
        addRequiredTexture(dynamicToolType.getMaterialTexture(dynamicProperties.material().getToolVariant()));
        addRequiredTint(Color.white.getRGB());
        addRequiredTint(material.getColor().getRGB());
    }

    public DynamicToolType getToolType()
    {
        return dynamicToolType;
    }

    private static Properties applyToolProperties(Properties properties, DynamicToolType tool, ToolMaterial material)
    {
        return switch (tool)
        {
            case Sword   -> properties.sword(material, 3.0f, -2.4f);
            case Pickaxe -> properties.pickaxe(material, 1.0f, -2.8f);
            case Axe     -> properties.axe(material, 5.0f, -3.0f);
            case Shovel  -> properties.shovel(material, 1.5f, -3.0f);
            case Hoe     -> properties.hoe(material, -3.0f, 0.0f);
        };
    }

    @Override
    public String getModelString()
    {
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "%s:item/%s".formatted(Fortuna.MOD_ID, dynamicToolType.getHiltTexture()));
        textures.addProperty("layer1", "%s:item/%s".formatted(Fortuna.MOD_ID, dynamicToolType.getMaterialTexture(dynamicProperties.material().getToolVariant())));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/handheld");
        model.add("textures", textures);

        return model.toString();
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item material = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (material == null)
            return new HashMap<>();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        recipes.put(getRegistryName(),
                helper.shapedTool(this, material, dynamicToolType.getRecipePattern()));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(getRegistryName());
    }
}