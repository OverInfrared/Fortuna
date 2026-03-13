package infrared.fortuna.equipment;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;

public interface IFortunaEquipment
{
    String getEquipmentName();
    JsonObject getEquipmentAsset();
}