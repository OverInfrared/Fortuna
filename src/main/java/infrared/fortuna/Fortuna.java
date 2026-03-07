package infrared.fortuna;

import infrared.fortuna.blocks.ModBlock;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.resources.Material;
import infrared.fortuna.resources.MaterialChain;
import infrared.fortuna.resources.enums.MiningLevel;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Fortuna implements ModInitializer
{
	public static final String MOD_ID = "fortuna";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		long seed = 101L;

		initializeOverworld(seed);
		ModItems.initializeCreatureModeTab();
	}

	private void initializeOverworld(long seed)
	{
		// Ore and material item creation.
		MaterialChain chain = new MaterialChain(seed);

		// Materials are stored in their mining level order.
		for (MiningLevel level : MiningLevel.values())
		{
			List<Material> materials = chain.getMaterialsAtMiningLevel(level);
			for (Material material : materials)
			{
				ModItems.initializeMaterial(material);
				ModBlock.initializeMaterial(material);
			}
		}
	}
}