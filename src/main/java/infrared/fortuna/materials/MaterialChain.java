package infrared.fortuna.materials;

import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.util.Utilities.WeightedRandom;
import infrared.fortuna.materials.ore.MiningLevel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

public class MaterialChain
{

    // Materials created for their respective mining level.
    private final EnumMap<MiningLevel, List<OreMaterial>> chainedMaterials = new EnumMap<>(MiningLevel.class);

    public MaterialChain(long seed)
    {
        Random chainRNG = new Random(seed);

        WeightedRandom<Integer> countRNG = new WeightedRandom<Integer>(chainRNG.nextLong())
                .add(10, 1).add(50, 2).add(30, 3).add(7, 4).add(3, 5);

        WeightedRandom<Integer> fuelsRNG = new WeightedRandom<Integer>(chainRNG.nextLong())
                .add(10, 1).add(50, 2).add(30, 3).add(3, 4).add(1, 5);

        // Create materials for that mining level.
        for (MiningLevel level : MiningLevel.values())
        {
            int materialCount = countRNG.next();

            List<OreMaterial> materials = new ArrayList<>();

            // Generate those materials.
            for (int i = 0; i < materialCount; i++)
            {
                OreMaterial newMat = new OreMaterial(chainRNG.nextLong(), level);
                materials.add(newMat);
            }

            chainedMaterials.put(level, materials);
        }
    }

    public List<OreMaterial> getMaterialsAtMiningLevel(MiningLevel level)
    {
        return chainedMaterials.get(level);
    }

}
