package ru.dzhiblavi.sd.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dzhiblavi.sd.server.model.CompanyModel;
import ru.dzhiblavi.sd.server.model.InMemoryCompanyModel;

@Configuration
public class InMemoryCompanyDaoConfiguration {
    @Bean
    public CompanyModel companyDao() {
        return new InMemoryCompanyModel();
    }
}
