package infrared.fortuna.blocks;

import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import infrared.fortuna.resources.FortunaProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public abstract class FortunaBlock extends Block
{
    protected final FortunaProperties<Block> fortunaProperties;

    private JsonObject blockStateJson = null;
    private JsonObject modelJson = null;

    public FortunaBlock(FortunaProperties<Block> fortunaProps, Properties properties)
    {
        super(properties.setId(fortunaProps.resourceKey()));
        fortunaProperties = fortunaProps;
    }

    public Component getDisplayName()
    {
        return fortunaProperties.displayName();
    }

    public String getRegistryName()
    {
        return fortunaProperties.registryName();
    }

    public ResourceKey<Block> getResourceKey()
    {
        return fortunaProperties.resourceKey();
    }

    public String getBlockStateString()
    {
        if (blockStateJson == null)
            blockStateJson = generateBlockState();
        return blockStateJson.toString();
    }

    public String getModelString()
    {
        if (modelJson == null)
            modelJson = generateModel();
        return modelJson.toString();
    }

    protected abstract JsonObject generateBlockState();

    protected abstract JsonObject generateModel();
}