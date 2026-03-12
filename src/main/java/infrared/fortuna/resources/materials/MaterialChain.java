package infrared.fortuna.resources.materials;

import infrared.fortuna.Utilities.WeightedRandom;
import infrared.fortuna.resources.enums.MiningLevel;

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
                .add(20, 1).add(40, 2).add(15, 3).add(7, 4).add(3, 5);

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
