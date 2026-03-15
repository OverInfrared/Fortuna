package infrared.fortuna.materials.ore;

public enum MaterialOreIngot
{
    Copper("copper_ingot"),
    Iron("iron_ingot"),
    Gold("gold_ingot"),
    Netherite("netherite_ingot"),
    Resin("resin_brick");

    private final String texture;

    MaterialOreIngot(String texture)
    {
        this.texture = texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
