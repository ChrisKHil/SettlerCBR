package CaseBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.similarity.AmalgamationFct;
import de.dfki.mycbr.core.similarity.IntegerFct;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.core.similarity.config.AmalgamationConfig;
import de.dfki.mycbr.core.similarity.config.NumberConfig;
import de.dfki.mycbr.util.Pair;

public class DiscardCardsThiefCases {
    //name of the project file
	private static Project myProject = null;
	
	//name of the case base that should be used an the central concept
	private static String caseBaseName = "DiscardCardsCB";
	private static String conceptName = "DiscardCards";
	private static Concept myConcept = null;
	private static ICaseBase caseBase = null;
	private static String[] discardCardsArray = {"clayH","cornH","lumberH","stoneH",
			"whoolH","clayN","cornN","lumberN","stoneN","whoolN","clayD","cornD",
			"lumberD","stoneD","whoolD"};
	
	//variables for the import of the csv that contains the instances for the case base
	private static String csv = "discardCardsThief.csv";
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
		
		AmalgamationFct discardFct = myConcept.addAmalgamationFct(AmalgamationConfig.WEIGHTED_SUM, 
				"discardFct", true);
		
		for (int i = 0; i < discardCardsArray.length; i++) {
			IntegerDesc discardCardAttribute = new IntegerDesc(myConcept, discardCardsArray[i], 0, 99);
			IntegerFct discardCardFunction = discardCardAttribute.addIntegerFct(discardCardsArray[i], true);
			discardCardFunction.setFunctionTypeL(NumberConfig.POLYNOMIAL_WITH);
			discardCardFunction.setFunctionTypeR(NumberConfig.POLYNOMIAL_WITH);
			discardCardFunction.setFunctionParameterL(49.0);
			discardCardFunction.setFunctionParameterL(49.0);
			
			HashMap<String, AttributeDesc> descs = myConcept.getAllAttributeDescs();
			discardFct.setWeight(descs.get("clayH"), 100);
			discardFct.setWeight(descs.get("cornH"), 100);
			discardFct.setWeight(descs.get("lumberH"), 100);
			discardFct.setWeight(descs.get("stoneH"), 100);
			discardFct.setWeight(descs.get("whoolH"), 100);
			discardFct.setWeight(descs.get("clayN"), 100);
			discardFct.setWeight(descs.get("cornN"), 100);
			discardFct.setWeight(descs.get("lumberN"), 100);
			discardFct.setWeight(descs.get("stoneN"), 100);
			discardFct.setWeight(descs.get("whoolN"), 100);
			discardFct.setWeight(descs.get("clayD"), 0);
			discardFct.setWeight(descs.get("cornD"), 0);
			discardFct.setWeight(descs.get("lumberD"), 0);
			discardFct.setWeight(descs.get("stoneD"), 0);
			discardFct.setWeight(descs.get("whoolD"), 0);
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
			Instance discardCardsCase = myConcept.addInstance("pos " + id++);
			String[] values = line.split(valueSeparator);
			
			for (int i = 0; i < values.length; i++) {
				discardCardsCase.addAttribute(list.get(i), list.get(i).getAttribute((values[i])));
				caseBase.addCase(discardCardsCase);
			}
		}
		
		System.out.println(id + " F?lle erstellt");
		
		br.close();
	}
	
	//save a new case
	public static void writeNewCases(String playerName, int[] haveCards, int[] needCards, 
			String doAMove) throws Exception {
		
		BufferedWriter bw = null;
		
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					playerName + csv, true)));
			
			String eintragCase = null;
			String[] discardCards = doAMove.split(";");

			bw.newLine();
			
			for (int i = 0; i < discardCardsArray.length; i++) {

				if (i == 0) {
					eintragCase = "" + haveCards[i];
				} else if (i > 0 && i < 5) {
					eintragCase = "," + haveCards[i];
				} else if (i > 4 && i < 10) {
					eintragCase = "," + needCards[i - 5];
				} else {
					eintragCase = "," + discardCards[i - 10];
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
