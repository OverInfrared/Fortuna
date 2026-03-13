package infrared.fortuna;

import infrared.fortuna.blocks.ModBlocks;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.materials.Material;
import infrared.fortuna.materials.MaterialChain;
import infrared.fortuna.enums.MiningLevel;
import infrared.fortuna.materials.OreMaterial;
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
		long seed = 3325432780980899012L;

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

	public static OreMaterial findMaterial(String name)
	{
		for (Material mat : initializedMaterials)
			if (mat instanceof OreMaterial oreMat && oreMat.getName().equals(name))
				return oreMat;
		return null;
	}
}