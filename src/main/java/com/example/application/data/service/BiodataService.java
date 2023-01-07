package com.example.application.data.service;

import com.example.application.data.entity.Biodata;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BiodataService {

    private final BiodataRepository repository;

    public BiodataService(BiodataRepository repository) {
        this.repository = repository;
    }

    public Optional<Biodata> get(Long id) {
        return repository.findById(id);
    }

    public Biodata update(Biodata entity) {
        return repository.save(entity);
    }

    public void delete(Biodata id) {
        repository.delete(id);
    }

    public Page<Biodata> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Biodata> list(Pageable pageable, Specification<Biodata> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
