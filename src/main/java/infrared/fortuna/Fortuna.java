package infrared.fortuna;

import infrared.fortuna.blocks.ModBlocks;
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

import java.util.ArrayList;
import java.util.List;

public class Fortuna implements ModInitializer
{
	public static final String MOD_ID = "fortuna";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final List<Material> initializedMaterials = new ArrayList<>();

	public static boolean disableVanillaOreVeins = true;

	@Override
	public void onInitialize()
	{
		// cool seed 3325432789787890912L / 547584395473L
		long seed = 1906548695489752389L;

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
			Fortuna.LOGGER.info("=== {} tier: {} materials ===", level, materials.size());
			for (OreMaterial material : materials)
			{
				Fortuna.LOGGER.info("  {} | base: {} | type: {}", material.getName(), material.getBase(), material.getType());

				ModItems.initializeOreMaterial(material);
				ModBlocks.initializeOreMaterial(material);

				initializedMaterials.add(material);
			}
		}

		FortunaBiomeModifications.registerBiomeModifications();

		VanillaReplacementMap.initialize(seed);
		LootTableReplacer.register();
	}

	public static OreMaterial findMaterial(String name)
	{
		for (Material mat : initializedMaterials)
			if (mat instanceof OreMaterial oreMat && oreMat.getName().equals(name))
				return oreMat;
		return null;
	}
}