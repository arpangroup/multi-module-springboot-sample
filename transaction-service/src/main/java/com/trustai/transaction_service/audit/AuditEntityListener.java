//package com.trustai.transaction_service.audit;
//
//import com.trustai.transaction_service.entity.Transaction;
//import jakarta.persistence.PrePersist;
//import jakarta.persistence.PreUpdate;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.time.LocalDateTime;
//
//public class AuditEntityListener {
//
//    @PrePersist
//    public void prePersist(Object target) {
//        if (target instanceof Transaction transaction) {
//            String currentUserId = getCurrentUserId();
//            transaction.setCreatedAt(LocalDateTime.now());
//            transaction.setUpdatedAt(LocalDateTime.now());
//            transaction.setCreatedBy(currentUserId);
//            transaction.setUpdatedBy(currentUserId);
//        }
//    }
//
//    @PreUpdate
//    public void preUpdate(Object target) {
//        if (target instanceof Transaction transaction) {
//            String currentUserId = getCurrentUserId();
//            transaction.setUpdatedAt(LocalDateTime.now());
//            transaction.setUpdatedBy(currentUserId);
//        }
//    }
//
//    private String getCurrentUserId() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.isAuthenticated()) {
//            Object principal = auth.getPrincipal();
//            if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
//                return userDetails.getUsername(); // or your own ID accessor
//            } else if (principal instanceof String str) {
//                return str;
//            }
//        }
//        return null;
//    }
//}
