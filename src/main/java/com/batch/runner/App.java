package com.batch.runner;

import static com.batch.model.UploadType.DICTIONARY;
import static com.batch.model.UploadType.SYNC_DICTIONARY;
import static com.batch.model.UploadType.EXERCISE;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.batch.config.SpringBatchConfig;
import com.batch.config.SpringConfig;
import com.batch.dao.impl.DictionaryDaoImpl;
import com.batch.dao.impl.LessonDictionaryDaoImpl;
import com.batch.dao.impl.ResultDaoImpl;
import com.batch.model.UploadType;
import com.batch.service.SendEmail;
import com.batch.util.Util;
import com.google.common.collect.ImmutableMap;
/**
 * Hello world!
 *
 */
public class App 
{
	private static final ImmutableMap<UploadType, String> LOCK_FOLDER = ImmutableMap.<UploadType, String>builder()
			.put(EXERCISE, "uploadExerciseLockFolder")
			.put(DICTIONARY, "uploadDictionaryLockFolder")
			.put(SYNC_DICTIONARY, "uploadDictionaryLockFolder")
			.build();
	
	private static final ImmutableMap<UploadType, String> LOCK_FILE_ERROR = ImmutableMap.<UploadType, String>builder()
			.put(EXERCISE, "There's another exercise upload process running, please wait until it's done.")
			.put(DICTIONARY, "There's another dictionary upload process running, please wait until it's done.")
			.put(SYNC_DICTIONARY, "There's another sync dictionary upload process running, please wait until it's done.")
			.build();
    public static void main(String[] args) {
        // Spring Java config
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Util.class);
        context.register(SpringConfig.class);
        context.register(SpringBatchConfig.class);
        context.register(ResultDaoImpl.class);
        context.register(DictionaryDaoImpl.class);
        context.register(LessonDictionaryDaoImpl.class);
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
    	UploadType uploadType = UploadType.valueOf(System.getProperty("uploadType"));
    	String uploadLockFolder = context.getEnvironment().getProperty(LOCK_FOLDER.get(uploadType));
    	List<String> files = util.getAllFilesNameInFolder(uploadLockFolder);
    	if (files.contains("lock.lockfile")) {
    		SendEmail sendEmail = context.getBean(SendEmail.class);
    		try {
				sendEmail.sendEmail(LOCK_FILE_ERROR.get(uploadType), "Upload fail");
			} catch (MessagingException e) {
				e.printStackTrace();
			} finally {
				System.exit(0);
			}
    	} else {
    		util.writeToFile("", uploadLockFolder+"/lock.lockfile");
    	}
    }
    
    private static void deleteLockFile(AnnotationConfigApplicationContext context) {
    	Util util = (Util) context.getBean("util");
    	UploadType uploadType = UploadType.valueOf(System.getProperty("uploadType"));
    	String uploadLockFolder = context.getEnvironment().getProperty(LOCK_FOLDER.get(uploadType));
    	util.deleteFile(uploadLockFolder+"/lock.lockfile");
    }
}
