package infrared.fortuna.items;

import com.google.common.collect.BiMap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import infrared.fortuna.DynamicProperties;
import infrared.fortuna.Fortuna;
import infrared.fortuna.util.Utilities;
import infrared.fortuna.materials.ore.OreMaterial;
import infrared.fortuna.recipes.FortunaRecipeProvider;
import infrared.fortuna.recipes.IFortunaRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.awt.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FortunaTool extends FortunaItem implements IFortunaRecipe
{
    private final DynamicToolType dynamicToolType;

    public FortunaTool(DynamicProperties<Item, OreMaterial> dynamicProperties, Properties properties, DynamicToolType dynamicToolType)
    {
        super(dynamicProperties, applyToolProperties(properties, dynamicToolType, dynamicProperties.material().getToolMaterial()));
        this.dynamicToolType = dynamicToolType;

        OreMaterial material = dynamicProperties.material();

        addRequiredTexture(dynamicToolType.getHiltTexture());
        addRequiredTexture(dynamicToolType.getMaterialTexture(dynamicProperties.material().getToolVariant()));
        addRequiredTint(Color.white.getRGB());
        addRequiredTint(material.getMainColor().getRGB());
    }

    public DynamicToolType getToolType()
    {
        return dynamicToolType;
    }

    private static Properties applyToolProperties(Properties properties, DynamicToolType tool, ToolMaterial material)
    {
        return switch (tool)
        {
            case Sword   -> properties.sword(material, 3.0f, -2.4f);
            case Pickaxe -> properties.pickaxe(material, 1.0f, -2.8f);
            case Axe     -> properties.axe(material, 5.0f, -3.0f);
            case Shovel  -> properties.shovel(material, 1.5f, -3.0f);
            case Hoe     -> properties.hoe(material, -3.0f, 0.0f);
        };
    }

    @Override
    public String getModelString()
    {
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", "%s:item/%s".formatted(Fortuna.MOD_ID, dynamicToolType.getHiltTexture()));
        textures.addProperty("layer1", "%s:item/%s".formatted(Fortuna.MOD_ID, dynamicToolType.getMaterialTexture(dynamicProperties.material().getToolVariant())));

        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/handheld");
        model.add("textures", textures);

        return model.toString();
    }

    @Override
    public Map<String, JsonObject> getRecipes(HolderLookup.Provider registries)
    {
        FortunaRecipeProvider helper = new FortunaRecipeProvider(registries);

        Item material = Utilities.findItem(dynamicProperties.material().getRefinedRegistryName());
        if (material == null)
            return new HashMap<>();

        Map<String, JsonObject> recipes = new LinkedHashMap<>();

        recipes.put(getRegistryName(),
                helper.shapedTool(this, material, dynamicToolType.getRecipePattern()));

        return recipes;
    }

    @Override
    public Set<String> getRecipeNames()
    {
        return Set.of(getRegistryName());
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        return switch (dynamicToolType)
        {
            case Axe -> handleAxeUse(context);
            case Shovel -> handleShovelUse(context);
            case Hoe -> handleHoeUse(context);
            default -> super.useOn(context);
        };
    }

    private InteractionResult handleAxeUse(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState blockState = level.getBlockState(blockPos);

        // Stripping logs
        Block stripped = AxeItem.STRIPPABLES.get(blockState.getBlock());
        if (stripped != null)
        {
            level.playSound(player, blockPos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            BlockState strippedState = stripped.defaultBlockState().setValue(RotatedPillarBlock.AXIS, blockState.getValue(RotatedPillarBlock.AXIS));
            level.setBlock(blockPos, strippedState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, strippedState));
            if (player != null)
                context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            return InteractionResult.SUCCESS;
        }

        // Scraping oxidation
        Optional<BlockState> previous = WeatheringCopper.getPrevious(blockState);
        if (previous.isPresent())
        {
            level.playSound(player, blockPos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, blockPos, 0);
            level.setBlock(blockPos, previous.get(), 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, previous.get()));
            if (player != null)
                context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            return InteractionResult.SUCCESS;
        }

        // Removing wax
        Optional<BlockState> unwaxed = Optional.ofNullable((Block) ((BiMap) HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(blockState.getBlock()))
                .map(block -> block.withPropertiesOf(blockState));
        if (unwaxed.isPresent())
        {
            level.playSound(player, blockPos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, blockPos, 0);
            level.setBlock(blockPos, unwaxed.get(), 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, unwaxed.get()));
            if (player != null)
                context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private InteractionResult handleShovelUse(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (context.getClickedFace() == Direction.DOWN)
            return InteractionResult.PASS;

        Player player = context.getPlayer();
        BlockState flattened = ShovelItem.FLATTENABLES.get(blockState.getBlock());

        if (flattened != null && level.getBlockState(blockPos.above()).isAir())
        {
            level.playSound(player, blockPos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!level.isClientSide())
            {
                level.setBlock(blockPos, flattened, 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, flattened));
                if (player != null)
                    context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            }
            return InteractionResult.SUCCESS;
        }

        if (blockState.getBlock() instanceof CampfireBlock && blockState.getValue(CampfireBlock.LIT))
        {
            if (!level.isClientSide())
            {
                level.levelEvent(null, 1009, blockPos, 0);
                CampfireBlock.dowse(player, level, blockPos, blockState);
                level.setBlock(blockPos, blockState.setValue(CampfireBlock.LIT, false), 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, blockState));
                if (player != null)
                    context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private InteractionResult handleHoeUse(UseOnContext context)
    {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = HoeItem.TILLABLES.get(
                level.getBlockState(blockPos).getBlock());

        if (pair == null)
            return InteractionResult.PASS;

        Predicate<UseOnContext> predicate = pair.getFirst();
        Consumer<UseOnContext> consumer = pair.getSecond();

        if (predicate.test(context))
        {
            Player player = context.getPlayer();
            level.playSound(player, blockPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!level.isClientSide())
            {
                consumer.accept(context);
                if (player != null)
                    context.getItemInHand().hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}