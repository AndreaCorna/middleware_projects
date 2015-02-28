package referringdomainsperday;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import middleware_hadoop.Utils;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class ReferringDomainsMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

	private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	private static final String startDateString = "22/04/2003";
	private static final String finalDateString = "30/05/2003";
	private Date startDate;
	private Date finalDate;
	
	@Override
	public void map(LongWritable key, Text record,
			OutputCollector<Text, Text> collector, Reporter reporter) throws IOException {
		String matchedDomain = Utils.getDomain(record.toString());
		//check if the request comes from a domain different from the local website
		if(matchedDomain != null){
			String date = Utils.getDate(record.toString());
			//check if the date is between the start and final date
			if(date != null){
				DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy",Locale.ENGLISH);
				
				Date dateLog = null;
				try {
					dateLog = formatter.parse(date);
					startDate =  df.parse(startDateString);
					finalDate = df.parse(finalDateString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(dateLog.before(finalDate) && dateLog.after(startDate)){
					Text dateText = new Text();
					dateText.set(dateLog.toString().getBytes());
					Text matchedDomainText = new Text();
					matchedDomainText.set(matchedDomain.getBytes());
					collector.collect(dateText, matchedDomainText);
				}
				
			}
			
		}
		
	}

}
