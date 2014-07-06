package net.cubitum.fortylife.search;

public interface ICardSearchCache {

    public String get(String key);

    public void put(String key, String string);
}
