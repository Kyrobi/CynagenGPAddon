package kyrobi.cynagengpaddon.Listeners;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimInspectionEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClaimVisualizer implements Listener {

    private CynagenGPAddon plugin;

    private static final long DISPLAY_TIME = 5 * 20L; // Display time in ticks (1 second = 20 ticks)
    HashMap<String, BukkitTask> visualQueue = new HashMap<>();
    static Set<String> claimTooLargeWarning = new HashSet<>();
    static int maxClaimSize = 1000;

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

        ArrayList<Claim> nearbyClaims = new ArrayList<>(e.getClaims());


//        ArrayList<TextDisplay> textEntitiesList = new ArrayList<>();
//        int tempCounter = 0;
//        for(Claim i: nearbyClaims){
//            System.out.println("Counter: " + tempCounter++);
//            textEntitiesList.add(spawnTextEntity(i.getLesserBoundaryCorner().getWorld(), e.getPlayer()));
//        }
//
//        AtomicInteger removeCounter = new AtomicInteger();
//        Bukkit.getScheduler().runTaskLater(plugin, () -> {
//            for(TextDisplay i: textEntitiesList){
//                System.out.println("Removing: " + removeCounter.getAndIncrement());
//                i.remove();
//            }
//
//        }, 20 * 20L);


        visualQueue.put(e.getPlayer().getName(),
                new BukkitRunnable(){
                    int counter = 0;

                    @Override
                    public void run(){
                /*
                Get all the claims and loop through them
                 */

                        for(Claim i: nearbyClaims){
                            Location corner1 = i.getLesserBoundaryCorner();
                            Location corner2 = i.getGreaterBoundaryCorner();

                            /*
                            Clamp down on claims that are too big
                             */
                            int xSize = Math.abs(corner2.getBlockX() - corner1.getBlockX()) + 1;
                            int zSize = Math.abs(corner2.getBlockZ() - corner1.getBlockZ()) + 1;

                            // Check if claim size exceeds the limit
                            if (xSize <= maxClaimSize && zSize <= maxClaimSize) {
                                int maxHeight = e.getPlayer().getLocation().getBlockY() + 25;
                                int minHeight = e.getPlayer().getLocation().getBlockY() - 10;
                                int spacing = 6;
                                int boundarySpacing = 5;
                                generateClaimOutline(e.getPlayer(), corner1, corner2, spacing, boundarySpacing, minHeight, maxHeight);
                            } else {
                                if(!claimTooLargeWarning.contains(e.getPlayer().getName())){
                                    e.getPlayer().sendMessage(ChatColor.RED + "There is a claim that is too large to visualize. Not showing particles.");
                                    claimTooLargeWarning.add(e.getPlayer().getName());
                                }
                            }

                        }

                        counter++;

                        if(counter >= 40){
                            visualQueue.remove(e.getPlayer().getName());
                            claimTooLargeWarning.remove(e.getPlayer().getName());
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
        for (double y = minHeight; y <= maxHeight; y += (verticalSpacing )) {
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
        //player.spawnParticle(Particle.FLAME, particleLoc, 1, 0, 0, 0, 0); // Good particle 110fps

        BlockData material = Material.BARRIER.createBlockData();
        player.spawnParticle(Particle.BLOCK_MARKER, particleLoc, 1, 0, 0, 0, 0, material);
    }

//    private TextDisplay spawnTextEntity(World world, Player playe){
//
//        // Get the corner locations
//        int x1 = -140;
//        int y1 = 85;
//        int z1 = 729;
//
//        int x2 = -140;
//        int y2 = 736;
//        int z2 = 736;
//
//        int x3 = -140;
//        int y3 = 79;
//        int z3 = 729;
//
//        int x4 = -140;
//        int y4 = 79;
//        int z4 = 736;
//
//        Location corner1 = new Location(world, x1, y1, z1);
//        Location corner2 = new Location(world, x2, y2, z2);
//        Location corner3 = new Location(world, x3, y3, z3);
//        Location corner4 = new Location(world, x4, y4, z4);
//
//        // Calculate center point
//        //double centerX = (x1 + x2 + x3 + x4) / 4.0;
//        //double centerY = (y1 + y2 + y3 + y4) / 4.0;
//        //double centerZ = (z1 + z2 + z3 + z4) / 4.0;
//
//        // Spawn TextDisplay at center
//        //TextDisplay text = (TextDisplay) world.spawnEntity(new Location(world, centerX, centerY, centerZ), EntityType.TEXT_DISPLAY);
//        TextDisplay text = (TextDisplay) world.spawnEntity(new Location(world, x1, y1, z1), EntityType.TEXT_DISPLAY);
//
//        // Calculate width and height
//        //double width = Math.max(Math.abs(x1 - x2), Math.abs(x3 - x4));
//        //double height = Math.max(Math.abs(z1 - z3), Math.abs(z2 - z4));
//        double width = 100;
//        double height = 100;
//
//        // Get the normal vector of the wall
//        Vector3f wallNormal = new Vector3f(0, 0, 1);
//
//// Create a rotation to align with the wall normal
//        Quaternionf rotation = new Quaternionf().rotationTo(wallNormal, new Vector3f(0, 0, 1));
//        Vector3f zeroTranslation = new Vector3f(0, 0, 0);
//        Quaternionf identity = new Quaternionf(0, 0, 0, 1);
//
//// Create the transformation
//        Transformation transform = new Transformation(
//                zeroTranslation, // no translation
//                rotation,
//                new Vector3f(10, 10, 10), // no scaling
//                identity // no final rotation
//        );
//
//
//        // Set size
//        text.setTransformation(transform);
//        text.text(Component.text("■").color(TextColor.color(255, 255, 0)));
//        text.setGlowing(false);
//
//        // Center text
//        text.setBillboard(Display.Billboard.FIXED);
//        text.setDefaultBackground(false);
//        text.setBackgroundColor(Color.fromARGB(0));
//
//        int opacityPercent = 20;
//        text.setTextOpacity((byte)((opacityPercent / 100f) * 255));
//        return text;
//    }
}
