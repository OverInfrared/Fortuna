package infrared.fortuna.items;

import infrared.fortuna.Fortuna;
import infrared.fortuna.Utilities;
import infrared.fortuna.resources.Material;
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
import java.util.function.Function;

public class ModItems
{
    public static void register(String name, String displayName, Function<Item.Properties, FortunaItem> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, name));

        // Create the item instance.
        FortunaItem item = itemFactory.apply(settings.setId(itemKey));
        item.setDisplayComponent(displayName);

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        registeredItems.add(item);
    }

    public static void registerBlock(String displayName, Block block, ResourceKey<Item> itemKey)
    {
        FortunaBlockItem blockItem = new FortunaBlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        blockItem.setDisplayComponent(displayName);

        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        registeredItems.add(blockItem);
    }

    public static final ResourceKey<CreativeModeTab> CUSTOM_CREATIVE_TAB_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, "creative_tab"));

    private static final List<Item> registeredItems = new ArrayList<>();

    // Initializes items for ores, raw material, refined material, tools, and armor items.
    public static void initializeMaterial(Material material)
    {
        // Generate raw and refined items.
        switch (material.getMaterialRaw())
        {
            case Gem:
            case Special:
                String displayName = Utilities.capitalize(material.getName());
                register(material.getName(), displayName, FortunaItem::new, material.getItemProperties());
                break;
            case Ingot:
                String capitalizedName = Utilities.capitalize(material.getName());
                register("raw_" + material.getName(), "Raw " + capitalizedName, FortunaItem::new, material.getItemProperties());
                register(material.getName() + "_ingot", capitalizedName + " Ingot", FortunaItem::new, material.getItemProperties());
                break;
        }
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
