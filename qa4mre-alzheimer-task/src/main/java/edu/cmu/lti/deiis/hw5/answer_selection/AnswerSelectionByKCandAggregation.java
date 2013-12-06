package edu.cmu.lti.deiis.hw5.answer_selection;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.qalab.types.Answer;
import edu.cmu.lti.qalab.types.CandidateAnswer;
import edu.cmu.lti.qalab.types.CandidateSentence;
import edu.cmu.lti.qalab.types.Question;
import edu.cmu.lti.qalab.types.QuestionAnswerSet;
import edu.cmu.lti.qalab.types.TestDocument;
import edu.cmu.lti.qalab.utils.Utils;

public class AnswerSelectionByKCandAggregation extends JCasAnnotator_ImplBase {

  int K_CANDIDATES = 5;
  double NoneThreshold = 2.1;

  @Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
    K_CANDIDATES = (Integer) context.getConfigParameterValue("K_CANDIDATES");
	}

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    TestDocument testDoc = Utils.getTestDocumentFromCAS(aJCas);
    ArrayList<QuestionAnswerSet> qaSet = Utils.fromFSListToCollection(testDoc.getQaList(),
            QuestionAnswerSet.class);
    int matched = 0;
    int total = 0;
    int unanswered = 0;
    for (int i = 0; i < qaSet.size(); i++) {

      Question question = qaSet.get(i).getQuestion();
      System.out.println("Question: " + question.getText());
      ArrayList<Answer> choiceList = Utils.fromFSListToCollection(qaSet.get(i).getAnswerList(),
              Answer.class);
      ArrayList<CandidateSentence> candSentList = Utils.fromFSListToCollection(qaSet.get(i)
              .getCandidateSentenceList(), CandidateSentence.class);

      int topK = Math.min(K_CANDIDATES, candSentList.size());
      System.out.println("ERROR: topK: " + topK + "\tSize" + candSentList.size() +"\tKcand: "+K_CANDIDATES);
      String correct = "";

      for (int j = 0; j < choiceList.size(); j++) {
        Answer answer = choiceList.get(j);
        if (answer.getIsCorrect()) {
          correct = answer.getText();
          break;
        }
      }

      HashMap<String, Double> hshAnswer = new HashMap<String, Double>();

      for (int c = 0; c < topK; c++) {

        CandidateSentence candSent = candSentList.get(c);

        ArrayList<CandidateAnswer> candAnswerList = Utils.fromFSListToCollection(
                candSent.getCandAnswerList(), CandidateAnswer.class);
        String selectedAnswer = "";
        double maxScore = Double.NEGATIVE_INFINITY;
        for (int j = 0; j < candAnswerList.size(); j++) {

          CandidateAnswer candAns = candAnswerList.get(j);
          String answer = candAns.getText();
          
          if(answer.contains("None of"))
          {
            hshAnswer.put(answer, 0.0);
            continue;
          }
          double totalScore = candAns.getSimilarityScore() + candAns.getSynonymScore()
                  + candAns.getPMIScore();
          System.out.println("Answer: " + candAns.getText() + "\tSim: " + candAns.getSimilarityScore() + "\tPMI: " + candAns.getPMIScore());
         // if (totalScore > maxScore) {
         //   maxScore = totalScore;
         //   selectedAnswer = answer;
         // }
          Double existingVal = 0.0;
          if(hshAnswer.containsKey(answer))
          {
            existingVal = hshAnswer.get(answer);
          }else {
            existingVal = 0.0;
          }
           hshAnswer.put(answer, totalScore + existingVal);
        }
        //System.out.println("MAX! "+ maxScore + "ANSWER: " + selectedAnswer);

        /*Double existingVal = hshAnswer.get(selectedAnswer);
        if (existingVal == null) {
          existingVal = new Double(0.0);
        }
        hshAnswer.put(selectedAnswer, existingVal + maxScore);*/
      }
       // Regularization
    /*  double sumScore = 0.0;
      Iterator<String> it = hshAnswer.keySet().iterator();
      while (it.hasNext()) {
        String key = it.next();
        Double val = hshAnswer.get(key);
        sumScore += val;
      }
      it = hshAnswer.keySet().iterator();
      while (it.hasNext()) {
        String key = it.next();
        Double val = hshAnswer.get(key);
        hshAnswer.put(key, val / sumScore);
      }
      */
      String bestChoice = null;
      try {
        bestChoice = findBestChoice(hshAnswer);

      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println("Correct Choice: " + "\t" + correct);
      System.out.println("Best Choice: " + "\t" + bestChoice);

      if (bestChoice == null) {
        unanswered++;
      }
      if (bestChoice != null && correct.equals(bestChoice)) {
        matched++;

      }
      total++;
      System.out.println("================================================");

    }

    System.out.println("Correct: " + matched + "/" + total + "=" + ((matched * 100.0) / total)
            + "%");
    // TO DO: Reader of this pipe line should read from xmi generated by
    // SimpleRunCPE
    double cAt1 = (((double) matched) / ((double) total) * unanswered + (double) matched)
            * (1.0 / total);
    System.out.println("c@1 score:" + cAt1);
   /* try
    {
      FileWriter writer = new FileWriter("panOut1", true);
      writer.write("Correct: " + matched + "/" + total + "=" + ((matched * 100.0) / total) + "%");
      writer.write("c@1 score:" + cAt1);
      writer.close();
    }catch(IOException e)
    {
      e.printStackTrace();
    }*/
  }

  public String findBestChoice(HashMap<String, Double> hshAnswer) throws Exception {

    Iterator<String> it = hshAnswer.keySet().iterator();
    String bestAns = null;
    double maxScore = 0;
    System.out.println("Aggregated counts; ");
    while (it.hasNext()) {
      String key = it.next();
      Double val = hshAnswer.get(key); 
      System.out.println("@@ Answer: "+key+"\tScore: " + val);
      if (val > maxScore) {
        maxScore = val;
        bestAns = key;
      }

    }
    System.out.println("Ture_MAX: "+maxScore+"\tAnswer: " + bestAns);
    if (maxScore <= NoneThreshold)
    {
      it = hshAnswer.keySet().iterator();
      while (it.hasNext()) 
      {
        String key = it.next();
        if(key.contains("None of"))
        {
          bestAns = key;
          break;
        }
      }
    }

    System.out.println("Again Ture_MAX: "+maxScore+"\tAnswer: " + bestAns);

    return bestAns;
  }

}
