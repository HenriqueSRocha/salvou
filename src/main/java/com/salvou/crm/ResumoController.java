package com.salvou.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            @RequestParam(value = "clienteId", required = false) String clienteIdStr,
            @RequestParam(value = "vendedorNome", required = false) String vendedorNome,
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            Model model) {

        // 1. Configura as datas padrão de corte caso venham nulas
        if (dataFim == null) dataFim = LocalDate.now();
        if (dataInicio == null) dataInicio = dataFim.minusMonths(1);

        LocalDateTime inicioDia = dataInicio.atStartOfDay();
        LocalDateTime fimDia = dataFim.atTime(LocalTime.MAX);

        // 2. Busca todas as vendas brutas do banco
        List<Venda> todasVendas = vendaRepository.findAll();

        // 3. FLUXO ÚNICO E SEGURO DE FILTRAGEM (Evita tela branca)
        final String finalClienteIdStr = clienteIdStr; // Variável auxiliar para a lambda
        List<Venda> vendasFiltradas = todasVendas.stream()
                .filter(v -> v != null && v.getDataVenda() != null) // Garante que a venda e a data existem

                // Filtro de Período Cronológico (Sempre Ativo)
                .filter(v -> !v.getDataVenda().isBefore(inicioDia) && !v.getDataVenda().isAfter(fimDia))

                // Filtro de Entidade (Aplica dinamicamente dependendo da aba ativa)
                .filter(v -> {
                    if ("cliente".equals(tipo)) {
                        if (finalClienteIdStr == null || finalClienteIdStr.isEmpty()) {
                            return true; // Exibir todos os clientes
                        }
                        if ("balcao".equals(finalClienteIdStr)) {
                            return v.getCliente() == null; // Isola Cliente de Balcão (Sem cliente vinculado)
                        }
                        // Filtra pelo ID do cliente convencional
                        try {
                            Long idLogico = Long.parseLong(finalClienteIdStr);
                            return v.getCliente() != null && idLogico.equals(v.getCliente().getId());
                        } catch (NumberFormatException e) {
                            return true;
                        }
                    } else if ("vendedor".equals(tipo)) {
                        if (vendedorNome == null || vendedorNome.isEmpty()) {
                            return true; // Exibir todos os vendedores
                        }
                        return vendedorNome.equals(v.getVendedor());
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 4. Cálculos estatísticos em cima da lista unificada e filtrada
        double faturamentoTotal = vendasFiltradas.stream()
                .filter(v -> v.getValorTotal() != null)
                .mapToDouble(Venda::getValorTotal)
                .sum();

        // Gera a lista dinâmica de nomes de vendedores únicos para o select
        List<String> listaVendedores = todasVendas.stream()
                .filter(v -> v != null && v.getVendedor() != null && !v.getVendedor().isEmpty())
                .map(Venda::getVendedor)
                .distinct()
                .collect(Collectors.toList());

        // 5. Devolve as variáveis de controle e estado para o Thymeleaf manter os campos acesos
        model.addAttribute("tipoSelecionado", tipo);
        model.addAttribute("clienteSelecionadoIdStr", clienteIdStr);
        model.addAttribute("vendedorSelecionadoNome", vendedorNome);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);

        model.addAttribute("vendas", vendasFiltradas);
        model.addAttribute("faturamentoTotal", faturamentoTotal);
        model.addAttribute("totalVendas", vendasFiltradas.size());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("vendedoresLista", listaVendedores);

        return "resumo";
    }
}