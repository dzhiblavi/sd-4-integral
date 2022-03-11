package ru.dzhiblavi.sd.server.model;

import ru.dzhiblavi.sd.server.entity.Company;

public interface CompanyModel {
    void addCompany(final Company company);

    Company getCompany(final String name);
}
