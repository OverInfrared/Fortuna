package infrared.fortuna.materials;

import infrared.fortuna.util.Utilities;
import net.minecraft.util.ColorRGBA;

import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class Material
{
    protected String name = null;
    protected Random rng;

    protected Map<String, Color> materialColors = new HashMap<>(Map.of("main", Color.white, "secondary", Color.white));

    public Material(long seed)
    {
        rng = new Random(seed);
    }

    public String getName()
    {
        return name;
    }

    public Color getMainColor()      { return getColor("main"); }
    public Color getSecondaryColor() { return getColor("secondary"); }

    public void setColor(String name, Color color) { materialColors.put(name, color); }
    public Color getColor(String name) { return materialColors.getOrDefault(name, Color.WHITE); }

    public ColorRGBA getMainColorRGBA() { return new ColorRGBA(getMainColor().getRGB()); }

    public Color getTransitionColor(float transition, float saturation, float brightness)
    {
        Color color = Utilities.lerpColor(getMainColor(), getSecondaryColor(), transition);
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1] * saturation, hsb[2] * brightness);
    }

    protected abstract String chooseName();

    protected static String pick(String[] array, Random rng)
    {
        return array[rng.nextInt(array.length)];
    }

}