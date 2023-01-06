package com.example.application.data.service;

import com.example.application.data.entity.Biodata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BiodataRepository extends JpaRepository<Biodata, Long>, JpaSpecificationExecutor<Biodata> {

}
