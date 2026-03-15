package infrared.fortuna.worldgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.ore.OreMaterial;

public class OrePlacedFeature
{
    private final int idealHeight;
    private final int topEnd;
    private final int bottomEnd;

    private final int count;

    public OrePlacedFeature(int idealHeight, int topEnd, int bottomEnd, FeatureProbability probability, )

    private String generatePlacedFeature(OreMaterial material, OreConfiguredFeature spawn, int index)
    {
        String featureRef = "%s:%s_ore_%d".formatted(Fortuna.MOD_ID, material.getName(), index);

        JsonArray placement = new JsonArray();

        // Count or rarity
        if (spawn.isRare())
        {
            JsonObject rarityFilter = new JsonObject();
            rarityFilter.addProperty("type", "minecraft:rarity_filter");
            rarityFilter.addProperty("chance", spawn.getRarity());
            placement.add(rarityFilter);
        }
        else
        {
            JsonObject count = new JsonObject();
            count.addProperty("type", "minecraft:count");
            count.addProperty("count", spawn.getCount());
            placement.add(count);
        }

        // In square
        JsonObject inSquare = new JsonObject();
        inSquare.addProperty("type", "minecraft:in_square");
        placement.add(inSquare);

        // Height range
        JsonObject heightRange = new JsonObject();
        heightRange.addProperty("type", "minecraft:height_range");

        JsonObject height = new JsonObject();
        if (spawn.usesTriangleDistribution())
            height.addProperty("type", "minecraft:trapezoid");
        else
            height.addProperty("type", "minecraft:uniform");

        JsonObject minInclusive = new JsonObject();
        minInclusive.addProperty("absolute", spawn.getMinHeight());
        height.add("min_inclusive", minInclusive);

        JsonObject maxInclusive = new JsonObject();
        maxInclusive.addProperty("absolute", spawn.getMaxHeight());
        height.add("max_inclusive", maxInclusive);

        heightRange.add("height", height);
        placement.add(heightRange);

        // Biome filter
        JsonObject biomeFilter = new JsonObject();
        biomeFilter.addProperty("type", "minecraft:biome");
        placement.add(biomeFilter);

        JsonObject placedFeature = new JsonObject();
        placedFeature.addProperty("feature", featureRef);
        placedFeature.add("placement", placement);

        return placedFeature.toString();
    }
}
