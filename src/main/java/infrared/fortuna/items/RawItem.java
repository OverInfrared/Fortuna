package infrared.fortuna.items;

import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreRaw;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.item.Item;

public class RawItem extends FortunaItem
{
    public RawItem(FortunaProperties<Item> fortunaProps, Properties properties, OreMaterial oreMaterial)
    {
        super(fortunaProps, properties);

        MaterialOreRaw rawOre = oreMaterial.getMaterialOreRaw();

        addRequiredTexture(rawOre.isOxidizable() ? rawOre.getTexture() + "_base" : rawOre.getTexture());
        addRequiredTint(oreMaterial.getColor().getRGB());

        if (rawOre.isOxidizable())
        {
            addRequiredTexture(rawOre.getTexture() + "_oxidized");
            addRequiredTint(oreMaterial.getSecondaryColor().getRGB());
            addRequiredTexture(rawOre.getTexture() + "_transition");
            addRequiredTint(oreMaterial.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
        }
    }
}
