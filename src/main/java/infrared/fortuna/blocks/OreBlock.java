package infrared.fortuna.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;

public class OreBlock extends FortunaBlock
{
    private final OreMaterial oreMaterial;

    public OreBlock(FortunaProperties<Block> fortunaProperties, Properties properties, OreMaterial oreMaterial) {
        super(fortunaProperties, properties);
        this.oreMaterial = oreMaterial;
    }

    @Override
    protected JsonObject generateBlockState()
    {
        JsonObject empty = new JsonObject();
        empty.addProperty("model", "%s:block/%s".formatted(Fortuna.MOD_ID, fortunaProperties.registryName()));

        JsonObject variants = new JsonObject();
        variants.add("", empty);

        JsonObject blockstate = new JsonObject();
        blockstate.add("variants", variants);

        return blockstate;
    }

    @Override
    protected JsonObject generateModel() {
        String base = oreMaterial.getMaterialOreBase().name().toLowerCase();

        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "%s:block/bases/%s".formatted(Fortuna.MOD_ID, base));
        textures.addProperty("top", "%s:block/bases/%s".formatted(Fortuna.MOD_ID, base));
        textures.addProperty("bottom", "%s:block/bases/%s".formatted(Fortuna.MOD_ID, base));
        textures.addProperty("side", "%s:block/bases/%s".formatted(Fortuna.MOD_ID, base));

        List<String> overlayLayers = new ArrayList<>();
        switch (oreMaterial.getMaterialOreOverlay()) {
            case Coal     -> overlayLayers.add("%s:block/overlay/ore_coal".formatted(Fortuna.MOD_ID));
            case Iron     -> overlayLayers.add("%s:block/overlay/ore_iron".formatted(Fortuna.MOD_ID));
            case Diamond  -> overlayLayers.add("%s:block/overlay/ore_diamond".formatted(Fortuna.MOD_ID));
            case Emerald  -> overlayLayers.add("%s:block/overlay/ore_emerald".formatted(Fortuna.MOD_ID));
            case Gold     -> overlayLayers.add("%s:block/overlay/ore_gold".formatted(Fortuna.MOD_ID));
            case Lapis    -> overlayLayers.add("%s:block/overlay/ore_lapis".formatted(Fortuna.MOD_ID));
            case Redstone -> overlayLayers.add("%s:block/overlay/ore_redstone".formatted(Fortuna.MOD_ID));
            case Copper   -> {
                overlayLayers.add("%s:block/overlay/ore_copper_base".formatted(Fortuna.MOD_ID));
                overlayLayers.add("%s:block/overlay/ore_copper_oxidized".formatted(Fortuna.MOD_ID));
                overlayLayers.add("%s:block/overlay/ore_copper_transition".formatted(Fortuna.MOD_ID));
            }
        }

        textures.addProperty("borderbottom", "%s:block/overlay/%s".formatted(Fortuna.MOD_ID, switch (oreMaterial.getMaterialOreOverlay())
            {
                case Iron -> "ore_iron_border_bottom";
                case Copper -> "ore_copper_border_bottom";
                case Coal -> "ore_coal_border_bottom";
                case Diamond -> "ore_diamond_border_bottom";
                case Emerald -> "ore_emerald_border_bottom";
                case Gold -> "ore_gold_border_bottom";
                case Lapis -> "ore_lapis_border_bottom";
                case Redstone -> "ore_redstone_border_bottom";
            }));

        textures.addProperty("bordertop", "%s:block/overlay/%s".formatted(Fortuna.MOD_ID, switch (oreMaterial.getMaterialOreOverlay())
        {
            case Iron -> "ore_iron_border_top";
            case Copper -> "ore_copper_border_top";
            case Coal -> "ore_coal_border_top";
            case Diamond -> "ore_diamond_border_top";
            case Emerald -> "ore_emerald_border_top";
            case Gold -> "ore_gold_border_top";
            case Lapis -> "ore_lapis_border_top";
            case Redstone -> "ore_redstone_border_top";
        }));

        for (int i = 0; i < overlayLayers.size(); i++)
            textures.addProperty("overlay" + i, overlayLayers.get(i));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "block/block");
        model.add("textures", textures);

        JsonArray elements = new JsonArray();
        elements.add(buildBaseCube());
        elements.add(buildOverlayCube("bordertop", 0));
        elements.add(buildOverlayCube("borderbottom", 1));

        for (int i = 0; i < overlayLayers.size(); i++)
            elements.add(buildOverlayCube("overlay" + i, i + 2));

        model.add("elements", elements);
        return model;
    }

    @Override
    protected JsonObject generateItemModel()
    {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", "%s:block/%s".formatted(Fortuna.MOD_ID, fortunaProperties.registryName()));

        JsonArray tints = new JsonArray();
        tints.add(buildTint(oreMaterial.getBorderColor()));
        tints.add(buildTint(oreMaterial.getBottomBorderColor()));
        tints.add(buildTint(oreMaterial.getColor()));
        tints.add(buildTint(oreMaterial.getSecondaryColor()));
        tints.add(buildTint(oreMaterial.getTertiaryColor()));

        model.add("tints", tints);

        JsonObject itemModel = new JsonObject();
        itemModel.add("model", model);
        return itemModel;
    }

    private JsonObject buildTint(int color)
    {
        JsonObject border = new JsonObject();
        border.addProperty("type", "minecraft:constant");
        border.addProperty("value", color);
        return border;
    }

    private JsonObject buildBaseCube() {
        JsonObject faceObj = new JsonObject();

        faceObj.add("down", buildFaceJson("down", "bottom"));
        faceObj.add("up", buildFaceJson("up", "top"));
        faceObj.add("north", buildFaceJson("north", "side"));
        faceObj.add("south", buildFaceJson("south", "side"));
        faceObj.add("east", buildFaceJson("east", "side"));
        faceObj.add("west", buildFaceJson("west", "side"));

        return buildElement(faceObj);
    }

    private JsonObject buildFaceJson(String direction, String texture)
    {
        JsonObject face = new JsonObject();
        face.add("uv", buildUVArray());
        face.addProperty("texture", "#" + texture);
        face.addProperty("cullface", direction);
        return face;
    }

    private JsonObject buildOverlayCube(String textureRef, int tintIndex) {
        // Grass only overlays the sides, but for ore we want all faces
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

    private JsonObject buildElement(JsonObject faces) {
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

    private JsonArray buildUVArray() {
        JsonArray uv = new JsonArray();
        uv.add(0); uv.add(0); uv.add(16); uv.add(16);
        return uv;
    }
}
