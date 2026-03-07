package infrared.fortuna.resources;

import infrared.fortuna.Fortuna;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

public class FortunaResourcePack extends AbstractPackResources implements RepositorySource
{
    private static final String PACK_ID = "fortuna_dynamic";
    private static final List<Material> materials = new ArrayList<>();
    private static final FortunaResourcePack INSTANCE = new FortunaResourcePack();

    public static FortunaResourcePack getInstance() { return INSTANCE; }
    public static void addMaterial(Material material) { materials.add(material); }

    private FortunaResourcePack() {
        super(new PackLocationInfo(
                PACK_ID,
                Component.literal("Fortuna Dynamic Resources"),
                PackSource.BUILT_IN,
                Optional.empty()
        ));
    }

    // ── RepositorySource ──────────────────────────────────────────────────────

    @Override
    public void loadPacks(@NonNull Consumer<Pack> consumer)
    {
        Pack pack = Pack.readMetaAndCreate(
                new PackLocationInfo(
                        PACK_ID,
                        Component.literal("Fortuna Dynamic Resources"),
                        PackSource.BUILT_IN,
                        Optional.empty()
                ),
                new Pack.ResourcesSupplier() {
                    @Override
                    public @NonNull PackResources openPrimary(@NonNull PackLocationInfo info) {
                        return INSTANCE;
                    }

                    @Override
                    public @NonNull PackResources openFull(@NonNull PackLocationInfo info, Pack.@NonNull Metadata metadata) {
                        return INSTANCE;
                    }
                },
                PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(true, Pack.Position.TOP, false)
        );
        if (pack != null) consumer.accept(pack);
    }

    // ── PackResources ─────────────────────────────────────────────────────────

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... paths)
    {
        if (paths.length == 1 && paths[0].equals("pack.mcmeta")) {
            String mcmeta = """
                    {"pack":{"pack_format":34,"description":"Fortuna dynamic resources"}}
                    """;
            return () -> new ByteArrayInputStream(mcmeta.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NonNull PackType packType, @NonNull Identifier id)
    {
        if (packType != PackType.CLIENT_RESOURCES) return null;
        if (!id.getNamespace().equals(Fortuna.MOD_ID)) return null;

        String json = generateJson(id.getPath());
        if (json == null) return null;

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(@NonNull PackType packType, @NonNull String namespace, @NonNull String prefix,
                              @NonNull ResourceOutput resourceOutput)
    {
        if (packType != PackType.CLIENT_RESOURCES) return;
        if (!namespace.equals(Fortuna.MOD_ID)) return;

        for (Material mat : materials) {
            String name = mat.getName();

            emitIfMatch(resourceOutput, prefix, "blockstates/" + name + "_ore.json");
            emitIfMatch(resourceOutput, prefix, "models/block/" + name + "_ore.json");
            emitIfMatch(resourceOutput, prefix, "models/item/" + name + "_ore.json");

            switch (mat.getMaterialRaw()) {
                case Ingot -> {
                    emitIfMatch(resourceOutput, prefix, "models/item/raw_" + name + ".json");
                    emitIfMatch(resourceOutput, prefix, "models/item/" + name + "_ingot.json");
                }
                case Gem, Special -> emitIfMatch(resourceOutput, prefix, "models/item/" + name + ".json");
            }
        }
    }

    @Override
    public @NonNull Set<String> getNamespaces(@NonNull PackType packType)
    {
        return packType == PackType.CLIENT_RESOURCES
                ? Set.of(Fortuna.MOD_ID)
                : Set.of();
    }

    @Override
    public void close() {}

    // ── JSON generation ───────────────────────────────────────────────────────

    private @Nullable String generateJson(String path)
    {
        for (Material mat : materials) {
            String name = mat.getName();

            if (path.equals("blockstates/" + name + "_ore.json"))
                return blockstateJson(name);
            if (path.equals("models/block/" + name + "_ore.json"))
                return blockModelJson();
            if (path.equals("models/item/" + name + "_ore.json"))
                return oreItemModelJson(name);

            switch (mat.getMaterialRaw()) {
                case Ingot -> {
                    if (path.equals("models/item/raw_" + name + ".json"))
                        return rawItemModelJson();
                    if (path.equals("models/item/" + name + "_ingot.json"))
                        return ingotItemModelJson();
                }
                case Gem, Special -> {
                    if (path.equals("models/item/" + name + ".json"))
                        return gemItemModelJson();
                }
            }
        }
        return null;
    }

    private String blockstateJson(String name) {
        return """
                {"variants":{"":{"model":"%s:block/%s_ore"}}}
                """.formatted(Fortuna.MOD_ID, name);
    }

    private String blockModelJson() {
        return """
                {
                  "parent": "minecraft:block/cube_all",
                  "textures": {
                    "layer0": "%s:block/ore_base",
                    "layer1": "%s:block/ore_overlay",
                    "all":    "%s:block/ore_base"
                  }
                }
                """.formatted(Fortuna.MOD_ID, Fortuna.MOD_ID, Fortuna.MOD_ID);
    }

    private String oreItemModelJson(String name) {
        return """
                {"parent":"%s:block/%s_ore"}
                """.formatted(Fortuna.MOD_ID, name);
    }

    private String layeredItemModelJson(String overlay) {
        return """
                {
                  "parent": "minecraft:item/generated",
                  "textures": {
                    "layer0": "%s:item/item_base",
                    "layer1": "%s:item/%s"
                  }
                }
                """.formatted(Fortuna.MOD_ID, Fortuna.MOD_ID, overlay);
    }

    private String rawItemModelJson()   { return layeredItemModelJson("raw_overlay"); }
    private String ingotItemModelJson() { return layeredItemModelJson("ingot_overlay"); }
    private String gemItemModelJson()   { return layeredItemModelJson("gem_overlay"); }

    private void emitIfMatch(ResourceOutput resourceOutput, String prefix, String path) {
        if (path.startsWith(prefix)) {
            Identifier id = Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, path);
            resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.CLIENT_RESOURCES, id)).get());
        }
    }
}