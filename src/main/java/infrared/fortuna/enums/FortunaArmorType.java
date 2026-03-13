package infrared.fortuna.enums;

import net.minecraft.world.entity.EquipmentSlot;

public enum FortunaArmorType
{
    Helmet("helmet", EquipmentSlot.HEAD, new String[]{
            "XXX",
            "X X"
    }),
    Chestplate("chestplate", EquipmentSlot.CHEST, new String[]{
            "X X",
            "XXX",
            "XXX"
    }),
    Leggings("leggings", EquipmentSlot.LEGS, new String[]{
            "XXX",
            "X X",
            "X X"
    }),
    Boots("boots", EquipmentSlot.FEET, new String[]{
            "X X",
            "X X"
    });

    private static final String[] VARIANTS = { "iron", "gold", "diamond", "copper", "netherite" };

    private final String name;
    private final EquipmentSlot slot;
    private final String[] recipePattern;

    FortunaArmorType(String name, EquipmentSlot slot, String[] recipePattern)
    {
        this.name = name;
        this.slot = slot;
        this.recipePattern = recipePattern;
    }

    public String getName()              { return name; }
    public EquipmentSlot getSlot()       { return slot; }
    public String[] getRecipePattern()   { return recipePattern; }

    public static int getVariantCount()  { return VARIANTS.length - 1; }

    // e.g. "iron_helmet", "diamond_boots"
    public String getItemTexture(int variant)
    {
        return VARIANTS[variant % getVariantCount()] + "_" + name;
    }

    // e.g. "iron", "diamond" — used for equipment asset humanoid/humanoid_leggings textures
    public static String getEquipmentTexture(int variant)
    {
        return VARIANTS[variant % getVariantCount()];
    }
}