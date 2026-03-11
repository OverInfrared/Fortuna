package infrared.fortuna.blocks;

import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreBlock;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

public class MaterialBlock extends FortunaBlock
{
    public MaterialBlock(FortunaProperties<Block> fortunaProps, Properties properties, OreMaterial oreMaterial)
    {
        super(fortunaProps, properties);

        MaterialOreBlock block = oreMaterial.getMaterialBlock();

        String blockTexture = block.getTexture();

        addRequiredTexture("particle",blockTexture);
        addOverlayTexture("overlay", blockTexture, 0);

        addRequiredTint(oreMaterial.getColor());
    }

public MaterialBlock(FortunaProperties<Block> fortunaProps, Properties properties, OreMaterial oreMaterial, WeatheringCopper.WeatherState weatherState)
{
    super(fortunaProps, properties);

    MaterialOreBlock block = oreMaterial.getMaterialBlock();
    String blockTexture = block.getTexture();
    String aloneName = blockTexture.replace("_block", "");

    if (weatherState == null || weatherState == WeatheringCopper.WeatherState.UNAFFECTED) {
        addRequiredTexture("particle", blockTexture);
        addOverlayTexture("overlay", blockTexture, 0);
        addRequiredTint(oreMaterial.getColor());
    } else {
        // Delegate to WeatheringMaterialBlock's logic by reusing the same switch
        setupWeatheredTextures(blockTexture, aloneName, oreMaterial, weatherState);
    }
}

private void setupWeatheredTextures(String blockTexture, String aloneName, OreMaterial oreMaterial, WeatheringCopper.WeatherState weatherState)
{
    switch (weatherState) {
        case EXPOSED -> {
            addRequiredTexture("particle", blockTexture);
            addOverlayTexture("overlay", blockTexture, 0);
            addOverlayTexture("overlaybase", "exposed_" + aloneName + "_base", 0);
            addOverlayTexture("overlayoxidized", "exposed_" + aloneName + "_oxidized", 1);
            addOverlayTexture("overlaytransition", "exposed_" + aloneName + "_transition", 2);
            addRequiredTint(oreMaterial.getColor());
            addRequiredTint(oreMaterial.getSecondaryColor());
            addRequiredTint(oreMaterial.getTertiaryColor());
        }
        case WEATHERED -> {
            addRequiredTexture("particle", blockTexture);
            addOverlayTexture("overlay", blockTexture, 0);
            addOverlayTexture("overlayoxidized", "weathered_" + aloneName + "_oxidized", 0);
            addOverlayTexture("overlaytransition", "weathered_" + aloneName + "_transition", 1);
            addRequiredTint(oreMaterial.getSecondaryColor());
            addRequiredTint(oreMaterial.getTertiaryColor());
        }
        case OXIDIZED -> {
            addRequiredTexture("particle", "oxidized_" + aloneName);
            addOverlayTexture("overlay", blockTexture, 0);
            addRequiredTint(oreMaterial.getSecondaryColor());
        }
    }
}

}
