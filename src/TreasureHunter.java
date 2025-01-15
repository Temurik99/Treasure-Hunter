import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;
    private boolean testMode;
    private static boolean samuraiMode;
    private int checkForSearched = 0;
    private final int maxDigs = 1;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        testMode = false;
        samuraiMode = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.println("Welcome to TREASURE HUNTER!");
        System.out.println("Going hunting for the big treasure, eh?");
        System.out.print("What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();

        // set hunter instance variable
        hunter = new Hunter(name, 20);

        String hard = "";

        while(!(hard.equals("y") || hard.equals("n") || hard.equals("test") || hard.equals("s"))) {
            System.out.print("Hard mode? (y/n) Or just type \"test\" for test mode: ");
            hard = SCANNER.nextLine().toLowerCase();
            if (hard.equals("y")) {
                hardMode = true;
            } else if (hard.equals("n")) {
                hardMode = true;
                hardMode = false;
            } else if (hard.equals("test")) {
                testMode = true;
            } else if (hard.equals("s")) {
                samuraiMode = true;
            } else {
                System.out.print("That ain't an option, bucko. Try again: ");
            }
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.25;
        double toughness = 0.4;

        if (testMode) {
            hunter.changeGold(80);
            hunter.addItem("water");
            hunter.addItem("rope");
            hunter.addItem("machete");
            hunter.addItem("horse");
            hunter.addItem("boat");
            hunter.addItem("boots");
        }

        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.5;

            // and the town is "tougher"
            toughness = 0.75;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }


    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";
        while (!choice.equals("x")) {
            System.out.println();
            System.out.println(currentTown.getLatestNews());
            System.out.println("***");
            System.out.println(hunter.infoString());
            System.out.println(hunter.infoStringTreasure());
            System.out.println(currentTown.infoString());
            System.out.println("(B)uy something at the shop.");
            System.out.println("(S)ell something at the shop.");
            System.out.println("(E)xplore surrounding terrain.");
            System.out.println("(M)ove on to a different town.");
            System.out.println("(L)ook for trouble!");
            System.out.println("(H)unt for treasure");
            System.out.println("(D)ig for gold");
            System.out.println("Give up the hunt and e(X)it.");
            System.out.println();

            if (hunter.getGold() < 0) {
                System.out.println("You've run out of gold and can't continue the hunt. Game over!");
                break;
            }

            if (hunter.isTreasureFull()) {
                System.out.println("Congratulations, you have found the last of the three treasures, you win!");
                break;
            }

            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);

        }
    }


    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        }
        if (choice.equals("d")){
            if (!(hunter.hasItemInKit("shovel"))){
                System.out.println("You can't dig for gold without a shovel. \nYou need a shovel to dig, y'know ^^");
            } else {
                if (currentTown.manageDigs() == maxDigs){
                    System.out.println("You already dug for gold in this town. \nPack up your kit and move elsewhere to find gold!");
                } else {
                    int jeapordy = (int) (Math.random() * 2);
                    if (jeapordy == 0){
                        System.out.println("You dug but all you found was dirt...");
                    } else {
                        int goldFound = (int) (Math.random() * 20) + 1;
                        hunter.changeGold(goldFound);
                        System.out.println("JACKPOT! You won " + goldFound + " gold!");
                    }
                    currentTown.addDigs();
                }
            }
        } else if (choice.equals("e")) {
            System.out.println(currentTown.getTerrain().infoString());
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                enterTown();
                checkForSearched = 0;
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("h") && checkForSearched == 1) {
            currentTown.setNewsTo("you have already searched this town");
        } else if (choice.equals("h")) {
            currentTown.huntForTreasure();
            checkForSearched = 1;
        } else if (choice.equals("x")) {
            System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
        } else {
            System.out.println("Yikes! That's an invalid option! Try again.");
        }
    }


    public static boolean ifSamuraiMode() {
        if (samuraiMode) {
            return true;
        } else {
            return false;
        }
    }
}