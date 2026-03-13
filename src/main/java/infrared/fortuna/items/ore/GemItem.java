package infrared.fortuna.items.ore;

import infrared.fortuna.DynamicProperties;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.materials.OreMaterial;
import net.minecraft.world.item.Item;

public class GemItem extends FortunaItem
{
    public GemItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        addRequiredTexture(dynamicProperties.material().getGem().getTexture());
        addRequiredTint(dynamicProperties.material().getColor().getRGB());
    }
}
