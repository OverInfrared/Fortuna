package infrared.fortuna.materials.ore;

public enum OreIngot
{
    Copper("copper_ingot"),
    Iron("iron_ingot"),
    Gold("gold_ingot"),
    Netherite("netherite_ingot"),
    Resin("resin_brick");

    private final String texture;

    OreIngot(String texture)
    {
        this.texture = "ingots/" + texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
