package infrared.fortuna.blocks.ore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.materials.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.WeatheringCopper;

public interface ITrapDoorBlock extends IFortunaBlock
{
    default void setupTextures(String texture, WeatheringCopper.WeatherState weatherState)
    {
        addRequiredTexture("particle", texture);

        Material material = getDynamicProperties().material();

        if (weatherState == null || weatherState == WeatheringCopper.WeatherState.UNAFFECTED)
        {
            addOverlayTexture("base", texture + "_neutral", 0);
            addOverlayTexture("white", texture + "_white", 1);
            addOverlayTexture("light", texture + "_light", 2);
            addOverlayTexture("dark", texture + "_dark", 3);
            addRequiredTint(material.getMainColor().getRGB());
            addRequiredTint(material.getColor("main_white").getRGB());
            addRequiredTint(material.getColor("main_light").getRGB());
            addRequiredTint(material.getColor("main_shift0").getRGB());
        }
        else if (weatherState == WeatheringCopper.WeatherState.EXPOSED)
        {
            addOverlayTexture("base", "exposed_" + texture + "_neutral", 0);
            addOverlayTexture("white", "exposed_" + texture + "_white", 1);
            addOverlayTexture("light", "exposed_" + texture + "_light", 2);
            addOverlayTexture("oxidized", "exposed_" + texture + "_oxidized", 3);
            addOverlayTexture("transition", "exposed_" + texture + "_transition", 4);
            addRequiredTint(material.getColor("transition_base").getRGB());
            addRequiredTint(material.getColor("transition_base_white").getRGB());
            addRequiredTint(material.getColor("transition_base_light").getRGB());
            addRequiredTint(material.getColor("transition_weathered").getRGB());
            addRequiredTint(material.getColor("transition_exposed").getRGB());
        }
        else if (weatherState == WeatheringCopper.WeatherState.WEATHERED)
        {
            addOverlayTexture("base", "weathered_" + texture + "_base", 0);
            addOverlayTexture("oxidized", "weathered_" + texture + "_oxidized", 1);
            addOverlayTexture("transition", "weathered_" + texture + "_transition", 2);
            addRequiredTint(material.getColor("transition_base").getRGB());
            addRequiredTint(material.getColor("transition_weathered").getRGB());
            addRequiredTint(material.getColor("transition_exposed").getRGB());
        }
        else if (weatherState == WeatheringCopper.WeatherState.OXIDIZED)
        {
            addOverlayTexture("base", "oxidized_" + texture, 0);
            addRequiredTint(material.getSecondaryColor().getRGB());
        }
    }

    String[] TRAPDOOR_MODELS = { "bottom", "top", "open" };

    default String getBlockStateString()
    {
        String name = getDynamicProperties().registryName();
        String[] facings = {"east", "south", "west", "north"};
        int[] openRotations = {90, 180, 270, 0};

        JsonObject variants = new JsonObject();

        for (int f = 0; f < facings.length; f++)
        {
            for (String half : new String[]{"bottom", "top"})
            {
                for (boolean open : new boolean[]{false, true})
                {
                    String key = "facing=%s,half=%s,open=%s".formatted(facings[f], half, String.valueOf(open));

                    JsonObject variant = new JsonObject();

                    if (open)
                    {
                        variant.addProperty("model", "%s:block/%s_open".formatted(Fortuna.MOD_ID, name));
                        int rotation = openRotations[f];
                        if (rotation != 0)
                            variant.addProperty("y", rotation);
                    }
                    else
                    {
                        variant.addProperty("model", "%s:block/%s_%s".formatted(Fortuna.MOD_ID, name, half));
                    }

                    variants.add(key, variant);
                }
            }
        }

        JsonObject blockstate = new JsonObject();
        blockstate.add("variants", variants);
        return blockstate.toString();
    }

    default JsonObject generateModel(String modelSuffix)
    {
        JsonObject textures = new JsonObject();
        for (var texture : getRequiredTextures())
            textures.addProperty(texture.getKey(), "%s:block/%s".formatted(Fortuna.MOD_ID, texture.getValue()));

        JsonArray elements = new JsonArray();
        for (RequiredElement element : getRequiredElements())
            elements.add(buildTrapDoorElement(modelSuffix, "#" + element.textureKey(), element.tintIndex()));

        JsonObject model = new JsonObject();
        model.addProperty("ambientocclusion", false);
        model.add("textures", textures);
        model.add("elements", elements);
        model.addProperty("parent", "block/thin_block");
        return model;
    }

