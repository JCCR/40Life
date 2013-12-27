package net.cubitum.fortylife.search;

/**
* Created by JuanCarlos on 12/10/13.
*/
public enum CardManaSymbol {
    VARIABLE("X"),
    ZERO("0"),
    COLORLESS("C"),
    WHITE("W"),
    BLUE("U"),
    BLACK("B"),
    RED("R"),
    GREEN("G"),
    WHITE_OR_BLUE("(W/U)"),
    WHITE_OR_BLACK("(W/B)"),
    BLUE_OR_BLACK("(U/B)"),
    BLUE_OR_RED("(U/R)"),
    BLACK_OR_RED("(B/R)"),
    BLACK_OR_GREEN("(B/G)"),
    RED_OR_GREEN("(R/G)"),
    RED_OR_WHITE("(R/W)"),
    GREEN_OR_WHITE("(G/W)"),
    GREEN_OR_BLUE("(G/U)"),
    TWO_COLORLESS_OR_WHITE("(2/W)"),
    TWO_COLORLESS_OR_BLUE("(2/U)"),
    TWO_COLORLESS_OR_BLACK("(2/B)"),
    TWO_COLORLESS_OR_RED("(2/R)"),
    TWO_COLORLESS_OR_GREEN("(2/G)"),
    PHYREXIAN_WHITE("(W/P)"),
    PHYREXIAN_BLUE("(U/P)"),
    PHYREXIAN_BLACK("(B/P)"),
    PHYREXIAN_RED("(R/P)"),
    PHYREXIAN_GREEN("(G/P)"),
    SNOW("S");

    private final String symbol;

    private CardManaSymbol(String s) {
        symbol = s;
    }

    public static CardManaSymbol valueFromSymbol(String symbol){
        for(CardManaSymbol c : CardManaSymbol.values()){
            if(c.symbol.equals(symbol)){
                return c;
            }
        }
        return null;
    }

    public String toString(){
        return symbol;
    }
}
