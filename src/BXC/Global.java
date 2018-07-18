/******************************************************
 	�ļ�	Global.java
 	˵��	�������ĸ����������ʹ�õ���ȫ�ֱ�������������
 	����	�����ļ������
 		�����ʷ���������
 		�����ݹ��½����﷨��������
 		����LL(1)�﷨��������
 	����	��ѧ��
******************************************************/

package BXC;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import WSL.Analyze.SymbTable;

public class Global {

	/************************ �ļ������ ****************************/
	public String list = "F:\\LineList.txt";	//�б�����ļ�
	public File ListFile = new File(list);
	public BufferedWriter writer = null;
	{try {
		if(ListFile.exists())
			ListFile.delete();
		ListFile.createNewFile();
		writer = new BufferedWriter(new FileWriter(ListFile, true));
	}catch (IOException e) {}}
	public PrintWriter pw = new PrintWriter(writer);

	/************************************************************
								�ʷ���������
	*************************************************************/
	
	/*************** �ʷ�������ȷ���������Զ���DFA��״̬����   *****************
	 	Start ��ʼ״̬;		InID ��ʶ��״̬;    	InNumber ����״̬;
	 	InAssign ��ֵ״̬;		InCin ����״̬;		InCout ���״̬;
	 	InComment ע��״̬;	ComBegin ע�Ϳ�ʼ;		ComEnd ע�ͽ���;
	 	InChar �ַ�״̬;		Error ����״̬;		Done ���״̬;
	*************************************************************/
	public enum StateType
	{
		START,		INID,		INNUMBER,
		INASSIGN,	INCIN,		INCOUT,
		INCOMMENT,	COMBEGIN,	COMEND,
		INCHAR,		ERROR,		DONE
	};
	
	/************************* ���ʵĴʷ����� *************************/
	/*�����ռ���*/
	public enum LexType
	{
		/* ���ǵ��ʷ��� */
	    ENDFILE,	ERROR,
		/* ������	�ܼƣ�11	*/
	    TYPEDEF,	STRUCT,		IF,			ELSE,		WHILE,
	    FOR,		CIN,		COUT,		RETURN,		INT,	CHAR,
		/* ���ַ����ʷ��� */
	    ID,			INTC,		CHARC,
	    /*������� */
		ASSIGN,		EQ,			LT,			LE,			GT,
		GE,			NEQ,		PLUS,		MINUS,/*��*/	TIMES,
		OVER,/*��*/	LPAREN,		RPAREN,		DOT,		SEMI,
		COMMA,		LMIDPAREN,	RMIDPAREN,	LBIGPAREN,	RBIGPAREN,
		IN,			OUT,
		/* �﷨�����еď�ջ�ź� */
		END_POP
	};

	/* Դ�����嵥���к� */
	public static int lineno = 0;
	
	/*Token�����е�token��Ŀ*/
	public static int Tokennum = 0;
	
	/* lineBufΪ��ǰ��������л����� */
	public static String lineBuf = null;
	
	/* lineposΪ�ڴ��뻺����LineBuf�еĵ�ǰ�ַ�λ��,��ʼΪ0 */
	public static int linepos = 0;
	
	/* bufsizeΪ��ǰ�������������ִ���С */
	public static int bufsize = 0;
	
	/* EOF_flag��Ϊ�ļ�βʱ,�ı亯��ungetNextChar���� */
	public static boolean EOF_flag = false;

	/* ����׷�ٱ�־,����ñ�־ΪTRUE,��ֹ�������ʱ��һ�����ݴ���  */
	public static boolean Error = false;
	
	/* ���屣������������MAXRESERVEDΪ11 */
	public static final int MAXRESERVED = 11;

	/************************ �����ֲ��ұ� ***************************/
	class reservedWord
	{
		public String  str;
	 	public LexType tok;
	 	public reservedWord(String s,LexType t){str = s;tok = t;}
	}
	public static reservedWord[] reservedWords = new reservedWord[MAXRESERVED];
	{
		reservedWords[0] = new reservedWord("typedef",LexType.TYPEDEF);
		reservedWords[1] = new reservedWord("struct",LexType.STRUCT);
		reservedWords[2] = new reservedWord("if",LexType.IF);
		reservedWords[3] = new reservedWord("else",LexType.ELSE);
		reservedWords[4] = new reservedWord("while",LexType.WHILE);
		reservedWords[5] = new reservedWord("for",LexType.FOR);
		reservedWords[6] = new reservedWord("cin",LexType.CIN);
		reservedWords[7] = new reservedWord("cout",LexType.COUT);
		reservedWords[8] = new reservedWord("return",LexType.RETURN);
		reservedWords[9] = new reservedWord("int",LexType.INT);
		reservedWords[10]= new reservedWord("char",LexType.CHAR);
	}
	
	/************************************************************
		����	reservedLookup								      
		����	�����ֲ��Һ���									  
		˵��	ʹ�����Բ���,�鿴һ����ʶ���Ƿ��Ǳ�����			  
			��ʶ������ڱ����ֱ����򷵻���Ӧ����,���򷵻ص���ID 
	/***********************************************************/
	public LexType reservedLookup (String s)
	{
	  /* �ڱ����ֱ��в���,MAXRESERVED�Ѿ�����Ϊ8,Ϊ�������� */
		for (int i=0;i<MAXRESERVED;i++)
			/* ���Բ鱣���ֱ�,�쿴��������sָ����ʶ���Ƿ��ڱ��� *
			 * �����ַ���ƥ���ʱ��,����strcmp����ֵΪ0(FALSE)	*/
			if (reservedWords[i].str.equals(s))
				/* �ַ���s�뱣���ֱ���ĳһ����ƥ��,�������ض�Ӧ�����ֵ��� */
				return reservedWords[i].tok;
	  
	  /* �ַ���sδ�ڱ����ֱ����ҵ�,�������ر�ʶ������ID */
	  return LexType.ID;
	}
	
	/*************************************************************
	  						�ݹ��½����﷨��������
	*************************************************************/

	/**************** �﷨���ڵ�ṹ   ***************/
	/* �﷨�����ڵ�RootK,�������ͽڵ�DecK,��־�ӽ�㶼�����������Ľ��TypeK,
	 * ��־�ӽ�㶼�Ǳ��������Ľ��VarK,�����������FuncDecK,
	 * ������нڵ�StmLK,����������StmtK,���ʽ���ExpK*/
	public enum NodeKind{RootK,DecK,TypeK,FuncDecK,StmLK,StmtK,ExpK};

