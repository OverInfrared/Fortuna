package infrared.fortuna.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.resources.DynamicProperties;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class FortunaItem extends Item
{
    protected final DynamicProperties<Item, OreMaterial> dynamicProperties;

    private JsonObject itemJson;
    private JsonObject modelJson;

    private final List<String> requiredTextures = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    public FortunaItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
    }

    @Override
    public Component getName(ItemStack itemStack)
    {
        return dynamicProperties.displayName();
    }

    public Component getDisplayName()
    {
        return dynamicProperties.displayName();
    }

    public String getRegistryName()
    {
        return dynamicProperties.registryName();
    }

    public ResourceKey<Item> getResourceKey()
    {
        return dynamicProperties.resourceKey();
    }

    protected void addRequiredTexture(String texture)
    {
        requiredTextures.add(texture);
    }

    protected void addRequiredTint(int color)
    {
        requiredTints.add(color);
    }

    public String getItemString()
    {
        if (itemJson == null)
            itemJson = generateItem();
        return itemJson.toString();
    }

    private JsonObject generateItem()
    {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", "%s:item/%s".formatted(Fortuna.MOD_ID, dynamicProperties.registryName()));

        JsonArray tints = new JsonArray();
        for (int color : requiredTints)
            tints.add(buildTint(color));

        model.add("tints", tints);

        JsonObject itemModel = new JsonObject();
        itemModel.add("model", model);

        return itemModel;
    }

    public String getModelString()
    {
        if (modelJson == null)
            modelJson = generateModel();
        return modelJson.toString();
    }

    private JsonObject generateModel()
    {
        JsonObject textures = new JsonObject();
        for (int i = 0; i < requiredTextures.size(); i++)
        {
            String texture = requiredTextures.get(i);
            textures.addProperty("layer" + i, "%s:item/%s".formatted(Fortuna.MOD_ID, texture));
        }

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        model.add("textures", textures);

        return model;
    }

    private JsonObject buildTint(int color)
    {
        JsonObject border = new JsonObject();
        border.addProperty("type", "minecraft:constant");
        border.addProperty("value", color);
        return border;
    }

}
