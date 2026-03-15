package infrared.fortuna.worldgen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.ore.MaterialOreBase;

public class OreConfiguredFeature implements IConfiguredFeature
{
    private final int size;
    private final float discardChance;

    private final String materialName;
    private final MaterialOreBase materialBase;

    private final String identifier;

    public OreConfiguredFeature(int veinSize, float discardChance, String materialName, MaterialOreBase base, String identifier)
    {
        this.size = veinSize;
        this.discardChance = discardChance;
        this.materialName = materialName;
        this.materialBase = base;
        this.identifier = identifier;
    }

    public int getVeinSize()               { return size; }
    public float getDiscardChance()        { return discardChance; }

    public String generateConfiguredFeature()
    {
        String oreName = "%s:%s_ore".formatted(Fortuna.MOD_ID, materialName);

        JsonArray targets = new JsonArray();

        if (materialBase == MaterialOreBase.Sand)
        {
            targets.add(buildTarget("minecraft:block_match", "minecraft:sand", oreName));
        }
        else if (materialBase == MaterialOreBase.Gravel)
        {
            targets.add(buildTarget("minecraft:block_match", "minecraft:gravel", oreName));
        }
        else
        {
            // Stone-based ore targets stone_ore_replaceables
            targets.add(buildTagTarget("minecraft:stone_ore_replaceables", oreName));

            // If stone base, also target deepslate
            if (materialBase == MaterialOreBase.Stone)
            {
                String deepslateName = "%s:deepslate_%s_ore".formatted(Fortuna.MOD_ID, materialName);
                targets.add(buildTagTarget("minecraft:deepslate_ore_replaceables", deepslateName));
            }
        }

        JsonObject config = new JsonObject();
        config.addProperty("size", size);
        config.add("targets", targets);
        config.addProperty("discard_chance_on_air_exposure", discardChance);

        JsonObject feature = new JsonObject();
        feature.addProperty("type", "minecraft:ore");
        feature.add("config", config);

        return feature.toString();
    }

    @Override
    public String getFeatureName()
    {
        return "%s:%s_ore_%s".formatted(Fortuna.MOD_ID, materialName, identifier);
    }

    private JsonObject buildTarget(String predicateType, String blockMatch, String blockName)
    {
        JsonObject target = new JsonObject();
        target.addProperty("predicate_type", predicateType);
        target.addProperty("block", blockMatch);

        JsonObject state = new JsonObject();
        state.addProperty("Name", blockName);

        JsonObject entry = new JsonObject();
        entry.add("target", target);
        entry.add("state", state);

        return entry;
    }

    private JsonObject buildTagTarget(String tag, String blockName)
    {
        JsonObject target = new JsonObject();
        target.addProperty("predicate_type", "minecraft:tag_match");
        target.addProperty("tag", tag);

        JsonObject state = new JsonObject();
        state.addProperty("Name", blockName);

        JsonObject entry = new JsonObject();
        entry.add("target", target);
        entry.add("state", state);

        return entry;
    }
}