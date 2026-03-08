package infrared.fortuna.resources;

import infrared.fortuna.resources.enums.MaterialOreBase;
import infrared.fortuna.resources.enums.MaterialOreOverlay;
import infrared.fortuna.resources.enums.MaterialRaw;
import infrared.fortuna.resources.enums.MiningLevel;
import infrared.fortuna.Utilities.WeightedRandom;
import net.minecraft.world.item.Item;

import java.util.Random;

public class Material
{
    protected final String name;
    protected final Item.Properties itemProperties = new Item.Properties();

    protected Random rng;

    public Material(long seed, MiningLevel level)
    {
        rng = new Random(seed);
    }

    public MiningLevel getMiningLevel()
    {
        return miningLevel;
    }

    public MaterialRaw getMaterialRaw()
    {
        return raw;
    }

    public MaterialOreBase getMaterialOreBase()
    {
        return oreBase;
    }

    public MaterialOreOverlay getMaterialOreOverlay()
    {
        return oreOverlay;
    }

    public Item.Properties getItemProperties()
    {
        return itemProperties;
    }

    public String getName()
    {
        return name;
    }

    protected abstract String generateName();

    // Todo replace with more sophisticated naming system.
    private static final String[] PREFIX = {
            "fer", "aur", "vel", "zar", "thal", "kor", "lum", "myr", "dra", "vor",
            "sel", "gal", "nor", "ryn", "val", "tor", "mer", "ash", "bel", "cal",
            "dar", "el", "fal", "gor", "hal", "jor", "kel", "lor", "mor", "nal",
            "or", "pel", "quil", "ran", "sar", "tur", "ul", "vor", "wyn", "xer",
            "yor", "zen", "br", "cr", "dr", "gr", "kr", "pr", "tr", "vr"
    };

    private static final String[] VOWELS = {
            "a", "e", "i", "o", "u", "ae", "ai", "ea", "ei", "ia", "io", "oa", "oi", "ou", "ui"
    };

    private static final String[] INFIX = {
            "r", "l", "n", "m", "th", "sh", "k", "z", "v", "d", "dr", "vr", "ll", "rr",
            "rn", "lm", "nd", "rk", "zl", "str", "nth", "ph", "x"
    };

    private static final String[] CORE = {
            "an", "or", "ir", "el", "ar", "en", "ul", "os", "is", "um",
            "ath", "eth", "orn", "yr", "al", "er", "il", "on", "ur", "ys"
    };

    private static final String[] STONE_END = {
            "ite", "ore", "stone", "ar", "al", "or", "is", "yx", "ane", "oth", "urn"
    };

    private static final String[] METAL_END = {
            "ium", "ite", "on", "ar", "or", "er", "um", "steel", "iume", "orn", "alloy"
    };

    private static final String[] GEM_END = {
            "ite", "ine", "yx", "al", "ar", "or", "ond", "ald", "is", "el", "yr", "yl", "ane"
    };

    private static final String[] RARE_PREFIX = {
            "aeth", "nyx", "vor", "az", "kael", "xan", "thal", "vyr", "drak", "ser"
    };

    private String cleanupName(String name)
    {
        name = name.replace("aaa", "aa");
        name = name.replace("eee", "ee");
        name = name.replace("iii", "ii");
        name = name.replace("ooo", "oo");
        name = name.replace("uuu", "uu");

        name = name.replace("yy", "y");
        name = name.replace("vvv", "v");
        name = name.replace("xxx", "x");

        return name;
    }

    private String generateName(MaterialRaw type, Random random)
    {
        String end = switch (type)
        {
            case Ingot -> pick(METAL_END, random);
            case Gem -> pick(GEM_END, random);
            case null, default -> pick(STONE_END, random);
        };

        String name;
        int pattern = random.nextInt(3);

        String prefix = random.nextInt(10) == 0
                ? pick(RARE_PREFIX, random)
                : pick(PREFIX, random);

        switch (pattern)
        {
            case 0 -> name = prefix + pick(VOWELS, random) + end;
            case 1 -> name = prefix + pick(VOWELS, random) + pick(INFIX, random) + pick(VOWELS, random) + end;
            default -> name = prefix + pick(CORE, random) + end;
        }

        return cleanupName(name);
    }

    private static String pick(String[] array, Random random)
    {
        return array[random.nextInt(array.length)];
    }
}
