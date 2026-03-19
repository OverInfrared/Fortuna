package infrared.fortuna.materials.ore;

public enum OreNugget
{
    Iron("iron_nugget"),
    Gold("gold_nugget"),
    Copper("copper_nugget");

    private final String texture;

    OreNugget(String texture)
    {
        this.texture = "nuggets/" + texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
