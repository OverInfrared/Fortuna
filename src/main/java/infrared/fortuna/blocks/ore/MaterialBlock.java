package infrared.fortuna.blocks.ore;

import com.google.gson.JsonObject;
import infrared.fortuna.materials.MaterialType;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.ore.enums.OreBlock;
import infrared.fortuna.materials.ore.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;

import java.util.*;

public class MaterialBlock extends FortunaBlock implements IFortunaRecipe
{
    private final WeatherState weatherState;

    public MaterialBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties)
    {
        this(dynamicProperties, properties, null);
    }

    public MaterialBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, WeatherState weatherState)
    {
        super(dynamicProperties, properties);
        this.weatherState = weatherState;

        OreMaterial material = dynamicProperties.material();

        requiredMiningLevel = material.getMiningLevel();

        OreBlock block = material.getMaterialBlock();
        String blockTexture = block.getTexture();
        String aloneName = blockTexture.replace("_block", "");
        addRequiredTexture("particle", blockTexture);

        if (material.getType() == MaterialType.Fuel)
        {
            addOverlayTexture("overlay0", blockTexture, 0);
            addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
            return;
        }

        if (weatherState == null)
        {
            addOverlayTexture("overlay0", blockTexture + "_neutral", 0);
            addOverlayTexture("overlay1", blockTexture + "_light", 1);
            addOverlayTexture("overlay2", blockTexture + "_white", 2);
            addOverlayTexture("overlay3", blockTexture + "_dark", 3);
            addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
            addRequiredTint(dynamicProperties.material().getColor("main_light").getRGB());
            addRequiredTint(dynamicProperties.material().getColor("main_white").getRGB());
            addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
        }
        else
        {
            // Delegate to WeatheringMaterialBlock's logic by reusing the same switch
            setupWeatheredTextures(blockTexture, aloneName, weatherState);
        }
    }

    private void setupWeatheredTextures(String blockTexture, String aloneName, WeatherState weatherState)
    {
        switch (weatherState) {
            case UNAFFECTED -> {
                addRequiredTexture("particle", blockTexture);
                addOverlayTexture("overlay", blockTexture, 0);
                addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
            }
            case EXPOSED -> {
                addRequiredTexture("particle", blockTexture);
                addOverlayTexture("overlay", blockTexture, 0);
                addOverlayTexture("overlaybase", "exposed_" + aloneName + "_base", 0);
                addOverlayTexture("overlayoxidized", "exposed_" + aloneName + "_oxidized", 1);
                addOverlayTexture("overlaytransition", "exposed_" + aloneName + "_transition", 2);
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.2f, 0.5f, 1f).getRGB());
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.8f, 0.5f, 1f).getRGB());
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.5f, 0.5f, 1f).getRGB());
            }
            case WEATHERED -> {
                addRequiredTexture("particle", blockTexture);
                addOverlayTexture("overlay", blockTexture, 0);
                addOverlayTexture("overlayoxidized", "weathered_" + aloneName + "_oxidized", 0);
                addOverlayTexture("overlaytransition", "weathered_" + aloneName + "_transition", 1);
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.8f, 0.5f, 1f).getRGB());
                addRequiredTint(dynamicProperties.material().getTransitionColor(0.5f, 0.5f, 1f).getRGB());
            }
            case OXIDIZED -> {
                addRequiredTexture("particle", "oxidized_" + aloneName);
                addOverlayTexture("overlay", "oxidized_" + aloneName, 0);
                addRequiredTint(dynamicProperties.material().getSecondaryColor().getRGB());
            }
        }
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        if (weatherState != WeatherState.UNAFFECTED && weatherState != null)
            return new HashMap<>();

        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item ingot = Utilities.findItem(getDynamicProperties().material().getRefinedRegistryName());
        if (ingot == null)
            return new HashMap<>();

        Item blockItem = this.asItem();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        // 9 ingots -> material block, excluding oxidized waxed blocks
        if (!getRegistryName().contains("waxed"))
            recipes.put(getRegistryName(),
                    helper.shapedNineToBlock(blockItem, ingot));

        // material block -> 9 ingots
        recipes.put(getRegistryName() + "_unpack",
                helper.shapelessNineFromBlock(ingot, blockItem));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        if (weatherState != WeatherState.UNAFFECTED && weatherState != null)
            return Collections.emptySet();

        Set<String> recipes = new HashSet<>();
        recipes.add(getRegistryName() + "_unpack");

        if (!getRegistryName().contains("waxed"))
            recipes.add(getRegistryName());

        return recipes;
    }

    @Override
    public List<String> getRequiredItemTextures()
    {
        return List.of();
    }
}
