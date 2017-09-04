package pcsnode.com.oldpvps;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;
import java.util.jar.Attributes;

public final class OldPVPs extends JavaPlugin implements Listener {

    OldPVPAPI api = new OldPVPAPI();
    HashMap<UUID,Vector> fishingRod = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        for(Player p : Bukkit.getOnlinePlayers()){
            api.enableOldPVP(p.getUniqueId());
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player p: Bukkit.getOnlinePlayers()){
                    if(api.isEnabledOldPVP(p.getUniqueId())){
                        if(api.isBlocking(p.getUniqueId())){
                            if(!((p.getInventory().getItemInMainHand().getType() == Material.WOOD_SWORD) || (p.getInventory().getItemInMainHand().getType() == Material.STONE_SWORD) || (p.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) || (p.getInventory().getItemInMainHand().getType() == Material.GOLD_SWORD) || (p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD))){
                                api.unBlock(p.getUniqueId());
                                p.getInventory().setItemInOffHand(null);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this,0,2);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        e.getEntity().spigot().respawn();
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        api.enableOldPVP(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        api.disableOldPVP(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!api.isEnabledOldPVP(e.getWhoClicked().getUniqueId())){
            return;
        }
        Bukkit.broadcastMessage(String.valueOf(e.getSlot()));
        if(e.getSlot() == 40){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSawp(PlayerSwapHandItemsEvent e){
        if(!api.isEnabledOldPVP(e.getPlayer().getUniqueId())){
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onSwitch(PlayerItemHeldEvent e){
        try {
            if (!api.isEnabledOldPVP(e.getPlayer().getUniqueId())) {
                return;
            }
            if (!e.getPlayer().getInventory().getItem(e.getPreviousSlot()).getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
                e.getPlayer().getInventory().setItem(e.getPreviousSlot(), resetAttackDamage(e.getPlayer().getInventory().getItem(e.getPreviousSlot())));
            }
        }catch (NullPointerException ee){
        }
    }

    @EventHandler
    public void pickUp(PlayerDropItemEvent e){
        if(!api.isEnabledOldPVP(e.getPlayer().getUniqueId())){
            return;
        }
        if(!e.getItemDrop().getItemStack().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)){
            e.getItemDrop().setItemStack(resetAttackDamage(e.getItemDrop().getItemStack()));
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof FishHook)) {
            return;
        }
        Player shooter = (Player)event.getEntity().getShooter();
        if(!api.isEnabledOldPVP(shooter.getUniqueId())){
            return;
        }
            EntityPlayer entityPlayer = ((CraftPlayer)shooter).getHandle();
            EntityFishingHook hook = entityPlayer.hookedFish;
        Player hooked = (Player)event.getHitEntity();
        Vector v = new Vector(0,0.65,0);
        Vector vv = new Vector(0,-fishingRod.get(shooter.getUniqueId()).getY(),0);
        hooked.setVelocity(fishingRod.get(shooter.getUniqueId()).add(v).add(vv).multiply(0.4));
        hooked.damage(0.00000001);
        }

        @EventHandler
        public void projectileShoot(ProjectileLaunchEvent e){
            Player shooter = (Player)e.getEntity().getShooter();
            if(!api.isEnabledOldPVP(shooter.getUniqueId())){
                return;
            }
            if(e.getEntityType() == EntityType.FISHING_HOOK){
                fishingRod.put(shooter.getUniqueId(),shooter.getLocation().getDirection());
            }
        }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent e)
    {
        if(!api.isEnabledOldPVP(e.getDamager().getUniqueId())){
            return;
        }
        if ((e.getDamager() instanceof Snowball))
        {
            e.setDamage(0.00001);
            e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, 80, 1);
        }
        if ((e.getDamager() instanceof Egg))
        {
            e.setDamage(0.0001);
            e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, 80, 1);
        }
        if ((e.getDamager() instanceof EntityFishingHook))
        {
            e.setDamage(0.0001);
            e.getEntity().getWorld().playEffect(e.getEntity().getLocation(), Effect.STEP_SOUND, 80, 1);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(!api.isEnabledOldPVP(e.getPlayer().getUniqueId())){
            return;
        }
        if (((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) &&
                (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) &&
                ((e.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOOD_SWORD) || (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.STONE_SWORD) || (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) || (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.GOLD_SWORD) || (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD)) &&
                (!api.isBlocking(e.getPlayer().getUniqueId()))){
            api.block(e.getPlayer().getUniqueId());
            if ((e.getPlayer().getInventory().getItemInOffHand() != null) && (e.getPlayer().getInventory().getItemInOffHand().getType() != Material.SHIELD)) {
                e.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
            }
        }
    }


    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e){
        if(!api.isEnabledOldPVP(e.getDamager().getUniqueId())){
            return;
        }
        if(e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
            e.setCancelled(true);
        }
    }


    public ItemStack resetAttackDamage(ItemStack i)
    {
        if (i != null)
        {
            Material type = i.getType();
            if (type == Material.WOOD_AXE)
            {
                i = changeDamageValue(i, 3.0D);
                return i;
            }
            if (type == Material.STONE_AXE)
            {
                i = changeDamageValue(i, 4.0D);
                return i;
            }
            if (type == Material.IRON_AXE)
            {
                i = changeDamageValue(i, 5.0D);
                return i;
            }
            if (type == Material.GOLD_AXE)
            {
                i = changeDamageValue(i, 3.0D);
                return i;
            }
            if (type == Material.DIAMOND_AXE)
            {
                i = changeDamageValue(i, 6.0D);
                return i;
            }
            if (type == Material.WOOD_PICKAXE)
            {
                i = changeDamageValue(i, 2.0D);
                return i;
            }
            if (type == Material.STONE_PICKAXE)
            {
                i = changeDamageValue(i, 3.0D);
                return i;
            }
            if (type == Material.IRON_PICKAXE)
            {
                i = changeDamageValue(i, 4.0D);
                return i;
            }
            if (type == Material.GOLD_PICKAXE)
            {
                i = changeDamageValue(i, 2.0D);
                return i;
            }
            if (type == Material.DIAMOND_PICKAXE)
            {
                i = changeDamageValue(i, 5.0D);
                return i;
            }
            if (type == Material.WOOD_SPADE)
            {
                i = changeDamageValue(i, 1.0D);
                return i;
            }
            if (type == Material.STONE_SPADE)
            {
                i = changeDamageValue(i, 2.0D);
                return i;
            }
            if (type == Material.IRON_SPADE)
            {
                i = changeDamageValue(i, 3.0D);
                return i;
            }
            if (type == Material.GOLD_SPADE)
            {
                i = changeDamageValue(i, 1.0D);
                return i;
            }
            if (type == Material.DIAMOND_SPADE)
            {
                i = changeDamageValue(i, 4.0D);
                return i;
            }
            if (type == Material.WOOD_HOE)
            {
                i = changeDamageValue(i, 0.0D);
                return i;
            }
            if (type == Material.STONE_HOE)
            {
                i = changeDamageValue(i, 0.0D);
                return i;
            }
            if (type == Material.IRON_HOE)
            {
                i = changeDamageValue(i, 0.0D);
                return i;
            }
            if (type == Material.GOLD_HOE)
            {
                i = changeDamageValue(i, 0.0D);
                return i;
            }
            if (type == Material.DIAMOND_HOE)
            {
                i = changeDamageValue(i, 0.0D);
                return i;
            }
            if(type == Material.WOOD_SWORD)
            {
                i = changeDamageValue(i,4.0D);
                return i;
            }
            if(type == Material.GOLD_SWORD)
            {
                i = changeDamageValue(i,4.0D);
                return i;
            }
            if(type == Material.STONE_SWORD)
            {
                i = changeDamageValue(i,5.0D);
                return i;
            }
            if(type == Material.IRON_SWORD)
            {
                i = changeDamageValue(i,6.0D);
                return i;
            }
            if(type == Material.DIAMOND_SWORD)
            {
                i = changeDamageValue(i,7.0D);
                return i;
            }
            return i;
        }
        return i;
    }


    public org.bukkit.inventory.ItemStack changeDamageValue(org.bukkit.inventory.ItemStack i, double damageValue)
    {
        net.minecraft.server. v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(i);
        NBTTagCompound compound = null;
        if (nmsStack != null)
        {
            if (nmsStack.hasTag()) {
                compound = nmsStack.getTag();
            } else {
                compound = new NBTTagCompound();
            }
            NBTTagList modifiers = new NBTTagList();
            NBTTagCompound damage = new NBTTagCompound();
            damage.set("AttributeName", new NBTTagString("generic.attackDamage"));
            damage.set("Name", new NBTTagString("generic.attackDamage"));
            damage.set("Amount", new NBTTagDouble(damageValue));
            damage.set("Operation", new NBTTagInt(0));
            damage.set("UUIDLeast", new NBTTagInt(894654));
            damage.set("UUIDMost", new NBTTagInt(2872));
            damage.set("Slot", new NBTTagString("mainhand"));
            damage.set("Slot0", new NBTTagString("offhand"));
            modifiers.add(damage);
            compound.set("AttributeModifiers", modifiers);
            nmsStack.setTag(compound);
            ItemStack item = CraftItemStack.asBukkitCopy(nmsStack);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(itemMeta);
            return item;
        }
        return null;
    }

}
