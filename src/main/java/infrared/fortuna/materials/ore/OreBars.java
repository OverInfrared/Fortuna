package infrared.fortuna.materials.ore;

public enum OreBars
{
    Iron("iron_bars", false),
    Copper("copper_bars", true);

    private final String texture;

    private final boolean oxidizable;

    OreBars(String texture, boolean oxidizable)
    {
        this.texture = texture;
        this.oxidizable = oxidizable;
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
