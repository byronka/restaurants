package org.example;

import com.renomad.minum.database.Db;
import com.renomad.minum.state.Context;
import com.renomad.minum.templating.TemplateProcessor;
import com.renomad.minum.utils.FileUtils;
import com.renomad.minum.utils.SearchUtils;
import com.renomad.minum.web.FullSystem;
import com.renomad.minum.web.Response;
import com.renomad.minum.web.StatusLine;

import java.util.*;
import java.util.stream.Collectors;

import static com.renomad.minum.web.RequestLine.Method.GET;
import static com.renomad.minum.web.RequestLine.Method.POST;

public class Main {

    public static void main(String[] args) {
        FullSystem fs = FullSystem.initialize();
        Context context = fs.getContext();
        org.example.FileUtils fileUtils = new org.example.FileUtils(new FileUtils(context.getLogger(), context.getConstants()), new Constants());
        Db<Restaurant> restaurants = context.getDb("restaurants", Restaurant.EMPTY);
        Db<RestaurantOrder> restaurantOrder = context.getDb("restaurantOrder", RestaurantOrder.EMPTY);
        if (restaurantOrder.values().isEmpty()) {
            restaurantOrder.write(new RestaurantOrder(0, new ArrayDeque<>()));
        }
        TemplateProcessor indexProcessor = TemplateProcessor.buildProcessor(fileUtils.readTemplate("index.html"));
        TemplateProcessor restaurantEntryProcessor = TemplateProcessor.buildProcessor(fileUtils.readTemplate("restaurant_entry.html"));

        fs.getWebFramework().registerPath(GET, "", request -> {
            RestaurantOrder currentOrder = restaurantOrder.values().stream().toList().getFirst();

            StringBuilder sb = new StringBuilder();
            for (var id : currentOrder.getOrderedIds().reversed()) {
                Map<String,String> myMap = new HashMap<>();
                Restaurant r = SearchUtils.findExactlyOne(restaurants.values().stream(), x -> x.getIndex() == id);
                myMap.put("restaurant_name", r.getName());
                myMap.put("restaurant_id", id.toString());
                sb.append(restaurantEntryProcessor.renderTemplate(myMap)).append(("\n"));
            }

            return Response.htmlOk(indexProcessor.renderTemplate(Map.of("restaurantsList", sb.toString())));
        });

        fs.getWebFramework().registerPath(POST, "add_restaurant", request -> {
            String restaurantName = request.getBody().asString("restaurant");
            if (restaurantName.length() > 50) return Response.buildLeanResponse(StatusLine.StatusCode.CODE_400_BAD_REQUEST);
            if (restaurants.values().size() > 30) return Response.buildLeanResponse(StatusLine.StatusCode.CODE_400_BAD_REQUEST);
            Restaurant newRestaurant = restaurants.write(new Restaurant(0, restaurantName));
            // update order value
            RestaurantOrder currentOrder = restaurantOrder.values().stream().toList().getFirst();
            currentOrder.getOrderedIds().add((int)newRestaurant.getIndex());
            restaurantOrder.write(currentOrder);
            return Response.redirectTo("/");
        });

        fs.getWebFramework().registerPath(POST, "increment_position", request -> {
            String idString = request.getBody().asString("id");
            long id = Long.parseLong(idString);
            Restaurant r = SearchUtils.findExactlyOne(restaurants.values().stream(), x -> x.getIndex() == id);
            RestaurantOrder currentOrder = restaurantOrder.values().stream().toList().getFirst();
            Integer[] orderedIds = currentOrder.getOrderedIds().toArray(Integer[]::new);
            int indexAtRestaurant = -1;
            ArrayDeque<Integer> adjustedDeque = null;
            for (int i = 0; i < orderedIds.length; i++) {
                if (orderedIds[i] == r.getIndex()) {
                    indexAtRestaurant = i;

                    // if we are below the ceiling, move up
                    if ((i + 1) < orderedIds.length) {
                        Integer temp = orderedIds[i];
                        orderedIds[i] = orderedIds[i + 1];
                        orderedIds[i + 1] = temp;
                    }
                    List<Integer> list = Arrays.asList(orderedIds);
                    adjustedDeque = new ArrayDeque<>(list);
                    break;
                }
            }
            if (indexAtRestaurant == -1) return Response.buildLeanResponse(StatusLine.StatusCode.CODE_400_BAD_REQUEST);
            RestaurantOrder adjustedRestaurantOrder = new RestaurantOrder(currentOrder.getIndex(), adjustedDeque);
            restaurantOrder.write(adjustedRestaurantOrder);
            return Response.redirectTo("/");
        });

        fs.getWebFramework().registerPath(POST, "decrement_position", request -> {
            String idString = request.getBody().asString("id");
            long id = Long.parseLong(idString);
            Restaurant r = SearchUtils.findExactlyOne(restaurants.values().stream(), x -> x.getIndex() == id);
            RestaurantOrder currentOrder = restaurantOrder.values().stream().toList().getFirst();
            Integer[] orderedIds = currentOrder.getOrderedIds().toArray(Integer[]::new);
            int indexAtRestaurant = -1;
            ArrayDeque<Integer> adjustedDeque = null;
            for (int i = 0; i < orderedIds.length; i++) {
                if (orderedIds[i] == r.getIndex()) {
                    indexAtRestaurant = i;

                    // if we are above the floor, move down
                    if (i > 0) {
                        Integer temp = orderedIds[i];
                        orderedIds[i] = orderedIds[i - 1];
                        orderedIds[i - 1] = temp;
                    }
                    List<Integer> list = Arrays.asList(orderedIds);
                    adjustedDeque = new ArrayDeque<>(list);
                    break;
                }
            }
            if (indexAtRestaurant == -1) return Response.buildLeanResponse(StatusLine.StatusCode.CODE_400_BAD_REQUEST);
            RestaurantOrder adjustedRestaurantOrder = new RestaurantOrder(currentOrder.getIndex(), adjustedDeque);
            restaurantOrder.write(adjustedRestaurantOrder);
            return Response.redirectTo("/");
        });

        fs.getWebFramework().registerPath(POST, "delete_restaurant", request -> {
            String idString = request.getBody().asString("id");
            long id = Long.parseLong(idString);
            Restaurant r = SearchUtils.findExactlyOne(restaurants.values().stream(), x -> x.getIndex() == id);
            restaurants.delete(r);

            RestaurantOrder currentOrder = restaurantOrder.values().stream().toList().getFirst();
            Integer[] orderedIds = currentOrder.getOrderedIds().toArray(Integer[]::new);
            int indexAtRestaurant = -1;
            ArrayDeque<Integer> adjustedDeque = new ArrayDeque<>();
            for (int i = 0; i < orderedIds.length; i++) {
                // add every restaurant id except the one we are removing
                if (orderedIds[i] == r.getIndex()) {
                    indexAtRestaurant = i;
                    continue;
                }
                adjustedDeque.add(orderedIds[i]);
            }
            if (indexAtRestaurant == -1) return Response.buildLeanResponse(StatusLine.StatusCode.CODE_400_BAD_REQUEST);
            RestaurantOrder adjustedRestaurantOrder = new RestaurantOrder(currentOrder.getIndex(), adjustedDeque);
            restaurantOrder.write(adjustedRestaurantOrder);

            return Response.redirectTo("/");
        });



        fs.block();
    }
}
