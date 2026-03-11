package infrared.fortuna.items;

import infrared.fortuna.Fortuna;

import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.resources.materials.Material;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ModItems
{
    public static void registerMaterial(Material material) {
        for (FortunaItem item : material.getItems())
        {
            Registry.register(BuiltInRegistries.ITEM, item.getResourceKey(), item);
            registeredItems.add(item);
        }
    }

    public static void registerBlock(IFortunaBlock block)
    {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, block.getRegistryName()));
        FortunaBlockItem blockItem = new FortunaBlockItem((Block) block, block.getDisplayName(), new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        registeredItems.add(blockItem);
    }

    public static final ResourceKey<CreativeModeTab> CUSTOM_CREATIVE_TAB_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, "creative_tab"));

    private static final List<Item> registeredItems = new ArrayList<>();

    // Initializes items for ores, raw material, refined material, tools, and armor items.
    public static void initializeMaterial(Material material)
    {
        registerMaterial(material);
    }

   public static void initializeCreativeModeTab()
   {
       CreativeModeTab creativeTab = FabricItemGroup.builder()
               .icon(() -> new ItemStack(ModItems.registeredItems.getFirst()))
               .title(Component.literal("Fortuna"))
               .displayItems((params, output) -> {
                   for (Item item : ModItems.registeredItems)
                   {
                       output.accept(item);
                   }
               })
               .build();

       Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CUSTOM_CREATIVE_TAB_KEY, creativeTab);
   }
}
