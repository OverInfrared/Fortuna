package infrared.fortuna;

import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.resources.materials.Material;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

import java.awt.*;

public class FortunaColorProvider
{
    public static void initializeBlock(IFortunaBlock block, Material material)
    {
        BlockColor color = (state, world, pos, tintIndex) -> {
            Fortuna.LOGGER.info("Color requested tintIndex={}", tintIndex);
            return block.getRegisteredTint(tintIndex);
        };

        ColorProviderRegistry.BLOCK.register(color, (Block) block);
    }
}
