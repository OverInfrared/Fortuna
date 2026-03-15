package infrared.fortuna.worldgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.ore.OreMaterial;

public class OrePlacedFeature implements IPlacedFeature
{
    private final int idealHeight;
    private final int topEnd;
    private final int bottomEnd;

    private final FeatureProbability probability;
    private final DisperseType disperseType;

    private final IConfiguredFeature targetFeature;

    public OrePlacedFeature(int idealHeight, int topEnd, int bottomEnd, FeatureProbability probability, DisperseType disperseType, IConfiguredFeature targetFeature)
    {
        this.idealHeight = idealHeight;
        this.topEnd = topEnd;
        this.bottomEnd = bottomEnd;
        this.probability = probability;
        this.disperseType = disperseType;
        this.targetFeature = targetFeature;
    }

    public String generatePlacedFeature()
    {
        JsonArray placement = new JsonArray();

        // Count or rarity
        if (probability.type() == FeatureProbability.CountType.Rarity)
        {
            JsonObject rarityFilter = new JsonObject();
            rarityFilter.addProperty("type", "minecraft:rarity_filter");
            rarityFilter.addProperty("chance", probability.count());
            placement.add(rarityFilter);
        }
        else
        {
            JsonObject count = new JsonObject();
            count.addProperty("type", "minecraft:count");
            count.addProperty("count", probability.count());
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
        height.addProperty("type", disperseType == DisperseType.Trapezoid
                ? "minecraft:trapezoid"
                : "minecraft:uniform");

        JsonObject minInclusive = new JsonObject();
        minInclusive.addProperty("absolute", bottomEnd);
        height.add("min_inclusive", minInclusive);

        JsonObject maxInclusive = new JsonObject();
        maxInclusive.addProperty("absolute", topEnd);
        height.add("max_inclusive", maxInclusive);

        heightRange.add("height", height);
        placement.add(heightRange);

        // Biome filter
        JsonObject biomeFilter = new JsonObject();
        biomeFilter.addProperty("type", "minecraft:biome");
        placement.add(biomeFilter);

        JsonObject placedFeature = new JsonObject();
        placedFeature.addProperty("feature", targetFeature.getFeatureName());
        placedFeature.add("placement", placement);

        return placedFeature.toString();
    }
}
