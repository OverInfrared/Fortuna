package infrared.fortuna.blocks;

import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class FortunaBlock extends Block
{
    private final String registryName;
    private final Component displayName;
    private final ResourceKey<Block> resourceKey;

    private final JsonObject blockStateJson;
    private final JsonObject modelJson;

    public FortunaBlock(String name, String display, ResourceKey<Block> key, Properties properties)
    {
        super(properties.setId(key));
        registryName = name;
        displayName = Component.literal(display);
        resourceKey = key;

        blockStateJson = generateBlockState();
        modelJson = generateModel();
    }

    public Component getDisplayName()
    {
        return displayName;
    }

    public String getRegistryName()
    {
        return registryName;
    }

    public ResourceKey<Block> getResourceKey()
    {
        return resourceKey;
    }

    public String getBlockStateString()
    {
        return blockStateJson.toString();
    }

    public String getModelString()
    {
        return modelJson.toString();
    }

    private JsonObject generateBlockState()
    {

    }

    private  JsonObject generateModel()
    {
        
    }


}