import java.util.ArrayList;
import java.util.List;


public class Section implements Comparable{
	List<Student> students = new ArrayList<Student>();
	List<Prof> professors = new ArrayList<Prof>();
	String id = "";
	int ExamPeriod = -1;
	boolean IsPlaced = false;
	@Override
	public int compareTo(Object arg0) {
		if(arg0 instanceof Section)
			return this.students.size()-((Section)arg0).students.size();
		else
		{
			return 0;
		}
	}
	
}
