package me.katanya04.utils;

import me.katanya04.database.ListCache;
import me.katanya04.database.SQLDatabaseConnection;
import me.katanya04.enums.Stat;
import me.katanya04.stats.PlayerStats;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class ListBuilder {
    public static Map.Entry<String, Integer> buildList(int numElem, int pagina, boolean incluirNoFiables, Stat stat, boolean hasRanks, PlayerStats[] list) {
        StringBuilder sb = new StringBuilder();
        int posInList = 0;
        for (int i = 0; i < numElem && i + pagina * numElem < list.length; i++) {
            posInList = i + pagina * numElem;

            String nombre = list[posInList].nombre.replaceAll("_", Matcher.quoteReplacement("\\_"));
            sb.append(posInList + 1).append(". ");

            if (hasRanks) sb.append(list[posInList].rank.getEmoji()).append(" ");

            if (incluirNoFiables) sb.append(list[posInList].statsFiables ? "" : "\\*");

            sb.append(nombre).append(" - ");

            double statValue = list[i + pagina * 10].getStats().get(stat);
            if (stat.isPercentage()) statValue *= 100;
            String format = statValue % 1 == 0 ? "%.0f" : "%4.2f";
            sb.append(String.format(format, statValue));
            if (stat.isPercentage()) sb.append("%");

            if (i != numElem - 1) sb.append("\n");
        }
        return Map.entry(sb.toString(), ++posInList);
    }
    public static PlayerStats[] getList(SQLDatabaseConnection connection, Stat stat, boolean incluirNoFiables, String tabla) {
        if (connection.listCache != null && connection.listCache.isInCache(stat, incluirNoFiables, tabla))
            return connection.listCache.getStatList();
        List<PlayerStats> tempList = connection.getPlayersStats(incluirNoFiables, tabla);
        tempList.sort(Comparator.comparing(o -> o.getStats().get(stat), Comparator.reverseOrder()));
        PlayerStats[] toret = new PlayerStats[tempList.size()];
        tempList.toArray(toret);
        if (connection.listCache == null)
            connection.listCache = new ListCache();
        connection.listCache.setCache(toret, stat, incluirNoFiables, tabla);
        return toret;
    }
}
