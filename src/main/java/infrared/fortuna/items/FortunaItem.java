package infrared.fortuna.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.ore.OreMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class FortunaItem extends Item implements IFortunaItem
{
    protected final DynamicProperties<Item, OreMaterial> dynamicProperties;

    private final List<String> requiredTextures = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    public FortunaItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
    }

    @Override
    public Component getName(ItemStack itemStack) { return dynamicProperties.displayName(); }

    @Override
    public Component getDisplayName() { return dynamicProperties.displayName(); }

    @Override
    public String getRegistryName() { return dynamicProperties.registryName(); }

    public ResourceKey<Item> getResourceKey() { return dynamicProperties.resourceKey(); }

    public DynamicProperties<Item, OreMaterial> getDynamicProperties() { return dynamicProperties; }

    protected void addRequiredTexture(String texture) { requiredTextures.add(texture); }
    protected void addRequiredTint(int color) { requiredTints.add(color); }

    @Override
    public List<String> getRequiredTextures() { return requiredTextures; }

    @Override
    public List<Integer> getRequiredTints() { return requiredTints; }
}
