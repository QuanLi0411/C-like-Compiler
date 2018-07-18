/******************************************************
 	�ļ�	parseLL1.java
 	˵��	��������LL1�﷨������ʵ��
 	����	�����﷨��
 		�����﷨������
 	����	��ѧ��
******************************************************/

package BXC;
import java.util.Stack;
import BXC.Global.DecKind;
import BXC.Global.ExpKind;
import BXC.Global.ExpType;
import BXC.Global.LexType;
import BXC.Global.NLexType;
import BXC.Global.NodeKind;
import BXC.Global.StmtKind;
import BXC.Global.VarKind;
import BXC.Global.treeNode;

public class ParseLL1 {

	public static void main(String[] args)
	{
		ParseLL1 myParseLL1 = new ParseLL1();
		myParseLL1.run();
	}

	public static Global global = new Global();
	
	public static Scanner myScanner = new Scanner();

	/* ʵ��LL1�����õķ���ջ����ŵ����ռ����ͷ��ռ��� */
	class StackNode {
		/* flagΪ0����ʾջ������Ϊ���ռ���, flagΪ1����ʾջ������Ϊ�ռ��� */
		boolean flag;
		NLexType nLexVar; /* ���ռ������� */
		LexType tLexVar; /* �ռ������� */
	}
	public Stack<StackNode> stack = new Stack<StackNode>();

	/* Ϊ�����﷨�������ָ��ջ */
	public Stack<treeNode> stackPA = new Stack<treeNode>();

	/* ������ */
	public Token token;
	
	public int index = 0;

	public String temp_name;
	
	public boolean isForLoop;
	/********************************************************/
	/* 	����	push											*/
	/* 	����	ѹջ��stack										*/
	/* 	˵��	ʹ���˺�������										*/
	/********************************************************/
	void push(NLexType j) {
		StackNode p = new StackNode();
		p.nLexVar = j;
		p.flag = false;
		stack.push(p);
	}
	void push(LexType j) {
		StackNode p = new StackNode();
		p.tLexVar = j;
		p.flag = true;
		stack.push(p);
	}
	
	/********************************************************/
	/* 	����	syntaxError										*/
	/* 	����	�﷨��������										*/
	/* 	˵��	����������messageָ���Ĵ�����Ϣ��ʽ��д���б��ļ�listing		*/
	/*		���ô���׷�ٱ�־ErrorΪtrue							*/
	/********************************************************/
	public void syntaxError(String message)
	{
		if(token==null) token=new Token();
		global.pw.print(">>> error :   ");
		global.pw.printf("Syntax error at line %d, %s: %s",token.lineshow,message,token.Sem);
		global.pw.println();
		global.pw.flush();
		Global.Error = true;
		System.exit(0);
	}

	/********************************************************/
	/* 	����	gettoken					     				*/
	/* 	����	��Token������ȡ��һ��Token	                        */										
	/* 	˵��	���ļ��д��Token����������ȡһ�����ʣ���Ϊ��ǰ����.      		*/
	/********************************************************/
	public void getToken()
	{
		try
		{token = myScanner.tokenChain.get(index);}
		catch(IndexOutOfBoundsException e)
		{syntaxError("you must lost something");}
		index ++;
		Global.lineno = token.lineshow;
	}

