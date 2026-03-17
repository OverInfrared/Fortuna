package infrared.fortuna.materials.ore;

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
        this.texture = "gems/" + texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
