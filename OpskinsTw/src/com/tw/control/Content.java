/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tw.control;

import com.tw.model.Items;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Tw
 */
public class Content {

    private String memo2 = "findopskins.txt";
    private String localsavetxt = "melhoresofertas.txt";
    private float dollarhj = 0;
    private float taxasteam = 10f;
    private static Document doc = null;
    private float lucrominimo = 4;
    private String savetxt = "";

    public Content(Document doc) {
        this.doc = doc;
    }

    // aqui filter pages
    public ArrayList getPages(Document doc, float dollarhj, float lucrominimo) {
        this.dollarhj = dollarhj;
        this.lucrominimo = lucrominimo;
        Elements elements = doc.getElementsByClass("featured-item");//aqui faz um limpeza jogando fora o que não é preciso. 
        if (elements.isEmpty()) {
            System.err.println("Vazio!\n" + elements);
        }
        //System.err.println(elements);
        return getQuestContent(elements);
    }

//pega varios elementos com a mesma tah caso houver
    private ArrayList getQuestContent(Elements elements) {
        
        Date date = GregorianCalendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat();
        savetxt += format.format(date);
        savetxt += System.getProperty("line.separator");
        savetxt += System.getProperty("line.separator");

        ArrayList<Items> items = new ArrayList<>();

        for (Element element : elements) {
            Items it = new Items();
            String nome, link, sop = "0", sugerido = "0";

            nome = filterElements(element.toString(), "<a class.*/a>");
            nome = cleanHtmlDocument(nome);

            link = filterElements(element.toString(), ".*href=\"https://.*\">");
            link = cleanHrefDocument(link);
            link = link.replaceAll("amp;", "");

            sop = element.getElementsByClass("item-amount").toString();
            if (sop.isEmpty()) {
                sop = "0";
                System.err.println("Erro!");
                System.err.println("Nome: " + nome);
                System.err.println("Link: " + link);
                System.err.println("Sop: " + sop);
                System.err.println("Sugerido:" + sugerido);
                System.err.println("Dollar: " + dollarhj + "\n\n");
            } else {
                sop = sop.replaceAll(",", "");
                sop = cleanHtmlDocument(sop);
            }

            float fsop = 0;
            try {
                fsop = (Integer.parseInt(sop));
            } catch (NumberFormatException numberFormatException) {
                System.err.println("Erro!");
                System.err.println("Nome: " + nome);
                System.err.println("Link: " + link);
                System.err.println("Sop: " + sop);
                System.err.println("FSop: " + fsop);
                System.err.println("Sugerido:" + sugerido);
                System.err.println("Dollar: " + dollarhj + "\n\n");
            }

            sugerido = filterElements(element.toString(), "Suggested Price:.*</a></span>");

            if (sugerido == null) {
                sugerido = "0";
                System.err.println("Erro!");
                System.err.println("Nome: " + nome);
                System.err.println("Link: " + link);
                System.err.println("Sop: " + sop);
                System.err.println("FSop: " + fsop);
                System.err.println("Sugerido:" + sugerido);
                System.err.println("Dollar: " + dollarhj + "\n\n");
            } else {
                sugerido = sugerido.replaceAll("Suggested Price:", "");
                sugerido = sugerido.replaceAll(",", "");
                sugerido = cleanHtmlDocument(sugerido);
            }
            float fsugerido = Float.parseFloat(sugerido);

            Calc calc = new Calc();

            it.setNome(nome);
            it.setLink(link);
            it.setSop(fsop);
            it.setSugerido(fsugerido);
            it.setLucroop(calc.checalucro(it.getSop(), it.getSugerido(), taxasteam));
            it.setLucroreais(calc.lucroreal(it.getLucroop(), dollarhj));
            it.setDollaratual(dollarhj);

            if (it.getLucroreais() > lucrominimo) {
                savetext(it);
                items.add(it);
            }
        }

        try {
            UtilsArquivos.salvar(localsavetxt, savetxt, false);
        } catch (IOException ex) {
            Logger.getLogger(Content.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Não conseguiu salvar arquivo txt.");
        }

        //ordenar lista decreçente com lucroop
        //sem ordenação por enquanto =P
        return items;
    }

    //faz um filtro mais fino com expreções regulares
    private String filterElements(String completeContent, String regex) {
        String content = null;
        Matcher matcher = Pattern.compile(regex).matcher(completeContent);
        while (matcher.find()) {
            content = matcher.group();
        }
        return content;
    }

    //faz uma limpeza removando sujeiras como tags
    private String cleanHtmlDocument(String completeContent) {
        try {
            completeContent = StringEscapeUtils.unescapeHtml4(completeContent);
            completeContent = completeContent.replaceAll("<[^>]*>", "");
            completeContent = completeContent.replaceAll("\\s+", " ");
            completeContent = completeContent.trim();
            return completeContent;
        } catch (Exception e) {
            System.out.println("Error cleanHtmlDocument  ==>> " + completeContent);
            return completeContent = "0.000000";
        }
    }

    public static String cleanHrefDocument(String content) {
        content = content.replaceAll("<a class=\"market-name market-link\" href=\"", "");
        content = content.replaceAll("\">", "");
        return content;
    }
    
    private void savetext(Items it){
        savetxt += "Nome: " + it.getNome();
                savetxt += System.getProperty("line.separator");
                savetxt += "\nLink: " + it.getLink();
                savetxt += System.getProperty("line.separator");
                savetxt += "\n$Op: " + it.getSop();
                savetxt += System.getProperty("line.separator");
                savetxt += "\nSugerido: " + it.getSugerido();
                savetxt += System.getProperty("line.separator");
                savetxt += "\nLucro $Op: " + it.getLucroop();
                savetxt += System.getProperty("line.separator");
                savetxt += "\nLucro R$: " + it.getLucroreais();
                savetxt += System.getProperty("line.separator");
                savetxt += "\nDollar: " + it.getDollaratual();
                savetxt += System.getProperty("line.separator");
                savetxt += System.getProperty("line.separator");
    }

}
