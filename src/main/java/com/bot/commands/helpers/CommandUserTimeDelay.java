package com.bot.commands.helpers;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.HashMap;

import static com.bot.FTCHelperBot.*;


public class CommandUserTimeDelay {

    static HashMap<String, Integer> userTimeDelay = new HashMap<>();

    public static void addTimeDelay(String userID) {
        //put user id and current time
        userTimeDelay.put(userID, (int) (System.currentTimeMillis() / 1000L));

    }



    public static Boolean timeCheckUser (String userID){
        //check if user is in the list
        if (userTimeDelay.containsKey(userID)) {
            //check if time delay has passed
            if ((int) (System.currentTimeMillis() / 1000L) - userTimeDelay.get(userID) > COMMAND_DELAY) {
                //remove user from list
                userTimeDelay.remove(userID);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    public static void timErrorEmbed (EmbedBuilder eb){
        eb.setTitle("Error");
            eb.setDescription("You are sending commands too fast. Please wait "+ COMMAND_DELAY+" second and try again.");
        eb.setColor(ERROR_COLOR);
    }





    public static void removeTimeDelay(String userID) {
        userTimeDelay.remove(userID);
    }

    public static void clearTimeDelay() {
        userTimeDelay.clear();
    }


}
