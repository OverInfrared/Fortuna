package infrared.fortuna;

import infrared.fortuna.blocks.ModBlocks;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.resources.FortunaDataPack;
import infrared.fortuna.resources.FortunaResourcePack;
import infrared.fortuna.resources.materials.Material;
import infrared.fortuna.resources.materials.MaterialChain;
import infrared.fortuna.resources.enums.MiningLevel;
import infrared.fortuna.resources.materials.OreMaterial;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Fortuna implements ModInitializer
{
	public static final String MOD_ID = "fortuna";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final List<Material> initializedMaterials = new ArrayList<>();

	@Override
	public void onInitialize()
	{
		// cool seed 321312312
		// cool copper seed 453294812
		long seed = 375671235465452312L;

		LOGGER.info("Starting generation for Overworld");
		initializeOverworld(seed);

		LOGGER.info("Initializing CreativeMoveTab");
		ModItems.initializeCreativeModeTab();
	}

	private void initializeOverworld(long seed)
	{
		// Ore and material item creation.
		MaterialChain chain = new MaterialChain(seed);

		// Materials are stored in their mining level order.
		for (MiningLevel level : MiningLevel.values())
		{
			List<OreMaterial> materials = chain.getMaterialsAtMiningLevel(level);
			for (OreMaterial material : materials)
			{
				ModItems.initializeOreMaterial(material);
				ModBlocks.initializeOreMaterial(material);

				initializedMaterials.add(material);
			}
		}
	}
}