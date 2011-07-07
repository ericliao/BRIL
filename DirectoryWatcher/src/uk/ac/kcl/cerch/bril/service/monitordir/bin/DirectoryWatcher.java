package uk.ac.kcl.cerch.bril.service.monitordir.bin;

import java.io.Console;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import uk.ac.kcl.cerch.bril.service.monitordir.ExperimentCreator;

public class DirectoryWatcher {

	public static void main(String[] args){
		  String directory = null;
	      String project =null;
     
	      String experimentId =null;
	      String type ="Crystallography";
	      boolean existingDir=true;	 
	      boolean newExperiment =false;
		
			
		String existingDir_Q1=
			"\n \n"
			+"Directory location \n"
			+"-------------------- \n"
			+"Do you want to use the previously entered directory, if not null ("+directory+")? \n"
			+"Options: true, false \n"
			+"Enter a value [default is true] ==> ";
		
		String UseThisDir_Q2=
			"\n \n"
			+"Directory location \n"
			+"-------------------- \n"
			+"Do you want to use the previously entered directory ";
			
			String UseThisDir_Q21=	"Options: true, false \n"
			       +"Enter a value [default is true] ==> ";
		
		String question_path=
			"\n \n"
			+"New directory location, full directory location for example; \n" +
		    " /home/user/project1 OR c:/exp/project1 \n"
			+"----------------------------- \n"
			+"Enter the directory location to monitor ==> " ;
		String question_project=
			"\n \n"
			+"Project name\n"
			+"----------------------------- \n"
			+"Enter your project name [default is ] ==> ";
		
		String question_area=
			"\n \n"
			+"Project research area\n"
			+"----------------------------- \n"
			+"Options: Crystallography, Nanoimaging, NMR \n"
			+"Enter your experiment type [default is Crystallography] ==> ";
		
		Console c = System.console();
        if (c == null) {
            System.err.println("No console.");
            System.exit(1);
        }

       
		
	/*	String inputArgumentsHelp = "Input arguments (exactly 2): \n"
			+ "---------------------------- \n"
			+ "     - first argument : one of the following metadata extraction tool (currently supports samgi & kea): \"datafountain\", \"kea\", \"samgi\", or \"paperbase\"\n"
			+ "     - second argument: type of invocation of the specified metadata extraction tool: \"standalone\", \"ws\"\n"
			+ "     - third argument : the location of the file, url object (as specified by the first argument)\n";
*/

          
	    
	   //if existing directory present print the last used directory
	   //Ask if this what to use this?
	   // if no as for the new directory to be monitored
	
		 String result1 = c.readLine(existingDir_Q1);
		 if(result1!=null && result1.equalsIgnoreCase("false")){
			 existingDir=false;
		 }else if (result1==null || result1==""){
			 existingDir=true;
		 }
		 System.out.println("Using existing experiment?, " + existingDir);
		 
		 if(existingDir==false){
			 newExperiment=true;
			
			 String result2 = c.readLine(question_path);
			 if(result2!=null||result2!=""){
			 boolean r = checkEnteredDirectory(result2);
			 if (r==true){
			 directory = result2;
			 }else{
				   System.out.println("Invalid directory location!");
				   System.exit(1);
			 }
			 }
			 
			 String result3 = c.readLine(question_project);
			 if(result3!=null || result3!=""){
				 project = result3;
			 }
			 
			 String result4 = c.readLine(question_area);
			 if(result4==null || result4==""){
				 type ="Crystallography";
			 }
			 if(result4!=null || result4!=""){
				 type = result4;
			 }else{
				 type ="Crystallography";
			 }
			 
		 }
		 
		if(existingDir==true){
			    ExperimentCreator helper = new ExperimentCreator();
				boolean hasActiveExpt= false;		
				hasActiveExpt= helper.hasAnActiveExperiment();
				
				System.out.println("Has active experiment, " + hasActiveExpt);
				if(hasActiveExpt==true){
					helper.loadActiveExperiment();
					String lastDir = helper.getDirectoryPath();
					String useThisPath = c.readLine(UseThisDir_Q2+"["+lastDir+"]? \n"+UseThisDir_Q21);
					if(useThisPath!=null && useThisPath.equalsIgnoreCase("false")){
						newExperiment=true;
					}else {
						experimentId = helper.getExperimentId();
						directory = helper.getDirectoryPath();
						type = helper.getExperimentType();
						project = helper.getProjectName();	
					}
					
				}else{
					//no experiment in the config file.
					 newExperiment=true;
					 //ask if to create new experiment or load old one from the list?
					 String result2 = c.readLine(question_path);
					 boolean r = checkEnteredDirectory(result2);
					 if (r==true){
					 directory = result2;
					 }else{
						   System.out.println("Invalid directory location!");
						   System.exit(1);
					 }
					 String result3 = c.readLine(question_project);
					 if(result3!=null||result3!=""){
						 project = result3;
					 }
					 
					 String result4 = c.readLine(question_area);
					 if(result4==null || result4==""){
						 type ="Crystallography";
					 }
					 if(result4!=null || result4!=""){
						 type = result4;
					 }
				}
				
				
		 }
		 
		 System.out.println("The new directory to monitor, " + directory);
		 System.out.println("The project name, " + project);
		 System.out.println("The project type, " + type);
		 
		
		 //create new experiment
		 if(newExperiment==true){			
			 try {
				 ExperimentCreator helper = new ExperimentCreator();
				helper.createNewExperiment(project, type, directory);
				experimentId = helper.getExperimentId();
				System.out.println("New experiment created, using id: " + experimentId);
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 
	      //  open up standard input
	/*      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String input=null;
			System.out.println("Do you want to use the existing directory'"+directory +"' [default is true]: ");
			 try {
					
				 input = br.readLine();
				 if(input!=null && input.equalsIgnoreCase("false")){
					 existingDir=false;
				 }else if (input==null){
					 existingDir=true;
				 }
		      } catch (IOException ioe) {
		         System.out.println("IO error trying to read the directory location!");
		         System.exit(1);
		      }
		
		 System.out.println("Using existing experiment, " + existingDir);
	      //  prompt the user to enter their name
	     System.out.print("Enter the directory location to monitor: ");

	      //  read the directory location from the command-line; need to use try/catch with the
	      //  readLine() method
	     try {
	    	  directory = br.readLine();
	      } catch (IOException ioe) {
	         System.out.println("IO error trying to read the directory location!");
	         System.exit(1);
	      }
	      System.out.println("Directory location, " + directory);

	      System.out.print("Enter your project name: ");

	      try {
	    	  project = br.readLine();
	      } catch (IOException ioe) {
	         System.out.println("IO error trying to read the directory location!");
	         System.exit(1);
	      }
	      
	      System.out.println("Project name, " + project);
	   
	  */
	/*	String directory="";
		String re = "-r";
		boolean recursive = false;
		if (!re.equals(null)){
			recursive = true;
		}
		Path dir = Paths.get(directory);
		try {
			DirectoryMonitor monitor = new DirectoryMonitor(dir, recursive);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
 private static boolean checkEnteredDirectory(String directory){
	 boolean result = false;
	if(directory!=null){
		if(directory.contains("\\") || directory.contains("/") || directory.contains("\\\\") ){
			result = true;
		}
	}
	return result;
}


}
