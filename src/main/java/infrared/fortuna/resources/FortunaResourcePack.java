package infrared.fortuna.resources;

import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.blocks.ModBlocks;
import infrared.fortuna.items.FortunaBlockItem;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.resources.materials.Material;
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

public class FortunaResourcePack extends AbstractPackResources implements RepositorySource
{
    private static final String PACK_ID = "fortuna_dynamic";
    private static final FortunaResourcePack INSTANCE = new FortunaResourcePack();

    public static FortunaResourcePack getInstance() { return INSTANCE; }

    private static final List<Material> loadedMaterials = new ArrayList<>();

    private FortunaResourcePack() {
        super(new PackLocationInfo(
                PACK_ID,
                Component.literal("Fortuna Dynamic Resources"),
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
        if (packType != PackType.CLIENT_RESOURCES) return;

        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks())
        {
            emitIfMatch(resourceOutput, prefix, "blockstates/" + block.getRegistryName() + ".json");
            emitIfMatch(resourceOutput, prefix, "models/block/" + block.getRegistryName() + ".json");
            emitIfMatch(resourceOutput, prefix, "items/" + block.getRegistryName() + ".json");
        }

        for (Item item : ModItems.getRegisteredItem())
        {
            if (!(item instanceof FortunaItem fortunaItem))
                continue;

            emitIfMatch(resourceOutput, prefix, "models/item/" + fortunaItem.getRegistryName() + ".json");
            emitIfMatch(resourceOutput, prefix, "items/" + fortunaItem.getRegistryName() + ".json");
        }
    }

    @Override
    public @NonNull Set<String> getNamespaces(@NonNull PackType packType)
    {
        return packType == PackType.CLIENT_RESOURCES ? Set.of(Fortuna.MOD_ID) : Set.of();
    }

    @Override
    public void close() {}

    private void emitIfMatch(ResourceOutput resourceOutput, String prefix, String path) {
        if (path.startsWith(prefix)) {
            Identifier id = Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, path);
            resourceOutput.accept(id, () -> Objects.requireNonNull(getResource(PackType.CLIENT_RESOURCES, id)).get());
        }
    }

    private @Nullable String resolveResource(String path)
    {
        if (path.startsWith("blockstates/") && path.endsWith(".json"))
        {
            String registryName = path.substring("blockstates/".length(), path.length() - ".json".length());
            IFortunaBlock block = findBlock(registryName);
            return block != null ? block.getBlockStateString() : null;
        }

        if (path.startsWith("models/block/") && path.endsWith(".json"))
        {
            String registryName = path.substring("models/block/".length(), path.length() - ".json".length());
            IFortunaBlock block = findBlock(registryName);
            return block != null ? block.getModelString() : null;
        }

        if (path.startsWith("models/item/") && path.endsWith(".json"))
        {
            String registryName = path.substring("models/item/".length(), path.length() - ".json".length());
            FortunaItem item = findItem(registryName);
            return item != null ? item.getModelString() : null;
        }

        if (path.startsWith("items/") && path.endsWith(".json"))
        {
            String registryName = path.substring("items/".length(), path.length() - ".json".length());
            IFortunaBlock block = findBlock(registryName);
            if (block != null) return block.getItemString();
            FortunaItem item = findItem(registryName);
            return item != null ? item.getItemString() : null;
        }

        return null;
    }

    private @Nullable IFortunaBlock findBlock(String registryName)
    {
        for (IFortunaBlock block : ModBlocks.getRegisteredBlocks())
            if (block.getRegistryName().equals(registryName))
                return block;
        return null;
    }

    private @Nullable FortunaItem findItem(String registryName)
    {
        for (Item item : ModItems.getRegisteredItem())
            if (item instanceof FortunaItem fortunaItem && fortunaItem.getRegistryName().equals(registryName))
                return fortunaItem;
        return null;
    }
}