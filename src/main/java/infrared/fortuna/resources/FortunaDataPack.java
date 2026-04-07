package infrared.fortuna.resources;

import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
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
    public @Nullable IoSupplier<InputStream> getResource(@NonNull PackType packType, @NonNull Identifier id)
    {
        if (packType != PackType.SERVER_DATA) return null;

        String json = DynamicResourceRegistry.server().resolve(id.getNamespace(), id.getPath());
        if (json == null) return null;

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        return () -> new ByteArrayInputStream(bytes);
    }

    @Override
    public void listResources(@NonNull PackType packType, @NonNull String namespace,
                              @NonNull String prefix, @NonNull ResourceOutput resourceOutput)
    {
        if (packType != PackType.SERVER_DATA) return;

        DynamicResourceRegistry reg = DynamicResourceRegistry.server();
        if (!reg.getNamespaces().contains(namespace)) return;

        for (String path : reg.listPaths(namespace, prefix))
        {
            Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
            resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.SERVER_DATA, id)).get());
        }
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


    public static void setRegistryLookup(HolderLookup.Provider lookup)
    {
        registryLookup = lookup;
    }

    public static HolderLookup.Provider getRegistryLookup()
    {
        return registryLookup;
    }
}