package infrared.fortuna.materials.ore;

public enum MaterialOreNugget
{
    Iron("iron_nugget"),
    Gold("gold_nugget"),
    Copper("copper_nugget");

    private final String texture;

    MaterialOreNugget(String texture)
    {
        this.texture = "nuggets/" + texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
