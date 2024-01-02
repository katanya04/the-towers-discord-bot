package me.katanya04.enums;

public enum Stat {
    KILLS("Kills", "\u2694 ", false, true),
    DEATHS("Muertes", "\uD83D\uDC80 ", false, true),
    ANOTED_POINTS("Puntos", "\uD83C\uDF96 ", false, true),
    GAMES_PLAYED("Partidas_jugadas", "\uD83C\uDFDF ", false, true),
    WINS("Wins", "\uD83C\uDFC6 ", false, true),
    BLOCKS_BROKEN("Bloques_rotos", "\u26CF ", false, true),
    BLOCKS_PLACED("Bloques_colocados", "\u26CF ", false, true),
    KILL_DEATH_RATIO("K/D", "▶ ", false, false),
    WIN_RATE("Win_rate", "▶ ", true, false),
    POINTS_PER_GAME("Puntos_por_partida", "▶ ", false, false),
    KILLS_PER_GAME("Kills_por_partida", "▶ ", false, false),
    DEATHS_PER_GAME("Muertes_por_partida", "▶ ", false, false),
    RANK("Rango", "", false, false);
    private final String text;
    private final String emoji;
    private final boolean isPercentage;
    private final boolean isInteger;
    Stat(String text, String emoji, boolean isPercentage, boolean isInteger) {
        this.text = text;
        this.emoji = emoji;
        this.isPercentage = isPercentage;
        this.isInteger = isInteger;
    }
    public String getText() {
        return this.text;
    }
    public String getEmoji() {
        return this.emoji;
    }
    public static Stat getStatFromText(String text) {
        for (Stat s : Stat.values())
            if (s.text.equals(text)) return s;
        return null;
    }
    public boolean isPercentage() {
        return this.isPercentage;
    }
    public boolean isInteger() {
        return this.isInteger;
    }
    public static Stat[] getValuesWithout(Stat stat) {
        Stat[] toret = new Stat[Stat.values().length - 1];
        int i = 0;
        for (Stat st : Stat.values())
            if (!st.equals(stat)) toret[i++] = st;
        return toret;
    }
}