	/********************************************************/
	/* 	 			������LL(1)����ʽ��Ӧ�����к���					*/
	/********************************************************/
	void process0()
	{
		push(NLexType.Decpart);
	}
	void process1()
	{
		push(NLexType.DecpartMore);
		push(NLexType.TypeDecpart);
	}
	void process2()
	{
		push(NLexType.DecpartMore);
		push(NLexType.VFDecpart);
		push(NLexType.VFName);
		push(NLexType.TypeDef);
	}
	void process101()
	{
		push(LexType.ID);
		
		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.DecK;
		t.lineno = Global.lineno;
		t.name[t.idnum] = token.Sem;
		t.idnum ++;
	}
	void process3()
	{
		stackPA.pop();	//��ʱջΪ�գ��ڵ�ȫ������
	}
	void process4()
	{
		push(NLexType.Decpart);

		treeNode t = stackPA.pop();
		t.sibling = global.newFuncNode();
		stackPA.push(t.sibling);	//�л�����һ���������ͽڵ�
	}
	void process5()
	{
		push(NLexType.VarDec);
	}
	void process6()
	{
		push(NLexType.FuncDec);
	}
	void process7()
	{
		push(LexType.SEMI);
		push(NLexType.TypeDef);
		push(LexType.ID);
		push(LexType.TYPEDEF);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.TypeK;
		t.lineno = Global.lineno;
		t.name[t.idnum] = myScanner.tokenChain.get(index).Sem;
		t.idnum ++;
	}
	void process8()
	{
		push(NLexType.ArrayMore);
		push(NLexType.BaseType);
	}
	void process9()
	{
		push(NLexType.StructType);
	}
	void process10()
	{
		push(LexType.ID);
		
		treeNode t = stackPA.lastElement();
	    t.kind.dec = DecKind.IdK;
		t.attr.type_name = token.Sem;
	}
	void process11()
	{
		push(LexType.INT);
		
		treeNode t = stackPA.lastElement();
        t.kind.dec = DecKind.IntK;
	}
	void process12()
	{
		push(LexType.CHAR);

		treeNode t = stackPA.lastElement();
        t.kind.dec = DecKind.CharK;
	}
	void process13()
	{}
	void process14()
	{
		push(LexType.RMIDPAREN);
		push(LexType.INTC);
		push(LexType.LMIDPAREN);

		treeNode t = stackPA.lastElement();
		t.attr.arrayAttr.size = Integer.parseInt(myScanner.tokenChain.get(index).Sem);
		t.attr.arrayAttr.childType = t.kind.dec;
        t.kind.dec = DecKind.ArrayK;
	}
	void process15()
	{
		push(LexType.RBIGPAREN);
		push(NLexType.FieldDecList);
		push(LexType.LBIGPAREN);
		push(NLexType.StructName);
		push(LexType.STRUCT);

		treeNode t = stackPA.lastElement();
        t.kind.dec = DecKind.StructK;
		t.child[0] = global.newDecNode();
		stackPA.push(t.child[0]);	//ѹ��ṹ�����������ͽڵ�
	}
	void process16()
	{
		push(NLexType.FieldDecMore);
		push(LexType.SEMI);
		push(NLexType.IdList);
		push(NLexType.ArrayMore);
		push(NLexType.BaseType);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.DecK;
		t.lineno = Global.lineno;
	}
	void process17()
	{
		stackPA.pop();	//�����ṹ�����������ͽڵ�
	}
	void process18()
	{
		push(NLexType.FieldDecList);
		
		treeNode t = stackPA.pop();
		t.sibling = global.newDecNode();
		stackPA.push(t.sibling);	//�л�����һ���ṹ�����������ͽڵ�
	}
	void process19()
	{
		push(NLexType.IdMore);
		push(LexType.ID);
		
		treeNode t = stackPA.lastElement();
		t.name[t.idnum] = token.Sem;
		t.idnum ++;
	}
	void process20()
	{}
	void process21()
	{
		push(NLexType.IdList);
		push(LexType.COMMA);
	}
	void process22()
	{}
	void process23()
	{
		push(LexType.ID);

		treeNode t = stackPA.lastElement();
		t.name[t.idnum] = token.Sem;
		t.idnum ++;
	}
	void process24()
	{
		push(LexType.SEMI);
		push(NLexType.VarIdMore);
	}
	void process26()
	{}
	void process27()
	{
		push(NLexType.VarIdList);
		push(LexType.COMMA);
	}
	void process25()
	{
		push(NLexType.VarIdMore);
		push(LexType.ID);
		
		treeNode t = stackPA.lastElement();
		t.name[t.idnum] = token.Sem;
		t.idnum ++;
	}
	void process28()
	{
		push(LexType.RBIGPAREN);
		push(NLexType.StmList);
		push(LexType.LBIGPAREN);
		push(LexType.RPAREN);
		push(NLexType.ParamList);
		push(LexType.LPAREN);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.FuncDecK;
		t.attr.funcAttr.returnType = t.kind.dec;
		t.child[1] = global.newStmlNode();
		t.child[1].lineno = Global.lineno;
		t.child[1].child[0] = global.newRootNode();
		t.child[0] = global.newDecNode();
		stackPA.push(t.child[1].child[0]);	//ѹ�뺯�������䲿�ֵĵ�һ���ڵ�
		stackPA.push(t.child[0]);	//ѹ���һ��������ڵ�
	}
	void process29()
	{
		stackPA.pop();	//������Ϊ��ʱ������������ڵ�
		treeNode t = stackPA.pop();
		stackPA.lastElement().child[0] = null;
		stackPA.push(t);
	}
	void process30()
	{
		push(NLexType.ParamDecList);
	}
	void process31()
	{
		push(NLexType.ParamMore);
		push(NLexType.Param);
	}
	void process32()
	{
		stackPA.pop();	//������Ϊ��ʱ������������ڵ�
	}
	void process33()
	{
		push(NLexType.ParamDecList);
		push(LexType.COMMA);
		
		treeNode t = stackPA.pop();
		t.sibling = global.newDecNode();
		stackPA.push(t.sibling);	//�л�����һ������ڵ�
	}
	void process34()
	{
		push(NLexType.FormName);
		push(NLexType.TypeDef);
	}
	void process35()
	{
		push(LexType.ID);

		treeNode t = stackPA.lastElement();
		t.name[t.idnum] = token.Sem;
		t.idnum ++;
	}
	void process36()
	{
		push(NLexType.StmMore);
		push(NLexType.Stm);
	}
	void process37()
	{
		stackPA.pop();	//�������������䲿�ֽڵ�
	}
	void process38()
	{
		push(NLexType.StmList);

		treeNode t = stackPA.pop();
		t.sibling = global.newRootNode();
		stackPA.push(t.sibling);	//�л������������䲿�ֵ���һ�ڵ�
	}
	void process39()
	{
		push(NLexType.StmDecpart);
	}
	void process40()
	{
		push(NLexType.ConditionalStm);
	}
	void process41()
	{
		push(NLexType.LoopStm); 
	}
	void process42()
	{
		push(NLexType.InputStm);
	}
	void process43()
	{
		push(NLexType.OutputStm);
	}
	void process44()
	{
		push(NLexType.ReturnStm);
	}
	void process45()
	{
		push(LexType.RBIGPAREN);
		push(NLexType.StmList);
		push(LexType.LBIGPAREN);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmLK;
		t.child[0] = global.newRootNode();
		stackPA.push(t.child[0]);	//ѹ����伯�ϣ�StmLK���ĵ�һ����Ա��䣨StmtK���ڵ�
	}
	void process46()
	{
		push(NLexType.TypeDecpart);
	}
	void process47()
	{
		push(NLexType.VarDec);
		push(NLexType.Name1);
		push(NLexType.ArrayMore);
		push(NLexType.BaseType);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.DecK;
		t.lineno = Global.lineno;
	}
	void process102()
	{
		push(LexType.ID);

		treeNode t = stackPA.lastElement();
		t.name[t.idnum] = token.Sem;
		t.idnum ++;
	}
	void process48()
	{
		push(NLexType.VarDec);
		push(NLexType.Name2);
		push(NLexType.StructType);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.DecK;
		t.lineno = Global.lineno;
	}
	void process103()
	{
		push(LexType.ID);

		treeNode t = stackPA.lastElement();
		t.name[t.idnum] = token.Sem;
		t.idnum ++;
	}
	void process49()
	{
		push(NLexType.IdDecpart);
		push(LexType.ID);
		
		temp_name = token.Sem;
	}
	void process50()
	{
		push(NLexType.AssCall);
	}
	void process51()
	{
		push(NLexType.VarDec);
		push(LexType.ID);
		
		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.DecK;
		t.kind.dec = DecKind.IdK;
		t.attr.type_name = temp_name;
		t.lineno = Global.lineno;
        t.name[t.idnum] = token.Sem;
		t.idnum ++;
	}
	void process52()
	{
		push(NLexType.AssignmentRest);
	}
	void process53()
	{
		push(LexType.SEMI);
		push(NLexType.CallStmRest);
	}
	void process54()
	{
		push(LexType.SEMI);
		push(LexType.END_POP);	//��ֵ��ʽ�ڵ���ɣ���Ҫ����
		push(NLexType.RightPart);
		push(LexType.ASSIGN);
		push(LexType.END_POP);	//��ֵ��ʽ�ڵ���ɣ���Ҫ����
		push(NLexType.VariMore);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmtK;
		t.kind.stmt = StmtKind.AssignK;
		t.lineno = Global.lineno;
		/*�����һ�����ӽ�㣬Ϊ�������ʽ���ͽڵ�*/
		t.child[0] = global.newExpNode(ExpKind.VariK);
		t.child[0].lineno = Global.lineno;
		if (isForLoop)
		{
			t.child[0].name[0] = t.name[0];
			isForLoop = false;
		}
		else
			t.child[0].name[0] = temp_name;
		t.child[0].idnum ++;
		t.child[1] = global.newRootNode();
		stackPA.push(t.child[1]);	//ѹ�븳ֵ��ʽ�ڵ�
		stackPA.push(t.child[0]);	//ѹ�븳ֵ��ʽ�ڵ�
	}
	void process55()
	{
		push(NLexType.Exp);
	}
	void process56()
	{
		push(LexType.CHARC);
		
		treeNode t = stackPA.lastElement();
	    t.nodeKind = NodeKind.ExpK;
	    t.kind.exp = ExpKind.ConstK;
	    if(t.attr.expAttr.varKind == null)
	    	t.attr.expAttr.varKind = VarKind.IdV;
	    if(t.attr.expAttr.type == null)
	    	t.attr.expAttr.type = ExpType.Char;
		t.attr.expAttr.char_val = token.Sem.charAt(0);
	    t.lineno = Global.lineno;
	}
	void process57()
	{
		push(NLexType.ElseStm);
		push(LexType.END_POP);	//IF(0) 1 ELSE 2 �е�1������ɣ���Ҫ����
		push(NLexType.Stm);
		push(LexType.RPAREN);
		push(LexType.END_POP);	//IF(0) 1 ELSE 2 �е�0������ɣ���Ҫ����
		push(NLexType.RelExp);
		push(LexType.LPAREN);
		push(LexType.IF);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmtK;
		t.kind.stmt = StmtKind.IfK;
		t.lineno = Global.lineno;
		t.child[1] = global.newRootNode();
		t.child[0] = global.newRootNode();
		stackPA.push(t.child[1]);	//IF(0) 1 ELSE 2 �е�1����
		stackPA.push(t.child[0]);	//IF(0) 1 ELSE 2 �е�0����
	}
	void process99()
	{}
	void process100()
	{
		push(LexType.END_POP);	//IF(0) 1 ELSE 2 �е�2������ɣ���Ҫ����
		push(NLexType.Stm);
		push(LexType.ELSE);
		
		treeNode t = stackPA.lastElement();
		t.child[2] = global.newRootNode();
		stackPA.push(t.child[2]);	//IF(0) 1 ELSE 2 �е�2����
	}
	void process58()
	{
		push(LexType.END_POP);	//WHILE(0) 1 �е�1������ɣ���Ҫ����
		push(NLexType.Stm);
		push(LexType.RPAREN);
		push(LexType.END_POP);	//WHILE(0) 1 �е�0������ɣ���Ҫ����
		push(NLexType.RelExp);
		push(LexType.LPAREN);
		push(LexType.WHILE);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmtK;
		t.kind.stmt = StmtKind.WhileK;
		t.lineno = Global.lineno;
		t.child[0] = global.newRootNode();
		t.child[1] = global.newRootNode();
		stackPA.push(t.child[1]);	//WHILE(0) 1 �е�1����
		stackPA.push(t.child[0]);	//WHILE(0) 1 �е�0����
	}
	void process59()
	{
		push(LexType.END_POP);	//FOR(0 1 2) 3 �е�3������ɣ���Ҫ����
		push(NLexType.Stm);
		push(LexType.RPAREN);
		push(LexType.END_POP);	//FOR(0 1 2) 3 �е�2������ɣ���Ҫ����
		push(NLexType.AssCall);
		push(NLexType.Name3);
		push(LexType.SEMI);
		push(LexType.END_POP);	//FOR(0 1 2) 3 �е�1������ɣ���Ҫ����
		push(NLexType.RelExp);
		push(LexType.END_POP);	//FOR(0 1 2) 3 �е�0������ɣ���Ҫ����
		push(NLexType.AssCall);
		push(LexType.ID);
		push(LexType.LPAREN);
		push(LexType.FOR);
		
		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmtK;
		t.kind.stmt = StmtKind.ForK;
		t.lineno = Global.lineno;
		t.child[0] = global.newRootNode();
		t.child[0].name[0] = myScanner.tokenChain.get(index+1).Sem;
		isForLoop = true;
		t.child[1] = global.newRootNode();
		t.child[2] = global.newRootNode();
		t.child[3] = global.newRootNode();
		stackPA.push(t.child[3]);	//FOR(0 1 2) 3 �е�3����
		stackPA.push(t.child[2]);	//FOR(0 1 2) 3 �е�2����
		stackPA.push(t.child[1]);	//FOR(0 1 2) 3 �е�1����
		stackPA.push(t.child[0]);	//FOR(0 1 2) 3 �е�0����
	}
	void process104()
	{
		push(LexType.ID);

		temp_name = token.Sem;
	}
	void process60()
	{
		push(LexType.SEMI);
		push(NLexType.Invar);
		push(LexType.IN);
		push(LexType.CIN);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmtK;
		t.kind.stmt = StmtKind.CinK;
	}
	void process61()
	{
		push(LexType.ID);

		treeNode t = stackPA.lastElement();
		t.name[0] = token.Sem;
        t.idnum ++;
		t.lineno = Global.lineno;
	}
	void process62()
	{
		push(LexType.SEMI);
		push(LexType.END_POP);	//������ʽ������ɣ���Ҫ����
		push(NLexType.Exp);
		push(LexType.OUT);
		push(LexType.COUT);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmtK;
		t.kind.stmt = StmtKind.CoutK;
		t.lineno = Global.lineno;
		t.child[0] = global.newRootNode();
		stackPA.push(t.child[0]);	//ѹ��������ʽ��ExpK��
	}
	void process63()
	{
		push(LexType.SEMI);
		push(LexType.END_POP);	//���ر��ʽ������ɣ���Ҫ����
		push(NLexType.Exp);
		push(LexType.RETURN);
		
		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmtK;
		t.kind.stmt = StmtKind.ReturnK;
		t.lineno = Global.lineno;
		t.child[0] = global.newRootNode();
		stackPA.push(t.child[0]);	//ѹ�뷵�ر��ʽ��ExpK��
	}
	void process64()
	{
		push(LexType.RPAREN);
		push(NLexType.ActParamList);
		push(LexType.LPAREN);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.StmtK;
		t.kind.stmt = StmtKind.CallK;
		t.lineno = Global.lineno;
		/*�������Ľ��Ҳ�ñ��ʽ���ͽ��*/
		t.child[0] = global.newExpNode(ExpKind.VariK);
		t.child[0].lineno = Global.lineno;
		t.child[0].name[0] = temp_name;
		t.child[0].idnum ++;
		t.child[1] = global.newRootNode();
		stackPA.push(t.child[1]);	//ѹ���һ��ʵ�α�ڵ�
	}
	void process65()
	{
		stackPA.pop();	//ʵ�α�Ϊ��ʱ������ʵ�α�ڵ�
		stackPA.lastElement().child[1] = null;
	}
	void process66()
	{
		push(NLexType.ActParamMore);
		push(NLexType.Exp);
	}
	void process67()
	{
		stackPA.pop();	//ʵ�α�Ϊ��ʱ������ʵ�α�ڵ�
	}
	void process68()
	{
		push(NLexType.ActParamList);
		push(LexType.COMMA);
		
		treeNode t = stackPA.pop();
		t.sibling = global.newRootNode();
		stackPA.push(t.sibling);	//�л�����һʵ�α�ڵ�
	}
	void process69()
	{
		push(NLexType.OtherRelE);
		push(NLexType.Exp);

		treeNode t = global.newRootNode();
		stackPA.push(t);	//ѹ������ʽ�ڵ�
	}
	void process70()
	{
		push(LexType.END_POP);	//�����ұ��ʽ�ڵ�
		push(NLexType.Exp);
		push(NLexType.CmpOp);

		treeNode l = stackPA.pop();	//����ʽ������ɣ������ڵ�
		treeNode t = stackPA.pop();	//�����ȽϷ��ڵ�
		t.nodeKind = NodeKind.ExpK;
		t.kind.exp = ExpKind.OpK;
		if(t.attr.expAttr.varKind == null)
			t.attr.expAttr.varKind = VarKind.IdV;
	    if(t.attr.expAttr.type == null)
	    	t.attr.expAttr.type = ExpType.Void;
	    t.lineno = Global.lineno;
		t.child[0] = l;
		t.child[1] = global.newRootNode();
		stackPA.push(t.child[1]);	//ѹ���ұ��ʽ�ڵ�
		stackPA.push(t);	//ѹ��ȽϷ��ڵ�
	}
	void process71()
	{
		push(NLexType.OtherTerm);
		push(NLexType.Term);
	}
	void process72()
	{}
	void process73()
	{
		push(LexType.END_POP);	//��������ڵ�
		push(NLexType.Exp);
		push(NLexType.AddOp);

		treeNode l = stackPA.pop();	//�������ɣ������ڵ�
		treeNode t = global.newRootNode();	//�Ӽ���������ڵ�
		t.nodeKind = NodeKind.ExpK;
		t.kind.exp = ExpKind.OpK;
		if(t.attr.expAttr.varKind == null)
			t.attr.expAttr.varKind = VarKind.IdV;
	    if(t.attr.expAttr.type == null)
	    	t.attr.expAttr.type = ExpType.Void;
	    t.lineno = Global.lineno;
	    treeNode a = stackPA.lastElement(); //�ı���ʽ��a��ָ��ֵ��ʹָ��������ڵ�
	    if(a.kind.stmt==StmtKind.CoutK||a.kind.stmt==StmtKind.ReturnK)
	    	a.child[0] = t;
	    else
	    	a.child[1] = t;
		t.child[0] = l;
		t.child[1] = global.newRootNode();
		stackPA.push(t.child[1]);	//ѹ������ڵ�
		stackPA.push(t);	//ѹ��Ӽ���������ڵ�
	}
	void process74()
	{
		push(NLexType.OtherFactor);
		push(NLexType.Factor);
	}
	void process75()
	{}
	void process76()
	{
		push(LexType.END_POP);	//���������ӽڵ�
		push(NLexType.Term);
		push(NLexType.MultOp);

		treeNode l = stackPA.pop();	//�����Ӵ�����ɣ������ڵ�
		treeNode t = global.newRootNode();	//�˳���������ڵ�
		t.nodeKind = NodeKind.ExpK;
		t.kind.exp = ExpKind.OpK;
		if(t.attr.expAttr.varKind == null)
			t.attr.expAttr.varKind = VarKind.IdV;
	    if(t.attr.expAttr.type == null)
	    	t.attr.expAttr.type = ExpType.Void;
	    t.lineno = Global.lineno;
	    treeNode a = stackPA.lastElement(); //�ı���ʽ��a��ָ��ֵ��ʹָ��������ڵ�
	    if(a.kind.stmt==StmtKind.CoutK||a.kind.stmt==StmtKind.ReturnK)
	    	a.child[0] = t;
	    else
	    	a.child[1] = t;
		t.child[0] = l;
		t.child[1] = global.newRootNode();
		stackPA.push(t.child[1]);	//ѹ�������ӽڵ�
		stackPA.push(t);	//ѹ��˳���������ڵ�
	}
	void process77()
	{
		push(LexType.RPAREN);
		push(NLexType.Exp);
		push(LexType.LPAREN);
	}
	void process78()
	{
		push(LexType.INTC);

		treeNode t = stackPA.lastElement();
		/* �����µ�ConstK���ʽ�����﷨���ڵ�,��ֵ��t */
		t.nodeKind = NodeKind.ExpK;
		t.kind.exp = ExpKind.ConstK;
	    /* ����ǰ������tokenStringת��Ϊ�����������﷨���ڵ�t����ֵ��Աattr.val	*/
		if(t.attr.expAttr.varKind == null)
			t.attr.expAttr.varKind = VarKind.IdV;
	    if(t.attr.expAttr.type == null)
	    	t.attr.expAttr.type = ExpType.Int;
		t.attr.expAttr.int_val = Integer.parseInt(token.Sem);
	    t.lineno = Global.lineno;
	}
	void process79()
	{
		push(NLexType.Variable);
	}
	void process80()
	{
		push(NLexType.VIdMore);
		push(LexType.ID);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.ExpK;
		t.kind.exp = ExpKind.VariK;
		if(t.attr.expAttr.varKind == null)
			t.attr.expAttr.varKind = VarKind.IdV;
	    if(t.attr.expAttr.type == null)
	    	t.attr.expAttr.type = ExpType.Void;
		t.name[0] = token.Sem;
        t.idnum ++;
		t.lineno = Global.lineno;
	}
	void process81()
	{
		push(NLexType.VariMore);
	}
	void process82()
	{
		push(NLexType.CallStmRest);
	}
	void process83()
	{}
	void process84()
	{
		push(LexType.RMIDPAREN);
		push(LexType.END_POP);	//���������Ա���ʽ�ڵ�
		push(NLexType.Exp);
		push(LexType.LMIDPAREN);

		treeNode t = stackPA.lastElement();
		t.attr.expAttr.varKind = VarKind.ArrayMembV;
		/*�˱��ʽΪ�����Ա��������*/
		t.child[0] = global.newRootNode();
		t.child[0].attr.expAttr.varKind = VarKind.IdV;
		/*�����Ժ��������ʽ��ֵ���������������±����*/
		stackPA.push(t.child[0]);	//ѹ�������Ա���ʽ�ڵ�
	}
	void process85()
	{
		push(LexType.END_POP);	//�����ṹ���Ա���ʽ�ڵ�
		push(NLexType.FieldVar);
		push(LexType.DOT);

		treeNode t = stackPA.lastElement();
		t.attr.expAttr.varKind = VarKind.FieldMembV;
		t.child[0] = global.newRootNode();
		t.child[0].attr.expAttr.varKind = VarKind.IdV;
		/*��һ������ָ�����Ա�������*/
		stackPA.push(t.child[0]);	//ѹ��ṹ���Ա���ʽ�ڵ�
	}
	void process86()
	{
		push(NLexType.FieldVarMore);
		push(LexType.ID);

		treeNode t = stackPA.lastElement();
		t.nodeKind = NodeKind.ExpK;
		t.kind.exp = ExpKind.VariK;
		if(t.attr.expAttr.varKind == null)
			t.attr.expAttr.varKind = VarKind.IdV;
	    if(t.attr.expAttr.type == null)
	    	t.attr.expAttr.type = ExpType.Void;
		t.name[0] = token.Sem;
	    t.idnum ++;
		t.lineno = Global.lineno;
	}
	void process87()
	{}
	void process88()
	{
		push(LexType.RMIDPAREN);
		push(LexType.END_POP);	//�����ṹ�������Ա���ʽ�ڵ�
		push(NLexType.Exp);
		push(LexType.LMIDPAREN);

		treeNode t = stackPA.lastElement();
		t.child[0] = global.newRootNode();
		t.child[0].attr.expAttr.varKind = VarKind.ArrayMembV;
		stackPA.push(t.child[0]);	//ѹ��ṹ�������Ա���ʽ�ڵ�
	}
	void process89()
	{
		push(LexType.LT);

		/* ������ȽϷ��󣬵����ȽϷ����ұ��ʽ�ڵ�˳�� */
		treeNode t = stackPA.pop();	//�����ȽϷ��ڵ�
		treeNode r = stackPA.pop();	//�����ұ��ʽ�ڵ�
		t.attr.expAttr.op = LexType.LT;
		stackPA.push(t);	//ѹ��ȽϷ��ڵ�
		stackPA.push(r);	//ѹ���ұ��ʽ�ڵ�
	}
	void process90()
	{
		push(LexType.GT);

		/* ������ȽϷ��󣬵����ȽϷ����ұ��ʽ�ڵ�˳�� */
		treeNode t = stackPA.pop();	//�����ȽϷ��ڵ�
		treeNode r = stackPA.pop();	//�����ұ��ʽ�ڵ�
		t.attr.expAttr.op = LexType.GT;
		stackPA.push(t);	//ѹ��ȽϷ��ڵ�
		stackPA.push(r);	//ѹ���ұ��ʽ�ڵ�
	}
	void process91()
	{
		push(LexType.LE);

		/* ������ȽϷ��󣬵����ȽϷ����ұ��ʽ�ڵ�˳�� */
		treeNode t = stackPA.pop();	//�����ȽϷ��ڵ�
		treeNode r = stackPA.pop();	//�����ұ��ʽ�ڵ�
		t.attr.expAttr.op = LexType.LE;
		stackPA.push(t);	//ѹ��ȽϷ��ڵ�
		stackPA.push(r);	//ѹ���ұ��ʽ�ڵ�
	}
	void process92()
	{
		push(LexType.GE);

		/* ������ȽϷ��󣬵����ȽϷ����ұ��ʽ�ڵ�˳�� */
		treeNode t = stackPA.pop();	//�����ȽϷ��ڵ�
		treeNode r = stackPA.pop();	//�����ұ��ʽ�ڵ�
		t.attr.expAttr.op = LexType.GE;
		stackPA.push(t);	//ѹ��ȽϷ��ڵ�
		stackPA.push(r);	//ѹ���ұ��ʽ�ڵ�
	}
	void process93()
	{
		push(LexType.EQ);

		/* ������ȽϷ��󣬵����ȽϷ����ұ��ʽ�ڵ�˳�� */
		treeNode t = stackPA.pop();	//�����ȽϷ��ڵ�
		treeNode r = stackPA.pop();	//�����ұ��ʽ�ڵ�
		t.attr.expAttr.op = LexType.EQ;
		stackPA.push(t);	//ѹ��ȽϷ��ڵ�
		stackPA.push(r);	//ѹ���ұ��ʽ�ڵ�
	}
	void process94()
	{
		push(LexType.NEQ);

		/* ������ȽϷ��󣬵����ȽϷ����ұ��ʽ�ڵ�˳�� */
		treeNode t = stackPA.pop();	//�����ȽϷ��ڵ�
		treeNode r = stackPA.pop();	//�����ұ��ʽ�ڵ�
		t.attr.expAttr.op = LexType.NEQ;
		stackPA.push(t);	//ѹ��ȽϷ��ڵ�
		stackPA.push(r);	//ѹ���ұ��ʽ�ڵ�
	}
	void process95()
	{
		push(LexType.PLUS);

		/* ������Ӽ���������󣬵����Ӽ��������������ڵ�˳�� */
		treeNode t = stackPA.pop();	//�����Ӽ���������ڵ�
		treeNode r = stackPA.pop();	//��������ڵ�
		t.attr.expAttr.op = LexType.PLUS;
		stackPA.push(t);	//ѹ��Ӽ���������ڵ�
		stackPA.push(r);	//ѹ������ڵ�
	}
	void process96()
	{
		push(LexType.MINUS);

		/* ������Ӽ���������󣬵����Ӽ��������������ڵ�˳�� */
		treeNode t = stackPA.pop();	//�����Ӽ���������ڵ�
		treeNode r = stackPA.pop();	//��������ڵ�
		t.attr.expAttr.op = LexType.MINUS;
		stackPA.push(t);	//ѹ��Ӽ���������ڵ�
		stackPA.push(r);	//ѹ������ڵ�
	}
	void process97()
	{
		push(LexType.TIMES);

		/* ������˳���������󣬵����˳���������������ӽڵ�˳�� */
		treeNode t = stackPA.pop();	//�����˳���������ڵ�
		treeNode r = stackPA.pop();	//���������ӽڵ�
		t.attr.expAttr.op = LexType.TIMES;
		stackPA.push(t);	//ѹ��˳���������ڵ�
		stackPA.push(r);	//ѹ�������ӽڵ�
	}
	void process98()
	{
		push(LexType.OVER);

		/* ������˳���������󣬵����˳���������������ӽڵ�˳�� */
		treeNode t = stackPA.pop();	//�����˳���������ڵ�
		treeNode r = stackPA.pop();	//���������ӽڵ�
		t.attr.expAttr.op = LexType.OVER;
		stackPA.push(t);	//ѹ��˳���������ڵ�
		stackPA.push(r);	//ѹ�������ӽڵ�
	}

