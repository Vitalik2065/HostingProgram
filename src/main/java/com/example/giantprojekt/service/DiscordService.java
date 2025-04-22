package com.example.giantprojekt.service;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.example.giantprojekt.service.DiscordAssignmentHandler;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class DiscordService extends ListenerAdapter {

    private JDA jda;


    public void startBot(String token) throws LoginException {
        JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(this)
                .build()
                .upsertCommand("disadd", "назначить discord‑id серверу")
                .addOption(OptionType.STRING, "uuid",   "Server UUID", true)
                .addOption(OptionType.USER,   "member", "Участник (mention)", true)
                .queue();




    }

    public void sendNotification(String channelId, String message) {
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getAuthor().isBot()) {
            String content = event.getMessage().getContentRaw();
            if (content.equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!").queue();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equalsIgnoreCase("disadd")) return;

        String uuid = event.getOption("uuid").getAsString();
        User user = event.getOption("member").getAsUser();
        String discordId = user.getId();

        try {
            new DiscordAssignmentHandler().assignDiscordIdToServerRow(uuid, discordId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        event.reply("✅ Участник " + user.getAsMention() +
                        " привязан к серверу " + uuid)
                .setEphemeral(true)
                .queue();
    }











}
