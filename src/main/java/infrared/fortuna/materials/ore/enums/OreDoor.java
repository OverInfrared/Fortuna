package infrared.fortuna.materials.ore.enums;

public enum OreDoor
{
    Iron("iron_door", false),
    Copper("copper_door", true);

    private final String texture;
    private final boolean oxidizable;

    OreDoor(String texture, boolean oxidizable)
    {
        this.texture = texture;
        this.oxidizable = oxidizable;
    }

    public String getTexture() { return texture; }
    public boolean isOxidizable() { return oxidizable; }
}