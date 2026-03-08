package infrared.fortuna.resources;

import infrared.fortuna.Utilities;
import infrared.fortuna.resources.enums.MaterialOreBase;
import infrared.fortuna.resources.enums.MaterialOreOverlay;
import infrared.fortuna.resources.enums.MaterialRaw;
import infrared.fortuna.resources.enums.MiningLevel;

import java.util.Random;

public class OreMaterial extends Material
{
    private final MiningLevel miningLevel;
    private final MaterialRaw raw;
    private final MaterialOreBase oreBase;
    private final MaterialOreOverlay oreOverlay;

    public OreMaterial(long seed, MiningLevel level)
    {
        super(seed, level);

        raw = generateMaterialRaw(level, materialRNG);

        // Ore generation randomness.
        miningLevel = level;
        oreBase = chooseOreBase(materialRNG);
        oreOverlay = chooseOreOverlay(materialRNG);
    }

    private MaterialRaw chooseMaterialRaw(MiningLevel level, Random random)
    {
        return switch (level)
        {
            case Stone -> MaterialRaw.Stone;
            case Iron ->
            {
                Utilities.WeightedRandom<MaterialRaw> ironRandom = new Utilities.WeightedRandom<MaterialRaw>(random.nextLong())
                        .add(65, MaterialRaw.Ingot).add(35, MaterialRaw.Gem);
                yield ironRandom.next();
            }
            case Diamond ->
            {
                Utilities.WeightedRandom<MaterialRaw> diamondRandom = new Utilities.WeightedRandom<MaterialRaw>(random.nextLong())
                        .add(35, MaterialRaw.Ingot).add(60, MaterialRaw.Gem).add(5, MaterialRaw.Special);
                yield diamondRandom.next();
            }
            case Netherite, Fortuna ->
            {
                Utilities.WeightedRandom<MaterialRaw> netherRandom = new Utilities.WeightedRandom<MaterialRaw>(random.nextLong())
                        .add(33, MaterialRaw.Ingot).add(33, MaterialRaw.Gem).add(33, MaterialRaw.Special);
                yield netherRandom.next();
            }
            default -> MaterialRaw.Ingot;
        };
    }

    private MaterialOreBase chooseOreBase(Random rng)
    {
        Utilities.WeightedRandom<MaterialOreBase> baseRandom = new Utilities.WeightedRandom<MaterialOreBase>(rng.nextLong())
                .add(50, MaterialOreBase.Stone).add(10, MaterialOreBase.Andesite).add(10, MaterialOreBase.Diorite)
                .add(10, MaterialOreBase.Granite).add(10, MaterialOreBase.Tuff).add(7, MaterialOreBase.Sand).add(7, MaterialOreBase.Gravel);
        return baseRandom.next();
    }

    private MaterialOreOverlay chooseOreOverlay(Random rng)
    {
        Utilities.WeightedRandom<MaterialOreOverlay> overlayRandom = new Utilities.WeightedRandom<MaterialOreOverlay>(rng.nextLong())
                .add(10, MaterialOreOverlay.Iron).add(10, MaterialOreOverlay.Diamond).add(10, MaterialOreOverlay.Coal)
                .add(10, MaterialOreOverlay.Redstone).add(10, MaterialOreOverlay.Emerald).add(10, MaterialOreOverlay.Copper)
                .add(10, MaterialOreOverlay.Gold).add(10, MaterialOreOverlay.Lapis);
        return overlayRandom.next();
    }
}
