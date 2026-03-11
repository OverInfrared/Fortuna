package infrared.fortuna.blocks.ore;

import infrared.fortuna.Utilities;
import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreBase;
import infrared.fortuna.resources.enums.ore.MaterialOreOverlay;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.awt.*;

public class OreBlock extends FortunaBlock
{
    public OreBlock(FortunaProperties<Block> fortunaProperties, Properties properties, OreMaterial oreMaterial)
    {
        this(fortunaProperties, properties, oreMaterial, oreMaterial.getMaterialOreBase());
    }

    public OreBlock(FortunaProperties<Block> fortunaProperties, Properties properties, OreMaterial oreMaterial, MaterialOreBase base)
    {
        this(fortunaProperties, properties, oreMaterial, base, oreMaterial.getXpRange());
    }

    public OreBlock(FortunaProperties<Block> fortunaProperties, Properties properties, OreMaterial oreMaterial, MaterialOreBase base, IntProvider xpRange) {
        super(fortunaProperties, properties);

        this.requiredTool = base.getRequiredTool();
        this.requiredMiningLevel = oreMaterial.getMiningLevel();
        this.xpRange = xpRange;

        addBaseTextures(base.getTexture());

        MaterialOreOverlay materialOreOverlay = oreMaterial.getMaterialOreOverlay();
        addOverlayTexture("borderbottom", materialOreOverlay.getBorderBottom(), 0);
        addOverlayTexture("bordertop", materialOreOverlay.getBorderTop(), 1);
        addRequiredTint(base.getBottomBorderColor().getRGB());
        addRequiredTint(base.getBorderColor().getRGB());

        addOverlayTexture("overlay", materialOreOverlay.getTexture(), 2);
        addRequiredTint(oreMaterial.getColor().getRGB());

        if (materialOreOverlay.hasSecondary() && materialOreOverlay.hasTertiary())
        {
            addOverlayTexture("overlayoxidized", materialOreOverlay.getSecondary(), 3);
            addOverlayTexture("overlaytransition", materialOreOverlay.getTertiary(), 4);
            addRequiredTint(oreMaterial.getSecondaryColor().getRGB());
            addRequiredTint(oreMaterial.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
        }
        else if (materialOreOverlay.hasSecondary())
        {
            addOverlayTexture("overlaytransition", materialOreOverlay.getSecondary(), 3);
            Color color = Utilities.lerpColor(oreMaterial.getColor(), base.getBottomBorderColor(), 0.2f);
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            color = Color.getHSBColor(hsb[0], hsb[1] * 0.3f, hsb[2]);
            addRequiredTint(color.getRGB());
        }
    }

    @Override
    protected void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl)
    {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
        if (bl && xpRange.getMaxValue() != 0) {
            this.tryDropExperience(serverLevel, blockPos, itemStack, xpRange);
        }
    }
}
