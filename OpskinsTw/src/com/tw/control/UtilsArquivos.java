/**
 * Funções utilitárias para trabalhar com arquivos autor: Tw arquivo:
 * UtilsArquivos.java 15/11/2012
 */
package com.tw.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Thiago
 */
public class UtilsArquivos {
    //Salva o conteúdo de uma variável em um arquivo

    public static void salvar(String arquivo, String conteudo, boolean adicionar)
            throws IOException {

        FileWriter fw = new FileWriter(arquivo, adicionar);
        fw.write(conteudo);
        fw.close();
    }

    //Carrega o conteúdo de um arquivo em uma String, se o aquivo não existir, retornará null.
    public static String carregar(String arquivo)
            throws FileNotFoundException, IOException {

        File file = new File(arquivo);
        if (!file.exists()) {
            return null;
        }
        BufferedReader br = new BufferedReader(new FileReader(arquivo));
        StringBuilder bufSaida = new StringBuilder();
        String linha;
        while ((linha = br.readLine()) != null) {
            bufSaida.append(linha + "\n");
        }
        br.close();
        return bufSaida.toString();
    }
}
