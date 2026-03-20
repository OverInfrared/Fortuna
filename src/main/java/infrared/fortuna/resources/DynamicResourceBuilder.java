package infrared.fortuna.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.*;
import infrared.fortuna.blocks.ore.BarsBlock;
import infrared.fortuna.blocks.ore.IBarsBlock;
import infrared.fortuna.blocks.ore.IDoorBlock;
import infrared.fortuna.equipment.IFortunaEquipment;
import infrared.fortuna.items.*;
import infrared.fortuna.materials.Material;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.util.PaletteGenerator;
import infrared.fortuna.worldgen.OreFeatureType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class DynamicResourceBuilder
{
    public static void buildServerResources()
    {
        DynamicResourceRegistry reg = DynamicResourceRegistry.server();
        reg.clear();

        // =====================================================================
        // Minecraft namespace — tags
        // =====================================================================

        // Block tags — mining tools
        reg.register("minecraft", "tags/block/mineable/pickaxe.json",
                () -> generateToolTag(BlockTags.MINEABLE_WITH_PICKAXE));
        reg.register("minecraft", "tags/block/mineable/axe.json",
                () -> generateToolTag(BlockTags.MINEABLE_WITH_AXE));
        reg.register("minecraft", "tags/block/mineable/shovel.json",
                () -> generateToolTag(BlockTags.MINEABLE_WITH_SHOVEL));

        // Block tags — mining levels
        reg.register("minecraft", "tags/block/needs_stone_tool.json",
                () -> generateMiningLevelTag(MiningLevel.Copper));
        reg.register("minecraft", "tags/block/needs_iron_tool.json",
                () -> generateMiningLevelTag(MiningLevel.Iron));
        reg.register("minecraft", "tags/block/needs_diamond_tool.json",
                () -> generateMiningLevelTag(MiningLevel.Diamond, MiningLevel.Netherite));

        // Block tags — bars
        reg.register("minecraft", "tags/block/bars.json",
                () -> generateBarsTag());

        // Item tags — armor trims
        reg.register("minecraft", "tags/item/trimmable_armor.json",
                () -> generateTrimmableArmorTag());
        reg.register("minecraft", "tags/item/trim_materials.json",
                () -> generateTrimMaterialsTag());

        // =====================================================================
        // Fortuna namespace — blocks
        // =====================================================================

        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks().values())
        {
            String name = block.getRegistryName();

            // Loot tables
            reg.register(Fortuna.MOD_ID, "loot_table/blocks/" + name + ".json",
                    () -> block.getLoot(FortunaDataPack.getRegistryLookup()).toString());

            // Recipes from blocks
            if (block instanceof IFortunaRecipe recipeSource)
            {
                for (String recipeName : recipeSource.getRecipeNames())
                {
                    reg.register(Fortuna.MOD_ID, "recipe/" + recipeName + ".json",
                            () -> recipeSource.getRecipes(FortunaDataPack.getRegistryLookup()).get(recipeName).toString());
                }
            }
        }

        // =====================================================================
        // Fortuna namespace — items
        // =====================================================================

        for (Item item : ModItems.getRegisteredItem().values())
        {
            // Recipes from items
            if (item instanceof IFortunaRecipe recipeSource)
            {
                for (String recipeName : recipeSource.getRecipeNames())
                {
                    reg.register(Fortuna.MOD_ID, "recipe/" + recipeName + ".json",
                            () -> recipeSource.getRecipes(FortunaDataPack.getRegistryLookup()).get(recipeName).toString());
                }
            }
        }

        // =====================================================================
        // Fortuna namespace — materials
        // =====================================================================

        for (Material mat : Fortuna.initializedMaterials)
        {
            if (!(mat instanceof OreMaterial oreMat))
                continue;

            // Trim materials
            if (oreMat.isTrimable())
            {
                reg.register(Fortuna.MOD_ID, "trim_material/" + oreMat.getName() + ".json", () ->
                {
                    for (Item item : ModItems.getRegisteredItem().values())
                    {
                        if (item instanceof IFortunaEquipment equipment &&
                                equipment.getEquipmentName().equals(oreMat.getName()))
                            return equipment.getTrimMaterialJson().toString();
                    }
                    return null;
                });
            }

            // Custom repair item tags
            if (oreMat.getRepairItemTag() != null)
            {
                String tagPath = oreMat.getRepairItemTag().location().getPath();
                reg.register(Fortuna.MOD_ID, "tags/item/" + tagPath + ".json", () ->
                {
                    JsonArray values = new JsonArray();
                    values.add("%s:%s".formatted(Fortuna.MOD_ID, oreMat.getRefinedRegistryName()));

                    JsonObject tag = new JsonObject();
                    tag.addProperty("replace", false);
                    tag.add("values", values);
                    return tag.toString();
                });
            }

            // Configured features
            for (OreFeatureType type : oreMat.getConfiguredFeatures().keySet())
            {
                String featureName = "%s_ore_%s".formatted(oreMat.getName(), type.getName());
                reg.register(Fortuna.MOD_ID, "worldgen/configured_feature/" + featureName + ".json",
                        () -> oreMat.getConfiguredFeatures().get(type).generateConfiguredFeature());
            }

            // Placed features
            for (int i = 0; i < oreMat.getPlacedFeatures().size(); i++)
            {
                int index = i;
                String featureName = "%s_ore_%d".formatted(oreMat.getName(), index);
                reg.register(Fortuna.MOD_ID, "worldgen/placed_feature/" + featureName + ".json",
                        () -> oreMat.getPlacedFeatures().get(index).generatePlacedFeature());
            }
        }
    }

    // =========================================================================
    // Tag generators
    // =========================================================================

    private static String generateToolTag(TagKey<Block> toolTag)
    {
        JsonArray values = new JsonArray();
        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks().values())
            if (block.getRequiredTool().equals(toolTag))
                values.add("%s:%s".formatted(Fortuna.MOD_ID, block.getRegistryName()));

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    private static String generateMiningLevelTag(MiningLevel... levels)
    {
        Set<MiningLevel> levelSet = Set.of(levels);
        JsonArray values = new JsonArray();
        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks().values())
            if (levelSet.contains(block.getMiningLevel()))
                values.add("%s:%s".formatted(Fortuna.MOD_ID, block.getRegistryName()));

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    private static String generateBarsTag()
    {
        JsonArray values = new JsonArray();
        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks().values())
            if (block instanceof IBarsBlock)
                values.add("%s:%s".formatted(Fortuna.MOD_ID, block.getRegistryName()));

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    private static String generateTrimmableArmorTag()
    {
        JsonArray values = new JsonArray();
        for (Item item : ModItems.getRegisteredItem().values())
            if (item instanceof FortunaArmor armor)
                values.add("%s:%s".formatted(Fortuna.MOD_ID, armor.getRegistryName()));

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    private static String generateTrimMaterialsTag()
    {
        JsonArray values = new JsonArray();
        for (Material mat : Fortuna.initializedMaterials)
            if (mat instanceof OreMaterial oreMat)
                values.add("%s:%s".formatted(Fortuna.MOD_ID, oreMat.getRefinedRegistryName()));

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    public static void buildClientResources()
    {
        DynamicResourceRegistry reg = DynamicResourceRegistry.client();
        reg.clear();

        // =====================================================================
        // Minecraft namespace — atlases
        // =====================================================================

        reg.register("minecraft", "atlases/armor_trims.json", DynamicResourceBuilder::generateArmorTrimsAtlas);
        reg.register("minecraft", "atlases/items.json", DynamicResourceBuilder::generateItemsAtlas);

        // =====================================================================
        // Fortuna namespace — lang
        // =====================================================================

        reg.register(Fortuna.MOD_ID, "lang/en_us.json", DynamicResourceBuilder::generateLang);

        // =====================================================================
        // Fortuna namespace — blocks
        // =====================================================================

        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks().values())
        {
            String name = block.getRegistryName();

            // Blockstates — all blocks
            reg.register(Fortuna.MOD_ID, "blockstates/" + name + ".json",
                    block::getBlockStateString);

            // Item model — all blocks
            reg.register(Fortuna.MOD_ID, "items/" + name + ".json",
                    block::getItemString);

            // Block models
            if (block instanceof IDoorBlock)
            {
                reg.register(Fortuna.MOD_ID, "models/item/" + name + ".json",
                        block::getItemModelString);

                for (String suffix : IDoorBlock.DOOR_MODELS)
                    reg.register(Fortuna.MOD_ID, "models/block/" + name + "_" + suffix + ".json",
                            () -> block.getModelString(suffix));
            }
            else if (block instanceof FortunaTrapDoorBlock)
            {
                // 3 trapdoor variant models
                for (String suffix : FortunaTrapDoorBlock.TRAPDOOR_MODELS)
                    reg.register(Fortuna.MOD_ID, "models/block/" + name + "_" + suffix + ".json",
                            () -> block.getModelString(suffix));
            }
            else if (block instanceof IBarsBlock)
            {
                reg.register(Fortuna.MOD_ID, "models/item/" + name + ".json",
                        block::getItemModelString);

                for (String suffix : BarsBlock.BARS_MODELS)
                    reg.register(Fortuna.MOD_ID, "models/block/" + name + "_" + suffix + ".json",
                            () -> block.getModelString(suffix));
            }
            else
            {
                // Regular blocks — single block model
                reg.register(Fortuna.MOD_ID, "models/block/" + name + ".json",
                        block::getModelString);
            }
        }

        // =====================================================================
        // Fortuna namespace — items
        // =====================================================================

        Set<String> emittedEquipment = new HashSet<>();
        Set<String> emittedPalettes = new HashSet<>();

        for (Item item : ModItems.getRegisteredItem().values())
        {
            if (item instanceof IFortunaItem fortunaItem && !(item instanceof BlockItem))
            {
                String name = fortunaItem.getRegistryName();

                reg.register(Fortuna.MOD_ID, "models/item/" + name + ".json",
                        fortunaItem::getModelString);

                reg.register(Fortuna.MOD_ID, "items/" + name + ".json",
                        fortunaItem::getItemString);
            }

            if (item instanceof IFortunaEquipment equipment)
            {
                String eName = equipment.getEquipmentName();

                if (emittedEquipment.add(eName))
                    reg.register(Fortuna.MOD_ID, "equipment/" + eName + ".json",
                            () -> equipment.getEquipmentAsset().toString());

                if (emittedPalettes.add(eName))
                    reg.registerBinary(Fortuna.MOD_ID, "textures/trims/color_palettes/" + eName + ".png",
                            () -> PaletteGenerator.generateTrimPalette(equipment.getMaterial().getMainColor()));
            }
        }

        // =====================================================================
        // Fortuna namespace — trim models
        // =====================================================================

        String[] vanillaTrimMaterials = { "quartz", "iron", "netherite", "redstone", "copper", "gold", "emerald", "diamond", "lapis", "amethyst", "resin" };

        for (Item item : ModItems.getRegisteredItem().values())
        {
            if (!(item instanceof FortunaArmor armor))
                continue;

            String armorName = armor.getRegistryName();

            for (String trim : vanillaTrimMaterials)
                reg.register(Fortuna.MOD_ID, "models/item/" + armorName + "_" + trim + "_trim.json",
                        () -> generateTrimModel(armor, trim));

            for (Material material : Fortuna.initializedMaterials)
                if (material instanceof OreMaterial oreMat)
                    reg.register(Fortuna.MOD_ID, "models/item/" + armorName + "_" + oreMat.getName() + "_trim.json",
                            () -> generateTrimModel(armor, oreMat.getName()));
        }
    }

// =========================================================================
// Client resource generators
// =========================================================================

    private static String generateTrimModel(FortunaArmor armor, String trim)
    {
        DynamicArmorType armorType = armor.getArmorType();
        String texture = armorType.getItemTexture(armor.getDynamicProperties().material().getArmorVariant());

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "%s:item/%s".formatted(Fortuna.MOD_ID, texture));
        textures.addProperty("layer1", "minecraft:%s_%s".formatted(armorType.getTrimPrefix(), trim));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        model.add("textures", textures);

        return model.toString();
    }

    private static String generateLang()
    {
        JsonObject lang = new JsonObject();

        Set<String> emitted = new HashSet<>();
        for (Item item : ModItems.getRegisteredItem().values())
        {
            if (item instanceof IFortunaEquipment equipment)
            {
                String key = equipment.getLangKey();
                if (emitted.add(key))
                    lang.addProperty(key, equipment.getLangValue());
            }
        }

        return lang.toString();
    }

    private static String generateArmorTrimsAtlas()
    {
        JsonObject permutations = new JsonObject();
        for (Material mat : Fortuna.initializedMaterials)
            if (mat instanceof OreMaterial oreMat && oreMat.hasArmor())
                permutations.addProperty(oreMat.getName(), "%s:trims/color_palettes/%s".formatted(Fortuna.MOD_ID, oreMat.getName()));

        String[] patterns = { "sentry", "dune", "coast", "wild", "ward", "eye", "vex", "tide", "snout", "rib", "spire", "wayfinder", "shaper", "silence", "raiser", "host", "flow", "bolt" };
        JsonArray textures = new JsonArray();
        for (String pattern : patterns)
        {
            textures.add("minecraft:trims/entity/humanoid/" + pattern);
            textures.add("minecraft:trims/entity/humanoid_leggings/" + pattern);
        }

        JsonObject palettedPermutation = new JsonObject();
        palettedPermutation.addProperty("type", "minecraft:paletted_permutations");
        palettedPermutation.add("textures", textures);
        palettedPermutation.addProperty("palette_key", "minecraft:trims/color_palettes/trim_palette");
        palettedPermutation.add("permutations", permutations);

        JsonArray sources = new JsonArray();
        sources.add(palettedPermutation);

        JsonObject atlas = new JsonObject();
        atlas.add("sources", sources);

        return atlas.toString();
    }

    private static String generateItemsAtlas()
    {
        JsonObject permutations = new JsonObject();
        for (Material mat : Fortuna.initializedMaterials)
            if (mat instanceof OreMaterial oreMat && oreMat.hasArmor())
                permutations.addProperty(oreMat.getName(), "%s:trims/color_palettes/%s".formatted(Fortuna.MOD_ID, oreMat.getName()));

        JsonArray textures = new JsonArray();
        textures.add("minecraft:trims/items/helmet_trim");
        textures.add("minecraft:trims/items/chestplate_trim");
        textures.add("minecraft:trims/items/leggings_trim");
        textures.add("minecraft:trims/items/boots_trim");

        JsonObject palettedPermutation = new JsonObject();
        palettedPermutation.addProperty("type", "minecraft:paletted_permutations");
        palettedPermutation.add("textures", textures);
        palettedPermutation.addProperty("palette_key", "minecraft:trims/color_palettes/trim_palette");
        palettedPermutation.add("permutations", permutations);

        JsonArray sources = new JsonArray();
        sources.add(palettedPermutation);

        JsonObject atlas = new JsonObject();
        atlas.add("sources", sources);

        return atlas.toString();
    }
}