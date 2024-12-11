package org.example;

import com.renomad.minum.database.Db;
import com.renomad.minum.state.Context;
import com.renomad.minum.templating.TemplateProcessor;
import com.renomad.minum.utils.FileUtils;
import com.renomad.minum.web.FullSystem;
import com.renomad.minum.web.Response;

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
        fs.getWebFramework().registerPath(GET, "", request -> {
            String restaurantsString = restaurants.values().stream().map(x ->  "<li><a href=\"#\">❌</a>"+x.getName()+"<a href=\"#\">\uD83E\uDC71</a> <a href=\"#\">\uD83E\uDC73</a></li>").collect(Collectors.joining("\n"));

            return Response.htmlOk(indexProcessor.renderTemplate(Map.of("restaurantsList", restaurantsString)));
        });
        fs.getWebFramework().registerPath(POST, "add_restaurant", request -> {
            String restaurantName = request.getBody().asString("restaurant");
            restaurants.write(new Restaurant(0, restaurantName, 0));
            return Response.redirectTo("/");
        });
        fs.block();
    }
}
