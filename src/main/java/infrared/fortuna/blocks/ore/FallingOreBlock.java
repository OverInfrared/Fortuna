package infrared.fortuna.blocks.ore;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import infrared.fortuna.Fortuna;
import infrared.fortuna.Utilities;
import infrared.fortuna.blocks.FallingFortunaBlock;
import infrared.fortuna.blocks.FortunaBlockLootProvider;
import infrared.fortuna.resources.DynamicProperties;
import infrared.fortuna.resources.enums.ore.MaterialOreBase;
import infrared.fortuna.resources.enums.ore.MaterialOreOverlay;
import infrared.fortuna.resources.materials.OreMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

import java.awt.*;

public class FallingOreBlock extends FallingFortunaBlock
{
    public FallingOreBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties)
    {
        this(dynamicProperties, properties, dynamicProperties.material().getBase());
    }

    public FallingOreBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, MaterialOreBase base)
    {
        this(dynamicProperties, properties, base, dynamicProperties.material().getXpRange());
    }

    public FallingOreBlock(DynamicProperties<Block, OreMaterial> dynamicProperties, Properties properties, MaterialOreBase base, IntProvider xpRange)
    {
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
        addRequiredTint(material.getColor().getRGB());

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
            Color color = Utilities.lerpColor(material.getColor(), base.getBottomBorderColor(), 0.2f);
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            color = Color.getHSBColor(hsb[0], hsb[1] * 0.2f, 1f);
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

        LootTable table = helper.createOreDrop(oreBlock, rawItem).build();

        return LootTable.DIRECT_CODEC
                .encodeStart(registries.createSerializationContext(JsonOps.INSTANCE), table)
                .getOrThrow()
                .getAsJsonObject();
    }
}
