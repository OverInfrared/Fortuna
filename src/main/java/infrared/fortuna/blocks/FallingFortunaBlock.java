package infrared.fortuna.blocks;

import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.mojang.serialization.MapCodec;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.materials.ore.MiningLevel;
import infrared.fortuna.materials.ore.OreMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.sounds.AmbientDesertBlockSoundsPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public abstract class FallingFortunaBlock extends ColoredFallingBlock implements IFortunaBlock
{
    public static final MapCodec<FallingFortunaBlock> CODEC = MapCodec.unit(() -> null);

    protected final DynamicProperties<Block, OreMaterial> dynamicProperties;
    private final List<Pair<String, String>> requiredTextures = new ArrayList<>();
    private final List<IFortunaBlock.RequiredElement> requiredElements = new ArrayList<>();
    private final List<Integer> requiredTints = new ArrayList<>();

    protected TagKey<Block> requiredTool = BlockTags.MINEABLE_WITH_PICKAXE;
    protected MiningLevel requiredMiningLevel = MiningLevel.Iron;
    protected IntProvider xpRange;

    public FallingFortunaBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties) {
        super(dynamicProperties.material().getMainColorRGBA(), properties.setId(dynamicProperties.resourceKey()));
        this.dynamicProperties = dynamicProperties;
    }

    @Override
    public MapCodec<FallingFortunaBlock> codec() { return CODEC; }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        super.animateTick(blockState, level, blockPos, randomSource);
        AmbientDesertBlockSoundsPlayer.playAmbientSandSounds(level, blockPos, randomSource);
    }

    @Override public List<Pair<String, String>> getRequiredTextures() { return requiredTextures; }
    @Override public List<IFortunaBlock.RequiredElement> getRequiredElements() { return requiredElements; }
    @Override public List<Integer> getRequiredTints() { return requiredTints; }
    @Override public DynamicProperties<Block, OreMaterial> getDynamicProperties() { return dynamicProperties; }
    @Override public MiningLevel getMiningLevel() {
        return requiredMiningLevel;
    }
    @Override public TagKey<Block> getRequiredTool() {
        return requiredTool;
    }
}
