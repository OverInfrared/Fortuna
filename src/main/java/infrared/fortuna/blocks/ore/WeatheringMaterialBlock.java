package infrared.fortuna.blocks.ore;

import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.resources.DynamicProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreBlock;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringMaterialBlock extends FortunaBlock implements WeatheringCopper
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
                addRequiredTint(material.getColor().getRGB());
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
}
