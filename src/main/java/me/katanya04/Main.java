package me.katanya04;

import me.katanya04.enums.Stat;
import me.katanya04.listeners.*;
import me.katanya04.database.SQLDatabaseConnection;
import me.katanya04.listeners.Button;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static final String ALL_TABLES = "ALL_TABLES";
    public static SQLDatabaseConnection connection = new SQLDatabaseConnection();
    public static void main(String[] args) throws InterruptedException {
        if (!connection.Conectar())
            throw new RuntimeException("Unable to connect to the database. Stopping bot.");
        JDA bot = JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"))
                .setActivity(Activity.playing("The Towers en tt.holy.gg")).enableIntents(GatewayIntent.MESSAGE_CONTENT).build().awaitReady();
        addListeners(bot);
        List<net.dv8tion.jda.api.interactions.commands.Command.Choice> listOfStatChoices = Arrays.stream(Stat.values()).map(Stat::getText).map(x -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(x, x)).toList();
        List<net.dv8tion.jda.api.interactions.commands.Command.Choice> listOfTables = connection.getTables().stream().map(x -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(x, x)).toList();

        bot.updateCommands().addCommands(Commands.slash("towers","Comando The Towers")
                .addSubcommands(new SubcommandData("stats", "Estadisticas").addOptions(
                        new OptionData(OptionType.STRING, "nombre", "El nombre del jugador", true, true),
                        new OptionData(OptionType.STRING, "stats", "Contar solo amistosas, torneo, o las dos", true, false)
                                .addChoices(listOfTables).addChoice("todos", ALL_TABLES)
                ))

                .addSubcommands(new SubcommandData("top", "Muestra a los jugadores ordenados por rango").addOptions(
                        new OptionData(OptionType.STRING, "stats", "Contar solo amistosas, torneo, o las dos", true, false)
                                .addChoices(listOfTables).addChoice("todos", ALL_TABLES),
                        new OptionData(OptionType.STRING, "solo_fiables", "Excluir datos poco fiables o no", false, false)
                                .addChoices(new net.dv8tion.jda.api.interactions.commands.Command.Choice("si", "si"))
                                .addChoices(new net.dv8tion.jda.api.interactions.commands.Command.Choice("no", "no")),
                        new OptionData(OptionType.STRING, "categoria", "Estadística con la que ordenar la tabla", false, false)
                                .addChoices(listOfStatChoices),
                        new OptionData(OptionType.INTEGER, "pagina", "La página de la tabla", false, false),
                        new OptionData(OptionType.STRING, "mostrar_rangos", "Mostrar rango o no", false, false)
                                .addChoices(new net.dv8tion.jda.api.interactions.commands.Command.Choice("si", "si"))
                                .addChoices(new net.dv8tion.jda.api.interactions.commands.Command.Choice("no", "no")),
                        new OptionData(OptionType.INTEGER, "elementos", "Número de elementos por página", false, false)))

                .addSubcommands(new SubcommandData("precio", "Precio en Nias y Greendolares").addOptions(
                        new OptionData(OptionType.STRING, "nombre", "El nombre del jugador", true, true)
        ))).queue();
    }
    private static void addListeners(JDA bot) {
        bot.addEventListener(new Command(connection));
        bot.addEventListener(new Button(connection));
        bot.addEventListener(new Autocomplete(connection));
        bot.addEventListener(new MessageReceived());
    }
}