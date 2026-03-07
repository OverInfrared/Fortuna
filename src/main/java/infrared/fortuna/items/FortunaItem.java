package infrared.fortuna.items;

import infrared.fortuna.resources.enums.MaterialRaw;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FortunaItem extends Item
{
    private Component displayName = Component.literal("fortuna item");

    public FortunaItem(Properties properties)
    {
        super(properties);
    }

    public void setDisplayComponent(String string)
    {
        displayName = Component.literal(string);
    }

    @Override
    public Component getName(ItemStack itemStack)
    {
        return displayName;
    }
}
