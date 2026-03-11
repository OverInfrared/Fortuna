package infrared.fortuna.items;

import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.item.Item;

public class GemItem extends FortunaItem
{
    public GemItem(FortunaProperties<Item> fortunaProps, Properties properties, OreMaterial oreMaterial)
    {
        super(fortunaProps, properties);

        addRequiredTexture(oreMaterial.getMaterialOreGem().getTexture());
        addRequiredTint(oreMaterial.getColor().getRGB());
    }
}
