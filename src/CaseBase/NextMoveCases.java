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
import de.dfki.mycbr.core.model.AttributeDesc;
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

public class NextMoveCases {
    //name of the project file
	private static Project myProject = null;
	
	//name of the case base that should be used an the central concept
	private static String caseBaseName = "NexttMoveCB";
	private static String conceptName = "NextMove";
	private static Concept myConcept = null;
	private static ICaseBase caseBase = null;
	private static String[] nextMoveArray = {"numberTown","numberCity","clay","corn","lumber",
			"stone","whool","buildPiece","ressourceOne","ressourceTwo","ressourceThree"};
	
	//variables for the import of the csv that contains the instances for the case base
	private static String csv = "nextMove.csv"; //fix note: prio to the fix the string was "NextMove.csv", this file doesnt exist
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
		
		for (int i = 0; i < nextMoveArray.length; i++) {
			if (i <= 6) {
				IntegerDesc nextMoveAttribute = new IntegerDesc(myConcept, nextMoveArray[i], 0, 99);
				IntegerFct nextMoveFunction = nextMoveAttribute.addIntegerFct(nextMoveArray[i], true);
				nextMoveFunction.setFunctionTypeL(NumberConfig.POLYNOMIAL_WITH);
				nextMoveFunction.setFunctionTypeR(NumberConfig.POLYNOMIAL_WITH);
				nextMoveFunction.setFunctionParameterL(49.0);
				nextMoveFunction.setFunctionParameterL(49.0);
			} else {
				SymbolDesc nextMoveAttribute = new SymbolDesc(myConcept, nextMoveArray[i], 
						new HashSet<String>(Arrays.asList("town","city","corn","clay","lumber",
								"stone","whool")));
				SymbolFct nextMoveFunction = nextMoveAttribute.addSymbolFct(nextMoveArray[i], true);
				nextMoveFunction.setSimilarity("town", "town", 1.0);
				nextMoveFunction.setSimilarity("city", "city", 1.0);
				nextMoveFunction.setSimilarity("corn", "corn", 1.0);
				nextMoveFunction.setSimilarity("clay", "clay", 1.0);
				nextMoveFunction.setSimilarity("lumber", "lumber", 1.0);
				nextMoveFunction.setSimilarity("stone", "stone", 1.0);
				nextMoveFunction.setSimilarity("whool", "whool", 1.0);
			}
			
			HashMap<String, AttributeDesc> descs = myConcept.getAllAttributeDescs();
			moveFct.setWeight(descs.get("numberTown"), 100);
			moveFct.setWeight(descs.get("numberCity"), 100);
			moveFct.setWeight(descs.get("clay"), 100);
			moveFct.setWeight(descs.get("corn"), 100);
			moveFct.setWeight(descs.get("lumber"), 100);
			moveFct.setWeight(descs.get("stone"), 100);
			moveFct.setWeight(descs.get("whool"), 100);
			moveFct.setWeight(descs.get("buildPiece"), 0);
			moveFct.setWeight(descs.get("ressourceOne"), 0);
			moveFct.setWeight(descs.get("ressourceTwo"), 0);
			moveFct.setWeight(descs.get("ressourceThree"), 0);
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
			Instance nextMoveCase = myConcept.addInstance("pos " + id++);
			String[] values = line.split(valueSeparator);
			
			for (int i = 0; i < values.length; i++) {
				nextMoveCase.addAttribute(list.get(i), list.get(i).getAttribute((values[i])));
				caseBase.addCase(nextMoveCase);
			}
		}
		
		System.out.println(id + " F�lle erstellt");
		
		br.close();
	}
	
	//save a new case
	public static void writeNewCases(String playerName, int[] pieces, int[] resources, 
			String doAMove) throws Exception {
		
		BufferedWriter bw = null;
		String[] nextMove = doAMove.split(";");
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					playerName + csv, true)));
			
			String eintragCase = null;

			bw.newLine();
			
			for (int i = 0; i < nextMoveArray.length; i++) {
				
				if (i == 0) {
					eintragCase = "" + pieces[i];
				} else if (i == 1) {
					eintragCase = "," + pieces[i];
				} else if (i > 1 && i < 7) {
					eintragCase = "," + resources[i-2];
				} else {
					eintragCase = "," + nextMove[i-7];
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
}
