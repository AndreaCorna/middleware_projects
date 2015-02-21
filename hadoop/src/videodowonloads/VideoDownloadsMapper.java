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
		Mapper<LongWritable, Text, Text, Text> {
	
	private final static IntWritable one = new IntWritable(1);


	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<Text, Text> collector, Reporter arg3)
			throws IOException {
		String record= value.toString();
		String matchedVideo= Utils.getVideo(record);
		String matchedDate= Utils.getDate(record);

		if(matchedVideo !=null && matchedDate!=null){
			Text matchedVideoText= new Text();
			matchedVideoText.set(matchedVideo.getBytes());
			
			Text matchedDateText= new Text();
			matchedDateText.set(matchedDate.getBytes());
			
			collector.collect(matchedDateText, matchedVideoText);
		}
		
	}

}
