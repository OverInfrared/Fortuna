package infrared.fortuna.items;

import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.item.Item;

public class IngotItem extends FortunaItem
{
    public IngotItem(FortunaProperties<Item> fortunaProps, Properties properties, OreMaterial oreMaterial)
    {
        super(fortunaProps, properties);

        addRequiredTexture(oreMaterial.getMaterialOreIngot().getTexture());
        addRequiredTint(oreMaterial.getColor());
    }
}
