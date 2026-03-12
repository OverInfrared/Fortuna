package infrared.fortuna.blocks.ore;

import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.resources.DynamicProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreBlock;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

public class MaterialBlock extends FortunaBlock
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

}
