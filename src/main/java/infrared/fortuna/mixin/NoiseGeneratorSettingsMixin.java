package infrared.fortuna.mixin;

import infrared.fortuna.Fortuna;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin
{
    @Inject(method = "oreVeinsEnabled", at = @At("HEAD"), cancellable = true)
    private void disableOreVeins(CallbackInfoReturnable<Boolean> cir)
    {
        if (Fortuna.disableVanillaOreVeins)
            cir.setReturnValue(false);
    }
}