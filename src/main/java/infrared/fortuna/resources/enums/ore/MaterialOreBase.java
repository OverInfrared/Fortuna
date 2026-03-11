package infrared.fortuna.resources.enums.ore;

public enum MaterialOreBase
{
    Stone("bases/stone"),
    Diorite("bases/diorite"),
    Granite("bases/granite"),
    Andesite("bases/andesite"),
    Gravel("bases/gravel"),
    Sand("bases/sand"),
    Tuff("bases/tuff"),
    Netherrack("bases/netherrack");

    private final String texture;

    MaterialOreBase(String texture)
    {
        this.texture = texture;
    }

    public String getTexture()
    {
        return this.texture;
    }
}
