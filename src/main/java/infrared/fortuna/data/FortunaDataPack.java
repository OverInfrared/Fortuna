package infrared.fortuna.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.blocks.ModBlocks;
import infrared.fortuna.equipment.IFortunaEquipment;
import infrared.fortuna.items.FortunaArmor;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.materials.Material;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.worldgen.OreConfiguredFeature;
import net.fabricmc.fabric.api.resource.v1.pack.ModPackResources;
import net.fabricmc.fabric.impl.resource.pack.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FortunaDataPack implements PackResources, ModPackResources
{
    private static final String PACK_ID = "fortuna_dynamic_data";
    private static final FortunaDataPack INSTANCE = new FortunaDataPack();
    public static FortunaDataPack getInstance() { return INSTANCE; }

    private static final Set<String> namespaces = Set.of("minecraft", Fortuna.MOD_ID);

    private static HolderLookup.Provider registryLookup;

    private final PackLocationInfo locationInfo = new PackLocationInfo(
            PACK_ID,
            Component.literal("Fortuna Dynamic Data"),
            ModResourcePackCreator.RESOURCE_PACK_SOURCE,
            Optional.of(new KnownPack(ModResourcePackCreator.VANILLA, PACK_ID, "1.0.0"))
    );

    private FortunaDataPack() {}

    @Override
    public PackLocationInfo location() {
        return locationInfo;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... paths) {
        if (paths.length == 1 && paths[0].equals("pack.mcmeta")) {
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 94);
            pack.addProperty("min_format", 94);
            pack.addProperty("max_format", 95);
            pack.addProperty("description", "Fortuna Dynamic Data");
            JsonObject mcmeta = new JsonObject();
            mcmeta.add("pack", pack);
            byte[] bytes = mcmeta.toString().getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(bytes);
        }
        return null;
    }

    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> type) throws IOException {
        IoSupplier<InputStream> mcmeta = getRootResource("pack.mcmeta");
        if (mcmeta == null) return null;
        try (InputStream is = mcmeta.get()) {
            return AbstractPackResources.getMetadataFromStream(type, is, locationInfo);
        }
    }

    @Override
    public @NonNull Set<String> getNamespaces(@NonNull PackType packType) {
        return packType == PackType.SERVER_DATA ? namespaces : Set.of();
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NonNull PackType packType, @NonNull Identifier id) {
        if (packType != PackType.SERVER_DATA) return null;
        if (!namespaces.contains(id.getNamespace())) return null;

        String path = id.getPath();
        String prefix = path.substring(0, path.lastIndexOf('/') + 1);
        String entry = path.substring(path.lastIndexOf('/') + 1);

        String json = resolveResource(prefix, entry);
        if (json == null) return null;

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(@NonNull PackType packType, @NonNull String namespace,
                              @NonNull String prefix, @NonNull ResourceOutput resourceOutput)
    {
        if (packType != PackType.SERVER_DATA)
            return;

        if (!namespaces.contains(namespace))
            return;

        if (namespace.equals("minecraft"))
        {
            emitData(resourceOutput, prefix, "tags/block/mineable/pickaxe.json", namespace);
            emitData(resourceOutput, prefix, "tags/block/mineable/axe.json", namespace);
            emitData(resourceOutput, prefix, "tags/block/mineable/shovel.json", namespace);
            emitData(resourceOutput, prefix, "tags/block/needs_stone_tool.json", namespace);
            emitData(resourceOutput, prefix, "tags/block/needs_iron_tool.json", namespace);
            emitData(resourceOutput, prefix, "tags/block/needs_diamond_tool.json", namespace);
            emitData(resourceOutput, prefix, "tags/item/trimmable_armor.json", namespace);
            emitData(resourceOutput, prefix, "tags/item/trim_materials.json", namespace);
        }

        if (!namespace.equals(Fortuna.MOD_ID))
            return;

        Map<String, IFortunaBlock> blocks = ModBlocks.getRegisteredBlocks();
        Map<String, Item>          items  = ModItems.getRegisteredItem();

        if (prefix.contains("loot_table"))
            for (IFortunaBlock block : blocks.values())
                emitData(resourceOutput, prefix, "loot_table/blocks/" + block.getRegistryName() + ".json", namespace);

        if (prefix.contains("recipe"))
        {
            for (IFortunaBlock block : blocks.values())
                if (block instanceof IFortunaRecipe recipeSource)
                    for (String name : recipeSource.getRecipeNames())
                        emitData(resourceOutput, prefix, "recipe/" + name + ".json", namespace);

            for (Item item : items.values())
                if (item instanceof FortunaItem fortunaItem &&
                    fortunaItem instanceof IFortunaRecipe recipeSource)
                        for (String name : recipeSource.getRecipeNames())
                            emitData(resourceOutput, prefix, "recipe/" + name + ".json", namespace);
        }

        if (prefix.contains("trim_material"))
            for (Material mat : Fortuna.initializedMaterials)
                if (mat instanceof OreMaterial oreMat)
                    emitData(resourceOutput, prefix, "trim_material/" + oreMat.getName() + ".json", namespace);

        if (prefix.contains("worldgen/configured_feature"))
            for (Material mat : Fortuna.initializedMaterials)
                if (mat instanceof OreMaterial oreMat)
                    for (int i = 0; i < oreMat.getSpawnEntries().size(); i++)
                        emitData(resourceOutput, prefix, "worldgen/configured_feature/%s_ore_%d.json".formatted(oreMat.getName(), i), namespace);

        if (prefix.contains("worldgen/placed_feature"))
            for (Material mat : Fortuna.initializedMaterials)
                if (mat instanceof OreMaterial oreMat)
                    for (int i = 0; i < oreMat.getSpawnEntries().size(); i++)
                        emitData(resourceOutput, prefix, "worldgen/placed_feature/%s_ore_%d.json".formatted(oreMat.getName(), i), namespace);
    }

    @Override
    public void close() {}

    @Override @NonNull
    public ModMetadata getFabricModMetadata()
    {
        return FabricLoader.getInstance()
                .getModContainer(Fortuna.MOD_ID)
                .orElseThrow()
                .getMetadata();
    }

    @Override @NonNull
    public ModPackResources createOverlay(@NonNull String overlay)
    {
        return this;
    }

    private void emitData(ResourceOutput resourceOutput, String prefix, String path, String namespace) {
        if (!path.startsWith(prefix))
            return;

        Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
        resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.SERVER_DATA, id)).get());
    }

    private @Nullable String resolveResource(String prefix, String entry)
    {
        String path = prefix + entry;

        if (prefix.startsWith("tags/block/"))
            return switch (path)
            {
                case "tags/block/mineable/pickaxe.json"   -> generateToolTag(BlockTags.MINEABLE_WITH_PICKAXE);
                case "tags/block/mineable/axe.json"       -> generateToolTag(BlockTags.MINEABLE_WITH_AXE);
                case "tags/block/mineable/shovel.json"    -> generateToolTag(BlockTags.MINEABLE_WITH_SHOVEL);
                case "tags/block/needs_stone_tool.json"   -> generateMiningLevelTag(MiningLevel.Iron);
                case "tags/block/needs_iron_tool.json"    -> generateMiningLevelTag(MiningLevel.Diamond);
                case "tags/block/needs_diamond_tool.json" -> generateMiningLevelTag(MiningLevel.Netherite);
                default -> null;
            };

        if (prefix.startsWith("loot_table/blocks"))
        {
            String blockName = entry.replace(".json", "");
            IFortunaBlock block = ModBlocks.getRegisteredBlocks().get(blockName);
            if (block == null) return null;
            return block.getLoot(registryLookup).toString();
        }

        if (prefix.startsWith("recipe/"))
        {
            String recipeName = entry.replace(".json", "");
            return resolveRecipe(recipeName);
        }

        if (prefix.startsWith("tags/item/"))
            return switch (path)
            {
                case "tags/item/trimmable_armor.json" -> generateTrimmableArmorTag();
                case "tags/item/trim_materials.json"  -> generateTrimMaterialsTag();
                default -> null;
            };

        if (prefix.startsWith("trim_material/"))
        {
            String materialName = entry.replace(".json", "");
            for (Item item : ModItems.getRegisteredItem().values())
            {
                if (item instanceof IFortunaEquipment equipment &&
                        equipment.getEquipmentName().equals(materialName))
                    return equipment.getTrimMaterialJson().toString();
            }
            return null;
        }

        if (prefix.startsWith("worldgen/configured_feature/"))
        {
            String featureName = entry.replace(".json", "");
            return resolveConfiguredFeature(featureName);
        }

        if (prefix.startsWith("worldgen/placed_feature/"))
        {
            String featureName = entry.replace(".json", "");
            return resolvePlacedFeature(featureName);
        }

        return null;
    }

    private @Nullable String resolveRecipe(String recipeName)
    {
        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks().values())
        {
            if (block instanceof IFortunaRecipe recipeSource &&
                    recipeSource.getRecipeNames().contains(recipeName))
            {
                JsonObject json = recipeSource.getRecipes(registryLookup).get(recipeName);
                if (json != null)
                    return json.toString();
            }
        }

        for (Item item : ModItems.getRegisteredItem().values())
        {
            if (item instanceof IFortunaRecipe recipeSource &&
                    recipeSource.getRecipeNames().contains(recipeName))
            {
                JsonObject json = recipeSource.getRecipes(registryLookup).get(recipeName);
                if (json != null)
                    return json.toString();
            }
        }

        return null;
    }

    private String generateToolTag(TagKey<Block> toolTag)
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

    private String generateMiningLevelTag(MiningLevel requiredLevel)
    {
        JsonArray values = new JsonArray();
        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks().values())
            if (block.getMiningLevel().ordinal() == requiredLevel.ordinal())
                values.add("%s:%s".formatted(Fortuna.MOD_ID, block.getRegistryName()));

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    private String generateTrimmableArmorTag()
    {
        JsonArray values = new JsonArray();
        for (Item item : ModItems.getRegisteredItem().values())
        {
            if (item instanceof FortunaArmor armor)
                values.add("%s:%s".formatted(Fortuna.MOD_ID, armor.getRegistryName()));
        }

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    private String generateTrimMaterialsTag()
    {
        JsonArray values = new JsonArray();
        for (Material mat : Fortuna.initializedMaterials)
        {
            if (mat instanceof OreMaterial oreMat)
            {
                String itemName = oreMat.getRefinedRegistryName();
                values.add("%s:%s".formatted(Fortuna.MOD_ID, itemName));
            }
        }

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    private @Nullable String resolveConfiguredFeature(String featureName)
    {
        for (Material mat : Fortuna.initializedMaterials)
        {
            if (!(mat instanceof OreMaterial oreMat))
                continue;

            List<OreConfiguredFeature> entries = oreMat.getSpawnEntries();
            for (int i = 0; i < entries.size(); i++)
            {
                if (!featureName.equals("%s_ore_%d".formatted(oreMat.getName(), i)))
                    continue;

                return entries.get(i).generateConfiguredFeature();
            }
        }
        return null;
    }

    private @Nullable String resolvePlacedFeature(String featureName)
    {
        for (Material mat : Fortuna.initializedMaterials)
        {
            if (!(mat instanceof OreMaterial oreMat))
                continue;

            List<OreConfiguredFeature> entries = oreMat.getSpawnEntries();
            for (int i = 0; i < entries.size(); i++)
            {
                if (!featureName.equals("%s_ore_%d".formatted(oreMat.getName(), i)))
                    continue;

                OreConfiguredFeature spawn = entries.get(i);
                return generatePlacedFeature(oreMat, spawn, i);
            }
        }
        return null;
    }

    public static void setRegistryLookup(HolderLookup.Provider lookup)
    {
        registryLookup = lookup;
    }

    public static HolderLookup.Provider getRegistryLookup()
    {
        return registryLookup;
    }
}