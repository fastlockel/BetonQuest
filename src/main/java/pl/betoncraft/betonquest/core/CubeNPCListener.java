/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.core;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.ConfigHandler;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Listener which starts conversation on clicking on NPCs made from blocks.
 * 
 * @author Co0sh
 */
public class CubeNPCListener implements Listener {

    /**
     * Creates new instance of the default NPC listener
     */
    public CubeNPCListener() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * This limits NPC creation by canceling all sign edits where first line is
     * "[NPC]"
     * 
     * @param event
     *            SignChangeEvent
     */
    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[NPC]")
            && !event.getPlayer().hasPermission("betonquest.admin")) {
            // if the player doesn't have the required permission deny the
            // editing
            event.setCancelled(true);
            event.getPlayer().sendMessage(
                    ConfigHandler.getString(
                            "messages." + ConfigHandler.getString("config.language")
                                + ".no_permission").replaceAll("&", "§"));
        }
    }

    /**
     * This checks if the player clicked on valid NPC, and starts the
     * conversation
     * 
     * @param event
     *            PlayerInteractEvent
     */
    @EventHandler
    public void onNPCClick(PlayerInteractEvent event) {
        // check if the player has required permission
        if (!event.getPlayer().hasPermission("betonquest.conversation")) {
            return;
        }
        // check if the blocks are placed in the correct way
        String conversationID = null;
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
            && event.getClickedBlock().getType().equals(Material.SKULL)) {
            Block block = event.getClickedBlock().getLocation().clone().add(0, -1, 0).getBlock();
            if (block.getType().equals(Material.STAINED_CLAY)) {
                Block[] signs = new Block[] { block.getRelative(BlockFace.EAST),
                    block.getRelative(BlockFace.WEST), block.getRelative(BlockFace.NORTH),
                    block.getRelative(BlockFace.SOUTH) };
                Sign theSign = null;
                byte count = 0;
                for (Block sign : signs) {
                    if (sign.getType().equals(Material.WALL_SIGN)
                        && sign.getState() instanceof Sign) {
                        theSign = (Sign) sign.getState();
                        count++;
                    }
                }
                if (count == 1 && theSign != null && theSign.getLine(0).equalsIgnoreCase("[NPC]")) {
                    conversationID = theSign.getLine(1);
                }
            }

        }
        // if the conversation ID was extracted from NPC then start the
        // conversation
        if (conversationID != null)
            new Conversation(PlayerConverter.getID(event.getPlayer()), conversationID, event
                    .getClickedBlock().getLocation().add(0.5, -1, 0.5));
    }
}
