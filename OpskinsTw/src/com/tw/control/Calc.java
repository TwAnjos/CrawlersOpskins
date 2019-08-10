/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tw.control;

/**
 *
 * @author Tw
 */
public class Calc {

    public float checalucro(float sop, float sugerido, float taxasteam) {
        sugerido = sugerido-(sugerido*taxasteam/100);
        
        if(sop > sugerido){
            return 0;
        }else{
            return sugerido-sop;
        }
    }

    public float lucroreal(float lucro, float dollaratual) {
        if (lucro > 0) {
            return lucro * dollaratual;
        } else {
            return 0;
        }
    }

}
