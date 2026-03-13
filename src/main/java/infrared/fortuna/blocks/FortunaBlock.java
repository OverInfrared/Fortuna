package infrared.fortuna.blocks;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.enums.MiningLevel;
import infrared.fortuna.materials.OreMaterial;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public abstract class FortunaBlock extends Block implements IFortunaBlock
{
    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<IFortunaBlock.RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    protected TagKey<Block> requiredTool = BlockTags.MINEABLE_WITH_PICKAXE;
    protected MiningLevel requiredMiningLevel = MiningLevel.Iron;
    protected IntProvider xpRange;

    public FortunaBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties)
    {
        super(properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
    }

    @Override public List<Pair<String, String>>            getRequiredTextures()  { return requiredTextures; }
    @Override public List<IFortunaBlock.RequiredElement>   getRequiredElements()  { return requiredElements; }
    @Override public List<Integer>                         getRequiredTints()     { return requiredTints; }
    @Override public DynamicProperties<Block, OreMaterial> getDynamicProperties() { return dynamicProperties; }
    @Override public MiningLevel                           getMiningLevel()       { return requiredMiningLevel; }
    @Override public TagKey<Block>                         getRequiredTool()      { return requiredTool; }
}