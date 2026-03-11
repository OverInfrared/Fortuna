package infrared.fortuna.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.resources.enums.MiningLevel;
import infrared.fortuna.resources.materials.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class FortunaDataPack extends AbstractPackResources implements RepositorySource
{
    private static final String PACK_ID = "fortuna_dynamic_data";
    private static final FortunaDataPack INSTANCE = new FortunaDataPack();

    public static FortunaDataPack getInstance() { return INSTANCE; }

    private static final List<Material> loadedMaterials = new ArrayList<>();

    private FortunaDataPack() {
        super(new PackLocationInfo(
                PACK_ID,
                Component.literal("Fortuna Dynamic Data"),
                PackSource.BUILT_IN,
                Optional.empty()
        ));
    }

    public static void initializeMaterial(Material material)
    {
        loadedMaterials.add(material);
    }

    @Override
    public void loadPacks(@NonNull Consumer<Pack> consumer)
    {
        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
            @Override
            public @NonNull PackResources openPrimary(@NonNull PackLocationInfo info) { return INSTANCE; }
            @Override
            public @NonNull PackResources openFull(@NonNull PackLocationInfo info, Pack.@NonNull Metadata metadata) { return INSTANCE; }
        };

        Pack pack = Pack.readMetaAndCreate(
                new PackLocationInfo(PACK_ID, Component.literal("Fortuna Dynamic Data"), PackSource.BUILT_IN, Optional.empty()),
                supplier,
                PackType.SERVER_DATA,
                new PackSelectionConfig(true, Pack.Position.TOP, false)
        );
        if (pack != null) consumer.accept(pack);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... paths)
    {
        if (paths.length != 1 || !paths[0].equals("pack.mcmeta")) return null;

        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", 94);
        pack.addProperty("min_format", 94);
        pack.addProperty("max_format", 95);
        pack.addProperty("description", "Fortuna dynamic data");

        JsonObject mcmeta = new JsonObject();
        mcmeta.add("pack", pack);

        return () -> new ByteArrayInputStream(mcmeta.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NonNull PackType packType, @NonNull Identifier id)
    {
        if (packType != PackType.SERVER_DATA) return null;
        if (!id.getNamespace().equals(Fortuna.MOD_ID)) return null;

        String json = resolveResource(id.getPath());
        if (json == null) return null;

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(@NonNull PackType packType, @NonNull String namespace, @NonNull String prefix,
                              @NonNull ResourceOutput resourceOutput)
    {
        if (packType != PackType.SERVER_DATA) return;

        emitTag(resourceOutput, prefix, "tags/block/mineable/pickaxe.json");
        emitTag(resourceOutput, prefix, "tags/block/mineable/axe.json");
        emitTag(resourceOutput, prefix, "tags/block/mineable/shovel.json");
        emitTag(resourceOutput, prefix, "tags/block/needs_stone_tool.json");
        emitTag(resourceOutput, prefix, "tags/block/needs_iron_tool.json");
        emitTag(resourceOutput, prefix, "tags/block/needs_diamond_tool.json");
    }

    @Override
    public @NonNull Set<String> getNamespaces(@NonNull PackType packType)
    {
        return packType == PackType.SERVER_DATA ? Set.of(Fortuna.MOD_ID) : Set.of();
    }

    @Override
    public void close() {}

    private void emitIfMatch(ResourceOutput resourceOutput, String prefix, String path) {
        if (path.startsWith(prefix)) {
            Identifier id = Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, path);
            resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.SERVER_DATA, id)).get());
        }
    }

    private @Nullable String resolveResource(String path)
    {
        return switch (path) {
            case "tags/block/mineable/pickaxe.json" -> {
                Fortuna.LOGGER.info("(fortuna) pickaxe tag JSON: {}",
                        generateToolTag(BlockTags.MINEABLE_WITH_PICKAXE));
                yield generateToolTag(BlockTags.MINEABLE_WITH_PICKAXE);
            }
            case "tags/block/mineable/axe.json"     -> generateToolTag(BlockTags.MINEABLE_WITH_AXE);
            case "tags/block/mineable/shovel.json"  -> generateToolTag(BlockTags.MINEABLE_WITH_SHOVEL);
            case "tags/block/needs_stone_tool.json"   -> generateMiningLevelTag(MiningLevel.Stone);
            case "tags/block/needs_iron_tool.json"    -> generateMiningLevelTag(MiningLevel.Iron);
            case "tags/block/needs_diamond_tool.json" -> generateMiningLevelTag(MiningLevel.Diamond);
            default -> null;
        };
    }

    private String generateToolTag(TagKey<Block> toolTag)
    {
        JsonArray values = new JsonArray();
        for (Material material : loadedMaterials)
            for (IFortunaBlock block : material.getBlocks())
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
        for (Material material : loadedMaterials)
            for (IFortunaBlock block : material.getBlocks())
                if (block.getMiningLevel().ordinal() == requiredLevel.ordinal())
                    values.add("%s:%s".formatted(Fortuna.MOD_ID, block.getRegistryName()));

        JsonObject tag = new JsonObject();
        tag.addProperty("replace", false);
        tag.add("values", values);
        return tag.toString();
    }

    private void emitTag(ResourceOutput resourceOutput, String prefix, String path) {
        if (path.startsWith(prefix)) {
            Identifier id = Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, path);
            resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.SERVER_DATA, id)).get());
        }
    }

    private @Nullable IFortunaBlock findBlock(String registryName)
    {
        for (Material material : loadedMaterials)
            for (IFortunaBlock block : material.getBlocks())
                if (block.getRegistryName().equals(registryName))
                    return block;
        return null;
    }
}