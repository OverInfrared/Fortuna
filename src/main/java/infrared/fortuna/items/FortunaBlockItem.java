package infrared.fortuna.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FortunaBlockItem extends BlockItem
{
    private Component displayName = Component.literal("fortuna item");

    public FortunaBlockItem(Block block, Properties properties)
    {
        super(block, properties);
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
