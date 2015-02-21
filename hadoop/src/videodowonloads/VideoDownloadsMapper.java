package videodowonloads;

import java.io.IOException;

import middleware_hadoop.Utils;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class VideoDownloadsMapper extends MapReduceBase implements
		Mapper<LongWritable, Text, Text, IntWritable> {
	
	private final static IntWritable one = new IntWritable(1);


	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, IntWritable> collector, Reporter arg3)
			throws IOException {
		String record= value.toString();
		String matched= Utils.getVideo(record);
		if(matched!=null){
			System.out.println("Mapper found value");
			Text matchedText= new Text();
			matchedText.set(matched.getBytes());
			collector.collect(matchedText, one);
		}
		
	}

}
