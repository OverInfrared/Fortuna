package infrared.fortuna.materials;

import infrared.fortuna.Fortuna;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.enums.MaterialType;
import infrared.fortuna.enums.MiningLevel;
import infrared.fortuna.enums.DynamicToolType;
import infrared.fortuna.enums.ore.*;
import infrared.fortuna.worldgen.OreSpawnEntry;
import infrared.fortuna.worldgen.SpawnProfile;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.*;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final ResourceKey<TrimMaterial> trimMaterialKey;

    // === Ore generation properties ===
    private final List<OreSpawnEntry> spawnEntries;

    // =========================================================================
    // Constructor
    // =========================================================================

    public OreMaterial(long seed, MiningLevel level)
    {
        super(seed);
        name = chooseName();

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
        toolVariant = rng.nextInt(DynamicToolType.getVariantCount());

        makeArmor = true;
        armorMaterial = chooseArmorMaterial();
        armorVariant = toolVariant;

        trimMaterialKey = ResourceKey.create(Registries.TRIM_MATERIAL,
                Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, name));

        spawnEntries = chooseSpawnEntries();

        generateOreColors();

        logSpawnEntries();
    }

    public void logSpawnEntries()
    {
        Fortuna.LOGGER.info("=== Ore Generation: {} ({}) ===", name, miningLevel);
        Fortuna.LOGGER.info("  Base: {}, Type: {}", oreBase, materialType);

        for (int i = 0; i < spawnEntries.size(); i++)
        {
            OreSpawnEntry entry = spawnEntries.get(i);
            String frequency = entry.isRare()
                    ? "1 in %d chunks".formatted(entry.getRarity())
                    : "%d per chunk".formatted(entry.getCount());

            Fortuna.LOGGER.info("  [{}] {} | size:{} | {} | y:{} to {} | {}",
                    i,
                    entry.getProfile(),
                    entry.getVeinSize(),
                    frequency,
                    entry.getMinHeight(),
                    entry.getMaxHeight(),
                    entry.usesTriangleDistribution() ? "triangle" : "uniform"
            );
        }
    }

    // =========================================================================
    // Public getters
    // =========================================================================

    public MiningLevel getMiningLevel()                   { return miningLevel; }
    public MaterialType getType()                         { return materialType; }

    public MaterialOreBase getBase()                      { return oreBase; }
    public MaterialOreOverlay getOverlay()                { return oreOverlay; }

    public MaterialOreIngot getIngot()                    { return oreIngot; }
    public MaterialOreRaw getMaterialType()               { return oreRaw; }
    public MaterialOreGem getGem()                        { return oreGem; }
    public MaterialOreBlock getMaterialBlock()            { return materialBlock; }

    public float getMaterialMineTime()                    { return materialMineTime; }
    public float getMaterialHardness()                    { return materialHardness; }

    public IntProvider getXpRange()                       { return xpRange; }
    public MaterialOreDrops getDropType()                 { return drops; }

    public boolean hasTools()                             { return makeTools; }
    public ToolMaterial getToolMaterial()                 { return toolMaterial; }
    public int getToolVariant()                           { return toolVariant; }

    public boolean hasArmor()                             { return makeArmor; }
    public ArmorMaterial getArmorMaterial()               { return armorMaterial; }
    public int getArmorVariant()                          { return armorVariant; }
    public ResourceKey<TrimMaterial> getTrimMaterialKey() { return trimMaterialKey; }

    public List<OreSpawnEntry> getSpawnEntries() { return spawnEntries; }

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
            case Copper ->
            {
                Utilities.WeightedRandom<MaterialType> copperRandom = new Utilities.WeightedRandom<MaterialType>(rng.nextLong())
                        .add(80, MaterialType.Ingot).add(20, MaterialType.Gem);
                yield copperRandom.next();
            }
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
            case Copper   -> 80 + rng.nextInt(120);      // 80 - 199
            case Iron     -> 180 + rng.nextInt(150);     // 180 - 329
            case Diamond  -> 800 + rng.nextInt(1200);    // 800 - 1999
            case Netherite -> 1500 + rng.nextInt(1000);  // 1500 - 2499
        };

        float baseSpeed = switch (miningLevel)
        {
            case Copper   -> 3.5f + rng.nextFloat() * 2.0f;    // 3.5 - 5.5
            case Iron     -> 5.0f + rng.nextFloat() * 2.0f;    // 5.0 - 7.0
            case Diamond  -> 7.0f + rng.nextFloat() * 2.5f;    // 7.0 - 9.5
            case Netherite -> 8.0f + rng.nextFloat() * 2.0f;   // 8.0 - 10.0
        };

        float baseAttackDamage = switch (miningLevel)
        {
            case Copper   -> 0.5f + rng.nextFloat();            // 0.5 - 1.5
            case Iron     -> 1.5f + rng.nextFloat();            // 1.5 - 2.5
            case Diamond  -> 2.5f + rng.nextFloat() * 1.5f;    // 2.5 - 4.0
            case Netherite -> 3.5f + rng.nextFloat() * 1.5f;   // 3.5 - 5.0
        };

        int baseEnchantment = switch (miningLevel)
        {
            case Copper   -> 10 + rng.nextInt(8);     // 10 - 17
            case Iron     -> 8 + rng.nextInt(10);     // 8 - 17
            case Diamond  -> 6 + rng.nextInt(10);     // 6 - 15
            case Netherite -> 10 + rng.nextInt(10);   // 10 - 19
        };

        TagKey<Block> incorrectBlocks = switch (miningLevel)
        {
            case Copper   -> BlockTags.INCORRECT_FOR_STONE_TOOL;
            case Iron     -> BlockTags.INCORRECT_FOR_IRON_TOOL;
            case Diamond  -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
            case Netherite -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        };

        // TODO: generate a custom repair item tag once item tags are dynamic
        TagKey<Item> repairItems = switch (miningLevel)
        {
            case Copper   -> ItemTags.STONE_TOOL_MATERIALS;
            case Iron     -> ItemTags.IRON_TOOL_MATERIALS;
            case Diamond  -> ItemTags.DIAMOND_TOOL_MATERIALS;
            case Netherite -> ItemTags.NETHERITE_TOOL_MATERIALS;
        };

        return new ToolMaterial(incorrectBlocks, baseDurability, baseSpeed, baseAttackDamage, baseEnchantment, repairItems);
    }

    // =========================================================================
    // Armor properties
    // =========================================================================

    private ArmorMaterial chooseArmorMaterial()
    {
        if (!makeArmor)
            return null;

        int durabilityMultiplier = switch (miningLevel)
        {
            case Copper    -> 8 + rng.nextInt(6);        // 8 - 13
            case Iron      -> 12 + rng.nextInt(8);       // 12 - 19
            case Diamond   -> 25 + rng.nextInt(15);      // 25 - 39
            case Netherite -> 30 + rng.nextInt(15);      // 30 - 44
        };

        int baseDefense = switch (miningLevel)
        {
            case Copper    -> 1;
            case Iron      -> 2;
            case Diamond   -> 3;
            case Netherite -> 3;
        };

        Map<ArmorType, Integer> defense = ArmorMaterials.makeDefense(
                baseDefense,
                baseDefense + rng.nextInt(2) + 3,
                baseDefense + rng.nextInt(2) + 4,
                baseDefense,
                baseDefense + rng.nextInt(3) + 4
        );

        int enchantmentValue = switch (miningLevel)
        {
            case Copper    -> 10 + rng.nextInt(8);
            case Iron      -> 8 + rng.nextInt(10);
            case Diamond   -> 6 + rng.nextInt(10);
            case Netherite -> 10 + rng.nextInt(10);
        };

        float toughness = switch (miningLevel)
        {
            case Diamond   -> 1.0f + rng.nextFloat() * 2.0f;
            case Netherite -> 2.0f + rng.nextFloat() * 2.0f;
            default        -> 0.0f;
        };

        float knockbackResistance = switch (miningLevel)
        {
            case Netherite -> 0.05f + rng.nextFloat() * 0.1f;
            case Diamond   -> rng.nextFloat() < 0.3f ? 0.05f : 0.0f;
            default        -> 0.0f;
        };

        // TODO: generate a custom repair item tag once item tags are dynamic
        TagKey<Item> repairItems = switch (miningLevel)
        {
            case Copper    -> ItemTags.REPAIRS_COPPER_ARMOR;
            case Iron      -> ItemTags.REPAIRS_IRON_ARMOR;
            case Diamond   -> ItemTags.REPAIRS_DIAMOND_ARMOR;
            case Netherite -> ItemTags.REPAIRS_NETHERITE_ARMOR;
        };

        ResourceKey<EquipmentAsset> armorAssetKey = ResourceKey.create(
                EquipmentAssets.ROOT_ID,
                Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, name)
        );

        return new ArmorMaterial(
                durabilityMultiplier,
                defense,
                enchantmentValue,
                SoundEvents.ARMOR_EQUIP_IRON,
                toughness,
                knockbackResistance,
                repairItems,
                armorAssetKey
        );
    }

    // =========================================================================
    // Ore generation
    // =========================================================================

    private List<OreSpawnEntry> chooseSpawnEntries()
    {
        List<OreSpawnEntry> entries = new ArrayList<>();

        boolean isSurfaceBlock = oreBase == MaterialOreBase.Sand || oreBase == MaterialOreBase.Gravel;

        // Primary placement — every ore gets one
        entries.add(choosePrimaryEntry());

        // Surface ores get an additional surface-specific placement
        if (isSurfaceBlock && rng.nextFloat() < 0.7f)
            entries.add(chooseSurfaceEntry());

        // Deep placement — stone-based ores can get an additional deep vein
        if (!isSurfaceBlock && rng.nextFloat() < 0.5f)
            entries.add(chooseDeepEntry());

        // Secondary placement — a smaller supplementary distribution
        if (rng.nextFloat() < 0.75f)
            entries.add(chooseSecondaryEntry());

        // Scattered underground for surface ores — so they're not only near the top
        if (isSurfaceBlock && rng.nextFloat() < 0.6f)
            entries.add(chooseScatteredEntry());

        // Deposit — rare large vein
        float depositChance = switch (miningLevel)
        {
            case Copper    -> 0.7f;
            case Iron      -> 0.6f;
            case Diamond   -> 0.35f;
            case Netherite -> 0.2f;
        };

        if (rng.nextFloat() < depositChance)
            entries.add(chooseDepositEntry(isSurfaceBlock));

        return entries;
    }

    private OreSpawnEntry choosePrimaryEntry()
    {
        return switch (miningLevel)
        {
            case Copper ->
            {
                int veinSize = 8 + rng.nextInt(10);         // 8-17
                int count = 10 + rng.nextInt(14);           // 10-23
                int minHeight = -16 + rng.nextInt(20);      // -16 to 3
                int maxHeight = 80 + rng.nextInt(50);       // 80 to 129
                yield new OreSpawnEntry(SpawnProfile.Spread, veinSize, count, 0, minHeight, maxHeight, true);
            }
            case Iron ->
            {
                int veinSize = 6 + rng.nextInt(8);
                int count = 8 + rng.nextInt(12);
                int minHeight = -24 + rng.nextInt(20);
                int maxHeight = 56 + rng.nextInt(40);
                yield new OreSpawnEntry(SpawnProfile.Spread, veinSize, count, 0, minHeight, maxHeight, true);
            }
            case Diamond ->
            {
                int veinSize = 3 + rng.nextInt(6);
                int count = 4 + rng.nextInt(5);
                int minHeight = -80 + rng.nextInt(20);
                int maxHeight = -20 + rng.nextInt(60);
                yield new OreSpawnEntry(SpawnProfile.Deep, veinSize, count, 0, minHeight, maxHeight, true);
            }
            case Netherite ->
            {
                int veinSize = 2 + rng.nextInt(4);
                int count = 2 + rng.nextInt(3);
                int minHeight = -80 + rng.nextInt(10);
                int maxHeight = -30 + rng.nextInt(30);
                yield new OreSpawnEntry(SpawnProfile.Deep, veinSize, count, 0, minHeight, maxHeight, true);
            }
        };
    }

    private OreSpawnEntry chooseDeepEntry()
    {
        int veinSize = switch (miningLevel)
        {
            case Copper   -> 6 + rng.nextInt(8);
            case Iron     -> 5 + rng.nextInt(6);
            case Diamond  -> 3 + rng.nextInt(5);
            case Netherite -> 2 + rng.nextInt(4);
        };

        int count = switch (miningLevel)
        {
            case Copper   -> 8 + rng.nextInt(10);
            case Iron     -> 6 + rng.nextInt(8);
            case Diamond  -> 3 + rng.nextInt(5);
            case Netherite -> 2 + rng.nextInt(3);
        };

        int minHeight = -64 + rng.nextInt(10);
        int maxHeight = -10 + rng.nextInt(20);

        return new OreSpawnEntry(SpawnProfile.Deep, veinSize, count, 0, minHeight, maxHeight, true);
    }

    private OreSpawnEntry chooseSurfaceEntry()
    {
        int veinSize = switch (miningLevel)
        {
            case Copper   -> 6 + rng.nextInt(8);
            case Iron     -> 5 + rng.nextInt(6);
            case Diamond  -> 3 + rng.nextInt(4);
            case Netherite -> 2 + rng.nextInt(3);
        };

        int count = switch (miningLevel)
        {
            case Copper   -> 6 + rng.nextInt(8);
            case Iron     -> 4 + rng.nextInt(6);
            case Diamond  -> 2 + rng.nextInt(3);
            case Netherite -> 1 + rng.nextInt(2);
        };

        int minHeight = 40 + rng.nextInt(20);
        int maxHeight = 80 + rng.nextInt(50);

        return new OreSpawnEntry(SpawnProfile.Surface, veinSize, count, 0, minHeight, maxHeight, false);
    }

    private OreSpawnEntry chooseSecondaryEntry()
    {
        // Smaller scattered placement for additional findability
        SpawnProfile profile = rng.nextFloat() < 0.4f ? SpawnProfile.Scattered : SpawnProfile.Pocket;

        if (profile == SpawnProfile.Scattered)
        {
            int veinSize = 2 + rng.nextInt(4);      // 2-5
            int count = 3 + rng.nextInt(5);          // 3-7
            return new OreSpawnEntry(SpawnProfile.Scattered, veinSize, count, 0, -64, 256, false);
        }
        else
        {
            // Pocket — narrow band, decent density
            int veinSize = 4 + rng.nextInt(6);       // 4-9
            int count = 4 + rng.nextInt(6);           // 4-9
            int center = -40 + rng.nextInt(80);       // -40 to 39
            int spread = 8 + rng.nextInt(12);         // 8 to 19
            return new OreSpawnEntry(SpawnProfile.Pocket, veinSize, count, 0, center - spread, center + spread, true);
        }
    }

    private OreSpawnEntry chooseScatteredEntry()
    {
        // For surface ores, a smaller chance deeper underground
        int veinSize = 2 + rng.nextInt(3);           // 2-4
        int rarity = 4 + rng.nextInt(6);             // 1 in 4-9 chunks
        return new OreSpawnEntry(SpawnProfile.Scattered, veinSize, 0, rarity, -20, 60, false);
    }

    private OreSpawnEntry chooseDepositEntry(boolean isSurfaceBlock)
    {
        int veinSize = switch (miningLevel)
        {
            case Copper   -> 25 + rng.nextInt(35);
            case Iron     -> 20 + rng.nextInt(30);
            case Diamond  -> 15 + rng.nextInt(20);
            case Netherite -> 10 + rng.nextInt(15);
        };

        int rarity = switch (miningLevel)
        {
            case Copper   -> 4 + rng.nextInt(4);
            case Iron     -> 6 + rng.nextInt(4);
            case Diamond  -> 8 + rng.nextInt(6);
            case Netherite -> 12 + rng.nextInt(8);
        };

        int minHeight;
        int maxHeight;

        if (isSurfaceBlock)
        {
            minHeight = 30 + rng.nextInt(20);
            maxHeight = 70 + rng.nextInt(40);
        }
        else
        {
            minHeight = -64 + rng.nextInt(30);
            maxHeight = -10 + rng.nextInt(40);
        }

        return new OreSpawnEntry(SpawnProfile.Deposit, veinSize, 0, rarity, minHeight, maxHeight, true);
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
            "mi", "ar", "la", "fa", "r", "gl", "red"
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

        Utilities.WeightedRandom<Integer> patternRNG = new Utilities.WeightedRandom<Integer>(rng.nextLong())
                .add(10, 0).add(1, 1).add(10, 2).add(3, 3).add(3, 4);

        switch (patternRNG.next())
        {
            case 0 -> name = prefix + chooseVowel() + end;
            case 1 -> name = prefix + chooseVowel() + pick(INFIX, rng) + chooseVowel() + end;
            case 2 -> name = prefix + pick(CORE, rng) + end;
            case 3 -> name = chooseVowel() + pick(INFIX, rng) + end;
            default -> name = prefix + end;
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

                setColor("main", Color.getHSBColor(hue, saturation, brightness));
                if (oreOverlay == MaterialOreOverlay.Copper) {
                    float oxidizedShift = 0.4f + rng.nextFloat() * 0.2f;
                    float oxidizedHue = (hue + oxidizedShift) % 1.0f;
                    float oxidizedSaturation = Math.clamp(saturation + (rng.nextFloat() * 0.2f - 0.1f), 0.1f, 0.9f);
                    float oxidizedBrightness = Math.clamp(brightness + (rng.nextFloat() * 0.2f - 0.1f), 0.4f, 1.0f);
                    setColor("secondary", Color.getHSBColor(oxidizedHue, oxidizedSaturation, oxidizedBrightness));
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
                setColor("main", Color.getHSBColor(hue, saturation, brightness));
            }
        }

        Color mainColor = getMainColor();
        setColor("main_white", Utilities.brightenColorByFactor(mainColor, 0.75f));
        setColor("main_light", Utilities.brightenColorByFactor(mainColor, 0.85f));
        setColor("main_dark",  Utilities.nudgeColor(mainColor, 0.15f, 0f, 0f));
    }
}