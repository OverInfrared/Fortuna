package infrared.fortuna.resources.materials;

import infrared.fortuna.Utilities;
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

    public Color getColor()
    {
        return mainColor;
    }

    public ColorRGBA getColorRGBA()
    {
        return new ColorRGBA(mainColor.getRGB());
    }

    public Color getSecondaryColor()
    {
        return secondaryColor;
    }

    public Color getBorderColor()
    {
        return borderColor;
    }

    public Color getBottomBorderColor()
    {
        return bottomBorderColor;
    }

    public Color getTransitionColor(float transition, float saturation, float brightness)
    {
        Color color = Utilities.lerpColor(mainColor, secondaryColor, transition);
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1] * saturation, hsb[2] * brightness);
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