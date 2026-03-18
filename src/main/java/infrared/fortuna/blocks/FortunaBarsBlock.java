package infrared.fortuna.blocks;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.util.Utilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;

import java.util.*;

public class FortunaBarsBlock extends IronBarsBlock implements IFortunaBlock, IFortunaRecipe
{
    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    protected MiningLevel requiredMiningLevel;

    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    public FortunaBarsBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties)
    {
        super(properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
        this.requiredMiningLevel = dynamicProperties.material().getMiningLevel();

        // Todo allow for weathering, that will be pain.
        addRequiredTexture("bars", "iron_bars");
        addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
    }

    @Override
    public List<Pair<String, String>> getRequiredTextures() { return requiredTextures; }

    @Override
    public List<String> getRequiredItemTextures()
    {
        return List.of("iron_bars");
    }

    @Override
    public List<RequiredElement> getRequiredElements() { return requiredElements; }

    @Override
    public List<Integer> getRequiredTints() { return requiredTints; }

    @Override
    public DynamicProperties<Block, OreMaterial> getDynamicProperties()
    {
        return dynamicProperties;
    }

    @Override
    public MiningLevel getMiningLevel()
    {
        return requiredMiningLevel;
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item ingot = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (ingot == null)
            return new HashMap<>();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        recipes.put(getRegistryName(),
                helper.shapedBars(this.asItem(), ingot));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(getRegistryName());
    }

    public static final String[] BARS_MODELS = {
            "post",
            "post_ends",
            "side",
            "side_alt",
            "cap",
            "cap_alt"
    };

    @Override
    public String getBlockStateString()
    {
        String name = dynamicProperties.registryName();

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

    @Override
    public JsonObject generateModel(String suffix)
    {
        JsonObject textures = new JsonObject();
        String texture = requiredTextures.getFirst().getValue();
        textures.addProperty("bars", "%s:block/%s".formatted(Fortuna.MOD_ID, texture));
        textures.addProperty("edge", "%s:block/%s".formatted(Fortuna.MOD_ID, texture));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "%s:block/tinted_template_bars_%s".formatted(Fortuna.MOD_ID, suffix));
        model.add("textures", textures);

        return model;
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
}
