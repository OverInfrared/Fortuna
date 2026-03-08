package infrared.fortuna.resources.materials;

import infrared.fortuna.Fortuna;
import infrared.fortuna.Utilities;
import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.resources.enums.MaterialOreBase;
import infrared.fortuna.resources.enums.MaterialOreOverlay;
import infrared.fortuna.resources.enums.MaterialRaw;
import infrared.fortuna.resources.enums.MiningLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class OreMaterial extends Material
{
    private final MiningLevel miningLevel;
    private final MaterialRaw raw;
    private final MaterialOreBase oreBase;
    private final MaterialOreOverlay oreOverlay;

    public OreMaterial(long seed, MiningLevel level)
    {
        super(seed);

        raw = chooseMaterialRaw(level);

        // Ore generation randomness.
        miningLevel = level;
        oreBase = chooseOreBase();
        oreOverlay = chooseOreOverlay();

        name = chooseName();

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
                .add(50, MaterialOreBase.Stone).add(10, MaterialOreBase.Andesite).add(10, MaterialOreBase.Diorite)
                .add(10, MaterialOreBase.Granite).add(10, MaterialOreBase.Tuff).add(7, MaterialOreBase.Sand).add(7, MaterialOreBase.Gravel);
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
        int pattern = rng.nextInt(3);

        String prefix = rng.nextInt(10) == 0
                ? pick(RARE_PREFIX, rng)
                : pick(PREFIX, rng);

        switch (pattern)
        {
            case 0 -> name = prefix + pick(VOWELS, rng) + end;
            case 1 -> name = prefix + pick(VOWELS, rng) + pick(INFIX, rng) + pick(VOWELS, rng) + end;
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
                String registryName = name + "_ore";
                String displayName = Utilities.capitalize(name) + " Ore";
                ResourceKey<Block> oreKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, registryName));
                materialBlocks.add(new FortunaBlock(registryName, displayName, oreKey, BlockBehaviour.Properties.of().sound(SoundType.STONE)));
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
                String displayName = Utilities.capitalize(name);
                ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, name));
                materialItems.add(new FortunaItem(name, displayName, key, new Item.Properties()));
                break;
            }
            case Ingot:
            {
                // Raw Ore Item
                String rawName = "raw_" + name;
                String rawDisplayName = Utilities.capitalize(name);
                ResourceKey<Item> rawKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, rawName));
                materialItems.add(new FortunaItem(rawName, rawDisplayName, rawKey, new Item.Properties()));

                // Refined Ore Item
                String refinedName = name + "_ingot";
                String refinedDisplayName = Utilities.capitalize(name) + " Ingot";
                ResourceKey<Item> refinedKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, refinedName));
                materialItems.add(new FortunaItem(refinedName, refinedDisplayName, refinedKey, new Item.Properties()));
                break;
            }
        }
    }
}
