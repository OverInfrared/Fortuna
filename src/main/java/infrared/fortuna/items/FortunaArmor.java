package infrared.fortuna.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.equipment.IFortunaEquipment;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FortunaArmor extends FortunaItem implements IFortunaRecipe, IFortunaEquipment
{
    private final DynamicArmorType armorType;

    public FortunaArmor(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties, DynamicArmorType armorType)
    {
        super(dynamicProperties, properties
                .humanoidArmor(dynamicProperties.material().getArmorMaterial(), armorType.getVanillaArmorType())
        );
        this.armorType = armorType;

        OreMaterial material = dynamicProperties.material();

        addRequiredTexture(armorType.getItemTexture(material.getArmorVariant()));
        addRequiredTint(material.getMainColor().getRGB());
    }
    public DynamicArmorType getArmorType()
    {
        return armorType;
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item material = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (material == null)
            return new HashMap<>();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        recipes.put(getRegistryName(),
                helper.shapedArmor(this, material, armorType.getRecipePattern()));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(getRegistryName());
    }

    @Override
    public String getEquipmentName()
    {
        return dynamicProperties.material().getName();
    }

    @Override
    public JsonObject getEquipmentAsset()
    {
        int color = dynamicProperties.material().getMainColor().getRGB();
        String texture = DynamicArmorType.getEquipmentTexture(dynamicProperties.material().getArmorVariant());

        JsonObject dyeable = new JsonObject();
        dyeable.addProperty("color_when_undyed", color);

        JsonObject humanoidLayer = new JsonObject();
        humanoidLayer.addProperty("texture", "%s:%s".formatted(Fortuna.MOD_ID, texture));
        humanoidLayer.add("dyeable", dyeable);

        JsonArray humanoidLayers = new JsonArray();
        humanoidLayers.add(humanoidLayer);

        JsonObject leggingsLayer = new JsonObject();
        leggingsLayer.addProperty("texture", "%s:%s".formatted(Fortuna.MOD_ID, texture));
        leggingsLayer.add("dyeable", dyeable);

        JsonArray leggingsLayers = new JsonArray();
        leggingsLayers.add(leggingsLayer);

        JsonObject layers = new JsonObject();
        layers.add("humanoid", humanoidLayers);
        layers.add("humanoid_leggings", leggingsLayers);

        JsonObject root = new JsonObject();
        root.add("layers", layers);

        return root;
    }

    @Override
    public OreMaterial getMaterial()
    {
        return dynamicProperties.material();
    }

    @Override
    public String getItemString()
    {
        String[] vanillaTrimMaterials = { "quartz", "iron", "netherite", "redstone", "copper", "gold", "emerald", "diamond", "lapis", "amethyst", "resin" };

        JsonArray cases = new JsonArray();

        // Vanilla trim cases
        for (String trim : vanillaTrimMaterials)
        {
            cases.add(buildTrimCase("minecraft", trim));
        }

        // Dynamic trim cases
        for (infrared.fortuna.materials.Material mat : Fortuna.initializedMaterials)
        {
            if (mat instanceof OreMaterial oreMat)
                cases.add(buildTrimCase(Fortuna.MOD_ID, oreMat.getName()));
        }

        JsonObject fallbackModel = new JsonObject();
        fallbackModel.addProperty("type", "minecraft:model");
        fallbackModel.addProperty("model", "%s:item/%s".formatted(Fortuna.MOD_ID, getRegistryName()));

        JsonArray fallbackTints = new JsonArray();
        fallbackTints.add(buildConstantTint(dynamicProperties.material().getMainColor().getRGB()));
        fallbackModel.add("tints", fallbackTints);

        JsonObject select = new JsonObject();
        select.addProperty("type", "minecraft:select");
        select.addProperty("property", "minecraft:trim_material");
        select.add("cases", cases);
        select.add("fallback", fallbackModel);

        JsonObject root = new JsonObject();
        root.add("model", select);

        return root.toString();
    }

    private JsonObject buildTrimCase(String namespace, String trim)
    {
        JsonObject trimModel = new JsonObject();
        trimModel.addProperty("type", "minecraft:model");
        trimModel.addProperty("model", "%s:item/%s_%s_trim".formatted(Fortuna.MOD_ID, getRegistryName(), trim));

        JsonArray tints = new JsonArray();
        tints.add(buildConstantTint(dynamicProperties.material().getMainColor().getRGB()));
        trimModel.add("tints", tints);

        JsonObject caseEntry = new JsonObject();
        caseEntry.add("model", trimModel);
        caseEntry.addProperty("when", "%s:%s".formatted(namespace, trim));
        return caseEntry;
    }

    private JsonObject buildConstantTint(int color)
    {
        JsonObject tint = new JsonObject();
        tint.addProperty("type", "minecraft:constant");
        tint.addProperty("value", color);
        return tint;
    }
}