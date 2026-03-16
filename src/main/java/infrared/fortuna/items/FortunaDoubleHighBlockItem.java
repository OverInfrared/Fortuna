package infrared.fortuna.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class FortunaDoubleHighBlockItem extends DoubleHighBlockItem implements IFortunaItem
{
    private final Component displayName;
    private final String registryName;
    private final List<String> requiredTextures = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    public FortunaDoubleHighBlockItem(Block block, Properties properties, Component displayName, String registryName)
    {
        super(block, properties);
        this.displayName = displayName;
        this.registryName = registryName;

        // Todo allow different textures
        addRequiredTexture("iron_door");
    }

    public void addRequiredTexture(String texture) { requiredTextures.add(texture); }
    public void addRequiredTint(int color) { requiredTints.add(color); }

    @Override
    public Component getName(ItemStack itemStack) { return displayName; }

    @Override
    public Component getDisplayName() { return displayName; }

    @Override
    public String getRegistryName() { return registryName; }

    @Override
    public List<String> getRequiredTextures() { return requiredTextures; }

    @Override
    public List<Integer> getRequiredTints() { return requiredTints; }
}