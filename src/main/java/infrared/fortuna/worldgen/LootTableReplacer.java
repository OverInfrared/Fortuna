package infrared.fortuna.worldgen;

import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.ore.enums.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

public class LootTableReplacer
{
    private static final Map<Item, MiningLevel> VANILLA_ITEM_TIERS = new HashMap<>();

    static
    {
        // Copper tier replacements
        VANILLA_ITEM_TIERS.put(Items.COAL, MiningLevel.Copper);
        VANILLA_ITEM_TIERS.put(Items.RAW_COPPER, MiningLevel.Copper);
        VANILLA_ITEM_TIERS.put(Items.COPPER_INGOT, MiningLevel.Copper);

        // Iron tier replacements
        VANILLA_ITEM_TIERS.put(Items.RAW_IRON, MiningLevel.Iron);
        VANILLA_ITEM_TIERS.put(Items.IRON_INGOT, MiningLevel.Iron);
        VANILLA_ITEM_TIERS.put(Items.IRON_NUGGET, MiningLevel.Iron);
        VANILLA_ITEM_TIERS.put(Items.RAW_GOLD, MiningLevel.Iron);
        VANILLA_ITEM_TIERS.put(Items.GOLD_INGOT, MiningLevel.Iron);
        VANILLA_ITEM_TIERS.put(Items.GOLD_NUGGET, MiningLevel.Iron);
        VANILLA_ITEM_TIERS.put(Items.REDSTONE, MiningLevel.Iron);
        VANILLA_ITEM_TIERS.put(Items.LAPIS_LAZULI, MiningLevel.Iron);

        // Diamond tier replacements
        VANILLA_ITEM_TIERS.put(Items.DIAMOND, MiningLevel.Diamond);
        VANILLA_ITEM_TIERS.put(Items.EMERALD, MiningLevel.Diamond);

        // Netherite tier replacements
        VANILLA_ITEM_TIERS.put(Items.NETHERITE_INGOT, MiningLevel.Netherite);
        VANILLA_ITEM_TIERS.put(Items.NETHERITE_SCRAP, MiningLevel.Netherite);
    }

    public static void register()
    {
        LootTableEvents.MODIFY_DROPS.register((entry, context, drops) ->
        {
            for (int i = 0; i < drops.size(); i++)
            {
                ItemStack stack = drops.get(i);
                MiningLevel tier = VANILLA_ITEM_TIERS.get(stack.getItem());

                if (tier == null)
                    continue;

                OreMaterial replacement = VanillaReplacementMap.getReplacement(tier);
                if (replacement == null)
                    continue;

                String replacementName = replacement.getRefinedRegistryName();
                Item replacementItem = BuiltInRegistries.ITEM
                        .get(Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, replacementName))
                        .map(ref -> ref.value())
                        .orElse(null);

                if (replacementItem != null)
                    drops.set(i, new ItemStack(replacementItem, stack.getCount()));
            }
        });
    }
}