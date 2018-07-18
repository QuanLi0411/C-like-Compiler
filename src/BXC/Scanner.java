/******************************************************
 	�ļ�	scanner.java
 	˵��	�������Ĵʷ�ɨ����ʵ��
 	����	����token����,�������������кš��������ͺ�������Ϣ
 		���дʷ�������
******************************************************/

package BXC;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import BXC.Global.LexType;

/* ���ʵ����ͣ������ʷ���Ϣ��������Ϣ */
class Token
{
	public int lineshow;
	public LexType Lex;
    public String Sem;
}

public class Scanner {
	
	public static void main(String[] args)
	{
		Scanner myScanner = new Scanner();
		myScanner.run();
	}

	public static Global global = new Global();
	
	/* �ļ������� */
	public BufferedReader reader = null;

	/* Դ����׷�ٱ�־,����ñ�־ΪTRUE,�﷨����ʱ��Դ�����к�д���б��ļ�listing */
	public boolean EchoSource = true;
	
	/* ˳������� */
	public ArrayList<Token> tokenChain = new ArrayList<Token>();

	/************************************************************
		����	getNextChar											   
	   	����	ȡ����һ�ǿ��ַ�									   
	   	˵��	�ú��������뻺����lineBuf��ȡ����һ���ǿ��ַ�		       
	   		���lineBuf�е��ִ��Ѿ�����,���Դ�����ļ��ж���һ����
	************************************************************/
	public char getNextChar()
	{
		/* ��ǰ���������л�����lineBuf�Ѿ��ľ� */
		if (!(Global.linepos < Global.bufsize))
		{
			/* Դ�����к�lineno��1 */
			Global.lineno++;
		    
		    /* ��Դ�ļ�source�ж���BUFLEN-2(254)���ַ����л�����lineBuf��
			   fgets�ڵ�lineBufĩβ�������з�.����ĩβ����һ��NULL�ַ���ʾ���� */
			try {Global.lineBuf = reader.readLine();}catch (IOException e) {}
			if (Global.lineBuf != null)
			{ 	 
			  /* ���Դ�ļ�׷�ٱ�־EchoSourceΪTRUE                               
			                 ��Դ�����к�lineno��������lineBuf�ڴʷ�ɨ��ʱд���б��ļ�listing */
			  if (EchoSource)
			  {
				  global.pw.printf("%4d: ", Global.lineno);
				  global.pw.println(Global.lineBuf);
				  global.pw.flush();
			  }
			  
			  /* ȡ�õ�ǰ����Դ�����е�ʵ�ʳ���,�͸�����bufsize */
			  Global.bufsize = Global.lineBuf.length();
			  if(Global.bufsize == 0) getNextChar();
			  
			  /* �����л�����lineBuf�е�ǰ�ַ�λ��lineposָ��lineBuf��ʼλ�� */
			  Global.linepos = 0;

			  /* ȡ�������л�����lineBuf����һ�ַ� */
		      return Global.lineBuf.charAt(Global.linepos++);

		    }
		    else
			{ 
			  /* δ�ܳɹ������µĴ�����,fget��������ֵΪNULL *
			   * �Ѿ���Դ�����ļ�ĩβ,����EOF_flag��־ΪTRUE */
			  Global.EOF_flag = true;
			  
		   	  /* ��������EOF */
			  return '#';
		    }
		}

		/* �����뻺����lineBuf���ַ���δ����,ֱ��ȡ������һ�ַ�,����������ȡ�ַ� */
		else
			return Global.lineBuf.charAt(Global.linepos++);
	}

	/************************************************************
		����	ungetNextChar											
		����	�ַ����˺���									
		˵��	�ù����������뻺����lineBuf�л���һ���ַ�		
			���ڳ�ǰ���ַ���ƥ��ʱ��Ļ���				
	************************************************************/
	public static void ungetNextChar()
	{
	  /* ���EOF_flag��־ΪFALSE,���Ǵ���Դ�ļ�ĩβ	  *
	   * �����л�����lineBuf�е�ǰ�ַ�λ��linepos��1 */
	  if (!Global.EOF_flag) Global.linepos-- ;
	}
	
