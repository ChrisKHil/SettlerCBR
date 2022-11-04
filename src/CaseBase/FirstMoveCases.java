package CaseBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.similarity.AmalgamationFct;
import de.dfki.mycbr.core.similarity.IntegerFct;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.core.similarity.SymbolFct;
import de.dfki.mycbr.core.similarity.config.AmalgamationConfig;
import de.dfki.mycbr.core.similarity.config.NumberConfig;
import de.dfki.mycbr.util.Pair;
import de.dfki.mycbr.core.model.AttributeDesc;

public class FirstMoveCases {
	
    //name of the project file
	private static Project myProject = null;
	
	//name of the case base that should be used an the central concept
	private static String caseBaseName = "FirstMoveCB";
	private static String conceptName = "FirstMove";
	private static Concept myConcept = null;
	private static ICaseBase caseBase = null;
	private static String[] firstMoveArray = {"ressourceOne","probabilityOne","ressourceTwo",
			"probabilityTwo","ressourceThree","probabilityThree","answerResOne","answerResTwo",
			"answerResThree"};
	
	//variables for the import of the csv that contains the instances for the case base
	private static String csv = "firstMove.csv";
	private static String valueSeparator = ",";
	
	//initialize case base
	public static void init(String name) throws Exception {
		//create new project
		myProject = new Project();
		//create concept
		myConcept = myProject.createTopConcept(conceptName);
		
		//create case base
		caseBase = myProject.createDefaultCB(caseBaseName);
		descriptionCase();
		readAllCases(name);
	}
	
	//initialize descriptions for all cases in the case base
	private static void descriptionCase() throws Exception {
		
		AmalgamationFct moveFct = myConcept.addAmalgamationFct(AmalgamationConfig.WEIGHTED_SUM, 
				"moveFct", true);
		
		for (int i = 0; i < firstMoveArray.length; i++) {
			if (i % 2 == 1 && i <= 5) {
				IntegerDesc firstMoveAttribute = new IntegerDesc(myConcept, firstMoveArray[i], 1, 12);
				IntegerFct firstMoveFunction = firstMoveAttribute.addIntegerFct(firstMoveArray[i], true);
				firstMoveFunction.setFunctionTypeL(NumberConfig.POLYNOMIAL_WITH);
				firstMoveFunction.setFunctionTypeR(NumberConfig.POLYNOMIAL_WITH);
				firstMoveFunction.setFunctionParameterL(6.0);
				firstMoveFunction.setFunctionParameterL(6.0);
			} else {
				SymbolDesc firstMoveAttribute = new SymbolDesc(myConcept, firstMoveArray[i], 
						new HashSet<String>(Arrays.asList("corn","clay","lumber","stone","whool", "unknown")));
				SymbolFct firstMoveFunction = firstMoveAttribute.addSymbolFct(firstMoveArray[i], true);
				firstMoveFunction.setSimilarity("corn", "corn", 1.0);
				firstMoveFunction.setSimilarity("clay", "clay", 1.0);
				firstMoveFunction.setSimilarity("lumber", "lumber", 1.0);
				firstMoveFunction.setSimilarity("stone", "stone", 1.0);
				firstMoveFunction.setSimilarity("whool", "whool", 1.0);
				firstMoveFunction.setSimilarity("unknown", "unknown", 1.0);
			} 
			
			HashMap<String, AttributeDesc> descs = myConcept.getAllAttributeDescs();
			moveFct.setWeight(descs.get("ressourceOne"), 100);
			moveFct.setWeight(descs.get("probabilityOne"), 100);
			moveFct.setWeight(descs.get("ressourceTwo"), 100);
			moveFct.setWeight(descs.get("probabilityTwo"), 100);
			moveFct.setWeight(descs.get("ressourceThree"), 100);
			moveFct.setWeight(descs.get("probabilityThree"), 100);
			moveFct.setWeight(descs.get("answerResOne"), 0);
			moveFct.setWeight(descs.get("answerResTwo"), 0);
			moveFct.setWeight(descs.get("answerResThree"), 0);
		}		
	}
	
	//read all cases
	private static void readAllCases(String playerName) throws Exception {

		int id = 0;
		BufferedReader br = new BufferedReader(new FileReader(playerName + csv));
		String line = br.readLine();
	
		List<AttributeDesc> list = new ArrayList<>();
	
		for (String name : line.split(valueSeparator)) {
			list.add(myConcept.getAttributeDesc(name));
		}
		
		while((line = br.readLine()) != null) {
			Instance firstMoveCase = myConcept.addInstance("pos " + id++);
			String[] values = line.split(valueSeparator);
			
			for (int i = 0; i < values.length; i++) {
				firstMoveCase.addAttribute(list.get(i), list.get(i).getAttribute((values[i])));
				caseBase.addCase(firstMoveCase);
			}
		}
		
		System.out.println(id + " F�lle erstellt");
		
		br.close();
	}
	
	//save a new case
	public static void writeNewCases(String name, String[] resource, int[] probability, 
			String doAMove) throws Exception {
		
		BufferedWriter bw = null;
		String[] firstMove = doAMove.split(";");
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					name + csv, true)));
			
			String eintragCase = null;

			bw.newLine();
			
			int j = 1;
			int k = 1;
			
			for (int i = 0; i < firstMoveArray.length; i++) {
				if (i == 0) {
					eintragCase = resource[i];
				} else {
					
					if (i % 2 == 1 && i < 6) { 
						eintragCase = "," + probability[i-k];
						k++;
					} else if (i % 2 == 0 && i < 6){
						eintragCase = "," + resource[i-j];						
						j++;
					} else {
						eintragCase = "," + firstMove[i-6];
					}
				}
				bw.write(eintragCase);
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//find one or more cases which are similar (Retrieval)
	public static List<Pair<Instance,Similarity>> findSimilarCases(Map<String, String> input) {
		
		// create Retrieval
		Retrieval retrieval = new Retrieval(myConcept, caseBase);
		
		// get all values of the query
		Instance query = retrieval.getQueryInstance();
		
		for(String name : input.keySet()) {
			AttributeDesc attr = myConcept.getAttributeDesc(name);
			try {
				query.addAttribute(attr, attr.getAttribute(input.get(name)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		// find a similar case
		retrieval.start();
		List<Pair<Instance,Similarity>> result = retrieval.getResult();
		Collections.sort(result, (i1,i2) -> -Double.compare(i1.getSecond().getValue(), i2.getSecond().getValue()));
 
		return result;
	}
	
public static List<Pair<Instance,Similarity>> findSimilarCasesNEW(Map<String, String> input) {
		
		// create Retrieval
		Retrieval retrieval = new Retrieval(myConcept, caseBase);
		
		// get all values of the query
		Instance query = retrieval.getQueryInstance();
		
		for(String name : input.keySet()) {
			AttributeDesc attr = myConcept.getAttributeDesc(name);
			try {
				query.addAttribute(attr, attr.getAttribute(input.get(name)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		// find a similar case
		retrieval.start();
		List<Pair<Instance,Similarity>> result = retrieval.getResult();
		Collections.sort(result, (i1,i2) -> -Double.compare(i1.getSecond().getValue(), i2.getSecond().getValue()));
 
		return result;
	}
}
