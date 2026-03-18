package infrared.fortuna.blocks.ore;

import com.google.gson.JsonObject;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.ore.MaterialOreBlock;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class WeatheringMaterialBlock extends FortunaBlock implements WeatheringCopper, IFortunaRecipe
{
    private final WeatherState weatherState;

    public WeatheringMaterialBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, WeatherState weatherState)
    {
        super(dynamicProperties, properties);
        this.weatherState = weatherState;

        OreMaterial material = dynamicProperties.material();

        this.requiredMiningLevel = material.getMiningLevel();

        MaterialOreBlock block = material.getMaterialBlock();
        String blockTexture = block.getTexture();

        switch (weatherState)
        {
            case UNAFFECTED -> {
                addRequiredTexture("particle",blockTexture);
                addOverlayTexture("overlay", blockTexture, 0);
                addRequiredTint(material.getMainColor().getRGB());
            }
            case EXPOSED -> {
                String aloneName = blockTexture.substring(0, "_block".length());

                addRequiredTexture("particle",blockTexture);
                addOverlayTexture("overlay", blockTexture, 0);
                addOverlayTexture("overlaybase", "exposed_" + aloneName + "_base", 0);
                addOverlayTexture("overlayoxidized", "exposed_" + aloneName + "_oxidized", 1);
                addOverlayTexture("overlaytransition", "exposed_" + aloneName + "_transition", 2);

                addRequiredTint(material.getTransitionColor(0.2f, 0.5f, 1f).getRGB());
                addRequiredTint(material.getTransitionColor(0.8f, 0.5f, 1f).getRGB());
                addRequiredTint(material.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
            }
            case WEATHERED -> {
                String aloneName = blockTexture.substring(0, "_block".length());

                addRequiredTexture("particle",blockTexture);
                addOverlayTexture("overlay", blockTexture, 0);
                addOverlayTexture("overlayoxidized", "weathered_" + aloneName + "_oxidized", 0);
                addOverlayTexture("overlaytransition", "weathered_" + aloneName + "_transition", 1);

                addRequiredTint(material.getTransitionColor(0.8f, 0.5f, 1f).getRGB());
                addRequiredTint(material.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
            }
            case OXIDIZED -> {
                String aloneName = blockTexture.substring(0, "_block".length());

                addRequiredTexture("particle", "oxidized_" + aloneName);
                addOverlayTexture("overlay", "oxidized_" + aloneName, 0);
                addRequiredTint(material.getSecondaryColor().getRGB());
            }
        }
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
        if (weatherState != WeatherState.UNAFFECTED)
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
        if (weatherState != WeatherState.UNAFFECTED)
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