	/*��������Deckind ���͵�ö�ٶ��壺
	  ��������ArrayK,�ַ�����CharK,
	  ��������IntegerK,��¼����RecordK,
	  �����ͱ�ʶ����Ϊ���͵�IdK*/ 
	public enum DecKind{IntK,CharK,ArrayK,StructK,IdK};

	/* ��������VarKind���͵�ö�ٶ���:           *
	 * ��ʶ��IdV,�����ԱArrayMembV,���ԱFieldMembV*/
	public enum VarKind{IdV,ArrayMembV,FieldMembV}; 

	/* �������StmtKind���͵�ö�ٶ���:			*
	 * �ж�����IfK,ѭ������WhileK				*
	 * ��ֵ����AssignK,������ReadK           *
	 * д����WriteK��������������CallK          */
	public enum StmtKind{IfK,WhileK,ForK,AssignK,CinK,CoutK,CallK,ReturnK};

	/* ���ʽ����ExpKind���͵�ö�ٶ���:         *
	 * ��������OpK,��������ConstK,��������VarK */
	public enum ExpKind{OpK,ConstK,VariK};

	/* ���ͼ��ExpType���͵�ö�ٶ���:           *
	 * ��Void,��������Integer,�ַ�����Char    */ 
	public enum ExpType{Void,Int,Char};
	
	public class treeNode {
		public treeNode child[] = new treeNode[4];	/* �ӽڵ�ָ��	*/
		public treeNode sibling;	/* �ֵܽڵ�ָ��	*/
		public int lineno;			/* Դ�����к�	*/
		public NodeKind nodeKind;	/* �ڵ�����		*/
		
		public class Kind
		{
			public DecKind dec;
			public StmtKind stmt;
			public ExpKind exp;
		}
		public Kind kind = new Kind();  /* �������� 	*/
		
		public int idnum;         		/* ��ͬ���͵ı������� */ 
		public String name[] = new String[10];		 /* ��ʶ��������  */
		public SymbTable table[] = new SymbTable[10];/* ���־����Ӧ�ķ��ű��ַ������������׶�����*/

		public class Attr
		{
			public class ArrayAttr
			{
				public int size;			 /* �����С     */
				public DecKind childType;/* ����������� */
			}
			public ArrayAttr arrayAttr = new ArrayAttr(); /* ��������     */

			public class FuncAttr
			{	
				public DecKind returnType;     /* ���̵ķ�������*/
			}
			public FuncAttr funcAttr = new FuncAttr();    /* ��������      */ 
				
			public class ExpAttr
			{
				public LexType op; 		 	/* ���ʽ�Ĳ�����	*/
				public int int_val; 		/* ���ʽ��ֵ	*/
				public char char_val; 		/* ���ʽ��ֵ	*/
				public VarKind varKind;  	/* ���������    	*/
				public ExpType type; 	 	/* �������ͼ��  	*/
			}
			public ExpAttr expAttr = new ExpAttr();		  /* ���ʽ����    */
			
			public String type_name; /* �������Ǳ�ʶ��  */
		}
		public Attr attr=new Attr(); /* ����	     */
	}

	/* ����indentno�ں���printTree�����ڴ洢��ǰ������������,��ʼΪ0		*/
	public static int indentno = 0;
	
	/********************************************************/
	/* ������ printSpaces										*/
	/* ��  �� �ո��ӡ����											*/
	/* ˵  �� �ú�����ӡָ�������ո�,���ڱ�ʾ��������						*/
	/********************************************************/
	public void printSpaces()
	{
	  /* ������������indentno��ӡ�ո��������	*
	   * ����������indentno���ܱ��ַǸ�		*/
		for (int i=0;i<indentno;i++)
			pw.print(" ");
	}
	
	/********************************************************/
	/* ������ printTab                                     	*/
	/* ��  �� ��ӡ�ո�                                        								*/
	/* ˵  �� ������ļ��д�ӡ����Ϊ����tabnum�Ŀո�          					*/
	/********************************************************/
	public void printTab(int tabnum)
	{
		for(int i=0;i<tabnum;i++)
			pw.print(" ");
	}

