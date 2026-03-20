package infrared.fortuna.blocks.ore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;

import java.util.Arrays;
import java.util.List;

public interface IDoorBlock extends IFortunaBlock
{
    String[] DOOR_MODELS = {
            "bottom_left", "bottom_left_open",
            "bottom_right", "bottom_right_open",
            "top_left", "top_left_open",
            "top_right", "top_right_open"
    };

    int[][] DOOR_ROTATIONS = {
            {0, 90, 180, 270},     // bottom_left
            {90, 180, 270, 0},     // bottom_left_open
            {0, 90, 180, 270},     // bottom_right
            {270, 0, 90, 180},     // bottom_right_open
            {0, 90, 180, 270},     // top_left
            {90, 180, 270, 0},     // top_left_open
            {0, 90, 180, 270},     // top_right
            {270, 0, 90, 180},     // top_right_open
    };

    default String getBlockStateString()
    {
        String name = getDynamicProperties().registryName();
        String[] facings = {"east", "south", "west", "north"};
        String[] halves = {"lower", "upper"};
        String[] hinges = {"left", "right"};
        boolean[] opens = {false, true};

        JsonObject variants = new JsonObject();

        for (int f = 0; f < facings.length; f++)
        {
            for (String half : halves)
            {
                for (String hinge : hinges)
                {
                    for (boolean open : opens)
                    {
                        String modelSuffix = half.equals("lower") ? "bottom" : "top";
                        modelSuffix += "_" + hinge;
                        if (open) modelSuffix += "_open";

                        int modelIndex = Arrays.asList(DOOR_MODELS).indexOf(modelSuffix);

                        String key = "facing=%s,half=%s,hinge=%s,open=%s".formatted(
                                facings[f], half, hinge, String.valueOf(open));

                        JsonObject variant = new JsonObject();
                        variant.addProperty("model", "%s:block/%s_%s".formatted(Fortuna.MOD_ID, name, modelSuffix));

                        int rotation = DOOR_ROTATIONS[modelIndex][f];
                        if (rotation != 0)
                            variant.addProperty("y", rotation);

                        variants.add(key, variant);
                    }
                }
            }
        }

        JsonObject blockstate = new JsonObject();
        blockstate.add("variants", variants);
        return blockstate.toString();
    }

    default JsonObject generateModel(String suffix)
    {
        JsonObject textures = new JsonObject();

        // Build textures from required textures list
        for (var texture : getRequiredTextures())
        {
            textures.addProperty(
                    texture.getKey(),
                    "%s:block/%s".formatted(Fortuna.MOD_ID, texture.getValue())
            );
        }

        // Determine which parent to use based on suffix being top or bottom
        String parentSuffix = suffix.startsWith("top") ? "top" : "bottom";

        // Build elements with tint support for each layer
        JsonArray elements = new JsonArray();
        for (RequiredElement element : getRequiredElements())
        {
            // Only use the element matching this half (top elements for top models, bottom for bottom)
            if (element.textureKey().contains("top") && !suffix.startsWith("top"))
                continue;
            if (element.textureKey().contains("bottom") && suffix.startsWith("top"))
                continue;

            String textureRef = "#" + element.textureKey();
            int tintIndex = element.tintIndex();

            elements.add(buildDoorElement(suffix, textureRef, tintIndex));
        }

        JsonObject model = new JsonObject();
        model.addProperty("ambientocclusion", false);
        model.add("textures", textures);
        model.add("elements", elements);

        return model;
    }

    private JsonObject buildDoorElement(String suffix, String texture, int tintIndex)
    {
        // All door variants are 3 pixels wide (x: 0-3), full height (y: 0-16), full depth (z: 0-16)
        JsonArray from = vectorArray(0, 0, 0);
        JsonArray to = vectorArray(3, 16, 16);

        JsonObject faces = new JsonObject();

        boolean isOpen = suffix.contains("open");
        boolean isTop = suffix.startsWith("top");
        boolean isRight = suffix.contains("right");

        // Top/bottom face (only on the appropriate half)
        if (isTop)
        {
            int[] uv = getDoorTopBottomUV(suffix);
            int rotation = getDoorTopBottomRotation(suffix);

            JsonObject face = new JsonObject();
            face.add("uv", uvArray(uv[0], uv[1], uv[2], uv[3]));
            face.addProperty("texture", texture);
            face.addProperty("cullface", "up");
            if (rotation != 0)
                face.addProperty("rotation", rotation);
            face.addProperty("tintindex", tintIndex);
            faces.add("up", face);
        }
        else
        {
            int[] uv = getDoorTopBottomUV(suffix);
            int rotation = getDoorTopBottomRotation(suffix);

            JsonObject face = new JsonObject();
            face.add("uv", uvArray(uv[0], uv[1], uv[2], uv[3]));
            face.addProperty("texture", texture);
            face.addProperty("cullface", "down");
            if (rotation != 0)
                face.addProperty("rotation", rotation);
            face.addProperty("tintindex", tintIndex);
            faces.add("down", face);
        }

        // North face
        {
            int[] uv = getDoorNorthUV(suffix);
            JsonObject face = new JsonObject();
            face.add("uv", uvArray(uv[0], uv[1], uv[2], uv[3]));
            face.addProperty("texture", texture);
            face.addProperty("cullface", "north");
            face.addProperty("tintindex", tintIndex);
            faces.add("north", face);
        }

        // South face
        {
            int[] uv = getDoorSouthUV(suffix);
            JsonObject face = new JsonObject();
            face.add("uv", uvArray(uv[0], uv[1], uv[2], uv[3]));
            face.addProperty("texture", texture);
            face.addProperty("cullface", "south");
            face.addProperty("tintindex", tintIndex);
            faces.add("south", face);
        }

        // West face
        {
            int[] uv = getDoorWestUV(suffix);
            JsonObject face = new JsonObject();
            face.add("uv", uvArray(uv[0], uv[1], uv[2], uv[3]));
            face.addProperty("texture", texture);
            face.addProperty("cullface", "west");
            face.addProperty("tintindex", tintIndex);
            faces.add("west", face);
        }

        // East face (no cullface on most door variants)
        {
            int[] uv = getDoorEastUV(suffix);
            JsonObject face = new JsonObject();
            face.add("uv", uvArray(uv[0], uv[1], uv[2], uv[3]));
            face.addProperty("texture", texture);
            face.addProperty("tintindex", tintIndex);
            faces.add("east", face);
        }

        JsonObject element = new JsonObject();
        element.add("from", from);
        element.add("to", to);
        element.add("faces", faces);
        return element;
    }

