package infrared.fortuna.util;

import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.blocks.ModBlocks;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.items.ModItems;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class Utilities
{

    // Seed hashing.
    public static long mixSeed(long worldSeed, int index)
    {
        long x = worldSeed;
        x ^= 0x9E3779B97F4A7C15L;
        x += index * 0xBF58476D1CE4E5B9L;
        x ^= (x >>> 30);
        x *= 0xBF58476D1CE4E5B9L;
        x ^= (x >>> 27);
        x *= 0x94D049BB133111EBL;
        x ^= (x >>> 31);
        return x;
    }

    public static class WeightedRandom<E>
    {
        private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
        private final Random random;
        private double total = 0;

        public WeightedRandom(long seed)
        {
            this(new Random(seed));
        }

        public WeightedRandom(Random random)
        {
            this.random = random;
        }

        public WeightedRandom<E> add(double weight, E result)
        {
            if (weight <= 0)
                return this;

            total += weight;
            map.put(total, result);
            return this;
        }

        public E next()
        {
            double value = random.nextDouble() * total;
            return map.higherEntry(value).getValue();
        }
    }

    public static String capitalize(String s)
    {
        if (s.isEmpty())
            return "";

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static Color lerpColor(Color a, Color b, float t)
    {
        t = Math.clamp(t, 0.0f, 1.0f);
        int r = (int) (a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int b2 = (int) (a.getBlue() + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, b2);
    }

    public static @Nullable IFortunaBlock findBlock(String registryName)
    {
        Map<String, IFortunaBlock> blocks = ModBlocks.getRegisteredBlocks();
        if (blocks.containsKey(registryName))
            return blocks.get(registryName);
        return null;
    }

    public static @Nullable FortunaItem findItem(String registryName)
    {
        Map<String, Item> items = ModItems.getRegisteredItem();
        if (items.containsKey(registryName))
        {
            Item item = items.get(registryName);
            if (item instanceof FortunaItem fortunaItem)
                return fortunaItem;
        }

        return null;
    }

    public static Color brightenColorByFactor(Color color, float factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        int i = (int)(1.0/(1.0-factor));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/factor), 255),
                Math.min((int)(g/factor), 255),
                Math.min((int)(b/factor), 255),
                alpha);
    }

    public static Color brightenColorByFactorWithCap(Color color, float factor, int cap) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        int floor = (int)(1.0 / (1.0 - factor));

        if (r == 0 && g == 0 && b == 0) {
            int v = Math.min(floor, cap);
            return new Color(v, v, v, alpha);
        }

        if (r > 0 && r < floor) r = floor;
        if (g > 0 && g < floor) g = floor;
        if (b > 0 && b < floor) b = floor;

        r = Math.min((int)(r / factor), cap);
        g = Math.min((int)(g / factor), cap);
        b = Math.min((int)(b / factor), cap);

        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));

        int minChannelDifference = 4;
        if (max - min < minChannelDifference) {
            if (r == min) r = Math.max(0, r - minChannelDifference);
            else if (g == min) g = Math.max(0, g - minChannelDifference);
            else b = Math.max(0, b - minChannelDifference);
        }

        return new Color(r, g, b, alpha);
    }

    public static Color nudgeColor(Color color, float hueShift, float satShift, float brightShift)
    {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        float hue = (hsb[0] + hueShift) % 1.0f;
        if (hue < 0) hue += 1.0f;
        float saturation = Math.clamp(hsb[1] + satShift, 0f, 1f);
        float brightness = Math.clamp(hsb[2] + brightShift, 0f, 1f);

        return Color.getHSBColor(hue, saturation, brightness);
    }

}
