package infrared.fortuna.worldgen;

public class OreSpawnEntry
{
    private final SpawnProfile profile;
    private final int veinSize;
    private final int count;
    private final int rarity;
    private final int minHeight;
    private final int maxHeight;
    private final boolean useTriangleDistribution;

    public OreSpawnEntry(SpawnProfile profile, int veinSize, int count, int rarity, int minHeight, int maxHeight, boolean useTriangleDistribution)
    {
        this.profile = profile;
        this.veinSize = veinSize;
        this.count = count;
        this.rarity = rarity;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.useTriangleDistribution = useTriangleDistribution;
    }

    public SpawnProfile getProfile()           { return profile; }
    public int getVeinSize()                   { return veinSize; }
    public int getCount()                      { return count; }
    public int getRarity()                     { return rarity; }
    public int getMinHeight()                  { return minHeight; }
    public int getMaxHeight()                  { return maxHeight; }
    public boolean usesTriangleDistribution()  { return useTriangleDistribution; }

    /**
     * Whether this entry uses count-per-chunk (common) or 1-in-N-chunks (rare).
     * Count is used when rarity is 0, rarity is used when count is 0.
     */
    public boolean isRare()
    {
        return count == 0;
    }
}