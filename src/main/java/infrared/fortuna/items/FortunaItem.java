package infrared.fortuna.items;

import infrared.fortuna.Fortuna;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FortunaItem extends Item
{
    private final String registryName;
    private final Component displayName;
    private final ResourceKey<Item> resourceKey;

    public FortunaItem(String name, String displayName, ResourceKey<Item> key, Properties properties)
    {
        super(properties.setId(key));
        this.registryName = name;
        this.displayName = Component.literal(displayName);
        this.resourceKey = key;
    }

    @Override
    public Component getName(ItemStack itemStack)
    {
        return displayName;
    }

    public Component getDisplayName()
    {
        return displayName;
    }

    public String getRegistryName()
    {
        return registryName;
    }

    public ResourceKey<Item> getResourceKey()
    {
        return resourceKey;
    }
}
