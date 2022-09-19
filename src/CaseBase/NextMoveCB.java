package CaseBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dfki.mycbr.core.casebase.Attribute;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;

public class NextMoveCB {
	public static void main(String[] args) {

		try {
			String name = "R";
			
			int[] pieces = {2,0};
			int[] resources = {1,3,2,2,0};

			String doAMove = agentQuery(name, pieces, resources);
			System.out.println("Agent R, baue: " + doAMove);
			
			NextMoveCases.writeNewCases(name,pieces,resources,doAMove);
			
			System.out.println("Case Base is created");
			
		} catch (Exception e) {
			System.err.println("Mistake!!!");
		}
	}
	
	public static String agentQuery(String name, int[] pieces, int[] resources) {
		
		try {
			NextMoveCases.init(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String build = "";
		
		String[] attrNumberPieces = {"numberTown","numberCity"};
		String[] attrNumberResources = {"clay","corn","lumber","stone","whool"};
		
		String query = ""; 
		
		for (int i = 0; i < attrNumberPieces.length; i++) {
			query += attrNumberPieces[i] + ":" + pieces[i] + ",";
		}
		
		for (int i = 0; i < attrNumberResources.length; i++) {
			if (i == 4) {
				query += attrNumberResources[i] + ":" + resources[i];
			} else {
				query += attrNumberResources[i] + ":" + resources[i] + ",";
			}				
		}
		System.out.println("New Query: " + query);

		// Get all attributes of the query
		String[] attrs = query.split(",");

		Map<String, String> request = new HashMap<>();

		for (String attr : attrs) {
			String[] value = attr.split(":");
			request.put(value[0], value[1]);
		}

		// Ähnliche Fälle abfragen
		List<Pair<Instance, Similarity>> cases = NextMoveCases.findSimilarCases(request);
		String result;

		if (cases.isEmpty()) {
			result = "unknown,unknown,unknown,unknown,unknown,unknown,unknown";
		} else {
			Pair<Instance, Similarity> simResult = cases.get(0);
			Map<AttributeDesc, Attribute> values = simResult.getFirst().getAttributes();

			if (simResult.getSecond().getValue() > 0.3) {

				String buildPiece = "";
				String ressourceOne = "";
				String ressourceTwo = "";
				String ressourceThree = "";

				for (AttributeDesc attrDesc : values.keySet()) {
					if (attrDesc.getName().equals("buildPiece")) {
						buildPiece = values.get(attrDesc).getValueAsString();
						continue;
					}
					if (attrDesc.getName().equals("ressourceOne")) {
						ressourceOne = values.get(attrDesc).getValueAsString();
						continue;
					}

					if (attrDesc.getName().equals("ressourceTwo")) {
						ressourceTwo = values.get(attrDesc).getValueAsString();
						continue;
					}

					if (attrDesc.getName().equals("ressourceThree")) {
						ressourceThree = values.get(attrDesc).getValueAsString();
						continue;
					}
				}

				result = " (Case:" + cases.get(0).getFirst().getName() + "; Similarity "
						+ Math.round(simResult.getSecond().getValue() * 1000) / 1000.0 + ")";
				System.out.println(result);
				build = buildPiece + ";" + ressourceOne + ";" + ressourceTwo + ";" + ressourceThree;

			} else {
				build = "No case with a similarity of at least 0.7 could be found!";
			}
		}

		return build;
	}
}
