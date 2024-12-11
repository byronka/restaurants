package org.example;

import com.renomad.minum.database.Db;
import com.renomad.minum.state.Context;
import com.renomad.minum.templating.TemplateProcessor;
import com.renomad.minum.utils.FileUtils;
import com.renomad.minum.utils.SearchUtils;
import com.renomad.minum.web.FullSystem;
import com.renomad.minum.web.Response;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.renomad.minum.web.RequestLine.Method.GET;
import static com.renomad.minum.web.RequestLine.Method.POST;

public class Main {

    public static void main(String[] args) {
        FullSystem fs = FullSystem.initialize();
        Context context = fs.getContext();
        var fileUtils = new FileUtils(context.getLogger(), context.getConstants());
        Db<Restaurant> restaurants = context.getDb("restaurants", Restaurant.EMPTY);
        TemplateProcessor indexProcessor = TemplateProcessor.buildProcessor(fileUtils.readTextFile("src/main/webapp/templates/index.html"));
        TemplateProcessor restaurantEntryProcessor = TemplateProcessor.buildProcessor(fileUtils.readTextFile("src/main/webapp/templates/restaurant_entry.html"));

        fs.getWebFramework().registerPath(GET, "", request -> {
            String restaurantsString = restaurants.values().stream()
                    .sorted(Comparator.comparingInt(Restaurant::getPlaceInList))
                    .map(x -> {
                        Map<String,String> myMap = new HashMap<>();
                        myMap.put("restaurant_name", x.getName());
                        myMap.put("restaurant_id", String.valueOf(x.getIndex()));
                        return restaurantEntryProcessor.renderTemplate(myMap);
                    }).collect(Collectors.joining("\n"));

            return Response.htmlOk(indexProcessor.renderTemplate(Map.of("restaurantsList", restaurantsString)));
        });

        fs.getWebFramework().registerPath(POST, "add_restaurant", request -> {
            String restaurantName = request.getBody().asString("restaurant");
            restaurants.write(new Restaurant(0, restaurantName, 0));
            return Response.redirectTo("/");
        });

        fs.getWebFramework().registerPath(POST, "increment_position", request -> {
            String idString = request.getBody().asString("id");
            long id = Long.parseLong(idString);
            Restaurant r = SearchUtils.findExactlyOne(restaurants.values().stream(), x -> x.getIndex() == id);
            int newPlaceInList = r.getPlaceInList() + 1;
            restaurants.write(new Restaurant(r.getIndex(), r.getName(), newPlaceInList));
            return Response.redirectTo("/");
        });

        fs.getWebFramework().registerPath(POST, "decrement_position", request -> {
            String idString = request.getBody().asString("id");
            long id = Long.parseLong(idString);
            Restaurant r = SearchUtils.findExactlyOne(restaurants.values().stream(), x -> x.getIndex() == id);
            int newPlaceInList = r.getPlaceInList() - 1;
            restaurants.write(new Restaurant(r.getIndex(), r.getName(), newPlaceInList));
            return Response.redirectTo("/");
        });



        fs.block();
    }
}
