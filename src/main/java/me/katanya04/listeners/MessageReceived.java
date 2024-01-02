package me.katanya04.listeners;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessageReceived extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getMember().getUser().isBot())
            return;
        List<RichCustomEmoji> emojis;
        if (e.getMessage().getContentRaw().toLowerCase().contains("hola soy lucio")) {
            emojis = e.getGuild().getEmojis();
            String emojiTemp = emojis.get(Math.abs(ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE) % emojis.size())).toString();
            String emoji = "<".concat(emojiTemp.split("ichCustomEmoji")[1]).replace(")",">").replace("(", ":").replace("id=", "");
            e.getMessage().reply("Meme muerto " + emoji).queue();
        }
        if (e.getAuthor().getId().equalsIgnoreCase("892605254221328515"))
            e.getMessage().addReaction(Emoji.fromUnicode("\uD83E\uDD13")).queue();
    }
}