    private JsonObject buildTrapDoorElement(String suffix, String texture, int tintIndex)
    {
        JsonArray from;
        JsonArray to;

        if (suffix.equals("bottom"))
        {
            from = vec(0, 0, 0);
            to   = vec(16, 3, 16);
        }
        else if (suffix.equals("top"))
        {
            from = vec(0, 13, 0);
            to   = vec(16, 16, 16);
        }
        else // open
        {
            from = vec(0, 0, 13);
            to   = vec(16, 16, 16);
        }

        JsonObject faces = new JsonObject();

        if (suffix.equals("bottom"))
        {
            faces.add("down",  tdFace(texture, tintIndex, uv(0, 0, 16, 16),  "down"));
            faces.add("up",    tdFace(texture, tintIndex, uv(0, 0, 16, 16),  null));
            faces.add("north", tdFace(texture, tintIndex, uv(0, 16, 16, 13), "north"));
            faces.add("south", tdFace(texture, tintIndex, uv(0, 16, 16, 13), "south"));
            faces.add("west",  tdFace(texture, tintIndex, uv(0, 16, 16, 13), "west"));
            faces.add("east",  tdFace(texture, tintIndex, uv(0, 16, 16, 13), "east"));
        }
        else if (suffix.equals("top"))
        {
            faces.add("down",  tdFace(texture, tintIndex, uv(0, 0, 16, 16),  null));
            faces.add("up",    tdFace(texture, tintIndex, uv(0, 0, 16, 16),  "up"));
            faces.add("north", tdFace(texture, tintIndex, uv(0, 16, 16, 13), "north"));
            faces.add("south", tdFace(texture, tintIndex, uv(0, 16, 16, 13), "south"));
            faces.add("west",  tdFace(texture, tintIndex, uv(0, 16, 16, 13), "west"));
            faces.add("east",  tdFace(texture, tintIndex, uv(0, 16, 16, 13), "east"));
        }
        else // open
        {
            faces.add("down",  tdFace(texture, tintIndex, uv(0,  13, 16, 16), "down"));
            faces.add("up",    tdFace(texture, tintIndex, uv(0,  16, 16, 13), "up"));
            faces.add("north", tdFace(texture, tintIndex, uv(0,  0,  16, 16), null));
            faces.add("south", tdFace(texture, tintIndex, uv(0,  0,  16, 16), "south"));
            faces.add("west",  tdFace(texture, tintIndex, uv(16, 0,  13, 16), "west"));
            faces.add("east",  tdFace(texture, tintIndex, uv(13, 0,  16, 16), "east"));
        }

        JsonObject element = new JsonObject();
        element.add("from", from);
        element.add("to", to);
        element.add("faces", faces);
        return element;
    }

    private JsonObject tdFace(String texture, int tintIndex, JsonArray uvArr, String cullface)
    {
        JsonObject face = new JsonObject();
        face.add("uv", uvArr);
        face.addProperty("texture", texture);
        if (cullface != null)
            face.addProperty("cullface", cullface);
        if (tintIndex != -1)
            face.addProperty("tintindex", tintIndex);
        return face;
    }

    private JsonArray vec(float x, float y, float z)
    {
        JsonArray v = new JsonArray();
        v.add(x); v.add(y); v.add(z);
        return v;
    }

    private JsonArray uv(float a, float b, float c, float d)
    {
        JsonArray u = new JsonArray();
        u.add(a); u.add(b); u.add(c); u.add(d);
        return u;
    }

    default JsonObject generateItemModel()
    {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", "%s:block/%s_bottom".formatted(Fortuna.MOD_ID, getRegistryName()));

        JsonArray tints = new JsonArray();
        for (int tint : getRequiredTints())
            tints.add(buildTint(tint));
        model.add("tints", tints);

        JsonObject itemModel = new JsonObject();
        itemModel.add("model", model);
        return itemModel;
    }

    default JsonObject getLoot(HolderLookup.Provider registries)
    {
        String blockName = "%s:%s".formatted(Fortuna.MOD_ID, getRegistryName());

        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", blockName);

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
}
