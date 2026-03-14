package infrared.fortuna.worldgen;

public enum SpawnProfile
{
    Spread,     // Wide height range, gentle bell curve
    Deep,       // Peak below buildable space, tapers up
    Pocket,     // Narrow height band, high density
    Surface,    // Near terrain surface, for sand/gravel ores
    Scattered,  // Low uniform chance everywhere
    Deposit     // Large vein within host material blob, rare
}