	/********************************************************/
	/* 	����	predict											*/
	/* 	����	ѡ�����ʽ����							  			*/
	/********************************************************/
	public void predict(int num)
	{
		switch(num)
		{
	      case 0:     process0();	break;
	      case 1:     process1();	break;
	      case 2:     process2();	break;
	      case 3:     process3();	break;
	      case 4:     process4();   break;
		  case 5:	  process5();   break;
	      case 6:	  process6();	break;
	      case 7:	  process7();	break;
	      case 8:	  process8();	break;
		  case 9:	  process9();   break;
	      case 10:	  process10();	break;
	      case 11:	  process11();	break;
	      case 12:	  process12();	break;
	      case 13:	  process13();	break;
	      case 14:	  process14();	break;
	      case 15:	  process15();	break;
	      case 16:	  process16();	break;
	      case 17:	  process17();	break;
	      case 18:	  process18();	break;
	      case 19:	  process19();	break;
	      case 20:	  process20();	break;
	      case 21:	  process21();	break;
		  case 22:	  process22();  break;
	      case 23:	  process23();  break;
	      case 24:	  process24();	break;
		  case 25:	  process25();  break;
	      case 26:	  process26();  break;
	      case 27:	  process27();  break;
	      case 28:	  process28();  break;
	      case 29:	  process29();	break;
	      case 30:	  process30();	break;
		  case 31:	  process31();  break;
	      case 32:	  process32();  break;
	      case 33:	  process33();	break;
		  case 34:	  process34();  break;
	      case 35:	  process35();  break;
		  case 36:	  process36();  break;
	      case 37:	  process37();  break;
	      case 38:	  process38();	break;
	      case 39:	  process39();	break;
	      case 40:	  process40();  break;
	      case 41:	  process41();  break;
	      case 42:	  process42();	break;
		  case 43:	  process43();  break;
	      case 44:	  process44();  break;
	      case 45:	  process45();	break; 
		  case 46:    process46();  break;
	      case 47:	  process47();	break;
	      case 48:	  process48();  break;
	      case 49:	  process49();  break;
	      case 50:	  process50();	break;
		  case 51:	  process51();  break;
	      case 52:	  process52();	break;
	      case 53:	  process53();	break;
	      case 54:	  process54();  break;
	      case 55:	  process55();  break;
	      case 56:	  process56();	break;
		  case 57:	  process57();  break;
	      case 58:	  process58();	break;
	      case 59:	  process59();	break;
	      case 60:	  process60();	break;
	      case 61:	  process61();	break;
	      case 62:	  process62();	break;
	      case 63:	  process63();	break;
	      case 64:	  process64();	break;
	      case 65:	  process65();	break;
	      case 66:	  process66();	break;
	      case 67:	  process67();	break;
	      case 68:	  process68();	break;
		  case 69:    process69();  break;
		  case 70:    process70();  break;
	      case 71:	  process71();	break;
	      case 72:	  process72();	break;
	      case 73:	  process73();  break;
	      case 74:	  process74();  break;
	      case 75:	  process75();  break;
		  case 76:	  process76();  break;
	      case 77:	  process77();	break;
		  case 78:    process78();  break;
	      case 79:    process79();  break;
		  case 80:	  process80();  break;
	      case 81:	  process81();  break;
	      case 82:	  process82();	break;
	      case 83:	  process83();  break;
	      case 84:	  process84();  break;
	      case 85:	  process85();	break;
		  case 86:	  process86();  break;
	      case 87:	  process87();  break;
	      case 88:	  process88();	break;
		  case 89:	  process89();  break;
	      case 90:	  process90();	break;
	      case 91:	  process91();	break;
	      case 92:	  process92();	break;
	      case 93:	  process93();	break;
	      case 94:	  process94();	break;
	      case 95:	  process95();	break;
	      case 96:	  process96();	break;
	      case 97:    process97();  break;
		  case 98:    process98();  break;
	      case 99:    process99();  break;
		  case 100:   process100(); break;
		  case 101:   process101(); break;
		  case 102:   process102(); break;
		  case 103:   process103(); break;
		  case 104:   process104(); break;
		  default:    syntaxError("unexpected token");
	   }
	}   

