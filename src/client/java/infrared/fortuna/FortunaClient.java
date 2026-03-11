package infrared.fortuna;

import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.resources.materials.Material;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;

public class FortunaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (Material material : Fortuna.initializedMaterials) {
			for (IFortunaBlock block : material.getBlocks()) {
				// IFortunaBlock doesn't extend Block, so cast to Block for the render layer
				BlockRenderLayerMap.putBlock((Block) block, ChunkSectionLayer.CUTOUT);
				FortunaColorProvider.initializeBlock(block, material);
			}
		}
	}
}