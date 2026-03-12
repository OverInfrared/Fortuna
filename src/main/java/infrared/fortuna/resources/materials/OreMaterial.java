package infrared.fortuna.resources.materials;

import infrared.fortuna.Utilities;
import infrared.fortuna.resources.enums.*;
import infrared.fortuna.resources.enums.ore.*;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;

import java.awt.*;

public class OreMaterial extends Material
{
    // The level required to mine the material
    private final MiningLevel miningLevel;

    // What the material is
    private final MaterialType raw;

    // The background base for the ore, i.e. stone, diorite, sand.
    private final MaterialOreBase oreBase;

    // The ore textures to overlay on the base, i.e. copper, diamond, lapis.
    private final MaterialOreOverlay oreOverlay;

    // If the material supports an ingot, which texture to use.
    private final MaterialOreIngot oreIngot;

    // If the material supports an ingot, which raw material texture to use.
    private final MaterialOreRaw oreRaw;

    // If the material is a gem, which texture
    private final MaterialOreGem oreGem;

    // Textures for the materials refined block.
    private final MaterialOreBlock materialBlock;

    private final float materialMineTime;
    private final float materialHardness;

    private final IntProvider xpRange;

    public OreMaterial(long seed, MiningLevel level)
    {
        super(seed);

        raw = chooseMaterialRaw(level);

        // Ore generation randomness.
        miningLevel = level;
        oreBase = chooseOreBase();
        oreOverlay = chooseOreOverlay();

        oreIngot = chooseOreIngot();
        oreRaw = chooseOreRaw();
        oreGem = chooseOreGem();
        materialBlock = chooseOreBlock();

        materialMineTime = chooseMiningTime();
        materialHardness = chooseHardness();

        int minXp = 1 + miningLevel.ordinal();
        int maxXp = minXp + 1 + rng.nextInt(4); // max is min+1 to min+4
        xpRange = UniformInt.of(minXp, maxXp);

        name = chooseName();

        generateOreColors();
    }

    public MiningLevel getMiningLevel()
    {
        return miningLevel;
    }

    public MaterialType getType()
    {
        return raw;
    }

    public MaterialOreBase getBase()
    {
        return oreBase;
    }

    public MaterialOreOverlay getOverlay()
    {
        return oreOverlay;
    }

    public MaterialOreIngot getIngot()
    {
        return oreIngot;
    }

    public MaterialOreRaw getRaw()
    {
        return oreRaw;
    }

    public MaterialOreGem getGem()
    {
        return oreGem;
    }

    public MaterialOreBlock getMaterialBlock()
    {
        return materialBlock;
    }

    public float getMaterialMineTime()
    {
        return materialMineTime;
    }

    public float getMaterialHardness()
    {
        return materialHardness;
    }

    public IntProvider getXpRange()
    {
        return xpRange;
    }

    private MaterialType chooseMaterialRaw(MiningLevel level)
    {
        return switch (level)
        {
            case Iron ->
            {
                Utilities.WeightedRandom<MaterialType> ironRandom = new Utilities.WeightedRandom<MaterialType>(rng.nextLong())
                        .add(65, MaterialType.Ingot).add(35, MaterialType.Gem);
                yield ironRandom.next();
            }
            case Diamond ->
            {
                Utilities.WeightedRandom<MaterialType> diamondRandom = new Utilities.WeightedRandom<MaterialType>(rng.nextLong())
                        .add(35, MaterialType.Ingot).add(60, MaterialType.Gem).add(5, MaterialType.Special);
                yield diamondRandom.next();
            }
            case Netherite ->
            {
                Utilities.WeightedRandom<MaterialType> netherRandom = new Utilities.WeightedRandom<MaterialType>(rng.nextLong())
                        .add(33, MaterialType.Ingot).add(33, MaterialType.Gem).add(33, MaterialType.Special);
                yield netherRandom.next();
            }
            default -> MaterialType.Ingot;
        };
    }

    private MaterialOreBase chooseOreBase()
    {
        Utilities.WeightedRandom<MaterialOreBase> baseRandom = new Utilities.WeightedRandom<MaterialOreBase>(rng.nextLong())
                .add(70, MaterialOreBase.Stone).add(6, MaterialOreBase.Andesite).add(6, MaterialOreBase.Diorite)
                .add(6, MaterialOreBase.Granite).add(6, MaterialOreBase.Tuff).add(3, MaterialOreBase.Sand).add(3, MaterialOreBase.Gravel);
        return baseRandom.next();
    }

