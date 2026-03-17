package infrared.fortuna.materials;

import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.util.Utilities.WeightedRandom;
import infrared.fortuna.materials.ore.MiningLevel;

import java.util.*;

public class MaterialChain
{

    // Materials created for their respective mining level.
    private final EnumMap<MiningLevel, List<OreMaterial>> chainedMaterials = new EnumMap<>(MiningLevel.class);

    public MaterialChain(long seed)
    {
        Random chainRNG = new Random(seed);

        WeightedRandom<Integer> countRNG = new WeightedRandom<Integer>(chainRNG.nextLong())
                .add(5, 1).add(40, 2).add(40, 3).add(5, 4).add(1, 5);

        // Create materials for that mining level.
        for (MiningLevel level : MiningLevel.values())
        {
            if (level == MiningLevel.Stone)
                continue;

            int materialCount = countRNG.next();

            List<OreMaterial> materials = new ArrayList<>();

            MaterialType lastType = null;
            int streak = 0;

            for (int i = 0; i < materialCount; i++)
            {
                MaterialType chosenType = chooseMaterialRaw(level, lastType, streak, chainRNG);

                OreMaterial newMat = new OreMaterial(chainRNG.nextLong(), level, chosenType);
                materials.add(newMat);

                if (chosenType == lastType)
                {
                    streak++;
                }
                else
                {
                    lastType = chosenType;
                    streak = 1;
                }
            }

            chainedMaterials.put(level, materials);
        }

        // Fuels
        WeightedRandom<Integer> fuelsRNG = new WeightedRandom<Integer>(chainRNG.nextLong())
                .add(10, 1).add(50, 2).add(30, 3).add(3, 4).add(1, 5);

        int fuelCount = fuelsRNG.next();
        List<OreMaterial> materials = new ArrayList<>();
        for (int i = 0; i < fuelCount; i++)
        {
            OreMaterial fuelMaterial = new OreMaterial(chainRNG.nextLong(), MiningLevel.Stone, MaterialType.Fuel);
            materials.add(fuelMaterial);
        }
        chainedMaterials.put(MiningLevel.Stone, materials);
    }

    private MaterialType chooseMaterialRaw(MiningLevel level, MaterialType lastType, int streak, Random rng)
    {
        Map<MaterialType, Integer> weights = new EnumMap<>(MaterialType.class);

        switch (level)
        {
            case Copper -> {
                weights.put(MaterialType.Ingot, 80);
                weights.put(MaterialType.Gem, 20);
            }
            case Iron -> {
                weights.put(MaterialType.Ingot, 65);
                weights.put(MaterialType.Gem, 35);
            }
            case Diamond -> {
                weights.put(MaterialType.Ingot, 35);
                weights.put(MaterialType.Gem, 60);
                weights.put(MaterialType.Special, 5);
            }
            case Netherite -> {
                weights.put(MaterialType.Ingot, 33);
                weights.put(MaterialType.Gem, 33);
                weights.put(MaterialType.Special, 33);
            }
        }

        if (lastType != null && streak > 0)
        {
            int penaltyPerStreak = 8;
            int penalty = penaltyPerStreak * streak;

            int oldWeight = weights.getOrDefault(lastType, 0);
            int newWeight = Math.max(1, oldWeight - penalty);
            int removed = oldWeight - newWeight;

            weights.put(lastType, newWeight);

            List<MaterialType> others = new ArrayList<>();
            for (MaterialType type : weights.keySet())
            {
                if (type != lastType)
                {
                    others.add(type);
                }
            }

            if (!others.isEmpty() && removed > 0)
            {
                int split = removed / others.size();
                int remainder = removed % others.size();

                for (MaterialType type : others)
                {
                    weights.put(type, weights.get(type) + split);
                }

                for (int i = 0; i < remainder; i++)
                {
                    MaterialType type = others.get(i);
                    weights.put(type, weights.get(type) + 1);
                }
            }
        }

        WeightedRandom<MaterialType> random = new WeightedRandom<>(rng.nextLong());
        for (Map.Entry<MaterialType, Integer> entry : weights.entrySet())
        {
            random.add(entry.getValue(), entry.getKey());
        }

        return random.next();
    }

    public List<OreMaterial> getMaterialsAtMiningLevel(MiningLevel level)
    {
        return chainedMaterials.get(level);
    }

}
