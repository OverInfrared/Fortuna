package infrared.fortuna;

import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.resources.materials.Material;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;

import java.awt.*;

public class FortunaColorProvider
{
    public static void initializeBlock(FortunaBlock block, Material material)
    {
        BlockColor color = (state, world, pos, tintIndex) -> {
            Fortuna.LOGGER.info("Color requested tintIndex={}", tintIndex);
            return switch (tintIndex)
            {
                case 0 -> material.getBorderColor();
                case 1 -> material.getBottomBorderColor();
                case 2 -> material.getColor();
                case 3 -> material.getSecondaryColor();
                case 4 -> material.getTertiaryColor();
                default -> Color.white.getRGB();
            };
        };

        ColorProviderRegistry.BLOCK.register(color, block);
    }

    public static void initializeMaterial(Material material)
    {
        for (FortunaBlock block : material.getBlocks())
        {
            BlockColor color = (state, world, pos, tintIndex) -> tintIndex == 0 ? 0xFF0000 : -1;
            ColorProviderRegistry.BLOCK.register(color, block);
        }
    }
}
