package me.rigbot.KMCLootProtection;

import org.bukkit.event.Listener;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Core
implements Listener
{
	private Main main;
	public Core(Main instance) {
	  this.main = instance;
	} 
	
	public boolean RemoveItems(ItemStack item) {
		Material itemType = item.getType();
		if (itemType.equals(Material.GOLD_HELMET)|| 
			itemType.equals(Material.GOLD_CHESTPLATE)||
			itemType.equals(Material.GOLD_LEGGINGS)||
			itemType.equals(Material.GOLD_BOOTS)||
			itemType.equals(Material.ARROW)||
			itemType.equals(Material.DIAMOND_AXE)||
			itemType.equals(Material.BOW)) {
			if (item.getEnchantments().size() < 1) {
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
	  Player victim = event.getEntity();
	  
	  if (!(victim.getKiller() instanceof Player)) {
	    return;
	  }
	  final Player killer = victim.getKiller();
	  UUID killerUUID = killer.getUniqueId();
	  List<ItemStack> drops = event.getDrops();
	  World world = victim.getWorld();
	  long time = System.currentTimeMillis() + (15000);
	  FixedMetadataValue fixedMetadataValue = new FixedMetadataValue(this.main, String.valueOf(time) + ":" + killerUUID);
	  
	  for (int i = 0; i < drops.size(); i++) {
	    ItemStack itemStack = (ItemStack)drops.get(i);
	    if (!RemoveItems(itemStack)) {
		    Item item = world.dropItemNaturally(victim.getLocation(), itemStack);
		    item.setMetadata("LootProtect", fixedMetadataValue);
	    }
	  } 
	  
	  drops.clear();
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
	  Player player = event.getPlayer();
	  Item item = event.getItem();
	  
	  if (!item.hasMetadata("LootProtect")) {
	    return;
	  }
	
	  String[] data = ((MetadataValue)item.getMetadata("LootProtect").get(0)).asString().split(":");
	  long time = Long.parseLong(data[0]);
	  UUID killerUUID = UUID.fromString(data[1]);
	  
	  if (player.getUniqueId().equals(killerUUID)) {
	    return;
	  }
	  if (System.currentTimeMillis() > time) {
	    return;
	  }
	  event.setCancelled(true);
	}
}
