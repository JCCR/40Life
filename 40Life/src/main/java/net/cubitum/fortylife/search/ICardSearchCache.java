package net.cubitum.fortylife.search;

/**
 * Created by JuanCarlos on 12/28/13.
 */
public interface ICardSearchCache {

    public String get(String key);
    public void put(String key, String string);
}
