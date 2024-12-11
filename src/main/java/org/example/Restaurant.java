package org.example;

import com.renomad.minum.database.DbData;

import java.util.Objects;

import static com.renomad.minum.utils.SerializationUtils.deserializeHelper;
import static com.renomad.minum.utils.SerializationUtils.serializeHelper;

public class Restaurant extends DbData<Restaurant> {

    private long index;
    private final String name;
    public static final Restaurant EMPTY = new Restaurant(0, "");

    public Restaurant(long index, String name) {

        this.index = index;
        this.name = name;
    }

    @Override
    protected String serialize() {
        return serializeHelper(index, name);
    }

    @Override
    protected Restaurant deserialize(String serializedText) {
        final var tokens = deserializeHelper(serializedText);
        return new Restaurant(
                Long.parseLong(tokens.get(0)),
                tokens.get(1)
        );
    }

    @Override
    protected long getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    @Override
    protected void setIndex(long index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return index == that.index && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, name);
    }


}
