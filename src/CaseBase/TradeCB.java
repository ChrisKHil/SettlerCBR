package CaseBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;

public class TradeCB {

	public static void main(String[] args) {
		
		try {
			String name = "R";
			TradeCases.init(name);
			
			int[] offer = {1,1,1,0,0};
			int[] need = {0,0,0,0,1};
			
			boolean doAMove = agentQuery(offer, need, name);
			System.out.println("Soll ich handeln? " + doAMove);
			
			TradeCases.writeNewCases(name,offer,need,doAMove);
			
			System.out.println("Case Base is created");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	public static boolean agentQuery(int[] offer, int[] need, String name) {
		try {
			TradeCases.init(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean trade = false;
		
		String[] attrOffer = {"clayG","cornG","lumberG","stoneG","whoolG"};
		String[] attrNeed = {"clayB","cornB","lumberB","stoneB","whoolB"};
	
		String query = ""; 
		
		for (int i = 0; i < attrOffer.length; i++) {
			query += attrOffer[i] + ":" + offer[i] + ",";
		}
		
		for (int i = 0; i < attrNeed.length; i++) {
			if (i == 4) {
				query += attrNeed[i] + ":" + need[i];
			} else {
				query += attrNeed[i] + ":" + need[i] + ",";
			}				
		}
		
		System.out.println("New Query: " + query);
		
		String[] attrs = query.split(",");

		Map<String, String> request = new HashMap<>();

		for (String attr : attrs) {
			String[] value = attr.split(":");
			request.put(value[0], value[1]);
		}

		// �hnliche F�lle abfragen
		List<Pair<Instance, Similarity>> cases = TradeCases.findSimilarCases(request);
		
		String result;

		if (cases.isEmpty()) {
			result = "unknown,unknown,unknown,unknown,unknown";
		} else {
			Pair<Instance, Similarity> simResult = cases.get(0);
			
			if (simResult.getSecond().getValue() > 0.89) {
				
				result = " (Case:" + cases.get(0).getFirst().getName() + 
						"; Similarity " + Math.round(simResult.getSecond().getValue()*1000)/1000.0 + ") ";
				
				trade = true;
				
			} else {
				result = "No case with a similarity of at least 0.89 could be found!";
			} 
		}
		
		System.out.println(result);
		
		return trade;
	}
	
	public static boolean agentQueryNEW(int[] offer, int[] requestArr, int[] need, String name) {
		try {
			TradeCases.init(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		boolean trade = false;
		
		/*
		 * 
		 *  String[] attrOffer = {"clayO","cornO","lumberO","stoneO","whoolO"};
		 *	String[] attrNeed = {"clayN","cornN","lumberN","stoneN","whoolN"};
		 *	String[] attrRequest = {"clayR","cornR","lumberR","stoneR","whoolR"};
		 */
		String[] attrOffer = {"clayG","cornG","lumberG","stoneG","whoolG"};
		String[] attrNeed = {"clayB","cornB","lumberB","stoneB","whoolB"};
	
		String query = ""; 
		
		for (int i = 0; i < attrOffer.length; i++) {
			query += attrOffer[i] + ":" + offer[i] + ",";
		}
		
		for (int i = 0; i < attrNeed.length; i++) {
			if (i == 4) {
				query += attrNeed[i] + ":" + need[i];
			} else {
				query += attrNeed[i] + ":" + need[i] + ",";
			}				
		}
		
		System.out.println("New Query: " + query);
		
		String[] attrs = query.split(",");

		Map<String, String> request = new HashMap<>();

		for (String attr : attrs) {
			String[] value = attr.split(":");
			request.put(value[0], value[1]);
		}

		// �hnliche F�lle abfragen
		List<Pair<Instance, Similarity>> cases = TradeCases.findSimilarCases(request);
		
		String result;

		if (cases.isEmpty()) {
			result = "unknown,unknown,unknown,unknown,unknown";
		} else {
			Pair<Instance, Similarity> simResult = cases.get(0);
			
			if (simResult.getSecond().getValue() > 0.89) {
				
				result = " (Case:" + cases.get(0).getFirst().getName() + 
						"; Similarity " + Math.round(simResult.getSecond().getValue()*1000)/1000.0 + ") ";
				
				trade = true;
				
			} else {
				result = "No case with a similarity of at least 0.89 could be found!";
			} 
		}
		
		System.out.println(result);
		
		return trade;
	}
}