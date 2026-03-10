package infrared.fortuna.resources.enums;

public enum MaterialOreRaw
{
    Copper("raw_copper_base", "raw_copper_oxidized", "raw_copper_transition"),
    Gold("raw_gold"),
    Iron("raw_iron");

    private final String texture;
    private final String secondary;
    private final String tertiary;

    private MaterialOreRaw(String texture)
    {
        this.texture = texture;
        this.secondary = "";
        this.tertiary = "";
    }

    private MaterialOreRaw(String texture, String secondary, String tertiary)
    {
        this.texture = texture;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    public String getTexture()
    {
        return this.texture;
    }

    public String getSecondary()
    {
        return this.secondary;
    }

    public String getTertiary()
    {
        return this.tertiary;
    }

}
