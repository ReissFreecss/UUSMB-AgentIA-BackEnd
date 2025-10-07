package ibt.unam.mx.n8n.controller;

import ibt.unam.mx.email.EmailDTO;
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

    @PostMapping("/email")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Message> sendEmail(@RequestBody EmailDTO request) {
        String to = request.getTo();
        String subject = request.getSubject();
        String body = request.getBody();
        return n8nService.sendEmail(to, subject, body);
    }

    @PostMapping("/file")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
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

    @PostMapping("/messageCotizar")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNO', 'EXTERNO')")
    public ResponseEntity<Message> getApiCotizar(@RequestBody ChatInputDTO request) {
        String userMessage = request.getChatInput();
        String userSessionId = request.getSessionId();
        return n8nService.getApiCotizar(userMessage, userSessionId);
    }

    @PostMapping("/fileCotizar")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Message> handleFileUploadCotizar(@RequestParam("file") MultipartFile file) {
        // Valida que el archivo no esté vacío
        if (file.isEmpty()) {
            Message errorMsg = new Message();
            errorMsg.setText("El archivo no puede estar vacío.");
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.badRequest().body(errorMsg);
        }
        return n8nService.sendFileCotizar(file);
    }

    @PostMapping("/messageSisbi")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'INTERNO', 'EXTERNO')")
    public ResponseEntity<Message> getApiSisbi(@RequestBody ChatInputDTO request) {
        String userMessage = request.getChatInput();
        String userSessionId = request.getSessionId();
        return n8nService.getApiSisbi(userMessage, userSessionId);
    }

    @PostMapping("/fileSisbi")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Message> handleFileUploadSisbi(@RequestParam("file") MultipartFile file) {
        // Valida que el archivo no esté vacío
        if (file.isEmpty()) {
            Message errorMsg = new Message();
            errorMsg.setText("El archivo no puede estar vacío.");
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.badRequest().body(errorMsg);
        }
        return n8nService.sendFileSisbi(file);
    }
}
