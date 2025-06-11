package ibt.unam.mx.n8n.service;

import ibt.unam.mx.n8n.model.N8nResponseDto;
import ibt.unam.mx.utils.Message;
import ibt.unam.mx.utils.TypesResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class N8nService {

    @Transactional(readOnly = true)
    public ResponseEntity<Message> getApi(String message) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://10.0.6.208:5678/webhook/message";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("chatInput", message);

        try {
            N8nResponseDto response = restTemplate.postForObject(url, requestBody, N8nResponseDto.class);

            if (response != null) {
                Message msg = new Message();
                msg.setText(response.getOutput());
                msg.setType(TypesResponse.SUCCESS);
                return ResponseEntity.ok(msg);
            } else {
                Message errorMsg = new Message();
                errorMsg.setText("La respuesta fue nula.");
                errorMsg.setType(TypesResponse.ERROR);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
            }

        } catch (Exception e) {
            Message errorMsg = new Message();
            errorMsg.setText("Error al llamar a la API: " + e.getMessage());
            errorMsg.setType(TypesResponse.ERROR);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }
}
