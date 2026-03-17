package infrared.fortuna.items.ore;

import infrared.fortuna.DynamicProperties;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.materials.ore.OreMaterial;
import net.minecraft.world.item.Item;

import java.awt.*;

public class IngotItem extends FortunaItem
{
    public IngotItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        Color color = dynamicProperties.material().getMainColor();
        Color whiteColor = dynamicProperties.material().getColor("main_white");
        Color lightColor = dynamicProperties.material().getColor("main_light");

        addRequiredTexture(dynamicProperties.material().getIngot().getTexture() + "_neutral");
        addRequiredTexture(dynamicProperties.material().getIngot().getTexture() + "_light");
        addRequiredTexture(dynamicProperties.material().getIngot().getTexture() + "_white");
        addRequiredTexture(dynamicProperties.material().getIngot().getTexture() + "_dark");
        addRequiredTint(color.getRGB());
        addRequiredTint(lightColor.getRGB());
        addRequiredTint(whiteColor.getRGB());
        addRequiredTint(color.getRGB());
    }
}
