package infrared.fortuna.equipment;

import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.Material;
import infrared.fortuna.util.PaletteGenerator;
import infrared.fortuna.util.Utilities;

public interface IFortunaEquipment
{
    String getEquipmentName();
    JsonObject getEquipmentAsset();

    default byte[] getTrimPalette()
    {
        return PaletteGenerator.generateTrimPalette(getMaterial().getMainColor());
    }

    default JsonObject getTrimMaterialJson()
    {
        Material material = getMaterial();

        JsonObject description = new JsonObject();
        description.addProperty("translate", "trim_material.%s.%s".formatted(Fortuna.MOD_ID, material.getName()));
        description.addProperty("color", String.format("#%06X", material.getMainColor().getRGB() & 0xFFFFFF));

        JsonObject trimMaterial = new JsonObject();
        trimMaterial.addProperty("asset_name", material.getName());
        trimMaterial.add("description", description);

        return trimMaterial;
    }

    default String getLangKey()
    {
        return "trim_material.%s.%s".formatted(Fortuna.MOD_ID, getMaterial().getName());
    }

    default String getLangValue()
    {
        return Utilities.capitalize(getMaterial().getName()) + " Material";
    }

    Material getMaterial();
}