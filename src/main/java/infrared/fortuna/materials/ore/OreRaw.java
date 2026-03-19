package infrared.fortuna.materials.ore;

public enum OreRaw
{
    Copper("raw_copper", true),
    Gold("raw_gold", false),
    Iron("raw_iron", false);

    private final String texture;

    private final boolean oxidizable;

    OreRaw(String texture, boolean oxidizeable)
    {
        this.texture = texture;
        this.oxidizable = oxidizeable;
    }

    public String getTexture()
    {
        return "raw/" + this.texture;
    }

    public String getName() { return this.texture; }

    public boolean isOxidizable()
    {
        return this.oxidizable;
    }

}
