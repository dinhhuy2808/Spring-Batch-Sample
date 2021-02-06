package com.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import com.batch.model.ProcessorInput;
import com.batch.model.ProcessorOutput;
import com.batch.model.UploadType;
import com.batch.processor.DictionaryUploadProcessor;
import com.batch.processor.ExerciseUploadProcessor;
import com.batch.processor.SyncDictionaryUploadProcessor;
import com.batch.processor.TestUploadProcessor;
import com.batch.reader.DictionaryUploadReader;
import com.batch.reader.ExerciseUploadReader;
import com.batch.reader.SyncDictionaryUploadReader;
import com.batch.reader.TestUploadReader;
import com.batch.writer.DictionaryUploadWriter;
import com.batch.writer.ExcerciseUploadWriter;
import com.batch.writer.SyncDictionaryUploadWriter;
import com.batch.writer.TestUploadWriter;
import com.google.common.collect.ImmutableMap;

public class SpringBatchConfig implements InitializingBean {
	private static final ImmutableMap<UploadType, Class<? extends ItemReader<ProcessorInput>>> MAP_READER = ImmutableMap
			.<UploadType, Class<? extends ItemReader<ProcessorInput>>>builder()
			.put(UploadType.EXERCISE, ExerciseUploadReader.class)
			.put(UploadType.DICTIONARY, DictionaryUploadReader.class)
			.put(UploadType.SYNC_DICTIONARY, SyncDictionaryUploadReader.class)
			.put(UploadType.TEST, TestUploadReader.class)
			.build();
	
	private static final ImmutableMap<UploadType, Class<? extends ItemProcessor<ProcessorInput, ProcessorOutput>>> MAP_PROCESSOR = ImmutableMap
			.<UploadType, Class<? extends ItemProcessor<ProcessorInput, ProcessorOutput>>>builder()
			.put(UploadType.EXERCISE, ExerciseUploadProcessor.class)
			.put(UploadType.DICTIONARY, DictionaryUploadProcessor.class)
			.put(UploadType.SYNC_DICTIONARY, SyncDictionaryUploadProcessor.class)
			.put(UploadType.TEST, TestUploadProcessor.class)
			.build();

	
	private static final ImmutableMap<UploadType, Class<? extends ItemWriter<ProcessorOutput>>> MAP_WRITER = ImmutableMap
			.<UploadType, Class<? extends ItemWriter<ProcessorOutput>>>builder()
			.put(UploadType.EXERCISE, ExcerciseUploadWriter.class)
			.put(UploadType.DICTIONARY, DictionaryUploadWriter.class)
			.put(UploadType.SYNC_DICTIONARY, SyncDictionaryUploadWriter.class)
			.put(UploadType.TEST, TestUploadWriter.class)
			.build();
	
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private AnnotationConfigApplicationContext context;
    
    @Value("input/record.csv")
    private Resource inputCsv;

    @Value("file:xml/output.xml")
    private Resource outputXml;

	@Value("#{systemProperties['uploadType']}")
	private UploadType uploadType;

	@Override
	public void afterPropertiesSet() throws Exception {
		context.register(MAP_READER.get(uploadType));
		context.register(MAP_PROCESSOR.get(uploadType));
		context.register(MAP_WRITER.get(uploadType));
	}

    @Bean
    protected Step step1(ItemReader<ProcessorInput> reader,
      ItemProcessor<ProcessorInput, ProcessorOutput> processor,
      ItemWriter<ProcessorOutput> writer) {
        return steps.get("step1").<ProcessorInput, ProcessorOutput> chunk(1)
          .reader(reader).processor(processor).writer(writer).build();
    }

    @Bean(name = "firstBatchJob")
    public Job job(@Qualifier("step1") Step step1) {
        return jobs.get("firstBatchJob").start(step1).build();
    }

}