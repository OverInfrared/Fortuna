package infrared.fortuna.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import infrared.fortuna.Fortuna;

public class LootBuilder
{
    private String dropName;
    private final String selfName;

    private boolean silkTouch = false;
    private boolean fortune = false;

    private float minCount = 1;
    private float maxCount = 1;

    public LootBuilder(String selfRegistryName)
    {
        this.selfName = selfRegistryName;
        this.dropName = selfRegistryName; // default drop self
    }

    public LootBuilder dropSelf()
    {
        this.dropName = selfName;
        return this;
    }

    public LootBuilder dropByName(String registryName)
    {
        this.dropName = registryName;
        return this;
    }

    public LootBuilder withFortune()
    {
        this.fortune = true;
        return this;
    }

    public LootBuilder withSilkTouch(String silkDropName)
    {
        this.silkTouch = true;
        this.dropName = silkDropName; // what drops without silk touch
        return this;
    }

    public LootBuilder withCount(float min, float max)
    {
        this.minCount = min;
        this.maxCount = max;
        return this;
    }

    public JsonObject build()
    {
        if (silkTouch) return buildOreLoot();
        return buildSimpleLoot();
    }

    private JsonObject buildSimpleLoot()
    {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", "%s:%s".formatted(Fortuna.MOD_ID, dropName));

        if (fortune)
        {
            JsonObject fortuneFunc = new JsonObject();
            fortuneFunc.addProperty("function", "minecraft:apply_bonus");
            fortuneFunc.addProperty("enchantment", "minecraft:fortune");
            fortuneFunc.addProperty("formula", "minecraft:ore_drops");
            JsonArray functions = new JsonArray();
            functions.add(fortuneFunc);
            entry.add("functions", functions);
        }

        JsonArray entries = new JsonArray();
        entries.add(entry);

        JsonArray conditions = new JsonArray();
        JsonObject explosion = new JsonObject();
        explosion.addProperty("condition", "minecraft:survives_explosion");
        conditions.add(explosion);

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);
        pool.add("conditions", conditions);
        pool.add("entries", entries);

        JsonArray pools = new JsonArray();
        pools.add(pool);

        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:block");
        table.add("pools", pools);
        return table;
    }

    private JsonObject buildOreLoot()
    {
        // Silk touch child
        JsonObject silkCondition = makeSilkTouchCondition();
        JsonArray silkConditions = new JsonArray();
        silkConditions.add(silkCondition);

        JsonObject silkChild = new JsonObject();
        silkChild.addProperty("type", "minecraft:item");
        silkChild.addProperty("name", "%s:%s".formatted(Fortuna.MOD_ID, selfName));
        silkChild.add("conditions", silkConditions);

        // Fortune + explosion decay child
        JsonObject fortuneFunc = new JsonObject();
        fortuneFunc.addProperty("function", "minecraft:apply_bonus");
        fortuneFunc.addProperty("enchantment", "minecraft:fortune");
        fortuneFunc.addProperty("formula", "minecraft:ore_drops");

        JsonObject decayFunc = new JsonObject();
        decayFunc.addProperty("function", "minecraft:explosion_decay");

        JsonArray functions = new JsonArray();
        if (minCount != 1 || maxCount != 1)
        {
            JsonObject countRange = new JsonObject();
            countRange.addProperty("type", "minecraft:uniform");
            countRange.addProperty("min", minCount);
            countRange.addProperty("max", maxCount);

            JsonObject setCount = new JsonObject();
            setCount.addProperty("function", "minecraft:set_count");
            setCount.addProperty("add", false);
            setCount.add("count", countRange);

            functions.add(setCount); // add before fortuneFunc
        }
        functions.add(fortuneFunc);
        functions.add(decayFunc);

        JsonObject rawChild = new JsonObject();
        rawChild.addProperty("type", "minecraft:item");
        rawChild.addProperty("name", "%s:%s".formatted(Fortuna.MOD_ID, dropName));
        rawChild.add("functions", functions);

        // Alternatives entry
        JsonArray children = new JsonArray();
        children.add(silkChild);
        children.add(rawChild);

        JsonObject alternativesEntry = new JsonObject();
        alternativesEntry.addProperty("type", "minecraft:alternatives");
        alternativesEntry.add("children", children);

        JsonArray entries = new JsonArray();
        entries.add(alternativesEntry);

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1.0);
        pool.addProperty("bonus_rolls", 0.0);
        pool.add("entries", entries);

        JsonArray pools = new JsonArray();
        pools.add(pool);

        JsonObject table = new JsonObject();
        table.addProperty("type", "minecraft:block");
        table.add("pools", pools);
        table.addProperty("random_sequence", "%s:blocks/%s".formatted(Fortuna.MOD_ID, selfName));
        return table;
    }

    private JsonObject makeSilkTouchCondition()
    {
        JsonObject levels = new JsonObject();
        levels.addProperty("min", 1);

        JsonObject enchantmentEntry = new JsonObject();
        enchantmentEntry.addProperty("enchantments", "minecraft:silk_touch");
        enchantmentEntry.add("levels", levels);

        JsonArray enchantmentArray = new JsonArray();
        enchantmentArray.add(enchantmentEntry);

        JsonObject enchantmentsObj = new JsonObject();
        enchantmentsObj.add("minecraft:enchantments", enchantmentArray);

        JsonObject predicates = new JsonObject();
        predicates.add("predicates", enchantmentsObj);

        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:match_tool");
        condition.add("predicate", predicates);
        return condition;
    }
}