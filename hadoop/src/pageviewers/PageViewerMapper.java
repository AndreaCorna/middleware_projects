package pageviewers;

import java.io.IOException;

import middleware_hadoop.Utils;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class PageViewerMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

	private final static IntWritable one = new IntWritable(1);

	
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> output, Reporter reporter)
			throws IOException {
		String record = value.toString();
		String date = Utils.getDate(record);
		if(date!=null){
			Text dateText=new Text();
			dateText.set(date.getBytes());
			output.collect(dateText, one);
		}
		
		
	}

}
