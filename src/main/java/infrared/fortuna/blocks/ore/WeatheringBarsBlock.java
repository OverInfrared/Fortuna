package infrared.fortuna.blocks.ore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.ore.*;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.util.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class WeatheringBarsBlock extends IronBarsBlock implements IBarsBlock, IFortunaRecipe, WeatheringCopper
{
    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    protected MiningLevel requiredMiningLevel;

    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    private final WeatherState weatherState;

    public WeatheringBarsBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, WeatherState weatherState)
    {
        super(properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
        this.requiredMiningLevel = dynamicProperties.material().getMiningLevel();
        this.weatherState = weatherState;

        OreMaterial material = dynamicProperties.material();
        OreBars bars = material.getBars();
        String texture = bars.getTexture();

        switch (weatherState)
        {
            case UNAFFECTED -> {
                addOverlayTexture("overlay", texture, 0);
                addRequiredTint(material.getMainColor().getRGB());
            }
            case EXPOSED -> {
                addOverlayTexture("overlay", texture, 0);
                addOverlayTexture("overlaybase", "exposed_" + texture + "_base", 0);
                addOverlayTexture("overlayoxidized", "exposed_" + texture + "_oxidized", 1);
                addOverlayTexture("overlaytransition", "exposed_" + texture + "_transition", 2);

                addRequiredTint(material.getTransitionColor(0.2f, 0.5f, 1f).getRGB());
                addRequiredTint(material.getTransitionColor(0.8f, 0.5f, 1f).getRGB());
                addRequiredTint(material.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
            }
            case WEATHERED -> {
                addOverlayTexture("overlay", texture, 0);
                addOverlayTexture("overlayoxidized", "weathered_" + texture + "_oxidized", 0);
                addOverlayTexture("overlaytransition", "weathered_" + texture + "_transition", 1);

                addRequiredTint(material.getTransitionColor(0.8f, 0.5f, 1f).getRGB());
                addRequiredTint(material.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
            }
            case OXIDIZED -> {
                addRequiredTexture("particle", "oxidized_" + texture);
                addOverlayTexture("overlay", "oxidized_" + texture, 0);
                addRequiredTint(material.getSecondaryColor().getRGB());
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
    public WeatherState getAge()
    {
        return weatherState;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public void changeOverTime(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        // Use Fabric's registry instead of vanilla's NEXT_BY_BLOCK
        WeatheringCopper.getNext(state.getBlock()).ifPresent(nextBlock -> {
            if (random.nextFloat() < 0.05688889F * getChanceModifier()) {
                level.setBlockAndUpdate(pos, nextBlock.withPropertiesOf(state));
            }
        });
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
