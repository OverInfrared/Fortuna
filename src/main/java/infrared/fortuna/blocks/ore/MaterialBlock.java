package infrared.fortuna.blocks.ore;

import com.google.gson.JsonObject;
import infrared.fortuna.Utilities;
import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.resources.DynamicProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreBlock;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MaterialBlock extends FortunaBlock implements IFortunaRecipe
{
    public MaterialBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        OreMaterial material = dynamicProperties.material();

        requiredMiningLevel = material.getMiningLevel();

        MaterialOreBlock block = material.getMaterialBlock();

        String blockTexture = block.getTexture();

        addRequiredTexture("particle",blockTexture);
        addOverlayTexture("overlay", blockTexture, 0);

        addRequiredTint(material.getColor().getRGB());
    }

    public MaterialBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, WeatheringCopper.WeatherState weatherState)
    {
        super(dynamicProperties, properties);

        OreMaterial material = dynamicProperties.material();

        requiredMiningLevel = material.getMiningLevel();

        MaterialOreBlock block = material.getMaterialBlock();
        String blockTexture = block.getTexture();
        String aloneName = blockTexture.replace("_block", "");

        if (weatherState == null || weatherState == WeatheringCopper.WeatherState.UNAFFECTED) {
            addRequiredTexture("particle", blockTexture);
            addOverlayTexture("overlay", blockTexture, 0);
            addRequiredTint(material.getColor().getRGB());
        } else {
            // Delegate to WeatheringMaterialBlock's logic by reusing the same switch
            setupWeatheredTextures(blockTexture, aloneName, weatherState);
        }
    }

    private void setupWeatheredTextures(String blockTexture, String aloneName, WeatheringCopper.WeatherState weatherState)
    {
        switch (weatherState) {
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
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item ingot = Utilities.findItem(getDynamicProperties().material().getRefinedRegistryName());
        if (ingot == null)
            return new HashMap<>();

        Item blockItem = this.asItem();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        // 9 ingots -> material block
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
        return Set.of(
                getRegistryName(),
                getRegistryName() + "_unpack"
        );
    }
}
