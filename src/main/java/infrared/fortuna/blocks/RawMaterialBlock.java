package infrared.fortuna.blocks;

import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreRaw;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.level.block.Block;

public class RawMaterialBlock extends FortunaBlock
{
    public RawMaterialBlock(FortunaProperties<Block> fortunaProps, Properties properties, OreMaterial oreMaterial)
    {
        super(fortunaProps, properties);

        MaterialOreRaw oreRaw = oreMaterial.getMaterialOreRaw();
        boolean oxidizable = oreRaw.isOxidizable();

        String blockTexture = oreRaw.getTexture() + "_block";

        addRequiredTexture("particle", oxidizable ? blockTexture + "_base" : blockTexture);
        addOverlayTexture("barrier", blockTexture, 0);
        addOverlayTexture("overlay", oxidizable ? blockTexture + "_base" : blockTexture, 0);
        if (oxidizable)
        {
            addOverlayTexture("overlayoxidized", blockTexture + "_oxidized", 1);
            addOverlayTexture("overlaytransition", blockTexture + "_transition", 2);
        }

        addRequiredTint(oreMaterial.getColor());
        addRequiredTint(oreMaterial.getSecondaryColor());
        addRequiredTint(oreMaterial.getTertiaryColor());
    }
}
