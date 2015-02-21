package middleware_hadoop;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import pageviewers.PageViewerMapper;
import pageviewers.PageViewerReducer;

import com.amazonaws.services.elastictranscoder.model.Job;

public class Main extends Configured implements Tool{

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new Main(), args);
        System.exit(res);

	}
	
	private JobConf getPageViewerJob(String[] args) {
Configuration conf = getConf();
		
        JobConf job = new JobConf(conf, Main.class);
        job.setJobName("PageViewer");
		
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
			
        job.setMapperClass(PageViewerMapper.class);
        job.setCombinerClass(PageViewerReducer.class);
        job.setReducerClass(PageViewerReducer.class);
			
        job.setInputFormat(TextInputFormat.class);
        job.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		return job;
		
		
	}


	@Override
	public int run(String[] arg0) throws Exception {
		
		JobConf pageViewerjob = getPageViewerJob(arg0);
		
		
		
		JobClient.runJob(pageViewerjob);
		return 0;
	}

}
