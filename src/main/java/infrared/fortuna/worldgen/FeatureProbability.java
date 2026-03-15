package infrared.fortuna.worldgen;

public record FeatureProbability(int count, CountType type)
{
    public enum CountType
    {
        Count,
        Rarity
    }
}
