package ibt.unam.mx.n8n.controller;

import ibt.unam.mx.n8n.model.ChatInputDTO;
import ibt.unam.mx.n8n.service.N8nService;
import ibt.unam.mx.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/n8n")
public class N8nController {

    private final N8nService n8nService;

    @Autowired
    public N8nController(N8nService n8nService) {
        this.n8nService = n8nService;
    }

    @PostMapping("/message")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNO', 'EXTERNO')")
    public ResponseEntity<Message> getApi(@RequestBody ChatInputDTO request) {
        String userMessage = request.getChatInput();
        return n8nService.getApi(userMessage);
    }
}
