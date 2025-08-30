package com.trustai.userservice.hierarchy.repository;

public interface UplineProjection {
    int getDepth();
    Long getAncestor();
}
