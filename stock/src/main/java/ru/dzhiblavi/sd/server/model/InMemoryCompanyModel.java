package ru.dzhiblavi.sd.server.model;

import ru.dzhiblavi.sd.server.entity.Company;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InMemoryCompanyModel implements CompanyModel {
    private final List<Company> companies = new ArrayList<>();

    private Stream<Company> getCompanyByName(final String name) {
        return companies.stream().filter(c -> c.getName().equals(name));
    }

    @Override
    public void addCompany(final Company company) {
        getCompanyByName(company.getName())
                .findAny().ifPresent(c -> {
                    throw new IllegalArgumentException("Company " + company.getName() + " is already registered.");
                });
        this.companies.add(company);
    }

    @Override
    public Company getCompany(final String name) {
        return getCompanyByName(name).findAny().orElseThrow();
    }
}