	/********************************************************/
	/* ������ printTree                             		    */
	/* ��  �� ���﷨���������ʾ��listing�ļ���         						*/
	/* ˵  �� �ú��������˺���������������������          						*/
	/********************************************************/
	public void printTree(treeNode tree)
	{	
		/* ����������,ÿ�ν����﷨���ڵ㶼������������ */
		indentno += 4;
		
		/* �������������﷨���ڵ�ָ��tree��null(��) */
		while (tree != null)
		{		
			/*��ӡ�к�*/
			if(tree.lineno==0)
				printTab(9);
			else
			    switch(tree.lineno / 10)
				{
					case 0:
						pw.print("line:");
						pw.print(tree.lineno);
						printTab(3);
						break;
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
						pw.print("line:");
						pw.print(tree.lineno);
						printTab(2);
						break;
					default:
						pw.print("line:");
						pw.print(tree.lineno);
						printTab(1);
				}

		    /* ���ú���printSpaces,��ӡ��Ӧ�Ŀո�,�������� */ 
		    printSpaces();
		    
		    switch (tree.nodeKind)
		    {
		    case RootK :
				pw.print("RootK  ");
		    	break;
			case DecK:
				pw.print("DecK  ");
				if(tree.kind.dec != null)
				switch(tree.kind.dec)
				{
				case  ArrayK:
					pw.print("ArrayK  ");
					pw.print(tree.attr.arrayAttr.size+"  ");
					if (tree.attr.arrayAttr.childType == DecKind.CharK)
						pw.print("CharK  ");
					else if( tree.attr.arrayAttr.childType == DecKind.IntK)
						pw.print("IntK  ");
					break;
				case  CharK:
					pw.print("CharK  ");
					break;
				case  IntK:
					pw.print("IntK  ");
					break;
				case  StructK:
					pw.print("StructK  ");
					break;
				case  IdK:
					pw.print("IdK  ");
					pw.print(tree.attr.type_name+"  ");
					break;
				default: 
					pw.print("error0!");
					Global.Error = true;
				};
				if (tree.idnum !=0)
					for (int i=0 ; i < (tree.idnum);i++)
					{
						pw.print(tree.name[i]+"  ");
					}
				else  
				{
					pw.print("wrong!no var!\n");
					Global.Error = true;	
				}
				break;
			case TypeK:
				pw.print("TypeK  ");
				switch(tree.kind.dec)
				{
				case  ArrayK:
					pw.print("ArrayK  ");
					pw.print(tree.attr.arrayAttr.size+"  ");
					if (tree.attr.arrayAttr.childType == DecKind.CharK)
						pw.print("CharK  ");
					else if( tree.attr.arrayAttr.childType == DecKind.IntK)
						pw.print("IntK  ");
					break;
				case  CharK:
					pw.print("CharK  ");
					break;
				case  IntK:
					pw.print("IntK  ");
					break;
				case  StructK:
					pw.print("StructK  ");
					break;
				case  IdK:
					pw.print("IdK  ");
					pw.print(tree.attr.type_name+"  ");
					break;
				default: 
					pw.print("error1!");
					Global.Error = true;
				};
				if (tree.idnum !=0)
					for (int i=0 ; i < (tree.idnum);i++)
					{
						pw.print(tree.name[i]+"  ");
					}
				else  
				{
					pw.print("wrong!no typename!\n");
					Global.Error = true;	
				}
				break;
			case FuncDecK: 
				pw.print("FuncDecK  ");
				pw.print(tree.attr.funcAttr.returnType+"  ");
				pw.print(tree.name[0]+"  ");
				if(tree.table[0]!=null)
				{
					pw.print(tree.table[0].attrIR.more.funcAttr.mOff+"  ");
					pw.print(tree.table[0].attrIR.more.funcAttr.nOff+"  ");
					pw.print(tree.table[0].attrIR.more.funcAttr.level+"  ");
				}
				break;
			case StmLK:
				pw.print("StmLk  ");
				break;
			case StmtK:
				pw.print("StmtK  ");
				switch (tree.kind.stmt)
				{
				case IfK:
					pw.print("If  ");
					break;
				case WhileK:
					pw.print("While  ");
					break;
				case ForK:
					pw.print("For  ");
					break;
				case AssignK:
					pw.print("Assign  ");
					break;
				case CinK:
					pw.print("Cin  ");
					pw.print(tree.name[0]+"  ");
					if(tree.table[0]!=null)
					{
						pw.print(tree.table[0].attrIR.more.varAttr.off+"  ");
						pw.print(tree.table[0].attrIR.more.varAttr.level+"  ");
					}
					break;
				case CoutK:
					pw.print("Cout  ");
					break;
				case CallK:
					pw.print("Call  ");
					pw.print(tree.name[0]+"  ");
					break;
				case ReturnK:
					pw.print("Return  ");
					break;
				default: 
					pw.print("error2!");
					Global.Error = true;
				}
				break;
			case ExpK: 
				pw.print("ExpK  ");
				switch (tree.kind.exp)
				{
				case OpK:
					pw.print("Op  ");
					switch(tree.attr.expAttr.op)
					{
					case LT:   
						pw.print("<  "); 
						break;      
					case GT:   
						pw.print(">  "); 
						break;      
					case LE:   
						pw.print("<=  "); 
						break;      
					case GE:   
						pw.print(">=  "); 
						break;      
					case EQ:
						pw.print("==  "); 
						break;
					case NEQ:
						pw.print("<>  "); 
						break;
					case PLUS: 
						pw.print("+  "); 
						break;   
					case MINUS:
						pw.print("-  "); 
						break;
					case TIMES:
						pw.print("*  "); 
						break;  
					case OVER:
						pw.print("/  "); 
						break;  
					default: 
						pw.print("error3!");
						Global.Error = true;
					}
					
					if(tree.attr.expAttr.varKind==VarKind.ArrayMembV)
					{
						pw.print("ArrayMember  ");
						pw.print(tree.name[0]+"  ");
					}
					break;
				case ConstK:
					pw.print("Const  ");
					switch(tree.attr.expAttr.varKind)
					{
					case IdV:
						pw.print("Id  ");
						switch (tree.attr.expAttr.type) {
						case Int:
							pw.print("IntC  ");
							break;
						case Char:
							pw.print("CharC  ");
							break;
						default:
							pw.print("undefined  ");
							break;
						}
						if(tree.name[0]!="")
							pw.print(tree.name[0]+"  ");
						break;
					case FieldMembV:
						pw.print("FieldMember  ");
						if(tree.name[0]!="")
							pw.print(tree.name[0]+"  ");
						break;
					case ArrayMembV:
						pw.print("ArrayMember  ");
						if(tree.name[0]!="")
							pw.print(tree.name[0]+"  ");
						break;
					default: 
						pw.print("var type error!");
						Global.Error = true;
					}
					if(tree.attr.expAttr.type == ExpType.Int)		
						pw.print(tree.attr.expAttr.int_val+"  ");
					else if(tree.attr.expAttr.type == ExpType.Char)		
						pw.print(tree.attr.expAttr.char_val+"  ");
					break;
				case VariK:
					pw.print("Vari  ");
					switch(tree.attr.expAttr.varKind)
					{
					case IdV:
						pw.print("Id  ");
						pw.print(tree.name[0]+"  ");
						break;
					case FieldMembV:
						pw.print("FieldMember  ");
						pw.print(tree.name[0]+"  ");
						break;
					case ArrayMembV:
						pw.print("ArrayMember  ");
						pw.print(tree.name[0]);
						break;
					default: 
						pw.print("var type error!");
						Global.Error = true;
					}
					if(tree.table[0]!=null)
					{
						pw.print(tree.table[0].attrIR.more.varAttr.off+"  ");
						pw.print(tree.table[0].attrIR.more.varAttr.level+"  ");
					}
					break;
				default: 
					pw.print("error4!");
					Global.Error = true;
				}
				break;
			default: 
				pw.print("error5!");
				  Global.Error = true;
		    }
		   
		    pw.println();
		    pw.flush();
			/* ���﷨�����tree�ĸ��ӽ��ݹ����printTree���� *
			 * ����д���б��ļ�listing						   */
			for (int i=0;i<tree.child.length;i++)
		        printTree(tree.child[i]);
		
			/* ���﷨�����tree���ֵܽڵ�ݹ����printTree���� *
			 * ����д���б��ļ�listing						   */ 
			tree = tree.sibling;			
		}

		/* ����������,ÿ���˳��﷨���ڵ�ʱ�������� */
		indentno -= 4;							
	}
	
	/********************************************************/
	/*				�����Ǵ����﷨�����õĸ���ڵ������ 				*/
	/********************************************************/
	
