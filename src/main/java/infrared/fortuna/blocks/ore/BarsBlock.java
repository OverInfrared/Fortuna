package infrared.fortuna.blocks.ore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreBars;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.util.Utilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.*;

public class BarsBlock extends IronBarsBlock implements IBarsBlock, IFortunaRecipe
{
    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    protected MiningLevel requiredMiningLevel;

    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    private final WeatheringCopper.WeatherState weatherState;

    public BarsBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties)
    {
        this(dynamicProperties, properties, null);
    }

    public BarsBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, WeatheringCopper.WeatherState weatherState)
    {
        super(properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
        this.requiredMiningLevel = dynamicProperties.material().getMiningLevel();
        this.weatherState = weatherState;

        OreMaterial material = dynamicProperties.material();
        OreBars bars = material.getBars();
        String texture = bars.getTexture();

        if (weatherState == null)
        {
            addOverlayTexture("bars", texture, 0);
            addOverlayTexture("bars1", texture + "_white", 1);
            addOverlayTexture("bars2", texture + "_light", 2);
            addOverlayTexture("bars3", texture + "_dark", 3);
            addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
            addRequiredTint(dynamicProperties.material().getColor("main_white").getRGB());
            addRequiredTint(dynamicProperties.material().getColor("main_light").getRGB());
            addRequiredTint(dynamicProperties.material().getColor("main_shift0").getRGB());
        }
        else
        {
            // Delegate to WeatheringMaterialBlock's logic by reusing the same switch
            setupWeatheredTextures(texture, weatherState);
        }

    }

    private void setupWeatheredTextures(String blockTexture, WeatheringCopper.WeatherState weatherState)
    {
        switch (weatherState) {
            case UNAFFECTED -> {
                addOverlayTexture("overlay", blockTexture, 0);
                addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
            }
            case EXPOSED -> {
                addOverlayTexture("overlay", blockTexture, 0);
                addOverlayTexture("overlaybase", "exposed_" + blockTexture + "_base", 0);
                addOverlayTexture("overlayoxidized", "exposed_" + blockTexture + "_oxidized", 1);
                addOverlayTexture("overlaytransition", "exposed_" + blockTexture + "_transition", 2);
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.2f, 0.5f, 1f).getRGB());
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.8f, 0.5f, 1f).getRGB());
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.5f, 0.5f, 1f).getRGB());
            }
            case WEATHERED -> {
                addOverlayTexture("overlay", blockTexture, 0);
                addOverlayTexture("overlayoxidized", "weathered_" + blockTexture + "_oxidized", 0);
                addOverlayTexture("overlaytransition", "weathered_" + blockTexture + "_transition", 1);
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.8f, 0.5f, 1f).getRGB());
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.5f, 0.5f, 1f).getRGB());
            }
            case OXIDIZED -> {
                addRequiredTexture("particle", "oxidized_" + blockTexture);
                addOverlayTexture("overlay", "oxidized_" + blockTexture, 0);
                addRequiredTint(dynamicProperties.material().getSecondaryColor().getRGB());
            }
        }
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
        if (weatherState != WeatheringCopper.WeatherState.UNAFFECTED && weatherState != null)
            return new HashMap<>();

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
        if (weatherState != WeatheringCopper.WeatherState.UNAFFECTED && weatherState != null)
            return Collections.emptySet();

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
