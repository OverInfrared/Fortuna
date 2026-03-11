package infrared.fortuna.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.resources.FortunaProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FortunaItem extends Item
{
    protected final FortunaProperties<Item> fortunaProperties;

    private JsonObject itemJson;
    private JsonObject modelJson;

    private final List<String> requiredTextures = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    public FortunaItem(FortunaProperties<Item> fortunaProps, Properties properties)
    {
        super(properties.setId(fortunaProps.resourceKey()));
        fortunaProperties = fortunaProps;
    }

    @Override
    public Component getName(ItemStack itemStack)
    {
        return fortunaProperties.displayName();
    }

    public Component getDisplayName()
    {
        return fortunaProperties.displayName();
    }

    public String getRegistryName()
    {
        return fortunaProperties.registryName();
    }

    public ResourceKey<Item> getResourceKey()
    {
        return fortunaProperties.resourceKey();
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
        model.addProperty("model", "%s:item/%s".formatted(Fortuna.MOD_ID, fortunaProperties.registryName()));

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
