package infrared.fortuna.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.SectionHeader;

import java.util.ArrayList;
import java.util.List;

@Modmenu(modId = "fortuna")
@Config(name = "fortuna-config", wrapperName = "FortunaConfig")
public class FortunaConfigModel
{
    @SectionHeader("Fortuna")
    public long seed = 42L;

    @SectionHeader("Ore Generation")
    public List<Integer> materialWeights = new ArrayList<>(List.of(10, 50, 30, 7, 3));
    public List<Integer> fuelWeights     = new ArrayList<>(List.of(10, 80, 10));

    @SectionHeader("Ore")
    @Nest public MaterialTypeWeights copperWeights = new MaterialTypeWeights(80, 20, 0);
    @Nest public MaterialTypeWeights ironWeights = new MaterialTypeWeights(65, 35, 0);
    @Nest public MaterialTypeWeights diamondWeights = new MaterialTypeWeights(35, 60, 5);
    @Nest public MaterialTypeWeights netheriteWeights = new MaterialTypeWeights(33, 33, 33);

    @SectionHeader("Debug")
    public boolean debugLogging = false;

    public static class MaterialTypeWeights
    {
        public int ingot = 0;
        public int gem = 0;
        public int special = 0;

        public MaterialTypeWeights() {}

        public MaterialTypeWeights(int ingot, int gem, int special)
        {
            this.ingot = ingot;
            this.gem = gem;
            this.special = special;
        }
    }
}
