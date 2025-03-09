package com.hongik.mentor.hongik_mentor.config;

import com.hongik.mentor.hongik_mentor.Initializer.TagInitializer;
import com.hongik.mentor.hongik_mentor.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TagConfig {

    @Bean
    public TagInitializer tagInitializer(TagRepository tagRepository) {
        return new TagInitializer(tagRepository);
    }

}
