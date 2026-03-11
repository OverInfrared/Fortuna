package infrared.fortuna.resources.enums.ore;

public enum MaterialOreBlock
{
    Copper("copper_block", true),
    Iron("iron_block", false),
    Diamond("diamond_block", false),
    Emerald("emerald_block", false),
    Gold("gold_block", false),
    Lapis("lapis_block", false),
    Netherite("netherite_block", false),
    Amethyst("amethyst_block", false);

    private final String texture;

    private final boolean oxidizable;

    MaterialOreBlock(String texture, boolean oxidizable)
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
