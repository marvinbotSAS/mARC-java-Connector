package mARC.Connector;


import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * KmString contains methods to manipulate String as required by the communication protocol of the by the Marvin's server.
 * @author William Kokou Dedzoe
 * @author Patrice Descourt
 *
 */

    public class KmString
    {
        private int idx1;
        private String str;
        public int getIdx1() {
	    return idx1;
	}

	public void setIdx1(int idx1) {
	    this.idx1 = idx1;
	}

	public String getStr() {
	    return str;
	}

	public void setStr(String str) {
	    this.str = str;
	}

	public  int getIdxS() {
	    return idxS;
	}

	public static void setIdxS(int idxS) {
	    idxS = idxS;
	}

	private  int idxS;
        
        public KmString()
        {
        }

        public KmString(String _kmString)
        {
            try
            {
            this.str = new String( _kmString.getBytes(Charset.forName("ISO-8859-15")), "ISO-8859-15" );
            }
            catch(Exception e)
            {
                this.str = _kmString;
            }
        }

        
public String fromGpBinary (MarcResult result, int idx,String src)
{
    boolean     ok;
    int      taille,pos,i;
    String no;
    
    if( src == null ) {return null;}
    if(src.length()<=5)           {return null;}
    pos = idx;
    idx = 0;
    for(i=pos;i<src.length();i++)
     {
       if(src.charAt(i)>32) break;
     }
    pos=i;

    if (pos > src.length() ) return src;

     ok =  (src.charAt(pos)=='<');
     if (ok==false)  return null;
     pos++;
     no = result.getWord(pos, src);
     pos = result.current;
     Integer n = Integer.valueOf(no);
     if ( n == null ) return null;
     taille = n;
     if ((pos+taille+1)>src.length()) return null;
     ok &= (src.charAt(pos+taille+1) =='>');
     ok &= (src.charAt(pos+taille) =='/');
     if (!ok) return null;

     String retour = "";
     for (i = pos; i < pos+taille ; i++)
     {
         retour += src.charAt(i);
     }
     idxS = pos+taille + 2;
     return retour;
}
        
        public void FromGPBinary()
        {
            // Console.WriteLine("str = " + this.str);
            int index = this.str.indexOf(" ");
            // Console.WriteLine("idx" + indexBegin);
            int num2 = Integer.getInteger(this.str.substring(1, index));
            this.str = this.str.substring(index + 1, (index + 1) + num2);
        }
        
         public  String FromString(int idx, String st)
        {

            int db = st.indexOf("<",idx);
            
            if (  db != idx )
            {
                return null;
            }

            // Console.WriteLine("str = " + this.str);
            int index = st.indexOf(" ",idx);
            String slen = st.substring(db+1, index);
            int len = Integer.parseInt( slen  );
            String retour = st.substring(index + 1, (index + 1) + len);
            idxS = (index + 1) + len + 3;
            return retour;
        }       

        public void FromProtocol()
        {
            int num = Integer.getInteger(this.str.substring(1, 2));
            int num2 = Integer.getInteger(this.str.substring(3, 3 + num));
            this.str = this.str.substring(4 + num, (4 + num) + num2);
        }

        public int GetIdx()
        {
            return this.idx1;
        }

        public String GetKMString()
        {
            return this.str;
        }

        public String GetNextString()
        {
            int num;
            int length = this.str.length();
            if (this.idx1 < 0)
            {
                this.idx1 = 0;
            }
            if (this.idx1 >= length)
            {
                this.idx1 = -1;
                return "";
            }
            char[] chArray = this.str.toCharArray();
            while (chArray[this.idx1] == ' ')
            {
                this.idx1++;
                if (this.idx1 >= length)
                {
                    this.idx1 = -1;
                    return "";
                }
            }
            if (chArray[this.idx1] == '<')
            {
                this.idx1++;
                num = this.idx1;
                while (chArray[num] != ' ')
                {
                    num++;
                }
                int num3 = Integer.getInteger(this.str.substring(this.idx1, num));
                this.idx1 = (num + num3) + 3;
                if (this.idx1 >= length)
                {
                    this.idx1 = -1;
                }
                if (num3 == 0)
                {
                    return "";
                }
                num++;
                return this.str.substring(num, num + num3);
            }
            num = this.idx1;
            while (chArray[num] != ' ')
            {
                num++;
            }
            int startIndex = this.idx1;
            this.idx1 = num;
            return this.str.substring(startIndex, num);
        }

        public void SetKMString(String _str)
        {
            this.idx1 = 0;
            this.str = _str;
        }

        public String[] split()
        {
            List<String> list = new ArrayList<String>();
            this.idx1 = 0;
            int index = this.str.indexOf("<");
            int indexNull = str.toLowerCase().indexOf("null");
            if (index != -1 || indexNull != -1)
            {
                if (indexNull > 0 && indexNull < index)
                {
                    list.add("NULL");
                    indexNull = str.toLowerCase().indexOf("null", indexNull + 4);
                }
                int num2 = this.str.indexOf("/>");
                if (num2 != -1)
                {
                    while ((num2 != -1) && (index != -1) || indexNull != -1)
                    {
                        if (indexNull > 0 && indexNull < index || ( index == -1 && indexNull != -1 ) )
                        {
                            list.add("NULL");
                            this.str = this.str.substring(indexNull + 4);
                        }
                        else if (this.str.charAt(index + 1) == '0')
                        {
                            list.add("");
                            this.str = this.str.substring(num2 + 2);
                        }
                        else
                        {
                            String tmp = null;
                            
                            try
                            {
                        	tmp = this.str.substring(index + 1,  num2 );
                            }
                            catch(Exception e)
                            {
                        	System.out.println(tmp);
                        	System.out.println(index);
                        	System.out.println(num2);
                            }
                            index = tmp.indexOf(" ");
//                            while ((indexBegin < tmp.length()) && ( indexBegin > 0 && indexBegin < tmp.length() && tmp[indexBegin] == ' '))
//                            {
                                index++;
                                tmp = tmp.substring(index);
//                            }

                            list.add(tmp);
                            this.str = this.str.substring(num2 + 2);
                        }

                       
                        index = this.str.indexOf("<");
                        num2 = this.str.indexOf("/>");
                        indexNull = str.toLowerCase().indexOf("null");
                    }
                    Object[] objArray = list.toArray();
                    String[] strArray = new String[objArray.length];
                    int num3 = 0;
                    for (Object obj2 : objArray)
                    {
                        strArray[num3++] = (String) obj2;
                    }
                    return strArray;
                }
            }
            return null;
        }

    public static String stringToGpBinary(String str, boolean error)
    {

        error = false;
        String out = "";
        String tmp;
        String st = str.replaceAll("[\r\n]+", "");
        String[] toGPBinary = null;
       // System.out.println("en entree : "+st);
        
        int s = st.indexOf("(");
        int e = st.indexOf(")");

        int previous_e;
        
        int dg = 0,fg = 0;
        int dv = 0, fv = 0;
        
        String stt;
        out = st.substring(0, s+1);
       // System.out.println("premier out = "+out);
        while (s != -1 && e != -1)
        {
            if ( e != s + 1)
            {
                tmp = st.substring( s + 1 ,e );
                // ordre traitement 
                // - guillement
                // - virgule
                dg = tmp.indexOf("\"");
                fg = tmp.indexOf("\"", dg+1 );
                dv = tmp.indexOf(",");
                while (   dg != -1 || dv != -1 )
                { 
                    if ( dg != -1 )
                    {   if ( dv != -1 && dg < dv ) // on a trouvé une chaine de caractère
                        {
                            fg = tmp.indexOf("\"", dg+1);
                            stt = tmp.substring( dg+1, fg );
                            stt = trim(stt);
                            out+= toGpBinary( stt );
                            // on cherche la prochaine virgule
                            fv = tmp.indexOf(",", fg+1);
                            if ( fv != -1 )
                            {
                            tmp = tmp.substring(fv+1);
                            }
                            if ( fg == -1 )
                            {
                                error = true;
                                break;
                            }
                        }
                        else if ( dv != -1 )// la virgule est en premier
                        {   
                            // on extrait la chaine avant les guillemets
                            stt = tmp.substring(0, dg);
                            // on la splitte par rapport aux virgules
                            stt =trim(stt);
                            toGPBinary = stt.split(",");
                            for (int i = 0; i < toGPBinary.length;i++)
                            {
                              toGPBinary[i] = trim (toGPBinary[i] );
                              if( toGPBinary[i].equalsIgnoreCase("NULL") || toGPBinary[i].equalsIgnoreCase("NULL") )
                              {
                                  out += toGPBinary[i];
                              }
                              else
                              {
                                  out += toGpBinary(toGPBinary[i]);
                              }
                                if ( i!= toGPBinary.length - 1)
                                {
                                    out +=", " ;
                                }
                            }
                            tmp = tmp.substring(dg);
                        }
                        tmp = trim(tmp);
                        if ( tmp.startsWith("\"") && tmp.endsWith("\""))
                        {
                            int last = tmp.lastIndexOf("\"");
                            int next = tmp.indexOf("\"",1);
                            if ( next == last ) // il reste une chaine de caractères
                            {
                                tmp = tmp.substring(1, last);
                                tmp = trim(tmp);
                                if ( !tmp.isEmpty() )
                                {
                                    if ( !out.endsWith("(") )
                                    {
                                        out+= ", "+toGpBinary( tmp );
                                    }
                                    else
                                    {
                                        out += toGpBinary( tmp );
                                    }
                                }
                                break;
                            }
                        }
                        // on passe au suivant
                        //
                        dg = tmp.indexOf("\"");
                        fg = tmp.indexOf("\"", dg + 1);
                        dv = tmp.indexOf(",");
                        if ( (dg != -1 && fg != -1) ||  dv != -1  )
                        {
                        out +=", ";
                        }
                        else // plus de guillemets ni de virgules ou pb de terminaison
                        {
                            tmp = trim(tmp);
                            if ( (dg == -1 && fg != -1) || (dg != -1 && fg == -1) )
                            {
                                out += toGpBinary(tmp);
                                error = true;
                                break;
                            }
                            
                            if ( !tmp.isEmpty() )
                            {
                                out +=", "+ toGpBinary(tmp);
                            }
                        }
                    }
                    else // pas de guillements ou il n'en reste plus
                    {
                       toGPBinary = tmp.split(",");
                       for (int i = 0; i < toGPBinary.length;i++)
                        {
                            // traitement du cas NULL ou VOID 
                            toGPBinary[i] = trim (toGPBinary[i] );
                            if( toGPBinary[i].equalsIgnoreCase("NULL") || toGPBinary[i].equalsIgnoreCase("NULL") )
                            {
                                out += toGPBinary[i];
                            }
                            else
                            {
                                out += toGpBinary(toGPBinary[i]);
                            }
                            if ( i != toGPBinary.length - 1)
                            {
                                out += ", ";
                            }
                        } 
                       // plus de guillements ni de virgules
                       tmp = trim(tmp);
                       if ( toGPBinary.length == 0 && !tmp.isEmpty() )
                       {
                           out += toGpBinary(tmp);
                       }
                      break;
                    }
                }
            }
            out += "); ";
            previous_e = e;
            s = st.indexOf("(", e);
            e = st.indexOf(")", s);
            if ( s != -1 && e != -1)
            {
                out += st.substring(previous_e+1, s+1);
            }
        }
        
        return out;
    }
        public void toGpBinary()
        {
            while (str.startsWith(" ") || str.endsWith(" "))
            {
                str = str.trim();
            }
            Charset encoding = Charset.forName("ISO-8859-15"); //"ISO-8859-15"
            byte[] bytes = str.getBytes(encoding);
            try
            {
            this.str =  "<"+ bytes.length+ " "+ this.str+ "/>";
            }
            catch (Exception e)
            {
                
            }
            
        }

        public static String toGpBinary(String str)
        {
            while (str.startsWith(" ") || str.endsWith(" "))
            {
                str = str.trim();
            }
            Charset encoding = Charset.forName("ISO-8859-15");
            ByteBuffer bytes = encoding.encode(str);
            return "<"+str.getBytes(encoding).length+" "+bytes.toString()+"/>" ;
        }

        public void toProtocol()
        {
            Charset encoding = Charset.forName("ISO-8859-15");
            ByteBuffer bytes = encoding.encode(str);
            int length = str.getBytes(encoding).length;
            int num2 = String.valueOf(length).length();
            this.str = "#"+num2+"#"+ length+ " "+encoding.decode(bytes).toString();
        }

        @Override
        public String toString()
        {
            return this.str;
        }

        public String ToString()
        {
            return this.str;
        }

        public static String trim(String s)
        {
            String str = s;
            while (str.startsWith(" ") || str.endsWith(" "))
            {
                str = str.trim();
            }
            return str;
        }

        public static String normalizeString(String _str, int buffersize)
        {
            Charset Destencoding = Charset.forName("ISO-8859-15");
            Charset SrcEncoding = Charset.forName("UTF-8");
            ByteBuffer srcbytes = SrcEncoding.encode(_str);
            CharBuffer inputBuffer = SrcEncoding.decode(srcbytes);
            ByteBuffer outputBuffer = Destencoding.encode(inputBuffer);
            byte[] bytes = outputBuffer.array();
            List<Byte> l = new ArrayList<Byte>();
            /*
             * ISO 8859-15  attention !!!!!
             * important : on remplace "oe" en "o""e" idem pour "ae"
             */
            for (int i = 0; i < bytes.length; i++)
            {
                if (bytes[i] == 0xBD) // oe
                {
                    l.add((byte)'o');
                    l.add((byte)'e');
                }
                else if (bytes[i] == 0xBC) // OE
                {
                    l.add((byte)'O');
                    l.add((byte)'E');
                }
                else if (bytes[i] == 0xE6) // ae
                {
                    l.add((byte)'a');
                    l.add((byte)'e');
                }
                else if (bytes[i] == 0xC6) // AE
                {
                    l.add((byte)'A');
                    l.add((byte)'E');
                }
                else
                {
                    l.add(bytes[i]);
                }
                

            }
            byte[] b = new byte[ l.size() ];
            for (int i = 0; i < l.size() ;i++)
            {
                b[i] = l.get(i).byteValue();
            }

           buffersize = l.size();
            
           String str = null;
           
           try
           {
               str= new String( b, "8859_15" ) ;
           }
           catch (Exception e)
           {
               
           }
            return str;
        }

        public static String normalizeWikiString(String s, int buffersize)
         {
            Charset Destencoding = Charset.forName("ISO-8859-15");
            Charset SrcEncoding = Charset.forName("UTF-8");
            ByteBuffer srcbytes = SrcEncoding.encode(s);
            CharBuffer inputBuffer = SrcEncoding.decode(srcbytes);
            ByteBuffer outputBuffer = Destencoding.encode(inputBuffer);
            byte[] bytes = outputBuffer.array();
            List<Byte> l = new ArrayList<Byte>();
            /*
             * ISO 8859-15  attention !!!!!
             * important : on remplace "oe" en "o""e" idem pour "ae"
             */
            for (int i = 0; i < bytes.length; i++)
            {
                if (bytes[i] == 0xBD) // oe
                {
                    l.add((byte)'o');
                    l.add((byte)'e');
                }
                else if (bytes[i] == 0xBC) // OE
                {
                    l.add((byte)'O');
                    l.add((byte)'E');
                }
                else if (bytes[i] == 0xE6) // ae
                {
                    l.add((byte)'a');
                    l.add((byte)'e');
                }
                else if (bytes[i] == 0xC6) // AE
                {
                    l.add((byte)'A');
                    l.add((byte)'E');
                }
                else
                {
                    l.add(bytes[i]);
                }
                

            }
            byte[] b = new byte[ l.size() ];
            for (int i = 0; i < l.size() ;i++)
            {
                b[i] = l.get(i).byteValue();
            }

           buffersize = l.size();
            
           String str = null;
           
           try
           {
               str= new String( b, "8859_15" ) ;
           }
           catch (Exception e)
           {
               
           }
            return str;
        }
        
        public static String cleanText(String s)
        {
            //s = suppBaliseFast("{|","|}", s);
            //s = suppBaliseFast("{{","}}", s);
            // s = s.replace("#REDIRECTION","");
            //   s = s.replace("#REDIRECT","");
            //( "(<my:String>).*?(</my:String>)" , "$1whatever$2" );
            s = s.replace("\n", "");

            s = s.replace("{|", "<clean>");
            s = s.replace("|}", "<cleanend>");

            s = s.replace("{{", "<clean2>");
            s = s.replace("}}", "<cleanend2>");

            String pattern = "(&lt;).*?(&gt;)";
            s = s.replaceAll(pattern, "$1 $2");
            pattern = "(<clean>).*?(<cleanend>)";
            s = s.replaceAll(pattern, "$1 $2");
            pattern = "(<clean2>).*?(<cleanend2>)";
            s = s.replaceAll(pattern, "$1 $2");
            //s = s.replaceAll("(\\{|).*?(\\|})", "$1 $2");
            s = s.replaceAll("<clean>", " ");
            s = s.replaceAll("<cleanend>", " ");
            s = s.replaceAll("<clean2>", " ");
            s = s.replaceAll("<cleanend2>", " ");
            //s = s.replaceAll("({|).*?(|})", "$1 $2");

            //s = s.replaceAll("({).*?(})", "$1 $2");
            //s = s.replaceAll("({).*?(\\})", "$1+$2");
            // s = s.replaceAll("(\\{\\{).*?(\\}\\})", "$1 $2");
            pattern = "(&lt;).*?(&gt;)";
            s = s.replaceAll( pattern, " ");
            //s = s.replaceAll("(\\{).*?(\\})", " ");

            //s = s.replaceAll("(\\{|).*?(|\\})", " ");
            //s = s.replaceAll("({{).*?(}})", " ");
            //s = s.replaceAll("(&quot;).*?(&quot;)", "beurkbeurk");
            s = s.replace("'''", " ");
            s = s.replace("’", "'");
              


            s = s.replace("image:", " ");
            s = s.replace("Image:", " ");
            s = s.replace("thumb ", " ");
            s = s.replace("class=", " ");
            s = s.replace("style=", " ");
            s = s.replace("align=", " ");
            s = s.replace("id=", " ");
            s = s.replace("class=", " ");
            s = s.replace("amp;lt;", "");
            s = s.replace("|", " ");
            s = s.replace("*", " ");
            s = s.replace("[[", " ");
            s = s.replace("]]", " ");
            s = s.replace("]", " ");
            s = s.replace("[", " ");
            s = s.replace("????", " ");
            s = s.replace("???", " ");
            s = s.replace("??", " ");
            s = s.replace(" #39;", " ");
            s = s.replace(" #91;", " ");
            s = s.replace(" amp;gt", " ");
            s = s.replace("&amp;", " ");
            s = s.replace("< revision>", " ");
            s = s.replace("&quot;", " ");
            s = s.replace("</text>", " ");
            s = s.replace("Modéle:", " ");
            s = s.replace("Catégorie:", " ");
            s = s.replace("Wikipédia:", " ");
            s = s.replace("== Définition ==", " ");
            s = s.replace("{{Infobox", " ");
            s = s.replace("{{homonymie", " ");
            s = s.replace("{{Homonymie", " ");
            s = s.replace("Lumière sur", " ");
            s = s.replace("Voir homonymes", " ");
            s = s.replace("voir homonymes", " ");
            s = s.replace("Voir famille", " ");
            s = s.replace("Taxobox Début", " ");
            s = s.replace("Taxobox Fin", " ");
            s = s.replace("Taxobox ", " ");
            s = s.replace("Fichier:", " ");
            s = s.replace("{{", " ");
            s = s.replace("}}", " ");
            s = s.replace("/", " ");
            s = s.replace("\\", " ");
            s = KmString.trim(s);
            return s;
        }
    }


