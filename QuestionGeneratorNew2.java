import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuestionGeneratorNew2 {
	
	private static String getStudyFiles() {
		String studyFiles = "";
		//studyFiles += getDairyFiles();
		//studyFiles += getAngularFiles();
		//studyFiles += getTestFiles();
		//studyFiles += getJavaStreams();
		//studyFiles += getGit();
		//studyFiles += getSelenium();
		studyFiles += getInterviewBriefStudy();
		return studyFiles;
	}
	
	private static String getInterviewBriefStudy() {
		String myFiles="C:\\Users\\srini\\Desktop\\VASU\\personal\\job\\workspace\\MyStudy\\study\\InterviewBriefStudy.txt";
		return myFiles;
	}
	
	private static String getTestFiles() {
		String myFiles="C:\\Users\\srini\\Desktop\\VASU\\personal\\job\\workspace\\MyStudy\\study\\Test.txt";
		return myFiles;
	}
	
	private static String getSelenium() {
		String myFiles="C:\\Users\\srini\\Desktop\\VASU\\personal\\job\\workspace\\MyStudy\\study\\Selenium\\SeleniumNotes\\Selenium.txt";
		return myFiles;
	}

	private static String getGit() {
		String myFiles="C:\\Users\\srini\\Desktop\\VASU\\personal\\job\\workspace\\MyStudy\\study\\tools\\git.txt";
		return myFiles;
	}

	private static String getJavaStreams() {

		//String myFiles="C:\\Users\\srini\\Desktop\\VASU\\personal\\job\\workspace\\MyStudy\\study\\coreJava\\Java Certification\\Durgajava8QAs.txt";
		String myFiles="C:\\Users\\srini\\Desktop\\VASU\\personal\\job\\workspace\\MyStudy\\study\\coreJava\\Java Certification\\amigos\\java-stream-qa.txt";
		return myFiles;
	}

	private static String getDairyFiles() {
		
		return Stream.of(new File("C:\\Users\\srini\\Desktop\\VASU\\personal\\job\\workspace\\MyStudy\\study\\Dairy\\2023\\Oct").listFiles())
			      .filter(file -> !file.isDirectory())
			      .map(File::getAbsolutePath)
			      .collect(Collectors.joining(","));
		
		}
	
	private static String getAngularFiles() {
		
		String myFiles="";
		
		for(int i=1; i<=30; i++) {
			myFiles += "C:\\Users\\srini\\Desktop\\VASU\\personal\\job\\workspace\\MyStudy\\study\\FrontEnd\\Angular\\rsk\\Angular_Tomato\\TDay" +i+ ".txt,";
		}
		
		return myFiles;
	}

	private static Map<String, String> read(String myFiles) throws IOException {
		String[] splitFiles = myFiles.split(",");
		Map<String, String> myMap = new LinkedHashMap<String, String>();
		
		for(String myFile : splitFiles) {
			File f1 = new File(myFile);
			String[] words = null; // Intialize the word Array
			FileReader fr = new FileReader(f1); // Creation of File Reader object
			BufferedReader br = new BufferedReader(fr); // Creation of BufferedReader object
			String s;
			
			StringBuffer data = new StringBuffer();
			while ((s = br.readLine()) != null) // Reading Content from the file
			{
				words = s.split(" "); // Split the word using space
				for (String word : words) {
					data = data.append(word);
					data = data.append(" ");
				}
				data = data.append("\n");
			}
			
			for (String question : data.toString().split("Q\\)")) {
				if (question.length() > 0) {
					String[] myEntry = question.split("A\\)");
					//whenever there is an issue in generating questions
					//System.out.println(myEntry[0]);
					//System.out.println(myEntry[1]);
					myMap.put(myEntry[0], "A)" + myEntry[1]);
				}
			}
			fr.close();
		}
		return myMap;
	}
	
	private static void display(Map<String, String> myMap) {
		int quesCount = 0;
		Map<String, String> myWrongMap = new LinkedHashMap<String, String>();
		List<Map.Entry<String, String>> myList = new ArrayList<Map.Entry<String, String>>(myMap.entrySet());
		//Collections.shuffle(myList);
		for (Map.Entry<String, String> entry : myList) {
			quesCount += 1;
			new Scanner(System.in).nextLine();
			System.out.flush();
			String[] entryKeyWords = null;
			String[] entryValueWords = null;
			int entryKeyTempLength = 0;
			int entryValueTempLength = 0;
			StringBuffer curKeyEntry = new StringBuffer();
			StringBuffer curValueEntry = new StringBuffer();

			System.out.println("Total Questions : " + myMap.size());
			System.out.println("You are at question : " + quesCount);
			System.out.println("*************************************");
			if (entry.getKey().length() > 999) {
				entryKeyWords = entry.getKey().split(" "); // Split the word using space
				for (String entryKeyWord : entryKeyWords) {
					curKeyEntry = curKeyEntry.append(entryKeyWord);
					curKeyEntry = curKeyEntry.append(" ");
					entryKeyTempLength = entryKeyTempLength + entryKeyWord.length();
					if (entryKeyTempLength > 999) {
						entryKeyTempLength = 0;
						curKeyEntry = curKeyEntry.append("\n");
					}
				}
				curKeyEntry = curKeyEntry.append("\n");
				System.out.print(curKeyEntry);
			} else {
				System.out.println("Q)" + entry.getKey());
			}
			//
			String presssedKeyboardChar = new Scanner(System.in).nextLine();
			if(presssedKeyboardChar.startsWith("+")) {
				myWrongMap.put(entry.getKey(), entry.getValue());
			}
			//
			//new Scanner(System.in).nextLine();
			System.out.flush();
			if (entry.getValue().length() > 999) {
				entryValueWords = entry.getValue().split(" "); // Split the word using space
				for (String entryValueWord : entryValueWords) {
					curValueEntry = curValueEntry.append(entryValueWord);
					curValueEntry = curValueEntry.append(" ");
					entryValueTempLength = entryValueTempLength + entryValueWord.length();
					if (entryValueTempLength > 999) {
						entryValueTempLength = 0;
						curValueEntry = curValueEntry.append("\n");
					}
				}
				curValueEntry = curValueEntry.append("\n");
				System.out.print(curValueEntry);
			} else {
				System.out.println(entry.getValue());
			}
			System.out.println("*************************************");
		}
		if(!myWrongMap.isEmpty()) {
			display(myWrongMap);
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		String myFiles = getStudyFiles();
		
		Map<String, String> myMap = read(myFiles);
		display(myMap);
	}		
}
