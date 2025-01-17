package io.github.defective4.raspberrypi.gqctl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Properties;

public class AppProperties extends Properties {
    protected String backButton = "0";
    protected String forwardButton = "0";
    protected String gqrxHost = "localhost";
    protected String gqrxPort = "7356";
    protected String modulationButton = "0";
    protected String stepButton = "0";
    private final File file;

    public AppProperties(File file) {
        this.file = file;
    }

    public int getBackButton() {
        return Integer.parseInt(backButton);
    }

    public int getForwardButton() {
        return Integer.parseInt(forwardButton);
    }

    public String getGqrxHost() {
        return gqrxHost;
    }

    public int getGqrxPort() {
        return Integer.parseInt(gqrxPort);
    }

    public int getModulationButton() {
        return Integer.parseInt(modulationButton);
    }

    public int getStepButton() {
        return Integer.parseInt(stepButton);
    }

    public void load() throws IOException {
        try (Reader reader = new FileReader(file)) {
            load(reader);
            for (Field field : getClass().getDeclaredFields()) if (field.getType() == String.class) {
                Object val = getProperty(field.getName());
                if (val != null) try {
                    field.set(this, val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save() throws IOException {
        try (Writer writer = new FileWriter(file)) {
            for (Field field : getClass().getDeclaredFields()) if (field.getType() == String.class) try {
                setProperty(field.getName(), (String) field.get(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
            store(writer, null);
        }
    }
}
