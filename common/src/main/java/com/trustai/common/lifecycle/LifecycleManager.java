//package com.trustai.common.lifecycle;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class LifecycleManager {
//    private final List<Initializable> initializables;
//    private final List<Refreshable> refreshables;
//    private final List<Stoppable> stoppables;
//    private final List<Validatable> validatables;
//
//
//    @PostConstruct
//    public void start() {
//        initializables.forEach(Initializable::initialize);
//        validatables.forEach(Validatable::validate);
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        stoppables.forEach(Stoppable::stop);
//    }
//
//    public void refreshAll() {
//        refreshables.forEach(Refreshable::refresh);
//    }
//
//    public void validateAll() {
//        validatables.forEach(Validatable::validate);
//    }
//}
