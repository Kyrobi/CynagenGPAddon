package kyrobi.cynagengpaddon.Storage;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringJoiner;

@Getter
@Setter
public class ClaimData {

    int claimID = 0;
    long creationDate = 0;
    String creator = "";
    String creatorUUID = "";
    String claimName = "";
    boolean allowPvP = false;
    ArrayList<String> noEnterPlayer = new ArrayList<>();
    String enterMessage = "";
    String exitMessage = "";

    public ClaimData(int claimID, long creationDate, String creator, String creatorUUID, String claimName, boolean allowPvP, String noEnterPlayer, String enterMessage, String exitMessage){
        this.claimID = claimID;
        this.creationDate = creationDate;

        if(creator != null){
            this.creator = creator;
        }
        if(creatorUUID != null){
            this.creatorUUID = creatorUUID;
        }
        if(claimName != null){
            this.claimName = claimName;
        }

        this.allowPvP = allowPvP;

        if(noEnterPlayer != null){
            String[] noEnterPlayers = noEnterPlayer.split(",");
            this.noEnterPlayer.addAll(Arrays.asList(noEnterPlayers));
        }

        if(enterMessage != null){
            this.enterMessage = enterMessage;
        }

        if(exitMessage != null){
            this.exitMessage = exitMessage;
        }
    }

    public String getNoEnterPlayerString(){
        StringJoiner joiner = new StringJoiner(",");

        for(String s: noEnterPlayer){
            joiner.add(s);
        }

        return joiner.toString();
    }
}