    private MaterialOreOverlay chooseOreOverlay()
    {
        Utilities.WeightedRandom<MaterialOreOverlay> overlayRandom = new Utilities.WeightedRandom<MaterialOreOverlay>(rng.nextLong())
                .add(10, MaterialOreOverlay.Iron).add(10, MaterialOreOverlay.Diamond).add(10, MaterialOreOverlay.Coal)
                .add(10, MaterialOreOverlay.Redstone).add(10, MaterialOreOverlay.Emerald).add(10, MaterialOreOverlay.Gold).add(10, MaterialOreOverlay.Lapis);

        if (raw == MaterialType.Ingot)
            overlayRandom.add(10, MaterialOreOverlay.Copper);

        return overlayRandom.next();
    }

    private MaterialOreIngot chooseOreIngot()
    {
        MaterialOreIngot[] values = MaterialOreIngot.values();
        return values[rng.nextInt(values.length)];
    }

    private MaterialOreRaw chooseOreRaw()
    {
        if (oreOverlay == null)
            return MaterialOreRaw.Iron;

        if (oreOverlay == MaterialOreOverlay.Copper)
            return MaterialOreRaw.Copper;

        MaterialOreRaw[] values = new MaterialOreRaw[] { MaterialOreRaw.Iron, MaterialOreRaw.Gold };
        return values[rng.nextInt(values.length)];
    }

    private MaterialOreGem chooseOreGem()
    {
        MaterialOreGem[] values = MaterialOreGem.values();
        return values[rng.nextInt(values.length)];
    }

    private MaterialOreBlock chooseOreBlock()
    {
        return switch (raw)
        {
            case Ingot -> {
                if (oreOverlay == MaterialOreOverlay.Copper)
                    yield MaterialOreBlock.Copper;

                MaterialOreBlock[] values = new MaterialOreBlock[] { MaterialOreBlock.Iron, MaterialOreBlock.Gold, MaterialOreBlock.Netherite };
                yield values[rng.nextInt(values.length)];
            }
            case Gem -> {
                MaterialOreBlock[] values = new MaterialOreBlock[] { MaterialOreBlock.Amethyst, MaterialOreBlock.Diamond, MaterialOreBlock.Amethyst, MaterialOreBlock.Emerald, MaterialOreBlock.Lapis, };
                yield values[rng.nextInt(values.length)];
            }
            case Special -> {
                MaterialOreBlock[] values = new MaterialOreBlock[] { MaterialOreBlock.Iron, MaterialOreBlock.Gold, MaterialOreBlock.Netherite, MaterialOreBlock.Amethyst,
                                                                     MaterialOreBlock.Diamond, MaterialOreBlock.Amethyst, MaterialOreBlock.Emerald, MaterialOreBlock.Lapis, };
                yield values[rng.nextInt(values.length)];
            }
            case null, default -> {
                yield MaterialOreBlock.Lapis;
            }
        };
    }

    private float chooseMiningTime()
    {
        float hardnessAddition = rng.nextFloat() * 3.5f;

        return switch (oreBase) {
            case Sand, Gravel   -> 0.5f + hardnessAddition;   // 0.5 - 1.0, soft
            case Stone, Andesite,
                 Diorite, Granite -> 1.5f + hardnessAddition; // 1.5 - 3.0, normal stone
            case Tuff            -> 0.5f + hardnessAddition;  // 2.0 - 3.5, slightly harder
            case Deepslate       -> 3.0f + hardnessAddition;  // 3.0 - 5.0, hard
            case Netherrack      -> hardnessAddition;         // 1.0 - 2.0, medium
        };
    }

    private float chooseHardness()
    {
        return 1f + rng.nextFloat() * 5.0f;
    }

    // Todo replace with more sophisticated naming system.
    private static final String[] PREFIX = {
            "fer", "aur", "vel", "zar", "thal", "kor", "lum", "myr", "dra", "vor",
            "sel", "gal", "nor", "ryn", "val", "tor", "mer", "ash", "bel", "cal",
            "dar", "el", "fal", "gor", "hal", "jor", "kel", "lor", "mor", "nal",
            "or", "pel", "quil", "ran", "sar", "tur", "ul", "vor", "wyn", "xer",
            "yor", "zen", "br", "cr", "dr", "gr", "kr", "pr", "tr", "vr", "run"
    };

    private static final String[] VOWELS = {
            "a", "e", "i", "o", "u",
    };

    private static final String[] RARE_VOWELS = {
            "ae", "ai", "ea", "ei", "ia", "io", "oa", "oi", "ou", "ui"
    };

    private static final String[] INFIX = {
            "r", "l", "n", "m", "th", "sh", "k", "z", "v", "d", "dr", "vr", "ll", "rr",
            "rn", "lm", "nd", "rk", "zl", "str", "nth", "ph", "x"
    };

    private static final String[] CORE = {
            "an", "or", "ir", "el", "ar", "en", "ul", "os", "is", "um",
            "ath", "eth", "orn", "yr", "al", "er", "il", "on", "ur", "ys", "st"
    };

