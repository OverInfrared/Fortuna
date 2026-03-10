package infrared.fortuna.items;

import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.MaterialOreRaw;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.item.Item;

public class RawItem extends FortunaItem
{
    public RawItem(FortunaProperties<Item> fortunaProps, Properties properties, OreMaterial oreMaterial)
    {
        super(fortunaProps, properties);

        MaterialOreRaw rawOre = oreMaterial.getMaterialOreRaw();

        addRequiredTexture(rawOre.getTexture());
        addRequiredTint(oreMaterial.getColor());

        if (!rawOre.getSecondary().isEmpty())
        {
            addRequiredTexture(rawOre.getSecondary());
            addRequiredTint(oreMaterial.getSecondaryColor());
        }
        if (!rawOre.getTertiary().isEmpty())
        {
            addRequiredTexture(rawOre.getTertiary());
            addRequiredTint(oreMaterial.getTertiaryColor());
        }
    }
}
