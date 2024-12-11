package org.example;

public final class FileUtils {

    private final com.renomad.minum.utils.FileUtils fileUtils;
    private final Constants constants;

    public FileUtils(com.renomad.minum.utils.FileUtils fileUtils, Constants constants) {
        this.fileUtils = fileUtils;
        this.constants = constants;
    }

    /**
     * Read a template file, expected to use this with {@link com.renomad.minum.templating.TemplateProcessor}
     */
    public String readTemplate(String path) {
        return fileUtils.readTextFile(constants.TEMPLATE_DIRECTORY + path);
    }

}
