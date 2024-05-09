package com.example.int221backend;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public ModelMapper modelMapper(){
//        ModelMapper modelMapper = new ModelMapper();
//
//        modelMapper.createTypeMap(Task.class, AddTaskDTO.class)
//                .addMapping(Task::getStatus, AddTaskDTO::setStatus,
//                        (destination, value) -> destination.setStatus(value.getStatus().getStatusName()));
//
//        return modelMapper;
        return new ModelMapper();
    }
}
