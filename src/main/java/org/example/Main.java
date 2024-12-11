package org.example;

import com.renomad.minum.state.Context;
import com.renomad.minum.templating.TemplateProcessor;
import com.renomad.minum.utils.FileUtils;
import com.renomad.minum.web.FullSystem;
import com.renomad.minum.web.Response;

import java.util.Map;

import static com.renomad.minum.web.RequestLine.Method.GET;

public class Main {

    public static void main(String[] args) {
        FullSystem fs = FullSystem.initialize();
        Context context = fs.getContext();
        var fileUtils = new FileUtils(context.getLogger(), context.getConstants());
        TemplateProcessor indexProcessor = TemplateProcessor.buildProcessor(fileUtils.readTextFile("src/main/webapp/templates/index.html"));
        fs.getWebFramework().registerPath(GET, "", request -> Response.htmlOk(indexProcessor.renderTemplate(Map.of())));
        fs.block();
    }
}
