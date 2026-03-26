package Server;

/**
 * Class representing a Course activity.
 */
public class Course extends Activity {
    public Course(int id, String name, Date date) {
        super(id, name, date);
    }

    @Override
    public String getType() {
        return "Course";
    }
}
