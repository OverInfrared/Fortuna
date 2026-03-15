package infrared.fortuna.items.ore;

import infrared.fortuna.DynamicProperties;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.materials.ore.OreMaterial;
import net.minecraft.world.item.Item;

public class IngotItem extends FortunaItem
{
    public IngotItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        addRequiredTexture(dynamicProperties.material().getIngot().getTexture());
        addRequiredTint(dynamicProperties.material().getMainColor().getRGB());
    }
}
