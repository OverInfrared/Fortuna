package infrared.fortuna.enums.ore;

public enum MaterialOreGem
{
    Diamond("diamond"),
    Lapis("lapis_lazuli"),
    Emerald("emerald"),
    Prismarine("prismarine_crystals"),
    Amethyst("amethyst_shard"),
    Resin("resin_clump");

    private final String texture;

    MaterialOreGem(String texture)
    {
        this.texture = texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
