package net.cubitum.fortylife.search;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
/**
 * Created by JuanCarlos on 12/10/13.
 */
public class CardSearch {
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36";
    private String lastCardFromPreviousResult;
    private int page = 0;
    private boolean noMoreResults = false;
    private CardManaSymbol[] manaColors;
    private Card[] results;
    private int count = 0;
    private ICardSearchCache cache;
    public CardSearch(ICardSearchCache cache){
        this.cache = cache;
        this.setOnCardSetListener(new OnCardSetListener() {
            @Override
            public void onCardSet() {
                count++;
                if(count==results.length){
                    mResultsLoadedListener.onResultsLoaded();
                }
            }
        });
    }

    public int search(){
        page = 0;
        noMoreResults = false;
        lastCardFromPreviousResult = "";
        return search(0);
    }

    private OnCardSetListener mCardSetListener;

    private void setOnCardSetListener(OnCardSetListener mCardSetListener) {
        this.mCardSetListener = mCardSetListener;
    }

    private interface OnCardSetListener {
        public void onCardSet();
    }

//    private OnSearchReadyListener mSearchReadyListener;
//
//    private void setOnSearchReadyListener(OnSearchReadyListener mSearchReadyListener) {
//        this.mSearchReadyListener = mSearchReadyListener;
//    }
//
//    private interface OnSearchReadyListener {
//        public void onSearchReady(int result);
//    }

    private OnResultsLoadedListener mResultsLoadedListener;

    public void setOnResultsLoadedListener(OnResultsLoadedListener mResultsLoadedListener) {
        this.mResultsLoadedListener = mResultsLoadedListener;
    }

    public interface OnResultsLoadedListener {
        public void onResultsLoaded();
    }


public CardSearch filter(CardManaSymbol[] manaColors){
    this.manaColors = manaColors;
    return this;
}
    private int search(int page){
        count = 0;
        String[] colors = null;
        if(manaColors != null && manaColors.length > 0){
            colors= new String[manaColors.length];
            for(int i = 0; i < manaColors.length; i++){
                colors[i] = manaColors[i].toString();
            }
        }
        String[] cards = getCardList(colors,page);
        if(cards == null){
            return -1;
        }
        if(cards.length == 0){
            return 0;
        }
        lastCardFromPreviousResult = cards[0];
        results = new Card[cards.length];
        for(int i = 0; i < cards.length; i++){

            final Card card = new Card();
            card.name = cards[i];
            results[i] = card;
            new Thread(){
                public void run(){
                    String cacheUrl = cache.get(card.name);
                    if(cacheUrl==null){
                        Document doc = null;
                        try {
                            doc = Jsoup.connect("http://magiccards.info/query?q=%21"+card.name).timeout(20000).userAgent(USER_AGENT).get();
                        } catch (IOException e) {

                        }
                        if(doc != null){
                            cacheUrl = doc.select("html > body > table:nth-of-type(3) > tbody > tr > td:nth-of-type(1) > img").attr("src");
                            cache.put(card.name,cacheUrl);
                        }
                    }
                    card.imageUrl = cacheUrl;
                    mCardSetListener.onCardSet();
                }
            }.start();
        }
        return cards.length;
    }

    public int next(){
        if(noMoreResults){
            Log.w("TEST","NoMoreResults");
            return 0;
        }
        page++;
        Log.w("TEST","Page = "+page);
        String lastCard = lastCardFromPreviousResult;
        Log.w("TEST","LastCard = " + lastCard);
        int search = search(page);
        Log.w("TEST","Result = "+search);
        if(search==0){
            noMoreResults = true;
            return 0;
        }
        noMoreResults = lastCard.equals(lastCardFromPreviousResult);
        return search;
    }

    public Card[] results(){
        return results;
    }

    private String[] getCardList(String[] colors, int page){
        Document doc;
        String colorQuery = "";
        if(colors!=null){
            if(colors.length>0){
                colorQuery+="&color=+@(";
                for(String s : colors){
                    colorQuery+="+["+s.toUpperCase()+"]";
                }
                colorQuery+=")";
            }
        }
        try {
            doc = Jsoup.connect("http://gatherer.wizards.com/Pages/Search/Default.aspx?output=compact&sort=rating-&action=advanced&type=+[\"Legendary\"]+[\"Creature\"]&page=" + page + colorQuery).timeout(20000).userAgent(USER_AGENT).get();
        } catch (IOException e) {
            return null;
        }

        Elements es = doc.select(".cardItem");
        String[] result = new String[es.size()];
        int i = 0;
        for(Element e : es){
            result[i] = e.select(".name").text();
            i++;
        }
        return result;
    }
}
