package org.example;

import com.renomad.minum.utils.MyThread;
import com.renomad.minum.utils.TimeUtils;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

public class Constants {

    private final Properties properties;

    public Constants() {
        this(null);
    }

    public Constants(Properties props) {
        properties = Objects.requireNonNullElseGet(props, Constants::getConfiguredProperties);
        TEMPLATE_DIRECTORY = properties.getProperty("TEMPLATE_DIRECTORY", "src/main/webapp/templates/");
    }

    /**
     * Where we store HTML templates
     */
    public final String TEMPLATE_DIRECTORY;


    /**
     * This overload allows you to specify that the contents of the
     * properties file should be shown when it's read.
     */
    public static Properties getConfiguredProperties() {
        var properties = new Properties();
        String fileName = "restaurant.config";
        try (FileInputStream fis = new FileInputStream(fileName)) {
            System.out.println(TimeUtils.getTimestampIsoInstant() +
                    " found properties file at ./restaurant.config.  Loading properties");
            properties.load(fis);
        } catch (Exception ex) {
            System.out.println("""

                    ********************

                    failed to successfully read restaurant.config: using built-in defaults

                    ***************************


                    """);
            MyThread.sleep(1000);
            System.out.print("Continuing in 5...");
            MyThread.sleep(1000);
            System.out.print("4...");
            MyThread.sleep(1000);
            System.out.print("3...");
            MyThread.sleep(1000);
            System.out.print("2...");
            MyThread.sleep(1000);
            System.out.print("1...");
            MyThread.sleep(1000);
            System.out.print("\n\n");
            return new Properties();
        }
        return properties;
    }
}
