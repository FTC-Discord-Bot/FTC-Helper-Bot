package com.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;


import java.awt.*;

import static com.bot.FTCHelperBot.*;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;

public class ActionRow extends ListenerAdapter {
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {

        if (event.getComponentId().equals("Docs Options")) {
            EmbedBuilder eb = new EmbedBuilder();
            switch (event.getValues().get(0)) {
                case "java":
                    eb.setTitle(":coffee: Java Resources");
                    eb.setDescription("Here are some resources for learning Java");
                    eb.addField("Java Docs", ":book: [W3 Schools](https://www.w3schools.com/java/default.asp) :arrow_forward: [Java video](https://www.youtube.com/watch?v=RRubcjpTkks&t=174s)", false);
                    eb.setColor(MAIN_COLOR);
                    eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                    break;

                case "road-runner":
                    eb.setTitle(":person_running: Road Runner Resources");
                    eb.setDescription("Here are some resources for learning Road Runner");
                    eb.addField("Road Runner Docs", ":book: [Road Runner Docs](https://learnroadrunner.com)", false);
                    eb.setColor(MAIN_COLOR);
                    eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                    break;

                case "open-cv":
                    eb.setTitle(":camera: Open CV Resources");
                    eb.setDescription("Here are some resources for learning Open CV");
                    eb.addField("Open CV Docs", ":book: [Open CV Docs](https://github.com/OpenFTC/EasyOpenCV) :arrow_forward: [Open CV tutorial](https://stemrobotics4all.org/ftc/java-lessons/opencv/)", false);
                    eb.setColor(MAIN_COLOR);
                    eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                    break;

                case "ftc-sdk":
                    eb.setTitle(":robot: FTC SDK Resources");
                    eb.setDescription("Here are some resources for learning the FTC SDK");
                    eb.addField("FTC SDK Docs", ":book: [FTC SDK Docs](https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki) :arrow_forward: [FTC programing video](https://www.youtube.com/watch?v=CdcpNZzekb0)", false);
                    eb.setColor(MAIN_COLOR);
                    eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                    break;

                case "ftc-lib":
                    eb.setTitle(":compass: FTC Lib Resources");
                    eb.setDescription("Here are some resources for learning FTC Lib");
                    eb.addField("FTC Lib Docs", ":book: [FTC Lib Docs](https://docs.ftclib.org/ftclib/v/v2.0.0/)", false);
                    eb.setColor(MAIN_COLOR);
                    eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                    break;

                case "pid":
                    eb.setTitle(":bar_chart: PID Resources");
                    eb.setDescription("Here are some resources for learning PID");
                    eb.addField("PID Docs", ":book: [PID Docs](https://www.ctrlaltftc.com/the-pid-controller) :arrow_forward: [PID video](https://www.youtube.com/watch?v=UR0hOmjaHp0)", false);
                    eb.setColor(MAIN_COLOR);
                    eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                    break;
            }

            event.replyEmbeds(eb.build()).setEphemeral(false)
                    .addActionRow(
                            Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                    )
                    .queue();

        }


    }
}
