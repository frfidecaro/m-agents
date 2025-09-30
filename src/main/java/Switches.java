import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;

import astra.core.Agent;
import astra.core.Module;
import astra.formula.Predicate;
import astra.term.Primitive;
import astra.term.Term;

public class Switches extends Module {
    private static final String LABEL_OFF = "Off";
    private static final String LABEL_ON = "On";
    private static Primitive<String> ON = Primitive.newPrimitive("on");
    private static Primitive<String> OFF = Primitive.newPrimitive("off");
    private Map<String, Switch> switches = new HashMap<>();
    
    private class Switch extends JFrame {
        private JButton button;
        private int buttonState;
        private String name;

        public Switch(String name) {
            this.name = name;
            button = new JButton(LABEL_OFF);
            button.setBackground(Color.RED);
            buttonState = 0;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonState = (buttonState+1) % 2;
                    button.setText(buttonState==0 ? LABEL_OFF:LABEL_ON);
                }
            });

            this.setTitle("Switch: " + name);
            this.setLayout(new BorderLayout());
            this.add(BorderLayout.CENTER, button);
            this.setSize(150, 100);
            this.setVisible(true);
        }

        private int lastState = -1;
        private Predicate lastPredicate = null;

        public void sense(Agent agent) {
            if (buttonState == lastState) return;

            lastState = buttonState;
            if (lastPredicate != null) agent.beliefs().dropBelief(lastPredicate);
            agent.beliefs().addBelief(lastPredicate = new Predicate("switch", new Term[] {Primitive.newPrimitive(name), buttonState == 0 ? OFF:ON}));
        }

    }

    public Switches() {
    }

    @ACTION
    public boolean create(String name) {
        if (switches.containsKey(name)) return false;
        switches.put(name, new Switch(name));
        return true;
    }

    @SENSOR
    public void sense() {
        for (Switch sw : switches.values()) {
            sw.sense(agent);
        }
    }
}