    private static final String[] STONE_END = {
            "ite", "ore", "stone", "ar", "al", "or", "is", "yx", "ane", "oth", "urn"
    };

    private static final String[] METAL_END = {
            "ium", "ite", "on", "ar", "or", "er", "um", "eel", "iume", "orn", "oy", "ld", "el"
    };

    private static final String[] GEM_END = {
            "ite", "ine", "yx", "al", "ar", "or", "ond", "ald", "is", "el", "yr", "yl",
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

    private String chooseVowel()
    {
        Utilities.WeightedRandom<String[]> vowelPick = new Utilities.WeightedRandom<String[]>(rng.nextLong()).add(10, VOWELS).add(4, RARE_VOWELS);
        return pick(vowelPick.next(), rng);
    }

    @Override
    protected String chooseName()
    {
        String end = switch (raw)
        {
            case Ingot -> pick(METAL_END, rng);
            case Gem -> pick(GEM_END, rng);
            case null, default -> pick(STONE_END, rng);
        };

        String name;

        String prefix = rng.nextInt(10) == 0
                ? pick(RARE_PREFIX, rng)
                : pick(PREFIX, rng);


        Utilities.WeightedRandom<Integer> patternRNG = new Utilities.WeightedRandom<Integer>(rng.nextLong()).add(10, 0).add(2, 1).add(7, 2);

        switch (patternRNG.next())
        {
            case 0 -> name = prefix + chooseVowel() + end;
            case 1 -> name = prefix + chooseVowel() + pick(INFIX, rng) + chooseVowel() + end;
            default -> name = prefix + pick(CORE, rng) + end;
        }

        return cleanupName(name);
    }

    public float getHue()
    {
        // Distinct base hues spread evenly around the color wheel
        float[] baseHues = {
                0.0f,   // red
                0.08f,  // orange
                0.15f,  // yellow
                0.25f,  // green
                0.45f,  // cyan
                0.55f,  // blue
                0.67f,  // indigo
                0.92f,  // pink/magenta
        };

        float baseHue = baseHues[rng.nextInt(baseHues.length)];
        return (baseHue + (rng.nextFloat() * 0.06f - 0.03f)) % 1.0f;
    }

    private void generateOreColors()
    {
        switch (raw)
        {
            case Ingot -> {
                float hue = getHue();

                Utilities.WeightedRandom<Integer> modeRNG = new Utilities.WeightedRandom<Integer>(rng.nextLong())
                        .add(1, 0).add(2, 1).add(3, 2);

                int mode = modeRNG.next();
                float saturation = switch (mode)
                {
                    case 0  ->        rng.nextFloat() * 0.15f;
                    case 1  -> 0.1f + rng.nextFloat() * 0.3f;
                    default -> 0.4f + rng.nextFloat() * 0.5f;
                };

                float brightness = switch (mode)
                {
                    case 0 -> 0.2f + (float) Math.pow(rng.nextFloat(), 1.5f) * 0.75f;
                    case 1 -> 0.6f + rng.nextFloat() * 0.4f;
                    default -> 0.4f + (float) Math.pow(rng.nextFloat(), 1.5f) * 0.6f;
                };

                mainColor = Color.getHSBColor(hue, saturation, brightness);
                if (oreOverlay == MaterialOreOverlay.Copper) {
                    float oxidizedShift = 0.4f + rng.nextFloat() * 0.2f;
                    float oxidizedHue = (hue + oxidizedShift) % 1.0f;
                    float oxidizedSaturation = Math.clamp(saturation + (rng.nextFloat() * 0.2f - 0.1f), 0.1f, 0.9f);
                    float oxidizedBrightness = Math.clamp(brightness + (rng.nextFloat() * 0.2f - 0.1f), 0.4f, 1.0f);
                    secondaryColor = Color.getHSBColor(oxidizedHue, oxidizedSaturation, oxidizedBrightness);
                }
            }
            case Gem, Special -> {
                float hue = getHue();
                float saturation = rng.nextFloat() < 0.2f
                        ? 0.05f + rng.nextFloat() * 0.2f             // 0.0 - 0.2, near white/crystal
                        : 0.4f + rng.nextFloat() * 0.5f;             // normal gem range
                float brightness = rng.nextFloat() < 0.2f
                        ? 0.85f + rng.nextFloat() * 0.15f            // 0.85 - 1.0, extra bright for pale gems
                        : 0.6f + rng.nextFloat() * 0.35f;
                mainColor = Color.getHSBColor(hue, saturation, brightness);
            }
        }

        borderColor = oreBase.getBorderColor();
        bottomBorderColor = oreBase.getBottomBorderColor();
    }
}
