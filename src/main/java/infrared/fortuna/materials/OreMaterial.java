package infrared.fortuna.materials;

import infrared.fortuna.Utilities;
import infrared.fortuna.enums.MaterialType;
import infrared.fortuna.enums.MiningLevel;
import infrared.fortuna.enums.ToolType;
import infrared.fortuna.enums.ore.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

import java.awt.*;

public class OreMaterial extends Material
{
    // === Core identity ===
    private final MiningLevel miningLevel;
    private final MaterialType materialType;

    // === Ore appearance ===
    private final MaterialOreBase oreBase;
    private final MaterialOreOverlay oreOverlay;

    // === Item/block textures ===
    private final MaterialOreIngot oreIngot;
    private final MaterialOreRaw oreRaw;
    private final MaterialOreGem oreGem;
    private final MaterialOreBlock materialBlock;

    // === Block properties ===
    private final float materialMineTime;
    private final float materialHardness;

    // === Drop properties ===
    private final IntProvider xpRange;
    private final MaterialOreDrops drops;

    // === Tool properties ===
    private final boolean makeTools;
    private final ToolMaterial toolMaterial;
    private final int toolVariant;

    // === Armor properties ===
    private final boolean makeArmor;
    private final ArmorMaterial armorMaterial;
    private final int armorVariant;

    // =========================================================================
    // Constructor
    // =========================================================================

    public OreMaterial(long seed, MiningLevel level)
    {
        super(seed);

        materialType = chooseMaterialRaw(level);

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
        int maxXp = minXp + 1 + rng.nextInt(4);
        xpRange = UniformInt.of(minXp, maxXp);
        drops = chooseDropCountType();

        makeTools = true;
        toolMaterial = chooseToolMaterial();
        toolVariant = rng.nextInt(ToolType.getVariantCount());

        makeArmor = true;
        armorMaterial =

        name = chooseName();

        generateOreColors();
    }

    // =========================================================================
    // Public getters
    // =========================================================================

    public MiningLevel getMiningLevel()         { return miningLevel; }
    public MaterialType getType()               { return materialType; }

    public MaterialOreBase getBase()            { return oreBase; }
    public MaterialOreOverlay getOverlay()      { return oreOverlay; }

    public MaterialOreIngot getIngot()          { return oreIngot; }
    public MaterialOreRaw getMaterialType()     { return oreRaw; }
    public MaterialOreGem getGem()              { return oreGem; }
    public MaterialOreBlock getMaterialBlock()  { return materialBlock; }

    public float getMaterialMineTime()          { return materialMineTime; }
    public float getMaterialHardness()          { return materialHardness; }

    public IntProvider getXpRange()             { return xpRange; }
    public MaterialOreDrops getDropType()       { return drops; }

    public boolean getHasTools()                { return makeTools; }
    public ToolMaterial getToolMaterial()       { return toolMaterial; }
    public int getToolVariant()                 { return toolVariant; }

    public boolean getHasArmor()

    // =========================================================================
    // Registry name helpers
    // =========================================================================

    public String getRawRegistryName()
    {
        return materialType == MaterialType.Ingot ? "raw_%s".formatted(name) : name;
    }

    public String getRefinedRegistryName()
    {
        return materialType == MaterialType.Ingot ? "%s_ingot".formatted(name) : name;
    }

    // =========================================================================
    // Material generation
    // =========================================================================

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
                .add(8, MaterialOreOverlay.Iron).add(8, MaterialOreOverlay.Diamond).add(8, MaterialOreOverlay.Coal)
                .add(8, MaterialOreOverlay.Redstone).add(8, MaterialOreOverlay.Emerald).add(8, MaterialOreOverlay.Gold).add(8, MaterialOreOverlay.Lapis);

        if (materialType == MaterialType.Ingot)
            overlayRandom.add(20, MaterialOreOverlay.Copper);

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
        Utilities.WeightedRandom<MaterialOreGem> gemRNG = new Utilities.WeightedRandom<MaterialOreGem>(rng.nextLong())
                .add(10, MaterialOreGem.Diamond).add(10, MaterialOreGem.Emerald).add(8, MaterialOreGem.Lapis)
                .add(7, MaterialOreGem.Resin).add(4, MaterialOreGem.Prismarine).add(4, MaterialOreGem.Amethyst);

