package com.salvou.crm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class VendaController {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    // INTERCEPTA A ABERTURA DA TELA DE VENDAS
    @GetMapping("/venda") // Certifique-se de que esta rota bate com o link da sua Home
    public String abrirTelaVenda(Model model) {
        // ENVIA AS LISTAS ESSENCIAIS PARA O THYMELEAF NÃO QUEBRAR
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("servicos", servicoRepository.findAll());

        return "venda"; // Abre o arquivo venda.html
    }

    // SALVA O JSON DA VENDA VINDO DO JAVASCRIPT
    @PostMapping("/vendas/salvar")
    public ResponseEntity<String> finalizarVenda(@RequestBody Venda venda) {
        try {
            venda.setDataVenda(java.time.LocalDateTime.now());
            vendaRepository.save(venda);
            return ResponseEntity.ok("Venda salva com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }
}