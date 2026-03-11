package infrared.fortuna.resources.materials;

import infrared.fortuna.Fortuna;
import infrared.fortuna.Utilities;
import infrared.fortuna.blocks.ore.*;
import infrared.fortuna.items.GemItem;
import infrared.fortuna.items.IngotItem;
import infrared.fortuna.items.RawItem;
import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.*;
import infrared.fortuna.resources.enums.ore.*;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.awt.*;

public class OreMaterial extends Material
{
    // The level required to mine the material
    private final MiningLevel miningLevel;

    // What the material is
    private final MaterialRaw raw;

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

    private MaterialRaw chooseMaterialRaw(MiningLevel level)
    {
        return switch (level)
        {
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
            case Netherite ->
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
                .add(10, MaterialOreOverlay.Redstone).add(10, MaterialOreOverlay.Emerald).add(10, MaterialOreOverlay.Gold).add(10, MaterialOreOverlay.Lapis);

        if (raw == MaterialRaw.Ingot)
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
        float hardnessAddition = rng.nextFloat() * 2.5f;

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

    // Todo generate correct block properties.
    private void createOreBlocks()
    {
        switch (raw)
        {
            case Ingot:
                // If an ingot make the raw block
                String rawRegistryName = name + "_raw_block";
                ResourceKey<Block> rawBlockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, rawRegistryName));
                FortunaProperties<Block> rawBlockProperties = new FortunaProperties<>(rawRegistryName, Component.literal("Block of Raw " + Utilities.capitalize(name)), rawBlockKey);
                materialBlocks.add(new RawMaterialBlock(rawBlockProperties, BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F), this));
            case Gem:
            case Special:
                // Ore block
                String registryName = name + "_ore";
                ResourceKey<Block> oreKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, registryName));

                FortunaProperties<Block> oreFortuna = new FortunaProperties<>(registryName, Component.literal(Utilities.capitalize(name) + " Ore"), oreKey);
                BlockBehaviour.Properties oreProperties = BlockBehaviour.Properties.of().strength(materialMineTime, materialHardness).requiresCorrectToolForDrops();

                if (oreBase == MaterialOreBase.Sand || oreBase == MaterialOreBase.Gravel)
                {
                    // Create falling sand ore block
                    materialBlocks.add(new FallingOreBlock(oreFortuna, oreProperties, this));
                }
                else
                {
                    materialBlocks.add(new OreBlock(oreFortuna, oreProperties, this));
                    if (oreBase == MaterialOreBase.Stone)
                    {
                        String deepslateRegistryName = "deepslate_" + name + "_ore";
                        ResourceKey<Block> deepslateOreKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, deepslateRegistryName));
                        FortunaProperties<Block> deepslateOreProperties = new FortunaProperties<>(deepslateRegistryName, Component.literal("Deepslate " + Utilities.capitalize(name) + " Ore"), deepslateOreKey);
                        materialBlocks.add(new OreBlock(deepslateOreProperties, BlockBehaviour.Properties.of().sound(SoundType.STONE), this, MaterialOreBase.Deepslate));
                    }
                }

                // The main material block
                if (oreOverlay == MaterialOreOverlay.Copper) {
                    createWeatheredBlocks();
                } else {
                    String materialBlockReg = name + "_block";
                    ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, materialBlockReg));
                    FortunaProperties<Block> blockProperties = new FortunaProperties<>(materialBlockReg, Component.literal("Block of " + Utilities.capitalize(name)), blockKey);
                    materialBlocks.add(new MaterialBlock(blockProperties, BlockBehaviour.Properties.of().sound(SoundType.STONE), this));
                }
        }
    }

    private void createWeatheredBlocks()
    {
        // Register 4 weathering variants
        String[] prefixes = {"", "exposed_", "weathered_", "oxidized_"};
        String[] waxedPrefixes = {"waxed_", "waxed_exposed_", "waxed_weathered_", "waxed_oxidized_"};
        String[] waxedDisplayPrefixes = {"Waxed ", "Waxed Exposed ", "Waxed Weathered ", "Waxed Oxidized "};
        WeatheringCopper.WeatherState[] states = {
                WeatheringCopper.WeatherState.UNAFFECTED,
                WeatheringCopper.WeatherState.EXPOSED,
                WeatheringCopper.WeatherState.WEATHERED,
                WeatheringCopper.WeatherState.OXIDIZED
        };

        WeatheringMaterialBlock[] weatheringBlocks = new WeatheringMaterialBlock[4];
        MaterialBlock[] waxedBlocks = new MaterialBlock[4];
        for (int i = 0; i < 4; i++) {
            String materialBlockReg = prefixes[i] + name + "_block";
            ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, materialBlockReg));
            FortunaProperties<Block> blockProperties = new FortunaProperties<>(materialBlockReg, Component.literal(Utilities.capitalize(prefixes[i].replace("_", " ")) + "Block of " + Utilities.capitalize(name)), blockKey);
            weatheringBlocks[i] = new WeatheringMaterialBlock(blockProperties, BlockBehaviour.Properties.of().sound(SoundType.STONE), this, states[i]);
            materialBlocks.add(weatheringBlocks[i]);

            // Waxed block
            String waxedBlockReg = waxedPrefixes[i] + name + "_block";
            ResourceKey<Block> waxedBlockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, waxedBlockReg));
            FortunaProperties<Block> waxedBlockProperties = new FortunaProperties<>(waxedBlockReg, Component.literal(waxedDisplayPrefixes[i] + "Block of " + Utilities.capitalize(name)), waxedBlockKey);
            waxedBlocks[i] = new MaterialBlock(waxedBlockProperties, BlockBehaviour.Properties.of().sound(SoundType.STONE), this, states[i]);
            materialBlocks.add(waxedBlocks[i]);
        }

        OxidizableBlocksRegistry.registerOxidizableBlockPair(weatheringBlocks[0], weatheringBlocks[1]);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(weatheringBlocks[1], weatheringBlocks[2]);
        OxidizableBlocksRegistry.registerOxidizableBlockPair(weatheringBlocks[2], weatheringBlocks[3]);

        OxidizableBlocksRegistry.registerWaxableBlockPair(weatheringBlocks[0], waxedBlocks[0]);
        OxidizableBlocksRegistry.registerWaxableBlockPair(weatheringBlocks[1], waxedBlocks[1]);
        OxidizableBlocksRegistry.registerWaxableBlockPair(weatheringBlocks[2], waxedBlocks[2]);
        OxidizableBlocksRegistry.registerWaxableBlockPair(weatheringBlocks[3], waxedBlocks[3]);
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

                float t1 = rng.nextFloat();
                float t2 = rng.nextFloat();
                float t = Math.abs(t1 - 0.5f) > Math.abs(t2 - 0.5f) ? t1 : t2;
                float saturation = t * 0.8f;

                float brightness = 0.3f + (float) Math.pow(rng.nextFloat(), 1.5f) * 0.7f;
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
                        ? rng.nextFloat() * 0.2f                     // 0.0 - 0.2, near white/crystal
                        : 0.4f + rng.nextFloat() * 0.4f;             // normal gem range
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
