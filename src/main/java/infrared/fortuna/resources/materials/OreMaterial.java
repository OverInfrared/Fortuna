package infrared.fortuna.resources.materials;

import infrared.fortuna.Fortuna;
import infrared.fortuna.Utilities;
import infrared.fortuna.blocks.OreBlock;
import infrared.fortuna.items.GemItem;
import infrared.fortuna.items.IngotItem;
import infrared.fortuna.items.RawItem;
import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.awt.*;

public class OreMaterial extends Material
{
    private final MiningLevel miningLevel;
    private final MaterialRaw raw;
    private final MaterialOreBase oreBase;
    private final MaterialOreOverlay oreOverlay;

    private final MaterialOreIngot oreIngot;
    private final MaterialOreRaw oreRaw;
    private final MaterialOreGem oreGem;

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

        name = chooseName();

        generateOreColors();

        createOreBlocks();
        createOreItems();
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

    public MaterialOreIngot getMaterialOreIngot()
    {
        return oreIngot;
    }

    public MaterialOreRaw getMaterialOreRaw()
    {
        return oreRaw;
    }

    public MaterialOreGem getMaterialOreGem()
    {
        return oreGem;
    }

    private MaterialRaw chooseMaterialRaw(MiningLevel level)
    {
        return switch (level)
        {
            case Stone -> MaterialRaw.Stone;
            case Iron ->
            {
                Utilities.WeightedRandom<MaterialRaw> ironRandom = new Utilities.WeightedRandom<MaterialRaw>(rng.nextLong())
                        .add(65, MaterialRaw.Ingot).add(35, MaterialRaw.Gem);
                yield ironRandom.next();
            }
            case Diamond ->
            {
                Utilities.WeightedRandom<MaterialRaw> diamondRandom = new Utilities.WeightedRandom<MaterialRaw>(rng.nextLong())
                        .add(35, MaterialRaw.Ingot).add(60, MaterialRaw.Gem).add(5, MaterialRaw.Special);
                yield diamondRandom.next();
            }
            case Netherite, Fortuna ->
            {
                Utilities.WeightedRandom<MaterialRaw> netherRandom = new Utilities.WeightedRandom<MaterialRaw>(rng.nextLong())
                        .add(33, MaterialRaw.Ingot).add(33, MaterialRaw.Gem).add(33, MaterialRaw.Special);
                yield netherRandom.next();
            }
            default -> MaterialRaw.Ingot;
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
                .add(10, MaterialOreOverlay.Redstone).add(10, MaterialOreOverlay.Emerald).add(10, MaterialOreOverlay.Copper)
                .add(10, MaterialOreOverlay.Gold).add(10, MaterialOreOverlay.Lapis);
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

    // Todo replace with more sophisticated naming system.
    private static final String[] PREFIX = {
            "fer", "aur", "vel", "zar", "thal", "kor", "lum", "myr", "dra", "vor",
            "sel", "gal", "nor", "ryn", "val", "tor", "mer", "ash", "bel", "cal",
            "dar", "el", "fal", "gor", "hal", "jor", "kel", "lor", "mor", "nal",
            "or", "pel", "quil", "ran", "sar", "tur", "ul", "vor", "wyn", "xer",
            "yor", "zen", "br", "cr", "dr", "gr", "kr", "pr", "tr", "vr"
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

        Utilities.WeightedRandom<Integer> patternRNG = new Utilities.WeightedRandom<Integer>(rng.nextLong()).add(7, 0).add(2, 1).add(5, 2);
        int pattern = patternRNG.next();

        String prefix = rng.nextInt(10) == 0
                ? pick(RARE_PREFIX, rng)
                : pick(PREFIX, rng);

        switch (pattern)
        {
            case 0 -> name = prefix + chooseVowel() + end;
            case 1 -> name = prefix + chooseVowel() + pick(INFIX, rng) + chooseVowel() + end;
            default -> name = prefix + pick(CORE, rng) + end;
        }

        return cleanupName(name);
    }

    // Todo generate correct block properties.
    private void createOreBlocks()
    {
        switch (raw)
        {
            case Gem:
            case Ingot:
            case Special:
                String registryName = name + "_ore";
                ResourceKey<Block> oreKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, registryName));
                FortunaProperties<Block> oreProperties = new FortunaProperties<>(registryName, Component.literal(Utilities.capitalize(name) + " Ore"), oreKey);
                materialBlocks.add(new OreBlock(oreProperties, BlockBehaviour.Properties.of().sound(SoundType.STONE), this));
                break;
        }
    }

