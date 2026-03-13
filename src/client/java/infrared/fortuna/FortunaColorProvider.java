package infrared.fortuna;

import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.materials.Material;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

public class FortunaColorProvider
{
    public static void initializeBlock(IFortunaBlock block, Material material)
    {
        BlockColor color = (state, world, pos, tintIndex) -> {
            return block.getRegisteredTint(tintIndex);
        };

        ColorProviderRegistry.BLOCK.register(color, (Block) block);
    }
}
