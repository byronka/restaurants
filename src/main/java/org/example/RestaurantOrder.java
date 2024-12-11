package org.example;

import com.renomad.minum.database.DbData;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.renomad.minum.utils.SerializationUtils.deserializeHelper;
import static com.renomad.minum.utils.SerializationUtils.serializeHelper;

public class RestaurantOrder extends DbData<RestaurantOrder> {


    private long index;
    private final Deque<Integer> orderedIds;
    public static final RestaurantOrder EMPTY = new RestaurantOrder(0, null);

    public RestaurantOrder(long index, Deque<Integer> orderedIds) {
        this.index = index;
        this.orderedIds = orderedIds;
    }

    @Override
    protected String serialize() {
        String valuesAsString = orderedIds.stream().map(Object::toString).collect(Collectors.joining(";"));
        return serializeHelper(index, valuesAsString);
    }

    @Override
    protected RestaurantOrder deserialize(String serializedText) {
        final var tokens = deserializeHelper(serializedText);
        String valuesAsString = tokens.get(1);
        String[] split = valuesAsString.split(";");
        ArrayDeque<Integer> integers = new ArrayDeque<>();
        for (String item : split) {
            integers.add(Integer.parseInt(item));
        }
        return new RestaurantOrder(
                Long.parseLong(tokens.getFirst()),
                integers
        );
    }

    @Override
    protected long getIndex() {
        return index;
    }

    @Override
    protected void setIndex(long index) {
        this.index = index;
    }

    public Deque<Integer> getOrderedIds() {
        return orderedIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantOrder that = (RestaurantOrder) o;
        return index == that.index && Objects.equals(orderedIds, that.orderedIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, orderedIds);
    }

    @Override
    public String toString() {
        return "RestaurantOrder{" +
                "index=" + index +
                ", orderedIds=" + orderedIds +
                '}';
    }

}
