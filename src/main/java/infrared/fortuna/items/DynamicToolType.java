package infrared.fortuna.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum DynamicToolType
{
    Sword("sword", null,
            new String[]{ "iron_sword", "golden_sword", "diamond_sword", "copper_sword", "netherite_sword" },
            "sword_hilt",
            new String[]{
                    "X",
                    "X",
                    "|"
            }),
    Pickaxe("pickaxe", BlockTags.MINEABLE_WITH_PICKAXE,
            new String[]{ "iron_pickaxe", "golden_pickaxe", "diamond_pickaxe", "copper_pickaxe", "netherite_pickaxe" },
            "pickaxe_handle",
            new String[]{
                    "XXX",
                    " | ",
                    " | "
            }),
    Axe("axe", BlockTags.MINEABLE_WITH_AXE,
            new String[]{ "iron_axe", "golden_axe", "diamond_axe", "copper_axe", "netherite_axe" },
            "axe_handle",
            new String[]{
                    "XX",
                    "X|",
                    " |"
            }),
    Shovel("shovel", BlockTags.MINEABLE_WITH_SHOVEL,
            new String[]{ "iron_shovel", "golden_shovel", "diamond_shovel", "copper_shovel", "netherite_shovel" },
            "shovel_handle",
            new String[]{
                    "X",
                    "|",
                    "|"
            }),
    Hoe("hoe", BlockTags.MINEABLE_WITH_HOE,
            new String[]{ "iron_hoe", "golden_hoe", "diamond_hoe", "copper_hoe", "netherite_hoe" },
            "hoe_handle",
            new String[]{
                    "XX",
                    " |",
                    " |"
            });

    private final String name;
    private final TagKey<Block> mineableTag;
    private final String[] materialVariants;
    private final String hiltTexture;
    private final String[] recipePattern;

    DynamicToolType(String name, TagKey<Block> mineableTag, String[] materialVariants, String hiltTexture, String[] recipePattern)
    {
        this.name = name;
        this.mineableTag = mineableTag;
        this.materialVariants = materialVariants;
        this.hiltTexture = hiltTexture;
        this.recipePattern = recipePattern;
    }

    public String getName()                { return name; }
    public TagKey<Block> getMineableTag()  { return mineableTag; }
    public String getHiltTexture()         { return hiltTexture; }
    public String[] getRecipePattern()     { return recipePattern; }

    public static int getVariantCount() { return DynamicToolType.Pickaxe.materialVariants.length - 1; }

    public String getMaterialTexture(int variant)
    {
        return materialVariants[variant % getVariantCount()];
    }
}