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
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClaimVisualizer implements Listener {

    public enum FACE {
        NORTH_OUTSIDE,
        SOUTH_OUTSIDE,
        EAST_OUTSIDE,
        WEST_OUTSIDE,
        NORTH_INSIDE,
        SOUTH_INSIDE,
        EAST_INSIDE,
        WEST_INSIDE
    }

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

//                        for(Claim i: nearbyClaims){
//                            Location corner1 = i.getLesserBoundaryCorner();
//                            Location corner2 = i.getGreaterBoundaryCorner();
//
//                            /*
//                            Clamp down on claims that are too big
//                             */
//                            int xSize = Math.abs(corner2.getBlockX() - corner1.getBlockX()) + 1;
//                            int zSize = Math.abs(corner2.getBlockZ() - corner1.getBlockZ()) + 1;
//
//                            // Check if claim size exceeds the limit
//                            if (xSize <= maxClaimSize && zSize <= maxClaimSize) {
//                                int maxHeight = e.getPlayer().getLocation().getBlockY() + 25;
//                                int minHeight = e.getPlayer().getLocation().getBlockY() - 10;
//                                int spacing = 6;
//                                int boundarySpacing = 5;
//                                generateClaimOutline(e.getPlayer(), corner1, corner2, spacing, boundarySpacing, minHeight, maxHeight);
//                            } else {
//                                if(!claimTooLargeWarning.contains(e.getPlayer().getName())){
//                                    e.getPlayer().sendMessage(ChatColor.RED + "There is a claim that is too large to visualize. Not showing particles.");
//                                    claimTooLargeWarning.add(e.getPlayer().getName());
//                                }
//                            }
//
//                        }

                        /*
                        TESTING START

                         */
//                        for (Claim i : nearbyClaims) {
//                            Location corner1 = i.getLesserBoundaryCorner();
//                            Location corner2 = i.getGreaterBoundaryCorner();
//                            World world = corner1.getWorld();
//
//                            // Calculate the other two corners
//                            Location corner3 = new Location(world, corner1.getX(), corner1.getY(), corner2.getZ());
//                            Location corner4 = new Location(world, corner2.getX(), corner2.getY(), corner1.getZ());
//
//                            // Create walls - EAST
//                            double top1 ;
//                            double top2;
//                            double bottom1 = corner2.getX();
//                            double bottom2 = corner4.getX();
//                            // spawnTextDisplayEast(world, , FACE.EAST_INSIDE);
//
//
//
//                            System.out.println("Corner 1: " + corner1);
//                            System.out.println("Corner 2: " + corner2);
//                            System.out.println("Corner 3: " + corner3);
//                            System.out.println("Corner 4: " + corner4);
//                        }

                        /*
                        TESTING END
                         */

                        counter++;

                        if(counter >= 40){
                            visualQueue.remove(e.getPlayer().getName());
                            claimTooLargeWarning.remove(e.getPlayer().getName());
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 10L)
        );

        /*
        TESTING START
         */

//         World world = Bukkit.getWorld("world");
//        Location corner1 = new Location(world, 241, -63, 24);
//        Location corner2 = new Location(world, 241, -50, 24);
//        Location corner3 = new Location(world, 241, -63, 9);
//        Location corner4 = new Location(world, 241, -50, 9);
//        spawnTextDisplayEast(world, corner1, corner2, corner3, corner4, FACE.EAST_OUTSIDE);
//        spawnTextDisplayEast(world, corner1, corner2, corner3, corner4, FACE.EAST_INSIDE);

