package infrared.fortuna.blocks;

import infrared.fortuna.Fortuna;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.blocks.ore.*;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.MaterialType;
import infrared.fortuna.materials.ore.enums.OreBase;
import infrared.fortuna.materials.ore.enums.OreOverlay;
import infrared.fortuna.materials.ore.OreMaterial;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;

import java.util.*;
import java.util.stream.Collectors;

public class ModBlocks
{
    // Map of dynamic registry name to fortuna block.
    private static final Map<String, IFortunaBlock> registeredBlocks = new LinkedHashMap<>();

    private static void registerBlock(IFortunaBlock fortunaBlock)
    {
        if (fortunaBlock instanceof IDoorBlock)
            ModItems.registerDoorBlock(fortunaBlock);
        else
            ModItems.registerBlock(fortunaBlock);

        Registry.register(BuiltInRegistries.BLOCK, fortunaBlock.getResourceKey(), (Block) fortunaBlock);
        registeredBlocks.put(fortunaBlock.getDynamicProperties().registryName(), fortunaBlock);
    }

    private static void registerBlocks(List<IFortunaBlock> fortunaBlocks)
    {
        for (IFortunaBlock block : fortunaBlocks)
            registerBlock(block);
    }

    private static ResourceKey<Block> keyOfBlock(String name)
    {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name)
    {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, name));
    }

    // Initialize ore blocks and items
    public static void initializeOreMaterial(OreMaterial material)
    {
        String name = material.getName();
        MaterialType type = material.getType();

        // If an ingot create raw ore block.
        if (type == MaterialType.Ingot)
        {
            // Registry Information
            String             registryName = "%s_raw_block".formatted(name);
            ResourceKey<Block> key          = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, registryName));

            // Properties
            DynamicProperties<Block, OreMaterial> dynamicProperties = new DynamicProperties<>(registryName, Component.literal("Block of Raw %s".formatted(Utilities.capitalize(name))), key, material);
            Properties                            blockProperties   = Properties.of().instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F);

            registerBlock(new RawMaterialBlock(dynamicProperties, blockProperties));
        }

        // Ore block
        String             oreRegistryName = "%s_ore".formatted(name);
        ResourceKey<Block> oreKey          = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, oreRegistryName));

        DynamicProperties<Block, OreMaterial> oreDynamicProperties = new DynamicProperties<>(oreRegistryName, Component.literal("%s Ore".formatted(Utilities.capitalize(name))), oreKey, material);
        Properties                            oreProperties        = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime(), material.getMaterialHardness()).requiresCorrectToolForDrops();

        OreBase base = material.getBase();

        // Create regular static or falling ore block.
        if (base == OreBase.Sand || base == OreBase.Gravel)
            registerBlock(new FallingOreBlock(oreDynamicProperties, oreProperties));
        else
            registerBlock(new OreBlock(oreDynamicProperties, oreProperties));

        // If stone then make a corresponding deepslate ore.
        if (base == OreBase.Stone)
        {
            String             dsRegistryName = "deepslate_%s_ore".formatted(name);
            ResourceKey<Block> dsOreKey       = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, dsRegistryName));

            DynamicProperties<Block, OreMaterial> dsDynamicProperties = new DynamicProperties<>(dsRegistryName, Component.literal("Deepslate %s Ore".formatted(Utilities.capitalize(name))), dsOreKey, material);
            Properties                            dsOreProperties     = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime() + 1.5f, material.getMaterialHardness()).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE);

            registerBlock(new OreBlock(dsDynamicProperties, dsOreProperties, OreBase.Deepslate));
        }

        OreOverlay overlay = material.getOverlay();

        // The main material block
        if (overlay == OreOverlay.Copper)
            registerBlocks(createWeatheredBlocks(material));
        else
            registerBlocks(createMaterialBlocks(material));
    }

    private static IFortunaBlock createMaterialBlock(OreMaterial material)
    {
        return createMaterialBlock(material, false, "", null);
    }

    private static IFortunaBlock createMaterialBlock(OreMaterial material, boolean oxidizable, String prefix, WeatheringCopper.WeatherState weatherState)
    {
        String blockRegistryName = "%s%s_block".formatted(prefix, material.getName());
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, blockRegistryName));

        String displayPrefix = Arrays.stream(prefix.split("_"))
                .filter(s -> !s.isEmpty())
                .map(Utilities::capitalize)
                .collect(Collectors.joining(" "));

        if (!displayPrefix.isEmpty())
            displayPrefix += " ";

        Component displayName = Component.literal("%sBlock of %s".formatted(displayPrefix, Utilities.capitalize(material.getName())));

        DynamicProperties<Block, OreMaterial> blockDynamicProperties = new DynamicProperties<>(blockRegistryName, displayName, blockKey, material);
        Properties                            blockProperties        = BlockBehaviour.Properties.of().strength(5f, 6f).sound(SoundType.METAL).requiresCorrectToolForDrops();

        if (oxidizable)
            return new WeatheringMaterialBlock(blockDynamicProperties, blockProperties, weatherState);
        else
            return new MaterialBlock(blockDynamicProperties, blockProperties, weatherState);
    }

    private static IFortunaBlock createBarsBlock(OreMaterial material, boolean oxidizable, String prefix, WeatheringCopper.WeatherState weatherState)
    {
        String blockRegistryName = "%s%s_bars".formatted(prefix, material.getName());
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, blockRegistryName));

        String displayPrefix = Arrays.stream(prefix.split("_"))
                .filter(s -> !s.isEmpty())
                .map(Utilities::capitalize)
                .collect(Collectors.joining(" "));

        if (!displayPrefix.isEmpty())
            displayPrefix += " ";

        Component displayName = Component.literal("%s%s Bars".formatted(displayPrefix, Utilities.capitalize(material.getName())));

        DynamicProperties<Block, OreMaterial> blockDynamicProperties = new DynamicProperties<>(blockRegistryName, displayName, blockKey, material);
        BlockBehaviour.Properties blockProperties = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime(), material.getMaterialHardness()).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion();

        if (oxidizable)
            return new WeatheringBarsBlock(blockDynamicProperties, blockProperties, weatherState);
        else
            return new BarsBlock(blockDynamicProperties, blockProperties, weatherState);
    }

    private static IFortunaBlock createDoorBlock(OreMaterial material, boolean weathering, String prefix, WeatheringCopper.WeatherState state)
    {
        String registryName = "%s%s_door".formatted(prefix, material.getName());
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, registryName));

        String displayPrefix = Arrays.stream(prefix.split("_"))
                .filter(s -> !s.isEmpty())
                .map(Utilities::capitalize)
                .collect(Collectors.joining(" "));

        if (!displayPrefix.isEmpty())
            displayPrefix += " ";

        String displayName = "%s%s Door".formatted(displayPrefix, Utilities.capitalize(material.getName()));

        DynamicProperties<Block, OreMaterial> dynamicProperties = new DynamicProperties<>(registryName, Component.literal(displayName), key, material);
        Properties properties = BlockBehaviour.Properties.of()
                .strength(material.getMaterialMineTime(), material.getMaterialHardness())
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);

        if (weathering)
            return new WeatheringDoorBlock(dynamicProperties, properties, BlockSetType.IRON, state);
        else
            return new FortunaDoorBlock(dynamicProperties, properties, BlockSetType.IRON, state);
    }

    private static IFortunaBlock createTrapdoorBlock(OreMaterial material, boolean weathering, String prefix, WeatheringCopper.WeatherState state)
    {
        String registryName = "%s%s_trapdoor".formatted(prefix, material.getName());
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, registryName));

        String displayPrefix = Arrays.stream(prefix.split("_"))
                .filter(s -> !s.isEmpty())
                .map(Utilities::capitalize)
                .collect(Collectors.joining(" "));

        if (!displayPrefix.isEmpty())
            displayPrefix += " ";

        String displayName = "%s%s Trapdoor".formatted(displayPrefix, Utilities.capitalize(material.getName()));

        DynamicProperties<Block, OreMaterial> dynamicProperties = new DynamicProperties<>(registryName, Component.literal(displayName), key, material);
        Properties properties = BlockBehaviour.Properties.of()
                .strength(material.getMaterialMineTime(), material.getMaterialHardness())
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);

        if (weathering)
            return new WeatheringTrapDoorBlock(dynamicProperties, properties, BlockSetType.IRON, state);
        else
            return new FortunaTrapDoorBlock(dynamicProperties, properties, BlockSetType.IRON, state);
    }

    private static List<IFortunaBlock> createMaterialBlocks(OreMaterial material)
    {
        String name = material.getName();
        List<IFortunaBlock> materialBlocks = new ArrayList<>();

        materialBlocks.add(createMaterialBlock(material));

        if (material.hasDoor())
        {
            String             doorRegistryName = "%s_door".formatted(name);
            ResourceKey<Block> doorKey          = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, doorRegistryName));

            DynamicProperties<Block, OreMaterial> doorDynamicProperties = new DynamicProperties<>(doorRegistryName, Component.literal("%s Door".formatted(Utilities.capitalize(name))), doorKey, material);
            Properties                            doorProperties        = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime(), material.getMaterialHardness()).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.DESTROY);

            materialBlocks.add(new FortunaDoorBlock(doorDynamicProperties, doorProperties, BlockSetType.IRON));
        }

        if (material.hasTrapdoor())
            materialBlocks.add(createTrapdoorBlock(material, false, "", null));

        if (material.hasBars())
        {
            String             barsRegistryName = "%s_bars".formatted(name);
            ResourceKey<Block> barsKey          = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, barsRegistryName));

            DynamicProperties<Block, OreMaterial> barsDynamicProperties = new DynamicProperties<>(barsRegistryName, Component.literal("%s Bars".formatted(Utilities.capitalize(name))), barsKey, material);
            Properties                            barsProperties        = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime(), material.getMaterialHardness()).requiresCorrectToolForDrops().noOcclusion();

            materialBlocks.add(new BarsBlock(barsDynamicProperties, barsProperties));
        }

        return materialBlocks;
    }

    private static void registerWeatheringSet(List<IFortunaBlock> allBlocks, IFortunaBlock[] weathering, IFortunaBlock[] waxed)
    {
        for (int i = 0; i < 4; i++)
        {
            allBlocks.add(weathering[i]);
            allBlocks.add(waxed[i]);
        }

        for (int i = 0; i < 3; i++)
            OxidizableBlocksRegistry.registerOxidizableBlockPair((Block) weathering[i], (Block) weathering[i + 1]);

        for (int i = 0; i < 4; i++)
            OxidizableBlocksRegistry.registerWaxableBlockPair((Block) weathering[i], (Block) waxed[i]);
    }

    private static List<IFortunaBlock> createWeatheredBlocks(OreMaterial material)
    {
        List<IFortunaBlock> allBlocks = new ArrayList<>();

        String[] prefixes = {"", "exposed_", "weathered_", "oxidized_"};
        String[] waxedPrefixes = {"waxed_", "waxed_exposed_", "waxed_weathered_", "waxed_oxidized_"};

        WeatheringCopper.WeatherState[] states = {
                WeatheringCopper.WeatherState.UNAFFECTED,
                WeatheringCopper.WeatherState.EXPOSED,
                WeatheringCopper.WeatherState.WEATHERED,
                WeatheringCopper.WeatherState.OXIDIZED
        };

        // Material blocks
        IFortunaBlock[] weatheringBlocks = new IFortunaBlock[4];
        IFortunaBlock[] waxedBlocks = new IFortunaBlock[4];
        for (int i = 0; i < 4; i++)
        {
            weatheringBlocks[i] = createMaterialBlock(material, true, prefixes[i], states[i]);
            waxedBlocks[i] = createMaterialBlock(material, false, waxedPrefixes[i], states[i]);
        }
        registerWeatheringSet(allBlocks, weatheringBlocks, waxedBlocks);

        // Doors
        if (material.hasDoor())
        {
            IFortunaBlock[] weatheringDoors = new IFortunaBlock[4];
            IFortunaBlock[] waxedDoors = new IFortunaBlock[4];
            for (int i = 0; i < 4; i++)
            {
                weatheringDoors[i] = createDoorBlock(material, true, prefixes[i], states[i]);
                waxedDoors[i] = createDoorBlock(material, false, waxedPrefixes[i], states[i]);
            }
            registerWeatheringSet(allBlocks, weatheringDoors, waxedDoors);
        }

        // Bars
        if (material.hasBars())
        {
            IFortunaBlock[] weatheringBars = new IFortunaBlock[4];
            IFortunaBlock[] waxedBars = new IFortunaBlock[4];
            for (int i = 0; i < 4; i++)
            {
                weatheringBars[i] = createBarsBlock(material, true, prefixes[i], states[i]);
                waxedBars[i] = createBarsBlock(material, false, waxedPrefixes[i], states[i]);
            }
            registerWeatheringSet(allBlocks, weatheringBars, waxedBars);
        }

        // Trapdoors
        if (material.hasTrapdoor())
        {
            IFortunaBlock[] weatheringTrapdoors = new IFortunaBlock[4];
            IFortunaBlock[] waxedTrapdoors = new IFortunaBlock[4];
            for (int i = 0; i < 4; i++)
            {
                weatheringTrapdoors[i] = createTrapdoorBlock(material, true, prefixes[i], states[i]);
                waxedTrapdoors[i] = createTrapdoorBlock(material, false, waxedPrefixes[i], states[i]);
            }
            registerWeatheringSet(allBlocks, weatheringTrapdoors, waxedTrapdoors);
        }

        return allBlocks;
    }

    public static Map<String, IFortunaBlock> getRegisteredBlocks()
    {
        return registeredBlocks;
    }

}
