package infrared.fortuna.resources;

import infrared.fortuna.Fortuna;
import infrared.fortuna.blocks.IFortunaBlock;
import infrared.fortuna.resources.enums.MiningLevel;
import infrared.fortuna.resources.materials.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;

import java.util.*;

public class FortunaTags
{
    private static final String PACK_ID = "fortuna_dynamic_data";

    private static final FortunaTags INSTANCE = new FortunaTags();

    public static FortunaTags getInstance() { return INSTANCE; }

    private static final List<Material> loadedMaterials = new ArrayList<>();

    private FortunaTags() { }

    public void injectTags(Map<Identifier, List<TagLoader.EntryWithSource>> map) {
        for (Material material : Fortuna.initializedMaterials) {
            for (IFortunaBlock block : material.getBlocks()) {
                TagEntry entry = TagEntry.element(Identifier.fromNamespaceAndPath(Fortuna.MOD_ID, block.getRegistryName()));
                TagLoader.EntryWithSource ews = new TagLoader.EntryWithSource(entry, PACK_ID);

                // tool tag
                Identifier toolTag = block.getRequiredTool().location();
                map.computeIfAbsent(toolTag, k -> new ArrayList<>()).add(ews);

                // mining level tags (cumulative)
                for (MiningLevel level : MiningLevel.values()) {
                    if (block.getMiningLevel().ordinal() >= level.ordinal()) {
                        Identifier levelTag = miningLevelToTag(level);
                        map.computeIfAbsent(levelTag, k -> new ArrayList<>()).add(ews);
                    }
                }
            }
        }
    }

    private Identifier miningLevelToTag(MiningLevel level) {
        return switch (level) {
            case Stone   -> Identifier.fromNamespaceAndPath("minecraft", "needs_stone_tool");
            case Iron    -> Identifier.fromNamespaceAndPath("minecraft", "needs_iron_tool");
            case Diamond -> Identifier.fromNamespaceAndPath("minecraft", "needs_diamond_tool");
            case Netherite -> null;
            case Fortuna -> null;
        };
    }
}