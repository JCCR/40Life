package net.cubitum.fortylife.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by JuanCarlos on 12/10/13.
 */
public class Card {
    public String id;
    public String imageUrl;
    public String name;
    public String text;
    public String flavorText;
    public String type;
    public String subType;
    public String manaCostText;
    public int convertedManaCost;
    public String power;
    public String toughness;
    public String set;
    public String rarity;
    public int cardNumber;
    public String artist;
    public float communityRating;

    private CardManaSymbol[] colorIdentity;
    private CardManaSymbol[] manaCost;

    public Card() {

    }

    public CardManaSymbol[] getManaCost() {
        if (manaCost == null) {
            manaCost = parseManaCostText(manaCostText);
        }
        return manaCost;

    }

    public CardManaSymbol[] getColorIdentity() {
        if (colorIdentity == null) {
            List<CardManaSymbol> list = new ArrayList<CardManaSymbol>();
            try {
                Pattern regex = Pattern.compile("\\{(.*?)\\}");
                Matcher regexMatcher = regex.matcher(text);
                while (regexMatcher.find()) {
                    CardManaSymbol c = CardManaSymbol.valueFromSymbol(regexMatcher.group().replace("{", "").replace("}", ""));
                    if(c!=null){
                        list.add(c);
                    }
                }
                colorIdentity = list.toArray(new CardManaSymbol[list.size()]);;
            } catch (PatternSyntaxException ex) {
            }
        }
        return colorIdentity;
    }

    private static CardManaSymbol[] parseManaCostText(String text) {
        if(text.equals("0")){
            return new CardManaSymbol[]{CardManaSymbol.ZERO};
        }
        List<CardManaSymbol> list = new ArrayList<CardManaSymbol>(text.length());
        String tmpString = "";
        String tmpNumber = "";
        boolean buildString = false;
        boolean buildNumber = false;
        for (char c : text.toCharArray()) {
            if (c >= '0' && c <= '9') {
                buildNumber = true;
                tmpNumber += String.valueOf(c);
                continue;
            } else if (c == '(') {
                buildString = true;
            } else if (c == ')') {
                tmpString+=")";
                buildString = false;
                list.add(CardManaSymbol.valueFromSymbol(tmpString));
                tmpString = "";
                continue;
            }
            if (buildNumber) {
                buildNumber = false;
                int number = Integer.parseInt(tmpNumber);
                for (int i = 0; i < number; i++) {
                    list.add(CardManaSymbol.COLORLESS);
                }
                tmpNumber = "";
            }
            if (buildString) {
                tmpString += String.valueOf(c);
            } else {
                list.add(CardManaSymbol.valueFromSymbol(String.valueOf(c)));
            }
        }
        return list.toArray(new CardManaSymbol[list.size()]);
    }

}
