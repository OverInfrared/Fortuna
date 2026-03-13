package infrared.fortuna.blocks.ore;

import com.google.gson.JsonObject;
import infrared.fortuna.Utilities;
import infrared.fortuna.blocks.FortunaBlock;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.enums.ore.MaterialOreRaw;
import infrared.fortuna.materials.OreMaterial;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RawMaterialBlock extends FortunaBlock implements IFortunaRecipe
{
    public RawMaterialBlock(DynamicProperties<Block, OreMaterial> fortunaProps, Properties properties)
    {
        super(fortunaProps, properties);

        this.requiredMiningLevel = fortunaProps.material().getMiningLevel();

        MaterialOreRaw oreRaw = fortunaProps.material().getMaterialType();
        boolean oxidizable = oreRaw.isOxidizable();

        String blockTexture = oreRaw.getTexture() + "_block";

        addRequiredTexture("particle", oxidizable ? blockTexture + "_base" : blockTexture);
        addOverlayTexture("barrier", blockTexture, 0);
        addOverlayTexture("overlay", oxidizable ? blockTexture + "_base" : blockTexture, 0);
        if (oxidizable)
        {
            addOverlayTexture("overlayoxidized", blockTexture + "_oxidized", 1);
            addOverlayTexture("overlaytransition", blockTexture + "_transition", 2);
        }

        addRequiredTint(fortunaProps.material().getColor().getRGB());
        addRequiredTint(fortunaProps.material().getSecondaryColor().getRGB());
        addRequiredTint(fortunaProps.material().getTransitionColor(0.5f, 0.5f, 1f).getRGB());
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item rawItem = Utilities.findItem(getDynamicProperties().material().getRawRegistryName());
        if (rawItem == null)
            return new HashMap<>();

        Item rawBlock = this.asItem();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        // 9 raw items -> raw block
        recipes.put(getRegistryName(),
                helper.shapedNineToBlock(rawBlock, rawItem));

        // raw block -> 9 raw items
        recipes.put(getRegistryName() + "_unpack",
                helper.shapelessNineFromBlock(rawItem, rawBlock));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(
            getRegistryName(),
            getRegistryName() + "_unpack"
        );
    }
}
