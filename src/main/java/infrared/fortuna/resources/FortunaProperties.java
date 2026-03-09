package infrared.fortuna.resources;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

public record FortunaProperties<T>(String registryName, Component displayName, ResourceKey<T> resourceKey) { }
