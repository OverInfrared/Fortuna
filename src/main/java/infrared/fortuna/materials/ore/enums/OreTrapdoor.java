package infrared.fortuna.materials.ore.enums;

public enum OreTrapdoor
{
    Iron("iron_trapdoor", false),
    Copper("copper_trapdoor", true);

    private final String texture;
    private final boolean oxidizable;

    OreTrapdoor(String texture, boolean oxidizable)
    {
        this.texture = texture;
        this.oxidizable = oxidizable;
    }

    public String getTexture() { return texture; }
    public boolean isOxidizable() { return oxidizable; }
}