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
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.*;

public class FortunaTrapDoorBlock extends TrapDoorBlock implements IFortunaBlock, IFortunaRecipe
{
    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    protected MiningLevel requiredMiningLevel;

    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    public FortunaTrapDoorBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, BlockSetType blockSetType)
    {
        super(blockSetType, properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
        this.requiredMiningLevel = dynamicProperties.material().getMiningLevel();

        addRequiredTexture("particle", "trapdoor");
        addOverlayTexture("texture", "trapdoor", 0);
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
                helper.shapedTrapdoor(this.asItem(), ingot));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(getRegistryName());
    }

    public static final String[] TRAPDOOR_MODELS = { "bottom", "top", "open" };

    @Override
    public String getBlockStateString()
    {
        String name = dynamicProperties.registryName();
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

    public String getTrapdoorModelString(String modelSuffix)
    {
        JsonObject textures = new JsonObject();
        textures.addProperty("texture", "%s:block/trapdoor".formatted(Fortuna.MOD_ID));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "%s:block/tinted_template_trapdoor_%s".formatted(Fortuna.MOD_ID, modelSuffix));
        model.add("textures", textures);

        return model.toString();
    }

    @Override
    public JsonObject generateItemModel()
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

    @Override
    public JsonObject getLoot(HolderLookup.Provider registries)
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