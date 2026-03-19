package infrared.fortuna;

import infrared.fortuna.blocks.ModBlocks;
import infrared.fortuna.config.FortunaConfig;
import infrared.fortuna.worldgen.LootTableReplacer;
import infrared.fortuna.items.ModItems;
import infrared.fortuna.materials.Material;
import infrared.fortuna.materials.MaterialChain;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.worldgen.FortunaBiomeModifications;
import infrared.fortuna.worldgen.VanillaReplacementMap;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fortuna implements ModInitializer
{
	public static final String MOD_ID = "fortuna";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<String, Material> initializedMaterials = new HashMap<>();

	public static boolean disableVanillaOreVeins = true;

	public static final FortunaConfig CONFIG = FortunaConfig.createAndLoad();

	@Override
	public void onInitialize()
	{
		LOGGER.info("Starting generation for Overworld");
		initializeOverworld(CONFIG.seed());

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
			Fortuna.LOGGER.info("=== {} tier: {} materials ===", level, materials.size());
			for (OreMaterial material : materials)
			{
				Fortuna.LOGGER.info("  {} | base: {} | type: {}", material.getName(), material.getBase(), material.getType());

				ModItems.initializeOreMaterial(material);
				ModBlocks.initializeOreMaterial(material);

				initializedMaterials.put(material.getName(), material);
			}
		}

		FortunaBiomeModifications.registerBiomeModifications();

		VanillaReplacementMap.initialize(seed);
		LootTableReplacer.register();
	}

	public static Material getMaterial(String name)
	{
		if (initializedMaterials.containsKey(name))
			return initializedMaterials.get(name);

		return null;
	}
}