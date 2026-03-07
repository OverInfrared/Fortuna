package infrared.fortuna.blocks;

import infrared.fortuna.Fortuna;
import infrared.fortuna.Utilities;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.resources.Material;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlock
{
    private static Block register(String name, String displayName, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem)
    {
        // Create a registry key for the block
        ResourceKey<Block> blockKey = keyOfBlock(name);
        // Create the block instance
        Block block = blockFactory.apply(settings.setId(blockKey));

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:moving_piston` or `minecraft:end_gateway`
        if (shouldRegisterItem)
        {
            // Items need to be registered with a different type of registry key, but the ID
            // can be the same.
            ResourceKey<Item> itemKey = keyOfItem(name);
            ModItems.registerBlock(displayName, block, itemKey);
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
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
    public static void initializeMaterial(Material material)
    {
        switch (material.getMaterialRaw())
        {
            case Gem:
            case Ingot:
                String displayName = Utilities.capitalize(material.getName()) + " Ore";
                register(material.getName() + "_ore", displayName, Block::new, BlockBehaviour.Properties.of().sound(SoundType.STONE), true);
                break;
        }
    }

}