	/********************************************************/
	/* 	����	run												*/
	/* 	����	LL1�﷨����������						    		*/
	/********************************************************/
	public treeNode run() {

		/* ���дʷ�������,��Դ�ļ���ȡ��token */
		myScanner.run();

		global.CreatLL1Table();
		
		/* ָ�������﷨�����ڵ��ָ�룬�����õ��﷨�� */
		treeNode root = global.newRootNode();
		root.child[0] = global.newFuncNode();

		/* �����￪ʼ�����﷨�������﷨�������� */
		push(NLexType.Program);
		stackPA.push(root.child[0]);

		/* ȡһ��token */
		getToken();

		while (!stack.isEmpty()) {
			/* ����ռ����Ƿ�ƥ�� */
			if (stack.lastElement().flag)/* ��ջ����־�������ռ������Ƿ��ռ��� */
			{
				LexType stacktopT = stack.lastElement().tLexVar;
				if (stacktopT == LexType.END_POP) {
					stackPA.pop();
					stack.pop();
				} else if (stacktopT == token.Lex) {
					getToken();/* ȡ��һ��token */
					stack.pop();
				} else
					syntaxError("unexpected token");
			} else {
				/* ���ݷ��ռ�����ջ�з��Ž���Ԥ�� */
				NLexType stacktopN = stack.lastElement().nLexVar;
				stack.pop();
				predict(Global.LL1Table[stacktopN.ordinal()][token.Lex.ordinal()]);
			}
		}

		if(token.Lex != LexType.ENDFILE)
			syntaxError("Code ends before file");

		/* ����﷨�� */
		global.pw.println();
		global.pw.println("----------------------- LL(1)�﷨�� -----------------------");
		global.pw.println();
		global.pw.flush();
		global.printTree(root);
		
		return root;
	}
}