package infrared.fortuna.blocks;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.resources.FortunaProperties;
import infrared.fortuna.resources.enums.MiningLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public abstract class FortunaBlock extends Block implements IFortunaBlock
{
    protected final FortunaProperties<Block> fortunaProperties;
    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<IFortunaBlock.RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    protected TagKey<Block> requiredTool = BlockTags.MINEABLE_WITH_PICKAXE;
    protected MiningLevel requiredMiningLevel = MiningLevel.Fortuna;
    protected IntProvider xpRange;

    public FortunaBlock(FortunaProperties<Block> fortunaProps, Properties properties) {
        super(properties.setId(fortunaProps.resourceKey()));
        fortunaProperties = fortunaProps;
    }

    @Override public List<Pair<String, String>> getRequiredTextures() { return requiredTextures; }
    @Override public List<IFortunaBlock.RequiredElement> getRequiredElements() { return requiredElements; }
    @Override public List<Integer> getRequiredTints() { return requiredTints; }
    @Override public FortunaProperties<Block> getFortunaProperties() { return fortunaProperties; }
    @Override public MiningLevel getMiningLevel() {
        return requiredMiningLevel;
    }
    @Override public TagKey<Block> getRequiredTool() {
        return requiredTool;
    }
}