import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Kevin
 */
public class Room {
    private final int id;
    private final String title;
    private final String description;
    private final String location;
    private final LinkedList<Item> objects;
    private final LinkedList<Exit> exits;
    private final GameCore gameCore;
    private final int NONEXISTANT_EXIT_ID = 0;
    
    public Room(GameCore gameCore, int id, String title, String description, String location) {
        this.objects = new LinkedList<>();
        this.exits = new LinkedList<>();
        this.id = id;
        this.title = title;
        this.description = description;
	    this.location = location;
        this.gameCore = gameCore;
    }

    /**
     * Output a message to all players in this room.
     * @param message to send
     */
    public void broadcast(String message) {
        for (Player player : gameCore.getPlayerList()) {
            if (player.getCurrentRoom() == id)
                player.broadcast(message);
        }
    }
    
    public String toString(PlayerList playerList, Player player) {
        String result = ".-------------------------\n";
        result += "| " + this.getTitle() + "\n";
        result += "-------------------------\n";
        result += this.getDescription() + "\n";
        result += "...................\n";
	result += "This room is " + this.getLocation() + "\n";
        result += "Objects in the area: " + this.getObjects() + "\n";
        result += "Players in the area: " + this.getPlayers(playerList) + "\n";
        result += "Ghouls in the area: " + this.getGhoulsString() + "\n";
        result += "You see paths in these directions: " + this.getExits() + "\n";
        result += "...................\n";
        result += "You are facing: " + player.getCurrentDirection() + "\n";
        if(player.getCurrentRoom() == 1){
            result += "You are near the shop, type ENTER SHOP to enter.\n";
        }
        if(player.getCurrentRoom() == 10){
            result += "Type LEAVE SHOP to leave.\n";
        }
        return result;
    }
    
    public int getId() {
        return this.id;
    }

    public Room getRoom(int id){
      return this;
    }

    public String getExits() {
        String result = "";
        for(Exit exit : this.exits) {
            if(exit.getRoom() != NONEXISTANT_EXIT_ID) {
                result += exit.getDirection().name() + " ";
            }
        }
        return result;
    }
    
    public void addExit(Direction direction, int room, String message) {
        exits.add(new Exit(direction, room, message));
    }
    
    public boolean canExit(Direction direction) {
        for(Exit exit : this.exits) {
            if(exit.getDirection() == direction) {
                return exit.getRoom() != NONEXISTANT_EXIT_ID;
            }
        }
        return false;
    }
    
    public String exitMessage(Direction direction) {
        for(Exit exit : this.exits) {
            if(exit.getDirection() == direction) {
                return exit.getMessage();
            }
        }
        return null;
    }
    
    public int getLink(Direction direction) {
        for(Exit exit : this.exits) {
            if(exit.getDirection() == direction) {
                return exit.getRoom();
            }
        }
        return NONEXISTANT_EXIT_ID;
    }
    
    public Exit randomValidExit(){
        List<Exit> validExits = new LinkedList<>(exits);
        validExits.removeIf(exit -> exit.getRoom() == NONEXISTANT_EXIT_ID);
        int index = new Random().nextInt(validExits.size());
      return validExits.get(index);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getTitle() {
        return this.title;
    }

    public String getLocation() {
	return this.location;
    }
    
    public String getObjects() {
        if(this.objects.isEmpty()) {
            return "None.";
        }
        else {
            return this.objects.toString();
        }
    }
    
    public void addObject(Item obj) {
        if(this.objects.size() < 5) {
            this.objects.add(obj);
        }
    }

    public void addObjectFromPlayer(Item obj) {
	this.objects.add(obj);
    }
    
    public Item removeObject(String target) {
        for(Item obj : this.objects) {
            if(obj.getItemName().equalsIgnoreCase(target)) {
                this.objects.remove(obj);
                return obj;
            }
        }
        return null;
    }
    
    public Item getLastObject() {
        if(this.objects.isEmpty()) 
            return null;
        else
            return this.objects.removeLast();    
    }
    
    public String getPlayers(PlayerList players) {
        String localPlayers = "";
        for(Player player : players) {
          System.err.println("Checking to see if " + player.getName() + " in room " + player.getCurrentRoom() + " is in this room (" + this.id + ")");
            if(player.getCurrentRoom() == this.id) {
                localPlayers += player.getName() + " ";
            }
        }
        if(localPlayers.equals("")) {
            return "None.";
        }
        else {
            return localPlayers;
        }
    }

    public ArrayList<String> getNamesOfNpcs(Set<NPC> npcSet){

        ArrayList<String> npcsFound = new ArrayList<>();

        for (NPC npc : npcSet) {
            if (npc.getCurrentRoom() == this.id) {
                npcsFound.add(npc.getName());
            }
        }
        if (npcsFound.isEmpty()){
            return null;
        }
        return npcsFound;
    }

    /**
     * @return a set of all the Ghouls in this room.
     * If there are no ghouls in this room, returns an empty set of Ghouls.
     */
    public Set<Ghoul> getGhouls() {
        Set<Ghoul> ghouls = new HashSet<>();
        for (NPC npc : gameCore.getNpcSet()) {
            if (npc instanceof Ghoul && npc.getCurrentRoom() == id) {
                ghouls.add((Ghoul) npc);
            }
        }
        return ghouls;
    }

    public String getGhoulsString() {
        Set<Ghoul> ghouls = getGhouls();
        if (ghouls.isEmpty())
            return "None";
        else {
            List<String> ghoulNames = ghouls.stream().map(Ghoul::toString).collect(Collectors.toList());
            return String.join(" ", ghoulNames);
        }
    }
}
