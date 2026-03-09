package infrared.fortuna;

import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.blocks.ModBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

@SuppressWarnings({"UnusedDeclaration", "unused"})

public class FortunaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (FortunaBlock block : ModBlock.getRegisteredBlocks()) {
			BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
		}
	}
}