package infrared.fortuna.resources.enums.ore;

public enum MaterialOreRaw
{
    Copper("raw_copper", true),
    Gold("raw_gold", false),
    Iron("raw_iron", false);

    private final String texture;

    private final boolean oxidizable;

    MaterialOreRaw(String texture, boolean oxidizeable)
    {
        this.texture = texture;
        this.oxidizable = oxidizeable;
    }

    public String getTexture()
    {
        return this.texture;
    }

    public boolean isOxidizable()
    {
        return this.oxidizable;
    }

}
