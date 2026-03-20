package infrared.fortuna.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.mojang.serialization.JsonOps;
import infrared.fortuna.Fortuna;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;

public interface IFortunaBlock
{
    enum ElementType {
        BASE,
        OVERLAY,
    }

    record RequiredElement(String textureKey, ElementType type, int tintIndex) {}

    // Abstract getters — implementing class provides these from its fields
    List<Pair<String, String>> getRequiredTextures();
    List<String> getRequiredItemTextures();
    List<RequiredElement> getRequiredElements();
    List<Integer> getRequiredTints();
    DynamicProperties<Block, OreMaterial> getDynamicProperties();

    MiningLevel getMiningLevel();

    default Component getDisplayName() {
        return getDynamicProperties().displayName();
    }

    default String getRegistryName() {
        return getDynamicProperties().registryName();
    }

    default ResourceKey<Block> getResourceKey() {
        return getDynamicProperties().resourceKey();
    }

    default Integer getRegisteredTint(int index) {
        return getRequiredTints().get(index);
    }

    default TagKey<Block> getRequiredTool() { return BlockTags.MINEABLE_WITH_PICKAXE; }

    default void addRequiredTexture(String label, String texture)
    {
        getRequiredTextures().add(Pair.of(label, texture));
    }

    default void addRequiredItemTexture(String texture)
    {
        getRequiredItemTextures().add(texture);
    }

    default void addBaseTextures(String texture)
    {
        addRequiredTexture("particle", texture);
        addRequiredTexture("top", texture);
        addRequiredTexture("bottom", texture);
        addRequiredTexture("side", texture);
        getRequiredElements().add(new RequiredElement("side", ElementType.BASE, -1));
    }

    default void addOverlayTexture(String key, String texture, int tintIndex)
    {
        addRequiredTexture(key, texture);
        getRequiredElements().add(new RequiredElement(key, ElementType.OVERLAY, tintIndex));
    }

    default String getItemModelString()
    {
        JsonObject textures = new JsonObject();
        List<String> itemTextures = getRequiredItemTextures();
        for (int i = 0; i < itemTextures.size(); i++)
            textures.addProperty("layer" + i, "%s:item/%s".formatted(Fortuna.MOD_ID, itemTextures.get(i)));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        model.add("textures", textures);

        return model.toString();
    }

    default void addRequiredTint(int color) {
        getRequiredTints().add(color);
    }

    default String getBlockStateString() {
        return generateBlockState().toString();
    }

    default String getModelString()
    {
        return getModelString("");
    }

    default String getModelString(String suffix)
    {
        return generateModel(suffix).toString();
    }

    default String getItemString() {
        return generateItemModel().toString();
    }

    default JsonObject generateBlockState()
    {
        JsonObject empty = new JsonObject();
        empty.addProperty("model", "%s:block/%s".formatted(Fortuna.MOD_ID, getRegistryName()));

        JsonObject variants = new JsonObject();
        variants.add("", empty);

        JsonObject blockstate = new JsonObject();
        blockstate.add("variants", variants);
        return blockstate;
    }

    default JsonObject generateModel(String suffix)
    {
        JsonObject textures = new JsonObject();
        for (Pair<String, String> texture : getRequiredTextures())
            textures.addProperty(texture.getLeft(), "%s:block/%s".formatted(Fortuna.MOD_ID, texture.getRight()));

        JsonArray elements = new JsonArray();
        for (RequiredElement element : getRequiredElements()) {
            elements.add(switch (element.type()) {
                case BASE    -> buildBaseCube();
                case OVERLAY -> buildOverlayCube(element.textureKey(), element.tintIndex());
            });
        }

        JsonObject model = new JsonObject();
        model.addProperty("parent", "block/block");
        model.add("textures", textures);
        model.add("elements", elements);
        return model;
    }

    default JsonObject generateItemModel()
    {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", "%s:block/%s".formatted(Fortuna.MOD_ID, getRegistryName()));

        JsonArray tints = new JsonArray();
        for (int tint : getRequiredTints())
            tints.add(buildTint(tint));
        model.add("tints", tints);

        JsonObject itemModel = new JsonObject();
        itemModel.add("model", model);
        return itemModel;
    }

    default JsonObject buildBaseCube()
    {
        JsonObject faceObj = new JsonObject();
        faceObj.add("down",  buildFaceJson("down",  "bottom"));
        faceObj.add("up",    buildFaceJson("up",    "top"));
        faceObj.add("north", buildFaceJson("north", "side"));
        faceObj.add("south", buildFaceJson("south", "side"));
        faceObj.add("east",  buildFaceJson("east",  "side"));
        faceObj.add("west",  buildFaceJson("west",  "side"));
        return buildElement(faceObj);
    }

    default JsonObject buildFaceJson(String direction, String texture)
    {
        JsonObject face = new JsonObject();
        face.add("uv", buildUVArray());
        face.addProperty("texture", "#" + texture);
        face.addProperty("cullface", direction);
        return face;
    }

    default JsonObject buildOverlayCube(String textureRef, int tintIndex)
    {
        String[] faces = {"down", "up", "north", "south", "west", "east"};
        JsonObject faceObj = new JsonObject();
        for (String face : faces) {
            JsonObject f = new JsonObject();
            f.add("uv", buildUVArray());
            f.addProperty("texture", "#" + textureRef);
            if (tintIndex != -1)
                f.addProperty("tintindex", tintIndex);
            f.addProperty("cullface", face);
            faceObj.add(face, f);
        }
        return buildElement(faceObj);
    }

    default JsonObject buildElement(JsonObject faces)
    {
        JsonArray from = new JsonArray();
        from.add(0); from.add(0); from.add(0);
        JsonArray to = new JsonArray();
        to.add(16); to.add(16); to.add(16);

        JsonObject element = new JsonObject();
        element.add("from", from);
        element.add("to", to);
        element.add("faces", faces);
        return element;
    }

    default JsonArray buildUVArray()
    {
        JsonArray uv = new JsonArray();
        uv.add(0); uv.add(0); uv.add(16); uv.add(16);
        return uv;
    }

    default JsonObject buildTint(int color)
    {
        JsonObject tint = new JsonObject();
        tint.addProperty("type", "minecraft:constant");
        tint.addProperty("value", color);
        return tint;
    }

    default JsonObject getLoot(HolderLookup.Provider registries)
    {
        Item selfItem = BuiltInRegistries.ITEM
                .get(Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, getDynamicProperties().registryName()))
                .orElseThrow()
                .value();

        LootTable table = new FortunaBlockLootProvider(registries)
                .createSingleItemTable(selfItem)
                .build();

        return LootTable.DIRECT_CODEC
                .encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), table)
                .getOrThrow()
                .getAsJsonObject();
    }
}