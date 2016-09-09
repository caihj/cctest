package org.berbon_agentinner_web;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import  org.springframework.core.OrderComparator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Unit test for simple App.
 */
public class AppTest 
     {

         public static void main(String [] args){
             Calendar cal2 = Calendar.getInstance();
             cal2.add(Calendar.DATE, 1);
             Date endD = cal2.getTime();
             System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(endD));
         }

}