	/************************************************************
	 	����	ChainToFile
 		����	�������е�Token������δ����ļ���
 		˵��	�õ���˳�����ArrayList
	************************************************************/
	public void ChainToFile()
	{
		/*����һ���µ��ļ�"TokenList",�Դ洢Token����*/
		String Tokenlist = "F:\\TokenList.txt";	//Token����ļ�
		File fp = new File(Tokenlist);
		
		BufferedWriter TokenWriter = null;
		try
		{
			if(fp.exists())
				fp.delete();
			fp.createNewFile();
			TokenWriter = new BufferedWriter(new FileWriter(fp, true));
		} 
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Cannot open file Tokenlist!\n");
			Global.Error = true;
			System.exit(0);
		}
		PrintWriter pw = new PrintWriter(TokenWriter);
		
		/*�ӱ�ͷ����β�����ν����е�Tokenд���ļ�*/ 
		for (int i=0; i<tokenChain.size(); i++)
		{
			pw.printf("%4d  %10s  ", tokenChain.get(i).lineshow,tokenChain.get(i).Lex);
			pw.println(tokenChain.get(i).Sem);
			pw.flush();
		}

		try {
			TokenWriter.flush();
			pw.close();
			TokenWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/************************************************************
	 	����	getTokenlist						   			    
	 	����	 �ʷ�ɨ��������������ȡ�õ��ʺ���										
	 	˵�� 	������Դ�ļ��ַ��������л�ȡ����Token���� 		
			ʹ��ȷ���������Զ���DFA,����ֱ��ת��    		
			��ǰ���ַ�,�Ա����ֲ��ò��ʽʶ��
			�����ʷ�����ʱ��,�����Թ�����������ַ�,���Ӹ���
	************************************************************/
	public void getTokenlist()
	{  
	   /*��ŵ�ǰ��Token*/
	   Token currentToken = new Token();
	
	   do
	   {  /* ��ǰ״̬��־state,ʼ�ն�����START��Ϊ��ʼ */
	      Global.StateType state = Global.StateType.START;

	  	 /* tokenString���ڱ����ʶ���ͱ����ֵ��ʵĴ�Ԫ,����41 */
	  	  String tokenString = "";

	      /* tokenString�Ĵ洢��־save,��������						*
	       * ������ǰʶ���ַ��Ƿ���뵱ǰʶ�𵥴ʴ�Ԫ�洢��tokenString */
	      boolean save = true;
	
	      /* ��ǰȷ���������Զ���DFA״̬state�������״̬DONE */
	      while (state != Global.StateType.DONE)
	   
		  { 
	        /* ��Դ�����ļ��л�ȡ��һ���ַ�,�������c��Ϊ��ǰ�ַ� */
	    	 char c = getNextChar();
	
	    	/* ��ǰ��ʶ���ַ��Ĵ洢��־save��ʼΪTRUE */
	         save = true;					
	
	         switch (state)
			 {
		       /* ��ǰDFA״̬stateΪ��ʼ״̬START,DFA���ڵ�ǰ���ʿ�ʼλ�� */
	            case START:	
	         	   
		 		   /* ��ǰ�ַ�cΪ��ĸ,��ǰDFA״̬state����Ϊ��ʶ��״̬INID *
		 			* ȷ���������Զ���DFA���ڱ�ʶ�����͵�����              */
		 	        if (Character.isAlphabetic(c))
		 	             state = Global.StateType.INID;			
		
		           /* ��ǰ�ַ�cΪ����,��ǰDFA״̬state����Ϊ����״̬INNUM *
			        * ȷ���������Զ���DFA�����������͵�����               */
		 	        else if (Character.isDigit(c))				
			     		 state = Global.StateType.INNUMBER;			
		
			       /* ��ǰ�ַ�cΪð��,��ǰDFA״̬state����Ϊ��ֵ״̬INASSIGN *
				    * ȷ���������Զ���DFA���ڸ�ֵ���͵�����				   */
		            else if (c == '=')
		                 state = Global.StateType.INASSIGN;		
		  		 
			       /* ��ǰ�ַ�cΪ<,��ǰDFA״̬state����Ϊ����״̬*/
			       /* INCIN��ȷ���������Զ���DFA�����������͵�����*/                         
				    else if (c == '<')
				         state = Global.StateType.INCIN;
		 	        
				    else if (c == '>')
				         state = Global.StateType.INCOUT;
                    
				    else if (c == '/')
				    {
		                 save = false;
				         state = Global.StateType.INCOMMENT;
				    }
                    
		 	       /* ��ǰ�ַ�cΪ������,��ǰDFA״̬state����Ϊ�ַ���־״̬*/
				    else if (c == '\'')
				    {
						 save = false;
				         state = Global.StateType.INCHAR;
				    }
		 	        
				   /* ��ǰ�ַ�cΪ�հ�(�ո�,�Ʊ��,���з�),�ַ��洢��־save����ΪFALSE *
				    * ��ǰ�ַ�Ϊ�ָ���,����Ҫ��������,����洢                        */
		            else if ((c == ' ') || (c == '\t') || (c == '\n'))
		            	 save = false;

		 	       /* ��ǰ�ַ�cΪ�����ַ�,��ǰDFA״̬state����Ϊ���״̬DONE *
		 	        * ȷ���������Զ���DFA���ڵ��ʵĽ���λ��,���һ�����ദ�� */
		            else
		 			{
		 				 state = Global.StateType.DONE;
		                 switch (c)
		 				 {
		 			      /* ��ǰ�ַ�cΪEOF,�ַ��洢��־save����ΪFALSE,����洢     *
		 			       * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�ļ���������ENDFILE */
		 		            case '#':
		                    save = false;
		                    currentToken.Lex = LexType.ENDFILE;
		                    break;					

		 			      /* ��ǰ�ַ�cΪ"=",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�Ⱥŵ���EQ */
		                    case '.':
		                    currentToken.Lex = LexType.DOT;
		                    break;

		 			      /* ��ǰ�ַ�cΪ"<",��ǰʶ�𵥴ʷ���ֵcurrentToken����ΪС�ڵ���LT */
		                    case ',':
		                    currentToken.Lex = LexType.COMMA;
		                    break;
		                    
		 			      /* ��ǰ�ַ�cΪ";",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�ֺŵ���SEMI */
		                    case ';':
		                    currentToken.Lex = LexType.SEMI;
		                    break;

		 			      /* ��ǰ�ַ�cΪ"+",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�Ӻŵ���PLUS */
		                    case '+':
		                    currentToken.Lex = LexType.PLUS;
		                    break;

		 			      /* ��ǰ�ַ�cΪ"-",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ŵ���MINUS */
		                    case '-':
		                    currentToken.Lex = LexType.MINUS;
		                    break;
		                    
		 		       	  /* ��ǰ�ַ�cΪ"*",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�˺ŵ���TIMES */
		                    case '*':
		                    currentToken.Lex = LexType.TIMES;
		                    break;

			 			  /* ��ǰ�ַ�cΪ"(",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�����ŵ���LPAREN */
			                case '(':
			                currentToken.Lex = LexType.LPAREN;
			                break;
			                
			 			  /* ��ǰ�ַ�cΪ")",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�����ŵ���RPAREN */
			                case ')':
			                currentToken.Lex = LexType.RPAREN;
			                break;
   		 
		 		          /* ��ǰ�ַ�cΪ"[",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�������ŵ���LMIDPAREN */
		 		            case '[':
		 			        currentToken.Lex = LexType.LMIDPAREN;
		 			        break;
		     		 
		 			      /* ��ǰ�ַ�cΪ"]",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�������ŵ���RMIDPAREN */
		 			        case ']':
		 			        currentToken.Lex = LexType.RMIDPAREN;
		 			        break;
		 			   		 
			 		      /* ��ǰ�ַ�cΪ"{",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�������ŵ���LMIDPAREN */
			 		        case '{':
			 		        currentToken.Lex = LexType.LBIGPAREN;
			 			    break;
			     		 
			 		      /* ��ǰ�ַ�cΪ"}",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�������ŵ���RMIDPAREN */
			 		        case '}':
			 		        currentToken.Lex = LexType.RBIGPAREN;
					        break;
		     
		 			      /* ��ǰ�ַ�cΪ�����ַ�,��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���󵥴�ERROR */
		                    default:
		                    currentToken.Lex = LexType.ERROR;
		                    Global.Error = true;
		                    break;
		 				 }
		 			}
		        break;						
		 	    /********** ��ǰ״̬Ϊ��ʼ״̬START�Ĵ������ **********/
		        
	            case INID:
	       		/* ��ǰ�ַ�c������ĸ,���������л�����Դ�л���һ���ַ�		 			*
	       		 * �ַ��洢��־����ΪFALSE,��ǰDFA״̬state����ΪDONE,��ʶ������ʶ����� *
	       		 * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ��ʶ������ID               */
	            	if(!(Character.isAlphabetic(c)||Character.isDigit(c)))
	            	{
	                    ungetNextChar();
	                    save = false;
	                    state = Global.StateType.DONE;
	                    currentToken.Lex = LexType.ID;
	            	}
	            break;
	            
	            case INNUMBER:
	       	 	/* ��ǰ�ַ�c��������,���������л�����Դ�л���һ���ַ�					*
	       	 	 * �ַ��洢��־����ΪFALSE,��ǰDFA״̬state����ΪDONE,���ֵ���ʶ�����	*
	       	 	 * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���ֵ���NUM           	*/
		            if (!Character.isDigit(c))
		            { 
			            ungetNextChar();
			            save = false;
			            state = Global.StateType.DONE;
			            currentToken.Lex = LexType.INTC;
		            }
	            break;

	     	   /* ��ǰDFA״̬stateΪ��ֵ״̬INASSIGN,ȷ���������Զ���DFA���ڸ�ֵ����λ�� */
	            case INASSIGN:

	            	/* ��ǰDFA״̬state����Ϊ���״̬DONE,��ֵ���ʽ��� */
	            	state = Global.StateType.DONE;				

		     		 /* ��ǰ�ַ�cΪ"=",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ��ֵ����ASSIGN */
		     		if (c == '=')
		                currentToken.Lex = LexType.EQ;
	
		     		 /* ��ǰ�ַ�cΪ�����ַ�,��":"����"=",�������л������л���һ���ַ�       *
		     		  * �ַ��洢״̬save����ΪFALSE,��ǰʶ�𵥴ʷ���ֵcurrentToken����ΪERROR */
		            else
		            {
		                ungetNextChar();
		                save = false;
		                currentToken.Lex = LexType.ASSIGN;
		            }
	            break;
	            
	            case INCIN:

	            	/* ��ǰDFA״̬state����Ϊ���״̬DONE,��ֵ���ʽ��� */
	            	state = Global.StateType.DONE;				

		     		 /* ��ǰ�ַ�cΪ"=",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ��ֵ����ASSIGN */
		     		if (c == '=')
		                currentToken.Lex = LexType.LE;

		     		else if (c == '>')
		                currentToken.Lex = LexType.NEQ;
		     		
		     		else if (c == '<')
		                currentToken.Lex = LexType.OUT;
	
		     		 /* ��ǰ�ַ�cΪ�����ַ�,�������л������л���һ���ַ�       *
		     		  * �ַ��洢״̬save����ΪFALSE,��ǰʶ�𵥴ʷ���ֵcurrentToken����ΪERROR */
		            else
		            {
		                ungetNextChar();
		                save = false;
		                currentToken.Lex = LexType.LT;
		            }
	            break;
	            
	            case INCOUT:

	            	/* ��ǰDFA״̬state����Ϊ���״̬DONE,��ֵ���ʽ��� */
	            	state = Global.StateType.DONE;				

		     		 /* ��ǰ�ַ�cΪ"=",��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ��ֵ����ASSIGN */
		     		if (c == '=')
		                currentToken.Lex = LexType.GE;

		     		else if (c == '>')
		                currentToken.Lex = LexType.IN;
	
		     		 /* ��ǰ�ַ�cΪ�����ַ�,�������л������л���һ���ַ�       *
		     		  * �ַ��洢״̬save����ΪFALSE,��ǰʶ�𵥴ʷ���ֵcurrentToken����ΪERROR */
		            else
		            {
		                ungetNextChar();
		                save = false;
		                currentToken.Lex = LexType.GT;
		            }
	            break;

	     	   /* ��ǰDFA״̬stateΪע��״̬INCOMMENT,ȷ���������Զ���DFA����ע��λ�� */
	            case INCOMMENT:

	            	if(c == '*')
	            	{
		                save = false;
				        state = Global.StateType.COMBEGIN;
	            	}
	            	else
		            {
		                ungetNextChar();
			            state = Global.StateType.DONE;
		                currentToken.Lex = LexType.OVER;
		            }
	            break;

	            case COMBEGIN:

	       		 	/* ��ǰ�ַ��洢״̬save����ΪFALSE,ע�������ݲ����ɵ���,����洢 */
	                save = false;				

		     		/* ��ǰ�ַ�cΪEOF,��ǰDFA״̬state����Ϊ���״̬DONE,��ǰ����ʶ����� *
		     		 * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ�ļ���������ENDFILE      */
	            	if (c == '#')
		            {
		            	state = Global.StateType.DONE;
		                currentToken.Lex = LexType.ENDFILE;
		            }
	            	else if(c == '*')
		            	state = Global.StateType.COMEND;
	            	
	            break;
	            	
	            case COMEND:
	            	
	            	save = false;
	            	if (c == '#')
		            {
		            	state = Global.StateType.DONE;
		                currentToken.Lex = LexType.ENDFILE;
		            }
		     		/* ��ǰ�ַ�cΪ"}",ע�ͽ���.��ǰDFA״̬state����Ϊ��ʼ״̬START */
		            else if(c == '/')
		            	state = Global.StateType.START;
		            else
		            	state = Global.StateType.COMBEGIN;
	            break;
	            

	        	/*��ǰDFA״̬stateΪ�ַ���־״̬INCHAR,ȷ�������Զ��������ַ���־״̬*/
	            case INCHAR:					

		             if (Character.isAlphabetic(c)||Character.isDigit(c))
		    		 { 
		            	 int c1=getNextChar();
		                 if (c1 =='\'')
		    			 {
		                	 save = true;
		                	 state = Global.StateType.DONE;
		                	 currentToken.Lex = LexType.CHARC;
		    			 }
		    		     else
		    			 {
		    		    	 ungetNextChar();
		                     ungetNextChar();
		                     state = Global.StateType.DONE;
		                     currentToken.Lex = LexType.ERROR;
		    				 Global.Error = true;
		    			 }
		    		 }
		    		 else
		    		 {      
		    			 ungetNextChar();
		                 state = Global.StateType.DONE;
		                 currentToken.Lex = LexType.ERROR;
		    			 Global.Error = true;
		    		 }
	    		break;
	            
	            /* ��ǰDFA״̬stateΪ���״̬DONE,ȷ���������Զ���DFA���ڵ��ʽ���λ�� */
	            case DONE:	break;

	     	    /* ��ǰDFA״̬stateΪ����״̬,���������Ӧ���� */
	            default:
	
	       		 /* ��ǰDFA״̬state����Ϊ���״̬DONE			*
	       		  * ��ǰʶ�𵥴ʷ���ֵcurrentToken����Ϊ���󵥴�ERROR*/
	                Global.Error = true;
	                state = Global.StateType.DONE;
	                currentToken.Lex = LexType.ERROR;
	                break;
	         }
	         /*************** �����жϴ������ *******************/
	       	 /* ��ǰ�ַ��洢״̬saveΪTRUE,�ҵ�ǰ��ʶ�𵥴��Ѿ�ʶ�𲿷�δ����������󳤶� *
	       	  * ����ǰ�ַ�cд�뵱ǰ��ʶ�𵥴ʴ�Ԫ�洢��tokenString				*/
	         if (save)
	        	 tokenString += c;

       		 /* ��ǰ����currentTokenΪ��ʶ����������,�鿴���Ƿ�Ϊ�����ֵ��� */
	       	 if (state == Global.StateType.DONE && currentToken.Lex == LexType.ID)
	       		 currentToken.Lex = global.reservedLookup(tokenString);
	      }
	      /**************** ѭ��������� ********************/
	      /*���к���Ϣ����Token*/
	      currentToken.lineshow = Global.lineno;
	      /*�����ʵ�������Ϣ����Token*/
	      currentToken.Sem = tokenString;
	      /*���Ѵ�����ĵ�ǰToken���������Token����*/
	      tokenChain.add(Global.Tokennum, currentToken);
	      Global.Tokennum++;   /*Token����Ŀ��1*/
	      LexType temp = currentToken.Lex;
	      currentToken = new Token();
	      currentToken.Lex = temp;
	    }
	    /* ֱ���������ʾ�ļ�������Token:ENDFILE��˵�����������е�Token*/
	    /* �������������У�ѭ������*/
	    while ((currentToken.Lex)!=LexType.ENDFILE);
	    /*����chainHeadָ���Token��������ļ�"Tokenlist"��*/
	    ChainToFile();
	}
	
	public void run()
	{
		global.pw.println("------------------------- Դ���� --------------------------");
		global.pw.println();
		global.pw.flush();

		//����һ���ʷ����������,��Ҫ "·����"+"�ļ���"+"��׺��"
		Object input = JOptionPane.showInputDialog(null, "������Դ�ļ�·������", "BXC", 1,
				null, null, "F:\\mySource.bxc");
		if(input==null)
			System.exit(0);
		else
			try {reader = new BufferedReader(new FileReader(new File(input.toString())));}
			catch (FileNotFoundException e) 
			{JOptionPane.showMessageDialog(null, "·��������ȷ���ļ������ڣ�", "BXC", 2);System.exit(0);}
		
		getTokenlist();
	}
}