/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tw.control;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Tw
 */
public class Connect {

    static Document doc = null;
    
    String local = "findopskins.txt";
    String localsavetxt = "melhoresofertas.txt";
    
    String urlteste = "https://opskins.com/index.php?loc=shop_browse";

    public Connect(Document doc) {
        this.doc = doc;
    }

    public Connect() {
    }

    public ArrayList connect(String url,float dollarhj, float lucrominimo) {
        trustEveryone();//ignora certificado ssl
        //System.setProperty("https://opskins.com","/path/to/web2.uconn.edu.jks");
        System.setProperty(url,"/path/to/web2.uconn.edu.jks");
        try {
            //esse código funcionou, mas acredito que devido aos meus testes o site em que fazia pesquisas me bloqueou temporareamente.
            //apartir de agora vou testar em arquivos salvos em txt.
            doc = Jsoup
                    .connect(urlteste)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2")
                    //.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .timeout(7 * 1000)
                    .followRedirects(true)
                    .maxBodySize(1024 * 1024 * 2) //2Mb
                    .ignoreContentType(true)//for download xml, json, etc
                    
                    .get();
        } catch (IOException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("erro ao conectar com a url. com.tw.control > connect");
            JOptionPane.showMessageDialog(null , "Erro com.tw.control.connect");
        }

        String txt = doc.toString();

        try {
            UtilsArquivos.salvar(local, txt, false);
        } catch (IOException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Erro ao salvar arquivo txt na pasta raiz");
        }
        
        Content content = new Content(doc);
        return content.getPages(doc, dollarhj, lucrominimo);
    }

    //aplica certificados ssl ou ignora erros
    private static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) { // nunca deveria acontecer
            e.printStackTrace();
        }
    }

    public ArrayList usetxt(float dollarhj, float lucrominimo) {
        //simulando pagina html
        String txtteste;
        try {
            txtteste = UtilsArquivos.carregar(local); //busca o conteudo em um arquivo local
            doc = Jsoup.parse(txtteste);  //converte arquivo txt em document
        } catch (IOException ex) {
            Logger.getLogger(Content.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Não encontrou o arquivo");
        }
        Content content = new Content(doc);
        return content.getPages(doc, dollarhj, lucrominimo);
    }

}
