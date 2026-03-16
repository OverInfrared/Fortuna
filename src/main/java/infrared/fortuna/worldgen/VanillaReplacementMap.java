package infrared.fortuna.worldgen;

import infrared.fortuna.Fortuna;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;

import java.util.EnumMap;
import java.util.List;
import java.util.Random;

public class VanillaReplacementMap
{
    private static final EnumMap<MiningLevel, OreMaterial> replacements = new EnumMap<>(MiningLevel.class);

    public static void initialize(long seed)
    {
        Random rng = new Random(seed);

        for (MiningLevel level : MiningLevel.values())
        {
            List<OreMaterial> candidates = Fortuna.initializedMaterials.stream()
                    .filter(mat -> mat instanceof OreMaterial oreMat && oreMat.getMiningLevel() == level)
                    .map(mat -> (OreMaterial) mat)
                    .toList();

            if (!candidates.isEmpty())
                replacements.put(level, candidates.get(rng.nextInt(candidates.size())));
        }
    }

    public static OreMaterial getReplacement(MiningLevel level)
    {
        return replacements.get(level);
    }
}