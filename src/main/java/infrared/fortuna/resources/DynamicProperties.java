package infrared.fortuna.resources;

import infrared.fortuna.resources.materials.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public record DynamicProperties<T, M extends Material>(String registryName, Component displayName, ResourceKey<T> resourceKey, M material) { }
