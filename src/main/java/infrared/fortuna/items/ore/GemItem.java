package infrared.fortuna.items.ore;

import infrared.fortuna.DynamicProperties;
import infrared.fortuna.items.FortunaItem;
import infrared.fortuna.materials.MaterialType;
import infrared.fortuna.materials.ore.OreMaterial;
import net.minecraft.world.item.Item;

import java.awt.*;

public class GemItem extends FortunaItem
{
    public GemItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        Color color = dynamicProperties.material().getMainColor();
        if (dynamicProperties.material().getType() == MaterialType.Fuel)
        {
            addRequiredTexture(dynamicProperties.material().getFuel().getTexture());
            addRequiredTint(color.getRGB());
            return;
        }

        Color whiteColor = dynamicProperties.material().getColor("main_white");
        Color lightColor = dynamicProperties.material().getColor("main_light");
        Color darkColor = dynamicProperties.material().getColor("main_dark");

        addRequiredTexture(dynamicProperties.material().getGem().getTexture() + "_neutral");
        addRequiredTint(color.getRGB());
        addRequiredTexture(dynamicProperties.material().getGem().getTexture() + "_light");
        addRequiredTexture(dynamicProperties.material().getGem().getTexture() + "_white");
        addRequiredTexture(dynamicProperties.material().getGem().getTexture() + "_dark");
        addRequiredTint(lightColor.getRGB());
        addRequiredTint(whiteColor.getRGB());
        addRequiredTint(darkColor.getRGB());
    }
}
