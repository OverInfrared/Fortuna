package infrared.fortuna.resources.enums;

public enum MaterialOreGem
{
    Diamond("diamond"),
    Lapis("lapis_lazuli"),
    Emerald("emerald"),
    Prismarine("prismarine_crystal"),
    Amethyst("amethyst_shard"),
    Resin("resin_clump");

    private final String texture;

    private MaterialOreGem(String texture)
    {
        this.texture = texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
