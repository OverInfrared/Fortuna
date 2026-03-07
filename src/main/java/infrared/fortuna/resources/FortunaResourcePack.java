package infrared.fortuna.resources;

import infrared.fortuna.Fortuna;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
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
    public void loadPacks(Consumer<Pack> consumer) {
        Pack pack = Pack.readMetaAndCreate(
                new PackLocationInfo(
                        PACK_ID,
                        Component.literal("Fortuna Dynamic Resources"),
                        PackSource.BUILT_IN,
                        Optional.empty()
                ),
                _ -> INSTANCE,
                PackType.CLIENT_RESOURCES,
                Pack.Position.TOP
        );
        if (pack != null) consumer.accept(pack);
    }

    // ── PackResources ─────────────────────────────────────────────────────────

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, Identifier id) {
        if (type != PackType.CLIENT_RESOURCES) return null;
        if (!id.getNamespace().equals(Fortuna.MOD_ID)) return null;

        String json = generateJson(id.getPath());
        if (json == null) return null;

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(PackType type, ResourceLocation id) {
        if (type != PackType.CLIENT_RESOURCES) return null;
        if (!id.getNamespace().equals(Fortuna.MOD_ID)) return null;

        String json = generateJson(id.getPath());
        if (json == null) return null;

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(PackType type, String namespace, String prefix,
                              ResourceOutput output) {
        if (type != PackType.CLIENT_RESOURCES) return;
        if (!namespace.equals(Fortuna.MOD_ID)) return;

        for (Material mat : materials) {
            String name = mat.getName();

            emit(output, "blockstates/" + name + "_ore.json");
            emit(output, "models/block/" + name + "_ore.json");
            emit(output, "models/item/" + name + "_ore.json");

            switch (mat.getMaterialRaw()) {
                case Ingot -> {
                    emit(output, "models/item/raw_" + name + ".json");
                    emit(output, "models/item/" + name + "_ingot.json");
                }
                case Gem, Special -> emit(output, "models/item/" + name + ".json");
            }
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        return type == PackType.CLIENT_RESOURCES
                ? Set.of(Fortuna.MOD_ID)
                : Set.of();
    }

    @Override
    public void close() {}

    // ── JSON generation ───────────────────────────────────────────────────────

    private @Nullable String generateJson(String path) {
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

    private void emit(ResourceOutput output, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Fortuna.MOD_ID, path);
        output.accept(id, () -> getResource(PackType.CLIENT_RESOURCES, id).get());
    }
}