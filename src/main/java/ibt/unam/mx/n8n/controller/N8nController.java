package ibt.unam.mx.n8n.controller;

import ibt.unam.mx.n8n.model.ChatInputDTO;
import ibt.unam.mx.n8n.service.N8nService;
import ibt.unam.mx.utils.Message;
import ibt.unam.mx.utils.TypesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        String userSessionId = request.getSessionId();
        return n8nService.getApi(userMessage, userSessionId);
    }

    @PostMapping("/file")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNO', 'EXTERNO')")
    public ResponseEntity<Message> handleFileUpload(@RequestParam("file") MultipartFile file) {
        // Valida que el archivo no esté vacío
        if (file.isEmpty()) {
            Message errorMsg = new Message();
            errorMsg.setText("El archivo no puede estar vacío.");
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.badRequest().body(errorMsg);
        }
        return n8nService.sendFile(file);
    }
}