//        spawnTextDisplay(world, corner1, corner2, corner3, corner4, FACE.WEST_OUTSIDE);
//        spawnTextDisplay(world, corner1, corner2, corner3, corner4, FACE.WEST_INSIDE);
//
//        spawnTextDisplay(world, corner1, corner2, corner3, corner4, FACE.NORTH_OUTSIDE);
//        spawnTextDisplay(world, corner1, corner2, corner3, corner4, FACE.NORTH_INSIDE);
//
//        spawnTextDisplay(world, corner1, corner2, corner3, corner4, FACE.SOUTH_OUTSIDE);
//        spawnTextDisplay(world, corner1, corner2, corner3, corner4, FACE.SOUTH_INSIDE);


        Player player = e.getPlayer();
        double playerY = player.getY() - 2;
        for (Claim i : nearbyClaims) {
            Location corner1 = i.getLesserBoundaryCorner();
            Location corner2 = i.getGreaterBoundaryCorner();
            World world = corner1.getWorld();

            // Calculate the other two corners
            Location corner3 = new Location(world, corner1.getX(), corner1.getY(), corner2.getZ());
            Location corner4 = new Location(world, corner2.getX(), corner2.getY(), corner1.getZ());

            // EAST
            Location corner1_ = new Location(world, corner1.getBlockX(), playerY, corner1.getBlockZ());
            Location corner2_ = new Location(world, corner2.getBlockX(), playerY, corner2.getBlockZ());
            Location corner3_ = new Location(world, corner3.getBlockX(), playerY, corner3.getBlockZ());
            Location corner4_ = new Location(world, corner4.getBlockX(), playerY, corner4.getBlockZ());



            spawnTextDisplayEast(world, corner2_, corner2_, corner4_, corner4_ , player, FACE.EAST_INSIDE);
            spawnTextDisplayEast(world, corner2_, corner2_, corner4_, corner4_ , player, FACE.EAST_OUTSIDE);

            // WEST
            spawnTextDisplayEast(world, corner1_, corner1_, corner3_, corner3_ , player, FACE.WEST_INSIDE);
            spawnTextDisplayEast(world, corner1_, corner1_, corner3_, corner3_ , player, FACE.WEST_OUTSIDE);

            // NORTH
            spawnTextDisplayEast(world, corner1_, corner1_, corner4_, corner4_ , player, FACE.NORTH_INSIDE);
            spawnTextDisplayEast(world, corner1_, corner1_, corner4_, corner4_ , player, FACE.NORTH_OUTSIDE);

            // SOUTH
            spawnTextDisplayEast(world, corner3_, corner3_, corner2_, corner2_ , player, FACE.SOUTH_INSIDE);
            spawnTextDisplayEast(world, corner3_, corner3_, corner2_, corner2_ , player, FACE.SOUTH_OUTSIDE);



            System.out.println("Corner 1: " + corner1);
            System.out.println("Corner 2: " + corner2);
            System.out.println("Corner 3: " + corner3);
            System.out.println("Corner 4: " + corner4);
        }


        /*
        TESTING ENDS
         */
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

