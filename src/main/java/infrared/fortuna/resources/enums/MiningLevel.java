package infrared.fortuna.resources.enums;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum MiningLevel
{
    Iron(BlockTags.NEEDS_STONE_TOOL),
    Diamond(BlockTags.NEEDS_IRON_TOOL),
    Netherite(BlockTags.NEEDS_DIAMOND_TOOL);

    private final TagKey<Block> requiredTool;

    MiningLevel(TagKey<Block> requiredTool)
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
