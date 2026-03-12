package infrared.fortuna.blocks.ore;

import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.resources.DynamicProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreRaw;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.level.block.Block;

public class RawMaterialBlock extends FortunaBlock
{
    public RawMaterialBlock(DynamicProperties<Block, OreMaterial> fortunaProps, Properties properties)
    {
        super(fortunaProps, properties);

        this.requiredMiningLevel = fortunaProps.material().getMiningLevel();

        MaterialOreRaw oreRaw = fortunaProps.material().getMaterialType();
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

        addRequiredTint(fortunaProps.material().getColor().getRGB());
        addRequiredTint(fortunaProps.material().getSecondaryColor().getRGB());
        addRequiredTint(fortunaProps.material().getTransitionColor(0.5f, 0.5f, 1f).getRGB());
    }
}
