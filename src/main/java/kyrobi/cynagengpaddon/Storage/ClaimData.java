package kyrobi.cynagengpaddon.Storage;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
public class ClaimData {

    private final String DELIMITER = " ";

    long claimID = 0;
    long creationDate = 0;
    String creator = "";
    String creatorUUID = "";
    String claimName = "[No Name]";
    boolean allowPvP = false;
    Set<String> noEnterPlayer = new HashSet<>();
    String enterMessage = "";
    String exitMessage = "";
    String iconMaterialName = "";

    public ClaimData(long claimID, long creationDate, String creator, String creatorUUID, String claimName, boolean allowPvP, String noEnterPlayer, String enterMessage, String exitMessage, String iconMaterialName){
        this.claimID = claimID;
        this.creationDate = creationDate;

        if(creator != null){
            this.creator = creator;
        }
        if(creatorUUID != null){
            this.creatorUUID = creatorUUID;
        }
        if(claimName != null){
            if(claimName.isEmpty()){
                this.claimName = "[No Name]";
            } else {
                this.claimName = claimName;
            }
        }

        this.allowPvP = allowPvP;

        if(noEnterPlayer != null){
            String[] noEnterPlayers = noEnterPlayer.split(DELIMITER);
            this.noEnterPlayer.addAll(Arrays.asList(noEnterPlayers));
        }

        if(enterMessage != null){
            this.enterMessage = enterMessage;
        }

        if(exitMessage != null){
            this.exitMessage = exitMessage;
        }

        if(iconMaterialName != null){
            this.iconMaterialName = iconMaterialName;
        }
    }

    public ClaimData(long claimID, Player claimCreator){
        this.claimID = claimID;
        this.creationDate = System.currentTimeMillis();

        this.creator = claimCreator.getName();
        this.creatorUUID = claimCreator.getUniqueId().toString();
    }

    /*
    This is ONLY a helper function for the Datastore. Do not use outside of it.
     */
    String getNoEnterPlayerString(){
        StringBuilder stringBuilder = new StringBuilder();

        for(String s: noEnterPlayer){
            stringBuilder.append(s + DELIMITER);
        }

        return stringBuilder.toString();
    }
}
