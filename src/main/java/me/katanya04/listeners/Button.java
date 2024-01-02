package me.katanya04.listeners;

import me.katanya04.Main;
import me.katanya04.database.SQLDatabaseConnection;
import me.katanya04.enums.Stat;
import me.katanya04.utils.AddButtons;
import me.katanya04.utils.ListBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.List;
import java.util.Map;

public class Button extends ListenerAdapter {
    private final SQLDatabaseConnection connection;
    public Button(SQLDatabaseConnection connection) {
        this.connection = connection;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {
        String[] buttonIdArray = e.getButton().getId().split(" ");
        if (!(buttonIdArray[0].equals("up") || buttonIdArray[0].equals("down")))
            return;
        boolean incluirNoFiables = buttonIdArray[1].equals("noFiable");
        int page = Integer.parseInt(buttonIdArray[2]);
        int pages = Integer.parseInt(buttonIdArray[3]);
        int numElem = Integer.parseInt(buttonIdArray[4]);
        boolean hasRanks = buttonIdArray[5].equals("rangos");
        Stat stat = Stat.valueOf(buttonIdArray[6]);
        String table = buttonIdArray[7];
        if (page < 0 || page >= pages) {
            e.reply("No hay más páginas.").setEphemeral(true).queue();
            return;
        }
        String embedFooter = "Página " + (page + 1) + "/" + pages;
        if (incluirNoFiables) embedFooter += "\n*: Poco fiable, pocos datos.";

        Map.Entry<String, Integer> paginaDeLista = ListBuilder.buildList(numElem, page, incluirNoFiables, stat, hasRanks, ListBuilder.getList(connection, stat, incluirNoFiables, table));
        String newTitle = e.getMessage().getEmbeds().get(0).getFields().get(0).getName().split(" ")[0] + " " + (page * numElem + 1) + "-" + paginaDeLista.getValue() + " " + stat.getText().replace("_", " ") + " " + (Main.ALL_TABLES.equals(table) ? "" : table);

        List<net.dv8tion.jda.api.interactions.components.buttons.Button> buttons = AddButtons.createButtons(incluirNoFiables, hasRanks, page, pages, numElem, stat, table);
        EmbedBuilder eb;
        try {
            eb = new EmbedBuilder().setFooter(embedFooter).addField(newTitle, paginaDeLista.getKey(), true);
        } catch (IllegalArgumentException ex) {
            e.reply("Hay demasiados elementos a devolver.").setEphemeral(true).queue();
            return;
        }
        e.editMessage(MessageEditData.fromCreateData(new MessageCreateBuilder().addEmbeds(eb.build()).addActionRow(buttons).build())).queue();
    }
}
