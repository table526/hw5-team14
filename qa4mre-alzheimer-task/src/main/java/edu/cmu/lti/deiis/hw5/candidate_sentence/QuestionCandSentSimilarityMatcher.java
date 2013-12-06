package edu.cmu.lti.deiis.hw5.candidate_sentence;

//import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.core.provider.solr.SolrWrapper;
import edu.cmu.lti.qalab.types.Answer;
import edu.cmu.lti.qalab.types.CandidateSentence;
import edu.cmu.lti.qalab.types.NER;
import edu.cmu.lti.qalab.types.NounPhrase;
import edu.cmu.lti.qalab.types.Question;
import edu.cmu.lti.qalab.types.QuestionAnswerSet;
import edu.cmu.lti.qalab.types.Sentence;
import edu.cmu.lti.qalab.types.TestDocument;
import edu.cmu.lti.qalab.utils.Utils;
//import edu.umass.cs.mallet.base.util.Arrays;

public class QuestionCandSentSimilarityMatcher  extends JCasAnnotator_ImplBase{

	SolrWrapper solrWrapper=null;
	String serverUrl;
	//IndexSchema indexSchema;
	String coreName;
	String schemaName;
	int TOP_SEARCH_RESULTS=10;
  public static String[] correctAnsString1 ={"immunofluorescence experiments","ER Golgi apparatus","CLU2","rs11136000T","rs11136000","CLU2","androgen","activation","valproate","449"};
  public static String[] correctAnsString2 ={"somatostatin","somatostatin","BV-2","RealTime PCR","somatostatin","microglia","extracellular","octreotide","SSTR-1 SSTR-2 SSTR-4","siRNA"};
  public static String[] correctAnsString3 ={"astrocytes","choroid plexus","more than 10 million","gelsolin","age","gelsolin","amyloid-beta","APP Ps mice","synaptic terminals","before amyloid-beta accumulation"};
  public static String[] correctAnsString4 ={"APP-CTF accumulation","longer","PSEN1","affinity chromatography","AICD","aspartate","EM","Semagacestat","P436Q","185"};
	public static String[] stopwordsStrings = {"and", "the","before","they"};
  @Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		serverUrl = (String) context.getConfigParameterValue("SOLR_SERVER_URL");
		coreName = (String) context.getConfigParameterValue("SOLR_CORE");
		schemaName = (String) context.getConfigParameterValue("SCHEMA_NAME");
		TOP_SEARCH_RESULTS = (Integer) context.getConfigParameterValue("TOP_SEARCH_RESULTS");
		try {
			this.solrWrapper = new SolrWrapper(serverUrl+coreName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		TestDocument testDoc=Utils.getTestDocumentFromCAS(aJCas);
		String testDocId=testDoc.getId();
		String testDocStr = testDoc.getText();
		System.out.println(testDocId);
		ArrayList<Sentence>sentenceList=Utils.getSentenceListFromTestDocCAS(aJCas);
		ArrayList<QuestionAnswerSet>qaSet=Utils.getQuestionAnswerSetFromTestDocCAS(aJCas);
	//Store correctness of Candidate Sentences for a Document
    double totallcorrect0 = 0;
    /**build hashset for stop words**/
    HashSet<String> stopW = new HashSet<String>(Arrays.asList(stopwordsStrings));
		for(int i=0;i<qaSet.size();i++){
			/** Get Answer Labeled Correct**/
		  ArrayList<Answer> choiceList = Utils.fromFSListToCollection(qaSet.get(i).getAnswerList(),
              Answer.class);
		  String correct = "";
      for (int j = 0; j < choiceList.size(); j++) {
        Answer answer = choiceList.get(j);
        if (answer.getIsCorrect()) {
          correct = answer.getText();
          break;
        }
      }
			Question question=qaSet.get(i).getQuestion();
			System.out.println("========================================================");
			System.out.println("Question: "+question.getText());
			System.out.println("Correct Answer: "+correct);
			String searchQuery=this.formSolrQuery(question);
			if(searchQuery.trim().equals("")){
				continue;
			}
			//Store number of correct Candidate Sentences for a Question
			double totallcorrect = 0;
			ArrayList<CandidateSentence>candidateSentList=new ArrayList<CandidateSentence>();
			SolrQuery solrQuery=new SolrQuery();
			solrQuery.add("fq", "docid:"+testDocId);
			solrQuery.add("q",searchQuery);
			solrQuery.add("rows",String.valueOf(TOP_SEARCH_RESULTS));
			solrQuery.setFields("*", "score");
			try {
				SolrDocumentList results=solrWrapper.runQuery(solrQuery, TOP_SEARCH_RESULTS);
				for(int j=0;j<results.size();j++){
					SolrDocument doc=results.get(j);					
					String sentId=doc.get("id").toString();
					String docId=doc.get("docid").toString();
					if(!testDocId.equals(docId)){
						continue;
					}
					String sentIdx=sentId.replace(docId,"").replace("_", "").trim();
					int idx=Integer.parseInt(sentIdx);
					Sentence annSentence=sentenceList.get(idx);
					
					String sentence=doc.get("text").toString();
					if(isQuesSent(sentence)){
					  sentence = getNextSent(sentence,testDocStr);
					}
					//sentence = getNextSent(sentence,testDocStr);
					/** Error Analysis for Sentence Selection**/
					String[] ansTok = correct.split(" ");
					boolean label = false;
					String tag = "F";
					//need noise filtering
					for(int b = 0; b < ansTok.length;b++){
					  String target = ansTok[b].replaceAll("([a-z]+)[?:!.,;]*", "$1");
					  if(!target.toUpperCase().equals(target)) target = target.toLowerCase();
					  if(stopW.contains(target) == false){
//					  if(sentence.toLowerCase().contains(target))
//					    label = true;
//					  }
					    String[] senTok = sentence.split(" ");
					    for(int g = 0; g < senTok.length;g++){
					      String filter = senTok[g].replaceAll("([a-z]+)[?:!.,;]*", "$1");
					      if(!filter.toUpperCase().equals(filter)) filter = filter.toLowerCase();
					      if(filter.equals(target)) label = true;
					    }
					    }
					}
					if(label) {
					  tag = "T";
					  totallcorrect++;
					}
					double relScore=Double.parseDouble(doc.get("score").toString());
					CandidateSentence candSent=new CandidateSentence(aJCas);
					candSent.setSentence(annSentence);
					candSent.setRelevanceScore(relScore);
					candidateSentList.add(candSent);
					System.out.println(tag+" "+relScore+"\t"+sentence);
				}
				FSList fsCandidateSentList=Utils.fromCollectionToFSList(aJCas, candidateSentList);
				fsCandidateSentList.addToIndexes();
				qaSet.get(i).setCandidateSentenceList(fsCandidateSentList);
				qaSet.get(i).addToIndexes();
				double rate = totallcorrect/TOP_SEARCH_RESULTS;
				totallcorrect0 += rate;
			  System.out.println("Sentence ErrorAnalysis for Q"+i+": "+rate);
				
			} catch (SolrServerException e) {
				e.printStackTrace();
			}
			FSList fsQASet=Utils.fromCollectionToFSList(aJCas, qaSet);
			testDoc.setQaList(fsQASet);
			
			System.out.println("=========================================================");
		}
	  
		System.out.println("Sentence ErrorAnalysis for Doc"+testDocId+": "+totallcorrect0/qaSet.size());
	}

	public String formSolrQuery(Question question){
		String solrQuery="";
		
		ArrayList<NounPhrase>nounPhrases=Utils.fromFSListToCollection(question.getNounList(), NounPhrase.class);
		
		for(int i=0;i<nounPhrases.size();i++){
			solrQuery+="nounphrases:\""+nounPhrases.get(i).getText()+"\" ";			
		}
		
		ArrayList<NER>neList=Utils.fromFSListToCollection(question.getNerList(), NER.class);
		for(int i=0;i<neList.size();i++){
			solrQuery+="namedentities:\""+neList.get(i).getText()+"\" ";
		}
		solrQuery=solrQuery.trim();
		
		return solrQuery;
	}
	
	public boolean isQuesSent(String sentence){
	  boolean flag = false;
	  if(sentence.toLowerCase().contains("what")||sentence.toLowerCase().contains("why")||
	          sentence.toLowerCase().contains("how")||sentence.toLowerCase().contains("?")){
	    flag = true;
	  }
	  return flag;
	}
	
	public String getNextSent(String sentence,String doc){
	  int startIndex = doc.indexOf(sentence)+sentence.length();
	  if(doc.substring(startIndex).contains(".")){
	    int endIndex = doc.indexOf(".", startIndex);
	    return sentence+doc.substring(startIndex, endIndex);
	  }
	  else
	    return sentence;
	  
	  
	}

}
