package me.katanya04.database;

import me.katanya04.enums.Stat;
import me.katanya04.stats.PlayerStats;

public class ListCache {
    private PlayerStats[] cache;
    private Stat stat;
    private boolean incluyeNoFiables;
    private String tabla;

    public boolean isInCache(Stat stat, boolean incluirNoFiables, String tabla) {
        return this.stat.equals(stat) && this.incluyeNoFiables == incluirNoFiables && this.tabla.equals(tabla);
    }

    public void setCache(PlayerStats[] cache, Stat stat, boolean incluirNoFiables, String tabla) {
        this.cache = cache;
        this.stat = stat;
        this.incluyeNoFiables = incluirNoFiables;
        this.tabla = tabla;
    }

    public PlayerStats[] getStatList() {
        return cache;
    }
}
