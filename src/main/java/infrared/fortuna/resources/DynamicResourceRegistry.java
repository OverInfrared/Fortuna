package infrared.fortuna.resources;

import java.util.*;
import java.util.function.Supplier;

public class DynamicResourceRegistry
{
    private static final DynamicResourceRegistry SERVER = new DynamicResourceRegistry();
    private static final DynamicResourceRegistry CLIENT = new DynamicResourceRegistry();

    private final Map<String, Map<String, Supplier<String>>> resources = new HashMap<>();
    private final Map<String, Map<String, Supplier<byte[]>>> binaryResources = new HashMap<>();

    public static DynamicResourceRegistry server() { return SERVER; }
    public static DynamicResourceRegistry client() { return CLIENT; }

    public void register(String namespace, String path, Supplier<String> supplier)
    {
        resources.computeIfAbsent(namespace, k -> new LinkedHashMap<>())
                .put(path, supplier);
    }

    public void register(String namespace, String path, String value)
    {
        register(namespace, path, () -> value);
    }

    public void registerBinary(String namespace, String path, Supplier<byte[]> supplier)
    {
        binaryResources.computeIfAbsent(namespace, k -> new LinkedHashMap<>())
                .put(path, supplier);
    }

    public String resolve(String namespace, String path)
    {
        Map<String, Supplier<String>> nsMap = resources.get(namespace);
        if (nsMap == null) return null;

        Supplier<String> supplier = nsMap.get(path);
        return supplier != null ? supplier.get() : null;
    }

    public byte[] resolveBinary(String namespace, String path)
    {
        Map<String, Supplier<byte[]>> nsMap = binaryResources.get(namespace);
        if (nsMap == null) return null;

        Supplier<byte[]> supplier = nsMap.get(path);
        return supplier != null ? supplier.get() : null;
    }

    public Set<String> getNamespaces()
    {
        Set<String> ns = new HashSet<>();
        ns.addAll(resources.keySet());
        ns.addAll(binaryResources.keySet());
        return ns;
    }

    public List<String> listPaths(String namespace, String prefix)
    {
        List<String> result = new ArrayList<>();

        Map<String, Supplier<String>> nsMap = resources.get(namespace);
        if (nsMap != null)
            for (String path : nsMap.keySet())
                if (path.startsWith(prefix))
                    result.add(path);

        Map<String, Supplier<byte[]>> binMap = binaryResources.get(namespace);
        if (binMap != null)
            for (String path : binMap.keySet())
                if (path.startsWith(prefix))
                    result.add(path);

        return result;
    }

    public void clear()
    {
        resources.clear();
        binaryResources.clear();
    }
}