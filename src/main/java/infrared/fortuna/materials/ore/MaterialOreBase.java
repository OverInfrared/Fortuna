package infrared.fortuna.materials.ore;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.awt.*;

public enum MaterialOreBase
{
    Stone     ("bases/stone",      "stone",      new Color(110, 110, 110), new Color(150, 150, 150), BlockTags.MINEABLE_WITH_PICKAXE),
    Diorite   ("bases/diorite",    "diorite",    new Color(139, 139, 139), new Color(233, 233, 233), BlockTags.MINEABLE_WITH_PICKAXE),
    Granite   ("bases/granite",    "granite",    new Color(127,  86,  70), new Color(169, 119, 100), BlockTags.MINEABLE_WITH_PICKAXE),
    Andesite  ("bases/andesite",   "andesite",   new Color(116, 116, 116), new Color(156, 156, 156), BlockTags.MINEABLE_WITH_PICKAXE),
    Gravel    ("bases/gravel",     "gravel",     new Color(114, 107, 105), new Color(150, 142, 142), BlockTags.MINEABLE_WITH_SHOVEL),
    Sand      ("bases/sand",       "sand",       new Color(209, 186, 138), new Color(231, 228, 187), BlockTags.MINEABLE_WITH_SHOVEL),
    Tuff      ("bases/tuff",       "tuff",       new Color( 93,  93,  80), new Color(133, 131, 123), BlockTags.MINEABLE_WITH_PICKAXE),
    Netherrack("bases/netherrack", "netherrack", new Color(100,  35,  35), new Color(140,  60,  60), BlockTags.MINEABLE_WITH_PICKAXE),
    Deepslate ("bases/deepslate",  "deepslate",  new Color( 61,  61,  67), new Color( 81,  81,  81), BlockTags.MINEABLE_WITH_PICKAXE);

    private final String texture;
    private final String blockId;
    private final Color borderColor;
    private final Color bottomBorderColor;
    private final TagKey<Block> requiredTool;

    MaterialOreBase(String texture, String blockId, Color borderColor, Color bottomBorderColor, TagKey<Block> requiredTool)
    {
        this.texture = texture;
        this.blockId = blockId;
        this.borderColor = borderColor;
        this.bottomBorderColor = bottomBorderColor;
        this.requiredTool = requiredTool;
    }

    public String getTexture()             { return texture; }
    public String getBlockId()             { return blockId; }
    public Color  getBorderColor()         { return borderColor; }
    public Color  getBottomBorderColor()   { return bottomBorderColor; }
    public TagKey<Block> getRequiredTool() { return requiredTool; }
}