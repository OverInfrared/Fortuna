package infrared.fortuna.enums;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum MiningLevel
{
    Copper(BlockTags.NEEDS_STONE_TOOL),
    Iron(BlockTags.NEEDS_IRON_TOOL),
    Diamond(BlockTags.NEEDS_DIAMOND_TOOL),
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