	/********************************************************/
	/* ������ newRootNode										*/
	/* ��  �� �����﷨�����ڵ㺯��			        				*/
	/* ˵  �� �ú���Ϊ�﷨������һ���µĸ����      							*/
	/*        �����﷨���ڵ��Ա��ʼ��								*/
	/********************************************************/
	public treeNode newRootNode()
	{
		  /* ���ڴ��ж�̬������䵥Ԫ������ָ��õ�Ԫ���﷨���������ָ��t */
		  treeNode t = new treeNode();
		    
		  /* ָ�����﷨���ڵ�t��Ա:�������nodekindΪ�������ProK */
		  t.nodeKind = NodeKind.RootK;
		    
		  /* ָ�����﷨���ڵ�t��Ա:Դ�����к�linenoΪȫ�ֱ���lineno */
		  t.lineno = lineno;
		
		  for(int i=0;i<t.name.length;i++) 
			  t.name[i] = "";
		  
		  /* ���������﷨�����ڵ�ָ��t */
		  return t;
	}
	
	/********************************************************/
	/* ������ newDecANode										*/	
	/* ��  �� ���������﷨���ڵ㺯��,û��ָ������Ľڵ�����					*/
	/*        ����,���﷨���ĵڶ���			  			        */
	/* ˵  �� �ú���Ϊ�﷨������һ���µĽ��      	     					*/
	/*        �����﷨���ڵ��Ա��ʼ��								*/
	/********************************************************/
	public treeNode newDecANode(NodeKind kind)
	{
		/* ���ڴ��ж�̬������䵥Ԫ������ָ��õ�Ԫ���﷨���������ָ��t */
		treeNode t = new treeNode();
		
		/* ָ�����﷨���ڵ�t��Ա:�������nodekindΪ����kind */
		t.nodeKind = kind;
		
		/* ָ�����﷨���ڵ�t��Ա:Դ�����к�linenoΪȫ�ֱ���lineno */
		t.lineno = lineno;

		for(int i=0;i<t.name.length;i++) 
			t.name[i] = "";
		
		/* ���������﷨�����ڵ�ָ��t */
		return t;
	}

	/********************************************************/
	/* ������ newTypeNode										*/
	/* ��  �� ���ͱ�־�﷨���ڵ㴴������								*/
	/* ˵  �� �ú���Ϊ�﷨������һ���µ����ͱ�־��㣬						*/
	/*        ��ʾ����֮�µ�������Ϊ����������        						*/
	/*        �����﷨���ڵ�ĳ�Ա��ʼ��								*/
	/********************************************************/
	public treeNode newTypeNode()
	{ 
		/* ���ڴ��ж�̬������䵥Ԫ������ָ��õ�Ԫ���﷨���������ָ��t */
		treeNode t = new treeNode();

		/* ָ�����﷨���ڵ�t��Ա: �������nodekindΪ���ʽ����ExpK */
		t.nodeKind = NodeKind.TypeK;
	
		/* ָ�����﷨���ڵ�t��Ա: Դ�����к�linenoΪȫ�ֱ���lineno */
	    t.lineno = lineno;

		/*��ʼ�����ű��ַָ��*/
		for(int i=0;i<t.name.length;i++) 
			t.name[i] = "";
		
	  	/* �������ر��ʽ�����﷨�����ָ��t */
	  	return t;
	}

	/********************************************************/
	/* ������ newDecNode										*/	
	/* ��  �� �������������﷨���ڵ㺯��								*/
	/* ˵  �� �ú���Ϊ�﷨������һ���µ��������ͽ��						*/
	/*        �����﷨���ڵ��Ա��ʼ��								*/
	/********************************************************/
	public treeNode newDecNode()
	{
		/* ���ڴ��ж�̬������䵥Ԫ������ָ��õ�Ԫ���﷨���������ָ��t */
		treeNode t = new treeNode();

		/* ָ�����﷨���ڵ�t��Ա:�������nodekindΪ�������DecK*/
		t.nodeKind = NodeKind.DecK;
	    
		/* ָ�����﷨���ڵ�t��Ա:Դ�����к�linenoΪȫ�ֱ���lineno */
		t.lineno = lineno;

		for(int i=0;i<t.name.length;i++) 
			t.name[i] = "";
	
		/* �����������������﷨���ڵ�ָ��t */
		return t;
	}
	
	/********************************************************/
	/* ������ newFuncNode										*/	
	/* ��  �� �������������﷨���ڵ㺯��								*/
	/* ˵  �� �ú���Ϊ�﷨������һ���µĺ������ͽ��						*/
	/*        �����﷨���ڵ��Ա��ʼ��								*/
	/********************************************************/
	public treeNode newFuncNode()
	{
		/* ���ڴ��ж�̬������䵥Ԫ������ָ��õ�Ԫ���﷨���������ָ��t */
		treeNode t = new treeNode();

		/* ָ�����﷨���ڵ�t��Ա:�������nodekindΪ�������ProcDecK */
		t.nodeKind = NodeKind.FuncDecK;
	
		/* ָ�����﷨���ڵ�t��Ա:Դ�����к�linenoΪȫ�ֱ���lineno */
		t.lineno = lineno;

		for(int i=0;i<t.name.length;i++) 
			t.name[i] = "";

		/* ����������������﷨���ڵ�ָ��t */
		return t;
	}
	
	/********************************************************/
	/* ������ newStmlNode										*/	
	/* ��  �� ��������־�����﷨���ڵ㺯��								*/
	/* ˵  �� �ú���Ϊ�﷨������һ���µ�����־���ͽ��						*/	
	/*        �����﷨���ڵ��Ա��ʼ��								*/
	/********************************************************/
	public treeNode newStmlNode()
	{
		/* ���ڴ��ж�̬������䵥Ԫ������ָ��õ�Ԫ���﷨���������ָ��t */
		treeNode t = new treeNode();

		/* ָ�����﷨���ڵ�t��Ա:�������nodekindΪ�������StmLK */
		t.nodeKind = NodeKind.StmLK;
	
	    /* ָ�����﷨���ڵ�t��Ա:Դ�����к�linenoΪȫ�ֱ���lineno */
		t.lineno = lineno;

		for(int i=0;i<10;i++)
			t.name[i] = "";

		/*����������������﷨���ڵ�ָ��t*/ 
		return t;
	}
	
	/********************************************************/
	/* ������ newStmtNode										*/
	/* ��  �� ������������﷨���ڵ㺯��								*/
	/* ˵  �� �ú���Ϊ�﷨������һ���µ�������ͽ��						*/
	/*        �����﷨���ڵ��Ա��ʼ��								*/
	/********************************************************/
	public treeNode newStmtNode(StmtKind kind)
	{
		/* ���ڴ��ж�̬������䵥Ԫ������ָ��õ�Ԫ���﷨���������ָ��t */
		treeNode t = new treeNode();
	    
		/* ָ�����﷨���ڵ�t��Ա:�������nodekindΪ�������StmtK */
		t.nodeKind = NodeKind.StmtK;
	
		/* ָ�����﷨���ڵ�t��Ա:�������kind.stmtΪ������������kind */
	    t.kind.stmt = kind;
	    
		/* ָ�����﷨���ڵ�t��Ա:Դ�����к�linenoΪȫ�ֱ���lineno */
		t.lineno = lineno;

		for(int i=0;i<10;i++)
			t.name[i] = "";

		/* ����������������﷨���ڵ�ָ��t */
		return t;
	}
	
