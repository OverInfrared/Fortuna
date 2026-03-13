package infrared.fortuna.mixin;

import infrared.fortuna.data.FortunaDataPack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin
{
    @Inject(method = "reload", at = @At("HEAD"))
    private static void onReload(LayeredRegistryAccess layeredRegistryAccess, List<?> list,
                                 ResourceManager resourceManager, Executor executor,
                                 CallbackInfoReturnable<?> cir)
    {
        HolderLookup.Provider registries = layeredRegistryAccess.compositeAccess();
        FortunaDataPack.setRegistryLookup(registries);
    }
}