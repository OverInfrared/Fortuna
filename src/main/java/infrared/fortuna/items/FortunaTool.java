package infrared.fortuna.items;

import com.google.gson.JsonObject;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.Utilities;
import infrared.fortuna.enums.ToolType;
import infrared.fortuna.materials.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FortunaTool extends FortunaItem implements IFortunaRecipe
{
    private final ToolType toolType;

    public FortunaTool(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties, ToolType toolType)
    {
        super(dynamicProperties, properties);
        this.toolType = toolType;

        OreMaterial material = dynamicProperties.material();

        addRequiredTexture(toolType.getHiltTexture());
        addRequiredTexture(toolType.getMaterialTexture(dynamicProperties.material().getToolVariant()));
        addRequiredTint(Color.white.getRGB());
        addRequiredTint(material.getColor().getRGB());
    }

    public ToolType getToolType()
    {
        return toolType;
    }

    @Override
    public String getModelString()
    {
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "%s:item/%s".formatted(Fortuna.MOD_ID, toolType.getHiltTexture()));
        textures.addProperty("layer1", "%s:item/%s".formatted(Fortuna.MOD_ID, toolType.getMaterialTexture(dynamicProperties.material().getToolVariant())));

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
                helper.shapedTool(this, material, toolType.getRecipePattern()));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(getRegistryName());
    }
}