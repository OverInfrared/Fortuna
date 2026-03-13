package infrared.fortuna.items;

import infrared.fortuna.Fortuna;

import infrared.fortuna.Utilities;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.enums.MaterialType;
import infrared.fortuna.items.ore.GemItem;
import infrared.fortuna.items.ore.IngotItem;
import infrared.fortuna.items.ore.RawItem;
import infrared.fortuna.materials.OreMaterial;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class ModItems
{
    private static final Map<String, Item> registeredItems = new LinkedHashMap<>();

    public static void registerItem(FortunaItem item) {
        Registry.register(BuiltInRegistries.ITEM, item.getResourceKey(), item);
        registeredItems.put(item.getRegistryName(),  item);
    }

    public static void registerBlock(IFortunaBlock block)
    {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, block.getRegistryName()));
        FortunaBlockItem blockItem = new FortunaBlockItem((Block) block, block.getDisplayName(), new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        registeredItems.put(block.getRegistryName(), blockItem);
    }

    public static final ResourceKey<CreativeModeTab> CUSTOM_CREATIVE_TAB_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, "creative_tab"));


    // Initializes items for ores, raw material, refined material, tools, and armor items.
    public static void initializeOreMaterial(OreMaterial material)
    {
        String name = material.getName();
        MaterialType type = material.getType();

        if (type == MaterialType.Gem || type == MaterialType.Special)
        {
            ResourceKey<Item>                    key               = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, name));
            DynamicProperties<Item, OreMaterial> dynamicProperties = new DynamicProperties<>(name, Component.literal(Utilities.capitalize(name)), key, material);
            Properties                           properties        = new Properties();

            registerItem(new GemItem(dynamicProperties, properties));
        }
        else if (type == MaterialType.Ingot)
        {
            // Raw Ore Item
            String            rawName = "raw_%s".formatted(name);
            ResourceKey<Item> rawKey  = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, rawName));

            DynamicProperties<Item, OreMaterial> rawDynamicProperties = new DynamicProperties<>(rawName, Component.literal("Raw %s".formatted(Utilities.capitalize(name))), rawKey, material);
            Properties                           properties           = new Properties();

            registerItem(new RawItem(rawDynamicProperties, properties));

            // Refined Ore Item
            String            refinedName = "%s_ingot".formatted(name);
            ResourceKey<Item> refinedKey  = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, refinedName));

            DynamicProperties<Item, OreMaterial> refinedDynamicProperties = new DynamicProperties<>(refinedName, Component.literal("%s Ingot".formatted(Utilities.capitalize(name))), refinedKey, material);
            Properties                           refinedProperties        = new Properties();

            registerItem(new IngotItem(refinedDynamicProperties, refinedProperties));
        }
    }

    public static void initializeCreativeModeTab()
    {
        CreativeModeTab creativeTab = FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.registeredItems.values().iterator().next()))
                .title(Component.literal("Fortuna"))
                .displayItems((params, output) -> {
                    for (Item item : ModItems.registeredItems.values())
                        output.accept(item);
                })
                .build();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CUSTOM_CREATIVE_TAB_KEY, creativeTab);
    }

    public static Map<String, Item> getRegisteredItem()
    {
        return registeredItems;
    }
}
