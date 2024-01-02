package me.katanya04.utils;

import me.katanya04.enums.Stat;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;

public class AddButtons {
    public static List<Button> createButtons(boolean incluirNoFiables, boolean hasRanks, int pagina, int pages, int numElem, Stat stat, String table) {
        List<Button> toret = new ArrayList<Button>();
        String fiable = incluirNoFiables ? "noFiable" : "fiable";
        String ranks = hasRanks ? "rangos" : "noRangos";
        // <up|down> <fiable|noFiable> <pagina actual> <total de paginas> <nº elementos> ...
        toret.add(Button.primary("up" + " " + fiable + " " + (pagina - 1) + " " + pages + " " + numElem + " " + ranks + " " + stat + " " + table, "\uD83D\uDD3C"));
        toret.add(Button.primary("down" + " " + fiable + " " + (pagina + 1) + " " + pages + " " + numElem + " " + ranks + " " + stat + " " + table, "\uD83D\uDD3D"));
        return toret;
    }
}
