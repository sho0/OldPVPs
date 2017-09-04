package pcsnode.com.oldpvps;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sho on 2017/09/02.
 */
public class OldPVPAPI {
    private static HashMap<UUID,Boolean> oldPVPEnabled = new HashMap<>();
    private static HashMap<UUID,Boolean> blocking = new HashMap<>();
    private static HashMap<UUID,ItemStack> blockItem = new HashMap<>();

    public void deleteBlockItem(UUID uuid){
        blockItem.remove(uuid);
    }

    public void setBlockItem(UUID uuid,ItemStack item){
        blockItem.put(uuid,item);
    }

    public ItemStack getBlockItem(UUID uuid){
        if(!blockItem.containsKey(uuid)){
            return null;
        }
        return blockItem.get(uuid);
    }

    public void block(UUID uuid){
        blocking.put(uuid,true);
    }

    public void unBlock(UUID uuid){
        blocking.put(uuid,false);
    }

    public boolean isBlocking(UUID uuid){
        if(!blocking.containsKey(uuid)){
            return false;
        }
        return blocking.get(uuid);
    }

    public void enableOldPVP(UUID uuid){
        oldPVPEnabled.put(uuid,true);
        Bukkit.getPlayer(uuid).getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100D);
    }

    public void disableOldPVP(UUID uuid){
        oldPVPEnabled.put(uuid,false);
        Bukkit.getPlayer(uuid).getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4D);
    }

    public boolean isEnabledOldPVP(UUID uuid){
        if(!oldPVPEnabled.containsKey(uuid)){
            return false;
        }
        return  oldPVPEnabled.get(uuid);
    }
}
