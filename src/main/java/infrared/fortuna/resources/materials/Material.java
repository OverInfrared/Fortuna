package infrared.fortuna.resources.materials;

import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.items.FortunaBlockItem;
import infrared.fortuna.items.FortunaItem;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Material
{
    protected String name = null;
    protected Random rng;

    protected final List<FortunaItem>  materialItems  = new ArrayList<>();
    protected final List<FortunaBlock> materialBlocks = new ArrayList<>();

    public Material(long seed)
    {
        rng = new Random(seed);
    }

    public String getName()
    {
        return name;
    }

    public List<FortunaItem> getItems()
    {
        return materialItems;
    }

    public List<FortunaBlock> getBlocks()
    {
        return materialBlocks;
    }

    protected abstract String chooseName();

    protected static String pick(String[] array, Random rng)
    {
        return array[rng.nextInt(array.length)];
    }

}