package infrared.fortuna.blocks.ore;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import infrared.fortuna.Fortuna;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.blocks.FortunaBlockLootProvider;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.enums.ore.MaterialOreBase;
import infrared.fortuna.enums.ore.MaterialOreOverlay;
import infrared.fortuna.materials.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class OreBlock extends FortunaBlock implements IFortunaRecipe
{
    public OreBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties)
    {
        this(dynamicProperties, properties, dynamicProperties.material().getBase());
    }

    public OreBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, MaterialOreBase base)
    {
        this(dynamicProperties, properties, base, dynamicProperties.material().getXpRange());
    }

    public OreBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, MaterialOreBase base, IntProvider xpRange) {
        super(dynamicProperties, properties);

        OreMaterial material = dynamicProperties.material();

        this.requiredTool = base.getRequiredTool();
        this.requiredMiningLevel = material.getMiningLevel();
        this.xpRange = xpRange;

        addBaseTextures(base.getTexture());

        MaterialOreOverlay materialOreOverlay = material.getOverlay();
        addOverlayTexture("borderbottom", materialOreOverlay.getBorderBottom(), 0);
        addOverlayTexture("bordertop", materialOreOverlay.getBorderTop(), 1);
        addRequiredTint(base.getBottomBorderColor().getRGB());
        addRequiredTint(base.getBorderColor().getRGB());

        addOverlayTexture("overlay", materialOreOverlay.getTexture(), 2);
        addRequiredTint(material.getMainColor().getRGB());

        if (materialOreOverlay.hasSecondary() && materialOreOverlay.hasTertiary())
        {
            addOverlayTexture("overlayoxidized", materialOreOverlay.getSecondary(), 3);
            addOverlayTexture("overlaytransition", materialOreOverlay.getTertiary(), 4);
            addRequiredTint(material.getSecondaryColor().getRGB());
            addRequiredTint(material.getTransitionColor(0.5f, 0.5f, 1f).getRGB());
        }
        else if (materialOreOverlay.hasSecondary())
        {
            addOverlayTexture("overlaytransition", materialOreOverlay.getSecondary(), 3);
            Color color = Utilities.lerpColor(material.getMainColor(), base.getBottomBorderColor(), 0.2f);
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            color = Color.getHSBColor(hsb[0], hsb[1] * 0.3f, hsb[2]);
            addRequiredTint(color.getRGB());
        }
    }

    @Override
    protected void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl)
    {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
        if (bl && xpRange.getMaxValue() != 0) {
            this.tryDropExperience(serverLevel, blockPos, itemStack, xpRange);
        }
    }

    @Override
    public JsonObject getLoot(HolderLookup.Provider registries)
    {
        FortunaBlockLootProvider helper = new FortunaBlockLootProvider(registries);

        Block oreBlock = this;
        Item rawItem = BuiltInRegistries.ITEM
                .get(Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, dynamicProperties.material().getRawRegistryName()))
                .orElseThrow().value();

        LootTable table = switch (dynamicProperties.material().getDropType())
        {
            case Single -> helper.createOreDrop(oreBlock, rawItem).build();
            case Copper -> helper.createMultiOreDrop(oreBlock, rawItem, UniformFloat.of(2.0f, 5.0f), ApplyBonusCount::addOreBonusCount).build();
            case Lapis -> helper.createMultiOreDrop(oreBlock, rawItem, UniformFloat.of(4.0f, 9.0f), ApplyBonusCount::addOreBonusCount).build();
            case Redstone -> helper.createMultiOreDrop(oreBlock, rawItem, UniformFloat.of(4.0f, 5.0f), ApplyBonusCount::addUniformBonusCount).build();
        };

        return LootTable.DIRECT_CODEC
                .encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), table)
                .getOrThrow()
                .getAsJsonObject();
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item ingot = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (ingot == null)
            return new HashMap<>();

        Item oreItem = this.asItem();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        recipes.put(getRegistryName() + "_smelting",
                helper.smelting(oreItem, ingot, 0.7f, 200));

        recipes.put(getRegistryName() + "_blasting",
                helper.blasting(oreItem, ingot, 0.7f, 100));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(
                getRegistryName() + "_smelting",
                getRegistryName() + "_blasting"
        );
    }
}
