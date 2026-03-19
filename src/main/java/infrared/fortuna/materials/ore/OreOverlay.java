package infrared.fortuna.materials.ore;

public enum OreOverlay
{
    Iron("overlay/ore_iron", "overlay/ore_iron_border"),
    Copper("overlay/ore_copper_base", "overlay/ore_copper_oxidized", "overlay/ore_copper_transition", "overlay/ore_copper_border"),
    Coal("overlay/ore_coal", "overlay/ore_coal_border"),
    Diamond("overlay/diamond_ore_base", "overlay/diamond_ore_transition", "overlay/diamond_ore_border"),
    Emerald("overlay/ore_emerald_base", "overlay/ore_emerald_transition", "overlay/ore_emerald_border"),
    Gold("overlay/ore_gold", "overlay/ore_gold_border"),
    Lapis("overlay/ore_lapis", "overlay/ore_lapis_border"),
    Redstone("overlay/ore_redstone", "overlay/ore_redstone_border");

    private final String texture;
    private final String secondary;
    private final String tertiary;

    private final String borderTop;
    private final String borderBottom;

    OreOverlay(String texture, String border)
    {
        this.texture = texture;
        this.secondary = "";
        this.tertiary = "";
        this.borderBottom = border + "_bottom";
        this.borderTop = border + "_top";
    }

    OreOverlay(String texture, String secondary, String tertiary, String border)
    {
        this.texture = texture;
        this.secondary = secondary;
        this.tertiary = tertiary;
        this.borderBottom = border + "_bottom";
        this.borderTop = border + "_top";
    }

    OreOverlay(String texture, String secondary, String border)
    {
        this.texture = texture;
        this.secondary = secondary;
        this.tertiary = "";
        this.borderBottom = border + "_bottom";
        this.borderTop = border + "_top";
    }

    public String getTexture()
    {
        return this.texture;
    }

    public String getSecondary()
    {
        return this.secondary;
    }

    public boolean hasSecondary()
    {
        return !this.secondary.isEmpty();
    }

    public boolean hasTertiary()
    {
        return !this.tertiary.isEmpty();
    }

    public String getTertiary()
    {
        return this.tertiary;
    }

    public String getBorderTop()
    {
        return borderTop;
    }

    public String getBorderBottom()
    {
        return borderBottom;
    }
}
