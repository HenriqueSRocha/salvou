package com.salvou.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    @GetMapping("/")
    public String home(Model model) {
        // 1. Contagem total de clientes cadastrados
        long totalClientes = clienteRepository.count();

        // 2. Cálculo do faturamento total de vendas
        List<Venda> todasVendas = vendaRepository.findAll();
        double faturamentoTotal = todasVendas.stream()
                .mapToDouble(Venda::getValorTotal)
                .sum();

        // 3. Contagem de compromissos pendentes na agenda
        long tarefasPendentes = agendaRepository.findByConcluidoFalse().size();

        // Passando as variáveis para a view do Thymeleaf
        model.addAttribute("totalClientes", totalClientes);
        model.addAttribute("faturamentoTotal", faturamentoTotal);
        model.addAttribute("tarefasPendentes", tarefasPendentes);

        return "home";
    }
}