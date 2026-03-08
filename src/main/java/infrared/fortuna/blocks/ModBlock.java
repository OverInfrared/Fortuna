package infrared.fortuna.blocks;

import infrared.fortuna.Fortuna;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.resources.materials.Material;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModBlock
{
    private static void registerMaterial(Material material)
    {
        for (FortunaBlock block : material.getBlocks())
        {
            ModItems.registerBlock(block);
            Registry.register(BuiltInRegistries.BLOCK, block.getResourceKey(), block);
        }
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
        registerMaterial(material);
    }

}
