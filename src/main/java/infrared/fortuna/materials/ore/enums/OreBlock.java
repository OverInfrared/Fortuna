package infrared.fortuna.materials.ore.enums;

public enum OreBlock
{
    Copper("copper_block", true),
    Iron("iron_block", false),
    Diamond("diamond_block", false),
    Emerald("emerald_block", false),
    Gold("gold_block", false),
    Lapis("lapis_block", false),
    Netherite("netherite_block", false),
    Amethyst("amethyst_block", false),
    Coal("coal_block", false),
    Resin("resin_block", false);

    private final String texture;

    private final boolean oxidizable;

    OreBlock(String texture, boolean oxidizable)
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
