package com.salvou.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ResumoController {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/resumo")
    public String exibirResumo(
            @RequestParam(value = "tipo", defaultValue = "cliente") String tipo,
            @RequestParam(value = "clienteId", required = false) Long clienteId,
            @RequestParam(value = "vendedorId", required = false) Long vendedorId,
            Model model) {

        // 1. Buscar todas as vendas de forma segura
        List<Venda> todasVendas = vendaRepository.findAll();
        List<Venda> vendasFiltradas = todasVendas;

        // 2. Aplicar filtros com proteção total contra NullPointerException
        if ("cliente".equals(tipo)) {
            if (clienteId != null) {
                vendasFiltradas = todasVendas.stream()
                        .filter(v -> v != null && v.getCliente() != null && clienteId.equals(v.getCliente().getId()))
                        .collect(Collectors.toList());
                model.addAttribute("clienteSelecionadoId", clienteId);
            }
        } else if ("vendedor".equals(tipo)) {
            // Como ainda não há a tabela de Vendedores na Sprint 1, não filtramos por ID
            // Apenas mantemos a lista completa ou tratamos o mock do Admin
            model.addAttribute("vendedorSelecionadoId", vendedorId);
        }

        // 3. Cálculos estatísticos seguros (evitando acumular nulos)
        double faturamentoTotal = vendasFiltradas.stream()
                .filter(v -> v != null && v.getValorTotal() != null)
                .mapToDouble(Venda::getValorTotal)
                .sum();

        long totalVendas = vendasFiltradas.size();

        // 4. Enviar os dados para o Thymeleaf
        model.addAttribute("tipoSelecionado", tipo);
        model.addAttribute("vendas", vendasFiltradas);
        model.addAttribute("faturamentoTotal", faturamentoTotal);
        model.addAttribute("totalVendas", totalVendas);

        // Alimenta o <select> de clientes com segurança
        model.addAttribute("clientes", clienteRepository.findAll());

        return "resumo";
    }
}