        return gemRNG.next();
    }

    private MaterialOreBlock chooseOreBlock()
    {
        return switch (materialType)
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
                MaterialOreBlock[] values = new MaterialOreBlock[] { MaterialOreBlock.Gold, MaterialOreBlock.Netherite, MaterialOreBlock.Amethyst,
                        MaterialOreBlock.Diamond, MaterialOreBlock.Amethyst, MaterialOreBlock.Emerald, MaterialOreBlock.Lapis, };
                yield values[rng.nextInt(values.length)];
            }
            case null, default -> {
                yield MaterialOreBlock.Lapis;
            }
        };
    }

    private MaterialOreDrops chooseDropCountType()
    {
        return switch (materialType)
        {
            case Ingot -> {
                Utilities.WeightedRandom<MaterialOreDrops> dropRNG = new Utilities.WeightedRandom<MaterialOreDrops>(rng.nextLong())
                        .add(90, MaterialOreDrops.Single).add(10, MaterialOreDrops.Copper);

                yield dropRNG.next();
            }
            case Gem -> {
                Utilities.WeightedRandom<MaterialOreDrops> dropRNG = new Utilities.WeightedRandom<MaterialOreDrops>(rng.nextLong())
                        .add(90, MaterialOreDrops.Single).add(7, MaterialOreDrops.Lapis).add(7, MaterialOreDrops.Redstone);

                yield dropRNG.next();
            }
            default -> MaterialOreDrops.Single;
        };
    }

    // =========================================================================
    // Block properties
    // =========================================================================

    private float chooseMiningTime()
    {
        float hardnessAddition = rng.nextFloat() * 3.5f;

        return switch (oreBase) {
            case Sand, Gravel   -> 0.5f + hardnessAddition;
            case Stone, Andesite,
                 Diorite, Granite -> 1.5f + hardnessAddition;
            case Tuff            -> 0.5f + hardnessAddition;
            case Deepslate       -> 3.0f + hardnessAddition;
            case Netherrack      -> hardnessAddition;
        };
    }

    private float chooseHardness()
    {
        return 1f + rng.nextFloat() * 5.0f;
    }

    // =========================================================================
    // Tool properties
    // =========================================================================

    private ToolMaterial chooseToolMaterial()
    {
        if (!makeTools)
            return null;

        int baseDurability = switch (miningLevel)
        {
            case Iron     -> 180 + rng.nextInt(150);     // 180 - 329
            case Diamond  -> 800 + rng.nextInt(1200);    // 800 - 1999
            case Netherite -> 1500 + rng.nextInt(1000);  // 1500 - 2499
            default       -> 40 + rng.nextInt(40);       // 40 - 79
        };

        float baseSpeed = switch (miningLevel)
        {
            case Iron     -> 5.0f + rng.nextFloat() * 2.0f;    // 5.0 - 7.0
            case Diamond  -> 7.0f + rng.nextFloat() * 2.5f;    // 7.0 - 9.5
            case Netherite -> 8.0f + rng.nextFloat() * 2.0f;   // 8.0 - 10.0
            default       -> 1.5f + rng.nextFloat() * 1.5f;    // 1.5 - 3.0
        };

        float baseAttackDamage = switch (miningLevel)
        {
            case Iron     -> 1.5f + rng.nextFloat() * 1.0f;    // 1.5 - 2.5
            case Diamond  -> 2.5f + rng.nextFloat() * 1.5f;    // 2.5 - 4.0
            case Netherite -> 3.5f + rng.nextFloat() * 1.5f;   // 3.5 - 5.0
            default       -> rng.nextFloat() * 0.5f;           // 0.0 - 0.5
        };

        int baseEnchantment = switch (miningLevel)
        {
            case Iron     -> 8 + rng.nextInt(10);     // 8 - 17
            case Diamond  -> 6 + rng.nextInt(10);     // 6 - 15
            case Netherite -> 10 + rng.nextInt(10);   // 10 - 19
            default       -> 10 + rng.nextInt(10);    // 10 - 19
        };

        TagKey<Block> incorrectBlocks = switch (miningLevel)
        {
            case Iron     -> BlockTags.INCORRECT_FOR_IRON_TOOL;
            case Diamond  -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
            case Netherite -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
            default       -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        };

        // TODO: generate a custom repair item tag once item tags are dynamic
        TagKey<Item> repairItems = switch (miningLevel)
        {
            case Iron     -> ItemTags.IRON_TOOL_MATERIALS;
            case Diamond  -> ItemTags.DIAMOND_TOOL_MATERIALS;
            case Netherite -> ItemTags.NETHERITE_TOOL_MATERIALS;
            default       -> ItemTags.WOODEN_TOOL_MATERIALS;
        };

        return new ToolMaterial(incorrectBlocks, baseDurability, baseSpeed, baseAttackDamage, baseEnchantment, repairItems);
    }

    // =========================================================================
    // Tool properties
    // =========================================================================

    private ArmorMaterial chooseArmorMaterial()
    {
        if (!makeTools)
            return null;

        int durabilityMultiplier = switch (miningLevel)
        {
            case Stone    -> 8 + rng.nextInt(6);       // 8 - 13
            case Iron     -> 12 + rng.nextInt(8);      // 12 - 19
            case Diamond  -> 25 + rng.nextInt(15);     // 25 - 39
            case Netherite -> 30 + rng.nextInt(15);    // 30 - 44
            default       -> 4 + rng.nextInt(4);       // 4 - 7
        };

        int baseDefense = switch (miningLevel)
        {
            case Stone    -> 1;
            case Iron     -> 2;
            case Diamond  -> 3;
            case Netherite -> 3;
            default       -> 1;
        };

        Map<ArmorType, Integer> defense = ArmorMaterials.makeDefense(
                baseDefense,                          // boots
                baseDefense + rng.nextInt(2) + 3,     // leggings
                baseDefense + rng.nextInt(2) + 4,     // chestplate
                baseDefense,                          // helmet
                baseDefense + rng.nextInt(3) + 4      // body
        );

        int enchantmentValue = switch (miningLevel)
        {
            case Stone    -> 3 + rng.nextInt(8);
            case Iron     -> 8 + rng.nextInt(10);
            case Diamond  -> 6 + rng.nextInt(10);
            case Netherite -> 10 + rng.nextInt(10);
            default       -> 10 + rng.nextInt(10);
        };

        float toughness = switch (miningLevel)
        {
            case Diamond  -> 1.0f + rng.nextFloat() * 2.0f;    // 1.0 - 3.0
            case Netherite -> 2.0f + rng.nextFloat() * 2.0f;   // 2.0 - 4.0
            default       -> 0.0f;
        };

        float knockbackResistance = switch (miningLevel)
        {
            case Netherite -> 0.05f + rng.nextFloat() * 0.1f;  // 0.05 - 0.15
            case Diamond  -> rng.nextFloat() < 0.3f ? 0.05f : 0.0f;
            default       -> 0.0f;
        };

        // TODO: custom repair tag and equipment asset for dynamic armor
        TagKey<Item> repairItems = switch (miningLevel)
        {
            case Stone    -> ItemTags.REPAIRS_IRON_ARMOR;
            case Iron     -> ItemTags.REPAIRS_IRON_ARMOR;
            case Diamond  -> ItemTags.REPAIRS_DIAMOND_ARMOR;
            case Netherite -> ItemTags.REPAIRS_NETHERITE_ARMOR;
            default       -> ItemTags.REPAIRS_LEATHER_ARMOR;
        };

        return new ArmorMaterial(
                durabilityMultiplier,
                defense,
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_IRON,
                toughness,
                knockbackResistance,
                repairItems,
                EquipmentAssets.IRON  // placeholder — need to figure out dynamic equipment assets
        );
    }

    // =========================================================================
    // Name generation
    // =========================================================================

    private static final String[] PREFIX = {
            "fer", "aur", "vel", "zar", "thal", "kor", "lum", "myr", "dra", "vor",
            "sel", "gal", "nor", "ryn", "val", "tor", "mer", "ash", "bel", "cal",
            "dar", "el", "fal", "gor", "hal", "jor", "kel", "lor", "mor", "nal",
            "or", "pel", "quil", "ran", "sar", "tur", "ul", "vor", "wyn", "xer",
            "yor", "zen", "br", "cr", "dr", "gr", "kr", "pr", "tr", "vr", "run",
            "mi", "ar", "la", "fa", "r", "gl",
    };

    private static final String[] VOWELS = {
            "a", "e", "i", "o", "u",
    };

    private static final String[] RARE_VOWELS = {
            "ae", "ai", "ea", "ei", "ia", "io", "oa", "oi", "ou", "ui"
    };

    private static final String[] INFIX = {
            "r", "l", "n", "m", "th", "sh", "k", "z", "v", "d", "dr", "vr", "ll", "rr",
            "rn", "lm", "nd", "rk", "zl", "str", "nth", "ph", "x", "thr"
    };

    private static final String[] CORE = {
            "an", "or", "ir", "el", "ar", "en", "ul", "os", "is", "um",
            "ath", "eth", "orn", "yr", "al", "er", "il", "on", "ur", "ys", "st", "thr"
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

        String[] doubleVowels = {"aa", "ee", "ii", "oo", "uu"};
        for (String dv : doubleVowels)
        {
            if (name.contains(dv) && rng.nextFloat() < 2f / 3f)
                name = name.replace(dv, dv.substring(0, 1));
        }

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
        String end = switch (materialType)
        {
            case Ingot -> pick(METAL_END, rng);
            case Gem -> pick(GEM_END, rng);
            case null, default -> pick(STONE_END, rng);
        };

        String name;

        String prefix = rng.nextInt(10) == 0
                ? pick(RARE_PREFIX, rng)
                : pick(PREFIX, rng);

        Utilities.WeightedRandom<Integer> patternRNG = new Utilities.WeightedRandom<Integer>(rng.nextLong()).add(10, 0).add(2, 1).add(7, 2).add(2, 3);

        switch (patternRNG.next())
        {
            case 0 -> name = prefix + chooseVowel() + end;
            case 1 -> name = prefix + chooseVowel() + pick(INFIX, rng) + chooseVowel() + end;
            case 2 -> name = prefix + pick(CORE, rng) + end;
            default -> name = chooseVowel() + pick(INFIX, rng) + end;
        }

        return cleanupName(name);
    }

    // =========================================================================
    // Color generation
    // =========================================================================

    public float getHue()
    {
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
        switch (materialType)
        {
            case Ingot -> {
                float hue = getHue();

                Utilities.WeightedRandom<Integer> modeRNG = new Utilities.WeightedRandom<Integer>(rng.nextLong())
                        .add(1, 0).add(3, 1).add(3, 2);

                int mode = modeRNG.next();
                float saturation = switch (mode)
                {
                    case 0  ->        rng.nextFloat() * 0.15f;
                    case 1  -> 0.1f + rng.nextFloat() * 0.3f;
                    default -> 0.4f + rng.nextFloat() * 0.5f;
                };

                float brightness = switch (mode)
                {
                    case 0 -> 0.2f + (float) Math.pow(rng.nextFloat(), 1.5f);
                    case 1 -> 0.6f + Math.max(rng.nextFloat() * 0.5f, 0.4f);
                    default -> 0.4f + (float) Math.pow(rng.nextFloat(), 1.5f) * 0.8f;
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
                        ? 0.05f + rng.nextFloat() * 0.2f
                        : 0.4f + rng.nextFloat() * 0.5f;
                float brightness = rng.nextFloat() < 0.2f
                        ? 0.85f + rng.nextFloat() * 0.15f
                        : 0.6f + rng.nextFloat() * 0.4f;
                mainColor = Color.getHSBColor(hue, saturation, brightness);
            }
        }

        borderColor = oreBase.getBorderColor();
        bottomBorderColor = oreBase.getBottomBorderColor();
    }
}