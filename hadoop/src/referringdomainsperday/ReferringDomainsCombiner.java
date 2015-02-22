package referringdomainsperday;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;


public class ReferringDomainsCombiner extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
	
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter)
			throws IOException {
		
		ArrayList<String> domains = new ArrayList<String>();
		while(values.hasNext()){
			String domain = values.next().toString();
			if(!domains.contains(domain)){
				domains.add(domain);
			}
		}
		for(String domain:domains){
			Text domainText = new Text();
			domainText.set(domain.getBytes());
			output.collect(key,domainText );
		}
		
		
	}



}


	





