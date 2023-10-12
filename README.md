<div style="text-align: center;">
  <img src="images/Long GitHub.png" alt="Logo" >
</div>

---
<h1 align="center" id="title">FTC Helper Bot</h1>  

![GitHub Actions Status](https://img.shields.io/github/actions/workflow/status/FTC-Discord-Bot/FTC-Helper-Bot/gradle.yml)  

[![Discord Invite](https://img.shields.io/badge/Discord%20Invite-8A2BE2?style=flat&logo=discord&link=https%3A%2F%2Fdiscord.com%2Fapi%2Foauth2%2Fauthorize%3Fclient_id%3D1138233951090135072%26permissions%3D8%26scope%3Dbot)](https://discord.com/api/oauth2/authorize?client_id=1138233951090135072&permissions=8&scope=bot)

 
<p id="description">A Java discord bot created for FTC servers, made by team 13190</p>  

**Note**: This bot is still in development and may go down sometimes for updates or maintenance  

*Contributions are welcome!*
   
### [+ Add to Server](https://discord.com/api/oauth2/authorize?client_id=1138233951090135072&permissions=8&scope=bot)


<h2>⚡ Integrations</h2>

- FTC API
- FTC Scout
- GitHub
- Google Maps
- Nasa
- GitBook (Documentation)
- Open Meteo (Weather)

<h2>📚 Useful Commands</h2>  

-  /help - Shows a list of commands  
- /team-info - Shows information about a team based on number  
- /match-results - Shows the results of a match during an event
- /events - Shows a list of events for a team

<h2>💻 Setup for bot development</h2>  

- Clone the repository
- Create a file called `Config.json` in the path `src/main/java/com/bot/Config.JSON`
- Add the following to the file:  

```json
{
  "MAIN_COLOR": [59, 127, 255],
  "ERROR_COLOR": [255, 0, 0],
  "BOT_NAME": "FTC Helper Bot",
  "DISCORD_TOKEN": "INSERT_HERE",
  "GITBOOK_TOKEN": "INSERT_HERE",
  "LEARN_SOFTWARE_SPACE_ID": "0Atm0LAmF7JLCQSIr8d4",
  "FTC_USERNAME": "INSERT_HERE",
  "FTC_PASSWORD": "INSERT_HERE",
  "NASA_API_KEY": "INSERT_HERE",
  "GOOGLE_MAPS_TOKEN": "INSERT_HERE",
  "INLINE": false,
  "DEFAULT_SEASON": 2023,
  "COMMAND_DELAY": 10
}
```
To get a discord token, go to the [Discord Developer Portal](https://discord.com/developers/applications) and create a new application. Then, go to the bot tab and create a bot. You can then copy the token and paste it into the `Config.json` file. Also do not forget to enable all the Privileged Gateway Intents  

Register for the FTC API [FTC Events API](https://ftc-events.firstinspires.org/services/API)     

To get a NASA API key, go to the [NASA API Portal](https://api.nasa.gov/) and create a new application. You can then copy the token and paste it into the `Config.json` file  

If you need to test the AI command, you can get a GitBook token by going to the [GitBook Developer Portal](https://app.gitbook.com/account/developer)  

To get a Google Maps token, go to the [Google Maps Developer Portal](https://developers.google.com/maps/documentation/javascript/get-api-key) and create a new application. You can then copy the token and paste it into the `Config.json` file  

It is not required to run with a Google Maps token as it only uses the google maps api if the normal geocoding fails  
