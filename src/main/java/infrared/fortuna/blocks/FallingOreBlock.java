package infrared.fortuna.blocks;

import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreOverlay;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.level.block.Block;

public class FallingOreBlock extends FallingFortunaBlock
{
    public FallingOreBlock(FortunaProperties<Block> fortunaProps, Properties properties, OreMaterial oreMaterial)
    {
        super(fortunaProps, oreMaterial.getColorRGBA(), properties);

        addBaseTextures(oreMaterial.getMaterialOreBase().getTexture());

        MaterialOreOverlay materialOreOverlay = oreMaterial.getMaterialOreOverlay();
        addOverlayTexture("borderbottom", materialOreOverlay.getBorderBottom(), 0);
        addOverlayTexture("bordertop", materialOreOverlay.getBorderTop(), 1);

        addOverlayTexture("overlay", materialOreOverlay.getTexture(), 2);
        if (!materialOreOverlay.getSecondary().isEmpty())
            addOverlayTexture("overlayoxidized", materialOreOverlay.getSecondary(), 3);
        if (!materialOreOverlay.getTertiary().isEmpty())
            addOverlayTexture("overlaytransition", materialOreOverlay.getTertiary(), 4);

        addRequiredTint(oreMaterial.getBorderColor());
        addRequiredTint(oreMaterial.getBottomBorderColor());
        addRequiredTint(oreMaterial.getColor());
        addRequiredTint(oreMaterial.getSecondaryColor());
        addRequiredTint(oreMaterial.getTertiaryColor());

    }
}
