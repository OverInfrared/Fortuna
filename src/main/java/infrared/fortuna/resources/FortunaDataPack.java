package infrared.fortuna.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.resources.enums.MiningLevel;
import infrared.fortuna.resources.materials.Material;
import net.fabricmc.fabric.api.resource.v1.pack.ModPackResources;
import net.fabricmc.fabric.impl.resource.pack.ModResourcePackCreator;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
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

    private static final List<Material> loadedMaterials = new ArrayList<>();

    private final PackLocationInfo locationInfo = new PackLocationInfo(
            PACK_ID,
            Component.literal("Fortuna Dynamic Data"),
            ModResourcePackCreator.RESOURCE_PACK_SOURCE,
            Optional.of(new KnownPack(ModResourcePackCreator.VANILLA, PACK_ID, "1.0.0"))
    );

    private FortunaDataPack() {}

    public static void initializeMaterial(Material material) {
        loadedMaterials.add(material);
    }

    @Override
    public PackLocationInfo location() {
        return locationInfo;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... paths) {
        if (paths.length == 1 && paths[0].equals("pack.mcmeta")) {
            JsonObject pack = new JsonObject();
            pack.addProperty("pack_format", 94);
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
        return packType == PackType.SERVER_DATA ? Set.of("minecraft") : Set.of();
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NonNull PackType packType, @NonNull Identifier id) {
        if (packType != PackType.SERVER_DATA) return null;
        if (!id.getNamespace().equals("minecraft")) return null;

        String json = resolveResource(id.getPath());
        if (json == null) return null;

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(@NonNull PackType packType, @NonNull String namespace,
                              @NonNull String prefix, @NonNull ResourceOutput resourceOutput) {
        if (packType != PackType.SERVER_DATA) return;
        if (!namespace.equals("minecraft")) return; // only emit for minecraft namespace

        emitTag(resourceOutput, prefix, "tags/block/mineable/pickaxe.json");
        emitTag(resourceOutput, prefix, "tags/block/mineable/axe.json");
        emitTag(resourceOutput, prefix, "tags/block/mineable/shovel.json");
        emitTag(resourceOutput, prefix, "tags/block/needs_stone_tool.json");
        emitTag(resourceOutput, prefix, "tags/block/needs_iron_tool.json");
        emitTag(resourceOutput, prefix, "tags/block/needs_diamond_tool.json");
    }

    @Override
    public void close() {}

    @Override
    public ModMetadata getFabricModMetadata() {
        return FabricLoader.getInstance()
                .getModContainer(Fortuna.MOD_ID)
                .orElseThrow()
                .getMetadata();
    }

    @Override
    public ModPackResources createOverlay(String overlay) {
        return this;
    }

    private void emitTag(ResourceOutput resourceOutput, String prefix, String path) {
        if (path.startsWith(prefix)) {
            Identifier id = Identifier.fromNamespaceAndPath("minecraft", path);
            resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.SERVER_DATA, id)).get());
        }
    }

    private @Nullable String resolveResource(String path) {
        return switch (path) {
            case "tags/block/mineable/pickaxe.json"   -> generateToolTag(BlockTags.MINEABLE_WITH_PICKAXE);
            case "tags/block/mineable/axe.json"       -> generateToolTag(BlockTags.MINEABLE_WITH_AXE);
            case "tags/block/mineable/shovel.json"    -> generateToolTag(BlockTags.MINEABLE_WITH_SHOVEL);
            case "tags/block/needs_stone_tool.json"   -> generateMiningLevelTag(MiningLevel.Iron);
            case "tags/block/needs_iron_tool.json"    -> generateMiningLevelTag(MiningLevel.Diamond);
            case "tags/block/needs_diamond_tool.json" -> generateMiningLevelTag(MiningLevel.Netherite);
            default -> null;
        };
    }

    private String generateToolTag(TagKey<Block> toolTag) {
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

    private String generateMiningLevelTag(MiningLevel requiredLevel) {
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
}