package infrared.fortuna.blocks.ore;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.util.Utilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.*;

public class FortunaDoorBlock extends DoorBlock implements IDoorBlock, IFortunaRecipe
{
    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    protected MiningLevel requiredMiningLevel;

    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    private final WeatheringCopper.WeatherState weatherState;
    private final String doorTexture;

    public FortunaDoorBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, BlockSetType blockSetType)
    {
        this(dynamicProperties, properties, blockSetType, null);
    }

    public FortunaDoorBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, BlockSetType blockSetType, WeatheringCopper.WeatherState weatherState)
    {
        super(blockSetType, properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
        this.requiredMiningLevel = dynamicProperties.material().getMiningLevel();
        this.weatherState = weatherState;
        this.doorTexture = dynamicProperties.material().getDoor().getTexture();

        setupTextures(doorTexture, weatherState);
    }

    private void setupWeatheredTextures(String topTexture, String bottomTexture, WeatheringCopper.WeatherState weatherState)
    {
        OreMaterial material = dynamicProperties.material();

        switch (weatherState)
        {
            case UNAFFECTED ->
            {
                addOverlayTexture("overlay_top", topTexture + "_overlay", -1);
                addOverlayTexture("overlay_bottom", bottomTexture + "_overlay", -1);
                addOverlayTexture("top", topTexture + "_neutral", 0);
                addOverlayTexture("bottom", bottomTexture+ "_neutral", 0);
                addOverlayTexture("top_white", topTexture + "_white", 1);
                addOverlayTexture("bottom_white", bottomTexture+ "_white", 1);
                addOverlayTexture("top_light", topTexture + "_light", 2);
                addOverlayTexture("bottom_light", bottomTexture+ "_light", 2);
                addOverlayTexture("top_dark", topTexture + "_dark", 3);
                addOverlayTexture("bottom_dark", bottomTexture+ "_dark", 3);
                addRequiredTint(material.getMainColor().getRGB());
                addRequiredTint(material.getColor("main_white").getRGB());
                addRequiredTint(material.getColor("main_light").getRGB());
                addRequiredTint(material.getColor("main_dark").getRGB());
            }
            case EXPOSED ->
            {
                addOverlayTexture("overlay_top", topTexture + "_overlay", -1);
                addOverlayTexture("overlay_bottom", bottomTexture + "_overlay", -1);
                addOverlayTexture("top", "exposed_" + topTexture + "_neutral", 0);
                addOverlayTexture("bottom", "exposed_" + bottomTexture + "_neutral", 0);
                addOverlayTexture("top_white", "exposed_" + topTexture + "_white", 1);
                addOverlayTexture("bottom_white", "exposed_" + bottomTexture + "_white", 1);
                addOverlayTexture("top_light", "exposed_" + topTexture + "_light", 2);
                addOverlayTexture("bottom_light", "exposed_" + bottomTexture + "_light", 2);
                addOverlayTexture("top_oxidized", "exposed_" + topTexture + "_oxidized", 3);
                addOverlayTexture("bottom_oxidized", "exposed_" + bottomTexture + "_oxidized", 3);
                addOverlayTexture("top_transition", "exposed_" + topTexture + "_transition", 4);
                addOverlayTexture("bottom_transition", "exposed_" + bottomTexture + "_transition", 4);
                addRequiredTint(material.getColor("transition_base").getRGB());
                addRequiredTint(material.getColor("transition_base_white").getRGB());
                addRequiredTint(material.getColor("transition_base_light").getRGB());
                addRequiredTint(material.getColor("transition_weathered").getRGB());
                addRequiredTint(material.getColor("transition_exposed").getRGB());
            }
            case WEATHERED ->
            {
                addOverlayTexture("overlay_top", topTexture + "_overlay", -1);
                addOverlayTexture("overlay_bottom", bottomTexture + "_overlay", -1);
                addOverlayTexture("top", "weathered_" + topTexture + "_base", 0);
                addOverlayTexture("bottom", "weathered_" + bottomTexture + "_base", 0);
                addOverlayTexture("top_oxidized", "weathered_" + topTexture + "_oxidized", 1);
                addOverlayTexture("bottom_oxidized", "weathered_" + bottomTexture + "_oxidized", 1);
                addOverlayTexture("top_transition", "weathered_" + topTexture + "_transition", 2);
                addOverlayTexture("bottom_transition", "weathered_" + bottomTexture + "_transition", 2);
                addRequiredTint(material.getColor("transition_base").getRGB());
                addRequiredTint(material.getColor("transition_weathered").getRGB());
                addRequiredTint(material.getColor("transition_exposed").getRGB());
            }
            case OXIDIZED ->
            {
                addOverlayTexture("overlay_top", topTexture + "_overlay", -1);
                addOverlayTexture("overlay_bottom", bottomTexture + "_overlay", -1);
                addOverlayTexture("top", "oxidized_" + topTexture, 0);
                addOverlayTexture("bottom", "oxidized_" + bottomTexture, 0);
                addRequiredTint(material.getSecondaryColor().getRGB());
            }
        }
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
    public JsonObject getLoot(HolderLookup.Provider registries) { return getDoorLoot(); }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        if (weatherState != null && weatherState != WeatheringCopper.WeatherState.UNAFFECTED)
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
        if (weatherState != null && weatherState != WeatheringCopper.WeatherState.UNAFFECTED)
            return Collections.emptySet();

        return Set.of(getRegistryName());
    }
}