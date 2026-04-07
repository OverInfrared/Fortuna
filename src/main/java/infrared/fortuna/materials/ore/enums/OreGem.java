package infrared.fortuna.materials.ore.enums;

public enum OreGem
{
    Diamond("diamond"),
    Lapis("lapis_lazuli"),
    Emerald("emerald"),
    Prismarine("prismarine_crystals"),
    Amethyst("amethyst_shard"),
    Resin("resin_clump");

    private final String texture;

    OreGem(String texture)
    {
        this.texture = "gems/" + texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
