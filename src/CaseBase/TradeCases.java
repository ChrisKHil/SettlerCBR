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
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.IntegerDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.similarity.AmalgamationFct;
import de.dfki.mycbr.core.similarity.IntegerFct;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.core.similarity.config.AmalgamationConfig;
import de.dfki.mycbr.core.similarity.config.NumberConfig;
import de.dfki.mycbr.util.Pair;
import de.dfki.mycbr.core.model.AttributeDesc;

public class TradeCases {
	
    //name of the project file
	private static Project myProject = null;
	
	//name of the case base that should be used an the central concept
	private static String caseBaseName = "TradeCB";
	private static String conceptName = "Trade";
	private static Concept myConcept = null;
	private static ICaseBase caseBase = null;
	private static String[] tradeArray = {"clayG","cornG","lumberG","stoneG","whoolG",
			"clayB", "cornB", "lumberB","stoneB","whoolB","solution"};
	
	//variables for the import of the csv that contains the instances for the case base
	private static String csv = "trade.csv";
	private static String valueSeparator = ",";
	
	//initialize case base
	public static void init(String name) throws Exception {
		//Name is only the starting letter of the agent color.		
		//create new project
		myProject = new Project();
		//create concept
		myConcept = myProject.createTopConcept(conceptName);
		
		//create case base
		caseBase = myProject.createDefaultCB(caseBaseName);
		descriptionCase();
		//writeNewCases(name);
		readAllCases(name);
		System.out.println(myProject.getInstance("pos 0").getValueAsString());
	}
	
	//initialize descriptions for all cases in the case base
	private static void descriptionCase() throws Exception {
		
		AmalgamationFct bookFct = myConcept.addAmalgamationFct(AmalgamationConfig.WEIGHTED_SUM, "myFunkt", true);
		
		for (int i = 0; i < tradeArray.length; i++) {
			IntegerDesc tradeAttribute = new IntegerDesc(myConcept, tradeArray[i], 0, 99);
			IntegerFct tradeFunction = tradeAttribute.addIntegerFct(tradeArray[i], true);
			tradeFunction.setFunctionTypeL(NumberConfig.POLYNOMIAL_WITH);
			tradeFunction.setFunctionTypeR(NumberConfig.POLYNOMIAL_WITH);
			tradeFunction.setFunctionParameterL(49.0);
			tradeFunction.setFunctionParameterL(1.0);
		}	

		HashMap<String, AttributeDesc> descs = myConcept.getAllAttributeDescs();
			bookFct.setWeight(descs.get("clayG"), 100);
			bookFct.setWeight(descs.get("cornG"), 100);
			bookFct.setWeight(descs.get("lumberG"), 100);
			bookFct.setWeight(descs.get("stoneG"), 100);
			bookFct.setWeight(descs.get("whoolG"), 100);
			bookFct.setWeight(descs.get("clayB"), 100);
			bookFct.setWeight(descs.get("cornB"), 100);
			bookFct.setWeight(descs.get("lumberB"), 100);
			bookFct.setWeight(descs.get("stoneB"), 100);
			bookFct.setWeight(descs.get("whoolB"), 100);
			bookFct.setWeight(descs.get("solution"), 0);
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
			Instance tradeCase = myConcept.addInstance("pos " + id++);
			String[] values = line.split(valueSeparator);
			
			for (int i = 0; i < values.length; i++) {
				tradeCase.addAttribute(list.get(i), list.get(i).getAttribute((values[i])));
				caseBase.addCase(tradeCase);
			}
		}
		
		System.out.println(id + " Fälle erstellt");
		
		br.close();
	}
	
	// save a new case
	public static void writeNewCases(String playerName, int[] offer, int[] need, boolean move) throws Exception {

		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(playerName + csv, true)));

			String eintragCase = "";
			bw.newLine();

			for (int i = 0; i < tradeArray.length; i++) {

				if (i == 0) {
					eintragCase = "" + offer[i];
					System.out.println("i == 0: " + eintragCase);
				} else if (i > 0 && i < 5) {
					eintragCase = "," + offer[i];
					System.out.println("i > 0 && < 5: " + eintragCase);
				} else if (i > 4 && i < 10) {
					eintragCase = "," + need[i - 5];
					System.out.println("i > 4 && < 10: " + eintragCase);
				} else {
					if (move != true) {
						eintragCase = "," + 0;
					} else {
						eintragCase = "," + 1;
					}
					System.out.println("i == 10: " + eintragCase);
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
			AttributeDesc attr = myConcept.getAllAttributeDescs().get(name);
			try {
				query.addAttribute(attr, attr.getAttribute(input.get(name)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
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