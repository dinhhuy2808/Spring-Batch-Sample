package com.batch.runner;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

import com.batch.config.SpringBatchConfig;
import com.batch.config.SpringConfig;
import com.batch.dao.impl.ResultDaoImpl;
import com.batch.service.SendEmail;
import com.batch.util.Util;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        // Spring Java config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Util.class);
        context.register(SpringConfig.class);
        context.register(SpringBatchConfig.class);
        context.register(ResultDaoImpl.class);
        context.register(SendEmail.class);
        context.refresh();
        checkAndWriteLockFile(context);
        JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        Job job = (Job) context.getBean("firstBatchJob");
        System.out.println("Starting the batch job");
        try {
            JobExecution execution = jobLauncher.run(job, new JobParameters());
            System.out.println("Job Status : " + execution.getStatus());
            System.out.println("Job completed");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Job failed");
        } finally {
        	deleteLockFile(context);
		}
    }
    
    private static void checkAndWriteLockFile(AnnotationConfigApplicationContext context) {
    	Util util = (Util) context.getBean("util");
    	String uploadFolder = context.getEnvironment().getProperty("uploadFolder");
    	List<String> files = util.getAllFilesNameInFolder(uploadFolder);
    	if (files.contains("lock.lockfile")) {
    		System.exit(0);
    	} else {
    		util.writeToFile("", uploadFolder+"/lock.lockfile");
    	}
    }
    
    private static void deleteLockFile(AnnotationConfigApplicationContext context) {
    	Util util = (Util) context.getBean("util");
    	String uploadFolder = context.getEnvironment().getProperty("uploadFolder");
    	util.deleteFile(uploadFolder+"/lock.lockfile");
    }
}
