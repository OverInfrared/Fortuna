package infrared.fortuna.blocks;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.function.Function;

public class FortunaBlockLootProvider extends BlockLootSubProvider
{
    public FortunaBlockLootProvider(HolderLookup.Provider registries)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    protected FortunaBlockLootProvider(Set<Item> set, FeatureFlagSet featureFlagSet, HolderLookup.Provider provider)
    {
        super(set, featureFlagSet, provider);
    }

    public LootTable.Builder createMultiOreDrop(Block block, Item item, FloatProvider countRange, Function<Holder<Enchantment>, LootItemConditionalFunction.Builder<?>> bonusFunction) {
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(
                block,
                this.applyExplosionDecay(
                        block,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(countRange.getMinValue(), countRange.getMaxValue())))
                                .apply(bonusFunction.apply(registryLookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    @Override
    public void generate()
    {

    }
}
