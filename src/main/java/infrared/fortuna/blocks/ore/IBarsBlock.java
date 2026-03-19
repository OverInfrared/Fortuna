package infrared.fortuna.blocks.ore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;

public interface IBarsBlock extends IFortunaBlock
{
    default String getBlockStateString()
    {
        String name = getDynamicProperties().registryName();

        JsonArray multipart = new JsonArray();

        multipart.add(part(applyPart("%s:block/%s_post_ends".formatted(Fortuna.MOD_ID, name))));

        multipart.add(part(
                applyPart("%s:block/%s_post".formatted(Fortuna.MOD_ID, name)),
                when(
                        prop("east", "false"),
                        prop("north", "false"),
                        prop("south", "false"),
                        prop("west", "false")
                )
        ));

        multipart.add(part(
                applyPart("%s:block/%s_cap".formatted(Fortuna.MOD_ID, name)),
                when(
                        prop("east", "false"),
                        prop("north", "true"),
                        prop("south", "false"),
                        prop("west", "false")
                )
        ));

        multipart.add(part(
                applyPart("%s:block/%s_cap".formatted(Fortuna.MOD_ID, name), 90),
                when(
                        prop("east", "true"),
                        prop("north", "false"),
                        prop("south", "false"),
                        prop("west", "false")
                )
        ));

        multipart.add(part(
                applyPart("%s:block/%s_cap_alt".formatted(Fortuna.MOD_ID, name)),
                when(
                        prop("east", "false"),
                        prop("north", "false"),
                        prop("south", "true"),
                        prop("west", "false")
                )
        ));

        multipart.add(part(
                applyPart("%s:block/%s_cap_alt".formatted(Fortuna.MOD_ID, name), 90),
                when(
                        prop("east", "false"),
                        prop("north", "false"),
                        prop("south", "false"),
                        prop("west", "true")
                )
        ));

        multipart.add(part(
                applyPart("%s:block/%s_side".formatted(Fortuna.MOD_ID, name)),
                when(prop("north", "true"))
        ));

        multipart.add(part(
                applyPart("%s:block/%s_side".formatted(Fortuna.MOD_ID, name), 90),
                when(prop("east", "true"))
        ));

        multipart.add(part(
                applyPart("%s:block/%s_side_alt".formatted(Fortuna.MOD_ID, name)),
                when(prop("south", "true"))
        ));

        multipart.add(part(
                applyPart("%s:block/%s_side_alt".formatted(Fortuna.MOD_ID, name), 90),
                when(prop("west", "true"))
        ));

        JsonObject blockstate = new JsonObject();
        blockstate.add("multipart", multipart);
        return blockstate.toString();
    }

    private JsonObject part(JsonObject apply)
    {
        JsonObject obj = new JsonObject();
        obj.add("apply", apply);
        return obj;
    }

    private JsonObject part(JsonObject apply, JsonObject when)
    {
        JsonObject obj = new JsonObject();
        obj.add("apply", apply);
        obj.add("when", when);
        return obj;
    }

    private JsonObject applyPart(String model)
    {
        JsonObject apply = new JsonObject();
        apply.addProperty("model", model);
        return apply;
    }

    private JsonObject applyPart(String model, int y)
    {
        JsonObject apply = applyPart(model);
        apply.addProperty("y", y);
        return apply;
    }

    private JsonObject when(JsonObject... props)
    {
        JsonObject when = new JsonObject();
        for (JsonObject prop : props)
        {
            for (String key : prop.keySet())
            {
                when.add(key, prop.get(key));
            }
        }
        return when;
    }

