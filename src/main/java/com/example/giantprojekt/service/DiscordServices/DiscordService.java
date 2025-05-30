package com.example.giantprojekt.service.DiscordServices;

import javax.security.auth.login.LoginException;

import com.example.giantprojekt.service.DiscordServices.DiscordAssignmentHandler;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.example.giantprojekt.service.DiscordServices.AddDisIdByEmail;

public class DiscordService extends ListenerAdapter {

    private JDA jda;


    public void startBot(String token) throws LoginException {
        JDA jda = JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                )
                .addEventListeners(this)
                .build();


                jda.upsertCommand("disadd", "назначить discord‑id серверу")
                .addOption(OptionType.STRING, "uuid",   "Server UUID", true)
                .addOption(OptionType.USER,   "member", "Участник (mention)", true)

                .queue();


                jda.upsertCommand("dissbyeall", "добавить discord-id всем серверам")
                        .addOption(OptionType.STRING, "email", "User Email", true)
                        .addOption(OptionType.USER, "member", "Участник (mention)", true)
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
        try {
            if (event.getName().equalsIgnoreCase("disadd")) {
                String uuid = event.getOption("uuid").getAsString();
                User user = event.getOption("member").getAsUser();
                String discordId = user.getId();

                new DiscordAssignmentHandler().assignDiscordIdToServerRow(uuid, discordId);

                event.reply("✅ Участник " + user.getAsMention() +
                                " привязан к серверу " + uuid)
                        .setEphemeral(true)
                        .queue();
            }
            else if (event.getName().equalsIgnoreCase("dissbyeall")) {
                String email = event.getOption("email").getAsString();
                User user = event.getOption("member").getAsUser();
                String discordId = user.getId();

                new AddDisIdByEmail().AddEmailToAll(email, discordId);

                event.reply("✅ Discord ID привязан ко всем серверам для email: " + email)
                        .setEphemeral(true)
                        .queue();
            }
        } catch (Exception e) {
            event.reply("❌ Произошла ошибка: " + e.getMessage())
                    .setEphemeral(true)
                    .queue();
        }
    }











}
