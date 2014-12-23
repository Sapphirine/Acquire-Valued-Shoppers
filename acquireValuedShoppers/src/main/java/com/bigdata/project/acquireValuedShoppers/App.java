package com.bigdata.project.acquireValuedShoppers;

import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//import org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator;
import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression;
import org.apache.mahout.classifier.sgd.CrossFoldLearner;
import org.apache.mahout.classifier.sgd.ElasticBandPrior;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.L2;
import org.apache.mahout.classifier.sgd.ModelSerializer;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.classifier.sgd.TPrior;
import org.apache.mahout.classifier.sgd.UniformPrior;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.vectorizer.encoders.ConstantValueEncoder;
import org.apache.mahout.vectorizer.encoders.Dictionary;
import org.apache.mahout.vectorizer.encoders.FeatureVectorEncoder;
import org.apache.mahout.vectorizer.encoders.StaticWordValueEncoder;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

// id chain dept category company brand date productsize 
// productmeasure purchasequantity purchaseamount 
// offer market repeattrips repeater offerdate category quantity offervalue
public class App {
	List<Integer> train;
	List<Integer> test;
	ArrayList<Integer> target;
	List<Vector> data;
	ArrayList<Integer> order;
	List<String> raw;
	Random random;
	String testFile;
	String trainFile;
	List<String> test_raw;
	List<Vector> test_data;
	List<String> test_ids;
	ArrayList<Integer> test_order;

	public void buildTrainVectors() throws IOException {

		// Snip ...

		RandomUtils.useTestSeed();
		Splitter onComma = Splitter.on(",");

		// read the data
		raw = Resources.readLines(Resources.getResource(trainFile),
				Charsets.UTF_8);

		// holds features
		data = Lists.newArrayList();

		// holds target variable
		target = Lists.newArrayList();

		// for decoding target values
		Dictionary dict = new Dictionary();

		// for permuting data later
		order = Lists.newArrayList();

		for (String line : raw.subList(1, raw.size())) {
			if (line.trim().equals(""))
				continue;
			// order gets a list of indexes
			order.add(order.size());

			// parse the predictor variables
			Vector v = new DenseVector(15);
			v.set(0, 1);
			int i = 1;
			int colID = 1;
			Iterable<String> values = onComma.split(line);
			// id chain dept category company brand date productsize
			// productmeasure purchasequantity purchaseamount offer market
			// repeattrips repeater offerdate quantity offervalue

			for (String value : Iterables.limit(values, 18)) {

				if (colID == 7 || colID == 9 || colID == 16) {
					colID++;
					continue;
				}
				if (colID == 15) {
					// System.out.println("target value is " + value);
					target.add(dict.intern(Iterables.get(values, 14)));
					colID++;
					continue;
				}
				// System.out.println("col value is " + value);
				v.set(i, Double.parseDouble(value));
				i++;
				colID++;
			}
			data.add(v);

		}
	}

	public void buildTestVecotrs() throws IOException {

		// Snip ...

		RandomUtils.useTestSeed();
		Splitter onComma = Splitter.on(",");

		// read the data
		test_raw = Resources.readLines(Resources.getResource(testFile),
				Charsets.UTF_8);

		// holds features
		test_data = Lists.newArrayList();

		// // holds target variable
		// target = Lists.newArrayList();

		// for decoding target values
		Dictionary dict = new Dictionary();

		// for permuting data later
		test_order = Lists.newArrayList();
		test_ids = Lists.newArrayList();

		for (String line : test_raw.subList(1, test_raw.size())) {
			if (line.trim().equals(""))
				continue;
			// order gets a list of indexes
			test_order.add(test_order.size());

			// parse the predictor variables
			Vector v = new DenseVector(15);
			v.set(0, 1);
			int i = 1;
			int colID = 1;
			Iterable<String> values = onComma.split(line);
			// id chain dept category company brand date productsize
			// productmeasure purchasequantity purchaseamount offer market
			// repeattrips repeater offerdate quantity offervalue

			for (String value : Iterables.limit(values, 17)) {
				if (colID == 1) {
					test_ids.add(value);
				}

				if (colID == 7 || colID == 9 || colID == 16) {
					colID++;
					continue;
				}
				// System.out.println("col value is " + value);
				v.set(i, Double.parseDouble(value));
				i++;
				colID++;
			}
			test_data.add(v);

		}
	}

	public static void main(String[] args) throws Exception {

		String train_file = args[1];
		String test_file = args[2];
		App app = new App();
		app.trainFile = train_file;
		app.testFile = test_file;
		app.buildTrainVectors();
		app.buildTestVecotrs();
		app.classify();

	}

	public void classify() throws Exception {

		double heldOutPercentage = 0.20;
		int cutoff = (int) (heldOutPercentage * order.size());
		Collections.shuffle(order);
		List<Integer> test = order.subList(0, cutoff);
		List<Integer> train = order.subList(cutoff, order.size());

		double accuracy = 0.0, temp = 0.0;

		// AdaptiveLogisticRegression lr = new AdaptiveLogisticRegression(2, 15,
		// new L1());
		// OnlineLogisticRegression lr = new OnlineLogisticRegression(2,15, new
		// UniformPrior());
		CrossFoldLearner lr = new CrossFoldLearner(5000, 2, 15, new L1());
		// lr.learningRate(0.);
		for (Integer k : train) {
			lr.train(target.get(k), data.get(k));
		}

		// System.out.println("Learning rate is " + lr.currentLearningRate());

		lr.close();

		// write model to disk

		/*
		 * ModelSerializer.writeBinary("/home/bigdata/Downloads/alr2.model",
		 * lr.getBest().getPayload().getLearner());//.getModels().get(0));
		 * InputStream in=new
		 * FileInputStream("/home/bigdata/Downloads/alr2.model");
		 * CrossFoldLearner
		 * best=ModelSerializer.readBinary(in,CrossFoldLearner.class);
		 * System.out
		 * .println("auc="+best.auc()+"percentCorrect="+best.percentCorrect
		 * ()+"LogLikelihood="+best.getLogLikelihood());
		 */

		// in.close();
		// check the accuracy on held out data
		int x = 0, r = 0;
		for (Integer k : test) {
			// Test testProfile;

			r = lr.classifyFull(data.get(k)).maxValueIndex();
			// System.out.println("r="+r+" "+"target_train.get(k)="+target_train.get(k));
			x += r == target.get(k) ? 1 : 0;
		}

		accuracy = (double) (x) / (double) (test.size());
		accuracy *= 100.0;

		System.out.printf("accuracy is %f\n", accuracy);

		r = 0;
		FileWriter writer = new FileWriter("submission.csv");

		writer.append("id");
		writer.append(",");
		writer.append("repeatProbability");
		writer.append("\n");
		for (Integer k : testOrder) {
			// Test testProfile;
			writer.append(test_ids.get(k));
			writer.append(",");
			r = lr.classifyFull(test_data.get(k)).maxValueIndex();
			// System.out.println("r="+r+" "+"target_train.get(k)="+target_train.get(k));
			writer.append(r);
			writer.append("\n");
			///x += r == target.get(k) ? 1 : 0;
		}
		writer.flush();
		writer.close();
	}
}
