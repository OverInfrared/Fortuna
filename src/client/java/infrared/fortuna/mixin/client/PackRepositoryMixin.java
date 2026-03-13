package infrared.fortuna.mixin.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import infrared.fortuna.resources.FortunaResourcePack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnusedDeclaration", "unused"})

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
	@Shadow private Map<String, Pack> available;
	@Shadow private List<Pack> selected;

	@Inject(method = "reload", at = @At("RETURN"))
	private void onReload(CallbackInfo ci) {
		// Add to available
		Map<String, Pack> mutable = new java.util.HashMap<>(this.available);
		FortunaResourcePack.getInstance().loadPacks(pack -> mutable.put(pack.getId(), pack));
		this.available = ImmutableMap.copyOf(mutable);

		// Add to selected at the FRONT (index 0 = lowest priority, vanilla loads after and wins)
		List<Pack> mutableSelected = new java.util.ArrayList<>(this.selected);
		FortunaResourcePack.getInstance().loadPacks(pack -> {
			if (!mutableSelected.contains(pack)) {
				mutableSelected.addFirst(pack); // insert at front = bottom priority
			}
		});
		this.selected = ImmutableList.copyOf(mutableSelected);
	}
}