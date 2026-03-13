package infrared.fortuna.items.ore;

import infrared.fortuna.DynamicProperties;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.materials.OreMaterial;
import infrared.fortuna.util.Utilities;
import net.minecraft.world.item.Item;

import java.awt.*;

public class GemItem extends FortunaItem
{
    public GemItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        Color color = dynamicProperties.material().getMainColor();
        Color whiteColor = dynamicProperties.material().getColor("main_white");
        Color lightColor = dynamicProperties.material().getColor("main_light");
        Color darkColor = dynamicProperties.material().getColor("main_dark");

        addRequiredTexture(dynamicProperties.material().getGem().getTexture() + "_neutral");
        addRequiredTexture(dynamicProperties.material().getGem().getTexture() + "_light");
        addRequiredTexture(dynamicProperties.material().getGem().getTexture() + "_white");
        addRequiredTexture(dynamicProperties.material().getGem().getTexture() + "_dark");
        addRequiredTint(color.getRGB());
        addRequiredTint(lightColor.getRGB());
        addRequiredTint(whiteColor.getRGB());
        addRequiredTint(darkColor.getRGB());
    }
}
