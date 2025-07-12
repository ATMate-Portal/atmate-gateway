package com.atmate.portal.gateway.atmategateway.services;


import com.atmate.portal.gateway.atmategateway.database.entitites.*;
import com.atmate.portal.gateway.atmategateway.database.repos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientTypeRepository clientTypeRepository;
    private final TaxTypeRepository taxTypeRepository;
    private final AddressTypeRepository addressTypeRepository;
    private final ContactTypeRepository contactTypeRepository;
    private final ConfigurationRepository configurationRepository;
    private final JdbcTemplate jdbcTemplate;
    private final KeyService keyService;


    // Injeta o repositório de utilizadores e o codificador de passwords
    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ClientTypeRepository clientTypeRepository,
                           TaxTypeRepository taxTypeRepository,
                           AddressTypeRepository addressTypeRepository,
                           ContactTypeRepository contactTypeRepository,
                           ConfigurationRepository configurationRepository,
                           JdbcTemplate jdbcTemplate,
                           KeyService keyService) {
        this.userRepository = userRepository;
        this.clientTypeRepository = clientTypeRepository;
        this.taxTypeRepository = taxTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressTypeRepository = addressTypeRepository;
        this.contactTypeRepository = contactTypeRepository;
        this.configurationRepository = configurationRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.keyService = keyService;
    }

    @Override
    public void run(String... args) throws Exception {
        checkAndGenerateSecretKey();
        applyLogicToPaymentDeadlineColumn();
        seedUsers();
        seedClientTypes();
        seedTaxTypes();
        seedContactTypes();
        seedAddressTypes();
        seedConfigTable();
    }

    private void checkAndGenerateSecretKey(){
        String keyFile = System.getenv("KEY_NAME").concat("/");
        String keyPath = System.getenv("KEY_PATH");

        if (keyPath == null || keyFile == null) {
            log.error(">>> ERRO: As variáveis de ambiente KEY_PATH e/ou KEY_NAME não estão definidas. A geração da chave foi ignorada.");
            return;
        }

        Path fullPath = Paths.get(keyPath, keyFile);

        // Verifica se o ficheiro da chave NÃO existe
        if (!Files.exists(fullPath)) {
            log.info(">>> Ficheiro de chave secreta não encontrado. A gerar nova chave... <<<");
            try {
                keyService.generateAndStoreKey();
                log.info(">>> Nova chave secreta gerada com sucesso em: " + fullPath + " <<<");
            } catch (Exception e) {
                log.error(">>> FALHA AO GERAR A CHAVE SECRETA: " + e.getMessage());
            }
        } else {
            log.info(">>> Ficheiro de chave secreta já existe. Nenhuma ação necessária. <<<");
        }
    }

    private void applyLogicToPaymentDeadlineColumn() {
        try {
            // Query para verificar se a coluna 'payment_deadline' já existe
            String sqlCheck = """
                        SELECT COUNT(*) FROM information_schema.columns 
                        WHERE table_schema = DATABASE() 
                        AND table_name = 'taxes' 
                        AND column_name = 'payment_deadline'
                    """;

            Integer columnCount = jdbcTemplate.queryForObject(sqlCheck, Integer.class);

            // Se a contagem for 0, a coluna não existe, então executamos o ALTER TABLE para a ADICIONAR
            if (columnCount != null && columnCount == 0) {
                System.out.println(">>> Coluna 'payment_deadline' não encontrada. A aplicar migração SQL customizada... <<<");

                String alterSql = """
                            ALTER TABLE taxes
                            ADD COLUMN payment_deadline DATE
                                AS (
                                    COALESCE(
                                        STR_TO_DATE(
                                            JSON_UNQUOTE(JSON_EXTRACT(tax_data, '$."Data Limite de Pagamento"')),
                                            '%Y-%m-%d'
                                        ),
                                        STR_TO_DATE(
                                            JSON_UNQUOTE(JSON_EXTRACT(tax_data, '$."Data Lim. Pag."')),
                                            '%Y-%m-%d'
                                        ),
                                        DATE '2025-12-31'
                                    )
                                ) STORED;
                        """;

                jdbcTemplate.execute(alterSql);
                System.out.println(">>> Migração customizada para 'payment_deadline' aplicada com sucesso. <<<");
            } else {
                System.out.println(">>> Coluna 'payment_deadline' já existe. Nenhuma migração customizada necessária. <<<");
            }
        } catch (Exception e) {
            System.err.println(">>> ERRO ao executar a migração customizada para 'payment_deadline': " + e.getMessage());
        }
    }

    private void seedUsers() {
        String defaultUsername = "global";
        String defaultEmail = "global@atmate.pt";
        String defaultPassword = "global123";

        Optional<User> existingUser = userRepository.findByUsername(defaultUsername);

        if (existingUser.isEmpty()) {
            // Se não existir, cria um novo utilizador
            User adminUser = new User();
            adminUser.setUsername(defaultUsername);
            adminUser.setEmail(defaultEmail);

            adminUser.setPassword(passwordEncoder.encode(defaultPassword));

            userRepository.save(adminUser);

            log.info(">>> Utilizador padrão 'admin' criado com sucesso. <<<");
        } else {
            log.info(">>> Utilizador padrão 'admin' já existe. Nenhuma ação necessária. <<<");
        }
    }

    private void seedClientTypes() {
        // Verifica se a tabela client_type já tem registos
        if (clientTypeRepository.count() == 0) {
            log.info(">>> A popular a tabela 'client_type' com dados iniciais... <<<");
            ClientType singular = new ClientType();
            singular.setId(1);
            singular.setDescription("Trabalhador independente (ENI)");

            ClientType empresa = new ClientType();
            empresa.setId(2);
            empresa.setDescription("Empresa");

            ClientType coletivo = new ClientType();
            coletivo.setId(3);
            coletivo.setDescription("Pessoa coletiva pública");

            ClientType privado = new ClientType();
            privado.setId(4);
            privado.setDescription("Contribuinte privado");

            clientTypeRepository.saveAll(List.of(singular, empresa, coletivo, privado));
            log.info(">>> Tabela 'client_type' populada com sucesso. <<<");
        } else {
            log.info(">>> Tabela 'client_type' já contém dados. Nenhuma ação necessária. <<<");
        }
    }

    private void seedTaxTypes() {
        // Verifica se a tabela tax_type já tem registos
        if (taxTypeRepository.count() == 0) {
            log.info(">>> A popular a tabela 'tax_type' com dados iniciais... <<<");
            TaxType iuc = new TaxType();
            iuc.setDescription("IUC");

            TaxType iva = new TaxType();
            iva.setDescription("IVA");

            TaxType irs = new TaxType();
            irs.setDescription("IRC");

            TaxType irc = new TaxType();
            irc.setDescription("IRC");

            TaxType imi = new TaxType();
            imi.setDescription("IMI");

            taxTypeRepository.saveAll(List.of(iuc, iva, irs, irc, imi));
            log.info(">>> Tabela 'tax_type' populada com sucesso. <<<");
        } else {
            log.info(">>> Tabela 'tax_type' já contém dados. Nenhuma ação necessária. <<<");
        }
    }

    private void seedContactTypes() {
        if (contactTypeRepository.count() == 0) {
            log.info(">>> A popular a tabela 'contact_type' com dados iniciais... <<<");
            ContactType email = new ContactType();
            email.setId(1);
            email.setDescription("Email");

            ContactType tel = new ContactType();
            tel.setId(2);
            tel.setDescription("Telefone");

            contactTypeRepository.saveAll(List.of(email, tel));

            log.info(">>> Tabela 'contact_type' populada com sucesso. <<<");
        } else {
            log.info(">>> Tabela 'contact_type' já contém dados. Nenhuma ação necessária. <<<");
        }
    }

    private void seedAddressTypes() {
        if (addressTypeRepository.count() == 0) {
            log.info(">>> A popular a tabela 'adress_type' com dados iniciais... <<<");
            AddressType res = new AddressType();
            res.setId(1);
            res.setDescription("Residencial");

            AddressType comercial = new AddressType();
            comercial.setId(2);
            comercial.setDescription("Comercial");

            AddressType entrega = new AddressType();
            entrega.setId(3);
            entrega.setDescription("Entrega");

            addressTypeRepository.saveAll(List.of(res, comercial, entrega));

            log.info(">>> Tabela 'adress_type' populada com sucesso. <<<");
        } else {
            log.info(">>> Tabela 'adress_type' já contém dados. Nenhuma ação necessária. <<<");
        }
    }

    private void seedConfigTable() {
        if (configurationRepository.count() == 0) {
            log.info(">>> A popular a tabela 'configuration' com dados iniciais... <<<");
            Configuration warningDays = new Configuration();
            warningDays.setId(1);
            warningDays.setVarname("warning_days");
            warningDays.setVarvalue("365");
            warningDays.setDescription("Número de dias para notificação de aviso");
            warningDays.setIsActive(true);

            Configuration urgendyDays = new Configuration();
            urgendyDays.setId(2);
            urgendyDays.setVarname("urgency_days");
            urgendyDays.setVarvalue("100");
            urgendyDays.setDescription("Número de dias para notificação de urgência");
            urgendyDays.setIsActive(true);


            configurationRepository.saveAll(List.of(warningDays, urgendyDays));

            log.info(">>> Tabela 'configuration' populada com sucesso. <<<");
        } else {
            log.info(">>> Tabela 'configuration' já contém dados. Nenhuma ação necessária. <<<");
        }
    }
}
