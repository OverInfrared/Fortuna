package infrared.fortuna.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface IFortunaItem
{
    Component getDisplayName();
    String getRegistryName();

    List<String> getRequiredTextures();
    List<Integer> getRequiredTints();

    default String getItemString()
    {
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", "%s:item/%s".formatted(Fortuna.MOD_ID, getRegistryName()));

        JsonArray tints = new JsonArray();
        for (int color : getRequiredTints())
        {
            JsonObject tint = new JsonObject();
            tint.addProperty("type", "minecraft:constant");
            tint.addProperty("value", color);
            tints.add(tint);
        }

        model.add("tints", tints);

        JsonObject itemModel = new JsonObject();
        itemModel.add("model", model);

        return itemModel.toString();
    }

    default String getModelString()
    {
        return getModelString("item");
    }

    default String getModelString(String location)
    {
        JsonObject textures = new JsonObject();
        List<String> requiredTextures = getRequiredTextures();
        for (int i = 0; i < requiredTextures.size(); i++)
        {
            textures.addProperty("layer" + i, "%s:%s/%s".formatted(Fortuna.MOD_ID, location, requiredTextures.get(i)));
        }

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");
        model.add("textures", textures);

        return model.toString();
    }
}