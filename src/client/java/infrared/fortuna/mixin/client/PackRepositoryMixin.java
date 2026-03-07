package infrared.fortuna.mixin.client;

import infrared.fortuna.resources.FortunaResourcePack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"UnusedDeclaration", "unused"})

@Mixin(PackRepository.class)
public class PackRepositoryMixin {

	@Shadow
	private Map<String, Pack> available;

	@Inject(method = "reload", at = @At("RETURN"))
	private void onReload(CallbackInfo ci) {
		// available is ImmutableMap after reload, so we rebuild it
		Map<String, Pack> mutable = new HashMap<>(this.available);
		FortunaResourcePack.getInstance().loadPacks(pack ->
				mutable.put(pack.getId(), pack)
		);
		this.available = mutable;
	}
}