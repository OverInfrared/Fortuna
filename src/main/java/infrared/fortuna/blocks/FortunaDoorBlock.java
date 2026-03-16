package infrared.fortuna.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.*;

public class FortunaDoorBlock extends DoorBlock implements IFortunaBlock, IFortunaRecipe
{
    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    protected MiningLevel requiredMiningLevel;

    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    public FortunaDoorBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, BlockSetType blockSetType)
    {
        super(blockSetType, properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
        this.requiredMiningLevel = dynamicProperties.material().getMiningLevel();

        addRequiredTexture("particle", "door_top");
        addOverlayTexture("top", "door_top", 0);
        addOverlayTexture("bottom", "door_bottom", 0);
        addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
        addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
    }

    @Override
    public List<Pair<String, String>> getRequiredTextures() { return requiredTextures; }

    @Override
    public List<RequiredElement> getRequiredElements() { return requiredElements; }

    @Override
    public List<Integer> getRequiredTints() { return requiredTints; }

    @Override
    public DynamicProperties<Block, OreMaterial> getDynamicProperties() { return dynamicProperties; }

    @Override
    public MiningLevel getMiningLevel() { return requiredMiningLevel; }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item ingot = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (ingot == null)
            return new HashMap<>();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        recipes.put(getRegistryName(),
                helper.shapedDoor(this.asItem(), ingot));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(getRegistryName());
    }

    public static final String[] DOOR_MODELS = {
            "bottom_left", "bottom_left_open",
            "bottom_right", "bottom_right_open",
            "top_left", "top_left_open",
            "top_right", "top_right_open"
    };

    private static final int[][] ROTATIONS = {
            // east, south, west, north — for each model pair (left/right × open/closed)
            // {east, south, west, north}
            {0, 90, 180, 270},     // bottom_left
            {90, 180, 270, 0},     // bottom_left_open
            {0, 90, 180, 270},     // bottom_right
            {270, 0, 90, 180},     // bottom_right_open
            {0, 90, 180, 270},     // top_left
            {90, 180, 270, 0},     // top_left_open
            {0, 90, 180, 270},     // top_right
            {270, 0, 90, 180},     // top_right_open
    };

    @Override
    public String getBlockStateString()
    {
        String name = dynamicProperties.registryName();
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

                        int modelIndex = java.util.Arrays.asList(DOOR_MODELS).indexOf(modelSuffix);

                        String key = "facing=%s,half=%s,hinge=%s,open=%s".formatted(
                                facings[f], half, hinge, String.valueOf(open));

                        JsonObject variant = new JsonObject();
                        variant.addProperty("model", "%s:block/%s_%s".formatted(Fortuna.MOD_ID, name, modelSuffix));

                        int rotation = ROTATIONS[modelIndex][f];
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

    public String getDoorModelString(String modelSuffix)
    {
        JsonObject textures = new JsonObject();
        textures.addProperty("top", "%s:block/door_top".formatted(Fortuna.MOD_ID));
        textures.addProperty("bottom", "%s:block/door_bottom".formatted(Fortuna.MOD_ID));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "%s:block/tinted_door_%s".formatted(Fortuna.MOD_ID, modelSuffix));
        model.add("textures", textures);

        return model.toString();
    }

    @Override
    public JsonObject generateItemModel()
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

    @Override
    public JsonObject getLoot(HolderLookup.Provider registries)
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
}