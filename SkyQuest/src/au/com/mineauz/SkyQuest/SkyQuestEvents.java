package au.com.mineauz.SkyQuest;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.SkyQuest.pedestals.DebugPedestal;
import au.com.mineauz.SkyQuest.pedestals.Pedestals;

public class SkyQuestEvents implements Listener{
	
	private Map<Player, ItemStack> droppedBook = new HashMap<Player, ItemStack>(); //TODO: Probably move this to a Data class.
    
    @EventHandler
	private void onRightClickGround(PlayerInteractEvent event)
	{
		// Test of pedestal creation
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock() && (event.hasItem() && event.getItem().getType() == Material.BOOK))
		{
			Location loc = event.getClickedBlock().getRelative(event.getBlockFace(), 1).getLocation();
			Pedestals.addPedestal(new DebugPedestal(loc));
		}
	}
    
    @EventHandler
    private void playerDeath(PlayerDeathEvent event){
    	Player player = (Player) event.getEntity();
		for(ItemStack item : event.getDrops()){
			if(item.getType() == Material.WRITTEN_BOOK && MagicBook.isMagicBook(item)){
				//Save the magic book to be given on respawn.
				droppedBook.put(player, item);
				event.getDrops().remove(item);
				break;
			}
		}
    }
    
    @EventHandler
    private void playerRespawn(PlayerRespawnEvent event){
    	if(droppedBook.containsKey(event.getPlayer())){
    		//If the player had died with a magic book, give it back to them.
    		event.getPlayer().getInventory().addItem(droppedBook.get(event.getPlayer()));
    		droppedBook.remove(event.getPlayer());
    	}
    }
    
    @EventHandler
    private void magicBookDrop(PlayerDropItemEvent event){
    	if(MagicBook.isMagicBook(event.getItemDrop().getItemStack())){
    		event.setCancelled(true);
    		event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "This book is bound to you by magic!");
    	}
    }
    
    @EventHandler
    private void magicBookForcedDropped(ItemSpawnEvent event){
    	if(event.getEntityType() == EntityType.DROPPED_ITEM){
    		Item item = (Item) event.getEntity();
    		if(item != null && MagicBook.isMagicBook(item.getItemStack())){
    			MagicBook mb = new MagicBook(item.getItemStack());
    			String owner = mb.getHandle().tag.getString("Owner");
    			if(Bukkit.getServer().getPlayer(owner) != null){
    				Player ply = Bukkit.getServer().getPlayer(owner);
    				ply.getInventory().addItem(mb);
    				ply.sendMessage(ChatColor.LIGHT_PURPLE + "Your magic book was dropped and found its way back to you!");
    				event.setCancelled(true);
    			}
    			else{
    				Location loc = event.getLocation();
    				loc.getBlock().setType(Material.CHEST);
    				if(loc.getBlock().getState() instanceof Chest){
	    				Chest chest = (Chest) loc.getBlock().getState();
	    				chest.getInventory().addItem(item.getItemStack());
	    				event.setCancelled(true);
	    				// Its being called but items aren't being put into the chest >:(
	    				// Help me out here!
    				}
    			}
    		}
    	}
    }
}