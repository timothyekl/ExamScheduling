import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class Processor {
	List<Prof> Professors = new ArrayList<Prof>();
	List<Student> Students = new ArrayList<Student>();
	ArrayList<Section> Classes = new ArrayList<Section>();
	static boolean SkipHumanities = true;
	private class Comp implements Comparator<Section>
	{

		public int compare(Section arg0, Section arg1) {
			return arg0.students.size() - arg1.students.size();
		}
		
	}
	
	public static void main(String args[]){
		Processor p = new Processor();
		DataInput.ReadData("Profs.dat", "Students.dat", p);
		p.SortSections();
		
		p.DisplaySchedule(p.GenerateExamSchedule(11, 34));
	}
	private void SortSections()
	{
		Object[] x = Classes.toArray();
		Arrays.sort(x);
		Classes = new ArrayList();
		for(int i=x.length-1;i>=0;--i)
		{
			Classes.add((Section)x[i]);
//			System.out.println(((Section)x[i]).id);
//			if(((Section)x[i]).id.equals("PH112"))
//			{
//				Section xi = (Section)x[i];
//				System.out.print("\t");
//				for(int j=0;j<xi.professors.size();++j)
//				{
//					System.out.print(xi.professors.get(j).id+" ");
//				}
//				System.out.print("\n\t");
//				for(int j=0;j<xi.students.size();++j)
//				{
//					System.out.print(xi.students.get(j).id+" ");
//				}
//				System.out.println();
//			}
		}
		
	}
	public ArrayList<Section>[] GenerateExamSchedule(int NumPeriods, int NumRooms)
	{
		ArrayList<Section>[] classSchedule = new ArrayList[NumPeriods];
		for(int i=0;i<NumPeriods;++i)
		{
			classSchedule[i] = new ArrayList<Section>();
		}
		IterateThroughClasses(true, true, classSchedule, NumPeriods-2, NumRooms);
		PostProcessor(classSchedule, NumRooms, NumPeriods-2);
		IterateThroughClasses(false, true, classSchedule, NumPeriods-1, NumRooms);
		PostProcessor(classSchedule, NumRooms, NumPeriods-1);
		IterateThroughClasses(false, true, classSchedule, NumPeriods, NumRooms);
		PostProcessor(classSchedule, NumRooms, NumPeriods);
		return classSchedule;
	}
	
	public void IterateThroughClasses(boolean SkipHumanities, boolean CheckThrees, ArrayList<Section>[] classSchedule, int NumPeriods, int NumRooms)
	{
		for(int i=0;i<Classes.size();++i)
		{ // for each class
			if(Classes.get(i).ExamPeriod != -1) continue;
			if(SkipHumanities)
			{
				if(Classes.get(i).id.startsWith("SL") ||
				   Classes.get(i).id.startsWith("JP")) continue;
			}
			for(int j=0;j<NumPeriods;++j)
			{ // for each period
				//check room constraint
				if(classSchedule[j].size()+Classes.get(i).professors.size()>NumRooms) { continue; }
				boolean IsValid = true;
				Section c = Classes.get(i);
				// check prof schedule
				for(int k=0;k<c.professors.size();++k)
				{
					Prof p = c.professors.get(k);
					// make sure doesn't have conflict
					for(int l=0;l<p.classes.size();++l)
					{
						if(j==p.classes.get(l).ExamPeriod) { IsValid = false; break; }
					}
				}
				// check students' schedule
				for(int k=0;k<c.students.size();++k)
				{
					Student s = c.students.get(k);
					int[] periodsWithExams = new int[NumPeriods];
					periodsWithExams[j] = 1;
					// make sure doesn't have conflict
					for(int l=0;l<s.classes.size();++l)
					{
						if(j==s.classes.get(l).ExamPeriod) { IsValid = false; break; }
						// setup for the 3-in-a-row check
						if(s.classes.get(l).ExamPeriod != -1) periodsWithExams[s.classes.get(l).ExamPeriod] = 1;
					}
					// avoid 3-in-a-row checks
					if(CheckThrees) for(int l=0;l<NumPeriods-2;++l)
					{
						if(periodsWithExams[l]==1 && periodsWithExams[l+1]==1 && periodsWithExams[l+2]==1 && l%3==0) // only all-day 3-in-a-rows counted
						{ IsValid = false; break; }
					}
					if(IsValid==false) break;
				}
				if(!IsValid) continue;
				Classes.get(i).ExamPeriod = j;
				for(int k=0;k<Classes.get(i).professors.size();++k)
				{
					classSchedule[j].add(Classes.get(i));
				}
				break;
			}
		}
	}
	public ArrayList<Section> GetOrphans()
	{
		ArrayList<Section> orphans = new ArrayList<Section>();
		for(int i=0;i<Classes.size();++i)
		{
			if(Classes.get(i).ExamPeriod==-1)
			{
				orphans.add(Classes.get(i));
			}
		}
		ArrayList<Section> returns = new ArrayList<Section>();
		ArrayList<Section> candidates = new ArrayList<Section>();
		for(int i=0;i<orphans.size();++i)
		{
			//if(orphans.get(i).id.equals("SL151") || orphans.get(i).id.equals("GL366")) continue;
			candidates.clear();
			candidates.add(orphans.get(i));
			for(int j=i+1;j<orphans.size();++j)
			{
				boolean usable = true;
				for(int k=0;k<candidates.size();++k)
				{
					if(candidates.get(k)!=orphans.get(j) && intersection(orphans.get(j).students, candidates.get(k).students).size()!=0) usable = false;
				}
				
				if(orphans.get(i) == orphans.get(j))
				{
					candidates.add(orphans.get(j));
				}
				else
				{
					
					if(usable) candidates.add(orphans.get(j));
				}
			}
			if(candidates.size() > returns.size()) 
			{
				returns = (ArrayList<Section>)candidates.clone();
			}
		}
		System.out.println();
		return returns;
	}
	public void PostProcessor(ArrayList<Section>[] schedule, int NumRooms, int NumPeriods)
	{
		ArrayList<Section> orphans = GetOrphans();
		for(int p = 0;p<NumPeriods;++p)
		{
			if(orphans.size()==0) break;
			ArrayList<Section> ClassesToSwitch = new ArrayList<Section>();
			for(int i=0;i<orphans.size();++i)
			{
				for(int j=0;j<schedule[p].size();++j)
				{
					ArrayList<Student> intersect = intersection(orphans.get(i).students, schedule[p].get(j).students);
					if(intersect.size() != 0)
					{
						if(!ClassesToSwitch.contains(schedule[p].get(j)))
							for(int k=0;k<schedule[p].get(j).professors.size();++k) 
							{ ClassesToSwitch.add(schedule[p].get(j)); }
					}
				}
			}
			if(ClassesToSwitch.size()<=orphans.size() && NumRooms >= schedule[p].size()-ClassesToSwitch.size()+orphans.size())
			{
				System.out.println("Swapping "+ClassesToSwitch.size()+" classes with "+orphans.size()+" classes in period "+(p+1));
				for(int i=0;i<ClassesToSwitch.size();++i)
				{
					System.out.println("\tRemoving "+ClassesToSwitch.get(i).id+" ("+i+")");
					while(schedule[p].remove(ClassesToSwitch.get(i))) {};
					ClassesToSwitch.get(i).ExamPeriod = -1;
				}
				for(int i=0;i<orphans.size();++i)
				{
					System.out.println("\tAdding "+orphans.get(i).id+" ("+i+")");
					schedule[p].add(orphans.get(i));
					orphans.get(i).ExamPeriod = p;
				}
				orphans = GetOrphans();
				p = 0;
			}
			
		}
	}
	private void DisplaySchedule(ArrayList<Section>[] schedule)
	{
		for(int i=0;i<schedule.length;++i)
		{
			System.out.print("Timeslot "+(i+1)+"\t<"+(schedule[i].size())+" classes>: ");
			for(int j=0;j<schedule[i].size();++j)
			{
				System.out.print(schedule[i].get(j).id+" ");
			}
			System.out.println("");
		}
		for(int i=0;i<Classes.size();++i)
		{
			if(i>0 && Classes.get(i)==Classes.get(i-1)) continue;
			if(Classes.get(i).ExamPeriod==-1)
			{
				System.out.print("Class "+Classes.get(i).id+" was not scheduled.\n\t");
				for(int j=0;j<Classes.get(i).students.size();++j)
				{
					Student s1 = Classes.get(i).students.get(j);
					System.out.print(s1.id+" ");
				}
				System.out.println();
				int p = 10;
				for(int j=0;j<schedule[p].size();++j)
				{
					if(j>0 && schedule[p].get(j)==schedule[p].get(j-1)) continue;
					ArrayList<Student> intersect = intersection(Classes.get(i).students, schedule[p].get(j).students);
					if(intersect.size() != 0)
					{
						System.out.print("\t"+schedule[p].get(j).id+": ");
						for(int k=0;k<intersect.size();++k)
						{
							System.out.print(intersect.get(k).id+" ");
						}
						System.out.println();
					}
				}
			}
		}
		for(int i=0;i<Students.size();++i)
		{
			for(int j=0;j<schedule.length;++j)
			{
				int count = 0;
				for(int k=0;k<schedule[j].size();++k)
				{
					if(k!=0 && schedule[j].get(k)==schedule[j].get(k-1)) continue;
					if(schedule[j].get(k).students.contains(Students.get(i))) count++;
				}
				if(count>1){
					Student s1 = Students.get(i);
					System.out.print("Student "+s1.id+" schedule conflict: Period "+(j+1)+"\n\t");
					for(int k = 0;k<s1.classes.size();++k)
					{
						System.out.print(s1.classes.get(k).id+" ");
					}
					System.out.println();
				}
			}
		}
	}
	static <E> ArrayList<E> intersection(List<E> A1, List<E> A2)
	{
		ArrayList<E> Rt = new ArrayList<E>();
		for(int i=0;i<A1.size();++i)
		{
			for(int j=0;j<A2.size();++j)
			{
				if(A1.get(i).equals(A2.get(j))) Rt.add(A1.get(i));
			}
		}
		return Rt;
		
	}
	static <E> ArrayList<E> union(List<E> A1, List<E> A2)
	{
		ArrayList<E> Rt = new ArrayList<E>();
		for(int i=0;i<A1.size();++i)
		{
			Rt.add(A1.get(i));
		}
		for(int j=0;j<A2.size();++j)
		{
			if(!Rt.contains((A2.get(j)))) Rt.add(A2.get(j));
		}
		return Rt;
		
	}
}
