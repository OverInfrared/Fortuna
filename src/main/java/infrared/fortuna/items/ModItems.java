package infrared.fortuna.items;

import infrared.fortuna.Fortuna;

import infrared.fortuna.util.Utilities;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.MaterialType;
import infrared.fortuna.items.ore.GemItem;
import infrared.fortuna.items.ore.IngotItem;
import infrared.fortuna.items.ore.RawItem;
import infrared.fortuna.materials.ore.OreMaterial;
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
import java.util.stream.Collectors;

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
            registerItem(new GemItem(createItemProperties(name, material), new Properties().trimMaterial(material.getTrimMaterialKey())));
        }
        else if (type == MaterialType.Ingot)
        {
            // Raw Ore Item
            registerItem(new RawItem(createItemProperties("raw", name, "", material), new Properties()));

            // Refined Ore Item
            registerItem(new IngotItem(createItemProperties(name, "ingot", material), new Properties().trimMaterial(material.getTrimMaterialKey())));
        }

        // Generate tool items
        if (material.hasTools())
            for (DynamicToolType tool : DynamicToolType.values())
                registerItem(new FortunaTool(createItemProperties(name, tool.getName(), material), new Properties(), tool));

        // Generate armor items
        if (material.hasArmor())
            for (DynamicArmorType armor : DynamicArmorType.values())
                registerItem(new FortunaArmor(createItemProperties(name, armor.getName(), material), new Properties(), armor));
    }

    private static DynamicProperties<Item, OreMaterial> createItemProperties(String name, OreMaterial material)
    {
        return createItemProperties("", name, "", material);
    }

    private static DynamicProperties<Item, OreMaterial> createItemProperties(String name, String suffix, OreMaterial material)
    {
        return createItemProperties("", name, suffix, material);
    }

    private static DynamicProperties<Item, OreMaterial> createItemProperties(String prefix, String name, String suffix, OreMaterial material)
    {
        String registryName = name;

        if (!prefix.isEmpty())
            registryName = "%s_%s".formatted(prefix, registryName);

        if (!suffix.isEmpty())
            registryName = "%s_%s".formatted(registryName, suffix);

        String displayName = Arrays.stream(registryName.split("_"))
                .filter(s -> !s.isEmpty())
                .map(Utilities::capitalize)
                .collect(Collectors.joining(" "));

        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, registryName));

        return new DynamicProperties<>(registryName, Component.literal(displayName), key, material);
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
