package test;
import java.io.*;

public class MyEclipseCode { 
 private static final String LL = "Decompiling this copyrighted software is a violation of both your license agreement and the Digital Millenium Copyright Act of 1998 (http://www.loc.gov/copyright/legislation/dmca.pdf). Under section 1204 of the DMCA, penalties range up to a $500,000 fine or up to five years imprisonment for a first offense. Think about it; pay for a license, avoid prosecution, and feel better about yourself."; 
  
 /** 
  * MyEclipse Standard Subscription---------YE2MY- 
  */
 public static String TYPE_STANDARD_SUBSCRIPTION = "YE2MY-"; 
   
 /** 
  * MyEclipse Professional Subscription-----YE3MP- 
  */
 public static String TYPE_PROFESSIONAL_SUBSCRIPTION = "YE3MP-"; 
  
 /** 
  * MyEclipse Blue Subscription-------------YE3MB- 
  */
 public static String TYPE_BLUE_SUBSCRIPTION = "YE3MB-"; 
  
 /** 
  * MyEclipse for Spring Subscription-------YE3MS- 
  */
 public static String TYPE_FOR_SPRING_SUBSCRIPTION = "YE3MS-"; 
  
 /** 
  *  
  * @param userId  ע����û��� 
  * @param licenseNum �û��� 999Ϊ��� 
  * @param type   MyEclipse ���   MyEclipse Standard Subscription---------YE2MY  
  *            MyEclipse Professional Subscription-----YE3MP 
  *            MyEclipse Blue Subscription-------------YE3MB 
  *            MyEclipse for Spring Subscription-------YE3MS 
  * @return  ע���� 
  */
 public String getSerial( String userId, String licenseNum, String type ) { 
  java.util.Calendar cal = java.util.Calendar.getInstance(); 
  cal.add( 1, 3 ); 
  cal.add( 6, -1 ); 
  java.text.NumberFormat nf = new java.text.DecimalFormat( "000" ); 
  licenseNum = nf.format( Integer.valueOf( licenseNum ) ); 
  String verTime = new StringBuffer( "-" ).append( 
    new java.text.SimpleDateFormat( "yyMMdd" ).format( cal.getTime() ) ).append( "0" ).toString(); 
  String need = new StringBuffer( userId.substring( 0, 1 ) ).append( type ).append( "300" ).append( licenseNum ) 
    .append( verTime ).toString(); 
  String dx = new StringBuffer( need ).append( LL ).append( userId ).toString(); 
  int suf = this.decode( dx ); 
  String code = new StringBuffer( need ).append( String.valueOf( suf ) ).toString(); 
  return this.change( code ); 
 } 
  
   
 private int decode( String s ) { 
  int i; 
  char[] ac; 
  int j; 
  int k; 
  i = 0; 
  ac = s.toCharArray(); 
  j = 0; 
  k = ac.length; 
  while ( j < k ) { 
   i = ( 31 * i ) + ac[j]; 
   j++; 
  } 
  return Math.abs( i ); 
 } 
  
 private String change( String s ) { 
  byte[] abyte0; 
  char[] ac; 
  int i; 
  int k; 
  int j; 
  abyte0 = s.getBytes(); 
  ac = new char[s.length()]; 
  i = 0; 
  k = abyte0.length; 
  while ( i < k ) { 
   j = abyte0[i]; 
   if ( ( j >= 48 ) && ( j <= 57 ) ) { 
    j = ( ( ( j - 48 ) + 5 ) % 10 ) + 48; 
   } 
   else if ( ( j >= 65 ) && ( j <= 90 ) ) { 
    j = ( ( ( j - 65 ) + 13 ) % 26 ) + 65; 
   } 
   else if ( ( j >= 97 ) && ( j <= 122 ) ) { 
    j = ( ( ( j - 97 ) + 13 ) % 26 ) + 97; 
   } 
   ac[i] = (char) j; 
   i++; 
  } 
  return String.valueOf( ac ); 
 } 
  
 public MyEclipseCode() { 
  super(); 
 } 
  
 public static void main( String[] args ) throws IOException { 
  System.out.println( "please input register name:" ); 
  BufferedReader reader = new BufferedReader( new InputStreamReader( System.in ) ); 
  String res = new MyEclipseCode().getSerial( reader.readLine(), "999", MyEclipseCode.TYPE_PROFESSIONAL_SUBSCRIPTION ); 
  System.out.println( "Serial:" + res ); 
 } 
} 
