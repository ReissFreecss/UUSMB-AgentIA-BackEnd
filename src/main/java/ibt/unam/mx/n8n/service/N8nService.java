package ibt.unam.mx.n8n.service;

import ibt.unam.mx.email.EmailDTO;
import ibt.unam.mx.n8n.model.N8nResponseDto;
import ibt.unam.mx.utils.Message;
import ibt.unam.mx.utils.TypesResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class N8nService {


    //Email Trigger
    @Transactional(readOnly = true)
    public ResponseEntity<Message> sendEmail(String to, String subject, String body) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://132.248.32.197:5678/webhook/gmail";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("to", to);
        requestBody.put("subject", subject);
        requestBody.put("body", body);

        try{
            EmailDTO response = restTemplate.postForObject(url, requestBody, EmailDTO.class);

            if(response != null){
                Message msg = new Message();
                msg.setText("Correo enviado exitosamente.");
                msg.setType(TypesResponse.SUCCESS);
                return ResponseEntity.ok(msg);

            }else{
                Message errorMsg = new Message();
                errorMsg.setText("La respuesta fue nula.");
                errorMsg.setType(TypesResponse.ERROR);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
            }
        } catch (Exception e){
            Message errorMsg = new Message();
            errorMsg.setText("Error al llamar a la API: " + e.getMessage());
            errorMsg.setType(TypesResponse.ERROR);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }

    //Chat default
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getApi(String message, String sessionId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://132.248.32.197:5678/webhook/message";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("chatInput", message);
        requestBody.put("sessionId", sessionId);

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

    public ResponseEntity<Message> sendFile(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://132.248.32.197:5678/webhook/file";

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource fileResource  = new ByteArrayResource(file.getBytes()){
                // Sobreescribire el getFile para que el servidor reciba el nombre original
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileResource);
            HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<>(body, headers);

            //Enviar por post
            ResponseEntity<N8nResponseDto> responseEntity = restTemplate.postForEntity(url, requestEntity, N8nResponseDto.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                Message msg = new Message();
                msg.setText(responseEntity.getBody().getOutput());
                msg.setType(TypesResponse.SUCCESS);
                return ResponseEntity.ok(msg);
            } else {
                Message errorMsg = new Message();
                errorMsg.setText("La respuesta del servidor no fue exitosa. Código: " + responseEntity.getStatusCode());
                errorMsg.setType(TypesResponse.ERROR);
                return ResponseEntity.status(responseEntity.getStatusCode()).body(errorMsg);
            }
        } catch (IOException e) {
            Message errorMsg = new Message();
            errorMsg.setText("Error al leer los bytes del archivo: " + e.getMessage());
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        } catch (Exception e) {
            Message errorMsg = new Message();
            errorMsg.setText("Error al llamar a la API de carga: " + e.getMessage());
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }

    //Chat cotizacion
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getApiCotizar(String message, String sessionId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://132.248.32.197:5678/webhook/messageCotizacion";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("chatInput", message);
        requestBody.put("sessionId", sessionId);

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

    public ResponseEntity<Message> sendFileCotizar(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://132.248.32.197:5678/webhook/fileCotizacion";

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource fileResource  = new ByteArrayResource(file.getBytes()){
                // Sobreescribire el getFile para que el servidor reciba el nombre original
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileResource);
            HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<>(body, headers);

            //Enviar por post
            ResponseEntity<N8nResponseDto> responseEntity = restTemplate.postForEntity(url, requestEntity, N8nResponseDto.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                Message msg = new Message();
                msg.setText(responseEntity.getBody().getOutput());
                msg.setType(TypesResponse.SUCCESS);
                return ResponseEntity.ok(msg);
            } else {
                Message errorMsg = new Message();
                errorMsg.setText("La respuesta del servidor no fue exitosa. Código: " + responseEntity.getStatusCode());
                errorMsg.setType(TypesResponse.ERROR);
                return ResponseEntity.status(responseEntity.getStatusCode()).body(errorMsg);
            }
        } catch (IOException e) {
            Message errorMsg = new Message();
            errorMsg.setText("Error al leer los bytes del archivo: " + e.getMessage());
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        } catch (Exception e) {
            Message errorMsg = new Message();
            errorMsg.setText("Error al llamar a la API de carga: " + e.getMessage());
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }

    //Chat sisbi
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getApiSisbi(String message, String sessionId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://132.248.32.197:5678/webhook/messageSisbi";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("chatInput", message);
        requestBody.put("sessionId", sessionId);

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

    public ResponseEntity<Message> sendFileSisbi(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://132.248.32.197:5678/webhook/fileSisbi";

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource fileResource  = new ByteArrayResource(file.getBytes()){
                // Sobreescribire el getFile para que el servidor reciba el nombre original
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", fileResource);
            HttpEntity<MultiValueMap<String, Object>> requestEntity  = new HttpEntity<>(body, headers);

            //Enviar por post
            ResponseEntity<N8nResponseDto> responseEntity = restTemplate.postForEntity(url, requestEntity, N8nResponseDto.class);
            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                Message msg = new Message();
                msg.setText(responseEntity.getBody().getOutput());
                msg.setType(TypesResponse.SUCCESS);
                return ResponseEntity.ok(msg);
            } else {
                Message errorMsg = new Message();
                errorMsg.setText("La respuesta del servidor no fue exitosa. Código: " + responseEntity.getStatusCode());
                errorMsg.setType(TypesResponse.ERROR);
                return ResponseEntity.status(responseEntity.getStatusCode()).body(errorMsg);
            }
        } catch (IOException e) {
            Message errorMsg = new Message();
            errorMsg.setText("Error al leer los bytes del archivo: " + e.getMessage());
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        } catch (Exception e) {
            Message errorMsg = new Message();
            errorMsg.setText("Error al llamar a la API de carga: " + e.getMessage());
            errorMsg.setType(TypesResponse.ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMsg);
        }
    }
}
