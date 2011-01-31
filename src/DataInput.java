import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;


public class DataInput {
	public static void ReadData(String file1, String file2, Processor p)
	{
		try{
			GetStudentsClasses(GetProfsClasses(file1, p), file2, p);
		}
		catch(Exception e) {}
	}
	private static void GetStudentsClasses(Hashtable<String, Section> classes, String file1, Processor p)
	{
		Hashtable<String, Student> students = new Hashtable<String, Student>();
		FileReader studentsFile;
		try {
			studentsFile = new FileReader(new File(file1));
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return;
		}
		try {
			Section currentClass = null;
			StringBuffer sb = new StringBuffer();
			int nextchar = studentsFile.read();
			while (nextchar != -1)
			{
				if((char)nextchar== '\t'){
					currentClass = classes.get(sb.toString());
//					if(currentClass == null)
//					{
//						Section s = new Section();
//						s.id = sb.toString();
//						classes.put(sb.toString(), s);
//					}
					sb = new StringBuffer();
				}
				else if((char)nextchar== '\n'){
					Student newStudent = students.get(sb.toString());
					if(newStudent == null)
					{
						Student s = new Student();
						s.id = sb.toString();
						newStudent = s;
						students.put(sb.toString(), s);
						p.Students.add(newStudent);
					}
					newStudent.classes.add(currentClass);
					currentClass.students.add(newStudent);
					currentClass = null;
					sb = new StringBuffer();
				}
				else{
					sb.append((char)nextchar);
				}
				nextchar = studentsFile.read();
			}
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return;
		}
	}
	private static Hashtable<String, Section> GetProfsClasses(String file1, Processor p) throws Exception
	{
		Hashtable<String, Prof> profs = new Hashtable<String, Prof>();
		Hashtable<String, Section> classes = new Hashtable<String, Section>();
		FileReader profsFile;
		try {
			profsFile = new FileReader(new File(file1));
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
			e.printStackTrace();
			throw e;
			//return null;
		}
		try {
			Section currentClass = null;
			StringBuffer sb = new StringBuffer();
			int nextchar = profsFile.read();
			while (nextchar != -1)
			{
				if((char)nextchar== '\t'){
					currentClass = classes.get(sb.toString());
					if(currentClass == null)
					{
						Section s = new Section();
						s.id = sb.toString();
						currentClass = s;
						classes.put(sb.toString(), s);
					}
					sb = new StringBuffer();
				}
				else if((char)nextchar== '\n'){
					Prof newProf = profs.get(sb.toString());
					if(newProf == null)
					{
						Prof s = new Prof();
						s.id = sb.toString();
						newProf = s;
						profs.put(sb.toString(), s);
						p.Classes.add(currentClass);
						p.Professors.add(newProf);
					}
					currentClass.professors.add(newProf);
					newProf.classes.add(currentClass);
					currentClass = null;
					sb = new StringBuffer();
				}
				else{
					sb.append((char)nextchar);
				}
				nextchar = profsFile.read();
			}
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
			throw e;
		}
		return classes;
	}
}
