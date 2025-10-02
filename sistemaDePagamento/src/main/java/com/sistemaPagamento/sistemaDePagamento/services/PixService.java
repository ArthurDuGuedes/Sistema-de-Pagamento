package com.sistemaPagamento.sistemaDePagamento.services;

import org.springframework.stereotype.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.Desktop;
import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import org.springframework.beans.factory.annotation.Value;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.sistemaPagamento.sistemaDePagamento.dto.PixChargeRequest;
import com.sistemaPagamento.sistemaDePagamento.pix.Credentials;

@Service
public class PixService {

    @Value("${CLIENT_ID}")
    private String clientId;

    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    public JSONObject pixCreateEVP() {
        System.out.println(">>> Iniciando pixCreateEVP()");
        System.out.println(">>> CLIENT_ID: " + clientId);
        System.out.println(">>> CLIENT_SECRET: " + (clientSecret != null ? "[REDACTED]" : "NULL!"));

        JSONObject options = configuringJsonObject();
        System.out.println(">>> Opções configuradas: " + options.toString());

        try {
            System.out.println(">>> Criando instância de EfiPay...");
            EfiPay efi = new EfiPay(options);
            System.out.println(">>> Chamando pixCreateEvp...");
            JSONObject response = efi.call("pixCreateEvp", new HashMap<String, String>(), new JSONObject());
            System.out.println(">>> Resposta pixCreateEvp: " + response.toString());
            return response;
        } catch (EfiPayException e) {
            System.out.println(">>> ERRO EfiPayException em pixCreateEVP:");
            System.out.println("Código: " + e.getError());
            System.out.println("Descrição: " + e.getErrorDescription());
            e.printStackTrace(); // ← Importante!
        } catch (Exception e) {
            System.out.println(">>> ERRO GENÉRICO em pixCreateEVP:");
            System.out.println("Mensagem: " + e.getMessage());
            e.printStackTrace(); // ← Mostra a pilha completa
        }
        return null;
    }

    public JSONObject pixCreateCharge(PixChargeRequest pixChargeRequest) {
        System.out.println(">>> Iniciando pixCreateCharge()");
        System.out.println(">>> Dados da requisição: " + pixChargeRequest);

        JSONObject options = configuringJsonObject();
        System.out.println(">>> Opções: " + options.toString());

        JSONObject body = new JSONObject();
        body.put("calendario", new JSONObject().put("expiracao", 3600));
        body.put("devedor", new JSONObject().put("cpf", "12345678909").put("nome", "Francisco da Silva"));
        body.put("valor", new JSONObject().put("original", pixChargeRequest.valor()));
        body.put("chave", pixChargeRequest.chave());

        JSONArray infoAdicionais = new JSONArray();
        infoAdicionais.put(new JSONObject().put("nome", "Campo 1").put("valor", "Informação Adicional1 do PSP-Recebedor"));
        infoAdicionais.put(new JSONObject().put("nome", "Campo 2").put("valor", "Informação Adicional2 do PSP-Recebedor"));
        body.put("infoAdicionais", infoAdicionais);

        System.out.println(">>> Corpo da requisição: " + body.toString());

        try {
            System.out.println(">>> Criando EfiPay...");
            EfiPay efi = new EfiPay(options);
            System.out.println(">>> Chamando pixCreateImmediateCharge...");
            JSONObject response = efi.call("pixCreateImmediateCharge", new HashMap<String, String>(), body);
            System.out.println(">>> Resposta da cobrança: " + response.toString());

            int idFromJson = response.getJSONObject("loc").getInt("id");
            System.out.println(">>> ID da location: " + idFromJson);

            pixGenerateQRCode(String.valueOf(idFromJson));
            return response;

        } catch (EfiPayException e) {
            System.out.println(">>> ERRO EfiPayException em pixCreateCharge:");
            System.out.println("Código: " + e.getError());
            System.out.println("Descrição: " + e.getErrorDescription());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(">>> ERRO GENÉRICO em pixCreateCharge:");
            System.out.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void pixGenerateQRCode(String id) {
        System.out.println(">>> Gerando QR Code para ID: " + id);

        JSONObject options = configuringJsonObject();
        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);

        try {
            System.out.println(">>> Criando EfiPay para QR Code...");
            EfiPay efi = new EfiPay(options);
            System.out.println(">>> Chamando pixGenerateQRCode...");
            Map<String, Object> response = efi.call("pixGenerateQRCode", params, new HashMap<>());
            System.out.println(">>> Resposta QR Code: " + response);

            if (!response.containsKey("imagemQrcode")) {
                System.out.println(">>> ERRO: 'imagemQrcode' não está na resposta!");
                return;
            }

            String base64Image = (String) response.get("imagemQrcode");
            System.out.println(">>> Base64 recebido (trecho): " + base64Image.substring(0, Math.min(50, base64Image.length())) + "...");

            // Remove prefixo data:image/png;base64, se existir
            String imageData = base64Image.contains(",") ? base64Image.split(",", 2)[1] : base64Image;

            File outputfile = new File("qrCodeImage.png");
            System.out.println(">>> Salvando QR Code em: " + outputfile.getAbsolutePath());

            ImageIO.write(
                ImageIO.read(new ByteArrayInputStream(javax.xml.bind.DatatypeConverter.parseBase64Binary(imageData))),
                "png",
                outputfile
            );

            Desktop desktop = Desktop.getDesktop();
            desktop.open(outputfile);
            System.out.println(">>> QR Code aberto com sucesso!");

        } catch (EfiPayException e) {
            System.out.println(">>> ERRO EfiPayException em pixGenerateQRCode:");
            System.out.println("Código: " + e.getError());
            System.out.println("Descrição: " + e.getErrorDescription());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(">>> ERRO GENÉRICO em pixGenerateQRCode:");
            System.out.println("Mensagem: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JSONObject configuringJsonObject() {
        System.out.println(">>> Configurando credenciais...");
        Credentials credentials = new Credentials();

        JSONObject options = new JSONObject();
        options.put("client_id", clientId);
        options.put("client_secret", clientSecret);
        options.put("certificate", credentials.getCertificate());
        options.put("sandbox", credentials.isSandbox());

        System.out.println(">>> client_id definido: " + (clientId != null && !clientId.isEmpty()));
        System.out.println(">>> client_secret definido: " + (clientSecret != null && !clientSecret.isEmpty()));
        System.out.println(">>> sandbox: " + credentials.isSandbox());
        System.out.println(">>> Certificado (trecho): " + (credentials.getCertificate() != null ? 
            credentials.getCertificate().substring(0, Math.min(30, credentials.getCertificate().length())) + "..." : "NULL"));

        return options;
    }
}