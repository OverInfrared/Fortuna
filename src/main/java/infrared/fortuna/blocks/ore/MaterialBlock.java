package infrared.fortuna.blocks.ore;

import infrared.fortuna.blocks.FortunaBlock;
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

        requiredMiningLevel = oreMaterial.getMiningLevel();

        MaterialOreBlock block = oreMaterial.getMaterialBlock();

        String blockTexture = block.getTexture();

        addRequiredTexture("particle",blockTexture);
        addOverlayTexture("overlay", blockTexture, 0);

        addRequiredTint(oreMaterial.getColor().getRGB());
    }

public MaterialBlock(FortunaProperties<Block> fortunaProps, Properties properties, OreMaterial oreMaterial, WeatheringCopper.WeatherState weatherState)
{
    super(fortunaProps, properties);

    requiredMiningLevel = oreMaterial.getMiningLevel();

    MaterialOreBlock block = oreMaterial.getMaterialBlock();
    String blockTexture = block.getTexture();
    String aloneName = blockTexture.replace("_block", "");

    if (weatherState == null || weatherState == WeatheringCopper.WeatherState.UNAFFECTED) {
        addRequiredTexture("particle", blockTexture);
        addOverlayTexture("overlay", blockTexture, 0);
        addRequiredTint(oreMaterial.getColor().getRGB());
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
            addRequiredTint(oreMaterial.getTransitionColor(0.2f, 0.5f, 1f).getRGB());
            addRequiredTint(oreMaterial.getTransitionColor(0.8f, 0.5f, 1f).getRGB());
            addRequiredTint(oreMaterial.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
        }
        case WEATHERED -> {
            addRequiredTexture("particle", blockTexture);
            addOverlayTexture("overlay", blockTexture, 0);
            addOverlayTexture("overlayoxidized", "weathered_" + aloneName + "_oxidized", 0);
            addOverlayTexture("overlaytransition", "weathered_" + aloneName + "_transition", 1);
            addRequiredTint(oreMaterial.getTransitionColor(0.8f, 0.5f, 1f).getRGB());
            addRequiredTint(oreMaterial.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
        }
        case OXIDIZED -> {
            addRequiredTexture("particle", "oxidized_" + aloneName);
            addOverlayTexture("overlay", "oxidized_" + aloneName, 0);
            addRequiredTint(oreMaterial.getSecondaryColor().getRGB());
        }
    }
}

}