//    private void spawnTextDisplayEast(World world, Location corner1, Location corner2, Location corner3, Location corner4, Player player, FACE face) {
//        // Calculate center position
//        double centerX = (corner1.getX() + corner2.getX() + corner3.getX() + corner4.getX()) / 4.0 + 0.5;
//        double centerY = (corner1.getY() + corner2.getY() + corner3.getY() + corner4.getY()) / 4.0 + 0.5;
//        double centerZ = (corner1.getZ() + corner2.getZ() + corner3.getZ() + corner4.getZ()) / 4.0 + 0.5;
//
//        // Logging the calculated center position
//        Bukkit.getLogger().info("Center Position: X=" + centerX + ", Y=" + centerY + ", Z=" + centerZ);
//
//        // Calculate width and height
//        double width = Math.abs(corner1.getZ() - corner3.getZ());
//        double height = Math.abs(corner1.getX() - corner4.getX());
//
//        // Logging the width and height
//        Bukkit.getLogger().info("Width: " + width + ", Height: " + height);
//
//        Location centerLocation = new Location(world, centerX, centerY, centerZ);
//        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(centerLocation, EntityType.TEXT_DISPLAY);
//
//        textDisplay.text(Component.text("■").color(TextColor.color(255, 255, 0)));
//        textDisplay.setBillboard(Display.Billboard.FIXED);
//        textDisplay.setDefaultBackground(false);
//        textDisplay.setBackgroundColor(Color.fromARGB(0));
//        textDisplay.setTextOpacity((byte) 127);
//
//        Vector3f translation = new Vector3f(0, 0, 0);
//        AxisAngle4f rotation = new AxisAngle4f(0, 0, 0, 0);
//        Vector3f scale = new Vector3f(0, 0, 0);
//
//        switch (face) {
//            case EAST_OUTSIDE:
//            case WEST_OUTSIDE:
//                rotation = new AxisAngle4f((float) Math.toRadians(90), 0, 1, 0);
//                translation = new Vector3f(0, 0, 1.5F);
//                Bukkit.getLogger().info(face.toString());
//                break;
//            case EAST_INSIDE:
//            case WEST_INSIDE:
//                rotation = new AxisAngle4f((float) Math.toRadians(270), 0, 1, 0);
//                translation = new Vector3f(0, 0, -1.5F);
//                Bukkit.getLogger().info(face.toString());
//                break;
//            case NORTH_OUTSIDE:
//            case SOUTH_INSIDE:
//                rotation = new AxisAngle4f(0, 0, 1, 0);
//                translation = new Vector3f(-1F, 0, 0);
//                Bukkit.getLogger().info(face.toString());
//                break;
//            case NORTH_INSIDE:
//            case SOUTH_OUTSIDE:
//                rotation = new AxisAngle4f((float) Math.toRadians(180), 0, 1, 0);
//                translation = new Vector3f(1F, 0, 0);
//                Bukkit.getLogger().info(face.toString());
//                break;
//        }
//
//        // Scale calculation based on face
//        if (face == FACE.EAST_OUTSIDE || face == FACE.EAST_INSIDE || face == FACE.WEST_OUTSIDE || face == FACE.WEST_INSIDE) {
//            scale = new Vector3f(8 * (float) width, 20, 1);
//        } else {
//            scale = new Vector3f(8 * (float) height, 20, 1);
//        }
//
//        // Logging the transformation parameters
//        Bukkit.getLogger().info("Translation: " + translation + ", Rotation: " + rotation + ", Scale: " + scale);
//
//        Transformation transformation = new Transformation(translation, rotation, scale, new AxisAngle4f(0, 0, 0, 0));
//        textDisplay.setTransformation(transformation);
//    }

    private void spawnTextDisplayEast(World world, Location corner1, Location corner2, Location corner3, Location corner4, Player player, FACE face) {
        // Calculate center position
        double centerX = (corner1.getX() + corner2.getX() + corner3.getX() + corner4.getX()) / 4.0 + 0.5;
        double centerY = (corner1.getY() + corner2.getY() + corner3.getY() + corner4.getY()) / 4.0 + 0.5;
        double centerZ = (corner1.getZ() + corner2.getZ() + corner3.getZ() + corner4.getZ()) / 4.0 + 0.5;

        // Logging the calculated center position
        Bukkit.getLogger().info("Center Position: X=" + centerX + ", Y=" + centerY + ", Z=" + centerZ);

        // Calculate width and height
        double width = Math.abs(corner1.getZ() - corner3.getZ());
        double height = Math.abs(corner1.getX() - corner4.getX());

        // Logging the width and height
        Bukkit.getLogger().info("Width: " + width + ", Height: " + height);

        Location centerLocation = new Location(world, centerX, centerY, centerZ);
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(centerLocation, EntityType.TEXT_DISPLAY);

        textDisplay.text(Component.text("■").color(TextColor.color(255, 255, 0)));
        textDisplay.setBillboard(Display.Billboard.FIXED);
        textDisplay.setDefaultBackground(false);
        textDisplay.setBackgroundColor(Color.fromARGB(0));
        textDisplay.setTextOpacity((byte) 127);
        textDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);

        Vector3f translation = new Vector3f(0, 0, 0);
        AxisAngle4f rotation = new AxisAngle4f(0, 0, 0, 0);
        Vector3f scale = new Vector3f(0, 0, 0);

        switch (face) {
            case EAST_OUTSIDE:
            case WEST_OUTSIDE:
                rotation = new AxisAngle4f((float) Math.toRadians(90), 0, 1, 0);
                // translation = new Vector3f(0, 0, 1.5F);
                translation = new Vector3f(0, 0, ((float)width)/10);
                Bukkit.getLogger().info(face.toString());
                break;
            case EAST_INSIDE:
            case WEST_INSIDE:
                rotation = new AxisAngle4f((float) Math.toRadians(270), 0, 1, 0);
                // translation = new Vector3f(0, 0, -1.5F);
                translation = new Vector3f(0, 0, ((float)-width)/10);
                Bukkit.getLogger().info(face.toString());
                break;
            case NORTH_OUTSIDE:
            case SOUTH_INSIDE:
                rotation = new AxisAngle4f(0, 0, 1, 0);
                // translation = new Vector3f(-1F, 0, 0);
                translation = new Vector3f(((float)-height)/10, 0, 0);
                Bukkit.getLogger().info(face.toString());
                break;
            case NORTH_INSIDE:
            case SOUTH_OUTSIDE:
                rotation = new AxisAngle4f((float) Math.toRadians(180), 0, 1, 0);
                // translation = new Vector3f(1F, 0, 0);
                translation = new Vector3f(((float)height)/10, 0, 0);
                Bukkit.getLogger().info(face.toString());
                break;
        }

        // Scale calculation based on face
        if (face == FACE.EAST_OUTSIDE || face == FACE.EAST_INSIDE || face == FACE.WEST_OUTSIDE || face == FACE.WEST_INSIDE) {
            scale = new Vector3f(8 * (float) width, 20, 1);
        } else {
            scale = new Vector3f(8 * (float) height, 20, 1);
        }

        // Logging the transformation parameters
        Bukkit.getLogger().info("Translation: " + translation + ", Rotation: " + rotation + ", Scale: " + scale);

        Transformation transformation = new Transformation(translation, rotation, scale, new AxisAngle4f(0, 0, 0, 0));
        textDisplay.setTransformation(transformation);
    }



}
