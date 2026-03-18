package infrared.fortuna.blocks;

import infrared.fortuna.Fortuna;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.blocks.ore.*;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.MaterialType;
import infrared.fortuna.materials.ore.MaterialOreBase;
import infrared.fortuna.materials.ore.MaterialOreOverlay;
import infrared.fortuna.materials.ore.OreMaterial;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
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
import java.util.stream.Stream;

public class ModBlocks
{
    // Map of dynamic registry name to fortuna block.
    private static final Map<String, IFortunaBlock> registeredBlocks = new LinkedHashMap<>();

    private static void registerBlock(IFortunaBlock fortunaBlock)
    {
        if (fortunaBlock instanceof FortunaDoorBlock)
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

        MaterialOreBase base = material.getBase();

        // Create regular static or falling ore block.
        if (base == MaterialOreBase.Sand || base == MaterialOreBase.Gravel)
            registerBlock(new FallingOreBlock(oreDynamicProperties, oreProperties));
        else
            registerBlock(new OreBlock(oreDynamicProperties, oreProperties));

        // If stone then make a corresponding deepslate ore.
        if (base == MaterialOreBase.Stone)
        {
            String             dsRegistryName = "deepslate_%s_ore".formatted(name);
            ResourceKey<Block> dsOreKey       = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, dsRegistryName));

            DynamicProperties<Block, OreMaterial> dsDynamicProperties = new DynamicProperties<>(dsRegistryName, Component.literal("Deepslate %s Ore".formatted(Utilities.capitalize(name))), dsOreKey, material);
            Properties                            dsOreProperties     = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime() + 1.5f, material.getMaterialHardness()).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE);

            registerBlock(new OreBlock(dsDynamicProperties, dsOreProperties, MaterialOreBase.Deepslate));
        }

        MaterialOreOverlay overlay = material.getOverlay();

        // The main material block
        if (overlay == MaterialOreOverlay.Copper)
            registerBlocks(createWeatheredBlocks(material));
        else
            registerBlock(createMaterialBlock(material));

        if (material.hasDoor())
        {
            String             doorRegistryName = "%s_door".formatted(name);
            ResourceKey<Block> doorKey          = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, doorRegistryName));

            DynamicProperties<Block, OreMaterial> doorDynamicProperties = new DynamicProperties<>(doorRegistryName, Component.literal("%s Door".formatted(Utilities.capitalize(name))), doorKey, material);
            Properties                            doorProperties        = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime(), material.getMaterialHardness()).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.DESTROY);

            registerBlock(new FortunaDoorBlock(doorDynamicProperties, doorProperties, BlockSetType.IRON));
        }

        if (material.hasTrapdoor())
        {
            String             tdRegistryName = "%s_trapdoor".formatted(name);
            ResourceKey<Block> tdKey          = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, tdRegistryName));

            DynamicProperties<Block, OreMaterial> tdDynamicProperties = new DynamicProperties<>(tdRegistryName, Component.literal("%s Trapdoor".formatted(Utilities.capitalize(name))), tdKey, material);
            Properties                            tdProperties        = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime(), material.getMaterialHardness()).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.DESTROY);

            registerBlock(new FortunaTrapDoorBlock(tdDynamicProperties, tdProperties, BlockSetType.IRON));
        }

        if (material.hasBars())
        {
            String             barsRegistryName = "%s_bars".formatted(name);
            ResourceKey<Block> barsKey          = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, barsRegistryName));

            DynamicProperties<Block, OreMaterial> barsDynamicProperties = new DynamicProperties<>(barsRegistryName, Component.literal("%s Bars".formatted(Utilities.capitalize(name))), barsKey, material);
            Properties                            barsProperties        = BlockBehaviour.Properties.of().strength(material.getMaterialMineTime(), material.getMaterialHardness()).requiresCorrectToolForDrops().noOcclusion();

            registerBlock(new FortunaBarsBlock(barsDynamicProperties, barsProperties));
        }
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

    private static List<IFortunaBlock> createWeatheredBlocks(OreMaterial material)
    {
        // Register 4 weathering variants
        String[] prefixes = {"", "exposed_", "weathered_", "oxidized_"};
        String[] waxedPrefixes = {"waxed_", "waxed_exposed_", "waxed_weathered_", "waxed_oxidized_"};
        WeatheringCopper.WeatherState[] states = {
                WeatheringCopper.WeatherState.UNAFFECTED,
                WeatheringCopper.WeatherState.EXPOSED,
                WeatheringCopper.WeatherState.WEATHERED,
                WeatheringCopper.WeatherState.OXIDIZED
        };

        IFortunaBlock[] weatheringBlocks = new WeatheringMaterialBlock[4];
        IFortunaBlock[] waxedBlocks      = new MaterialBlock[4];
        for (int i = 0; i < 4; i++)
        {
            weatheringBlocks[i] = createMaterialBlock(material, true,  prefixes[i],      states[i]);
            waxedBlocks[i]      = createMaterialBlock(material, false, waxedPrefixes[i], states[i]);
        }

        OxidizableBlocksRegistry.registerOxidizableBlockPair((Block) weatheringBlocks[0], (Block) weatheringBlocks[1]);
        OxidizableBlocksRegistry.registerOxidizableBlockPair((Block) weatheringBlocks[1], (Block) weatheringBlocks[2]);
        OxidizableBlocksRegistry.registerOxidizableBlockPair((Block) weatheringBlocks[2], (Block) weatheringBlocks[3]);

        OxidizableBlocksRegistry.registerWaxableBlockPair((Block) weatheringBlocks[0], (Block) waxedBlocks[0]);
        OxidizableBlocksRegistry.registerWaxableBlockPair((Block) weatheringBlocks[1], (Block) waxedBlocks[1]);
        OxidizableBlocksRegistry.registerWaxableBlockPair((Block) weatheringBlocks[2], (Block) waxedBlocks[2]);
        OxidizableBlocksRegistry.registerWaxableBlockPair((Block) weatheringBlocks[3], (Block) waxedBlocks[3]);

        return Stream.concat(Arrays.stream(weatheringBlocks), Arrays.stream(waxedBlocks))
                .collect(Collectors.toList());
    }

    public static Map<String, IFortunaBlock> getRegisteredBlocks()
    {
        return registeredBlocks;
    }

}
