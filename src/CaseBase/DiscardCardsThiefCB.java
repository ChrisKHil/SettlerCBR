package CaseBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dfki.mycbr.core.casebase.Attribute;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;

public class DiscardCardsThiefCB {
	
	public static void main(String[] args) {

		try {
			String name = "R";
			DiscardCardsThiefCases.init(name);
			
			int[] haveCards = {2,0,1,3,2};
			int[] needCards = {2,0,0,1,1};
			
			String doAMove = agentQuery(haveCards, needCards, name);
			System.out.println("Agent R, discard 2: " + doAMove);
			
			DiscardCardsThiefCases.writeNewCases(name,haveCards,needCards,doAMove);
			
			System.out.println("Case Base is created");
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public static String agentQuery(int[] haveCards, int[] needCards, String name) {
		
		try {
			DiscardCardsThiefCases.init(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String build = "";
		String query = "";
		
		String[] attrHaveCards = {"clayH","cornH","lumberH","stoneH","whoolH"};
		String[] attrNeedCards = {"clayN","cornN","lumberN","stoneN","whoolN"};
		
		for (int i = 0; i < attrHaveCards.length; i++) {
			query += attrHaveCards[i] + ":" + haveCards[i] + ",";
		}
		
		for (int i = 0; i < attrNeedCards.length; i++) {
			if (i == 4) {
				query += attrNeedCards[i] + ":" + needCards[i];
			} else {
				query += attrNeedCards[i] + ":" + needCards[i] + ",";
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
		List<Pair<Instance, Similarity>> cases = 
				DiscardCardsThiefCases.findSimilarCases(request);
		String result;

		if (cases.isEmpty()) {
			result = "unknown,unknown,unknown,unknown,unknown,unknown,unknown,"
					+ "unknown,unknown,unknown";
		} else {
			
			Pair<Instance, Similarity> simResult = cases.get(0);
			Map<AttributeDesc, Attribute> values = simResult.getFirst().getAttributes();
			
			if (simResult.getSecond().getValue() > 0.7) {

				String clayD = "";
				String cornD = "";
				String lumberD = "";
				String stoneD = "";
				String whoolD = "";

				for (AttributeDesc attrDesc : values.keySet()) {
					if (attrDesc.getName().equals("clayD")) {
						clayD = values.get(attrDesc).getValueAsString();
						continue;
					}
					if (attrDesc.getName().equals("cornD")) {
						cornD = values.get(attrDesc).getValueAsString();
						continue;
					}
					if (attrDesc.getName().equals("lumberD")) {
						lumberD = values.get(attrDesc).getValueAsString();
						continue;
					}

					if (attrDesc.getName().equals("stoneD")) {
						stoneD = values.get(attrDesc).getValueAsString();
						continue;
					}

					if (attrDesc.getName().equals("whoolD")) {
						whoolD = values.get(attrDesc).getValueAsString();
						continue;
					}
				}

				result = " (Case:" + cases.get(0).getFirst().getName() + "; Similarity "
						+ Math.round(simResult.getSecond().getValue() * 1000) / 1000.0 + ")";
				System.out.println(result);
				build = clayD + ";" + cornD + ";" + lumberD + ";" + stoneD + ";" + whoolD;

			} else {
				build = "No case with a similarity of at least 0.7 could be found!";
			}
		}

		return build;
	}
}
