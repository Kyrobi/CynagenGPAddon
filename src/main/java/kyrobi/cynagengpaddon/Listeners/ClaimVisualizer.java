package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimInspectionEvent;
import me.ryanhamshire.GriefPrevention.util.BoundingBox;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class ClaimVisualizer implements Listener {

    private CynagenGPAddon plugin;

    private static final long DISPLAY_TIME = 5 * 20L; // Display time in ticks (1 second = 20 ticks)
    HashMap<String, BukkitTask> visualQueue = new HashMap<>();

    public ClaimVisualizer(CynagenGPAddon plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClaimVisualize(ClaimInspectionEvent e){

        if(e.getClaims().isEmpty()){
            return;
        }

        if(visualQueue.get(e.getPlayer().getName()) != null){
            visualQueue.get(e.getPlayer().getName()).cancel();
        }

        visualQueue.put(e.getPlayer().getName(),
                new BukkitRunnable(){
                    int counter = 0;

                    @Override
                    public void run(){
                /*
                Get all the claims and loop through them
                 */
                        ArrayList<Claim> nearbyClaims = new ArrayList<>(e.getClaims());
                        for(Claim i: nearbyClaims){
                            Location corner1 = i.getLesserBoundaryCorner();
                            Location corner2 = i.getGreaterBoundaryCorner();

                            int maxHeight = e.getPlayer().getLocation().getBlockY() + 30;
                            int minHeight = e.getPlayer().getLocation().getBlockY() - 30;
                            int spacing = 5;
                            int boundarySpacing = 5;
                            generateClaimOutline(e.getPlayer(), corner1, corner2, spacing, boundarySpacing, minHeight, maxHeight);
                        }

                        counter++;
                        if(counter >= 40){
                            visualQueue.remove(e.getPlayer().getName());
                            cancel();
                        }
                    }
                }.runTaskTimerAsynchronously(plugin, 0, 10L)
        );
    }

    public void generateClaimOutline(Player player, Location corner1, Location corner2, double verticalSpacing, double boundarySpacing, int minHeight, int maxHeight) {
        World world = player.getWorld();

        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        /*
        Don't show claims that are far away
         */

        // Generate particles for the four corners
        for (double y = minY; y <= maxHeight; y += verticalSpacing - 1) {
            spawnParticleCorner(world, player, minX, y, minZ);
            spawnParticleCorner(world, player, minX, y, maxZ);
            spawnParticleCorner(world, player, maxX, y, minZ);
            spawnParticleCorner(world, player, maxX, y, maxZ);
        }

        // Generate additional vertical lines along the boundaries
        for (double y = Math.max(minY + verticalSpacing, minHeight); y <= Math.min(maxHeight, world.getMaxHeight()); y += verticalSpacing) {
            // Vertical lines along X-axis boundaries
            for (double z = minZ + boundarySpacing; z < maxZ; z += boundarySpacing) {
                spawnParticle(world, player, minX, y, z);
                spawnParticle(world, player, maxX, y, z);
            }

            // Vertical lines along Z-axis boundaries
            for (double x = minX + boundarySpacing; x < maxX; x += boundarySpacing) {
                spawnParticle(world, player, x, y, minZ);
                spawnParticle(world, player, x, y, maxZ);
            }
        }
    }

    private void spawnParticle(World world, Player player, double x, double y, double z) {
        Location particleLoc = new Location(world, x, y, z);
        // player.spawnParticle(Particle.SPELL_WITCH, particleLoc, 1, 0, 0, 0, 0); // Good particle
        player.spawnParticle(Particle.CLOUD, particleLoc, 1, 0, 0, 0, 0); // Good particle 110fps
        // player.spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.FUCHSIA, 2)); 22fps
    }

    private void spawnParticleCorner(World world, Player player, double x, double y, double z) {
        Location particleLoc = new Location(world, x, y, z);
        player.spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0); // Good particle 110fps
    }
}
