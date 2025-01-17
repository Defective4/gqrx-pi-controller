package io.github.defective4.raspberrypi.gqctl;

import java.io.File;
import java.io.IOException;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;

import io.github.defective4.raspberrypi.gqctl.GqrxController.Modulation;

public class Main {

    private static final Modulation[] availableMods = Modulation.values();
    private static int freq = 0;
    private static int modIndex = 0;
    private static int step = 1000;

    public static void main(String[] args) {
        try {
            File propsFile = new File("gqpi.properties");
            AppProperties props = new AppProperties(propsFile);
            if (!propsFile.exists()) {
                props.save();
                System.err.println("Properties file saved");
                System.exit(1);
            }
            props.load();

            Context ctx = Pi4J.newAutoContext();
            DigitalInput modButton = ctx.digitalInput().create(props.getModulationButton());
            DigitalInput stepButton = ctx.digitalInput().create(props.getStepButton());
            DigitalInput backButton = ctx.digitalInput().create(props.getBackButton());
            DigitalInput forwardButton = ctx.digitalInput().create(props.getForwardButton());

            try (GqrxController ctl = new GqrxController(props.getGqrxHost(), props.getGqrxPort())) {
                DigitalStateChangeListener modLs = state -> {
                    if (state.state() == DigitalState.HIGH) {
                        modIndex++;
                        if (modIndex >= availableMods.length) modIndex = 0;
                        try {
                            ctl.setModulation(availableMods[modIndex]);
                            Notification.show(availableMods[modIndex].name());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                DigitalStateChangeListener stepLs = state -> {
                    if (state.state() == DigitalState.HIGH) {
                        step *= 10;
                        if (step > 100e6) {
                            step = 1000;
                        }
                        if (step >= 1e6) Notification.show(step / 1000000 + " MHz");
                        else Notification.show(step / 1000 + " KHz");
                    }
                };

                DigitalStateChangeListener backLs = state -> {
                    if (state.state() == DigitalState.HIGH) {
                        freq -= step;
                        freq = Math.max(0, freq);
                        try {
                            ctl.setFrequency(freq);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                DigitalStateChangeListener forwardLs = state -> {
                    if (state.state() == DigitalState.HIGH) {
                        freq += step;
                        try {
                            ctl.setFrequency(freq);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ctl.connect();
                freq = ctl.getFrequency();
                ctl.setModulation(availableMods[0]);
                modButton.addListener(modLs);
                stepButton.addListener(stepLs);
                backButton.addListener(backLs);
                forwardButton.addListener(forwardLs);
                while (true) {
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
