package infrared.fortuna.blocks.ore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import net.minecraft.core.HolderLookup;

public interface ITrapDoorBlock extends IFortunaBlock
{
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
        textures.addProperty("texture", "%s:block/trapdoor".formatted(Fortuna.MOD_ID));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "%s:block/tinted_template_trapdoor_%s".formatted(Fortuna.MOD_ID, modelSuffix));
        model.add("textures", textures);

        return model;
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
