import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import astra.core.Agent;
import astra.core.Module;
import astra.formula.Predicate;
import astra.term.Primitive;
import astra.term.Term;

public class Lights extends Module {
    private static Primitive<String> ON = Primitive.newPrimitive("on");
    private static Primitive<String> OFF = Primitive.newPrimitive("off");
    private static ImageIcon lightOff = new ImageIcon("../images/lightOff.png");
    private static ImageIcon lightOn = new ImageIcon("../images/lightOn.png");
    private Map<String, Light> lights = new HashMap<>();

    public Lights() {
    }

    private class Light extends JFrame {
        private JLabel light;
        private int lightState;
        private String name;

        public Light(String name) {
            this.name = name;
            light = new JLabel();
            light.setIcon(lightOff);

            this.setTitle("Light: " + name);
            this.setLayout(new BorderLayout());
            this.add(BorderLayout.CENTER, light);
            this.setSize(150, 100);
            this.setVisible(true);
        }

        public boolean set(String state) {
            if (state.equals("on")) {
                lightState = 1;
            } else if (state.equals("off")) {
                lightState = 0;
            } else {
                return false;
            }
            
            light.setIcon(lightState==0 ? lightOff:lightOn);
            return true;
        }

        private int lastState = -1;
        private Predicate lastPredicate = null;

        public void sense(Agent agent) {
            if (lightState == lastState) return;

            lastState = lightState;
            if (lastPredicate != null) agent.beliefs().dropBelief(lastPredicate);
            agent.beliefs().addBelief(lastPredicate = new Predicate("light", new Term[] {Primitive.newPrimitive(name), lightState == 0 ? OFF:ON}));
        }
    }

    @ACTION
    public boolean create(String name) {
        if (lights.containsKey(name)) return false;
        lights.put(name, new Light(name));
        return true;
    }

    @ACTION
    public boolean set(String name, String state) {
        if (!lights.containsKey(name)) return false;
        return lights.get(name).set(state);
    }

    @SENSOR
    public void sense() {
        for (Light light:lights.values()) {
            light.sense(agent);
        }
    }
}
