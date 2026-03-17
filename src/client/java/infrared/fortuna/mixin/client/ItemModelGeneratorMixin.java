package infrared.fortuna.mixin.client;

import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

// Can't believe I actually needed more Item layers...

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin
{
    @Shadow @Final @Mutable private static List<String> LAYERS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void fortuna$extendLayers(CallbackInfo ci)
    {
        List<String> layers = new ArrayList<>(LAYERS);
        layers.add("layer5");
        layers.add("layer6");
        layers.add("layer7");
        layers.add("layer8");
        LAYERS = List.copyOf(layers);
    }
}