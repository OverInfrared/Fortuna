package infrared.fortuna.items;

import infrared.fortuna.resources.DynamicProperties;
import infrared.fortuna.resources.materials.OreMaterial;
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
