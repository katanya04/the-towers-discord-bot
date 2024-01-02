package me.katanya04.database;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import me.katanya04.Main;
import me.katanya04.enums.Stat;
import me.katanya04.stats.PlayerStats;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.*;

public class SQLDatabaseConnection {
    private Connection connection;
    private final String hostname;
    private final String database;
    private final String user;
    private final String password;
    private final List<String> tables;
    public ListCache listCache;

    public enum OperationType {HAS_ACC, GET_PLAYER_DATA, GET_PLAYERS_NAME, GET_PLAYERS_DATA}

    public SQLDatabaseConnection() {
        this.hostname = System.getenv("DB_HOSTNAME");
        this.database = System.getenv("DB");
        this.user = System.getenv("DB_USER");
        this.password = System.getenv("DB_PASSWORD");
        this.tables = new ArrayList<>();
        this.tables.addAll(Arrays.stream(System.getenv("DB_TABLES").split(",")).toList());
    }

    public boolean Conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + "/" + this.database +
                    "?autoReconnect=true", this.user, this.password);
            return this.connection != null;
        } catch (SQLException e) {
            System.out.println("SQL error when connecting with the database.");
        } catch (ClassNotFoundException classNotFoundException) {
            System.out.println("Class error when connecting with the database.");
        }
        return false;
    }

    public Object operation(OperationType operation, @Nullable String playerName, @Nullable String table, boolean incluirFiables) {
        boolean repeat = false;
        do {
            try {
                return switch (operation) {
                    case HAS_ACC -> hasAccount_(playerName, table);
                    case GET_PLAYER_DATA -> getData_(playerName, table);
                    case GET_PLAYERS_NAME -> getPlayerNames_();
                    case GET_PLAYERS_DATA -> getPlayersStats_(incluirFiables, table);
                };
            } catch (CommunicationsException e) {
                if (this.Conectar() && !repeat)
                    repeat = true;
                else {
                    System.out.println("Communication error on operation " + operation + " for playerName: " + playerName + ", db table: " + table);
                    repeat = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                repeat = false;
            }
        } while (repeat);
        return null;
    }

    public boolean hasAccount(String playerName, String table) {
        return (boolean) Objects.requireNonNullElse(operation(OperationType.HAS_ACC, playerName, table, false), false);
    }
    @SuppressWarnings({"unchecked"})
    public HashMap<Stat, Double> getData(String playerName, String tabla) {
        return (HashMap<Stat, Double>) Objects.requireNonNullElse(operation(OperationType.GET_PLAYER_DATA, playerName, tabla, false), new HashMap<>());
    }
    public String[] getPlayerNames() {
        return (String[]) Objects.requireNonNullElse(operation(OperationType.GET_PLAYERS_NAME, null, null, false), new String[0]);
    }
    @SuppressWarnings({"unchecked"})
    public List<PlayerStats> getPlayersStats(boolean incluirNoFiables, String tabla) {
        return (List<PlayerStats>) Objects.requireNonNullElse(operation(OperationType.GET_PLAYERS_DATA, null, tabla, incluirNoFiables), new LinkedList<>());
    }

    private boolean hasAccount_(String playerName, String tabla) throws SQLException {
        StringBuilder query = new StringBuilder();
        if (!Main.ALL_TABLES.equals(tabla)) {
            query.append("SELECT * FROM ").append(tabla).append(" WHERE PlayerName = ?");
        } else {
            query.append("SELECT * FROM ").append(tables.get(0)).append(" ");
            for (int i = 1; i < tables.size(); i++) {
                query.append("INNER JOIN ").append(tables.get(i)).append(" ON ").append(tables.get(0)).append(".PlayerName = ").append(tables.get(i)).append(".PlayerName").append(" ");
            }
            query.append("WHERE ").append(tables.get(0)).append(".PlayerName = ?");
        }
        PreparedStatement ps = this.connection.prepareStatement(query.toString());
        ps.setString(1, playerName);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    private HashMap<Stat, Double> getData_(String playerName, String tabla) throws SQLException {
        HashMap<Stat, Double> toret = new HashMap<>();
        StringBuilder query = new StringBuilder();
        if (!Main.ALL_TABLES.equals(tabla)) {
            query.append("SELECT * FROM ").append(tabla).append(" WHERE PlayerName = ?");
        } else {
            query.append("SELECT UUID, PlayerName,sum(Kills) Kills,sum(Deaths) Deaths,sum(Anoted_Points) Anoted_Points,sum(Games_Played) Games_Played,sum(Wins) Wins,sum(Blocks_Broken) Blocks_Broken,sum(Blocks_Placed) Blocks_Placed FROM (");
            for (int i = 0; i < tables.size(); i++) {
                query.append("SELECT * FROM ").append(tables.get(i));
                if (i != tables.size() - 1) query.append(" UNION ALL ");
            }
            query.append(") t WHERE PlayerName = ?");
        }
        if (hasAccount(playerName, tabla)) {
            PreparedStatement ps = this.connection.prepareStatement(query.toString());
            ps.setString(1, playerName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                for (int j = 3; j < 10; j++)
                    toret.put(Stat.valueOf(rs.getMetaData().getColumnName(j).toUpperCase()), (double) rs.getInt(j));
            }
        }
        return toret;
    }

    private String[] getPlayerNames_() throws SQLException {
        List<String> list = new ArrayList<>();
        PreparedStatement ps = this.connection.prepareStatement("SELECT PlayerName FROM " + tables.get(0));
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            list.add(rs.getString(1));
        String[] toret = new String[list.size()];
        list.toArray(toret);
        return toret;
    }

    private List<PlayerStats> getPlayersStats_(boolean incluirNoFiables, String tabla) throws SQLException {
        List<PlayerStats> toret = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        if (!Main.ALL_TABLES.equals(tabla)) {
            query.append("SELECT * FROM ").append(tabla);
        } else {
            query.append("SELECT UUID, PlayerName,sum(Kills) Kills,sum(Deaths) Deaths,sum(Anoted_Points) Anoted_Points,sum(Games_Played) Games_Played,sum(Wins) Wins,sum(Blocks_Broken) Blocks_Broken,sum(Blocks_Placed) Blocks_Placed FROM (");
            for (int i = 0; i < tables.size(); i++) {
                query.append("SELECT * FROM ").append(tables.get(i));
                if (i != tables.size() - 1) query.append(" UNION ALL ");
            }
            query.append(") t GROUP BY PlayerName");
        }
        PreparedStatement ps = this.connection.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        if (incluirNoFiables)
            toret.addAll(PlayerStats.listFromResultSet(rs));
        else {
            while (rs.next()) {
                PlayerStats p = new PlayerStats(rs);
                if (p.statsFiables)
                    toret.add(p);
            }
        }
        return toret;
    }

    public List<String> getTables() {
        return tables;
    }
}
