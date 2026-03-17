package infrared.fortuna.config;

import io.wispforest.owo.config.annotation.*;

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

    @SectionHeader("Debug")
    public boolean debugLogging = false;
}
