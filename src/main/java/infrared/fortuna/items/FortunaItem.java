package infrared.fortuna.items;

import infrared.fortuna.Fortuna;
import infrared.fortuna.resources.FortunaProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FortunaItem extends Item
{
    private final FortunaProperties<Item> fortunaProperties;

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
}
