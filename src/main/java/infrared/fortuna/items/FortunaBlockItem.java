package infrared.fortuna.items;

import infrared.fortuna.blocks.FortunaBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class FortunaBlockItem extends BlockItem
{
    private final Component displayName;

    public FortunaBlockItem(Block block, Component displayName, Properties properties)
    {
        super(block, properties);
        this.displayName = displayName;
    }

    @Override
    public Component getName(ItemStack itemStack)
    {
        return displayName;
    }
}
