package com.batch.config;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import com.batch.model.ProcessorInput;
import com.batch.model.ProcessorOutput;
import com.batch.processor.CustomItemProcessor;
import com.batch.reader.CustomItemReader;
import com.batch.writer.CustomItemWriter;

public class SpringBatchConfig {
    
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Value("input/record.csv")
    private Resource inputCsv;

    @Value("file:xml/output.xml")
    private Resource outputXml;

    @Bean
    public ItemReader<ProcessorInput> itemReader() {
        return new CustomItemReader();
    }

    @Bean
    public ItemProcessor<ProcessorInput, ProcessorOutput> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<ProcessorOutput> itemWriter(){
        return new CustomItemWriter();
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