    private JsonObject prop(String key, String value)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty(key, value);
        return obj;
    }

    default JsonObject generateModel(String suffix)
    {
        JsonObject textures = new JsonObject();

        textures.addProperty("particle", "%s:block/%s".formatted(Fortuna.MOD_ID, getRequiredTextures().getFirst().getValue()));

        for (Pair<String, String> texture : getRequiredTextures())
        {
            textures.addProperty(
                    texture.getKey(),
                    "%s:block/%s".formatted(Fortuna.MOD_ID, texture.getValue())
            );
        }

        JsonObject model = new JsonObject();
        model.addProperty("ambientocclusion", false);
        model.add("textures", textures);

        JsonArray elements = switch (suffix)
        {
            case "cap" -> getCapElements();
            case "cap_alt" -> getCapAltElements();
            case "post" -> getPostElements();
            case "post_ends" -> getPostEndsElements();
            case "side" -> getSideElements();
            case "side_alt" -> getSideAltElements();
            default -> new JsonArray();
        };

        model.add("elements", elements);
        return model;
    }

    private JsonArray getCapElements()
    {
        return buildBarsElements(
                new int[]{8, 0, 8}, new int[]{8, 16, 9},
                new int[]{8, 0, 7, 16}, new int[]{7, 0, 8, 16},
                new int[]{7, 0, 9}, new int[]{9, 16, 9},
                new int[]{9, 0, 7, 16}, new int[]{7, 0, 9, 16}
        );
    }

    private JsonArray getCapAltElements()
    {
        return buildBarsElements(
                new int[]{8, 0, 7}, new int[]{8, 16, 8},
                new int[]{8, 0, 9, 16}, new int[]{9, 0, 8, 16},
                new int[]{7, 0, 7}, new int[]{9, 16, 7},
                new int[]{7, 0, 9, 16}, new int[]{9, 0, 7, 16}
        );
    }

    private JsonArray getPostElements()
    {
        JsonArray elements = new JsonArray();

        for (RequiredElement element : getRequiredElements())
        {
            String texture = "#" + element.textureKey();
            int tintIndex = element.tintIndex();

            elements.add(element(
                    vectorArray(8, 0, 7),
                    vectorArray(8, 16, 9),
                    faces(
                            face("west", uv(7, 0, 9, 16), texture, tintIndex),
                            face("east", uv(9, 0, 7, 16), texture, tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(7, 0, 8),
                    vectorArray(9, 16, 8),
                    faces(
                            face("north", uv(7, 0, 9, 16), texture, tintIndex),
                            face("south", uv(9, 0, 7, 16), texture, tintIndex)
                    )
            ));
        }

        return elements;
    }

    private JsonArray getPostEndsElements()
    {
        JsonArray elements = new JsonArray();

        for (RequiredElement element : getRequiredElements())
        {
            String texture = "#" + element.textureKey();
            int tintIndex = element.tintIndex();

            elements.add(element(
                    vectorArray(7, 0.001f, 7),
                    vectorArray(9, 0.001f, 9),
                    faces(
                            face("down", uv(7, 7, 9, 9), texture, tintIndex),
                            face("up", uv(7, 7, 9, 9), texture, tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(7, 15.999f, 7),
                    vectorArray(9, 15.999f, 9),
                    faces(
                            face("down", uv(7, 7, 9, 9), texture, tintIndex),
                            face("up", uv(7, 7, 9, 9), texture, tintIndex)
                    )
            ));
        }

        return elements;
    }

    private JsonArray getSideElements()
    {
        JsonArray elements = new JsonArray();

        for (RequiredElement element : getRequiredElements())
        {
            String texture = "#" + element.textureKey();
            int tintIndex = element.tintIndex();

            elements.add(element(
                    vectorArray(8, 0, 0),
                    vectorArray(8, 16, 8),
                    faces(
                            face("west", uv(16, 0, 8, 16), texture, tintIndex),
                            face("east", uv(8, 0, 16, 16), texture, tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(7, 0, 0),
                    vectorArray(9, 16, 7),
                    faces(
                            faceWithCull("north", uv(7, 0, 9, 16), texture, "north", tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(7, 0.001f, 0),
                    vectorArray(9, 0.001f, 7),
                    faces(
                            face("down", uv(9, 0, 7, 7), texture, tintIndex),
                            face("up", uv(7, 0, 9, 7), texture, tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(7, 15.999f, 0),
                    vectorArray(9, 15.999f, 7),
                    faces(
                            face("down", uv(9, 0, 7, 7), texture, tintIndex),
                            face("up", uv(7, 0, 9, 7), texture, tintIndex)
                    )
            ));
        }

        return elements;
    }

    private JsonArray getSideAltElements()
    {
        JsonArray elements = new JsonArray();

        for (RequiredElement element : getRequiredElements())
        {
            String texture = "#" + element.textureKey();
            int tintIndex = element.tintIndex();

            elements.add(element(
                    vectorArray(8, 0, 8),
                    vectorArray(8, 16, 16),
                    faces(
                            face("west", uv(8, 0, 0, 16), texture, tintIndex),
                            face("east", uv(0, 0, 8, 16), texture, tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(7, 0, 9),
                    vectorArray(9, 16, 16),
                    faces(
                            faceWithCull("south", uv(7, 0, 9, 16), texture, "south", tintIndex),
                            face("down", uv(9, 9, 7, 16), texture, tintIndex),
                            face("up", uv(7, 9, 9, 16), texture, tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(7, 0.001f, 9),
                    vectorArray(9, 0.001f, 16),
                    faces(
                            face("down", uv(9, 9, 7, 16), texture, tintIndex),
                            face("up", uv(7, 9, 9, 16), texture, tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(7, 15.999f, 9),
                    vectorArray(9, 15.999f, 16),
                    faces(
                            face("down", uv(9, 9, 7, 16), texture, tintIndex),
                            face("up", uv(7, 9, 9, 16), texture, tintIndex)
                    )
            ));
        }

        return elements;
    }

    private JsonArray buildBarsElements(
            int[] ewFrom, int[] ewTo,
            int[] westUv, int[] eastUv,
            int[] nsFrom, int[] nsTo,
            int[] northUv, int[] southUv
    )
    {
        JsonArray elements = new JsonArray();

        for (RequiredElement element : getRequiredElements())
        {
            String texture = "#" + element.textureKey();
            int tintIndex = element.tintIndex();

            elements.add(element(
                    vectorArray(ewFrom[0], ewFrom[1], ewFrom[2]),
                    vectorArray(ewTo[0], ewTo[1], ewTo[2]),
                    faces(
                            face("west", uv(westUv[0], westUv[1], westUv[2], westUv[3]), texture, tintIndex),
                            face("east", uv(eastUv[0], eastUv[1], eastUv[2], eastUv[3]), texture, tintIndex)
                    )
            ));

            elements.add(element(
                    vectorArray(nsFrom[0], nsFrom[1], nsFrom[2]),
                    vectorArray(nsTo[0], nsTo[1], nsTo[2]),
                    faces(
                            face("north", uv(northUv[0], northUv[1], northUv[2], northUv[3]), texture, tintIndex),
                            face("south", uv(southUv[0], southUv[1], southUv[2], southUv[3]), texture, tintIndex)
                    )
            ));
        }

        return elements;
    }

    private JsonObject element(JsonArray from, JsonArray to, JsonObject faces)
    {
        JsonObject element = new JsonObject();
        element.add("from", from);
        element.add("to", to);
        element.add("faces", faces);
        return element;
    }

    private JsonObject faces(JsonObject... entries)
    {
        JsonObject faces = new JsonObject();
        for (JsonObject entry : entries)
        {
            for (String key : entry.keySet())
            {
                faces.add(key, entry.get(key));
            }
        }
        return faces;
    }

    private JsonObject face(String direction, JsonArray uv, String texture, int tintIndex)
    {
        JsonObject face = new JsonObject();
        face.add("uv", uv);
        face.addProperty("texture", texture);
        face.addProperty("tintindex", tintIndex);

        JsonObject wrapped = new JsonObject();
        wrapped.add(direction, face);
        return wrapped;
    }

    private JsonObject faceWithCull(String direction, JsonArray uv, String texture, String cullface, int tintIndex)
    {
        JsonObject face = new JsonObject();
        face.add("uv", uv);
        face.addProperty("texture", texture);
        face.addProperty("cullface", cullface);
        face.addProperty("tintindex", tintIndex);

        JsonObject wrapped = new JsonObject();
        wrapped.add(direction, face);
        return wrapped;
    }

    private JsonArray vectorArray(float x, float y, float z)
    {
        JsonArray vector = new JsonArray();
        vector.add(x);
        vector.add(y);
        vector.add(z);
        return vector;
    }

    private JsonArray uv(float x, float y, float z, float w)
    {
        JsonArray uv = new JsonArray();
        uv.add(x);
        uv.add(y);
        uv.add(z);
        uv.add(w);
        return uv;
    }
}
