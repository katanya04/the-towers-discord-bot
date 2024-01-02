package me.katanya04.listeners;

import me.katanya04.database.SQLDatabaseConnection;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Autocomplete extends ListenerAdapter {
    private final String[] playerNames;
    public Autocomplete(SQLDatabaseConnection connection) {
        playerNames = connection.getPlayerNames();
    }
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent e) {
        if (!e.getName().equals("towers") || !e.getFocusedOption().getName().equals("nombre"))
            return;
        List<Command.Choice> options = Stream.of(playerNames)
                .filter(word -> word.regionMatches(true, 0, e.getFocusedOption().getValue(), 0, e.getFocusedOption().getValue().length())).limit(25) // only display words that start with the user's current input
                .map(word -> new Command.Choice(word, word)) // map the words to choices
                .collect(Collectors.toList());
        e.replyChoices(options).queue();
    }
}
