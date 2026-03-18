package infrared.fortuna.items;

import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.blocks.IFortunaBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class FortunaBlockItem extends BlockItem implements IFortunaItem
{
    private final Component displayName;
    private final String registryName;
    private final List<String> requiredTextures = new ArrayList<>();

    public FortunaBlockItem(IFortunaBlock block, Component displayName, Properties properties)
    {
        super((Block)block, properties);
        this.displayName = displayName;
        registryName = block.getRegistryName();

        for (String texture : block.getRequiredItemTextures())
            addRequiredTexture(texture);
    }

    private void addRequiredTexture(String texture) { Fortuna.LOGGER.info(texture); requiredTextures.add(texture); }

    @Override
    public Component getName(ItemStack itemStack)
    {
        return displayName;
    }

    @Override
    public Component getDisplayName()
    {
        return displayName;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public List<String> getRequiredTextures()
    {
        return requiredTextures;
    }

    @Override
    public List<Integer> getRequiredTints()
    {
        return List.of();
    }
}
