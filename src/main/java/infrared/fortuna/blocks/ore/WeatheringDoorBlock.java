package infrared.fortuna.blocks.ore;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreDoor;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.util.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.awt.*;
import java.util.*;
import java.util.List;

public class WeatheringDoorBlock extends DoorBlock implements IDoorBlock, IFortunaRecipe, WeatheringCopper
{
    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    protected MiningLevel requiredMiningLevel;

    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    private final WeatherState weatherState;
    private final String doorTexture;

    public WeatheringDoorBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, BlockSetType blockSetType, WeatherState weatherState)
    {
        super(blockSetType, properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
        this.requiredMiningLevel = dynamicProperties.material().getMiningLevel();
        this.weatherState = weatherState;
        this.doorTexture = dynamicProperties.material().getDoor().getTexture();

        setupTextures(doorTexture, weatherState);
    }

    @Override
    public List<Pair<String, String>> getRequiredTextures() { return requiredTextures; }

    @Override
    public List<String> getRequiredItemTextures()
    {
        return switch (weatherState)
        {
            case UNAFFECTED -> List.of(doorTexture + "_neutral", doorTexture + "_white", doorTexture + "_light", doorTexture + "_dark", doorTexture + "_overlay");
            case EXPOSED -> List.of("exposed_" + doorTexture + "_base", "exposed_" + doorTexture + "_light", "exposed_" + doorTexture + "_white" , "exposed_" + doorTexture + "_transition", "exposed_" + doorTexture + "_oxidized", doorTexture + "_overlay");
            case WEATHERED -> List.of("weathered_" + doorTexture + "_base", "weathered_" + doorTexture + "_oxidized", "weathered_" + doorTexture + "_transition", doorTexture + "_overlay");
            case OXIDIZED -> List.of("oxidized_" + doorTexture, doorTexture + "_overlay");
            case null -> List.of(doorTexture + "_neutral", doorTexture + "_white", doorTexture + "_light", doorTexture + "_dark");
        };
    }

    @Override
    public List<RequiredElement> getRequiredElements() { return requiredElements; }

    @Override
    public List<Integer> getRequiredTints() { return requiredTints; }

    @Override
    public DynamicProperties<Block, OreMaterial> getDynamicProperties() { return dynamicProperties; }

    @Override
    public MiningLevel getMiningLevel() { return requiredMiningLevel; }

    @Override
    public WeatherState getAge() { return weatherState; }

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
        WeatheringCopper.getNext(state.getBlock()).ifPresent(nextBlock ->
        {
            if (random.nextFloat() < 0.05688889F * getChanceModifier())
            {
                level.setBlockAndUpdate(pos, nextBlock.withPropertiesOf(state));
            }
        });
    }

    @Override
    public JsonObject getLoot(HolderLookup.Provider registries) { return getDoorLoot(); }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        if (weatherState != WeatherState.UNAFFECTED)
            return new HashMap<>();

        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item ingot = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (ingot == null)
            return new HashMap<>();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();
        recipes.put(getRegistryName(), helper.shapedDoor(this.asItem(), ingot));
        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        if (weatherState != WeatherState.UNAFFECTED)
            return Collections.emptySet();

        return Set.of(getRegistryName());
    }
}