package edu.cmu.lti.deiis.hw5.annotators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.qalab.types.NounPhrase;
import edu.cmu.lti.qalab.types.Sentence;
import edu.cmu.lti.qalab.types.TestDocument;
import edu.cmu.lti.qalab.types.Token;
import edu.cmu.lti.qalab.utils.Utils;

public class PhraseAnnotator extends JCasAnnotator_ImplBase{

	
	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		
		TestDocument testDoc=Utils.getTestDocumentFromCAS(aJCas);
				
		ArrayList<Sentence>sentenceList=Utils.getSentenceListFromTestDocCAS(aJCas);
		
		for(int i=0;i<sentenceList.size();i++){
			
			Sentence sent=sentenceList.get(i);
			ArrayList<Token>tokenList= Utils.getTokenListFromSentenceList(sent);
			//ArrayList<NounPhrase>phraseList=extractNounPhrases(tokenList,aJCas);
			
			ArrayList<NounPhrase>nounPhraseList=new ArrayList<NounPhrase>();
	    String nounPhrase="";
	    ArrayList<Token> Ptoks = new ArrayList<Token>();
	    for(int t=0;t<tokenList.size();t++){
	      Token token=tokenList.get(t);
	      String word=token.getText();
	      String pos=token.getPos();
	      if(pos.startsWith("NN") || pos.startsWith("JJ") || pos.startsWith("CD")){
	        nounPhrase+=word+" ";
	        Ptoks.add(token);
//	        System.out.println("token stem: "+token.getStem());
	      }else{
	        nounPhrase=nounPhrase.trim();
	        if(!nounPhrase.equals("")){
	          NounPhrase nn=new NounPhrase(aJCas);
	          nn.setText(nounPhrase);
	          if(Ptoks.size() != 0){
	            FSList fsPTokenList = this.createTokenList(aJCas, Ptoks);
	          fsPTokenList.addToIndexes();
	          nn.setTokens(fsPTokenList);
	          nn.addToIndexes();
//	          ArrayList<Token> Pt =Utils.getTokenListFromNounPhrase(nn); 
//	          System.out.println("Pt stem size:"+Pt.size());
	          }
	          nounPhraseList.add(nn);
	          //System.out.println("Noun Phrase: "+nounPhrase);
	          //System.out.println("Noun Phrase "+nn.getText()+" stem size:"+Ptoks.size());
	          
	          nounPhrase="";
	          Ptoks.clear();
	        }
	      }
	      
	      
	    }
	    nounPhrase=nounPhrase.trim();
	    if(!nounPhrase.equals("")){
	      NounPhrase nn=new NounPhrase(aJCas);
	      nn.setText(nounPhrase);
	      if(Ptoks.size() != 0){
	      FSList fsPTokenList =this.createTokenList(aJCas, Ptoks);
	      fsPTokenList.addToIndexes();
	      nn.setTokens(fsPTokenList);
	      nn.addToIndexes();
	      }
	      //System.out.println("Noun Phrase "+nn.getText()+" stem size:"+Ptoks.size());
	      nounPhraseList.add(nn);
	    }
	    
			FSList fsPhraseList=Utils.createNounPhraseList(aJCas, nounPhraseList);
			fsPhraseList.addToIndexes(aJCas);
			sent.setPhraseList(fsPhraseList);
			sent.addToIndexes();
			sentenceList.set(i, sent);
		}
		
		FSList fsSentList=Utils.createSentenceList(aJCas, sentenceList);
		testDoc.setSentenceList(fsSentList);
		
	}
	
	public ArrayList<NounPhrase> extractNounPhrases(ArrayList<Token> tokenList,JCas jCas){
		
		ArrayList<NounPhrase>nounPhraseList=new ArrayList<NounPhrase>();
		String nounPhrase="";
		ArrayList<Token> Ptoks = new ArrayList<Token>();
		for(int i=0;i<tokenList.size();i++){
			Token token=tokenList.get(i);
			String word=token.getText();
			String pos=token.getPos();
			if(pos.startsWith("NN") || pos.startsWith("JJ") || pos.startsWith("CD")){
				nounPhrase+=word+" ";
				Ptoks.add(token);
			}else{
				nounPhrase=nounPhrase.trim();
				if(!nounPhrase.equals("")){
					NounPhrase nn=new NounPhrase(jCas);
					nn.setText(nounPhrase);
					if(Ptoks.size() != 0){
					  FSList fsPTokenList = this.createTokenList(jCas, Ptoks);
					fsPTokenList.addToIndexes();
					nn.setTokens(fsPTokenList);
					}
					nounPhraseList.add(nn);
					//System.out.println("Noun Phrase: "+nounPhrase);
					//System.out.println("Noun Phrase "+nn.getText()+" stem size:"+Ptoks.size());
					
					nounPhrase="";
					Ptoks.clear();
				}
			}
			
			
		}
		nounPhrase=nounPhrase.trim();
		if(!nounPhrase.equals("")){
			NounPhrase nn=new NounPhrase(jCas);
			nn.setText(nounPhrase);
			if(Ptoks.size() != 0){
			FSList fsPTokenList = this.createTokenList(jCas, Ptoks);
			fsPTokenList.addToIndexes();
      nn.setTokens(fsPTokenList);
			}
			//System.out.println("Noun Phrase "+nn.getText()+" stem size:"+Ptoks.size());
			nounPhraseList.add(nn);
		}
		
		return nounPhraseList;
	}
	
	public FSList createTokenList(JCas aJCas, Collection<Token> aCollection) {
    if (aCollection.size() == 0) {
      return new EmptyFSList(aJCas);
    }

    NonEmptyFSList head = new NonEmptyFSList(aJCas);
    NonEmptyFSList list = head;
    Iterator<Token> i = aCollection.iterator();
    while (i.hasNext()) {
      head.setHead(i.next());
      if (i.hasNext()) {
        head.setTail(new NonEmptyFSList(aJCas));
        head = (NonEmptyFSList) head.getTail();
      } else {
        head.setTail(new EmptyFSList(aJCas));
      }
    }

    return list;
  }
}

