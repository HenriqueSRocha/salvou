package com.salvou.crm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ClienteRepository clienteRepository,
                                   ServicoRepository servicoRepository,
                                   VendaRepository vendaRepository) {
        return args -> {
            // Se já existirem clientes cadastrados, limpa o console e ignora para não duplicar
            if (clienteRepository.count() > 0) {
                return;
            }

            System.out.println("Gerando massa de dados avançada de teste para o Salvou CRM...");

            // 1. MIX DE SERVIÇOS E PRODUTOS CONFIGURADOS (Variedade de Preços)
            Servico s1 = servicoRepository.save(new Servico("Consultoria Estratégica CRM", 350.0));
            Servico s2 = servicoRepository.save(new Servico("Desenvolvimento Landing Page Premium", 1500.0));
            Servico s3 = servicoRepository.save(new Servico("Suporte Técnico Mensal (Plano Básico)", 190.0));
            Servico s4 = servicoRepository.save(new Servico("Suporte Técnico Mensal (Plano Pro)", 450.0));
            Servico s5 = servicoRepository.save(new Servico("Gestão de Tráfego Pago (Mensalidade)", 800.0));
            Servico s6 = servicoRepository.save(new Servico("Identidade Visual & Branding", 1200.0));
            Servico s7 = servicoRepository.save(new Servico("Otimização de SEO Avançada", 950.0));
            Servico s8 = servicoRepository.save(new Servico("Hospedagem Cloud Anual", 600.0));

            Cliente padrao = new Cliente();
            padrao.setNome("Cliente Padrão");
            padrao.setCpf("000.000.000-00"); // CPF fictício ou padrão
            padrao.setTelefone("(00) 00000-0000");
            padrao.setEmail("balcao@salvou.com");
            padrao.setDataNascimento(java.time.LocalDate.of(2020, 1, 1));
            padrao.setEndereco("Venda Direta");

            clienteRepository.save(padrao);

            // 2. CARTEIRA DE CLIENTES EXPANDIDA
            Cliente c1 = clienteRepository.save(new Cliente("Joyce Manicure & Estética"));
            c1.setCpf("111.222.333-44");
            c1.setTelefone("(35) 98888-1111");
            c1.setEndereco("Rua das Flores, 123");
            c1.setEmail("joyce@estetica.com");
            clienteRepository.save(c1);

            Cliente c2 = clienteRepository.save(new Cliente("Marcos Pinturas Residenciais"));
            c2.setCpf("555.666.777-88");
            c2.setTelefone("(35) 98888-2222");
            c2.setEndereco("Av. Central, 456");
            c2.setEmail("marcos@pinturas.com");
            clienteRepository.save(c2);

            Cliente c3 = clienteRepository.save(new Cliente("Ana Laura Odontologia"));
            c3.setCpf("999.888.777-66");
            c3.setTelefone("(35) 98888-3333");
            c3.setEndereco("Rua Santa Cruz, 789");
            c3.setEmail("contato@anaodontologia.com");
            clienteRepository.save(c3);

            Cliente c4 = clienteRepository.save(new Cliente("Carlos Mecânica Automotiva"));
            c4.setCpf("222.444.666-88");
            c4.setTelefone("(35) 99999-4444");
            c4.setEndereco("Rua Engenheiro Prado, 10");
            c4.setEmail("carlos@mecanicaprado.com");
            clienteRepository.save(c4);

            Cliente c5 = clienteRepository.save(new Cliente("Fernanda Confeitaria Artesanal"));
            c5.setCpf("333.777.111-22");
            c5.setTelefone("(35) 99999-5555");
            c5.setEndereco("Praça da Matriz, 45");
            c5.setEmail("doces@fernandaconfeitaria.com");
            clienteRepository.save(c5);


            // 3. HISTÓRICO CRONOLÓGICO DE VENDAS (Simulando variação de faturamento por dia)
            LocalDateTime hoje = LocalDateTime.now();

            // Há 9 dias - Venda menor
            Venda v1 = new Venda();
            v1.setCliente(c1);
            v1.setServicos(List.of(s3));
            v1.setValorTotal(190.0);
            v1.setDataVenda(hoje.minusDays(9));
            vendaRepository.save(v1);

            // Há 8 dias - Faturamento alto
            Venda v2 = new Venda();
            v2.setCliente(c3);
            v2.setServicos(List.of(s2, s8));
            v2.setValorTotal(2100.0);
            v2.setDataVenda(hoje.minusDays(8));
            vendaRepository.save(v2);

            // Há 7 dias - Duas vendas no mesmo dia para testar o agrupamento do gráfico
            Venda v3 = new Venda();
            v3.setCliente(c2);
            v3.setServicos(List.of(s1));
            v3.setValorTotal(350.0);
            v3.setDataVenda(hoje.minusDays(7));
            vendaRepository.save(v3);

            Venda v4 = new Venda();
            v4.setCliente(c4);
            v4.setServicos(List.of(s5));
            v4.setValorTotal(800.0);
            v4.setDataVenda(hoje.minusDays(7)); // Mesma data do de cima
            vendaRepository.save(v4);

            // Há 5 dias (pulando o dia 6 de propósito para ver o comportamento do gráfico)
            Venda v5 = new Venda();
            v5.setCliente(c5);
            v5.setServicos(List.of(s6));
            v5.setValorTotal(1200.0);
            v5.setDataVenda(hoje.minusDays(5));
            vendaRepository.save(v5);

            // Há 4 dias
            Venda v6 = new Venda();
            v6.setCliente(c1);
            v6.setServicos(List.of(s4, s7));
            v6.setValorTotal(1400.0);
            v6.setDataVenda(hoje.minusDays(4));
            vendaRepository.save(v6);

            // Há 3 dias - Venda sem cliente (Balcão / Admin Padrão)
            Venda v7 = new Venda();
            v7.setCliente(null);
            v7.setServicos(List.of(s3));
            v7.setValorTotal(190.0);
            v7.setDataVenda(hoje.minusDays(3));
            vendaRepository.save(v7);

            // Há 2 dias
            Venda v8 = new Venda();
            v8.setCliente(c2);
            v8.setServicos(List.of(s5));
            v8.setValorTotal(800.0);
            v8.setDataVenda(hoje.minusDays(2));
            vendaRepository.save(v8);

            // Ontem - Faturamento de pico
            Venda v9 = new Venda();
            v9.setCliente(c4);
            v9.setServicos(List.of(s2, s7));
            v9.setValorTotal(2450.0);
            v9.setDataVenda(hoje.minusDays(1));
            vendaRepository.save(v9);

            // Hoje
            Venda v10 = new Venda();
            v10.setCliente(c5);
            v10.setServicos(List.of(s4));
            v10.setValorTotal(450.0);
            v10.setDataVenda(hoje);
            vendaRepository.save(v10);

            System.out.println("Nova amostragem de dados injetada com sucesso!");
        };
    }
}