	/********************************************************/
	/* ������ newExpNode										*/
	/* ��  �� ���ʽ�����﷨���ڵ㴴������								*/
	/* ˵  �� �ú���Ϊ�﷨������һ���µı��ʽ���ͽ��						*/
	/*        �����﷨���ڵ�ĳ�Ա��ʼ��								*/
	/********************************************************/
	public treeNode newExpNode(ExpKind kind)
	{ 
		/* �ڴ��ж�̬������䵥Ԫ������ָ��õ�Ԫ���﷨���ڵ�����ָ��t */
		treeNode t = new treeNode();

		/* ָ�����﷨���ڵ�t��Ա: �������nodekindΪ���ʽ����ExpK */
		t.nodeKind = NodeKind.ExpK;
	
		/* ָ�����﷨���ڵ�t��Ա: ���ʽ����kind.expΪ������������kind */
	  	t.kind.exp = kind;
	
		/* ָ�����﷨���ڵ�t��Ա: Դ�����к�linenoΪȫ�ֱ���lineno */
	    t.lineno = lineno;
	
		/* ָ�����﷨���ڵ�t��Ա: ���ʽΪ��������ʱ�ı�������varkind *
		/* ΪIdV.*/
		t.attr.expAttr.varKind = VarKind.IdV;
	
		/* ָ�����﷨���ڵ�t��Ա: ���ͼ������typeΪVoid */
	 	t.attr.expAttr.type = ExpType.Void;
	
	    for(int i=0;i<10;i++)
			t.name[i] = "";
	
	    /* �������ر��ʽ�����﷨�����ָ��t */
	    return t;
	}
	
	/*************************************************************
	  						LL(1)�﷨��������
	*************************************************************/

	/*���з��ռ���������Ժ���ɲο�LL1�ķ�*/
	public enum NLexType
	{
	  Program,	      Decpart,	      	DecpartMore,	VFDecpart,
	  TypeDecpart,    TypeDef,			BaseType,	    ArrayMore,
	  StructType,     FieldDecList,   	FieldDecMore,	IdList,
	  IdMore,		  StructName,	  	VarDec,	      	VarIdList,
	  VarIdMore,	  FuncDec,			ParamList,		ParamDecList,
	  ParamMore,      Param,		    FormName,		StmList,
	  StmMore,        Stm,				StmDecpart,		IdDecpart,
	  AssCall,		  AssignmentRest,	RightPart,		ConditionalStm,
	  ElseStm,		  LoopStm,		  	InputStm,		Invar,
	  OutputStm,	  ReturnStm,	  	CallStmRest,	ActParamList,
	  ActParamMore,	  RelExp,		 	OtherRelE,		Exp,
	  OtherTerm,	  Term,          	OtherFactor,	Factor,
	  Variable,		  VIdMore,		 	VariMore,		FieldVar,
	  FieldVarMore,	  CmpOp,		  	AddOp,        	MultOp,
	  VFName,		  Name1,			Name2,			Name3
	};

	/*���ռ���������*/
	public static final int NLEXNUM = 60;

	/*�ռ���������*/
	public static final int LEXNUM = 42;

	/*LL1������*/
	public static int LL1Table[][] = new int[NLEXNUM][];

