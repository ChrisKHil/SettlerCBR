package CaseBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dfki.mycbr.core.casebase.Attribute;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;

public class FirstMoveCB {

	public static void main(String[] args) {

		try {
			String name = "R";

			String[] resource = { "stone", "stone", "stone" };
			int[] probability = { 10, 2, 4 };

			String doAMove = agentQuery(resource, probability, name);
			System.out.println("Ressourcen (R) an denen eine Siedlung gebaut werden soll: " + doAMove);
			
			FirstMoveCases.writeNewCases(name,resource,probability,doAMove);
			System.out.println("Case Base is created");

		} catch (Exception e) {
			System.err.println("Mistake!!!");
		}
	}

	public static String agentQuery(String[] resource, int[] probability, String name) {

		try {
			FirstMoveCases.init(name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String buildTownPiece = "";

		String[] attrResource = { "ressourceOne", "ressourceTwo", "ressourceThree" };
		String[] attrProbability = { "probabilityOne", "probabilityTwo", "probabilityThree" };

		String query = "";

		for (int i = 0; i < attrResource.length; i++) {
			query += attrResource[i] + ":" + resource[i] + ",";
		}

		for (int i = 0; i < attrProbability.length; i++) {
			if (i == 2) {
				query += attrProbability[i] + ":" + probability[i];
			} else {
				query += attrProbability[i] + ":" + probability[i] + ",";
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
		List<Pair<Instance, Similarity>> cases = FirstMoveCases.findSimilarCases(request);

		String result;

		if (cases.isEmpty()) {
			result = "unknown,unknown,unknown,unknown,unknown,unknown";
		} else {
			Pair<Instance, Similarity> simResult = cases.get(0);
			Map<AttributeDesc, Attribute> values = simResult.getFirst().getAttributes();

			if (simResult.getSecond().getValue() > 0.3) {

				String ressourceOne = "";
				String ressourceTwo = "";
				String ressourceThree = "";

				for (AttributeDesc attrDesc : values.keySet()) {
					if (attrDesc.getName().equals("answerResOne")) {
						ressourceOne = values.get(attrDesc).getValueAsString();
						continue;
					}

					if (attrDesc.getName().equals("answerResTwo")) {
						ressourceTwo = values.get(attrDesc).getValueAsString();
						continue;
					}

					if (attrDesc.getName().equals("answerResThree")) {
						ressourceThree = values.get(attrDesc).getValueAsString();
						continue;
					}
				}

				result = " (Case:" + cases.get(0).getFirst().getName() + "; Similarity "
						+ Math.round(simResult.getSecond().getValue() * 1000) / 1000.0 + ")";
				System.out.println(result);
				buildTownPiece = ressourceOne + ";" + ressourceTwo + ";" + ressourceThree;

			} else {
				buildTownPiece = "No case with a similarity of at least 0.7 could be found!";
			}
		}

		return buildTownPiece;
	}
}
