package infrared.fortuna.enums;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.ArmorType;

public enum DynamicArmorType
{
    Helmet("helmet", EquipmentSlot.HEAD, ArmorType.HELMET, new String[]{
            "XXX",
            "X X"
    }),
    Chestplate("chestplate", EquipmentSlot.CHEST, ArmorType.CHESTPLATE, new String[]{
            "X X",
            "XXX",
            "XXX"
    }),
    Leggings("leggings", EquipmentSlot.LEGS, ArmorType.LEGGINGS, new String[]{
            "XXX",
            "X X",
            "X X"
    }),
    Boots("boots", EquipmentSlot.FEET, ArmorType.BOOTS, new String[]{
            "X X",
            "X X"
    });

    private static final String[] VARIANTS = { "iron", "gold", "diamond", "copper", "netherite" };

    private final String name;
    private final EquipmentSlot slot;
    private final String[] recipePattern;
    private final ArmorType vanillaArmorType;

    DynamicArmorType(String name, EquipmentSlot slot, ArmorType vanillaArmorType, String[] recipePattern)
    {
        this.name = name;
        this.slot = slot;
        this.recipePattern = recipePattern;
        this.vanillaArmorType = vanillaArmorType;
    }

    public String getName()                { return name; }
    public EquipmentSlot getSlot()         { return slot; }
    public String[] getRecipePattern()     { return recipePattern; }
    public ArmorType getVanillaArmorType() { return vanillaArmorType; }

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

    public String getTrimPrefix() { return "trims/items/" + name + "_trim"; }
}