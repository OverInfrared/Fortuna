package infrared.fortuna.items;

import infrared.fortuna.resources.DynamicProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreRaw;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.world.item.Item;

public class RawItem extends FortunaItem
{
    public RawItem(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties)
    {
        super(dynamicProperties, properties);

        OreMaterial material = dynamicProperties.material();
        MaterialOreRaw rawOre = material.getRaw();

        addRequiredTexture(rawOre.isOxidizable() ? rawOre.getTexture() + "_base" : rawOre.getTexture());
        addRequiredTint(material.getColor().getRGB());

        if (rawOre.isOxidizable())
        {
            addRequiredTexture(rawOre.getTexture() + "_oxidized");
            addRequiredTint(material.getSecondaryColor().getRGB());
            addRequiredTexture(rawOre.getTexture() + "_transition");
            addRequiredTint(material.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
        }
    }
}
