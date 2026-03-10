package infrared.fortuna;

import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.resources.materials.Material;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class FortunaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		for (Material material : Fortuna.initializedMaterials) {
			for (FortunaBlock block : material.getBlocks())
			{
				BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
				FortunaColorProvider.initializeBlock(block, material);
			}
		}
	}
}