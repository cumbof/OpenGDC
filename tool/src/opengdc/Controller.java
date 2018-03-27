package opengdc;

import opengdc.action.Action;
import java.util.HashMap;

public class Controller {

    private HashMap<String, String> command2action;

    public Controller() {
        init();
    }

    public void execute(String[] args) {
        if (args!=null && args.length > 0) {
            if (!args[0].trim().equals("")) {
                if (command2action.containsKey(args[0].trim().toLowerCase())) {
                    Action action;
                    try {
                        action = (Action) Class.forName(command2action.get(args[0].trim().toLowerCase())).newInstance();
                        action.execute(args);
                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else System.err.println("command not found");
            }
        }
        else System.err.println("args is null or empty");
    }

    private void init() {
        command2action = new HashMap<>();
        command2action.put("download", "opengdc.action.DownloadDataAction");
        command2action.put("convert", "opengdc.action.ConvertDataAction");
    }

}
