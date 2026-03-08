package io.github.nextentity.example.dao;

import io.github.nextentity.data.AbstractRepository;
import io.github.nextentity.example.eneity.Company;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HuangChengwei
 * @since 2024-03-27 14:24
 */
@Repository
public class CompanyRepository extends AbstractRepository<Integer, Company> {
    public List<Company> getList() {
        return repository.getList();
    }

    public void delete(Iterable<Company> list) {
        repository.delete(list);
    }
}
