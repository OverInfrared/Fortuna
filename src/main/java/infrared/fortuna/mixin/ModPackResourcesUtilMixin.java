package infrared.fortuna.mixin;

import infrared.fortuna.data.FortunaDataPack;
import net.fabricmc.fabric.api.resource.v1.pack.ModPackResources;
import net.fabricmc.fabric.impl.resource.pack.ModPackResourcesUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ModPackResourcesUtil.class)
public class ModPackResourcesUtilMixin {
    @Inject(method = "getModResourcePacks", at = @At("RETURN"), cancellable = true)
    private static void onGetModResourcePacks(FabricLoader fabricLoader, PackType type, String subPath,
                                              CallbackInfoReturnable<List<ModPackResources>> cir) {
        if (type != PackType.SERVER_DATA) return;

        List<ModPackResources> result = new ArrayList<>(cir.getReturnValue());
        result.addLast(FortunaDataPack.getInstance()); // index 0 = lowest priority
        cir.setReturnValue(result);
    }
}
