package infrared.fortuna.enums;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public enum ToolType
{
    Sword("sword", null, 3.0f, 1.6f, new String[]{
            "X",
            "X",
            "|"
    }),
    Pickaxe("pickaxe", BlockTags.MINEABLE_WITH_PICKAXE, 1.0f, 1.2f, new String[]{
            "XXX",
            " | ",
            " | "
    }),
    Axe("axe", BlockTags.MINEABLE_WITH_AXE, 6.0f, 0.8f, new String[]{
            "XX",
            "X|",
            " |"
    }),
    Shovel("shovel", BlockTags.MINEABLE_WITH_SHOVEL, 1.5f, 1.0f, new String[]{
            "X",
            "|",
            "|"
    }),
    Hoe("hoe", BlockTags.MINEABLE_WITH_HOE, 0.0f, 3.0f, new String[]{
            "XX",
            " |",
            " |"
    });

    private final String name;
    private final TagKey<Block> mineableTag;
    private final float baseAttackDamage;
    private final float attackSpeed;
    private final String[] recipePattern;

    ToolType(String name, TagKey<Block> mineableTag, float baseAttackDamage, float attackSpeed, String[] recipePattern)
    {
        this.name = name;
        this.mineableTag = mineableTag;
        this.baseAttackDamage = baseAttackDamage;
        this.attackSpeed = attackSpeed;
        this.recipePattern = recipePattern;
    }

    public String getName()
    {
        return name;
    }

    public TagKey<Block> getMineableTag()
    {
        return mineableTag;
    }

    public float getBaseAttackDamage()
    {
        return baseAttackDamage;
    }

    public float getAttackSpeed()
    {
        return attackSpeed;
    }

    public String[] getRecipePattern()
    {
        return recipePattern;
    }
}