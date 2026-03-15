package infrared.fortuna.worldgen;

public enum OreFeatureType
{
    Small,
    Medium,
    Large,
    Buried;

    public String getName()
    {
        return this.name().toLowerCase();
    }
}