package infrared.fortuna.blocks;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.resources.FortunaProperties;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public abstract class FortunaBlock extends Block implements IFortunaBlock
{
    protected final FortunaProperties<Block> fortunaProperties;
    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<IFortunaBlock.RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    public FortunaBlock(FortunaProperties<Block> fortunaProps, Properties properties) {
        super(properties.setId(fortunaProps.resourceKey()));
        fortunaProperties = fortunaProps;
    }

    @Override public List<Pair<String, String>> getRequiredTextures() { return requiredTextures; }
    @Override public List<IFortunaBlock.RequiredElement> getRequiredElements() { return requiredElements; }
    @Override public List<Integer> getRequiredTints() { return requiredTints; }
    @Override public FortunaProperties<Block> getFortunaProperties() { return fortunaProperties; }
}