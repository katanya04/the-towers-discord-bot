package me.katanya04.enums;

public enum Rank {
    PAPU(10, "<:papu:1115273210120896552>", "<:papu:1115273210120896552>"),
    S(8, "<:emoji_31:1060615777578926230>", "<a:s_:1102714420355936306>"),
    A(6, "<:emoji_32:1060616139476041738>", "<a:a_:1102571281431416974>"),
    B(4, "<:emoji_33:1060616189497319454>", "<a:b_:1102693833013198918>"),
    C(2, "<:emoji_75:1085257658422001824>", "<a:c_:1102693846695034900>"),
    D(1, "<:emoji_76:1085257725186932796>", "<a:d_:1102693863543554078>"),
    E(0.5, "<:emoji_77:1085257785622671472>", "<a:e_:1102693873744085023>"),
    F(0, "<:emoji_78:1085257863863210034>", "<a:f_:1102693885957914704>");
    private final double points;
    private final String emoji;
    private final String animatedEmoji;
    Rank(double points, String emoji, String animatedEmoji) {
        this.points = points;
        this.emoji = emoji;
        this.animatedEmoji = animatedEmoji;
    }
    public static Rank getRank(double points) {
        for (Rank r: values()) {
            if (points >= r.points)
                return r;
        }
        return F;
    }
    public String getEmoji() {
        return this.emoji;
    }

    public String getAnimatedEmoji() {
        return this.animatedEmoji;
    }
}
