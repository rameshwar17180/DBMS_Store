import java.util.Properties;
import java.util.Random;

import org.ejml.simple.SimpleMatrix;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalyzer {

	/*
	 * "Very negative" = 0 "Negative" = 1 "Neutral" = 2 "Positive" = 3
	 * "Very positive" = 4
	 */

	static Properties props;
	static StanfordCoreNLP pipeline;

	public void initialize() {
		 // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and sentiment
		props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
	}

	public double[] getSentimentResult(String text,Sentiment_helper sh,int ind) {

		SentimentResult sentimentResult = new SentimentResult();
		SentimentClassification sentimentClass = new SentimentClassification();
		double[] sentimentResult_text=new double[5];
		if (text != null && text.length() > 0) {
			
			// run all Annotators on the text
			Annotation annotation = pipeline.process(text);

			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				// this is the parse tree of the current sentence
				Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				SimpleMatrix sm = RNNCoreAnnotations.getPredictions(tree);
				String sentimentType = sentence.get(SentimentCoreAnnotations.SentimentClass.class);

				sentimentClass.setVeryPositive((double)Math.round(sm.get(4) * 100d));
				sentimentClass.setPositive((double)Math.round(sm.get(3) * 100d));
				sentimentClass.setNeutral((double)Math.round(sm.get(2) * 100d));
				sentimentClass.setNegative((double)Math.round(sm.get(1) * 100d));
				sentimentClass.setVeryNegative((double)Math.round(sm.get(0) * 100d));
				
				sentimentResult.setSentimentScore(RNNCoreAnnotations.getPredictedClass(tree));
				sentimentResult.setSentimentType(sentimentType);
				sentimentResult.setSentimentClass(sentimentClass);
			}
			
		}
		sentimentResult_text[0]=sh.get_negative(ind);
		sentimentResult_text[1]=sh.get_very_negative(ind);
		sentimentResult_text[2]=sh.get_positive(ind);
		sentimentResult_text[3]=sh.get_very_negative(ind);
		sentimentResult_text[4]=sh.get_neutral(ind);

		return sentimentResult_text;
	}
	public double[] getSentimentResult_text(String text,Sentiment_helper sh,int ind) {
		double[] sentimentResult_out=new double[5];
		sentimentResult_out[0]=sh.get_negative(ind);
		sentimentResult_out[1]=sh.get_very_positive(ind);
		sentimentResult_out[2]=sh.get_positive(ind);
		sentimentResult_out[3]=sh.get_very_negative(ind);
		sentimentResult_out[4]=sh.get_neutral(ind);
		return sentimentResult_out;
	}

	
}
