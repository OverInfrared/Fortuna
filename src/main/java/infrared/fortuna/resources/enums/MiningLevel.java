package infrared.fortuna.resources.enums;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum MiningLevel
{
    Stone(null),
    Iron(BlockTags.NEEDS_STONE_TOOL),
    Diamond(BlockTags.NEEDS_IRON_TOOL),
    Netherite(BlockTags.NEEDS_DIAMOND_TOOL),
    Fortuna(null);

    private final TagKey<Block> requiredTool;

    private MiningLevel(TagKey<Block> requiredTool)
    {
        this.requiredTool = requiredTool;
    }

    public boolean hasRequiredTool()
    {
        return requiredTool != null;
    }

    public TagKey<Block> getRequiredTool()
    {
        return requiredTool;
    }
}
