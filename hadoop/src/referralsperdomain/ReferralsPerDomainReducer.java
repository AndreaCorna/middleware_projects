package referralsperdomain;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class ReferralsPerDomainReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

	@Override
	public void reduce(Text domain, Iterator<IntWritable> values,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		int sum = 0;
		while (values.hasNext()) {
			IntWritable intWritable = (IntWritable) values.next();
			sum += intWritable.get();	
			System.out.println("REDUCER "+sum);

		}
		System.out.println("REDUCER "+sum);
		output.collect(domain, new IntWritable(sum));
		
	}

}