	/********************************************************/
	/* ������  CreatLL1Table									*/
	/* ��  ��  ����LL1������										*/
	/* ˵  ��  ��ʼ���飨���е�ÿһ�Ϊ0������LL1�ķ�   					*/
	/*         �����鸳ֵ���������ú���ֵΪ0��					*/
	/*         ��ʾ�޲���ʽ��ѡ��������Ϊѡ�еĲ���ʽ  					*/
	/********************************************************/
	public void CreatLL1Table()
	{
		/*��ʼ��LL1��Ԫ��*/
		for (int i=0;i<NLEXNUM;i++)
		{
			LL1Table[i] = new int[LEXNUM];
			for (int j=0;j<LEXNUM;j++)
				LL1Table[i][j] = -1;	/* -1������� */
		}

		LL1Table[NLexType.Program.ordinal()][LexType.ENDFILE.ordinal()]	 		= 0;
		LL1Table[NLexType.Program.ordinal()][LexType.ID.ordinal()] 				= 0;
		LL1Table[NLexType.Program.ordinal()][LexType.TYPEDEF.ordinal()] 		= 0;
		LL1Table[NLexType.Program.ordinal()][LexType.INT.ordinal()] 			= 0;
		LL1Table[NLexType.Program.ordinal()][LexType.CHAR.ordinal()] 			= 0;
		LL1Table[NLexType.Program.ordinal()][LexType.STRUCT.ordinal()] 			= 0;
		
		LL1Table[NLexType.Decpart.ordinal()][LexType.ENDFILE.ordinal()] 		= 3;
		LL1Table[NLexType.Decpart.ordinal()][LexType.ID.ordinal()] 				= 2;
		LL1Table[NLexType.Decpart.ordinal()][LexType.TYPEDEF.ordinal()] 		= 1;
		LL1Table[NLexType.Decpart.ordinal()][LexType.INT.ordinal()] 			= 2;
		LL1Table[NLexType.Decpart.ordinal()][LexType.CHAR.ordinal()] 			= 2;
		LL1Table[NLexType.Decpart.ordinal()][LexType.STRUCT.ordinal()] 			= 2;

		LL1Table[NLexType.DecpartMore.ordinal()][LexType.ENDFILE.ordinal()] 	= 3;
		LL1Table[NLexType.DecpartMore.ordinal()][LexType.ID.ordinal()] 			= 4;
		LL1Table[NLexType.DecpartMore.ordinal()][LexType.TYPEDEF.ordinal()] 	= 4;
		LL1Table[NLexType.DecpartMore.ordinal()][LexType.INT.ordinal()] 		= 4;
		LL1Table[NLexType.DecpartMore.ordinal()][LexType.CHAR.ordinal()] 		= 4;
		LL1Table[NLexType.DecpartMore.ordinal()][LexType.STRUCT.ordinal()] 		= 4;
		
		LL1Table[NLexType.VFDecpart.ordinal()][LexType.SEMI.ordinal()]			= 5;
		LL1Table[NLexType.VFDecpart.ordinal()][LexType.COMMA.ordinal()] 		= 5;
		LL1Table[NLexType.VFDecpart.ordinal()][LexType.LPAREN.ordinal()]		= 6;
		
		LL1Table[NLexType.TypeDecpart.ordinal()][LexType.TYPEDEF.ordinal()]		= 7;

		LL1Table[NLexType.TypeDef.ordinal()][LexType.ID.ordinal()]				= 10;
		LL1Table[NLexType.TypeDef.ordinal()][LexType.INT.ordinal()]				= 8;
		LL1Table[NLexType.TypeDef.ordinal()][LexType.CHAR.ordinal()]			= 8;
		LL1Table[NLexType.TypeDef.ordinal()][LexType.STRUCT.ordinal()]			= 9;
		
		LL1Table[NLexType.BaseType.ordinal()][LexType.INT.ordinal()]			= 11;
		LL1Table[NLexType.BaseType.ordinal()][LexType.CHAR.ordinal()]			= 12;

		LL1Table[NLexType.ArrayMore.ordinal()][LexType.ID.ordinal()]			= 13;
		LL1Table[NLexType.ArrayMore.ordinal()][LexType.LMIDPAREN.ordinal()]		= 14;
		LL1Table[NLexType.ArrayMore.ordinal()][LexType.SEMI.ordinal()]			= 13;

		LL1Table[NLexType.StructType.ordinal()][LexType.STRUCT.ordinal()] 		= 15;

		LL1Table[NLexType.FieldDecList.ordinal()][LexType.INT.ordinal()]		= 16;
		LL1Table[NLexType.FieldDecList.ordinal()][LexType.CHAR.ordinal()] 		= 16;
		
		LL1Table[NLexType.FieldDecMore.ordinal()][LexType.INT.ordinal()]		= 18;
		LL1Table[NLexType.FieldDecMore.ordinal()][LexType.CHAR.ordinal()] 		= 18;
		LL1Table[NLexType.FieldDecMore.ordinal()][LexType.RBIGPAREN.ordinal()] 	= 17;

		LL1Table[NLexType.IdList.ordinal()][LexType.ID.ordinal()]				= 19;

		LL1Table[NLexType.IdMore.ordinal()][LexType.SEMI.ordinal()]				= 20;
		LL1Table[NLexType.IdMore.ordinal()][LexType.COMMA.ordinal()] 			= 21;

		LL1Table[NLexType.StructName.ordinal()][LexType.ID.ordinal()]			= 23;
		LL1Table[NLexType.StructName.ordinal()][LexType.LBIGPAREN.ordinal()] 	= 22;

		LL1Table[NLexType.VarDec.ordinal()][LexType.SEMI.ordinal()]				= 24;
		LL1Table[NLexType.VarDec.ordinal()][LexType.COMMA.ordinal()] 			= 24;

		LL1Table[NLexType.VarIdList.ordinal()][LexType.ID.ordinal()]			= 25;

		LL1Table[NLexType.VarIdMore.ordinal()][LexType.SEMI.ordinal()]			= 26;
		LL1Table[NLexType.VarIdMore.ordinal()][LexType.COMMA.ordinal()] 		= 27;

		LL1Table[NLexType.FuncDec.ordinal()][LexType.LPAREN.ordinal()]			= 28;

		LL1Table[NLexType.ParamList.ordinal()][LexType.ID.ordinal()] 			= 30;
		LL1Table[NLexType.ParamList.ordinal()][LexType.INT.ordinal()] 			= 30;
		LL1Table[NLexType.ParamList.ordinal()][LexType.CHAR.ordinal()] 			= 30;
		LL1Table[NLexType.ParamList.ordinal()][LexType.STRUCT.ordinal()] 		= 30;
		LL1Table[NLexType.ParamList.ordinal()][LexType.RPAREN.ordinal()] 		= 29;

		LL1Table[NLexType.ParamDecList.ordinal()][LexType.ID.ordinal()] 		= 31;
		LL1Table[NLexType.ParamDecList.ordinal()][LexType.INT.ordinal()] 		= 31;
		LL1Table[NLexType.ParamDecList.ordinal()][LexType.CHAR.ordinal()] 		= 31;
		LL1Table[NLexType.ParamDecList.ordinal()][LexType.STRUCT.ordinal()] 	= 31;

		LL1Table[NLexType.ParamMore.ordinal()][LexType.RPAREN.ordinal()] 		= 32;
		LL1Table[NLexType.ParamMore.ordinal()][LexType.COMMA.ordinal()] 		= 33;

		LL1Table[NLexType.Param.ordinal()][LexType.ID.ordinal()] 				= 34;
		LL1Table[NLexType.Param.ordinal()][LexType.INT.ordinal()] 				= 34;
		LL1Table[NLexType.Param.ordinal()][LexType.CHAR.ordinal()] 				= 34;
		LL1Table[NLexType.Param.ordinal()][LexType.STRUCT.ordinal()] 			= 34;

		LL1Table[NLexType.FormName.ordinal()][LexType.ID.ordinal()] 			= 35;

		LL1Table[NLexType.StmList.ordinal()][LexType.ID.ordinal()] 				= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.TYPEDEF.ordinal()] 		= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.INT.ordinal()] 			= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.CHAR.ordinal()] 			= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.STRUCT.ordinal()] 			= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.LBIGPAREN.ordinal()] 		= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.IF.ordinal()] 				= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.WHILE.ordinal()] 			= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.FOR.ordinal()] 			= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.CIN.ordinal()] 			= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.COUT.ordinal()] 			= 36;
		LL1Table[NLexType.StmList.ordinal()][LexType.RETURN.ordinal()] 			= 36;

		LL1Table[NLexType.StmMore.ordinal()][LexType.ID.ordinal()] 				= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.TYPEDEF.ordinal()] 		= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.INT.ordinal()] 			= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.CHAR.ordinal()] 			= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.STRUCT.ordinal()] 			= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.LBIGPAREN.ordinal()] 		= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.RBIGPAREN.ordinal()] 		= 37;
		LL1Table[NLexType.StmMore.ordinal()][LexType.IF.ordinal()] 				= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.WHILE.ordinal()] 			= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.FOR.ordinal()] 			= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.CIN.ordinal()] 			= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.COUT.ordinal()] 			= 38;
		LL1Table[NLexType.StmMore.ordinal()][LexType.RETURN.ordinal()] 			= 38;

		LL1Table[NLexType.Stm.ordinal()][LexType.ID.ordinal()] 					= 39;
		LL1Table[NLexType.Stm.ordinal()][LexType.TYPEDEF.ordinal()] 			= 39;
		LL1Table[NLexType.Stm.ordinal()][LexType.INT.ordinal()] 				= 39;
		LL1Table[NLexType.Stm.ordinal()][LexType.CHAR.ordinal()] 				= 39;
		LL1Table[NLexType.Stm.ordinal()][LexType.STRUCT.ordinal()] 				= 39;
		LL1Table[NLexType.Stm.ordinal()][LexType.LBIGPAREN.ordinal()] 			= 45;
		LL1Table[NLexType.Stm.ordinal()][LexType.IF.ordinal()] 					= 40;
		LL1Table[NLexType.Stm.ordinal()][LexType.WHILE.ordinal()] 				= 41;
		LL1Table[NLexType.Stm.ordinal()][LexType.FOR.ordinal()] 				= 41;
		LL1Table[NLexType.Stm.ordinal()][LexType.CIN.ordinal()] 				= 42;
		LL1Table[NLexType.Stm.ordinal()][LexType.COUT.ordinal()] 				= 43;
		LL1Table[NLexType.Stm.ordinal()][LexType.RETURN.ordinal()] 				= 44;

		LL1Table[NLexType.StmDecpart.ordinal()][LexType.ID.ordinal()] 			= 49;
		LL1Table[NLexType.StmDecpart.ordinal()][LexType.TYPEDEF.ordinal()] 		= 46;
		LL1Table[NLexType.StmDecpart.ordinal()][LexType.INT.ordinal()] 			= 47;
		LL1Table[NLexType.StmDecpart.ordinal()][LexType.CHAR.ordinal()] 		= 47;
		LL1Table[NLexType.StmDecpart.ordinal()][LexType.STRUCT.ordinal()] 		= 48;

		LL1Table[NLexType.IdDecpart.ordinal()][LexType.ID.ordinal()] 			= 51;
		LL1Table[NLexType.IdDecpart.ordinal()][LexType.LMIDPAREN.ordinal()] 	= 50;
		LL1Table[NLexType.IdDecpart.ordinal()][LexType.LPAREN.ordinal()] 		= 50;
		LL1Table[NLexType.IdDecpart.ordinal()][LexType.ASSIGN.ordinal()] 		= 50;
		LL1Table[NLexType.IdDecpart.ordinal()][LexType.DOT.ordinal()] 			= 50;

		LL1Table[NLexType.AssCall.ordinal()][LexType.LMIDPAREN.ordinal()] 		= 52;
		LL1Table[NLexType.AssCall.ordinal()][LexType.LPAREN.ordinal()] 			= 53;
		LL1Table[NLexType.AssCall.ordinal()][LexType.ASSIGN.ordinal()] 			= 52;
		LL1Table[NLexType.AssCall.ordinal()][LexType.DOT.ordinal()] 			= 52;

		LL1Table[NLexType.AssignmentRest.ordinal()][LexType.LMIDPAREN.ordinal()]= 54;
		LL1Table[NLexType.AssignmentRest.ordinal()][LexType.ASSIGN.ordinal()] 	= 54;
		LL1Table[NLexType.AssignmentRest.ordinal()][LexType.DOT.ordinal()] 		= 54;

		LL1Table[NLexType.RightPart.ordinal()][LexType.ID.ordinal()] 			= 55;
		LL1Table[NLexType.RightPart.ordinal()][LexType.INTC.ordinal()] 			= 55;
		LL1Table[NLexType.RightPart.ordinal()][LexType.LPAREN.ordinal()] 		= 55;
		LL1Table[NLexType.RightPart.ordinal()][LexType.CHARC.ordinal()] 		= 56;

		LL1Table[NLexType.ConditionalStm.ordinal()][LexType.IF.ordinal()] 		= 57;

		LL1Table[NLexType.ElseStm.ordinal()][LexType.ID.ordinal()] 				= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.TYPEDEF.ordinal()] 		= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.INT.ordinal()] 			= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.CHAR.ordinal()] 			= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.STRUCT.ordinal()] 			= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.RBIGPAREN.ordinal()] 		= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.IF.ordinal()] 				= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.WHILE.ordinal()] 			= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.FOR.ordinal()] 			= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.CIN.ordinal()] 			= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.COUT.ordinal()] 			= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.RETURN.ordinal()] 			= 99;
		LL1Table[NLexType.ElseStm.ordinal()][LexType.ELSE.ordinal()] 			= 100;

		LL1Table[NLexType.LoopStm.ordinal()][LexType.WHILE.ordinal()] 			= 58;
		LL1Table[NLexType.LoopStm.ordinal()][LexType.FOR.ordinal()] 			= 59;

		LL1Table[NLexType.InputStm.ordinal()][LexType.CIN.ordinal()] 			= 60;

		LL1Table[NLexType.Invar.ordinal()][LexType.ID.ordinal()] 				= 61;

		LL1Table[NLexType.OutputStm.ordinal()][LexType.COUT.ordinal()] 			= 62;

		LL1Table[NLexType.ReturnStm.ordinal()][LexType.RETURN.ordinal()] 		= 63;

		LL1Table[NLexType.CallStmRest.ordinal()][LexType.LPAREN.ordinal()] 		= 64;

		LL1Table[NLexType.ActParamList.ordinal()][LexType.ID.ordinal()] 		= 66;
		LL1Table[NLexType.ActParamList.ordinal()][LexType.INTC.ordinal()] 		= 66;
		LL1Table[NLexType.ActParamList.ordinal()][LexType.LPAREN.ordinal()] 	= 66;
		LL1Table[NLexType.ActParamList.ordinal()][LexType.RPAREN.ordinal()] 	= 65;

		LL1Table[NLexType.ActParamMore.ordinal()][LexType.COMMA.ordinal()] 		= 68;
		LL1Table[NLexType.ActParamMore.ordinal()][LexType.RPAREN.ordinal()] 	= 67;

		LL1Table[NLexType.RelExp.ordinal()][LexType.ID.ordinal()] 				= 69;
		LL1Table[NLexType.RelExp.ordinal()][LexType.INTC.ordinal()] 			= 69;
		LL1Table[NLexType.RelExp.ordinal()][LexType.LPAREN.ordinal()] 			= 69;

		LL1Table[NLexType.OtherRelE.ordinal()][LexType.LT.ordinal()] 			= 70;
		LL1Table[NLexType.OtherRelE.ordinal()][LexType.GT.ordinal()] 			= 70;
		LL1Table[NLexType.OtherRelE.ordinal()][LexType.LE.ordinal()] 			= 70;
		LL1Table[NLexType.OtherRelE.ordinal()][LexType.GE.ordinal()] 			= 70;
		LL1Table[NLexType.OtherRelE.ordinal()][LexType.EQ.ordinal()] 			= 70;
		LL1Table[NLexType.OtherRelE.ordinal()][LexType.NEQ.ordinal()] 			= 70;

		LL1Table[NLexType.Exp.ordinal()][LexType.ID.ordinal()] 					= 71;
		LL1Table[NLexType.Exp.ordinal()][LexType.INTC.ordinal()] 				= 71;
		LL1Table[NLexType.Exp.ordinal()][LexType.LPAREN.ordinal()] 				= 71;

		LL1Table[NLexType.OtherTerm.ordinal()][LexType.RMIDPAREN.ordinal()] 	= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.SEMI.ordinal()] 			= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.COMMA.ordinal()] 		= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.RPAREN.ordinal()] 		= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.LT.ordinal()] 			= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.GT.ordinal()] 			= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.LE.ordinal()] 			= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.GE.ordinal()] 			= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.EQ.ordinal()] 			= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.NEQ.ordinal()] 			= 72;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.PLUS.ordinal()] 			= 73;
		LL1Table[NLexType.OtherTerm.ordinal()][LexType.MINUS.ordinal()] 		= 73;

		LL1Table[NLexType.Term.ordinal()][LexType.ID.ordinal()] 				= 74;
		LL1Table[NLexType.Term.ordinal()][LexType.INTC.ordinal()] 				= 74;
		LL1Table[NLexType.Term.ordinal()][LexType.LPAREN.ordinal()] 			= 74;

		LL1Table[NLexType.OtherFactor.ordinal()][LexType.RMIDPAREN.ordinal()] 	= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.SEMI.ordinal()] 		= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.COMMA.ordinal()] 		= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.RPAREN.ordinal()] 		= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.LT.ordinal()] 			= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.GT.ordinal()] 			= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.LE.ordinal()] 			= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.GE.ordinal()] 			= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.EQ.ordinal()] 			= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.NEQ.ordinal()] 		= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.PLUS.ordinal()] 		= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.MINUS.ordinal()] 		= 75;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.TIMES.ordinal()] 		= 76;
		LL1Table[NLexType.OtherFactor.ordinal()][LexType.OVER.ordinal()] 		= 76;

		LL1Table[NLexType.Factor.ordinal()][LexType.ID.ordinal()] 				= 79;
		LL1Table[NLexType.Factor.ordinal()][LexType.INTC.ordinal()] 			= 78;
		LL1Table[NLexType.Factor.ordinal()][LexType.LPAREN.ordinal()] 			= 77;

		LL1Table[NLexType.Variable.ordinal()][LexType.ID.ordinal()] 			= 80;

		LL1Table[NLexType.VIdMore.ordinal()][LexType.LMIDPAREN.ordinal()] 		= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.RMIDPAREN.ordinal()] 		= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.SEMI.ordinal()] 			= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.COMMA.ordinal()] 			= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.LPAREN.ordinal()] 			= 82;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.RPAREN.ordinal()] 			= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.DOT.ordinal()] 			= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.LT.ordinal()] 				= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.GT.ordinal()] 				= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.LE.ordinal()] 				= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.GE.ordinal()] 				= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.EQ.ordinal()] 				= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.NEQ.ordinal()] 			= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.PLUS.ordinal()] 			= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.MINUS.ordinal()] 			= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.TIMES.ordinal()] 			= 81;
		LL1Table[NLexType.VIdMore.ordinal()][LexType.OVER.ordinal()] 			= 81;

		LL1Table[NLexType.VariMore.ordinal()][LexType.LMIDPAREN.ordinal()] 		= 84;
		LL1Table[NLexType.VariMore.ordinal()][LexType.RMIDPAREN.ordinal()] 		= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.SEMI.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.COMMA.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.RPAREN.ordinal()] 		= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.ASSIGN.ordinal()] 		= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.DOT.ordinal()] 			= 85;
		LL1Table[NLexType.VariMore.ordinal()][LexType.LT.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.GT.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.LE.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.GE.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.EQ.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.NEQ.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.PLUS.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.MINUS.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.TIMES.ordinal()] 			= 83;
		LL1Table[NLexType.VariMore.ordinal()][LexType.OVER.ordinal()] 			= 83;

		LL1Table[NLexType.FieldVar.ordinal()][LexType.ID.ordinal()] 			= 86;

		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.LMIDPAREN.ordinal()] 	= 88;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.RMIDPAREN.ordinal()] 	= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.SEMI.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.COMMA.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.RPAREN.ordinal()] 	= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.ASSIGN.ordinal()] 	= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.LT.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.GT.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.LE.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.GE.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.EQ.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.NEQ.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.PLUS.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.MINUS.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.TIMES.ordinal()] 		= 87;
		LL1Table[NLexType.FieldVarMore.ordinal()][LexType.OVER.ordinal()] 		= 87;

		LL1Table[NLexType.CmpOp.ordinal()][LexType.LT.ordinal()] 				= 89;
		LL1Table[NLexType.CmpOp.ordinal()][LexType.GT.ordinal()] 				= 90;
		LL1Table[NLexType.CmpOp.ordinal()][LexType.LE.ordinal()] 				= 91;
		LL1Table[NLexType.CmpOp.ordinal()][LexType.GE.ordinal()] 				= 92;
		LL1Table[NLexType.CmpOp.ordinal()][LexType.EQ.ordinal()] 				= 93;
		LL1Table[NLexType.CmpOp.ordinal()][LexType.NEQ.ordinal()] 				= 94;

		LL1Table[NLexType.AddOp.ordinal()][LexType.PLUS.ordinal()] 				= 95;
		LL1Table[NLexType.AddOp.ordinal()][LexType.MINUS.ordinal()] 			= 96;

		LL1Table[NLexType.MultOp.ordinal()][LexType.TIMES.ordinal()] 			= 97;
		LL1Table[NLexType.MultOp.ordinal()][LexType.OVER.ordinal()] 			= 98;

		LL1Table[NLexType.VFName.ordinal()][LexType.ID.ordinal()] 				= 101;
		
		LL1Table[NLexType.Name1.ordinal()][LexType.ID.ordinal()] 				= 102;

		LL1Table[NLexType.Name2.ordinal()][LexType.ID.ordinal()] 				= 103;
		
		LL1Table[NLexType.Name3.ordinal()][LexType.ID.ordinal()] 				= 104;
	}
}