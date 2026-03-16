package infrared.fortuna.worldgen;

import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.Material;
import infrared.fortuna.materials.ore.OreMaterial;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public class FortunaBiomeModifications
{
    public static void registerBiomeModifications()
    {
        for (Material mat : Fortuna.initializedMaterials)
        {
            if (!(mat instanceof OreMaterial oreMat))
                continue;

            List<OrePlacedFeature> features = oreMat.getPlacedFeatures();
            for (int i = 0; i < features.size(); i++)
            {
                ResourceKey<PlacedFeature> key = ResourceKey.create(
                        Registries.PLACED_FEATURE,
                        Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, "%s_ore_%d".formatted(oreMat.getName(), i))
                );

                Fortuna.LOGGER.info("Registering placed feature: {}", key.identifier());

                BiomeModifications.addFeature(
                        BiomeSelectors.foundInOverworld(),
                        GenerationStep.Decoration.UNDERGROUND_ORES,
                        key
                );
            }
        }

        disableVanillaOres();
    }

    public static void disableVanillaOres()
    {
        BiomeModifications.create(Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, "remove_vanilla_ores"))
                .add(ModificationPhase.REMOVALS, BiomeSelectors.foundInOverworld(), context ->
                {
                    var settings = context.getGenerationSettings();

                    // Iron
                    settings.removeFeature(OrePlacements.ORE_IRON_UPPER);
                    settings.removeFeature(OrePlacements.ORE_IRON_MIDDLE);
                    settings.removeFeature(OrePlacements.ORE_IRON_SMALL);

                    // Gold
                    settings.removeFeature(OrePlacements.ORE_GOLD);
                    settings.removeFeature(OrePlacements.ORE_GOLD_EXTRA);
                    settings.removeFeature(OrePlacements.ORE_GOLD_LOWER);

                    // Diamond
                    settings.removeFeature(OrePlacements.ORE_DIAMOND);
                    settings.removeFeature(OrePlacements.ORE_DIAMOND_MEDIUM);
                    settings.removeFeature(OrePlacements.ORE_DIAMOND_LARGE);
                    settings.removeFeature(OrePlacements.ORE_DIAMOND_BURIED);

                    // Copper
                    settings.removeFeature(OrePlacements.ORE_COPPER);
                    settings.removeFeature(OrePlacements.ORE_COPPER_LARGE);

                    // Coal
                    settings.removeFeature(OrePlacements.ORE_COAL_UPPER);
                    settings.removeFeature(OrePlacements.ORE_COAL_LOWER);

                    // Redstone
                    settings.removeFeature(OrePlacements.ORE_REDSTONE);
                    settings.removeFeature(OrePlacements.ORE_REDSTONE_LOWER);

                    // Lapis
                    settings.removeFeature(OrePlacements.ORE_LAPIS);
                    settings.removeFeature(OrePlacements.ORE_LAPIS_BURIED);

                    // Emerald
                    settings.removeFeature(OrePlacements.ORE_EMERALD);
                });
    }

}
