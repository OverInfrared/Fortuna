package infrared.fortuna.mixin;

import infrared.fortuna.resources.FortunaTags;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public class TagLoaderMixin {
	@Shadow private String directory;

	@Inject(method = "load", at = @At("RETURN"), cancellable = true)
	private void onLoad(ResourceManager resourceManager,
						CallbackInfoReturnable<Map<Identifier, List<TagLoader.EntryWithSource>>> cir) {
		if (!directory.equals("tags/block")) return;

		Map<Identifier, List<TagLoader.EntryWithSource>> result = new HashMap<>(cir.getReturnValue());
		FortunaTags.getInstance().injectTags(result);
		cir.setReturnValue(result);
	}
}