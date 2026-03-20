package infrared.fortuna.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.*;
import infrared.fortuna.blocks.ore.BarsBlock;
import infrared.fortuna.blocks.ore.IBarsBlock;
import infrared.fortuna.items.*;
import infrared.fortuna.equipment.IFortunaEquipment;
import infrared.fortuna.materials.Material;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.util.PaletteGenerator;
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

import static infrared.fortuna.Fortuna.getMaterial;

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

        DynamicResourceRegistry reg = DynamicResourceRegistry.client();
        String ns = id.getNamespace();
        String path = id.getPath();

        // Binary resources (PNGs)
        byte[] binary = reg.resolveBinary(ns, path);
        if (binary != null)
            return () -> new ByteArrayInputStream(binary);

        // JSON resources
        String json = reg.resolve(ns, path);
        if (json != null)
        {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            return () -> new ByteArrayInputStream(bytes);
        }

        return null;
    }

    @Override
    public void listResources(@NonNull PackType packType, @NonNull String namespace, @NonNull String prefix,
                              @NonNull ResourceOutput resourceOutput)
    {
        if (packType != PackType.CLIENT_RESOURCES) return;

        DynamicResourceRegistry reg = DynamicResourceRegistry.client();
        if (!reg.getNamespaces().contains(namespace)) return;

        for (String path : reg.listPaths(namespace, prefix))
        {
            Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
            resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.CLIENT_RESOURCES, id)).get());
        }
    }

    @Override
    public void close() {}
}