    // UV and rotation data matching the tinted_door static models exactly

    private int[] getDoorTopBottomUV(String suffix)
    {
        return switch (suffix)
        {
            case "bottom_left"       -> new int[]{16, 13,  0, 16};
            case "bottom_left_open"  -> new int[]{ 0, 16, 16, 13};
            case "bottom_right"      -> new int[]{ 0, 13, 16, 16};
            case "bottom_right_open" -> new int[]{16, 16,  0, 13};
            case "top_left"          -> new int[]{ 0,  3, 16,  0};
            case "top_left_open"     -> new int[]{ 0,  3, 16,  0};
            case "top_right"         -> new int[]{ 0,  0, 16,  3};
            case "top_right_open"    -> new int[]{ 0,  0, 16,  3};
            default -> new int[]{0, 0, 16, 16};
        };
    }

    private int getDoorTopBottomRotation(String suffix)
    {
        return switch (suffix)
        {
            case "bottom_left"       -> 90;
            case "bottom_left_open"  -> 90;
            case "bottom_right"      -> 90;
            case "bottom_right_open" -> 90;
            case "top_left"          -> 90;
            case "top_left_open"     -> 270;
            case "top_right"         -> 270;
            case "top_right_open"    -> 90;
            default -> 0;
        };
    }

    private int[] getDoorNorthUV(String suffix)
    {
        return switch (suffix)
        {
            case "bottom_left", "bottom_right", "top_left", "top_right"
                    -> new int[]{3, 0, 0, 16};
            case "bottom_left_open", "top_left_open"
                    -> new int[]{0, 0, 3, 16};
            case "bottom_right_open", "top_right_open"
                    -> new int[]{3, 0, 0, 16};
            default -> new int[]{0, 0, 3, 16};
        };
    }

    private int[] getDoorSouthUV(String suffix)
    {
        return switch (suffix)
        {
            case "bottom_left", "bottom_right", "top_left", "top_right"
                    -> new int[]{0, 0, 3, 16};
            case "bottom_left_open", "top_left_open"
                    -> new int[]{0, 0, 3, 16};
            case "bottom_right_open", "top_right_open"
                    -> new int[]{3, 0, 0, 16};
            default -> new int[]{0, 0, 3, 16};
        };
    }

    private int[] getDoorWestUV(String suffix)
    {
        return switch (suffix)
        {
            case "bottom_left_open", "bottom_right_open",
                 "top_left_open", "top_right_open"
                    -> new int[]{16, 0, 0, 16};
            default -> new int[]{0, 0, 16, 16};
        };
    }

    private int[] getDoorEastUV(String suffix)
    {
        return switch (suffix)
        {
            case "bottom_left_open", "bottom_right_open",
                 "top_left_open", "top_right_open"
                    -> new int[]{0, 0, 16, 16};
            default -> new int[]{16, 0, 0, 16};
        };
    }

    default JsonObject generateItemModel()
    {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", "%s:item/%s".formatted(Fortuna.MOD_ID, getRegistryName()));

        JsonArray tints = new JsonArray();
        for (int tint : getRequiredTints())
            tints.add(buildTint(tint));
        model.add("tints", tints);

        JsonObject itemModel = new JsonObject();
        itemModel.add("model", model);
        return itemModel;
    }

    default JsonObject getDoorLoot()
    {
        String blockName = "%s:%s".formatted(Fortuna.MOD_ID, getRegistryName());

        JsonObject halfCondition = new JsonObject();
        halfCondition.addProperty("condition", "minecraft:block_state_property");
        halfCondition.addProperty("block", blockName);
        JsonObject properties = new JsonObject();
        properties.addProperty("half", "lower");
        halfCondition.add("properties", properties);

        JsonArray entryConditions = new JsonArray();
        entryConditions.add(halfCondition);

        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", blockName);
        entry.add("conditions", entryConditions);

        JsonArray entries = new JsonArray();
        entries.add(entry);

        JsonObject survives = new JsonObject();
        survives.addProperty("condition", "minecraft:survives_explosion");

        JsonArray poolConditions = new JsonArray();
        poolConditions.add(survives);

        JsonObject pool = new JsonObject();
        pool.add("conditions", poolConditions);
        pool.add("entries", entries);
        pool.addProperty("rolls", 1.0);

        JsonArray pools = new JsonArray();
        pools.add(pool);

        JsonObject lootTable = new JsonObject();
        lootTable.addProperty("type", "minecraft:block");
        lootTable.add("pools", pools);

        return lootTable;
    }

    private JsonArray vectorArray(float x, float y, float z)
    {
        JsonArray v = new JsonArray();
        v.add(x); v.add(y); v.add(z);
        return v;
    }

    private JsonArray uvArray(float a, float b, float c, float d)
    {
        JsonArray uv = new JsonArray();
        uv.add(a); uv.add(b); uv.add(c); uv.add(d);
        return uv;
    }
}