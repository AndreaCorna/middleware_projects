package videodowonloads;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class VideoDownloadsReducer extends MapReduceBase implements
		Reducer<Text, Text, Text, Text> {

	private HashMap<String,Integer> videoNameList = new HashMap<String,Integer>();

	@Override
	public void reduce(Text data, Iterator<Text> videos,
			OutputCollector<Text, Text> output, Reporter arg3)
			throws IOException {

		while (videos.hasNext()) {

			String videoName= videos.next().toString();
			Integer currentValue=videoNameList.get(videoName);
			if (currentValue==null) {
				currentValue=0;
			}
			videoNameList.put(videoName, currentValue+1);

		}
		for (String videoName : videoNameList.keySet()) {
			String value= videoName+","+videoNameList.get(videoName);
			Text line= new Text();
			line.set(value.getBytes());
			output.collect(data, line);
		}
		videoNameList = new HashMap<String,Integer>();


	}



}
