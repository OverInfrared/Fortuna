package infrared.fortuna.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.blocks.ModBlocks;
import infrared.fortuna.enums.DynamicArmorType;
import infrared.fortuna.equipment.IFortunaEquipment;
import infrared.fortuna.items.FortunaArmor;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.materials.Material;
import infrared.fortuna.materials.OreMaterial;
import infrared.fortuna.util.PaletteGenerator;
import infrared.fortuna.util.Utilities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static infrared.fortuna.Fortuna.findMaterial;

public class FortunaResourcePack extends AbstractPackResources implements RepositorySource
{
    private static final String PACK_ID = "fortuna_dynamic";
    private static final FortunaResourcePack INSTANCE = new FortunaResourcePack();

    public static FortunaResourcePack getInstance() { return INSTANCE; }

    private FortunaResourcePack()
    {
        super(new PackLocationInfo(
                PACK_ID,
                Component.literal("Fortuna Dynamic Resources"),
                PackSource.BUILT_IN,
                Optional.empty()
        ));
    }

    @Override
    public @NonNull Set<String> getNamespaces(@NonNull PackType packType)
    {
        return packType == PackType.CLIENT_RESOURCES ? Set.of(Fortuna.MOD_ID, "minecraft") : Set.of();
    }

    @Override
    public void loadPacks(@NonNull Consumer<Pack> consumer)
    {
        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier()
        {
            @Override
            public @NonNull PackResources openPrimary(@NonNull PackLocationInfo info) { return INSTANCE; }
            @Override
            public @NonNull PackResources openFull(@NonNull PackLocationInfo info, Pack.@NonNull Metadata metadata) { return INSTANCE; }
        };

        Pack pack = Pack.readMetaAndCreate(
                new PackLocationInfo(PACK_ID, Component.literal("Fortuna Dynamic Resources"), PackSource.BUILT_IN, Optional.empty()),
                supplier,
                PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(true, Pack.Position.TOP, false)
        );
        if (pack != null) consumer.accept(pack);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... paths)
    {
        if (paths.length != 1 || !paths[0].equals("pack.mcmeta")) return null;

        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", 75);
        pack.addProperty("min_format", 75);
        pack.addProperty("max_format", 75);
        pack.addProperty("description", "Fortuna dynamic resources");

        JsonObject mcmeta = new JsonObject();
        mcmeta.add("pack", pack);

        return () -> new ByteArrayInputStream(mcmeta.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NonNull PackType packType, @NonNull Identifier id)
    {
        if (packType != PackType.CLIENT_RESOURCES) return null;

        String ns = id.getNamespace();
        String path = id.getPath();

        // Palette PNGs (fortuna namespace)
        if (ns.equals(Fortuna.MOD_ID) && path.startsWith("textures/trims/color_palettes/") && path.endsWith(".png"))
        {
            String name = path.substring("textures/trims/color_palettes/".length(), path.length() - ".png".length());
            OreMaterial material = findMaterial(name);
            if (material == null) return null;

            byte[] png = PaletteGenerator.generateTrimPalette(material.getMainColor());
            return () -> new ByteArrayInputStream(png);
        }

        // Atlas overrides (minecraft namespace)
        if (ns.equals("minecraft") && path.startsWith("atlases/"))
        {
            String json = resolveMinecraftResource(path);
            if (json == null) return null;
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(bytes);
        }

        // Fortuna JSON resources
        if (!ns.equals(Fortuna.MOD_ID)) return null;

        String prefix = path.substring(0, path.lastIndexOf('/') + 1);
        String entry = path.substring(path.lastIndexOf('/') + 1);

        String json = resolveResource(prefix, entry);
        if (json == null) return null;

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(@NonNull PackType packType, @NonNull String namespace, @NonNull String prefix,
                              @NonNull ResourceOutput resourceOutput)
    {
        if (packType != PackType.CLIENT_RESOURCES) return;

        if (namespace.equals("minecraft"))
        {
            emitMinecraftResource(resourceOutput, prefix, "atlases/armor_trims.json");
            emitMinecraftResource(resourceOutput, prefix, "atlases/items.json");
            return;
        }

        if (!namespace.equals(Fortuna.MOD_ID)) return;

        emitResource(resourceOutput, prefix, "lang/en_us.json");

        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks().values())
        {
            emitResource(resourceOutput, prefix, "blockstates/" + block.getRegistryName() + ".json");
            emitResource(resourceOutput, prefix, "models/block/" + block.getRegistryName() + ".json");
            emitResource(resourceOutput, prefix, "items/" + block.getRegistryName() + ".json");
        }

        Set<String> emittedPalettes = new HashSet<>();
        Set<String> emittedEquipment = new HashSet<>();
        for (Item item : ModItems.getRegisteredItem().values())
        {
            if (item instanceof FortunaItem fortunaItem)
            {
                emitResource(resourceOutput, prefix, "models/item/" + fortunaItem.getRegistryName() + ".json");
                emitResource(resourceOutput, prefix, "items/" + fortunaItem.getRegistryName() + ".json");
            }

            if (item instanceof IFortunaEquipment equipment)
            {
                String name = equipment.getEquipmentName();
                if (emittedEquipment.add(name))
                    emitResource(resourceOutput, prefix, "equipment/" + name + ".json");
                if (emittedPalettes.add(name))
                    emitResource(resourceOutput, prefix, "textures/trims/color_palettes/" + name + ".png");
            }
        }

        String[] vanillaTrimMaterials = { "quartz", "iron", "netherite", "redstone", "copper", "gold", "emerald", "diamond", "lapis", "amethyst", "resin" };
        for (Item item : ModItems.getRegisteredItem().values())
        {
            if (item instanceof FortunaArmor armor)
            {
                for (String trim : vanillaTrimMaterials)
                    emitResource(resourceOutput, prefix, "models/item/" + armor.getRegistryName() + "_" + trim + "_trim.json");

                for (infrared.fortuna.materials.Material mat : Fortuna.initializedMaterials)
                    if (mat instanceof OreMaterial oreMat)
                        emitResource(resourceOutput, prefix, "models/item/" + armor.getRegistryName() + "_" + oreMat.getName() + "_trim.json");
            }
        }
    }

    @Override
    public void close() {}

    private void emitResource(ResourceOutput resourceOutput, String prefix, String path)
    {
        if (!path.startsWith(prefix))
            return;

        Identifier id = Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, path);
        resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.CLIENT_RESOURCES, id)).get());
    }

    private void emitMinecraftResource(ResourceOutput resourceOutput, String prefix, String path)
    {
        if (!path.startsWith(prefix))
            return;

        Identifier id = Identifier.fromNamespaceAndPath("minecraft", path);
        resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.CLIENT_RESOURCES, id)).get());
    }

    private @Nullable String resolveResource(String prefix, String entry)
    {
        String name = entry.replace(".json", "");

        if (prefix.startsWith("lang/"))
        {
            if (name.equals("en_us"))
                return generateLang();
            return null;
        }

        if (prefix.startsWith("blockstates/"))
        {
            IFortunaBlock block = ModBlocks.getRegisteredBlocks().get(name);
            return block != null ? block.getBlockStateString() : null;
        }

        if (prefix.startsWith("models/block/"))
        {
            IFortunaBlock block = ModBlocks.getRegisteredBlocks().get(name);
            return block != null ? block.getModelString() : null;
        }

        if (prefix.startsWith("models/item/"))
        {
            // Check for trim variant models
            if (name.endsWith("_trim"))
            {
                return resolveTrimModel(name);
            }

            Item item = ModItems.getRegisteredItem().get(name);
            return item instanceof FortunaItem fortunaItem ? fortunaItem.getModelString() : null;
        }

        if (prefix.startsWith("items/"))
        {
            IFortunaBlock block = ModBlocks.getRegisteredBlocks().get(name);
            if (block != null) return block.getItemString();

            Item item = ModItems.getRegisteredItem().get(name);
            return item instanceof FortunaItem fortunaItem ? fortunaItem.getItemString() : null;
        }

        if (prefix.startsWith("equipment/"))
        {
            for (Item item : ModItems.getRegisteredItem().values())
            {
                if (item instanceof IFortunaEquipment equipment &&
                        equipment.getEquipmentName().equals(name))
                    return equipment.getEquipmentAsset().toString();
            }
            return null;
        }

        return null;
    }

    private String generateLang()
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

    private @Nullable String resolveTrimModel(String name)
    {
        String[] vanillaTrimMaterials = { "quartz", "iron", "netherite", "redstone", "copper", "gold", "emerald", "diamond", "lapis", "amethyst", "resin" };

        // Try vanilla trims
        for (String trim : vanillaTrimMaterials)
        {
            String result = tryResolveTrim(name, trim);
            if (result != null) return result;
        }

        // Try dynamic trims
        for (Material mat : Fortuna.initializedMaterials)
        {
            if (mat instanceof Material material)
            {
                String result = tryResolveTrim(name, material.getName());
                if (result != null) return result;
            }
        }

        return null;
    }

    private @Nullable String tryResolveTrim(String name, String trim)
    {
        String suffix = "_" + trim + "_trim";
        if (!name.endsWith(suffix))
            return null;

        String armorName = name.substring(0, name.length() - suffix.length());
        Item item = ModItems.getRegisteredItem().get(armorName);
        if (!(item instanceof FortunaArmor armor))
            return null;

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

    private @Nullable String resolveMinecraftResource(String path)
    {
        return switch (path)
        {
            case "atlases/armor_trims.json" -> generateArmorTrimsAtlas();
            case "atlases/items.json" -> generateItemsAtlas();
            default -> null;
        };
    }

    private String generateArmorTrimsAtlas()
    {
        JsonObject permutations = new JsonObject();
        for (infrared.fortuna.materials.Material mat : Fortuna.initializedMaterials)
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

    private String generateItemsAtlas()
    {
        JsonObject permutations = new JsonObject();
        for (infrared.fortuna.materials.Material mat : Fortuna.initializedMaterials)
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