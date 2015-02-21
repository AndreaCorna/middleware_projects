package referralsperdomain;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import middleware_hadoop.Utils;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;


public class ReferralsPerDomainMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	private Date startDate;
	private Date finalDate;
	private final static IntWritable one = new IntWritable(1);


	@Override
	public void map(LongWritable key, Text record,
			OutputCollector<Text, IntWritable> collector, Reporter reporter)
			throws IOException {
		String matchedDomain = Utils.getDomain(record.toString());
		if(matchedDomain != null){
			String date = Utils.getDate(record.toString());
			if(date != null){
				DateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
				Date dateLog = null;
				try {
					dateLog = formatter.parse(date);
					startDate =  df.parse("22/04/2003");
					finalDate = df.parse("30/05/2003");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(dateLog.before(finalDate) && dateLog.after(startDate)){
					Text matchedDomainText = new Text();
					matchedDomainText.set(matchedDomain.getBytes());
					collector.collect(matchedDomainText, one);
				}
				
			}
			
		}
		
	}


}
