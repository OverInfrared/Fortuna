package infrared.fortuna.materials.ore;

import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.Material;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.materials.MaterialType;
import infrared.fortuna.items.DynamicToolType;
import infrared.fortuna.worldgen.*;
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
import java.util.HashMap;
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
    private final MaterialOreNugget oreNugget;
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
    private final boolean doGeneration;
    private final Map<OreFeatureType, OreConfiguredFeature> configuredFeatures = new HashMap<>();
    private final List<OrePlacedFeature> placedFeatures = new ArrayList<>();

    // === Tertiary Blocks ===
    private final boolean hasNugget;
    private final boolean hasDoor;
    private final boolean hasTrapdoor;
    private final boolean hasBars;
    private final boolean hasChain;

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
        oreNugget = chooseOreNugget();
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

        doGeneration = true;
        createConfiguredFeatures();
        choosePlacedFeatures();

        hasNugget = materialType == MaterialType.Ingot;
        hasDoor = materialType == MaterialType.Ingot && rng.nextFloat() < 0.5f;
        hasTrapdoor = hasDoor && rng.nextFloat() < 0.8f;
        hasBars = materialType == MaterialType.Ingot && rng.nextFloat() < 0.4f;
        hasChain = hasBars;

        generateOreColors();
    }

    // =========================================================================
    // Public getters
    // =========================================================================

    public MiningLevel getMiningLevel() { return miningLevel; }
    public MaterialType getType()       { return materialType; }

    public MaterialOreBase getBase()           { return oreBase; }
    public MaterialOreOverlay getOverlay()     { return oreOverlay; }
    public MaterialOreIngot getIngot()         { return oreIngot; }
    public MaterialOreRaw getMaterialType()    { return oreRaw; }
    public MaterialOreGem getGem()             { return oreGem; }
    public MaterialOreNugget getNugget()       { return oreNugget; }
    public MaterialOreBlock getMaterialBlock() { return materialBlock; }

    public float getMaterialMineTime() { return materialMineTime; }
    public float getMaterialHardness() { return materialHardness; }

    public IntProvider getXpRange()       { return xpRange; }
    public MaterialOreDrops getDropType() { return drops; }

    public boolean hasTools()             { return makeTools; }
    public ToolMaterial getToolMaterial() { return toolMaterial; }
    public int getToolVariant()           { return toolVariant; }

    public boolean hasArmor()                             { return makeArmor; }
    public ArmorMaterial getArmorMaterial()               { return armorMaterial; }
    public int getArmorVariant()                          { return armorVariant; }
    public ResourceKey<TrimMaterial> getTrimMaterialKey() { return trimMaterialKey; }

    public boolean doesGeneration()                                          { return doGeneration; }
    public Map<OreFeatureType, OreConfiguredFeature> getConfiguredFeatures() { return configuredFeatures; }
    public List<OrePlacedFeature> getPlacedFeatures()                        { return placedFeatures; }

    public boolean hasDoor()     { return hasDoor; }
    public boolean hasTrapdoor() { return hasTrapdoor; }
    public boolean hasBars()     { return hasBars; }
    public boolean hasChain()    { return hasChain; }

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
        Utilities.WeightedRandom<MaterialOreBase> baseRandom = new Utilities.WeightedRandom<MaterialOreBase>(rng.nextLong());

        switch (miningLevel)
        {
            case Copper ->
            {
                baseRandom.add(40, MaterialOreBase.Stone)
                        .add(12, MaterialOreBase.Andesite)
                        .add(12, MaterialOreBase.Diorite)
                        .add(12, MaterialOreBase.Granite)
                        .add(2, MaterialOreBase.Tuff)
                        .add(12, MaterialOreBase.Sand)
                        .add(10, MaterialOreBase.Gravel);
            }
            case Iron ->
            {
                baseRandom.add(55, MaterialOreBase.Stone)
                        .add(8, MaterialOreBase.Andesite)
                        .add(8, MaterialOreBase.Diorite)
                        .add(8, MaterialOreBase.Granite)
                        .add(6, MaterialOreBase.Tuff)
                        .add(8, MaterialOreBase.Sand)
                        .add(7, MaterialOreBase.Gravel);
            }
            case Diamond ->
            {
                baseRandom.add(60, MaterialOreBase.Stone)
                        .add(5, MaterialOreBase.Andesite)
                        .add(5, MaterialOreBase.Diorite)
                        .add(5, MaterialOreBase.Granite)
                        .add(12, MaterialOreBase.Tuff)
                        .add(3, MaterialOreBase.Sand)
                        .add(10, MaterialOreBase.Gravel);
            }
            case Netherite ->
            {
                baseRandom.add(50, MaterialOreBase.Stone)
                        .add(3, MaterialOreBase.Andesite)
                        .add(3, MaterialOreBase.Diorite)
                        .add(3, MaterialOreBase.Granite)
                        .add(25, MaterialOreBase.Tuff)
                        .add(1, MaterialOreBase.Sand)
                        .add(15, MaterialOreBase.Gravel);
            }
        }

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

    private MaterialOreNugget chooseOreNugget()
    {
        MaterialOreNugget[] values = MaterialOreNugget.values();
        return values[rng.nextInt(values.length)];
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

    private void createConfiguredFeatures()
    {
        boolean largerVeins = switch (miningLevel)
        {
            case Copper -> rng.nextFloat() < 0.7f;
            case Iron -> rng.nextFloat() < 0.2f;
            default -> false;
        };

        int baseVeinSize = largerVeins ?
                7 + rng.nextInt(9) :
                3 + rng.nextInt( 9);

        float base = switch (miningLevel)
        {
            case Copper    -> 0.0f;
            case Iron      -> 0.1f;
            case Diamond   -> 0.3f;
            case Netherite -> 0.5f;
        };
        float discardChance = Math.clamp(base + (rng.nextFloat() - 0.5f) * 0.8f, 0.05f, 0.95f);

        // Medium features is like the base feature, loot at iron_ore feature or diamond_ore_medium
        OreConfiguredFeature mediumFeature = new OreConfiguredFeature(baseVeinSize, discardChance, name, oreBase, OreFeatureType.Medium.getName());
        configuredFeatures.put(OreFeatureType.Medium, mediumFeature);

        int smallVeinSize = largerVeins ?
                baseVeinSize / 2 :
                Math.max(baseVeinSize - (1 + rng.nextInt(5)), 1);

        OreConfiguredFeature smallFeature = new OreConfiguredFeature(smallVeinSize, discardChance, name, oreBase, OreFeatureType.Small.getName());
        configuredFeatures.put(OreFeatureType.Small, smallFeature);

        int largeVeinSize = largerVeins ?
                baseVeinSize * 2 :
                baseVeinSize + (1 + rng.nextInt(3));

        OreConfiguredFeature largeFeature = new OreConfiguredFeature(largeVeinSize, discardChance, name, oreBase, OreFeatureType.Large.getName());
        configuredFeatures.put(OreFeatureType.Large, largeFeature);

        OreConfiguredFeature buriedFeature = new OreConfiguredFeature(baseVeinSize, 1.0f, name, oreBase, OreFeatureType.Buried.getName());
        configuredFeatures.put(OreFeatureType.Buried, buriedFeature);
    }

    public record HeightRange(int min, int max) {}

    private void choosePlacedFeatures()
    {
        HeightRange oreRange = switch (oreBase)
        {
            case Diorite, Andesite, Granite -> new HeightRange(0, 96);
            case Sand     -> new HeightRange(50, 75);
            case Tuff     -> new HeightRange(-64, 0);
            case Gravel   -> new HeightRange(-64, 80);
            default       -> new HeightRange(-64, 128);
        };

        int rangeSize = oreRange.max() - oreRange.min();

        // Mining level biases ideal height — higher tiers tend deeper
        float baseIdeal = switch (miningLevel)
        {
            case Copper    -> 0.7f;
            case Iron      -> 0.5f;
            case Diamond   -> 0.2f;
            case Netherite -> 0.1f;
        };

        // Power curve — squaring the random offset makes extreme values much rarer
        float offset = rng.nextFloat() - 0.5f;
        float curved = Math.signum(offset) * (float) Math.pow(Math.abs(offset) * 2f, 2.0) / 2f;
        float idealBias = Math.clamp(baseIdeal + curved * 0.6f, 0f, 1f);
        int idealHeight = oreRange.min() + (int)(rangeSize * idealBias);

        int minSpread = rangeSize / 6;
        int maxSpread = rangeSize / 3;
        int spread = minSpread + rng.nextInt(maxSpread - minSpread + 1);

        int bottomEnd = idealHeight - spread;
        int topEnd = idealHeight + spread;

        // Ensure top end is at least 15 blocks inside the ore range
        topEnd = Math.max(topEnd, oreRange.min() + 15);

        int totalRange = topEnd - bottomEnd;
        FeatureProbability probability = chooseFeatureProbability(totalRange);

        OreFeatureType targetFeatureType = rng.nextFloat() < 0.6f
                ? OreFeatureType.Medium
                : OreFeatureType.Small;

        IConfiguredFeature targetFeature = configuredFeatures.get(targetFeatureType);

        // Every ore gets one trapezoid placement.
        OrePlacedFeature generalFeature = new OrePlacedFeature(idealHeight, topEnd, bottomEnd, probability, DisperseType.Trapezoid, targetFeature);
        placedFeatures.add(generalFeature);

        // Extended uniform placement — flat distribution across a wider range
        float extendedChance = switch (miningLevel)
        {
            case Copper    -> 0.7f;
            case Iron      -> 0.6f;
            case Diamond   -> 0.3f;
            case Netherite -> 0.15f;
        };

        if (rng.nextFloat() < extendedChance)
        {
            // Wider range than the primary, uniform so no peak
            int extendedBottom = oreRange.min() + rng.nextInt(rangeSize / 4);
            int extendedTop = oreRange.max() - rng.nextInt(rangeSize / 4);
            int extendedRange = extendedTop - extendedBottom;

            // Lower density than primary — supplementary, not dominant
            float extendedDensityScale = 0.3f + rng.nextFloat() * 0.3f; // 30-60% of primary density
            FeatureProbability extendedProbability = chooseFeatureProbability((int)(extendedRange * extendedDensityScale));

            IConfiguredFeature extendedFeature = configuredFeatures.get(OreFeatureType.Small);

            OrePlacedFeature extended = new OrePlacedFeature(
                    (extendedBottom + extendedTop) / 2, extendedTop, extendedBottom,
                    extendedProbability, DisperseType.Uniform, extendedFeature);
            placedFeatures.add(extended);
        }

        // Buried placement — fully hidden, no air exposure
        float buriedChance = switch (miningLevel)
        {
            case Copper    -> 0.2f;
            case Iron      -> 0.4f;
            case Diamond   -> 0.6f;
            case Netherite -> 0.8f;
        };

        if (rng.nextFloat() < buriedChance)
        {
            // Buried uses the same general height area but tighter
            int buriedSpread = spread / 2 + rng.nextInt(spread / 3 + 1);
            int buriedBottom = idealHeight - buriedSpread;
            int buriedTop = idealHeight + buriedSpread;
            int buriedRange = buriedTop - buriedBottom;

            // Similar density to primary — buried ore is meant to reward deep mining
            FeatureProbability buriedProbability = chooseFeatureProbability(buriedRange);

            IConfiguredFeature buriedFeature = configuredFeatures.get(OreFeatureType.Buried);

            OrePlacedFeature buried = new OrePlacedFeature(
                    idealHeight, buriedTop, buriedBottom,
                    buriedProbability, DisperseType.Trapezoid, buriedFeature);
            placedFeatures.add(buried);
        }
    }

    private FeatureProbability chooseFeatureProbability(int totalRange)
    {
        float density = switch (miningLevel)
        {
            case Copper    -> 0.10f + rng.nextFloat() * 0.15f;  // 0.10 - 0.25
            case Iron      -> 0.08f + rng.nextFloat() * 0.12f;  // 0.08 - 0.20
            case Diamond   -> 0.03f + rng.nextFloat() * 0.05f;  // 0.03 - 0.08
            case Netherite -> 0.01f + rng.nextFloat() * 0.03f;  // 0.01 - 0.04
        };

        // In chooseFeatureProbability, scale density up for rare base blocks
        float baseBlockMultiplier = switch (oreBase)
        {
            case Tuff                       -> 4.0f;
            case Diorite, Andesite, Granite -> 2.5f;
            case Sand                       -> 3.0f;
            case Gravel                     -> 2.0f;
            default                         -> 1.0f;
        };

        int count = Math.round(density * totalRange * baseBlockMultiplier);

        if (count < 1)
        {
            int rarity = Math.max(2, Math.round(1f / (density * totalRange)));
            return new FeatureProbability(rarity, FeatureProbability.CountType.Rarity);
        }

        return new FeatureProbability(count, FeatureProbability.CountType.Count);
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
                    setColor("secondary_white", Utilities.brightenColorByFactor(getSecondaryColor(), 0.75f));
                    setColor("secondary_light", Utilities.brightenColorByFactor(getSecondaryColor(), 0.85f));
                    setColor("secondary_dark",  Utilities.nudgeColor(getSecondaryColor(), 0.15f, 0f, 0f));
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