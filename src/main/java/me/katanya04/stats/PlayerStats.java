package me.katanya04.stats;

import me.katanya04.database.SQLDatabaseConnection;
import me.katanya04.enums.Rank;
import me.katanya04.enums.Stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PlayerStats {
    public String nombre;
    private final HashMap<Stat, Double> stats = new HashMap<>();
    public Rank rank;
    public boolean statsFiables;
    public PlayerStats(SQLDatabaseConnection connection, String nombre, String tabla) {
        this.nombre = nombre;
        this.stats.putAll(connection.getData(nombre, tabla));
        calcularDatos();
    }
    public PlayerStats(ResultSet rs) throws SQLException {
        this.nombre = rs.getString(2);
        for (int j = 3; j < 10; j++)
            stats.put(Stat.valueOf(rs.getMetaData().getColumnName(j).toUpperCase()), (double) rs.getInt(j));
        calcularDatos();
    }
    private void calcularDatos() {
        statsFiables = esFiable();
        if (stats.get(Stat.DEATHS) != 0)
            stats.put(Stat.KILL_DEATH_RATIO, stats.get(Stat.KILLS) / stats.get(Stat.DEATHS));
        else
            stats.put(Stat.KILL_DEATH_RATIO, stats.get(Stat.KILLS));

        if (stats.get(Stat.GAMES_PLAYED) != 0) {
            stats.put(Stat.WIN_RATE, stats.get(Stat.WINS) / stats.get(Stat.GAMES_PLAYED));
            stats.put(Stat.POINTS_PER_GAME, stats.get(Stat.ANOTED_POINTS) / stats.get(Stat.GAMES_PLAYED));
            stats.put(Stat.KILLS_PER_GAME, stats.get(Stat.KILLS) / stats.get(Stat.GAMES_PLAYED));
            stats.put(Stat.DEATHS_PER_GAME, stats.get(Stat.DEATHS) / stats.get(Stat.GAMES_PLAYED));
        } else {
            stats.put(Stat.WIN_RATE, stats.get(Stat.WINS));
            stats.put(Stat.POINTS_PER_GAME, stats.get(Stat.ANOTED_POINTS));
            stats.put(Stat.KILLS_PER_GAME, stats.get(Stat.KILLS));
            stats.put(Stat.DEATHS_PER_GAME, stats.get(Stat.DEATHS));
        }
        stats.put(Stat.RANK, calcularRango());
        rank = Rank.getRank(stats.get(Stat.RANK));
    }
    private double calcularRango() {
        double toret = stats.get(Stat.KILL_DEATH_RATIO) * 1.25 + stats.get(Stat.POINTS_PER_GAME) * 2.5 +
                (stats.get(Stat.KILLS_PER_GAME) / 15 - stats.get(Stat.DEATHS_PER_GAME) / 20) + stats.get(Stat.WIN_RATE) * 2;
        return toret < 0 ? 0 : toret;
    }
    private boolean esFiable() {
        return stats.get(Stat.KILLS) + stats.get(Stat.DEATHS) + stats.get(Stat.ANOTED_POINTS)
                + stats.get(Stat.GAMES_PLAYED) + stats.get(Stat.WINS) > 150;
    }
    public HashMap<Stat, Double> getStats() {
        return this.stats;
    }
    public static List<PlayerStats> listFromResultSet(ResultSet rs) throws SQLException {
        List<PlayerStats> toret = new LinkedList<>();
        while (rs.next())
            toret.add(new PlayerStats(rs));
        return toret;
    }
}
