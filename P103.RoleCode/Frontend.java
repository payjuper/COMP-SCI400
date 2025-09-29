import java.util.Scanner;
import java.util.List;

public class Frontend implements FrontendInterface {
    private final Scanner in;
    private final BackendInterface backend;

    // track the most recent energy range (to avoid nulls)
    private Integer currentLow = 0;     // default min
    private Integer currentHigh = 999;  // default max

    public Frontend(Scanner in, BackendInterface backend) {
        this.in = in;
        this.backend = backend;
    }

    @Override
    public void runCommandLoop() {
        showCommandInstructions();
        boolean running = true;
        while (running && in.hasNextLine()) {
            String line = in.nextLine().trim();
            if (line.equalsIgnoreCase("quit")) {
                running = false;
                break;
            }
            processSingleCommand(line);
        }
    }

    @Override
    public void showCommandInstructions() {
        System.out.println("Commands:");
        System.out.println("  load FILEPATH");
        System.out.println("  energy MAX");
        System.out.println("  energy MIN to MAX");
        System.out.println("  danceability MIN");
        System.out.println("  show MAX_COUNT");
        System.out.println("  show most recent");
        System.out.println("  help");
        System.out.println("  quit");
        System.out.print("> ");
    }

    @Override
    public void processSingleCommand(String command) {
        if (command == null) { System.out.print("> "); return; }
        command = command.trim();
        if (command.isEmpty()) { System.out.print("> "); return; }

        String lower = command.toLowerCase();

        // HELP
        if (lower.equals("help")) {
            showCommandInstructions();
            return;
        }

        // LOAD
        else if (lower.startsWith("load ")) {
            String filepath = command.substring(5).trim();
            if (filepath.isEmpty()) {
                System.out.println("Invalid: expected 'load FILEPATH'");
            } else {
                try {
                    backend.readData(filepath);
                } catch (Exception e) {
                    System.out.println("Error loading file: " + e.getMessage());
                }
            }
        }

        // ENERGY
        else if (lower.startsWith("energy")) {
            String args = command.substring("energy".length()).trim();
            if (args.isEmpty()) {
                System.out.println("Invalid: expected 'energy MAX' or 'energy MIN to MAX'.");
            } else if (args.toLowerCase().contains(" to ")) {
                String[] parts = args.split("(?i)\\s+to\\s+");
                if (parts.length == 2) {
                    try {
                        currentLow = Integer.parseInt(parts[0].trim());
                        currentHigh = Integer.parseInt(parts[1].trim());
                        backend.getAndSetRange(currentLow, currentHigh);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid energy range.");
                    }
                } else {
                    System.out.println("Invalid energy syntax.");
                }
            } else {
                try {
                    currentHigh = Integer.parseInt(args);
                    backend.getAndSetRange(currentLow, currentHigh);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid energy max.");
                }
            }
        }

        // DANCEABILITY
        else if (lower.startsWith("danceability")) {
            String args = command.substring("danceability".length()).trim();
            try {
                Integer minDance = Integer.parseInt(args);
                backend.applyAndSetFilter(minDance);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid danceability value.");
            }
        }

        // SHOW MOST RECENT
        else if (lower.equals("show most recent")) {
            List<String> titles = backend.fiveMost();
            for (String t : titles) {
                System.out.println(t);
            }
        }

        // SHOW COUNT
        else if (lower.startsWith("show ")) {
            String num = command.substring(5).trim();
            try {
                int count = Integer.parseInt(num);
                List<String> titles = backend.getAndSetRange(currentLow, currentHigh);
                for (int i = 0; i < count && i < titles.size(); i++) {
                    System.out.println(titles.get(i));
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid: expected 'show MAX_COUNT' with an integer.");
            }
        }

        // INVALID
        else {
            System.out.println("Invalid command: " + command);
        }

        // print prompt again
        System.out.print("> ");
    }
}