    private void createOreItems()
    {
        // Generate raw and refined items.
        switch (raw)
        {
            case Gem:
            case Special:
            {
                ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, name));
                FortunaProperties<Item> itemProperties = new FortunaProperties<>(name, Component.literal(Utilities.capitalize(name)), key);
                materialItems.add(new GemItem(itemProperties, new Item.Properties(), this));
                break;
            }
            case Ingot:
            {
                // Raw Ore Item
                String rawName = "raw_" + name;
                ResourceKey<Item> rawKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, rawName));
                FortunaProperties<Item> rawProps = new FortunaProperties<>(rawName, Component.literal("Raw " + Utilities.capitalize(name)), rawKey);
                materialItems.add(new RawItem(rawProps, new Item.Properties(), this));

                // Refined Ore Item
                String refinedName = name + "_ingot";
                ResourceKey<Item> refinedKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, refinedName));
                FortunaProperties<Item> refinedProps = new FortunaProperties<>(refinedName, Component.literal(Utilities.capitalize(name) + " Ingot"), refinedKey);
                materialItems.add(new IngotItem(refinedProps, new Item.Properties(), this));
                break;
            }
        }
    }

    private void generateOreColors()
    {
        switch (raw)
        {
            case Ingot -> {
                float raw = rng.nextFloat();
                float hue = raw < 0.75f ? raw : 0.85f + (raw - 0.75f) * (1.0f / 0.25f);
                float saturation = 0.25f + rng.nextFloat() * 0.4f;
                float brightness = 0.55f + rng.nextFloat() * 0.45f;
                mainColor = Color.getHSBColor(hue % 1.0f, saturation, brightness);

                if (oreOverlay == MaterialOreOverlay.Copper) {
                    float oxidizedShift = 0.4f + rng.nextFloat() * 0.2f;
                    float oxidizedHue = (hue + oxidizedShift) % 1.0f;
                    // Keep saturation close to main with small variance
                    float oxidizedSaturation = saturation + (rng.nextFloat() * 0.2f - 0.1f); // ±0.1
                    float oxidizedBrightness = brightness + (rng.nextFloat() * 0.2f - 0.1f); // ±0.1
                    oxidizedSaturation = Math.clamp(oxidizedSaturation, 0.1f, 0.9f);
                    oxidizedBrightness = Math.clamp(oxidizedBrightness, 0.4f, 1.0f);
                    secondaryColor = Color.getHSBColor(oxidizedHue, oxidizedSaturation, oxidizedBrightness);

                    float transitionHue = (hue + oxidizedShift * 0.5f) % 1.0f;
                    float transitionSaturation = (saturation + oxidizedSaturation) / 2.0f;
                    float transitionBrightness = (brightness + oxidizedBrightness) / 2.0f;
                    tertiaryColor = Color.getHSBColor(transitionHue, transitionSaturation, transitionBrightness);
                }
            }
            case Gem, Special -> {
                float raw = rng.nextFloat();
                float hue = raw < 0.75f ? raw : 0.85f + (raw - 0.75f) * (1.0f / 0.25f);
                float saturation = 0.4f + rng.nextFloat() * 0.4f;
                float brightness = 0.6f + rng.nextFloat() * 0.35f;
                mainColor = Color.getHSBColor(hue % 1.0f, saturation, brightness);
            }
        }

        switch (oreBase)
        {
            case Sand -> {
                borderColor = new Color(209, 186,138);
                bottomBorderColor = new Color(231, 228, 187);
            }
            case Stone -> {
                borderColor = new Color(110, 110, 110);
                bottomBorderColor = new Color(150, 150, 150);
            }
            case Granite -> {
                borderColor = new Color(127, 86, 70);
                bottomBorderColor = new Color(169, 119, 100);
            }
            case Gravel -> {
                borderColor = new Color(114, 107, 105);
                bottomBorderColor = new Color(150, 142, 142);
            }
            case Andesite -> {
                borderColor = new Color(116, 116, 116);
                bottomBorderColor = new Color(156, 156, 156);
            }
            case Diorite -> {
                borderColor = new Color(139, 139, 139);
                bottomBorderColor = new Color(233, 233, 233);
            }
            case Tuff -> {
                borderColor = new Color(93, 93, 80);
                bottomBorderColor = new Color(133, 131, 123);
            }
        }
    }
}
