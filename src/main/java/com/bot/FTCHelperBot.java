package com.bot;

import com.bot.commands.ActionRow;
import com.bot.commands.Buttons;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import com.bot.commands.BotCommands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import static com.bot.CommandsUpdater.updateCommands;


public class FTCHelperBot {
    public static Guild guild;
    public static JDA jda;

    // Declare public variables
    public static Color MAIN_COLOR;
    public static Color ERROR_COLOR;
    public static String BOT_NAME;
    public static String DISCORD_TOKEN;
    public static String GITBOOK_TOKEN;
    public static String LEARN_SOFTWARE_SPACE_ID;

    public static String FTC_USERNAME;
    public static String FTC_PASSWORD;
    public static String NASA_API_KEY;
    public static boolean INLINE;
    public static int DEFAULT_SEASON;
    public static int COMMAND_DELAY;
    public static String GOOGLE_MAPS_TOKEN;
    public static void main(String[] args) throws InterruptedException {

        String jsonStr = null;
        String filePath = "com/bot/Config.JSON";

        // Parse the JSON string into a JSONObject

        // Get the InputStream of the resource file from the content root folder
        InputStream inputStream = FTCHelperBot.class.getClassLoader().getResourceAsStream(filePath);


        // Read the content of the file as a string
        try {
            jsonStr = new String(inputStream.readAllBytes());
            System.out.println("\n-Loaded JSON Config File-");
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject configJson = new JSONObject(jsonStr);
        // Extract individual configuration values
        MAIN_COLOR = new Color(configJson.getJSONArray("MAIN_COLOR").getInt(0),
                configJson.getJSONArray("MAIN_COLOR").getInt(1),
                configJson.getJSONArray("MAIN_COLOR").getInt(2));
        ERROR_COLOR = new Color(configJson.getJSONArray("ERROR_COLOR").getInt(0),
                configJson.getJSONArray("ERROR_COLOR").getInt(1),
                configJson.getJSONArray("ERROR_COLOR").getInt(2));
        BOT_NAME = configJson.getString("BOT_NAME");
        DISCORD_TOKEN = configJson.getString("DISCORD_TOKEN");
        GITBOOK_TOKEN = configJson.getString("GITBOOK_TOKEN");
        LEARN_SOFTWARE_SPACE_ID = configJson.getString("LEARN_SOFTWARE_SPACE_ID");


        FTC_USERNAME = configJson.getString("FTC_USERNAME");
        FTC_PASSWORD = configJson.getString("FTC_PASSWORD");
        NASA_API_KEY = configJson.getString("NASA_API_KEY");
        INLINE = configJson.getBoolean("INLINE");
        DEFAULT_SEASON = configJson.getInt("DEFAULT_SEASON");
        COMMAND_DELAY = configJson.getInt("COMMAND_DELAY");
        GOOGLE_MAPS_TOKEN = configJson.getString("GOOGLE_MAPS_TOKEN");


         jda = JDABuilder.createDefault(DISCORD_TOKEN)
                .setActivity(Activity.playing("Vote /vote-best-name"))
                 .addEventListeners(
                         new BotCommands(),
                         new Buttons(),
                         new ActionRow()
                 )
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)

                 .build().awaitReady();


            System.out.println("\nAmount of servers in: " +  jda.getGuilds().size());
            System.out.println( BOT_NAME+" is ready to go!\n");




        // Register slash commands
        updateCommands(jda);
        }


}


