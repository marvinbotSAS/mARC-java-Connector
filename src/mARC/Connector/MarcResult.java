package mARC.Connector;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * MarcResult class contains methods to manipulate to parsing the returns by the Marvin's server.
 * @author William Kokou Dedzoe
 * @author Patrice Descourt
 *
 */

   @SuppressWarnings("all")
    public class MarcResult
    {
        public boolean _analyse;
	public ArrayList _columns;
        public ArrayList _data;
        public ArrayList _lines;
        public ArrayList _names;
        public ArrayList _sizes;
        public ArrayList _types;
        public boolean mError;
        public String mErrorMessage;
        public int mScriptSize;
        
        public String session_name;
        
        public String session_id;
        private String toReceive;

        public  int current;
        
        private KmString kmString;
        
        public MarcResult()
        {
        this.kmString = new KmString();
            this._lines = new ArrayList();
            this._columns = new ArrayList();
            this._data = new ArrayList();
            this._names = new ArrayList();
            this._types = new ArrayList();
            this._sizes = new ArrayList();
            this.session_id = "-1";
        }

        public MarcResult(String ret)
        {
        this.kmString = new KmString();
            this._lines = new ArrayList();
            this._columns = new ArrayList();
            this._data = new ArrayList();
            this._names = new ArrayList();
            this._types = new ArrayList();
            this._sizes = new ArrayList();
            this.analyze(ret);
        }

        public String getWord(int idx, String val)
        {
            current = idx;
            String retour = "";
            int       pos,i;

            int Count = val.length();
            pos=idx;
            if (pos>= Count ) 
            {
                current = 0;
                return retour;
            }
            if (pos<0) 
            {
                current = 0;
                return retour;
            }
            //trouve le premier caractère non vide et sa position
            int c = ' ';
            for(i = pos; i< Count; i++)
            {
                try
                {
                    c = val.charAt(i);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
                if ( c  != 32) 
                {
                    break;
                }
            }
            pos=i;
            if (pos>=Count) 
            {
                current = 0;
                return retour;
            }
            for(i=pos;i<Count;i++)
            {
                try
                {
                c = val.charAt(i);
                }
                catch(Exception e)
                {
                    System.out.println(e.getMessage());
                }
                if( c == 32) 
                {
                    break;
                }
                retour+= val.charAt(i);
            }
            current=i+1;
            
            return retour;
        }
        
        
public void analyse(String ret)
{

   clear();
 mError = false;
 mErrorMessage = "";
 mScriptSize = 0;

 String       session, erreur;

 byte[] b;
 try
 {
  b = ret.getBytes("ISO-8859-15");
  toReceive = new String( b , "ISO-8859-15");
 }
 catch(Exception e)
 {
     
 }

  current = 0;
  int len = toReceive.length();
  session_id = getWord(current, toReceive); // gets the session 
  
  //   anciennement :  session_id = Integer.parseInt(session);
  erreur = getWord(current, toReceive); // gets the error
  if (erreur.equals("0") )
   {
     //gets the error message
     mErrorMessage = "code ";
     erreur = getWord(current,toReceive); //code d'erreur
     mErrorMessage += erreur;
     mErrorMessage += " in "+toReceive.substring(current);
     mError = true;
     return;
   }

  int idx = current;
   analyseLine(idx);
   if (idx == 0)     return;
   if (idx == -1)    return;
   if (idx > len)    return;
   
   idx = current;
   while (idx <= len && idx!= -1 )
    {
      //the next space
      while (toReceive.charAt(idx) == 32 )
      {
        idx++ ;
        if (idx > len) return;
      }
 
     if (toReceive.charAt(idx) != ';' )  return;
     idx++;
     if (idx >= len - 1) return;
     analyseLine(idx);
     idx = current;
     if (idx == 0) return;

   }
}
  
public  void  analyseLine(int idx)
{

  ArrayList list = new ArrayList();
  ArrayList list2 = new ArrayList();
  ArrayList list3 = new ArrayList();
  this._types.add(list);
  this._names.add(list2);
  this._sizes.add(list3);
  
  Hashtable<String, ArrayList<String> > dictionary = new Hashtable();
  
  String       val,tmp;
  int            i,j,size,idxtmp;

  String lines = getWord(idx, toReceive);
  int rows = Integer.parseInt(lines);
  _lines.add( rows );

  String columns = getWord(current,toReceive);
  int cols = Integer.parseInt(columns);
  _columns.add( cols );

  // Reading of the types, sizes and columns names 
  for (i = 0; i < cols ; i++)
   {
     val = getWord(current,toReceive);      //gets the type
     list.add(Connector.getKmTypeLabel()[ Integer.parseInt(val)]);
     //
     val = getWord(current,toReceive);   //gets the size
     list3.add(Integer.parseInt(val));
     val = getWord(current,toReceive);   //gets names
     list2.add(new String(val).toLowerCase() );

   }
  //Reading of data
   ArrayList list4 = new ArrayList();
   for (j = 0; j < cols;j++)
   {
     
     dictionary.put( (String) list2.get(j), list4);
     if ( j != cols - 1)
     {
         list4 = new ArrayList();
     }
   }

   
   idx = current;
     for (i = 0; i < rows;i++)
     {
         for (j = 0; j < cols;j++)
         {
            idxtmp = idx;
            tmp = getWord (idxtmp,toReceive);

            if (tmp.equals("NULL") || tmp.equals("VOID") )
              {
                 idx = current;
              }
             else
              {
                val = kmString.fromGpBinary(this,idx, toReceive);

                if ( val != null)
                {
                    tmp = val;
                }
                current = kmString.getIdxS();
                
                idx = current;
              }

            dictionary.get( (String) list2.get(j) ).add(tmp);
         }

     }
     
     _data.add( dictionary );
     mScriptSize++;
}

        public void analyseLine(String line)
        {
            if ( (line!=null || !line.equals("") ) && (line.length() != 0))
            {
                ArrayList list = new ArrayList();
                ArrayList list2 = new ArrayList();
                ArrayList list3 = new ArrayList();
                Hashtable<String, ArrayList<String> > dictionary = new Hashtable();
                String[] strArray;
                int num3 = -1;
                int num4 = -1;
                while (line.startsWith(" ") || line.endsWith(" "))
                {
                    line = line.trim();
                }
                int index = line.indexOf('<');
                // Be careful in case we have column with "null" value 
                int indexNull = line.toLowerCase().indexOf("null");
                if (indexNull != -1)
                {

                    if (indexNull < index)
                        index = indexNull;
                }
                if (index == -1)
                {

                    strArray = line.split(" ");
                    num3 = (new Integer((strArray[0]))).intValue();
                    num4 = (new Integer((strArray[1]))).intValue();
                    this._lines.add(num3);
                    this._columns.add(num4);
                    this._types.add(list);
                    this._names.add(list2);
                    this._sizes.add(list3);
                    
                    if (num3 == 0 && num4 == 0)
                    {
                        this._data.add(dictionary);
                        this.mScriptSize++;
                        return;
                    }
                    strArray = line.split(" ");
                }
                else
                {
                    index--;
                    strArray = line.substring(0, index).split(" ");
                }

                if (num3 == -1 && num4 == -1)
                {
                    num3 = (new Integer((strArray[0]))).intValue();
                    num4 = (new Integer((strArray[1]))).intValue();
                    this._lines.add(num3);
                    this._columns.add(num4);
                    this._types.add(list);
                    this._names.add(list2);
                    this._sizes.add(list3);
                }

                int num5 = 2;
                // Gets informations about the type and names of variables of current line 
                for (int i = 0; i < num4; i++)
                {
                    int num7 = (new Integer((strArray[num5]))).intValue();
                    num5++;
                    list.add(Connector.getKmTypeLabel()[num7]);
                    num7 = (new Integer((strArray[num5]))).intValue();
                    list3.add(num7);
                    num5++;
                    list2.add(strArray[num5]);
                    num5++;
                }

                if (index != -1)
                {
                    strArray = new KmString(line.substring(index)).split();

                    for (index = 0; index < list2.size(); index++)
                    {
                        ArrayList<String> list4 = new ArrayList();
                        for (int j = 0; j < num3; j++)
                        {
                            num5 = index + (j * num4);
                            list4.add(strArray[num5]);
                        }
                        dictionary.put((String)list2.get(index), list4);
                    }
                }

                this._data.add(dictionary);
                this.mScriptSize++;
            }
        }

        public void analyze(String ret)
        {
            String[] strArray;
            int num2 = 0;
            this.mError = false;
            this.mErrorMessage = "";
            this.mScriptSize = 0;
            this.toReceive = ret;
            while (this.toReceive.startsWith(" ") || this.toReceive.endsWith(" "))
            {
                this.toReceive = this.toReceive.trim();
            }
            String toReceive = this.toReceive;
            //  Drop all the lines with 0 1 0 0 which are not useful


            int indexBegin = toReceive.indexOf('<');
            int pointvirguleIndex = toReceive.indexOf(';');
            int indexEnd = toReceive.indexOf("/>");

            if (((pointvirguleIndex == -1) || (indexBegin == -1)) || (indexEnd == -1))
            {
                strArray = new String[] { this.toReceive };
            }
            else
            {
                boolean flag = false;

                List<Integer> list = new ArrayList<Integer>();
                int jj = 0, kk = 0, ll = -1;

                int current = 0;
            // Drop all the empty lines
                while (!flag)
                {
                    jj = toReceive.indexOf("0 1 0 0  ;", current);
                    kk = toReceive.indexOf("0 0  ;", current);
					//done = false;
                    if (jj == -1 && kk == -1 ) //  There are not empty lines but here we are searching for array of zero line
                    {
                        ll = toReceive.indexOf("0 ", current);
                        char llm2 = ' ';
                        if ( ll != -1 && current > 2 )
                         llm2 = toReceive.charAt(ll - 2);
                        if ( ll != -1 && ll < pointvirguleIndex && llm2 == ';' )
                        {
                            current = toReceive.indexOf(";", ll) + 1;
                            list.add(current - 1);
							pointvirguleIndex = toReceive.indexOf(';', current);
							if (pointvirguleIndex == -1)
							{
								break;
							}
							indexBegin = toReceive.indexOf('<', current);
							if (indexBegin == -1)
							{
								break;
							}
							indexEnd = toReceive.indexOf("/>", indexBegin);
							if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
							{
								flag = true;
							}
							continue;
                        }
                    }
					
                    //  Searches if they is ';' after  the current ';'?
                    int tmp = toReceive.indexOf(";", pointvirguleIndex + 1);
                    if (tmp != -1)
                    {
                        // It is "; 0 0  ;" ? or "; 0 1 0 0  ;" ?
                        if (kk - pointvirguleIndex > 4 || jj - pointvirguleIndex > 4 )
                        // no
                        {
                            int tmpb = toReceive.indexOf("/>", tmp);
                            // If  ';' is before an "/>" then go ahead
                            if (tmp < tmpb)
                            {
                                //  Searches for all the ";" which follow the current ";" before "/>"
                                int t = -1;
                                while (tmp != -1 && tmp < tmpb)
                                {
                                    t = tmp;
                                    tmp = toReceive.indexOf(";", tmp + 1);
                                }
                                if (t != -1)
                                    tmp = t;

                                current = tmp + 1;
								pointvirguleIndex = toReceive.indexOf(';', current);
								if (pointvirguleIndex == -1)
								{
									break;
								}
								indexBegin = toReceive.indexOf('<', current);
								if (indexBegin == -1)
								{
									break;
								}
								indexEnd = toReceive.indexOf("/>", indexBegin);
								if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
								{
									flag = true;

								}
								continue;
                            }
                        }
                    }

                    if ( jj != -1 && (pointvirguleIndex > 0 && jj > 0 && jj < pointvirguleIndex)) // We have a line which is not useful 
                    {
                        list.add( jj+10 );
                        current = jj + 11;
						pointvirguleIndex = toReceive.indexOf(';', current);
						if (pointvirguleIndex == -1)
						{
							break;
						}
						indexBegin = toReceive.indexOf('<', current);
						if (indexBegin == -1)
						{
							break;
						}
						indexEnd = toReceive.indexOf("/>", indexBegin);
						if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
						{
							flag = true;

						}
						continue;

                    }
                    else if (kk != -1 && (pointvirguleIndex > 0 && kk > 0 && kk < pointvirguleIndex))
                    {
                        list.add(kk + 6);
                        current = kk + 7;
						pointvirguleIndex = toReceive.indexOf(';', current);
						if (pointvirguleIndex == -1)
						{
							break;
						}
						indexBegin = toReceive.indexOf('<', current);
						if (indexBegin == -1)
						{
							break;
						}
						indexEnd = toReceive.indexOf("/>", indexBegin);
						if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
						{
							flag = true;

						}
						continue;
                    }
                    else 
                    {

                        //  First case they is 1er  "/>"  before the current ";" 
                        int tmp1 = toReceive.indexOf("/>", pointvirguleIndex + 1);
                        if (tmp1 != -1)
                        {
                            tmp = toReceive.indexOf(";", pointvirguleIndex + 1);
                            //  Searches for all ";" which follow the current ";" before "/>"
                            int t = -1;
                            while (tmp != -1 && tmp < tmp1)
                            {
                                t = tmp;
                                tmp = toReceive.indexOf(";", tmp + 1);
                            }
                            if (t != -1)
                                tmp = t;

                            if (kk != -1 || jj != -1)
                            {

                                if (kk > pointvirguleIndex && kk < tmp || jj > pointvirguleIndex && jj < tmp)
                                {
                                    // do nothing
                                }
                                else
                                {
                                    current = tmp + 1;
									pointvirguleIndex = toReceive.indexOf(';', current);
									if (pointvirguleIndex == -1)
									{
										break;
									}
									indexBegin = toReceive.indexOf('<', current);
									if (indexBegin == -1)
									{
										break;
									}
									indexEnd = toReceive.indexOf("/>", indexBegin);
									if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
									{
										flag = true;

									}
									continue;
                                }
                            }

                        }
                        // second case  '<' ';' '/>'
                        if (indexBegin > 0 && indexEnd > 0 && pointvirguleIndex > 0 && (indexBegin < pointvirguleIndex && pointvirguleIndex < indexEnd))
                        {
                            // if ';' is in a string then go ahead 
                            current = indexEnd + 2;
							pointvirguleIndex = toReceive.indexOf(';', current);
							if (pointvirguleIndex == -1)
							{
								break;
							}
							indexBegin = toReceive.indexOf('<', current);
							if (indexBegin == -1)
							{
								break;
							}
							indexEnd = toReceive.indexOf("/>", indexBegin);
							if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
							{
								flag = true;

							}
							continue;
                        }
                        // Third case '/>' ';' '<'
                        if (indexBegin > 0 && indexEnd > 0 && pointvirguleIndex > 0 && (indexEnd < pointvirguleIndex && pointvirguleIndex < indexBegin))
                        {
                            // if ';' is in a string then go ahead 
                            current = pointvirguleIndex + 1;
                            list.add(pointvirguleIndex);
							pointvirguleIndex = toReceive.indexOf(';', current);
							if (pointvirguleIndex == -1)
							{
								break;
							}
							indexBegin = toReceive.indexOf('<', current);
							if (indexBegin == -1)
							{
								break;
							}
							indexEnd = toReceive.indexOf("/>", indexBegin);
							if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
							{
								flag = true;
							}
							continue;
                        }
                        // Fourth case  '<' '/>' ';' 
                        if (indexBegin > 0 && indexEnd > 0 && pointvirguleIndex > 0 && (indexBegin < indexEnd && indexEnd < pointvirguleIndex ))
                        {
                            // is they ';' in the String?
                            tmp1 = toReceive.indexOf('<', indexBegin + 1);
                            // Searches the '<'  which is closer to ';'
                            jj = -1;
                            while (tmp1 != -1 && tmp1 < pointvirguleIndex)
                            {
                                jj = tmp1;
                                tmp1 = toReceive.indexOf('<', tmp1 + 1);
                            }
                            if (jj != -1)
                                tmp1 = jj;
                            int tmp2 = toReceive.indexOf("/>", pointvirguleIndex);
                            int tmp3 = toReceive.indexOf("/>", indexBegin);
                            int tmp4 = -1;
                            if ( tmp1 != -1)
                                tmp4 = toReceive.indexOf("/>", tmp1);
                            // First case
                            // '<'  '/>' ';'
                            if (  tmp4 != -1 && tmp4 < pointvirguleIndex) // if it is the case then go ahead
                            {
                                current = pointvirguleIndex + 1;
                                list.add(pointvirguleIndex);
								pointvirguleIndex = toReceive.indexOf(';', current);
								if (pointvirguleIndex == -1)
								{
									break;
								}
								indexBegin = toReceive.indexOf('<', current);
								if (indexBegin == -1)
								{
									break;
								}
								indexEnd = toReceive.indexOf("/>", indexBegin);
								if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
								{
									flag = true;
								}
								continue;
                            }
                            //else if ( tmp2 != -1 )
                            // First case
                            // '<' ';' '/>'
                            if (tmp2 != -1 && tmp1 != -1 && tmp1 < tmp2 ) // && tmp3 >= tmp2) // 
                            {
                                current = tmp2 + 2;
								pointvirguleIndex = toReceive.indexOf(';', current);
								if (pointvirguleIndex == -1)
								{
									break;
								}
								indexBegin = toReceive.indexOf('<', current);
								if (indexBegin == -1)
								{
									break;
								}
								indexEnd = toReceive.indexOf("/>", indexBegin);
								if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
								{
									flag = true;
								}
								continue;
                            }

                            // no

                            //if ( tmp1 * tmp2 != 1 && pointvirguleIndex < tmp1 && pointvirguleIndex < tmp2)
                            //{
                                current = pointvirguleIndex + 1;
                                list.add(pointvirguleIndex);
                            //}
                        }
                    }

              
                    pointvirguleIndex = toReceive.indexOf(';', current);
                    if (pointvirguleIndex == -1)
                    {
                        break;
                    }
                    indexBegin = toReceive.indexOf('<', current);
                    if (indexBegin == -1)
                    {
                        break;
                    }
                    indexEnd = toReceive.indexOf("/>", indexBegin);
                    if (((indexBegin == -1) && (pointvirguleIndex == -1)) && (indexEnd == -1))
                    {
                        flag = true;
                    }
                }
				
				
                strArray = new String[list.size()];
                int num8 = 0;
                for (int i = 0; i < list.size(); i++)
                {
                    strArray[i] = this.toReceive.substring(num8, (int) list.get(i) );
                    num8 = (int)  list.get(i) + 1;
                }
            }
            if (strArray.length == 0)
            {
                strArray = new String[1];
                strArray[0] = this.toReceive ;
            }

            String[] strArray2 = strArray[0].split(" ");
            this.session_id = strArray2[0];
        	    
            num2 = (new Integer(strArray2[1])).intValue();
            this.mErrorMessage = "Ok";
            if (num2 == 0)
            {
                this.mErrorMessage = " error code : ";
                this.mErrorMessage = this.mErrorMessage + strArray2[2];
                this.mErrorMessage = this.mErrorMessage + " '";
                toReceive = "";
                for (int j = 3; j < strArray2.length; j++)
                {
                    toReceive = toReceive + strArray2[j] + " ";
                }
                strArray2 = new KmString(toReceive).split();
                if (strArray2 != null)
                {
                    this.mErrorMessage = this.mErrorMessage + strArray2[0];
                }
                this.mError = true;
            }
            else if (this._analyse)
            {
                if (strArray[0].indexOf('<') != -1)
                {
                    int num11 = strArray[0].indexOf(' ');
                    strArray[0] = strArray[0].substring(num11 + 1);
                    num11 = strArray[0].indexOf(' ');
                    strArray[0] = strArray[0].substring(num11 + 1);
                    this.analyseLine(strArray[0]);
                }
                else
                {
                    // Empty line
                    this._lines.add(0);
                    this._columns.add(0);
                    ArrayList list = new ArrayList();
                    ArrayList list3 = new ArrayList();
                    ArrayList list2 = new ArrayList();
                    this._types.add(list);
                    this._names.add(list2);
                    this._sizes.add(list3);
                    Hashtable<String, ArrayList> dictionary2 = new Hashtable<String, ArrayList>();
                    this._data.add(dictionary2);
                    mScriptSize++;
                }
                for (int k = 1; k < strArray.length; k++)
                {
                    this.analyseLine(strArray[k]);
                }
            }
        }

        public void clear()
        {
            this._names.clear();
            this._columns.clear();
            this._lines.clear();
            this._data.clear();
            this._sizes.clear();
        }

        public void copyFrom(MarcResult r)
        {
            this.session_id = r.session_id;
            this.mScriptSize = r.mScriptSize;
            this.mError = r.mError;
            this.mErrorMessage = r.mErrorMessage;
            for (int i = 0; i < r._lines.size(); i++)
            {
                this._lines.add(r._lines.get(i)  );
            }
            for (int j = 0; j < r._columns.size(); j++)
            {
                this._columns.add(r._columns.get(j));
            }
            for (int k = 0; k < r._sizes.size() ; k++)
            {
                this._sizes.add(r._sizes.get(k));
            }
            for (int m = 0; m < r._names.size(); m++)
            {
                ArrayList list = new ArrayList();
                ArrayList list2 = (ArrayList) r._names.get(m);
                for (int num5 = 0; num5 < list2.size(); num5++)
                {
                    list.add( list2.get(num5) );
                }
                this._names.add(list);
            }
            for (int n = 0; n < r._types.size(); n++)
            {
                ArrayList list3 = new ArrayList();
                ArrayList list4 = (ArrayList) r._types.get(n);
                for (int num7 = 0; num7 < list4.size(); num7++)
                {
                    list3.add(list4.get(num7) );
                }
                this._types.add(list3);
            }
            for (int num8 = 0; num8 < r._sizes.size(); num8++)
            {
                ArrayList list5 = new ArrayList();
                ArrayList list6 = (ArrayList) r._sizes.get(num8);
                for (int num9 = 0; num9 < list6.size(); num9++)
                {
                    list5.add(list6.get(num9));
                }
                this._sizes.add(list5);
            }
            for (int num10 = 0; num10 < r._data.size(); num10++)
            {
                Hashtable<String, ArrayList> dictionary = new Hashtable<String, ArrayList>();
                Hashtable<String, ArrayList> dictionary2 = (Hashtable<String, ArrayList>) r._data.get(num10);
                ArrayList<String> list7 = new ArrayList(dictionary2.keySet());
                for (String str : list7)
                {
                    ArrayList list8 = dictionary2.get(str);
                    ArrayList list9 = new ArrayList();
                    for (int num11 = 0; num11 < list8.size(); num11++)
                    {
                        list9.add((String) list8.get(num11) );
                    }
                    dictionary.put(str, list9);
                }
                this._data.add(dictionary);
            }
        }



        public String getDataAt(int line, int col, int idx)
        {
            if (idx > (this.mScriptSize - 1))
            {
                return null;
            }
            if (idx == -1)
            {
                idx = this.mScriptSize - 1;
            }
            if (line >= ((Integer) this._lines.get(idx)) )
            {
                return null;
            }
            if (line < 0)
            {
                return null;
            }
            if (col > ((Integer) this._columns.get(idx) ))
            {
                return null;
            }
            if (col < 0)
            {
                return null;
            }
            Hashtable<String, ArrayList> dictionary = (Hashtable<String, ArrayList>) this._data.get(idx);
            ArrayList list = (ArrayList) this._names.get(idx);
            String str = (String) list.get(col);
            ArrayList list2 = dictionary.get(str);
            return (String) list2.get(line);
        }

        public String[] getDataByName(String name, int idx)
        {
            name = name.toLowerCase();
            if (idx == -1)
            {
                idx = this.mScriptSize - 1;
            }
            if (this._names.isEmpty())
            {
                return null;
            }
            ArrayList<String> list = (ArrayList) this._names.get(idx);
            if ( !list.contains(name ) )
            {
                return null;
            }
            Hashtable<String, ArrayList> dictionary = (Hashtable<String, ArrayList>) this._data.get(idx);
            if (!dictionary.containsKey(name))
            {
                return null;
            }
            Object[] objArray = dictionary.get(name).toArray();
            String[] strArray = new String[objArray.length];
            int num = 0;
            for(Object obj2 : objArray)
            {
                strArray[num++] = (String) obj2;
            }
            return strArray;
        }

        public String[] getDataByLine(int row, int idx)
        {
            if (idx == -1)
            {
                idx = this.mScriptSize - 1;
            }
            if (this._names.size() == 0)
            {
                return null;
            }

            int rows = (Integer) this._lines.get(idx) - 1;

            if ( row > rows || rows < 0 )
                return null;

            String[] result = new String[ (Integer) this._columns.get(idx) ];

            Hashtable<String, ArrayList> dictionary = (Hashtable<String, ArrayList>)this._data.get(idx);

            ArrayList<String> names = (ArrayList) this._names.get(idx); 
            for (int i = 0; i < names.size(); i++)
            {
                result[i] = (String) dictionary.get((String) names.get(i)).get(row);

            }
                return result;
        }

        public int rowsAtScriptLine(int idx)
        {
            if (idx > (this.mScriptSize - 1))
            {
                idx = this.mScriptSize - 1;
            }
            if (idx == -1)
            {
                idx = this.mScriptSize - 1;
            }
            return (Integer) this._lines.get(idx);
        }
    }
