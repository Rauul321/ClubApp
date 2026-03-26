package Server;

/**
 * Class representing a competition activity.
 */
public class Competition extends Activity {
    public Competition(int id, String name, Date date) {
        super(id, name, date);
    }

    @Override
    public String getType() {
        return "Competition";
    }
}
