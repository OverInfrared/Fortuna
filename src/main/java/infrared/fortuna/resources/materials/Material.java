package infrared.fortuna.resources.materials;

import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.items.FortunaBlockItem;
import infrared.fortuna.items.FortunaItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.ColorRGBA;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Material
{
    protected String name = null;
    protected Random rng;

    protected Color mainColor = Color.white;
    protected Color secondaryColor = Color.white;
    protected Color tertiaryColor = Color.white;
    protected Color borderColor = Color.white;
    protected Color bottomBorderColor = Color.white;

    protected final List<FortunaItem>  materialItems  = new ArrayList<>();
    protected final List<IFortunaBlock> materialBlocks = new ArrayList<>();

    public Material(long seed)
    {
        rng = new Random(seed);
    }

    public String getName()
    {
        return name;
    }

    public int getColor()
    {
        return mainColor.getRGB();
    }

    public ColorRGBA getColorRGBA()
    {
        return new ColorRGBA(mainColor.getRGB());
    }

    public int getSecondaryColor()
    {
        return secondaryColor.getRGB();
    }

    public int getTertiaryColor()
    {
        return tertiaryColor.getRGB();
    }

    public int getBorderColor()
    {
        return borderColor.getRGB();
    }

    public int getBottomBorderColor()
    {
        return bottomBorderColor.getRGB();
    }

    public List<FortunaItem> getItems()
    {
        return materialItems;
    }

    public List<IFortunaBlock> getBlocks()
    {
        return materialBlocks;
    }

    protected abstract String chooseName();

    protected static String pick(String[] array, Random rng)
    {
        return array[rng.nextInt(array.length)];
    }

}