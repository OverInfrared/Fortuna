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

}
