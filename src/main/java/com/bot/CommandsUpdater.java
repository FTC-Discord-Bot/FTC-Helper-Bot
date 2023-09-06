package com.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class CommandsUpdater {
    public static void updateCommands(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("team-info", "Get info about a team")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022). Not that important for this command", false),

                Commands.slash("events", "Get events for a team")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number",true)
                        .addOption(OptionType.BOOLEAN, "advanced", "Show more advanced details about events",false)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("team-league-rank", "Get league rank for a team")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number",true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("league-rank", "Get league rank for a league")
                        .addOption(OptionType.STRING, "region-league-or-team", "Team Number or Region and League code. Put (Region Code):(League Code) use : to separate.",true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("event-rank", "Get event rank for an event")
                        .addOption(OptionType.STRING, "code", "Event Code ex (USCANOSBM1A) use /events to find the code",true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("predict", "predictftc.org for a team")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number",true),
                Commands.slash("scout", "ftcscout.org for a team")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number",true),
                Commands.slash("scout-event", "ftcscout.org for a team")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number",true),
                Commands.slash("ftc-scores", "ftcscores.com for a team")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number",true),
                Commands.slash("learn-java", "Resources for learning Java for FTC"),
                Commands.slash("discord-event-details", "Get events for the server"),
                Commands.slash("repo-details", "Details about a repository")
                        .addOption(OptionType.STRING, "name-owner", "The Name of the repo and the Owner of it seperated by a /", true),
                Commands.slash("latest-commit", "Latest commit for the chosen repository")
                        .addOption(OptionType.STRING, "name-owner", "The Name of the repo and the Owner of it seperated by a /", true),
                Commands.slash("clear", "Clear messages in a channel")
                        .addOption(OptionType.INTEGER, "amount", "Amount of messages to clear", true)
                        .addOption(OptionType.USER, "user", "User whose messages to clear", false),
                Commands.slash("weather", "Get the weather at a team's location")
                        .addOption(OptionType.INTEGER, "team-number", "Team Number",true)
                        .addOptions(
                                new OptionData(OptionType.STRING, "measurement-unit", "what units to use for the weather")
                                        .addChoice("Imperial", "imperial")
                                        .addChoice("Metric", "metric")),
                Commands.slash("docs", "Get links to documentation"),
                Commands.slash("forecast","Week forecast for a robotics team's location")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number",true),
                Commands.slash("fetch-docs", "Fetch the latest documentation")
                        .addOption(OptionType.STRING, "query", "Search query",true),
                Commands.slash("game-manual", "Get the game manual for the current season"),
                Commands.slash("run-code", "Run code in a channel")
                        .addOption(OptionType.STRING, "language", "Language to run code in",true)
                        .addOption(OptionType.STRING, "code", "Code to run, write \"file\" if you wish to use a file instead", true)
                        .addOption(OptionType.ATTACHMENT, "file-code", "Code to run", false),
                Commands.slash("vote","Start a vote")
                        .addOption(OptionType.STRING, "question", "Question to ask", true)
                        .addOption(OptionType.STRING, "option1", "First option", true)
                        .addOption(OptionType.STRING, "option2", "Second option", true)
                        .addOption(OptionType.STRING, "option3", "Third option", false)
                        .addOption(OptionType.STRING, "option4", "Fourth option", false)
                        .addOption(OptionType.STRING, "option5", "Fifth option", false)
                        .addOption(OptionType.STRING, "option6", "Sixth option", false)
                        .addOption(OptionType.STRING, "option7", "Seventh option", false)
                        .addOption(OptionType.STRING, "option8", "Eighth option", false)
                        .addOption(OptionType.STRING, "option9", "Ninth option", false)
                        .addOption(OptionType.STRING, "option10", "Tenth option", false),
                Commands.slash("search-repos","Search GitHub Repos")
                        .addOption(OptionType.STRING, "query", "Search query", true),
                Commands.slash("apod","Get the Astronomy Picture of the Day from NASA"),
                Commands.slash("awards-list","Get a list of awards for a season")
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("event-awards","Get awards for an event")
                        .addOption(OptionType.STRING, "event", "Event Code ex (USCANOSBM1A) use /events to find the code", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false)
                        .addOption(OptionType.INTEGER, "highlight-teamnumber", "Team Number", false),
                Commands.slash("team-awards-at-event","Get awards for a team at an event")
                        .addOption(OptionType.STRING, "event", "Event Code ex (USCANOSBM1A) use /events to find the code", true)
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("team-awards","Get awards for a team")
                        .addOption(OptionType.INTEGER, "teamnumber", "Team Number", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("alliance-details","Get alliance details for an event")
                        .addOption(OptionType.STRING, "event", "Event Code ex (USCANOSBM1A) use /events to find the code", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("alliance-selection","Get alliance selection for an event")
                        .addOption(OptionType.STRING, "event", "Event Code ex (USCANOSBM1A) use /events to find the code", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("season-data","Get data for a season")
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("match-results","Get match results for an event")
                        .addOption(OptionType.STRING, "event", "Event Code ex (USCANOSBM1A) use /events to find the code", true)
                        .addOption(OptionType.INTEGER, "teamnumber", "Find specific results played by that team", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("match-schedule","Get the schedule for a team during an event")
                        .addOption(OptionType.STRING, "event", "Event Code ex (USCANOSBM1A) use /events to find the code", true)
                        .addOption(OptionType.INTEGER, "teamnumber", "Schedule by that team. Required for schedule.", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("bot-info","Get information about the bot"),
                Commands.slash("alum-list","Get a list of alumni for the team"),
                Commands.slash("ask-ai","Ask AI a question about programming")
                        .addOption(OptionType.STRING, "question", "Question to ask", true),
                Commands.slash("search-teams-by-name","Search for teams by name")
                        .addOption(OptionType.STRING, "query", "Search query for team name", true),
                Commands.slash("todays-events","Find Events Today"),
                Commands.slash("events-search","Search for events")
                        .addOption(OptionType.STRING, "query", "Search query for event name", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("event-info","Get information about a team")
                        .addOption(OptionType.STRING, "event", "Event Code ex (USCANOSBM1A) use /events to find the code", true)
                        .addOption(OptionType.BOOLEAN, "advanced", "Advanced information", false)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false),
                Commands.slash("help","Get help with the bot and commands"),
                Commands.slash("event-weather","Get weather for an event")
                        .addOption(OptionType.STRING, "event", "Event Code ex (USCANOSBM1A) use /events to find the code", true)
                        .addOption(OptionType.INTEGER, "season", "Season ex (2022)", false)
                        .addOptions(
                                new OptionData(OptionType.STRING, "measurement-unit", "what units to use for the weather")
                                        .addChoice("Imperial", "imperial")
                                        .addChoice("Metric", "metric"))

        ).queue();
    }
}
