package infrared.fortuna.materials.ore;

public enum MaterialOreFuel
{
    Coal("coal"),
    Charcoal("charcoal"),
    Lapis("lapis_lazuli"),
    Resin("resin_clump");

    private final String texture;

    MaterialOreFuel(String texture)
    {
        this.texture = "fuels/" + texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
